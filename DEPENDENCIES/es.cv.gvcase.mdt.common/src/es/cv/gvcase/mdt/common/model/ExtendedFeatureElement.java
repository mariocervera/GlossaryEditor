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

import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;

/**
 * Interface for feature extender. The different get methods will return a valid
 * value only if the feature stores an object of that type.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public interface ExtendedFeatureElement extends IAdaptable {

	/**
	 * Provides the extended <EModelElement>.
	 * 
	 * @return
	 */
	EModelElement getElement();

	/**
	 * Raw stored value.
	 * 
	 * @param featureID
	 * @return
	 */
	Object getValue(String featureID);

	/**
	 * Stored value as a reference to an <EObject>.
	 * 
	 * @param featureID
	 * @return
	 */
	EObject getReference(String featureID);

	/**
	 * Stored value as a <String>.
	 * 
	 * @param featureID
	 * @return
	 */
	String getString(String featureID);

	/**
	 * Stored value as an <Integer>.
	 * 
	 * @param featureID
	 * @return
	 */
	Integer getInteger(String featureID);

	/**
	 * Stored value as a <Double>.
	 * 
	 * @param featureID
	 * @return
	 */
	Double getDouble(String featureID);

	/**
	 * Stored value as a <Boolean>.
	 * 
	 * @param featureID
	 * @return
	 */
	Boolean getBoolean(String featureID);

	/**
	 * Stored value as a <List> of <EObject>s.
	 * 
	 * @param featureID
	 * @return
	 */
	List<EObject> getReferenceList(String featureID);

	/**
	 * Stored value as a <List> of <String>s.
	 * 
	 * @param featureID
	 * @return
	 */
	List<String> getStringList(String featureID);

	/**
	 * Stored value as a <List> of <Integer>s.
	 * 
	 * @param featureID
	 * @return
	 */
	List<Integer> getIntegerList(String featureID);

	/**
	 * Stored value as a <List> of <Double>s.
	 * 
	 * @param featureID
	 * @return
	 */
	List<Double> getDoubleList(String featureID);

	/**
	 * Stored value as a <List> of <Boolean>s.
	 * 
	 * @param featureID
	 * @return
	 */
	List<Boolean> getBooleanList(String featureID);

	/**
	 * Set the specified feature to the specified value. If the given value
	 * cannot be stored in the given feature, the feature value is not changed.
	 * Value can be a single value, a <Collection> of values or null.
	 * 
	 * @param featureID
	 * @param value
	 * @return
	 */
	boolean setValue(String featureID, Object value);

	/**
	 * Add the specified feature to the specified value. If the given value
	 * cannot be stored in the given feature, the feature value is not changed.
	 * Value can be a single value or a <Collection> of values.
	 * 
	 * @param featureID
	 * @param value
	 * @return
	 */
	boolean addValue(String featureID, Object value);

	/**
	 * Remove the specified value from the specified feature. Value can be a
	 * single value or a <Collection> of values.
	 * 
	 * @param featureID
	 * @param value
	 * @return
	 */
	boolean removeValue(String featureID, Object value);

	/**
	 * Sets the specified feature to null.
	 * 
	 * @param featureID
	 * @return
	 */
	boolean unsetValue(String featureID);

}
