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
import mpicbg.spim.data.generic.base.ViewSetupAttributeIo;
import mpicbg.spim.data.generic.base.XmlIoEntity;
import org.jdom2.Element;

@ViewSetupAttributeIo(name = "displaysettings", type = Displaysettings.class)
public class XmlIoDisplaysettings extends XmlIoEntity<Displaysettings> {

	public static final String DISPLAYSETTINGS_XML_TAG = "Displaysettings";

	public static final String IS_SET_XML_TAG = "isset";

	public static final String COLOR_XML_TAG = "color";

	public static final String MIN_XML_TAG = "min";

	public static final String MAX_XML_TAG = "max";

	// the underscore keeps this a valid xml element name
	public static final String PROJECTION_MODE_XML_TAG = "Projection_Mode";

	/**
	 * Tags added in 0.23.0. They are absent from datasets written by earlier
	 * versions, so {@link #fromXml(Element)} reads them through the defaulting
	 * overloads of {@link XmlHelpers}.
	 */
	public static final String IS_LABEL_IMAGE_XML_TAG = "islabelimage";

	public static final String LUT_NAME_XML_TAG = "lutname";

	public XmlIoDisplaysettings() {
		super(DISPLAYSETTINGS_XML_TAG, Displaysettings.class);
		// Without this, XmlIoEntity treats the tags below as foreign content: it
		// stashes them on read and prepends them again on write, so a read /
		// modify / write cycle emits every tag twice, the stale value first.
		handledTags.add(IS_SET_XML_TAG);
		handledTags.add(COLOR_XML_TAG);
		handledTags.add(MIN_XML_TAG);
		handledTags.add(MAX_XML_TAG);
		handledTags.add(PROJECTION_MODE_XML_TAG);
		handledTags.add(IS_LABEL_IMAGE_XML_TAG);
		handledTags.add(LUT_NAME_XML_TAG);
	}

	@Override
	public Element toXml(final Displaysettings ds) {
		final Element elem = super.toXml(ds);
		elem.addContent(XmlHelpers.booleanElement(IS_SET_XML_TAG, ds.isSet));
		elem.addContent(XmlHelpers.intArrayElement(COLOR_XML_TAG, ds.color));
		elem.addContent(XmlHelpers.doubleElement(MIN_XML_TAG, ds.min));
		elem.addContent(XmlHelpers.doubleElement(MAX_XML_TAG, ds.max));
		elem.addContent(XmlHelpers.textElement(PROJECTION_MODE_XML_TAG,
			ds.projectionMode));
		elem.addContent(XmlHelpers.booleanElement(IS_LABEL_IMAGE_XML_TAG,
			ds.isLabelImage));
		elem.addContent(XmlHelpers.textElement(LUT_NAME_XML_TAG, ds.lutName == null
			? "" : ds.lutName));
		return elem;
	}

	@Override
	public Displaysettings fromXml(final Element elem) throws SpimDataException {
		final Displaysettings ds = super.fromXml(elem);
		ds.isSet = XmlHelpers.getBoolean(elem, IS_SET_XML_TAG);
		ds.color = XmlHelpers.getIntArray(elem, COLOR_XML_TAG);
		ds.min = XmlHelpers.getDouble(elem, MIN_XML_TAG);
		ds.max = XmlHelpers.getDouble(elem, MAX_XML_TAG);
		ds.projectionMode = XmlHelpers.getText(elem, PROJECTION_MODE_XML_TAG);
		// tags below may be missing from datasets written before they existed
		ds.isLabelImage = XmlHelpers.getBoolean(elem, IS_LABEL_IMAGE_XML_TAG, false);
		ds.lutName = XmlHelpers.getText(elem, LUT_NAME_XML_TAG, "");
		return ds;
	}
}
