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
package es.cv.gvcase.mdt.common.parsers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.gmf.runtime.common.core.command.ICommand;
import org.eclipse.gmf.runtime.common.ui.services.parser.IParserEditStatus;
import org.eclipse.gmf.runtime.common.ui.services.parser.ParserEditStatus;
import org.eclipse.gmf.runtime.emf.ui.services.parser.ISemanticParser;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.viewers.ILabelProvider;

import es.cv.gvcase.mdt.common.Activator;
import es.cv.gvcase.mdt.common.commands.SetExtendedFeatureValueCommand;
import es.cv.gvcase.mdt.common.model.ExtendedFeatureElement;
import es.cv.gvcase.mdt.common.model.ExtendedFeatureElementFactory;
import es.cv.gvcase.mdt.common.model.Feature;
import es.cv.gvcase.mdt.common.util.MDTUtil;

/**
 * A semantic parser for an extended feature. <br>
 * Given the feature identifier it can give an edit string, a printing string,
 * and a command to parse the introduced text into a Bollean, Integer, double or
 * String. <br>
 * It can react to changes to referenced EObjects if the extended feature is a
 * reference one.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 */
public abstract class ExtendedFeatureSemanticParser implements ISemanticParser {

	/**
	 * Identifier of the feature to parse.
	 */
	private final String featureID;

	/** The label provider for feature value elements. */
	private ILabelProvider labelProvider = MDTUtil.getLabelProvider();

	/**
	 * Constructor with feature identifier.
	 * 
	 * @param featureID
	 */
	public ExtendedFeatureSemanticParser(String featureID) {
		this.featureID = featureID;
	}

	/**
	 * Getter for the feature identifier.
	 * 
	 * @return
	 */
	protected String getFeatureID() {
		return featureID;
	}

	/**
	 * Gets the feature from its identifier.
	 * 
	 * @return
	 */
	protected Feature getFeature() {
		return ExtendedFeatureElementFactory.getInstance()
				.getMapFeatureIDToFeature().get(getFeatureID());
	}

	/**
	 * Returns the given event as a Notification.
	 * 
	 * @param event
	 * @return
	 */
	protected Notification getNotification(Object event) {
		return (Notification) Platform.getAdapterManager().getAdapter(event,
				Notification.class);
	}

	/**
	 * ILabelProvider getter.
	 * 
	 * @return
	 */
	protected ILabelProvider getLabelProvider() {
		return labelProvider;
	}

	/**
	 * True if the notification affects the EAnnotation storing the extended
	 * feature.
	 */
	public boolean areSemanticElementsAffected(EObject listener,
			Object notification) {
		// retrieve the notification as an EMF Notification
		Notification event = getNotification(notification);
		if (event != null) {
			EAnnotation eAnnotation = (EAnnotation) Platform
					.getAdapterManager().getAdapter(event.getNotifier(),
							EAnnotation.class);
			if (eAnnotation != null
					&& getFeatureID().equals(eAnnotation.getSource())) {
				// this Notification is about a change in the extended feature
				// we are parsing, return true
				return true;
			}
		}
		return false;
	}

	/**
	 * The elements parsed are the EObject itself and those EObjects stored in
	 * the extended feature.
	 */
	public List getSemanticElementsBeingParsed(EObject element) {
		ExtendedFeatureElement extendedElement = ExtendedFeatureElementFactory
				.getInstance().asExtendedFeatureElement(element);
		List list = null;
		if (extendedElement != null) {
			// we get all the stored values from the extended feature
			Object value = extendedElement.getValue(getFeatureID());
			if (value != null) {
				list = (List) Platform.getAdapterManager().getAdapter(value,
						List.class);
				if (list != null) {
					list.add(element);
				} else {
					list = new ArrayList();
					list.add(element);
					list.add(value);
				}
			}
		}
		if (list != null && list.size() > 0) {
			// remove all Objects from the List that are not EObjects
			List toRemove = new ArrayList();
			for (Object object : list) {
				if (Platform.getAdapterManager().getAdapter(object,
						EObject.class) == null) {
					toRemove.add(object);
				}
			}
			list.removeAll(toRemove);
		}
		return list != null ? list : Collections.emptyList();
	}

	/**
	 * For subclasses to override. <br>
	 * Default return value is null.
	 */
	public IContentAssistProcessor getCompletionProcessor(IAdaptable element) {
		return null;
	}

	/**
	 * True is the given type can be stored as a String in the extended feature.
	 * Currently Boolean, Double, Integer and string types can be stored as
	 * Strings.
	 * 
	 * @param type
	 * @return
	 */
	protected boolean isStringableType(String type) {
		if (Feature.BooleanType.equals(type)) {
			return true;
		}
		if (Feature.DoubleType.equals(type)) {
			return true;
		}
		if (Feature.IntegerType.equals(type)) {
			return true;
		}
		if (Feature.StringType.equals(type)) {
			return true;
		}
		return false;
	}

	/**
	 * Get the edit String from the extended feature value(s).
	 */
	public String getEditString(IAdaptable element, int flags) {
		Feature feature = ExtendedFeatureElementFactory.getInstance()
				.getMapFeatureIDToFeature().get(getFeatureID());
		if (feature != null && isStringableType(feature.getType())) {
			EModelElement eModelElement = (EModelElement) element
					.getAdapter(EModelElement.class);
			ExtendedFeatureElement extendedElement = ExtendedFeatureElementFactory
					.getInstance().asExtendedFeatureElement(eModelElement);
			return getValueAsString(extendedElement.getValue(getFeatureID()));
		}
		return null;
	}

	/**
	 * Gets the value from the feature as a String.
	 * 
	 * @param value
	 * @return
	 */
	protected String getValueAsString(Object value) {
		if (value == null) {
			return null;
		}
		// StringBuilder to build the string from the values.
		StringBuilder string = new StringBuilder();
		Collection collection = (Collection) Platform.getAdapterManager()
				.getAdapter(value, Collection.class);
		if (collection != null) {
			for (Iterator iterator = collection.iterator(); iterator.hasNext();) {
				Object object = iterator.next();
				if (object != null) {
					// fetch each value's string representation and append it to
					// the string being built
					string.append(getObjectAsString(object));
					if (iterator.hasNext()) {
						string.append(", ");
					}
				}
			}
		}
		return string.toString();
	}

	/**
	 * Returns the given Object represented as a String.
	 * 
	 * @param value
	 * @return
	 */
	protected String getObjectAsString(Object value) {
		if (value == null) {
			return null;
		}
		String string = null;
		if (getLabelProvider() != null) {
			// try to get a string representation from a ILabelProvider
			string = getLabelProvider().getText(value);
		}
		// if the ILabelProvider gave no representation, we'll return the
		// default object string representation.
		return string != null ? string : value.toString();
	}

	/**
	 * Get a Boolean from a String.
	 * 
	 * @param text
	 * @return
	 */
	protected Boolean parseBoolean(String text) {
		return Boolean.valueOf(text);
	}

	/**
	 * Get an Integer from a String, or null.
	 * 
	 * @param text
	 * @return
	 */
	protected Integer parseInteger(String text) {
		try {
			return Integer.valueOf(text);
		} catch (NumberFormatException ex) {
			return null;
		}
	}

	/**
	 * Get a Double from a String, or null.
	 * 
	 * @param text
	 * @return
	 */
	protected Double parseDouble(String text) {
		try {
			return Double.valueOf(text);
		} catch (NumberFormatException ex) {
			return null;
		}
	}

	/**
	 * An ICommand that will set the extended feature to the value specified by
	 * the newString. <br>
	 * Currently can only work with Boolean, Double, Integer or String types
	 * extended features.
	 */
	public ICommand getParseCommand(IAdaptable element, String newString,
			int flags) {
		EModelElement eObject = (EModelElement) element
				.getAdapter(EModelElement.class);
		if (eObject == null || getFeature() == null) {
			return null;
		}
		// get the real value from the newString
		Object value = null;
		String type = getFeature().getType();
		if (Feature.BooleanType.equals(type)) {
			value = parseBoolean(newString);
		} else if (Feature.DoubleType.equals(type)) {
			value = parseDouble(newString);
		} else if (Feature.IntegerType.equals(type)) {
			value = parseInteger(newString);
		} else if (Feature.StringType.equals(type)) {
			value = newString;
		}
		// a SetExtendedFeatureValueCommand will set the feature value.
		SetExtendedFeatureValueCommand command = new SetExtendedFeatureValueCommand(
				TransactionUtil.getEditingDomain(eObject), eObject,
				getFeatureID(), value);

		return command;
	}

	/**
	 * A representation as a String of the extended feature's value(s)
	 */
	public String getPrintString(IAdaptable element, int flags) {
		Feature feature = ExtendedFeatureElementFactory.getInstance()
				.getMapFeatureIDToFeature().get(getFeatureID());
		if (feature != null && isStringableType(feature.getType())) {
			EModelElement eModelElement = (EModelElement) element
					.getAdapter(EModelElement.class);
			ExtendedFeatureElement extendedElement = ExtendedFeatureElementFactory
					.getInstance().asExtendedFeatureElement(eModelElement);
			return getValueAsString(extendedElement.getValue(getFeatureID()));
		}
		return null;
	}

	/**
	 * True if the event affects the EAnnotation that deals with the parsed
	 * extended feature.
	 */
	public boolean isAffectingEvent(Object event, int flags) {
		Notification notification = getNotification(event);
		if (notification != null) {
			EAnnotation eAnnotation = (EAnnotation) Platform
					.getAdapterManager().getAdapter(notification.getNotifier(),
							EAnnotation.class);
			if (eAnnotation != null
					&& getFeatureID().equals(eAnnotation.getSource())) {
				// the affected element is an EAnnotation with source equal to
				// the feature identifier this parser has, return true
				return true;
			}
		}
		return false;
	}

	/**
	 * For subclasses to override. <br>
	 * Default return value is Ok.
	 */
	public IParserEditStatus isValidEditString(IAdaptable element,
			String editString) {
		// default value
		return new ParserEditStatus(Activator.PLUGIN_ID,
				IParserEditStatus.EDITABLE, "Correct value");
	}

}
