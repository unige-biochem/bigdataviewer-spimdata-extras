/*-
 * #%L
 * Repo containing extra settings that can be stored in spimdata file format
 * %%
 * Copyright (C) 2020 - 2026 EPFL
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
package spimdata.util;

import mpicbg.spim.data.SpimDataException;
import mpicbg.spim.data.XmlHelpers;
import org.jdom2.Element;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Guards the xml compatibility of the entities of this package: datasets
 * written by older versions must stay readable, tags written by newer versions
 * must survive a read / modify / write cycle, and that cycle must not duplicate
 * any tag.
 */
public class XmlIoCompatibilityTest {

	/** Exactly what versions up to 0.22.0 wrote, no more. */
	private static Element legacyDisplaysettings() {
		final Element elem = new Element(
			XmlIoDisplaysettings.DISPLAYSETTINGS_XML_TAG);
		elem.addContent(new Element("id").setText("3"));
		elem.addContent(new Element("isset").setText("true"));
		elem.addContent(new Element("color").setText("255 128 0 255"));
		elem.addContent(new Element("min").setText("10.0"));
		elem.addContent(new Element("max").setText("200.0"));
		elem.addContent(new Element("Projection_Mode").setText("Avg"));
		return elem;
	}

	@Test
	public void legacyDatasetIsStillReadable() throws SpimDataException {
		final Displaysettings ds = new XmlIoDisplaysettings().fromXml(
			legacyDisplaysettings());

		assertEquals(3, ds.getId());
		assertTrue(ds.isSet);
		assertArrayEquals(new int[] { 255, 128, 0, 255 }, ds.color);
		assertEquals(10.0, ds.min, 0.0);
		assertEquals(200.0, ds.max, 0.0);
		assertEquals("Avg", ds.projectionMode);

		// fields added in 0.23.0, absent from the xml
		assertFalse("a legacy dataset must not be reported as a label image",
			ds.isLabelImage);
		assertEquals("", ds.lutName);
	}

	@Test
	public void displaysettingsSurviveARoundTrip() throws SpimDataException {
		final Displaysettings ds = new Displaysettings(7, "vs:7");
		ds.isSet = true;
		ds.color = new int[] { 0, 255, 0, 255 };
		ds.min = 1;
		ds.max = 42;
		ds.projectionMode = "Sum";
		ds.isLabelImage = true;
		ds.lutName = "glasbey_on_dark";

		final Displaysettings read = new XmlIoDisplaysettings().fromXml(
			new XmlIoDisplaysettings().toXml(ds));

		assertTrue(read.isSet);
		assertArrayEquals(new int[] { 0, 255, 0, 255 }, read.color);
		assertEquals(1.0, read.min, 0.0);
		assertEquals(42.0, read.max, 0.0);
		assertEquals("Sum", read.projectionMode);
		assertTrue(read.isLabelImage);
		assertEquals("glasbey_on_dark", read.lutName);
	}

	/**
	 * Reading and writing through the same serializer instance, which is what
	 * happens when a dataset is opened, edited and saved again.
	 */
	@Test
	public void readModifyWriteDoesNotDuplicateTags() throws SpimDataException {
		final XmlIoDisplaysettings io = new XmlIoDisplaysettings();

		final Displaysettings ds = io.fromXml(legacyDisplaysettings());
		ds.lutName = "Fire";
		ds.max = 255;

		final Element written = io.toXml(ds);

		for (final String tag : new String[] { "id", "isset", "color", "min",
			"max", "Projection_Mode", "islabelimage", "lutname" })
		{
			assertEquals("<" + tag + "> should be written exactly once", 1, written
				.getChildren(tag).size());
		}
		// the edits, not the values that were read, must be what is written
		assertEquals("Fire", XmlHelpers.getText(written, "lutname"));
		assertEquals(255.0, XmlHelpers.getDouble(written, "max"), 0.0);
	}

	/**
	 * A tag this version knows nothing about, as a later version would write it,
	 * must be carried over untouched rather than dropped.
	 */
	@Test
	public void unknownTagsFromNewerVersionsArePreserved()
		throws SpimDataException
	{
		final Element elem = legacyDisplaysettings();
		elem.addContent(new Element("gamma").setText("0.5"));

		final XmlIoDisplaysettings io = new XmlIoDisplaysettings();
		final Element written = io.toXml(io.fromXml(elem));

		assertNotNull("an unknown tag must not be dropped on save", written
			.getChild("gamma"));
		assertEquals("0.5", written.getChildText("gamma"));
	}

	@Test
	public void wellSurvivesAReadModifyWriteCycle() throws SpimDataException {
		final XmlIoWell io = new XmlIoWell();

		final Element written = io.toXml(io.fromXml(io.toXml(new Well(4, "B4", 1,
			3))));

		assertEquals(1, written.getChildren("row").size());
		assertEquals(1, written.getChildren("column").size());

		final Well read = io.fromXml(written);
		assertEquals(4, read.getId());
		assertEquals("B4", read.getName());
		assertEquals(1, read.getRow());
		assertEquals(3, read.getColumn());
	}
}
