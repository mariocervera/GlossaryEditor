/*******************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 * Francisco Javier Cano Muñoz (Prodevelop) – Initial implementation.
 * Mario Cervera Ubeda (Prodevelop) - Added method getFeatureAsString
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.sections.extended;

import org.eclipse.emf.edit.domain.EditingDomain;

import es.cv.gvcase.mdt.common.model.ExtendedFeatureElement;
import es.cv.gvcase.mdt.common.model.ExtendedFeatureElementFactory;
import es.cv.gvcase.mdt.common.model.Feature;

/**
 * An abstract property section that can handle Integer extended features via
 * EAnnotations.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public abstract class AbstractExtendedIntegerPropertySection extends
		AbstractExtendedTextPropertySection {

	public AbstractExtendedIntegerPropertySection(String featureID) {
		super(featureID);
	}

	@Override
	protected EditingDomain getEditingDomain() {
		try {
			return super.getEditingDomain();
		} catch (IllegalArgumentException ex) {
			return null;
		}
	}
	
	@Override
	protected Object getNewFeatureValue(String newText) {
		try {
			Integer integer = Integer.parseInt(newText);
			return integer;
		} catch (NumberFormatException ex) {
			return null;
		}
	}

	@Override
	protected boolean verifyFeature() {
		Feature feature = ExtendedFeatureElementFactory.getInstance()
				.getMapFeatureIDToFeature().get(getFeatureID());
		if (feature == null) {
			return false;
		}
		if (feature.isValued() && feature.isMonoValued()
				&& feature.getType().equals(Feature.IntegerType)) {
			return true;
		}
		return false;
	}
	
	@Override
	protected String getFeatureAsString() {
		ExtendedFeatureElement element = getExtendedFeatureElement();
		if (element == null) {
			return "";
		}
		Integer integer = element.getInteger(getFeatureID());
		return integer != null ? integer.toString() : "";
	}

}
