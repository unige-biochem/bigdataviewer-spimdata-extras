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

package spimdata;

import mpicbg.spim.data.generic.AbstractSpimData;
import mpicbg.spim.data.generic.base.Entity;
import mpicbg.spim.data.generic.base.ViewSetupAttributes;
import mpicbg.spim.data.registration.ViewTransformAffine;
import net.imglib2.realtransform.AffineTransform3D;

/**
 * Various helper methods to manipulate spimdata objects
 */
public class SpimDataHelper {

	/**
	 * Removes the given attributes from all view setups of the dataset.
	 *
	 * @param asd the dataset to modify
	 * @param entityNames attribute names, as declared in the
	 *          {@code ViewSetupAttributeIo} annotation, for instance
	 *          {@code "displaysettings"}
	 */
	public static void removeEntities(AbstractSpimData<?> asd,
		String... entityNames)
	{
		asd.getSequenceDescription().getViewSetups().forEach((id, vs) -> {
			for (String entityName : entityNames) {
				vs.getAttributes().remove(entityName.trim());
			}
		});
	}

	/**
	 * Removes the given attributes from all view setups of the dataset.
	 *
	 * @param asd the dataset to modify
	 * @param entityClasses entity classes, for instance
	 *          {@code Displaysettings.class}
	 */
	@SafeVarargs
	public static void removeEntities(AbstractSpimData<?> asd,
		Class<? extends Entity>... entityClasses)
	{
		asd.getSequenceDescription().getViewSetups().forEach((id, vs) -> {
			for (Class<? extends Entity> entityClass : entityClasses) {
				vs.getAttributes().remove(ViewSetupAttributes.getNameForClass(
					entityClass));
			}
		});
	}

	/**
	 * Preconcatenates an affine transform to every view registration of the
	 * dataset, in other words moves the whole dataset in physical space.
	 *
	 * @param asd the dataset to modify
	 * @param name name given to the appended view transform
	 * @param at3d the transform to preconcatenate
	 */
	public static void pretransform(AbstractSpimData<?> asd, String name,
		AffineTransform3D at3d)
	{
		asd.getViewRegistrations().getViewRegistrations().values().forEach(
			viewRegistration -> viewRegistration.preconcatenateTransform(
				new ViewTransformAffine(name, at3d)));
	}

	/**
	 * Scales the whole dataset in physical space, see
	 * {@link #pretransform(AbstractSpimData, String, AffineTransform3D)}.
	 *
	 * @param asd the dataset to modify
	 * @param name name given to the appended view transform
	 * @param scaleX scaling factor along X
	 * @param scaleY scaling factor along Y
	 * @param scaleZ scaling factor along Z
	 */
	public static void scale(AbstractSpimData<?> asd, String name, double scaleX,
		double scaleY, double scaleZ)
	{
		AffineTransform3D at3d = new AffineTransform3D();
		at3d.scale(scaleX, scaleY, scaleZ);
		pretransform(asd, name, at3d);
	}

	/**
	 * Scales the whole dataset isotropically in physical space.
	 *
	 * @param asd the dataset to modify
	 * @param name name given to the appended view transform
	 * @param scale scaling factor applied along the three axes
	 */
	public static void scale(AbstractSpimData<?> asd, String name, double scale) {
		scale(asd, name, scale, scale, scale);
	}

	/**
	 * Translates the whole dataset in physical space, see
	 * {@link #pretransform(AbstractSpimData, String, AffineTransform3D)}.
	 *
	 * @param asd the dataset to modify
	 * @param name name given to the appended view transform
	 * @param translateX translation along X
	 * @param translateY translation along Y
	 * @param translateZ translation along Z
	 */
	public static void translate(AbstractSpimData<?> asd, String name,
		double translateX, double translateY, double translateZ)
	{
		AffineTransform3D at3d = new AffineTransform3D();
		at3d.translate(translateX, translateY, translateZ);
		pretransform(asd, name, at3d);
	}

}
