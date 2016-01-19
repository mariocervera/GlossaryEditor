/*******************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Mario Cervera Ubeda (Prodevelop) - Initial implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.sections.extended;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gmf.runtime.common.core.command.CompositeCommand;

import es.cv.gvcase.emf.ui.common.composites.EObjectChooserComposite;
import es.cv.gvcase.mdt.common.commands.SetExtendedFeatureValueCommand;
import es.cv.gvcase.mdt.common.commands.wrappers.GMFtoEMFCommandWrapper;
import es.cv.gvcase.mdt.common.model.ExtendedFeatureElement;
import es.cv.gvcase.mdt.common.model.ExtendedFeatureElementFactory;
import es.cv.gvcase.mdt.common.model.Feature;
import es.cv.gvcase.mdt.common.sections.AbstractChooserPropertySection;

public abstract class AbstractExtendedReferencePropertySection extends
		AbstractChooserPropertySection {

	/**
	 * Extended feature identifier.
	 */
	private String featureID = null;

	/**
	 * Constructor with feature identifier provided.
	 */
	public AbstractExtendedReferencePropertySection(String featureID) {
		this(featureID, true);
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
	 * Constructor with feature identifier provided and single selection
	 * indicator.
	 */
	public AbstractExtendedReferencePropertySection(String featureID,
			boolean singleSelection) {
		super(singleSelection);
		this.featureID = featureID;
	}

	/**
	 * Get the extended feature identifier.
	 * 
	 * @return
	 */
	protected String getFeatureID() {
		return featureID;
	}

	/**
	 * Gets the extended feature.
	 * 
	 * @return
	 */
	protected Feature getExtendedFeature() {
		return ExtendedFeatureElementFactory.getInstance()
				.getMapFeatureIDToFeature().get(getFeatureID());
	}

	/**
	 * Must not be used with extended features.
	 */
	@Override
	protected EStructuralFeature getFeature() {
		return null;
	}

	/**
	 * Gets the selected EObject as an EModelElement.
	 * 
	 * @return
	 */
	protected EModelElement getEModelElement() {
		return (EModelElement) Platform.getAdapterManager().getAdapter(
				getEObject(), EModelElement.class);
	}

	/**
	 * Gets the selected EObjects as EModelElements.
	 * 
	 * @return
	 */
	protected List<EModelElement> getEModelElementsList() {
		List<EObject> eObjects = getEObjectList();
		List<EModelElement> eModelElements = new ArrayList<EModelElement>();
		for (EObject eObject : eObjects) {
			EModelElement element = (EModelElement) Platform
					.getAdapterManager().getAdapter(eObject,
							EModelElement.class);
			if (element != null) {
				eModelElements.add(element);
			}
		}
		return eModelElements;
	}

	protected TransactionalEditingDomain getTransactionalEditingDomain() {
		return (TransactionalEditingDomain) Platform.getAdapterManager()
				.getAdapter(getEditingDomain(),
						TransactionalEditingDomain.class);
	}

	/**
	 * Handle the combo modified event.
	 */
	protected void handleComboModified() {
		if (!isRefreshing()
				&& getFeatureValue() != getCSingleObjectChooser()
						.getSelection()) {

			TransactionalEditingDomain editingDomain = getTransactionalEditingDomain();
			Feature feature = getExtendedFeature();
			List<EModelElement> selectedElements = getEModelElementsList();
			// in case any of those is null, we can do nothing
			if (editingDomain == null || feature == null
					|| selectedElements == null || selectedElements.size() <= 0) {
				return;
			}

			if (selectedElements.size() == 1) {
				// if only one EModelElement was selected, we apply the change
				// to it alone
				Object value = getCSingleObjectChooser().getSelection();
				if (value instanceof EObjectChooserComposite.NullObject) {
					value = null;
				}
				SetExtendedFeatureValueCommand command = new SetExtendedFeatureValueCommand(
						editingDomain, selectedElements.get(0), getFeatureID(),
						value);
				/* apply the property change to single selected object */
				editingDomain.getCommandStack().execute(command.toEMFCommand());
			} else {
				// if more than one was selected, we apply the change to all of
				// them
				CompositeCommand compositeCommand = new CompositeCommand(
						"Set many objects property.");
				/* apply the property change to all selected elements */
				for (EModelElement element : selectedElements) {
					compositeCommand.add(new SetExtendedFeatureValueCommand(
							editingDomain, element, getFeatureID(),
							getCSingleObjectChooser().getSelection()));
				}
				editingDomain.getCommandStack().execute(
						new GMFtoEMFCommandWrapper(compositeCommand));
			}
		}
	}

	/**
	 * Get the current feature value of the selected model object.
	 * 
	 * @return the feature value to select in the ccombo.
	 */
	protected Object getFeatureValue() {
		ExtendedFeatureElement element = ExtendedFeatureElementFactory
				.getInstance().asExtendedFeatureElement(getEModelElement());
		if (element != null) {
			return element.getValue(getFeatureID());
		} else {
			return null;
		}
	}

}
