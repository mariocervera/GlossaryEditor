/*******************************************************************************
 * Copyright (c) 2009 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 * Marc Gil Sendra (Prodevelop) - Initial implementation
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.parsers;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.gmf.runtime.common.core.command.ICommand;
import org.eclipse.gmf.runtime.common.core.command.UnexecutableCommand;
import org.eclipse.gmf.runtime.common.ui.services.parser.IParserEditStatus;
import org.eclipse.gmf.runtime.common.ui.services.parser.ParserEditStatus;
import org.eclipse.gmf.runtime.emf.commands.core.command.CompositeTransactionalCommand;
import org.eclipse.osgi.util.NLS;

import es.cv.gvcase.mdt.common.Activator;
import es.cv.gvcase.mdt.common.commands.SetExtendedFeatureValueCommand;
import es.cv.gvcase.mdt.common.model.ExtendedFeatureElementFactory;
import es.cv.gvcase.mdt.common.model.Feature;

/**
 * This class provides an specific Parser to show the EMF features associated to
 * the label and the Extended Features added through Extension Point and the
 * user wants to provide a mechanism to edit them through the label.
 * 
 * The extended features are shown separated with a separator (default: ' - ').
 * When editing the label, the name of the feature will be shown to let the user
 * to know which extended feature is modifying.
 * 
 * @author mgil
 */
public class GlobalParser extends MessageFormatParser {

	/**
	 * The list of Extended Features with value
	 */
	protected String[] extendedFeatures;

	/**
	 * The list of Extended Features with or without value
	 */
	protected final String[] extendedFeaturesBackup;

	/**
	 * The Extended Features separator
	 */
	protected String separator;

	/**
	 * @param features
	 *            the list of EMF features
	 */
	public GlobalParser(EAttribute[] features) {
		this(features, new String[] {}, "-");
	}

	/**
	 * @param features
	 *            the list of EMF features
	 * @param separator
	 *            the separator for the Extended Features
	 */
	protected GlobalParser(EAttribute[] features, String separator) {
		this(features, new String[] {}, separator);
	}

	/**
	 * @param features
	 *            The list of EMF features
	 * @param extendedFeatures
	 *            The list of Extended Features
	 */
	public GlobalParser(EAttribute[] features, String[] extendedFeatures) {
		this(features, extendedFeatures, "-");
	}

	/**
	 * @param features
	 *            The list of EMF features
	 * @param extendedFeatures
	 *            The list of Extended Features
	 * @param separator
	 *            The separator for the Extended Features
	 */
	public GlobalParser(EAttribute[] features, String[] extendedFeatures,
			String separator) {
		super(features);
		this.extendedFeatures = extendedFeatures;
		this.extendedFeaturesBackup = extendedFeatures;
		this.separator = " " + separator + " ";
	}

	/**
	 * The method to show the values when the label is not in editing mode
	 */
	@Override
	public String getPrintString(IAdaptable adapter, int flags) {
		EModelElement eModelElement = (EModelElement) adapter
				.getAdapter(EModelElement.class);

		// check if there are extended features without value (there not exist,
		// but if exist we want to show the value)
		checkExtendedFeatures(eModelElement);

		// update the View Pattern according to the number of the features
		setViewPattern(eModelElement);

		// calculate the string to show when not editing
		return super.getPrintString(adapter, flags);
	}

	/**
	 * The method to show the values when the label is in editing mode
	 */
	@Override
	public String getEditString(IAdaptable adapter, int flags) {
		EModelElement eModelElement = (EModelElement) adapter
				.getAdapter(EModelElement.class);

		// check if there are extended features without value (there not exist,
		// but if exist we want to show the value)
		checkExtendedFeatures(eModelElement);

		// update the Editor Pattern according to the number of the features
		setEditorPattern(eModelElement);

		// calculate the string to show when editing
		return super.getEditString(adapter, flags);
	}

	/**
	 * Update the View Pattern according to the number of the features
	 * 
	 * @param eModelElement
	 */
	protected void setViewPattern(EModelElement eModelElement) {
		String viewPattern = "";

		StringBuffer sb = new StringBuffer();
		int parser = 0;
		for (int i = 0; i < features.length; i++) {
			if (i > 0) {
				sb.append(' ');
			}
			sb.append('{');
			sb.append(parser++);
			sb.append('}');
		}
		viewPattern = sb.toString();

		sb = new StringBuffer();
		for (int i = 0; i < extendedFeatures.length; i++) {
			if (i == 0) {
				sb.append(separator);
			} else if (i > 0) {
				sb.append(separator);
			}
			sb.append("[{");
			sb.append(parser++);
			sb.append("}]");
		}
		viewPattern += sb.toString();

		super.setViewPattern(viewPattern);
	}

	/**
	 * Update the Edit Pattern according to the number of the features
	 * 
	 * @param eModelElement
	 */
	protected void setEditPattern(EModelElement eModelElement) {
		String editPattern = "";

		StringBuffer sb = new StringBuffer();
		int parser = 0;
		for (int i = 0; i < features.length; i++) {
			if (i > 0) {
				sb.append(' ');
			}
			sb.append('{');
			sb.append(parser++);
			sb.append('}');
		}
		editPattern = sb.toString();

		sb = new StringBuffer();
		for (int i = 0; i < extendedFeaturesBackup.length; i++) {
			if (i == 0) {
				sb.append(separator);
			} else if (i > 0) {
				sb.append(separator);
			}

			Feature feature = ExtendedFeatureElementFactory.getInstance()
					.getMapFeatureIDToFeature().get(extendedFeaturesBackup[i]);
			if (feature == null) {
				continue;
			}

			sb.append("[" + feature.name + ": ");
			sb.append("{");
			sb.append(parser++);
			sb.append("}" + "]");
		}
		editPattern += sb.toString();

		super.setEditPattern(editPattern);
	}

	/**
	 * Update the Editor Pattern according to the number of the features
	 * 
	 * @param eModelElement
	 */
	protected void setEditorPattern(EModelElement eModelElement) {
		String editorPattern = "";

		StringBuffer sb = new StringBuffer();
		int parser = 0;
		for (int i = 0; i < features.length; i++) {
			if (i > 0) {
				sb.append(' ');
			}
			sb.append('{');
			sb.append(parser++);
			sb.append('}');
		}
		editorPattern = sb.toString();

		sb = new StringBuffer();
		for (int i = 0; i < extendedFeaturesBackup.length; i++) {
			if (i == 0) {
				sb.append(separator);
			} else if (i > 0) {
				sb.append(separator);
			}

			Feature feature = ExtendedFeatureElementFactory.getInstance()
					.getMapFeatureIDToFeature().get(extendedFeaturesBackup[i]);
			if (feature == null) {
				continue;
			}

			sb.append("[" + feature.name + ": ");
			sb.append("{");
			sb.append(parser++);
			sb.append("}" + "]");
		}
		editorPattern += sb.toString();

		super.setEditorPattern(editorPattern);
	}

	/**
	 * Calculate all the values to be present in the label
	 */
	@Override
	protected Object[] getValues(EObject element) {
		// collect all the EMF Features
		Object[] featureValues = super.getValues(element);

		// collect all the Extended Features
		EModelElement eModelElement = (EModelElement) Platform
				.getAdapterManager().getAdapter(element, EModelElement.class);
		Object[] extendedFeatureValues = new Object[extendedFeaturesBackup.length];
		for (int i = 0; i < extendedFeaturesBackup.length; i++) {
			Object o = ExtendedFeatureElementFactory.getInstance()
					.getObjectFeatureValue(eModelElement,
							extendedFeaturesBackup[i]);

			// if the feature hasn't feature (doesn't exist), show an empty
			// label instead of nothing
			if (o == null) {
				extendedFeatureValues[i] = "";
			} else {
				extendedFeatureValues[i] = o;
			}
		}

		// return the merge of the two lists as an Array
		ArrayList<Object> list = new ArrayList<Object>();
		list.addAll(Arrays.asList(featureValues));
		list.addAll(Arrays.asList(extendedFeatureValues));
		return list.toArray();
	}

	/**
	 * Check if the given string is valid, according of the type of the features
	 */
	@Override
	public IParserEditStatus isValidEditString(IAdaptable adapter,
			String editString) {
		EModelElement eModelElement = (EModelElement) adapter
				.getAdapter(EModelElement.class);

		// check if there are extended features without value (there not exist,
		// but if exist we want to show the value)
		checkExtendedFeatures(eModelElement);

		// update the Edit Pattern according to the number of the features
		setEditPattern(eModelElement);

		ParsePosition pos = new ParsePosition(0);

		// parse the value with the correct calculated pattern
		Object[] values = getEditProcessor().parse(editString, pos);
		if (values == null) {
			return new ParserEditStatus(Activator.PLUGIN_ID,
					IParserEditStatus.UNEDITABLE, NLS.bind("Invalid input: ",
							new Integer(pos.getErrorIndex())));
		}

		// check if all the values are correct. Every value with the
		// corresponding feature
		return validateNewValues(eModelElement, values);
	}

	/**
	 * Collect all the commands to place the changes into the model, one command
	 * for every changed feature
	 */
	@Override
	protected ICommand getParseCommand(IAdaptable adapter, Object[] values,
			int flags) {
		EModelElement eModelElement = (EModelElement) adapter
				.getAdapter(EModelElement.class);

		// check if there are extended features without value (there not exist,
		// but if exist we want to show the value)
		checkExtendedFeatures(eModelElement);

		// update the Edit Pattern according to the number of the features
		setEditPattern(eModelElement);

		// check if all the values are correct. Every value with the
		// corresponding feature
		if (values == null
				|| validateNewValues(eModelElement, values).getCode() != IParserEditStatus.EDITABLE) {
			return UnexecutableCommand.INSTANCE;
		}

		TransactionalEditingDomain editingDomain = TransactionUtil
				.getEditingDomain(eModelElement);
		if (editingDomain == null) {
			return UnexecutableCommand.INSTANCE;
		}

		CompositeTransactionalCommand command = new CompositeTransactionalCommand(
				editingDomain, "Set Values"); //$NON-NLS-1$

		// check all the features. First the EMF Features, and then the Extended
		// Features
		for (int i = 0; i < values.length; i++) {
			try {
				// process the EMF Features

				// for every EMF property, get the pertinent command
				ICommand c = getModificationCommand(eModelElement, features[i],
						values[i]);
				if (c != null && c.canExecute()) {
					command.compose(c);
				}
			} catch (IndexOutOfBoundsException ex) {
				// process the Extended Features

				// for every EMF property, get the pertinent command
				ICommand c = getExtendedFeatureModificationCommand(
						eModelElement, extendedFeaturesBackup[i
								- features.length], values[i]);
				if (c != null && c.canExecute()) {
					command.compose(c);
				}
			}
		}
		return command;
	}

	/**
	 * Creates the MessageFormat for the parser when editing the label. We will
	 * change the Format for the Integer values, to remove the number groups
	 */
	@Override
	protected MessageFormat createEditorProcessor(String editorPattern) {
		MessageFormat messageFormat = new MessageFormat(editorPattern);

		for (int i = 0; i < (features.length + extendedFeatures.length); i++) {
			try {
				// process first the EMF features...
				EClassifier type = features[i].getEType();
				if (!(type instanceof EDataType)) {
					continue;
				}
				Class iClass = type.getInstanceClass();
				if (Integer.TYPE.equals(iClass)) {
					DecimalFormat df = new DecimalFormat();
					df.setMaximumFractionDigits(0);
					df.setGroupingUsed(false);

					messageFormat.setFormatByArgumentIndex(i, df);
				}
			} catch (IndexOutOfBoundsException ex) {
				// ... then, the Extended Features
				String featureID = extendedFeatures[i - features.length];
				Feature feature = ExtendedFeatureElementFactory.getInstance()
						.getMapFeatureIDToFeature().get(featureID);
				if (feature == null) {
					continue;
				}

				String type = feature.getType();
				if (type.equals(Feature.IntegerType)) {
					DecimalFormat df = new DecimalFormat();
					df.setMaximumFractionDigits(0);
					df.setGroupingUsed(false);

					messageFormat.setFormatByArgumentIndex(i, df);
				}
			}
		}

		return messageFormat;
	}

	/**
	 * Check if the given event affects to the label to be updated
	 */
	@Override
	public boolean isAffectingEvent(Object event, int flags) {
		// check first the EMF Features...
		boolean isAffecting = super.isAffectingEvent(event, flags);

		// ... then the Extended Features
		if (event instanceof Notification) {
			Notification notification = (Notification) event;
			if (notification.getNotifier() instanceof EAnnotation) {
				EAnnotation eAnnotation = (EAnnotation) notification
						.getNotifier();

				boolean shouldUpdate = false;
				for (int i = 0; i < extendedFeatures.length; i++) {
					if (eAnnotation.getSource().equals(extendedFeatures[i])) {
						shouldUpdate = true;
					}
				}

				if (shouldUpdate) {
					EModelElement eModelElement = (EModelElement) eAnnotation
							.eContainer();

					// update the parsers if an Extended Feature is modified,
					// because the modification could provoke the delete or the
					// creation of the Feature
					updateParser(eModelElement);
				}
			} else if (notification.getFeature().equals(
					EcorePackage.eINSTANCE.getEModelElement_EAnnotations())) {
				EModelElement eModelElement = (EModelElement) notification
						.getNotifier();
				updateParser(eModelElement);
			}
		}

		return isAffecting;
	}

	/**
	 * Check if the given feature affects to the label to be updated
	 */
	@Override
	protected boolean isAffectingFeature(Object feature) {
		// check first the EMF Features...
		boolean isAffecting = super.isAffectingFeature(feature);

		// ... then the Extended Features
		if (feature.equals(EcorePackage.eINSTANCE
				.getEModelElement_EAnnotations())
				|| feature.equals(EcorePackage.eINSTANCE
						.getEAnnotation_Details())) {
			isAffecting = true;
		}

		return isAffecting;
	}

	/**
	 * Creates a command to change the value for the Extended Feature
	 * 
	 * @param element
	 * @param featureID
	 * @param value
	 * @return
	 */
	protected ICommand getExtendedFeatureModificationCommand(EObject element,
			String featureID, Object value) {
		Feature feature = ExtendedFeatureElementFactory.getInstance()
				.getMapFeatureIDToFeature().get(featureID);
		if (feature == null) {
			return UnexecutableCommand.INSTANCE;
		}

		value = getValidNewValueForExtendedFeature(feature, value);
		if (value instanceof InvalidValue) {
			return UnexecutableCommand.INSTANCE;
		}
		SetExtendedFeatureValueCommand command = new SetExtendedFeatureValueCommand(
				TransactionUtil.getEditingDomain(element),
				(EModelElement) element, featureID, value);
		return command;
	}

	/**
	 * Check if all the values are correct. Every value with the corresponding
	 * feature
	 * 
	 * @param eModelElement
	 * @param values
	 * @return
	 */
	protected IParserEditStatus validateNewValues(EModelElement eModelElement,
			Object[] values) {
		// the length of the values should be the same of all the features
		if (values.length != (features.length + extendedFeaturesBackup.length)) {
			return ParserEditStatus.UNEDITABLE_STATUS;
		}

		for (int i = 0; i < values.length; i++) {
			try {
				// first, process the EMF Features...
				Object value = getValidNewValue(features[i], values[i]);
				if (value instanceof InvalidValue) {
					return new ParserEditStatus(Activator.PLUGIN_ID,
							IParserEditStatus.UNEDITABLE, value.toString());
				}
			} catch (IndexOutOfBoundsException ex) {
				// ... then the Extended Features
				String featureID = extendedFeaturesBackup[i - features.length];
				Feature feature = ExtendedFeatureElementFactory.getInstance()
						.getMapFeatureIDToFeature().get(featureID);
				if (feature == null) {
					continue;
				}

				// check if the new value is correct for the given Extended
				// Feature
				Object ob = getValidNewValueForExtendedFeature(feature,
						values[i]);

				if (ob instanceof InvalidValue) {
					return new ParserEditStatus(Activator.PLUGIN_ID,
							IParserEditStatus.UNEDITABLE, "Different types");
				}
			}
		}
		return ParserEditStatus.EDITABLE_STATUS;
	}

	/**
	 * check if the new value is correct for the given Extended Feature
	 * 
	 * @param feature
	 * @param value
	 * @return
	 */
	protected Object getValidNewValueForExtendedFeature(Feature feature,
			Object value) {
		String type = feature.getType();
		if (type.equals(Feature.BooleanType)) {
			if (value instanceof Boolean) {
				// ok
			} else if (value instanceof String) {
				value = Boolean.valueOf((String) value);
			} else {
				value = new InvalidValue(NLS.bind("Unexpected Value Type: ",
						Feature.BooleanType));
			}
		} else if (type.equals(Feature.IntegerType)) {
			if (value instanceof Integer) {
				// ok
			} else if (value instanceof Number) {
				value = new Integer(((Number) value).intValue());
			} else if (value instanceof String) {
				String s = (String) value;
				if (s.length() == 0) {
					value = null;
				} else {
					try {
						value = Integer.valueOf(s);
					} catch (NumberFormatException nfe) {
						value = new InvalidValue(NLS.bind(
								"Wrong String conversion: ",
								Feature.IntegerType));
					}
				}
			} else {
				value = new InvalidValue(NLS.bind("Unexpected Value Type: ",
						Feature.IntegerType));
			}
		} else if (type.equals(Feature.DoubleType)) {
			if (value instanceof Double) {
				// ok
			} else if (value instanceof Number) {
				value = new Double(((Number) value).doubleValue());
			} else if (value instanceof String) {
				String s = (String) value;
				if (s.length() == 0) {
					value = null;
				} else {
					try {
						value = Double.valueOf(s);
					} catch (NumberFormatException nfe) {
						value = new InvalidValue(NLS
								.bind("Wrong String conversion: ",
										Feature.DoubleType));
					}
				}
			} else {
				value = new InvalidValue(NLS.bind("Unexpected Value Type: ",
						Feature.DoubleType));
			}
		} else if (type.equals(Feature.StringType)) {
			// ok
		}

		return value;
	}

	/**
	 * Update the view, edit and editor patterns for the parser
	 * 
	 * @param eModelElement
	 */
	private void updateParser(EModelElement eModelElement) {
		checkExtendedFeatures(eModelElement);
		setViewPattern(eModelElement);
		setEditPattern(eModelElement);
		setEditorPattern(eModelElement);
	}

	/**
	 * check if there are extended features without value (there not exist, but
	 * if exist we want to show the value)
	 * 
	 * @param eModelElement
	 */
	private void checkExtendedFeatures(EModelElement eModelElement) {
		List<String> extendedFeaturesList = new ArrayList<String>();

		for (int i = 0; i < extendedFeaturesBackup.length; i++) {
			Object o = ExtendedFeatureElementFactory.getInstance()
					.getObjectFeatureValue(eModelElement,
							extendedFeaturesBackup[i]);

			if (o != null) {
				extendedFeaturesList.add(extendedFeaturesBackup[i]);
			}
		}

		extendedFeatures = extendedFeaturesList.toArray(new String[0]);
	}
}
