/*******************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – Initial implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.gmf.runtime.emf.core.util.EObjectAdapter;

import es.cv.gvcase.mdt.common.util.ExtensionPointParser;

/**
 * A registry for feature extensions to <EModelElement>s. An adapter from
 * <EModelElement> to <EModelElementAdapterFeatureExtender>.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public class ExtendedFeatureElementFactory implements IAdapterFactory {

	// Singleton part

	/**
	 * Singleton instance.
	 */
	public static final ExtendedFeatureElementFactory INSTANCE = new ExtendedFeatureElementFactory();

	/**
	 * Empty constructor
	 */
	private ExtendedFeatureElementFactory() {
	}

	/**
	 * Singleton instance fetcher.
	 * 
	 * @return
	 */
	public static ExtendedFeatureElementFactory getInstance() {
		return INSTANCE;
	}

	// Registry part

	/**
	 * Feature extension <IExtensionPoint> identifier.
	 */
	private static final String extensionPointID = "es.cv.gvcase.mdt.common.featureExtender";

	/**
	 * Caching map.
	 */
	private static Map<String, Feature> mapFeatureIDToFeature = null;

	/**
	 * Getter for <String> identifier to <Feature> <Map>.
	 * 
	 * @return
	 */
	public Map<String, Feature> getMapFeatureIDToFeature() {
		// caching!
		if (mapFeatureIDToFeature == null) {
			parseFeaturesFromExtensionPoint();
		}
		return mapFeatureIDToFeature;
	}

	/**
	 * Providers <Feature>s defined via <IextensionPoint>s.
	 * 
	 * @return
	 */
	public Collection<Feature> getFeaturesFromExtensions() {
		return getMapFeatureIDToFeature().values();
	}

	/**
	 * Reads <Feature>s defined in <IExtensionPoint>s and stores them in a
	 * caching <Map>.
	 */
	protected void parseFeaturesFromExtensionPoint() {
		// fjcano :: we use our own homebrewed extension point parser :)
		mapFeatureIDToFeature = new HashMap<String, Feature>();
		ExtensionPointParser extensionPointParser = new ExtensionPointParser(
				extensionPointID, new Class[] { Feature.class, AdaptTo.class,
						AvailableValue.class });
		for (Object object : extensionPointParser.parseExtensionPoint()) {
			Feature feature = (Feature) Platform.getAdapterManager()
					.getAdapter(object, Feature.class);
			if (feature != null) {
				// store each Feature in the Map by featureID.
				mapFeatureIDToFeature.put(feature.featureID, feature);
			}
		}
	}

	/**
	 * Checks if a <Feature> can be applied to the given <EModelElement>.
	 * 
	 * @param element
	 * @param featureID
	 * @return
	 */
	public boolean isForType(EModelElement element, String featureID) {
		Feature feature = getMapFeatureIDToFeature().get(featureID);
		if (feature != null) {
			return feature.isForType(element);
		}
		return false;
	}

	// AdapterFactory part

	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adaptableObject == null) {
			return null;
		}
		EModelElement element = (EModelElement) Platform.getAdapterManager()
				.getAdapter(adaptableObject, EModelElement.class);
		if (element != null) {
			if (ExtendedFeatureElement.class.equals(adapterType)
					|| EObjectAdapter.class.equals(adapterType)) {
				// Both EModelElementAdapterFeature and EObjectAdapter are given
				// an EModelElementAdapterFEatureExtender as result.
				return new ExtendedFeatureElementImpl(element);
			}
		}
		return null;
	}

	/**
	 * Provides an EModelElement as an ExtendedFeatureElement.
	 * 
	 * @param element
	 * @return
	 */
	public ExtendedFeatureElement asExtendedFeatureElement(Object element) {
		return (ExtendedFeatureElement) ExtendedFeatureElementFactory
				.getInstance()
				.getAdapter(element, ExtendedFeatureElement.class);
	}

	public Object getObjectFeatureValue(Object element, String feature) {
		ExtendedFeatureElement efElement = asExtendedFeatureElement(element);
		if (efElement == null) {
			return null;
		}

		Object o = efElement.getValue(feature);

		return o;
	}

	/**
	 * <List> of allowed <Class> adaptations.
	 */
	private static final Class[] allowedAdapterTypes = new Class[] {
			ExtendedFeatureElement.class, EObjectAdapter.class };

	public Class[] getAdapterList() {
		return allowedAdapterTypes;
	}

}
