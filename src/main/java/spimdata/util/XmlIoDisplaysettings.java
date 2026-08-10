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
	public static final String PROJECTION_MODE_XML_TAG = "Projection_Mode";// underscore
																																					// necessary
																																					// for
																																					// valid
																																					// xml
																																					// element
																																					// to
																																					// store
																																					// in
																																					// @see
																																					// DisplaySettings

	public XmlIoDisplaysettings() {
		super(DISPLAYSETTINGS_XML_TAG, Displaysettings.class);
	}

	@Override
	public Element toXml(final Displaysettings ds) {
		final Element elem = super.toXml(ds);
		elem.addContent(XmlHelpers.booleanElement("isset", ds.isSet));
		elem.addContent(XmlHelpers.intArrayElement("color", ds.color));
		elem.addContent(XmlHelpers.doubleElement("min", ds.min));
		elem.addContent(XmlHelpers.doubleElement("max", ds.max));
		elem.addContent(XmlHelpers.textElement(PROJECTION_MODE_XML_TAG,
			ds.projectionMode));
		return elem;
	}

	@Override
	public Displaysettings fromXml(final Element elem) throws SpimDataException {
		final Displaysettings ds = super.fromXml(elem);
		ds.isSet = XmlHelpers.getBoolean(elem, "isset");
		ds.color = XmlHelpers.getIntArray(elem, "color");
		ds.min = XmlHelpers.getDouble(elem, "min");
		ds.max = XmlHelpers.getDouble(elem, "max");
		ds.projectionMode = XmlHelpers.getText(elem, PROJECTION_MODE_XML_TAG);
		return ds;
	}
}
