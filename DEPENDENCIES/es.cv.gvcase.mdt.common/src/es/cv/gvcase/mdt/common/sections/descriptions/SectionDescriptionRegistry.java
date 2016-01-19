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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.cv.gvcase.emf.common.part.ExtensionPointParser;
import es.cv.gvcase.mdt.common.model.Feature;

/**
 * Registry to store all the descriptions for different
 * {@link EStructuralFeature} or extended {@link Feature}. <br>
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public class SectionDescriptionRegistry {

	// //
	// descriptive classes
	// //

	public class FeatureSectionDescription {
		public String id;
		public String description;
		public List<EStructuralFeature> EStructuralFeature;
		public List<ExtendedFeature> ExtendedFeature;
	}

	public class EStructuralFeature {
		public String id;
	}

	public class ExtendedFeature {
		public String id;
	}

	// //
	// Singleton
	// //

	protected static SectionDescriptionRegistry INSTANCE = null;

	public static SectionDescriptionRegistry getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new SectionDescriptionRegistry();
		}
		return INSTANCE;
	}

	private SectionDescriptionRegistry() {

	}

	// //
	// extension point reader
	// //

	protected static final String extensionPointID = "es.cv.gvcase.mdt.common.propertySectionFeatureDescription";

	protected void readExtensionPoint() {
		if (mapFeature2Description == null) {
			mapFeature2Description = new HashMap<String, String>();
		}
		mapFeature2Description.clear();
		// read Extension Point
		ExtensionPointParser parser = new ExtensionPointParser(
				extensionPointID, new Class[] {
						FeatureSectionDescription.class,
						EStructuralFeature.class, ExtendedFeature.class }, this);
		for (Object o : parser.parseExtensionPoint()) {
			if (o instanceof FeatureSectionDescription) {
				FeatureSectionDescription description = (FeatureSectionDescription) o;
				if (description.description != null) {
					if (description.EStructuralFeature != null) {
						for (EStructuralFeature feature : description.EStructuralFeature) {
							mapFeature2Description.put(feature.id,
									description.description);
						}
					}
					if (description.ExtendedFeature != null) {
						for (ExtendedFeature feature : description.ExtendedFeature) {
							mapFeature2Description.put(feature.id,
									description.description);
						}
					}
				}
			}
		}
	}

	// //
	// map from identifier to description
	// //

	protected Map<String, String> mapFeature2Description = null;

	// //
	// utility methods
	// //

	public String getDescriptionForEStructuralFeature(
			org.eclipse.emf.ecore.EStructuralFeature feature) {
		if (feature == null) {
			return null;
		}
		if (feature.getName() == null || feature.getEContainingClass() == null
				|| feature.getEContainingClass().getName() == null) {
			return null;
		}
		String key = calculateKey(feature);
		readExtensionPoint();
		return mapFeature2Description.get(key);
	}

	protected String calculateKey(
			org.eclipse.emf.ecore.EStructuralFeature feature) {
		if (feature == null) {
			return null;
		}
		String key = feature.getEContainingClass().getName();
		key += ".";
		key += feature.getName();
		return key;
	}

	public String getDescriptionForExtendedFeature(ExtendedFeature feature) {
		if (feature == null) {
			return null;
		}
		String key = calculateKey(feature);
		readExtensionPoint();
		return mapFeature2Description.get(key);
	}

	public String getDescriptionForExtendedFeature(String featureID) {
		if (featureID == null) {
			return null;
		}
		readExtensionPoint();
		return mapFeature2Description.get(featureID);
	}

	protected String calculateKey(ExtendedFeature feature) {
		if (feature == null) {
			return null;
		}
		String key = feature.id;
		return key;
	}

	public Map<String, String> getAllDescriptions() {
		readExtensionPoint();
		return mapFeature2Description;
	}

}
