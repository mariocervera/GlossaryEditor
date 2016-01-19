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
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecoretools.tabbedproperties.sections.AbstractTabbedPropertySection;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gmf.runtime.common.core.command.CompositeCommand;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import es.cv.gvcase.mdt.common.commands.SetExtendedFeatureValueCommand;
import es.cv.gvcase.mdt.common.commands.wrappers.GMFtoEMFCommandWrapper;
import es.cv.gvcase.mdt.common.model.ExtendedFeatureElement;
import es.cv.gvcase.mdt.common.model.ExtendedFeatureElementFactory;
import es.cv.gvcase.mdt.common.model.Feature;
import es.cv.gvcase.mdt.common.sections.descriptions.ControlDescriptionDecoratorProvider;
import es.cv.gvcase.mdt.common.util.MDTUtil;

/**
 * An abstract property section that can handle String, Double, Integer and
 * Boolean extended features values with predefined available values in the
 * extension point.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public abstract class AbstractExtendedChooserPropertySection extends
		AbstractTabbedPropertySection {

	/**
	 * A boolean that store if refreshing is happening and no model
	 * modifications should be performed
	 */
	private boolean isRefreshing = false;

	/**
	 * The combo box control for the section.
	 */
	private CCombo cCombo;

	/**
	 * The label for this section
	 */
	private CLabel labelCombo;

	/**
	 * Extended feature identifier.
	 */
	private String featureID = null;

	@Override
	protected EditingDomain getEditingDomain() {
		try {
			return super.getEditingDomain();
		} catch (IllegalArgumentException ex) {
			return null;
		}
	}
	
	/**
	 * Constructor with feature identifier provided.
	 */
	public AbstractExtendedChooserPropertySection(String featureID) {
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
	 * @see org.eclipse.ui.views.properties.tabbed.ISection#createControls(org.eclipse.swt.widgets.Composite,
	 *      org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage)
	 */
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
	}

	/**
	 * @see org.eclipse.emf.ecoretools.tabbedproperties.sections.AbstractTabbedPropertySection#createWidgets(org.eclipse.swt.widgets.Composite)
	 */
	protected void createWidgets(Composite composite) {
		labelCombo = getWidgetFactory().createCLabel(composite, getLabelText());
		cCombo = new CCombo(composite, SWT.BORDER);
		// add control decorators that show the description of features
		ControlDescriptionDecoratorProvider.addDescriptionDecoration(cCombo,
				getFeatureID());
	}

	/**
	 * @see org.eclipse.emf.ecoretools.tabbedproperties.sections.AbstractTabbedPropertySection#setSectionData(org.eclipse.swt.widgets.Composite)
	 */
	protected void setSectionData(Composite composite) {
		FormData data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(cCombo,
				-ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
		labelCombo.setLayoutData(data);

		data = new FormData();
		data.left = new FormAttachment(0, getStandardLabelWidth(composite,
				new String[] { getLabelText() }));
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(labelCombo, 0, SWT.CENTER);
		cCombo.setLayoutData(data);
	}

	/**
	 * Adds the listeners on the widgets
	 */
	protected void hookListeners() {
		cCombo.addSelectionListener(new SelectionAdapter() {
			/**
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			public void widgetSelected(SelectionEvent e) {
				handleComboModified();
			}
		});
	}

	/**
	 * Handle the combo modified event.
	 */
	protected void handleComboModified() {
		if (!isRefreshing
				&& !getFeatureValueAsString().equals(
						cCombo.getItem(cCombo.getSelectionIndex()))) {
			// transactional editing domain, extended feature and list of
			// selected EModelElements
			TransactionalEditingDomain editingDomain = getTransactionalEditingDomain();
			Feature feature = getExtendedFeature();
			List<EModelElement> selectedElements = getEModelElementsList();
			// in case any of those is null, we can do nothing
			if (editingDomain == null || feature == null
					|| selectedElements == null || selectedElements.size() <= 0) {
				return;
			}
			// the real value of the selected item in the combo
			Object value = feature.getOfAvailableValues(cCombo.getItem(cCombo
					.getSelectionIndex()));
			if (getEModelElementsList().size() == 1) {
				// if only one EModelElement was selected, we apply the change
				// to it alone
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
							editingDomain, selectedElements.get(0),
							getFeatureID(), value));
				}
				editingDomain.getCommandStack().execute(
						new GMFtoEMFCommandWrapper(compositeCommand));
			}
		}
	}

	/**
	 * @see org.eclipse.ui.views.properties.tabbed.ISection#refresh()
	 */
	public void refresh() {
		isRefreshing = true;
		String[] items = getFeatureValuesAsStrings();
		cCombo.setItems(items);
		for (int i = 0; i < items.length; i++) {
			if (items[i].equals(getFeatureValueAsString())) {
				cCombo.select(i);
			}
		}
		isRefreshing = false;
	}

	/**
	 * @return the isRefreshing
	 */
	protected boolean isRefreshing() {
		return isRefreshing;
	}

	/**
	 * Returns an array of all reachable objects of a given type from the
	 * current selection.
	 * 
	 * @param object
	 *            current EObject selection
	 * @param type
	 *            Reachable object which have this type
	 * @return An array of objects of the given type
	 */
	protected Object[] getChoices(EObject object, EClassifier type) {
		List<Object> choices = new ArrayList<Object>();
		choices.add(null);
		choices.addAll(ItemPropertyDescriptor.getReachableObjectsOfType(
				getEObject(), type));

		return choices.toArray();
	}

	/**
	 * Returns the label text for the given item
	 * 
	 * @param object
	 *            the item to find the name
	 * @return The found name of the given item
	 */
	protected String getItemLabelText(EObject object) {
		return object.toString();
	}

	/**
	 * Get the LabelProvider to use to display the Object
	 * 
	 * @return ILabelProvider
	 */
	protected ILabelProvider getLabelProvider() {
		return MDTUtil.getLabelProvider();
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

	/**
	 * Gets the extended feature's value as a String.
	 * 
	 * @return
	 */
	protected String getFeatureValueAsString() {
		return getFeatureValue() != null ? getFeatureValue().toString() : "";
	}

	/**
	 * Get the enumeration values of the feature for the combo field for the
	 * section.
	 * 
	 * @return the list of values of the feature as text.
	 */
	protected Object[] getComboFeatureValues() {
		return getFeatureValuesAsStrings();
	}

	/**
	 * Gets the extended feature's available values as a String.
	 * 
	 * @return
	 */
	protected String[] getFeatureValuesAsStrings() {
		Feature feature = getExtendedFeature();
		if (feature != null) {
			List<Object> featureValues = feature.getAvailableValues();
			String[] strings = new String[featureValues.size() + 1];
			strings[0] = "";
			for (int i = 0; i < featureValues.size(); i++) {
				strings[i + 1] = featureValues.get(i).toString();
			}
			return strings;
		}
		return new String[0];
	}
	
	public CCombo getCombo() {
		return cCombo;
	}

}
