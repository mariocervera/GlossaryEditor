/*******************************************************************************
 * Copyright (c) 2008-2011 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – Initial implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcoreFactory;

import es.cv.gvcase.mdt.common.Activator;
import es.cv.gvcase.mdt.common.util.MDTUtil;

/**
 * An extended feature, defined in an IExtensionPoint and stored in an
 * EModelElement via an EAnnotation. It can be of one of these types:
 * 
 * <li>EObject reference</li> <li>String</li> <li>Integer</li> <li>Double</li>
 * <li>Boolean</li> <br>
 * The getter methods return a good value only if this Feature if of the proper
 * type. The simple getter methods return a good value only when this feature is
 * monovalued. The List getter methods return a good value only when this
 * feature is multivalued. <br>
 * The setter, adder and remover methods check the given value to be of the
 * proper type. <br>
 * The setter, adder and remover methods can handle Collection, Object[] and
 * Object values.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public class Feature {

	/**
	 * Feature identifier.
	 */
	public String featureID;

	/**
	 * Human readable feature name.
	 */
	public String name;

	/**
	 * Type, one of {String, Int, Double, Boolean, Reference}
	 */
	public String type;

	public static final String ReferenceType = "Reference";
	public static final String StringType = "String";
	public static final String IntegerType = "Int";
	public static final String DoubleType = "Double";
	public static final String BooleanType = "Boolean";

	/**
	 * Allowed values for type.
	 */
	private static final String[] allowedTypes = new String[] { ReferenceType,
			StringType, IntegerType, DoubleType, BooleanType };

	public static String[] getAllowedTypes() {
		return allowedTypes;
	}

	/**
	 * Minimum multiplicity of this feature.
	 */
	public String minMultiplicity;
	private static final String UnboundedMaximum = "*";
	/**
	 * Maximum multiplicity of this feature.
	 */
	public String maxMultiplicity;
	/**
	 * List of children AdaptTo.
	 */
	public List<AdaptTo> AdaptTo;

	/**
	 * List of available values.
	 */
	public List<AvailableValue> AvailableValue;

	private List<Object> realAvailableValues = null;

	// Auxiliar helpers.

	/**
	 * Returns the list of available values removing those values that are not
	 * compatible with this feature type.
	 */
	protected List<AvailableValue> getRealAvailableValue() {
		if (this.AvailableValue != null) {
			List<AvailableValue> toRemove = new ArrayList<AvailableValue>();
			for (AvailableValue availableValue : this.AvailableValue) {
				if (!availableValue.isValueForType(getType())) {
					toRemove.add(availableValue);
				}
			}
			this.AvailableValue.removeAll(toRemove);
			return this.AvailableValue;
		} else {
			return Collections.emptyList();
		}
	}

	/**
	 * Gets the list of available values in their Object form, parsed from the
	 * Strings in the extension point.
	 * 
	 * @return
	 */
	public List<Object> getAvailableValues() {
		if (isMultiValued()) {
			return Collections.emptyList();
		}
		if (realAvailableValues == null) {
			realAvailableValues = new ArrayList<Object>();
			List<AvailableValue> availableValues = getRealAvailableValue();
			for (AvailableValue availableValue : availableValues) {
				Object value = availableValue.getValue(getType());
				if (value != null) {
					realAvailableValues.add(value);
				}
			}
		}
		return realAvailableValues;
	}

	/**
	 * Returns whether the given is one of the availables defined in the
	 * extension point. <br>
	 * If no available value was defined in the extension point, this returns
	 * true.
	 * 
	 * @param value
	 * @return
	 */
	protected boolean isAvailableValue(Object value) {
		return getAvailableValues() == null || getAvailableValues().size() == 0
				|| getAvailableValues().contains(value);
	}

	/**
	 * Gets an Object value from one of the available values.
	 * 
	 * @param value
	 * @return
	 */
	public Object getOfAvailableValues(String value) {
		if (value == "") {
			return null;
		}
		for (AvailableValue availableValue : getRealAvailableValue()) {
			if (availableValue.value.equals(value)) {
				return availableValue.getValue(getType());
			}
		}
		return null;
	}

	protected Boolean cachedIsReference = null;

	/**
	 * A reference features stores its references in the references field of an
	 * EAnnotation.
	 * 
	 * @return
	 */
	public boolean isReference() {
		if (cachedIsReference == null) {
			cachedIsReference = ReferenceType.equals(type);
		}
		return cachedIsReference;
	}

	protected Boolean cachedIsValued = null;

	/**
	 * A valued feature stores its values as Strings in the details field of an
	 * EAnnotation.
	 * 
	 * @return
	 */
	public boolean isValued() {
		if (cachedIsValued == null) {
			cachedIsValued = ReferenceType.equals(type) == false;
		}
		return cachedIsValued;
	}

	/**
	 * Checks if the provided object can be stored in this feature as value.
	 * 
	 * @param value
	 * @return
	 */
	public boolean acceptsType(Object value) {
		if (value != null) {
			if (isMonoValued() && isCollection(value)) {
				// a mono valued feature cannot store collections of objects
				return false;
			}
			List<Object> values = asList(value);
			if (isReference()) {
				// for a reference feature, all values must be references to
				// EOBjects
				for (Object o : values) {
					if (!isReferenceType(o)) {
						return false;
					}
				}
				return true;
			} else if (isValued()) {
				// for a valued feature, all values must be of a proper type
				for (Object o : values) {
					if (!isValueType(o) && isAvailableValue(o)) {
						return false;
					}
				}
				return true;
			} else {
				// unknown; false
				return false;
			}
		}
		if (isRequired()) {
			// if this feature requires a value, it cannot be set to null
			return false;
		}
		return true;
	}

	/**
	 * True if the given Object is a Collection or an Object[].
	 * 
	 * @param object
	 * @return
	 */
	protected boolean isCollection(Object object) {
		if (object == null) {
			return false;
		}
		Collection collection = (Collection) Platform.getAdapterManager()
				.getAdapter(object, Collection.class);
		if (collection != null) {
			return true;
		}
		Object[] objects = (Object[]) Platform.getAdapterManager().getAdapter(
				object, Object[].class);
		if (objects != null) {
			return true;
		}
		return false;
	}

	/**
	 * True if the given object is not null and not a Collection or an Object[].
	 * 
	 * @param object
	 * @return
	 */
	protected boolean isMonoValue(Object object) {
		return object != null && !isCollection(object);
	}

	/**
	 * Checks if this Feature can be applied to the given EModelElement.
	 * 
	 * @param element
	 * @return
	 */
	protected boolean isForType(EModelElement element) {
		if (element == null || featureID == null) {
			return false;
		}
		if (this.AdaptTo != null) {
			for (AdaptTo adaptTo : this.AdaptTo) {
				if (adaptTo.adapter != null) {
					if (MDTUtil.isOfType(element.getClass(), adaptTo.adapter)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Returns the minimum multiplicity as an integer.
	 * 
	 * @return
	 */
	protected int getMinMultiplicity() {
		if (minMultiplicity == null) {
			return 0;
		} else {
			try {
				return Integer.valueOf(minMultiplicity);
			} catch (NumberFormatException ex) {
				Activator.getDefault().logError(
						"Minimum multiplicity malformed", ex);
				return 0;
			}
		}
	}

	/**
	 * Returns the maximum multiplicity as an integer.
	 * 
	 * @return
	 */
	protected int getMaxMultiplicity() {
		if (maxMultiplicity == null) {
			return 0;
		} else {
			try {
				return Integer.valueOf(maxMultiplicity);
			} catch (NumberFormatException ex) {
				Activator.getDefault().logError(
						"Maximum multiplicity malformed", ex);
				return 0;
			}
		}
	}

	protected Boolean cachedMultiValued = null;

	/**
	 * True if this feature can store more than one value.
	 * 
	 * @return
	 */
	public boolean isMultiValued() {
		if (cachedMultiValued == null) {
			boolean isMultiValued = false;
			if (UnboundedMaximum.equals(maxMultiplicity)) {
				isMultiValued = true;
			} else {
				try {
					int min = getMinMultiplicity();
					int max = getMaxMultiplicity();
					if ((min == 1 || min == 0) && max == 1) {
						isMultiValued = false;
					} else {
						isMultiValued = true;
					}
				} catch (NumberFormatException ex) {
					isMultiValued = false;
				}
			}
			cachedMultiValued = isMultiValued;
		}
		return cachedMultiValued;
	}

	protected Boolean cachedMonoValued = null;

	/**
	 * True if this feature can store only one value.
	 * 
	 * @return
	 */
	public boolean isMonoValued() {
		if (cachedMonoValued == null) {
			cachedMonoValued = !isMultiValued();
		}
		return cachedMonoValued;
	}

	protected Boolean cachedIsRequired = null;

	/**
	 * True if minimum multiplicity is greater than 0.
	 * 
	 * @return
	 */
	protected boolean isRequired() {
		if (cachedIsRequired == null) {
			cachedIsRequired = getMinMultiplicity() > 0;
		}
		return cachedIsRequired;
	}

	/**
	 * Returns the type of this Feature.
	 * 
	 * @return
	 */
	public String getType() {
		return type;
	}

	/**
	 * Provides the given EAnnotation's keys details as a List.
	 * 
	 * @param eAnnotation
	 * @return
	 */
	protected List<Object> valuesAsList(EAnnotation eAnnotation) {
		if (eAnnotation == null) {
			return Collections.emptyList();
		}
		return new ArrayList<Object>(eAnnotation.getDetails().keySet());
	}

	/**
	 * Provides a good value for this unset feature.
	 * 
	 * @return
	 */
	protected Object nonExistantFeature() {
		if (isMultiValued()) {
			return Collections.emptyList();
		} else {
			return null;
		}
	}

	/**
	 * Retrieves the EAnnotation representing this feature for the given
	 * EModelElement. That EAnnotation will be created if it doesn't exist.
	 * 
	 * @param element
	 * @param source
	 * @return
	 */
	protected EAnnotation getEAnnotation(EModelElement element) {
		EAnnotation eAnnotation = element.getEAnnotation(featureID);
		return eAnnotation != null ? eAnnotation : createEAnnotation(element);
	}

	/**
	 * Created an EAnnotation in the given EModelElement representing this
	 * Feature.
	 * 
	 * @param element
	 * @return
	 */
	protected EAnnotation createEAnnotation(EModelElement element) {
		EAnnotation eAnnotation = EcoreFactory.eINSTANCE.createEAnnotation();
		eAnnotation.setSource(featureID);
		element.getEAnnotations().add(eAnnotation);
		return eAnnotation;
	}

	/**
	 * Checks that value the given value is an EObject.
	 * 
	 * @param value
	 * @return
	 */
	protected boolean isReferenceType(Object value) {
		boolean isReferenceType = false;
		EObject element = (EObject) Platform.getAdapterManager().getAdapter(
				value, EObject.class);
		if (element != null) {
			isReferenceType = true;
		} else {
			isReferenceType = false;
		}
		return isReferenceType;
	}

	/**
	 * Checks that the given value is of proper type for this Feature.
	 * 
	 * @param object
	 * @return
	 */
	protected boolean isValueType(Object object) {
		boolean isValueType = false;
		if (BooleanType.equals(type)) {
			isValueType = object instanceof Boolean;
		} else if (StringType.equals(type)) {
			isValueType = object instanceof String;
		} else if (IntegerType.equals(type)) {
			isValueType = object instanceof Integer;
		} else if (DoubleType.equals(type)) {
			isValueType = object instanceof Double;
		} else {
			isValueType = false;
		}
		return isValueType;
	}

	/**
	 * Returns the correct type of the given eObject
	 * 
	 * @param object
	 * @return
	 */
	public Object getTypedValue(Object object) {
		if (object == null) {
			return null;
		}
		if (StringType.equals(type)) {
			return asString(object);
		}
		if (IntegerType.equals(type)) {
			return asInteger(object);
		}
		if (DoubleType.equals(type)) {
			return asDouble(object);
		}
		if (BooleanType.equals(type)) {
			return asBoolean(object);
		}
		return null;
	}

	/**
	 * Returns the given value as a List. Collection and Object[] types are
	 * turned into a List. Monovalued objects are wrapped in a list.
	 * 
	 * @param object
	 * @return
	 */
	protected List<Object> asList(Object object) {
		List<Object> list = new ArrayList<Object>();
		if (isCollection(object)) {
			Collection collection = (Collection) Platform.getAdapterManager()
					.getAdapter(object, Collection.class);
			if (collection != null) {
				return new ArrayList<Object>(collection);
			}
			Object[] objects = (Object[]) Platform.getAdapterManager()
					.getAdapter(object, Object[].class);
			if (objects != null) {
				for (Object o : objects) {
					list.add(o);
				}
				return list;
			}
		} else if (object != null) {
			list.add(object);
		}
		return list;
	}

	/**
	 * Returns the given List of objects as a List of Strings.
	 * 
	 * @param objects
	 * @return
	 */
	protected List<String> asListOfStrings(List<Object> objects) {
		List<String> strings = new ArrayList<String>();
		for (Object object : objects) {
			strings.add(object.toString());
		}
		return strings;
	}

	/**
	 * Returns the given List of objects as a List of EObjects.
	 * 
	 * @param objects
	 * @return
	 */
	protected List<EObject> asListOfEObjects(List<Object> objects) {
		List<EObject> eObjects = new ArrayList<EObject>();
		for (Object object : objects) {
			eObjects.add(asEObject(object));
		}
		return eObjects;
	}

	/**
	 * Returns the given Object as an String.
	 * 
	 * @param value
	 * @return
	 */
	protected String asString(Object value) {
		if (value instanceof String) {
			return (String) value;
		}
		return null;
	}

	/**
	 * Returns the given Object as an Integer.
	 * 
	 * @param value
	 * @return
	 */
	protected Integer asInteger(Object value) {
		if (value instanceof Integer) {
			return (Integer) value;
		}

		String integerString = asString(value);
		Integer integer = null;
		try {
			integer = Integer.valueOf(integerString);
		} catch (NumberFormatException ex) {
			integer = null;
		}
		return integer;
	}

	/**
	 * Returns the given Object as a Double.
	 * 
	 * @param value
	 * @return
	 */
	protected Double asDouble(Object value) {
		if (value instanceof Double) {
			return (Double) value;
		}

		String doubleString = asString(value);
		Double double_ = null;
		try {
			double_ = Double.valueOf(doubleString);
		} catch (NumberFormatException ex) {
			double_ = null;
		}
		return double_;
	}

	/**
	 * Returns the given Object as a Boolean.
	 * 
	 * @param value
	 * @return
	 */
	protected Boolean asBoolean(Object value) {
		if (value instanceof Boolean) {
			return (Boolean) value;
		}

		String booleanString = asString(value);
		Boolean boolean_ = null;
		boolean_ = Boolean.valueOf(booleanString);
		return boolean_;
	}

	/**
	 * Returns the given value as an EObject.
	 * 
	 * @param value
	 * @return
	 */
	protected EObject asEObject(Object value) {
		return (EObject) Platform.getAdapterManager().getAdapter(value,
				EObject.class);
	}

	/**
	 * Parses the given string to the proper type of this extended feature.
	 * 
	 * @param s
	 * @return
	 */
	public Object valueOf(String s) {
		if (type.equals(IntegerType)) {
			return asInteger(s);
		} else if (type.equals(DoubleType)) {
			return asDouble(s);
		} else if (type.equals(BooleanType)) {
			return asBoolean(s);
		} else if (type.equals(StringType)) {
			return asString(s);
		} else {
			return null;
		}
	}

	/**
	 * Gives the number of elements this Feature is storing in the given
	 * EModelElement.
	 * 
	 * @param element
	 * @return
	 */
	protected int getSize(EModelElement element) {
		return asList(getValue(element)).size();
	}

	/**
	 * Checks that this Feature's multiplicity will be ok after adding the given
	 * value.
	 * 
	 * @param element
	 * @param addValue
	 * @return
	 */
	protected boolean checkAddMultiplicity(EModelElement element,
			Object addValue) {
		if (addValue == null) {
			return true;
		} else {
			List<Object> addValues = asList(addValue);
			int size = getSize(element);
			int totalSize = size + addValues.size();
			if (validatesMaximum(totalSize) && validatesMinimum(totalSize)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks that this Feature's multiplicity will be ok after removing the
	 * given value.
	 * 
	 * @param element
	 * @param removeValue
	 * @return
	 */
	protected boolean checkRemoveMultiplicity(EModelElement element,
			Object removeValue) {
		if (removeValue == null) {
			return true;
		} else {
			List<Object> removeValues = asList(removeValue);
			int size = getSize(element);
			int totalSize = size - removeValues.size();
			if (validatesMaximum(totalSize) && validatesMinimum(totalSize)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks that this Feature's multiplicity will be ok after setting the
	 * given value.
	 * 
	 * @param element
	 * @param newValue
	 * @return
	 */
	protected boolean checkSetMultiplicity(EModelElement element,
			Object newValue) {
		if (newValue == null) {
			if (isRequired()) {
				return false;
			}
			return true;
		} else {
			List<Object> newValues = asList(newValue);
			int size = newValues.size();
			if (validatesMaximum(size) && validatesMinimum(size)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check minimum multiplicity.
	 * 
	 * @param size
	 * @return
	 */
	protected boolean validatesMinimum(int size) {
		if (minMultiplicity == null) {
			return true;
		}
		int min = getMinMultiplicity();
		if (size >= min) {
			return true;
		}
		return false;
	}

	/**
	 * Check maximum multiplicity.
	 * 
	 * @param size
	 * @return
	 */
	protected boolean validatesMaximum(int size) {
		if (maxMultiplicity == null || UnboundedMaximum.equals(maxMultiplicity)) {
			return true;
		}
		int max = getMaxMultiplicity();
		if (size <= max) {
			return true;
		}
		return false;
	}

	// Extended features getters

	/**
	 * Returns this Feature's value for the given EModelElement as an EObject.
	 */
	public EObject getReference(EModelElement element) {
		if (element != null && isMonoValued() && ReferenceType.equals(type)) {
			Object value = getValue(element);
			return value != null ? asEObject(value) : null;
		}
		return null;
	}

	/**
	 * Returns this Feature's value for the given EModelElement as an String.
	 */
	public String getString(EModelElement element) {
		if (element != null && isMonoValued() && StringType.equals(type)) {
			Object value = getValue(element);
			return value != null ? asString(value) : null;
		}
		return null;
	}

	/**
	 * Returns this Feature's value for the given EModelElement as an Integer.
	 */
	public Integer getInteger(EModelElement element) {
		if (element != null && isMonoValued() && IntegerType.equals(type)) {
			Object value = getValue(element);
			return value != null ? asInteger(value) : null;
		}
		return null;
	}

	/**
	 * Returns this Feature's value for the given EModelElement as an Double.
	 */
	public Double getDouble(EModelElement element) {
		if (element != null && isMonoValued() && DoubleType.equals(type)) {
			Object value = getValue(element);
			return value != null ? asDouble(value) : null;
		}
		return null;
	}

	/**
	 * Returns this Feature's value for the given EModelElement as an Boolean.
	 */
	public Boolean getBoolean(EModelElement element) {
		if (element != null && isMonoValued() && BooleanType.equals(type)) {
			Object value = getValue(element);
			return value != null ? asBoolean(value) : null;
		}
		return null;
	}

	/**
	 * Returns this Feature's value for the given EModelElement as a List of
	 * EObjects.
	 */
	public List<EObject> getReferenceList(EModelElement element) {
		if (element != null && isMultiValued() && ReferenceType.equals(type)) {
			Object value = getValue(element);
			if (isCollection(value)) {
				List<Object> values = asList(value);
				List<EObject> eObjects = new ArrayList<EObject>();
				for (Object o : values) {
					EObject eObject = asEObject(o);
					if (eObject != null) {
						eObjects.add(eObject);
					}
				}
				return eObjects;
			}
		}
		return Collections.emptyList();
	}

	/**
	 * Returns this Feature's value for the given EModelElement as a List of
	 * Strings.
	 */
	public List<String> getStringList(EModelElement element) {
		if (element != null && isMultiValued() && StringType.equals(type)) {
			Object value = getValue(element);
			if (isCollection(value)) {
				List<Object> values = asList(value);
				List<String> strings = new ArrayList<String>();
				for (Object o : values) {
					String string = asString(o);
					if (string != null) {
						strings.add(string);
					}
				}
				return strings;
			}
		}
		return Collections.emptyList();
	}

	/**
	 * Returns this Feature's value for the given EModelElement as a List of
	 * Strings.
	 */
	public List<Integer> getIntegerList(EModelElement element) {
		if (element != null && isMultiValued() && IntegerType.equals(type)) {
			Object value = getValue(element);
			if (isCollection(value)) {
				List<Object> values = asList(value);
				List<Integer> integers = new ArrayList<Integer>();
				for (Object o : values) {
					Integer integer = asInteger(o);
					if (integer != null) {
						integers.add(integer);
					}
				}
				return integers;
			}
		}
		return Collections.emptyList();
	}

	/**
	 * Returns this Feature's value for the given EModelElement as a List of
	 * Strings.
	 */
	public List<Double> getDoubleList(EModelElement element) {
		if (element != null && isMultiValued() && DoubleType.equals(type)) {
			Object value = getValue(element);
			if (isCollection(value)) {
				List<Object> values = asList(value);
				List<Double> doubles = new ArrayList<Double>();
				for (Object o : values) {
					Double double_ = asDouble(o);
					if (double_ != null) {
						doubles.add(double_);
					}
				}
				return doubles;
			}
		}
		return Collections.emptyList();
	}

	/**
	 * Returns this Feature's value for the given EModelElement as a List of
	 * Booleans.
	 */
	public List<Boolean> getBooleanList(EModelElement element) {
		if (element != null && isMultiValued() && BooleanType.equals(type)) {
			Object value = getValue(element);
			if (isCollection(value)) {
				List<Object> values = asList(value);
				List<Boolean> booleans = new ArrayList<Boolean>();
				for (Object o : values) {
					Boolean boolean_ = asBoolean(o);
					if (boolean_ != null) {
						booleans.add(boolean_);
					}
				}
				return booleans;
			}
		}
		return Collections.emptyList();
	}

	/**
	 * Returns this Feature's raw value for the given EModelElement.
	 */
	public Object getValue(EModelElement element) {
		if (isReference()) {
			return getReferenced(element);
		} else if (isValued()) {
			return getValued(element);
		}
		if (isMultiValued()) {
			return Collections.emptyList();
		}
		return null;
	}

	/**
	 * Returns this Feature's raw valued value for the given EModelElement.
	 */
	protected Object getValued(EModelElement element) {
		EAnnotation eAnnotation = element.getEAnnotation(featureID);
		if (eAnnotation == null || eAnnotation.getDetails() == null
				|| eAnnotation.getDetails().size() <= 0) {
			return nonExistantFeature();
		}
		if (isMultiValued()) {
			List<Object> values = valuesAsList(eAnnotation);
			List<Object> values2 = transformValues(values);
			return values2;
		} else {
			Object value = eAnnotation.getDetails().keySet().iterator().next();
			return getTypedValue(value);
		}
	}

	private List<Object> transformValues(List<Object> values) {
		List<Object> values2 = new ArrayList<Object>();

		for (Object o : values) {
			values2.add(getTypedValue(o));
		}

		return values2;
	}

	/**
	 * Returns this Feature's raw refrenced value for the given EModelElement.
	 */
	protected Object getReferenced(EModelElement element) {
		EAnnotation eAnnotation = element.getEAnnotation(featureID);
		if (eAnnotation == null || eAnnotation.getReferences() == null
				|| eAnnotation.getReferences().size() <= 0) {
			return nonExistantFeature();
		}
		if (isMultiValued()) {
			return eAnnotation.getReferences();
		} else {
			return eAnnotation.getReferences().get(0);
		}
	}

	// extended features setters

	/**
	 * Set this Feature's value in the given EModelElement. Checks type and
	 * multiplicity.
	 */
	public boolean setValue(EModelElement element, Object value) {
		if (element != null /* && isForType(element) */&& acceptsType(value)
				&& checkSetMultiplicity(element, value)) {
			EAnnotation eAnnotation = getEAnnotation(element);
			if (isReference()) {
				if (isMonoValued()) {
					return setReferencedMonoValued(eAnnotation,
							asEObject(value));
				} else if (isMultiValued()) {
					List<Object> objects = asList(value);
					return setReferencedMultiValued(eAnnotation,
							asListOfEObjects(objects));
				}
			} else if (isValued()) {
				if (isMonoValued()) {
					return setValueMonoValued(eAnnotation, value.toString());
				} else if (isMultiValued()) {
					List<Object> objects = asList(value);
					return setValueMultiValued(eAnnotation,
							asListOfStrings(objects));
				}
			} else {
				return false;
			}
		}
		return false;
	}

	/**
	 * Set this Feature's mono valued value.
	 * 
	 * @param eAnnotation
	 * @param value
	 * @return
	 */
	protected boolean setValueMonoValued(EAnnotation eAnnotation, String value) {
		if (eAnnotation != null) {
			eAnnotation.getDetails().clear();
			if (value != null) {
				eAnnotation.getDetails().put(value, null);
			}
			return true;
		}
		return false;
	}

	/**
	 * Set this Feature's mono valued reference.
	 * 
	 * @param eAnnotation
	 * @param referenced
	 * @return
	 */
	protected boolean setReferencedMonoValued(EAnnotation eAnnotation,
			EObject referenced) {
		if (eAnnotation != null) {
			eAnnotation.getReferences().clear();
			if (referenced != null) {
				eAnnotation.getReferences().add(referenced);
			}
			return true;
		}
		return false;
	}

	/**
	 * Set this Feature's multi valued value.
	 * 
	 * @param eAnnotation
	 * @param values
	 * @return
	 */
	protected boolean setValueMultiValued(EAnnotation eAnnotation,
			Collection<String> values) {
		if (eAnnotation != null) {
			eAnnotation.getDetails().clear();
			if (values != null && values.size() > 0) {
				for (String string : values) {
					eAnnotation.getDetails().put(string, null);
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * Set this Feature's multi valued reference.
	 * 
	 * @param eAnnotation
	 * @param values
	 * @return
	 */
	protected boolean setReferencedMultiValued(EAnnotation eAnnotation,
			Collection<EObject> values) {
		if (eAnnotation != null) {
			eAnnotation.getReferences().clear();
			if (values != null && values.size() > 0) {
				for (EObject eObject : values) {
					eAnnotation.getReferences().add(eObject);
				}
			}
			return true;
		}
		return false;
	}

	// Adder methods

	/**
	 * Add the given value to this Feature in the given EModelElement.
	 */
	public boolean addValue(EModelElement element, Object value) {
		if (element != null && isForType(element) && acceptsType(value)
				&& checkAddMultiplicity(element, value)) {
			EAnnotation eAnnotation = getEAnnotation(element);
			if (isReference()) {
				List<EObject> eObjects = asListOfEObjects(asList(value));
				return addSeveralReferenceValue(eAnnotation, eObjects);
			} else if (isValued()) {
				List<String> values = asListOfStrings(asList(value));
				return addSeveralValueValue(eAnnotation, values);
			} else {
				return false;
			}
		}
		return false;
	}

	/**
	 * Add several values to references in the given EModelElement.
	 * 
	 * @param eAnnotation
	 * @param eObjects
	 * @return
	 */
	protected boolean addSeveralReferenceValue(EAnnotation eAnnotation,
			Collection<EObject> eObjects) {
		if (eAnnotation != null && eObjects != null && eObjects.size() > 0) {
			for (EObject eObject : eObjects) {
				if (!addOneReferenceValue(eAnnotation, eObject)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * Add several values to this Feature's values in the given EModelElement.
	 * 
	 * @param eAnnotation
	 * @param values
	 * @return
	 */
	protected boolean addSeveralValueValue(EAnnotation eAnnotation,
			Collection<String> values) {
		if (eAnnotation != null && values != null && values.size() > 0) {
			for (String string : values) {
				if (!addOneValueValue(eAnnotation, string)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * Add one reference to this Feature's references in the given
	 * EModelElement.
	 * 
	 * @param eAnnotation
	 * @param eObject
	 * @return
	 */
	protected boolean addOneReferenceValue(EAnnotation eAnnotation,
			EObject eObject) {
		if (eAnnotation != null) {
			eAnnotation.getReferences().add(eObject);
			return true;
		}
		return false;
	}

	/**
	 * Add one value to this Feature's values in the given EModelElement.
	 * 
	 * @param eAnnotation
	 * @param value
	 * @return
	 */
	protected boolean addOneValueValue(EAnnotation eAnnotation, String value) {
		if (eAnnotation != null) {
			eAnnotation.getDetails().put(value, null);
			return true;
		}
		return false;
	}

	// Remover methods

	/**
	 * Removes the given value from this Feature in the given EModelElement.
	 * Value can be a Collection, an Object[] or an Object.
	 */
	public boolean removeValue(EModelElement element, Object value) {
		if (element != null && isForType(element) && acceptsType(value)
				&& checkRemoveMultiplicity(element, value)) {
			EAnnotation eAnnotation = getEAnnotation(element);
			if (isReference()) {
				List<EObject> values = asListOfEObjects(asList(value));
				return removeSeveralReferences(eAnnotation, values);
			} else if (isValued()) {
				List<String> strings = asListOfStrings(asList(value));
				return removeSeveralValues(eAnnotation, strings);
			} else {
				return false;
			}
		}
		return false;
	}

	/**
	 * Removes several references from this Feature's references in the given
	 * EModelElement.
	 * 
	 * @param eAnnotation
	 * @param eObjects
	 * @return
	 */
	protected boolean removeSeveralReferences(EAnnotation eAnnotation,
			List<EObject> eObjects) {
		if (eAnnotation != null && eObjects != null && eObjects.size() > 0) {
			for (EObject eObject : eObjects) {
				if (!removeOneReference(eAnnotation, eObject)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * Removes several values from this Feature's values in the given
	 * EModelElement.
	 * 
	 * @param eAnnotation
	 * @param values
	 * @return
	 */
	protected boolean removeSeveralValues(EAnnotation eAnnotation,
			List<String> values) {
		if (eAnnotation != null && values != null && values.size() > 0) {
			for (String value : values) {
				if (!removeOneValue(eAnnotation, value)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * Removes one reference from this Feature's references in the given
	 * EModelElement.
	 * 
	 * @param eAnnotation
	 * @param reference
	 * @return
	 */
	protected boolean removeOneReference(EAnnotation eAnnotation,
			EObject reference) {
		if (eAnnotation != null && reference != null) {
			eAnnotation.getReferences().remove(reference);
			return !eAnnotation.getReferences().contains(reference);
		}
		return false;
	}

	/**
	 * Removes one value from this Feature's values in the given EModelElement.
	 * 
	 * @param eAnnotation
	 * @param value
	 * @return
	 */
	protected boolean removeOneValue(EAnnotation eAnnotation, String value) {
		if (eAnnotation != null && value != null) {
			eAnnotation.getDetails().remove(value);
			return !eAnnotation.getDetails().containsKey(value);
		}
		return false;
	}

}
