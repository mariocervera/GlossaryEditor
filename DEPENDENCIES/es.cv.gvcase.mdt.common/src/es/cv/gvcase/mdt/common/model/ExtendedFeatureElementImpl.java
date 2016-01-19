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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * A proxy for an <EModelElement> that provides extended features capabilities.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public class ExtendedFeatureElementImpl implements ExtendedFeatureElement {

	private EModelElement element = null;

	/**
	 * Builds this proxy on top of provided <EModelElement>.
	 * 
	 * @param element
	 */
	public ExtendedFeatureElementImpl(EModelElement element) {
		this.element = element;
	}

	/**
	 * Adapts to an <EModelElement> or an <EObject>.
	 */
	public Object getAdapter(Class adapter) {
		if (EObject.class.equals(adapter)
				|| EModelElement.class.equals(adapter)) {
			return getElement();
		}
		return null;
	}

	/**
	 * The proxied <EModelElement>.
	 * 
	 * @return
	 */
	public EModelElement getElement() {
		return element;
	}

	/**
	 * Fetches a <Feature> from the <EModelElementAdapterFEatureFactory>.
	 * 
	 * @param id
	 * @return
	 */
	protected Feature findFeature(String id) {
		return ExtendedFeatureElementFactory.getInstance()
				.getMapFeatureIDToFeature().get(id);
	}

	public boolean addValue(String featureID, Object value) {
		Feature feature = findFeature(featureID);
		if (feature != null) {
			cleanCachedValueForFeature(featureID);
			return feature.addValue(getElement(), value);
		}
		return false;
	}

	public Boolean getBoolean(String featureID) {
		Boolean cachedBoolean = getCachedBooleanForFeature(featureID);
		if (cachedBoolean != null) {
			return cachedBoolean;
		}
		Feature feature = findFeature(featureID);
		if (feature != null) {
			Boolean value = feature.getBoolean(getElement());
			getMapFeatureID2CachedValue().put(featureID, value);
			return value;
		}
		return null;
	}

	public List<Boolean> getBooleanList(String featureID) {
		List<Boolean> cachedList = getCachedBooleanListForFeature(featureID);
		if (cachedList != null) {
			return cachedList;
		}
		Feature feature = findFeature(featureID);
		if (feature != null) {
			List<Boolean> value = feature.getBooleanList(getElement());
			getMapFeatureID2CachedValue().put(featureID, value);
			return value;
		}
		return Collections.emptyList();
	}

	public Double getDouble(String featureID) {
		Double cachedDouble = getCachedDoubleForFeature(featureID);
		if (cachedDouble != null) {
			return cachedDouble;
		}
		Feature feature = findFeature(featureID);
		if (feature != null) {
			Double value = feature.getDouble(getElement());
			getMapFeatureID2CachedValue().put(featureID, value);
			return value;
		}
		return null;
	}

	public List<Double> getDoubleList(String featureID) {
		List<Double> cachedList = getCachedDoubleListForFeature(featureID);
		if (cachedList != null) {
			return cachedList;
		}
		Feature feature = findFeature(featureID);
		if (feature != null) {
			List<Double> value = feature.getDoubleList(getElement());
			getMapFeatureID2CachedValue().put(featureID, value);
			return value;
		}
		return Collections.emptyList();
	}

	public Integer getInteger(String featureID) {
		Integer integer = getCachedIntegerForFeature(featureID);
		if (integer != null) {
			return integer;
		}
		Feature feature = findFeature(featureID);
		if (feature != null) {
			Integer value = feature.getInteger(getElement());
			getMapFeatureID2CachedValue().put(featureID, value);
			return value;
		}
		return null;
	}

	public List<Integer> getIntegerList(String featureID) {
		List<Integer> cachedList = getCachedIntegerListForFeature(featureID);
		if (cachedList != null) {
			return cachedList;
		}
		Feature feature = findFeature(featureID);
		if (feature != null) {
			List<Integer> value = feature.getIntegerList(getElement());
			getMapFeatureID2CachedValue().put(featureID, value);
			return value;
		}
		return Collections.emptyList();
	}

	public EObject getReference(String featureID) {
		EObject reference = getCachedReferenceForFeature(featureID);
		if (reference != null) {
			return reference;
		}
		Feature feature = findFeature(featureID);
		if (feature != null) {
			EObject value = feature.getReference(getElement());
			getMapFeatureID2CachedValue().put(featureID, value);
			return value;
		}
		return null;
	}

	public List<EObject> getReferenceList(String featureID) {
		List<EObject> cachedList = getCachedReferenceListForFeature(featureID);
		if (cachedList != null) {
			return cachedList;
		}
		Feature feature = findFeature(featureID);
		if (feature != null) {
			List<EObject> value = feature.getReferenceList(getElement());
			getMapFeatureID2CachedValue().put(featureID, value);
			return value;
		}
		return Collections.emptyList();
	}

	public String getString(String featureID) {
		String string = getCachedStringForFeature(featureID);
		if (string != null) {
			return string;
		}
		Feature feature = findFeature(featureID);
		if (feature != null) {
			String value = feature.getString(getElement());
			getMapFeatureID2CachedValue().put(featureID, value);
			return value;
		}
		return null;
	}

	public List<String> getStringList(String featureID) {
		List<String> cachedList = getCachedStringListForFeature(featureID);
		if (cachedList != null) {
			return cachedList;
		}
		Feature feature = findFeature(featureID);
		if (feature != null) {
			List<String> value = feature.getStringList(getElement());
			getMapFeatureID2CachedValue().put(featureID, value);
			return value;
		}
		return Collections.emptyList();
	}

	public Object getValue(String featureID) {
		Feature feature = findFeature(featureID);
		if (feature != null) {
			return feature.getValue(getElement());
		}
		return null;
	}

	public boolean removeValue(String featureID, Object value) {
		Feature feature = findFeature(featureID);
		if (feature != null) {
			cleanCachedValueForFeature(featureID);
			return feature.removeValue(getElement(), value);
		}
		return false;
	}

	public boolean setValue(String featureID, Object value) {
		Feature feature = findFeature(featureID);
		if (feature != null) {
			cleanCachedValueForFeature(featureID);
			return feature.setValue(getElement(), value);
		}
		return false;
	}

	public boolean unsetValue(String featureID) {
		Feature feature = findFeature(featureID);
		if (feature != null) {
			return removeFeatureEAnnotation(featureID);
		}
		return false;
	}

	protected boolean removeFeatureEAnnotation(String featureID) {
		EModelElement element = getElement();
		EAnnotation eAnnotation = element.getEAnnotation(featureID);
		if (eAnnotation != null) {
			EcoreUtil.delete(eAnnotation);
			cleanCachedValueForFeature(featureID);
			return true;
		}
		return false;
	}

	// //
	// Cached values
	// //

	protected Map<String, Object> mapFeatureID2CachedValue = null;

	protected Map<String, Object> getMapFeatureID2CachedValue() {
		if (mapFeatureID2CachedValue == null) {
			mapFeatureID2CachedValue = new HashMap<String, Object>();
		}
		return mapFeatureID2CachedValue;
	}

	protected Object getCachedValueForFeature(String featureID) {
		if (featureID == null) {
			return null;
		}
		if (getMapFeatureID2CachedValue().containsKey(featureID) == false) {
			return null;
		}
		return getMapFeatureID2CachedValue().get(featureID);
	}

	protected Boolean getCachedBooleanForFeature(String featureID) {
		Object value = getCachedValueForFeature(featureID);
		if (value instanceof Boolean) {
			return (Boolean) value;
		}
		return null;
	}

	protected List<Boolean> getCachedBooleanListForFeature(String featureID) {
		Object value = getCachedValueForFeature(featureID);
		if (value instanceof List<?>) {
			return (List<Boolean>) value;
		}
		return null;
	}

	protected Double getCachedDoubleForFeature(String featureID) {
		Object value = getCachedValueForFeature(featureID);
		if (value instanceof Double) {
			return (Double) value;
		}
		return null;
	}

	protected List<Double> getCachedDoubleListForFeature(String featureID) {
		Object value = getCachedValueForFeature(featureID);
		if (value instanceof List<?>) {
			return (List<Double>) value;
		}
		return null;
	}

	protected Integer getCachedIntegerForFeature(String featureID) {
		Object value = getCachedValueForFeature(featureID);
		if (value instanceof Integer) {
			return (Integer) value;
		}
		return null;
	}

	protected List<Integer> getCachedIntegerListForFeature(String featureID) {
		Object value = getCachedValueForFeature(featureID);
		if (value instanceof List<?>) {
			return (List<Integer>) value;
		}
		return null;
	}

	protected EObject getCachedReferenceForFeature(String featureID) {
		Object value = getCachedValueForFeature(featureID);
		if (value instanceof EObject) {
			return (EObject) value;
		}
		return null;
	}

	protected List<EObject> getCachedReferenceListForFeature(String featureID) {
		Object value = getCachedValueForFeature(featureID);
		if (value instanceof List<?>) {
			return (List<EObject>) value;
		}
		return null;
	}

	protected String getCachedStringForFeature(String featureID) {
		Object value = getCachedValueForFeature(featureID);
		if (value instanceof String) {
			return (String) value;
		}
		return null;
	}

	protected List<String> getCachedStringListForFeature(String featureID) {
		Object value = getCachedValueForFeature(featureID);
		if (value instanceof List<?>) {
			return (List<String>) value;
		}
		return null;
	}

	protected void cleanCachedValueForFeature(String featureID) {
		if (featureID == null) {
			return;
		}
		if (getMapFeatureID2CachedValue().containsKey(featureID)) {
			getMapFeatureID2CachedValue().remove(featureID);
		}
	}

}
