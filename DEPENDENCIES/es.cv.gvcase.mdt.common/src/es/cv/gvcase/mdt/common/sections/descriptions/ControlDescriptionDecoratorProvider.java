/*******************************************************************************
 * Copyright (c) 2010 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – Initial implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.sections.descriptions;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;

import es.cv.gvcase.mdt.common.Activator;

/**
 * Provides decorations with descriptions for the given control and feature.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public class ControlDescriptionDecoratorProvider {

	protected static Image DescriptionDecorationImage = null;

	protected final static String iconPath = "icons/info.png";

	public static Image getDescriptionDecorationImage() {
		if (DescriptionDecorationImage == null) {
			DescriptionDecorationImage = Activator.getDefault()
					.getBundledImage(iconPath);
		}
		return DescriptionDecorationImage;
	}

	public static ControlDecoration addDescriptionDecoration(Control control,
			String extendedFeatureID) {
		if (control == null || extendedFeatureID == null) {
			return null;
		}
		String description = SectionDescriptionRegistry.getInstance()
				.getDescriptionForExtendedFeature(extendedFeatureID);
		return addDescriptionControlDecoration(control, description, SWT.LEFT);
	}

	public static ControlDecoration addDescriptionDecoration(Control control,
			EStructuralFeature feature) {
		if (control == null || feature == null) {
			return null;
		}
		String description = SectionDescriptionRegistry.getInstance()
				.getDescriptionForEStructuralFeature(feature);
		return addDescriptionControlDecoration(control, description, SWT.LEFT);
	}

	public static ControlDecoration addDescriptionControlDecoration(
			Control control, String description, int position) {
		if (control == null || description == null) {
			return null;
		}
		ControlDecoration decoration = new ControlDecoration(control, position);
		decoration.setDescriptionText(description);
		decoration.setImage(getDescriptionDecorationImage());
		decoration.setShowHover(true);
		return decoration;
	}
}
