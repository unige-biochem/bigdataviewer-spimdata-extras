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

import mpicbg.spim.data.generic.base.NamedEntity;

/**
 * Entity which identifies the well, within a {@link Plate}, a view setup was
 * acquired in. On top of the id and name inherited from {@link NamedEntity}, a
 * well knows its position in the plate as a (row, column) pair. Serialized
 * under the {@code well} attribute name, see {@link XmlIoWell}.
 */
public class Well extends NamedEntity implements
        Comparable<Well>
{

    int row, column;

    public Well(final int id, final String name, int row, int column) {
        super(id, name);
        this.row = row;
        this.column = column;
    }

    /**
     * @return the row index of this well in its plate, zero based when the
     *         well was created from OME metadata
     */
    public int getRow() {
        return row;
    }

    /**
     * @return the column index of this well in its plate, zero based when the
     *         well was created from OME metadata
     */
    public int getColumn() {
        return column;
    }

    /**
     * Compares the {@link #getId() ids}.
     */
    @Override
    public int compareTo(final Well o) {
        return getId() - o.getId();
    }

    /**
     * Empty constructor strictly necessary for dataset deserialization
     */
    protected Well() {}

}
