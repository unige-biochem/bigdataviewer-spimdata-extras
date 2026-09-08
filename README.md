[![](https://github.com/unige-biochem/bigdataviewer-spimdata-extras/actions/workflows/build-main.yml/badge.svg)](https://github.com/unige-biochem/bigdataviewer-spimdata-extras/actions/workflows/build-main.yml)
[![Maven Scijava Version](https://img.shields.io/github/v/tag/BIOP/bigdataviewer-spimdata-extras?label=Version-[Maven%20Scijava])](https://maven.scijava.org/#browse/browse:releases:ch%2Fepfl%2Fbiop%2Fbigdataviewer-spimdata-extras)

# bigdataviewer-spimdata-extras

Extra [entities](https://github.com/bigdataviewer/spimdata) that can be attached to the view setups of a
SpimData dataset, and serialized to / deserialized from its xml file.

SpimData describes each view setup with a set of *attributes* (channel, tile, angle, illumination). This
library adds a few more, so that information which is not part of the core SpimData model — how a source
should be displayed, where it comes from in a multi-well plate — survives a save / reload cycle of the
dataset.

Each entity comes with an xml serializer annotated with `@ViewSetupAttributeIo`. SpimData discovers those
serializers on the classpath through the SciJava annotation index, so simply having this artifact as a
dependency is enough: no registration code is needed.

## Entities

| Class | Extends | XML attribute | Stores |
|---|---|---|---|
| `Displaysettings` | `NamedEntity` | `displaysettings` | color (RGBA), min / max display range, projection mode, LUT name, label image flag |
| `ImageName` | `NamedEntity` | `imagename` | the name of the image the view setup originates from |
| `Plate` | `NamedEntity` | `plate` | the multi-well plate the view setup belongs to |
| `Well` | `NamedEntity` | `well` | the well, plus its (row, column) position in the plate |
| `Field` | `Entity` | `field` | the field of view within a well (id only, no name) |

`spimdata.SpimDataHelper` additionally provides a few static helpers to manipulate a whole dataset:
`removeEntities`, `pretransform`, `scale` and `translate`.

## Installation

The artifact is deployed on the [SciJava Maven repository](https://maven.scijava.org/):

```xml
<repositories>
    <repository>
        <id>scijava.public</id>
        <url>https://maven.scijava.org/content/groups/public</url>
    </repository>
</repositories>

<dependency>
    <groupId>ch.epfl.biop</groupId>
    <artifactId>bigdataviewer-spimdata-extras</artifactId>
    <version>0.23.0</version>
</dependency>
```

## Usage

Attaching entities to a view setup, when building a dataset:

```java
BasicViewSetup viewSetup = new BasicViewSetup(setupId, name, size, voxelSize);
viewSetup.setAttribute(new ImageName(0, "my_image.tif"));
viewSetup.setAttribute(new Plate(0, "Plate_001"));
viewSetup.setAttribute(new Well(3, "B4", 1, 3)); // row 1, column 3
viewSetup.setAttribute(new Field(0));
```

Reading them back from a loaded dataset:

```java
Well well = viewSetup.getAttribute(Well.class);
if (well != null) {
    System.out.println(well.getName() + " at row " + well.getRow() +
        ", column " + well.getColumn());
}
```

`Displaysettings` has a dedicated API to move settings between a `SourceAndConverter` and the dataset:

```java
// save what the viewer currently displays into the entity
Displaysettings ds = new Displaysettings(setupId);
Displaysettings.getDisplaySettingsFromCurrentConverter(sac, ds);
viewSetup.setAttribute(ds);

// and apply stored settings back onto a source
Displaysettings.applyDisplaysettings(sac, viewSetup.getAttribute(Displaysettings.class));
```

Two fields of `Displaysettings` are pure metadata, which this library stores and returns but never
acts on:

```java
ds.isLabelImage = true;         // holds object indices, not intensities
ds.lutName = "glasbey_on_dark"; // LUT to display it with
```

`isLabelImage` lets a renderer switch to a categorical LUT and turn interpolation off.
`lutName` names a lookup table rather than embedding it: resolving that name to actual colors needs
a LUT provider (`net.imagej.display.ColorTables`, Fiji's `LutLoader`, …), and this artifact
deliberately depends on neither, so the resolution is left to the caller. An empty `lutName` means
no LUT was specified and the `color` field should be used instead.

## Compatibility

The xml format only ever grows: new versions add tags, they do not rename or repurpose existing
ones. Concretely,

- **datasets written by older versions stay readable** — tags added later are read through the
  defaulting overloads of `XmlHelpers`, so a dataset without `islabelimage` yields `false` and one
  without `lutname` yields `""`;
- **tags written by newer versions are preserved** — `XmlIoEntity` stashes children it does not
  recognise and writes them back out, so opening a dataset from a later version and saving it does
  not silently drop its extra fields.

`src/test/java/spimdata/util/XmlIoCompatibilityTest.java` pins both properties down.

Dropping entities from a dataset before saving it:

```java
SpimDataHelper.removeEntities(spimData, Displaysettings.class, ImageName.class);
```

## Serialized form

Entities are written in the `SequenceDescription` of the dataset xml, grouped by attribute name, and
referenced by id from each view setup:

```xml
<ViewSetup>
  <id>0</id>
  <name>TileScan-Channel 1</name>
  <attributes>
    <channel>0</channel>
    <imagename>0</imagename>
    <displaysettings>0</displaysettings>
  </attributes>
</ViewSetup>
...
<Attributes name="imagename">
  <imagename>
    <id>0</id>
    <name>TileScan</name>
  </imagename>
</Attributes>
<Attributes name="displaysettings">
  <Displaysettings>
    <id>0</id>
    <isset>true</isset>
    <color>255 255 255 255</color>
    <min>0.0</min>
    <max>255.0</max>
    <Projection_Mode>Sum</Projection_Mode>
    <islabelimage>false</islabelimage>
    <lutname></lutname>
  </Displaysettings>
</Attributes>
```

`islabelimage` and `lutname` were added in 0.23.0; earlier datasets simply do not carry them.

## Building

```
mvn clean install
```

Java 11 and Maven are required. The project inherits from
[pom-scijava](https://github.com/scijava/pom-scijava).

## License

MIT, see [LICENSE.txt](LICENSE.txt).