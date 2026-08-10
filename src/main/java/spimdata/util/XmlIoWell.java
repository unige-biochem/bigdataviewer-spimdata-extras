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
import mpicbg.spim.data.generic.base.XmlIoNamedEntity;
import org.jdom2.Element;

/**
 * For xml serialization and deserialization of {@link Well}. The row and column
 * indices are stored as extra {@code row} and {@code column} xml elements, in
 * addition to the id and name handled by the superclass.
 */
@ViewSetupAttributeIo(name = "well", type = Well.class)
public class XmlIoWell extends XmlIoNamedEntity<Well> {

    public XmlIoWell() {
        super("well", Well.class);
    }

    @Override
    public Element toXml(final Well w) {
        final Element elem = super.toXml(w);
        elem.addContent(XmlHelpers.intElement("row", w.row));
        elem.addContent(XmlHelpers.intElement("column", w.column));
        return elem;
    }

    @Override
    public Well fromXml(final Element elem) throws SpimDataException {
        final Well w = super.fromXml(elem);
        w.row = XmlHelpers.getInt(elem, "row");
        w.column = XmlHelpers.getInt(elem, "column");
        return w;
    }

}
