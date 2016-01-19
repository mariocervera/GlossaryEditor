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
package es.cv.gvcase.mdt.common.sections.extended;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecoretools.tabbedproperties.sections.AbstractTextPropertySection;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;

import es.cv.gvcase.mdt.common.commands.SetExtendedFeatureValueCommand;
import es.cv.gvcase.mdt.common.model.ExtendedFeatureElement;
import es.cv.gvcase.mdt.common.model.ExtendedFeatureElementFactory;
import es.cv.gvcase.mdt.common.model.Feature;
import es.cv.gvcase.mdt.common.sections.descriptions.ControlDescriptionDecoratorProvider;

/**
 * An abstract property section that can handle extended features via
 * EAnnotations.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public abstract class AbstractExtendedTextPropertySection extends
		AbstractTextPropertySection {

	/**
	 * Constructor with feature identifier.
	 * 
	 * @param featureID
	 */
	public AbstractExtendedTextPropertySection(String featureID) {
		this.featureID = featureID;
		if (!verifyFeature()) {
			throw new IllegalArgumentException("The feature '" + featureID
					+ "' cannot be edited whit this section.");
		}
	}
	
	@Override
	protected EditingDomain getEditingDomain() {
		try {
			return super.getEditingDomain();
		} catch (IllegalArgumentException ex) {
			return null;
		}
	}

	/**
	 * Verifies that the given feature can be checked with this property sheet.
	 * 
	 * @return True if this property section can edit the given feature.
	 */
	protected boolean verifyFeature() {
		Feature feature = ExtendedFeatureElementFactory.getInstance()
				.getMapFeatureIDToFeature().get(getFeatureID());
		if (feature == null) {
			return false;
		}
		if (feature.isValued() && feature.isMonoValued()
				&& feature.getType().equals(Feature.StringType)) {
			return true;
		}
		return false;
	}

	/**
	 * The feature ID this property sheet will modify, as defined in the
	 * extension point.
	 */
	private String featureID = "";

	/**
	 * The feature ID this property sheet will modify, as defined in the
	 * extension point.
	 * 
	 * @return
	 */
	protected String getFeatureID() {
		return featureID;
	}

	/**
	 * Provides the EObject as an EModelElement.
	 * 
	 * @return
	 */
	protected EModelElement getEModelElement() {
		EObject eObject = getEObject();
		EModelElement eModelElement = (EModelElement) Platform
				.getAdapterManager().getAdapter(eObject, EModelElement.class);
		return eModelElement;
	}

	/**
	 * Provides the List of EObjects as EModelElements.
	 * 
	 * @return
	 */
	protected List<EModelElement> getEModelElementsList() {
		List<EObject> eObjects = getEObjectList();
		if (eObjects.size() <= 0) {
			return Collections.emptyList();
		}
		List<EModelElement> eModelElements = new ArrayList<EModelElement>();
		for (EObject eObject : eObjects) {
			EModelElement eModelElement = (EModelElement) Platform
					.getAdapterManager().getAdapter(eObject,
							EModelElement.class);
			if (eModelElement != null) {
				eModelElements.add(eModelElement);
			}
		}
		return eModelElements;
	}

	/**
	 * Provides the EObject wrapped as an ExtendedFeatureElement.
	 * 
	 * @return
	 */
	protected ExtendedFeatureElement getExtendedFeatureElement() {
		EModelElement element = getEModelElement();
		if (element == null) {
			return null;
		}
		return ExtendedFeatureElementFactory.getInstance()
				.asExtendedFeatureElement(element);
	}

	/**
	 * Retrieves the extended feature value as a String.
	 */
	@Override
	protected String getFeatureAsString() {
		ExtendedFeatureElement element = getExtendedFeatureElement();
		if (element == null) {
			return "";
		}
		String string = element.getString(getFeatureID());
		return string != null ? string : "";
	}

	@Override
	protected Object getNewFeatureValue(String newText) {
		return newText;
	}

	@Override
	protected Object getOldFeatureValue() {
		return getFeatureAsString();
	}

	@Override
	protected void verifyField(Event e) {
		// for subclasses to implement if any verification is required.
	}

	/**
	 * @see org.eclipse.emf.ecoretools.tabbedproperties.sections.AbstractTabbedPropertySection#hookListeners()
	 */
	@Override
	protected void hookListeners() {
		super.hookListeners();

		getText().addFocusListener(new FocusListener() {

			public void focusGained(FocusEvent e) {
				getText().setSelection(0, getText().getText().length());
			}

			public void focusLost(FocusEvent e) {
			}

		});

	}

	/**
	 * No use for EStructuralFeature in this property section.
	 */
	@Override
	protected EStructuralFeature getFeature() {
		// throw new UnsupportedOperationException(
		// "This property sections works with extended features via EAnnotations. Use getFeatureID instead"
		// );
		return null;
	}

	protected TransactionalEditingDomain getTransactionalEditingDomain() {
		EditingDomain domain = getEditingDomain();
		return (TransactionalEditingDomain) Platform.getAdapterManager()
				.getAdapter(domain, TransactionalEditingDomain.class);
	}

	@Override
	protected void createWidgets(Composite composite) {
		super.createWidgets(composite);
		// set the text widget to enabled
		getText().setEditable(true);
		getText().setEnabled(true);
		// add control decorators that show the description of features
		ControlDescriptionDecoratorProvider.addDescriptionDecoration(getText(),
				getFeatureID());
	}

	@Override
	protected void handleModelChanged(Notification msg) {
		Object notifier = msg.getNotifier();
		if (notifier.equals(getEObject())
				&& msg.getFeature() != null
				&& (msg.getFeature().equals(
						EcorePackage.eINSTANCE.getEAnnotation_Details()) || msg
						.getFeature().equals(
								EcorePackage.eINSTANCE
										.getEAnnotation_References()))) {
			refresh();
		}
	}

	/**
	 * This command must use the commands that work with extended features.
	 */
	@Override
	protected void createCommand(Object oldValue, Object newValue) {
		boolean equals = oldValue == null ? false : oldValue.equals(newValue);
		if (!equals) {
			TransactionalEditingDomain editingDomain = getTransactionalEditingDomain();
			if (getEModelElementsList().size() == 1) {
				// apply the property change to single selected object
				editingDomain.getCommandStack().execute(
						(new SetExtendedFeatureValueCommand(editingDomain,
								getEModelElement(), getFeatureID(), newValue))
								.toEMFCommand());
			} else {
				CompoundCommand compoundCommand = new CompoundCommand();
				// apply the property change to all selected elements
				for (EModelElement nextObject : getEModelElementsList()) {
					compoundCommand
							.append((new SetExtendedFeatureValueCommand(
									editingDomain, nextObject, getFeatureID(),
									newValue)).toEMFCommand());
				}
				editingDomain.getCommandStack().execute(compoundCommand);
			}
		}
	}
}
