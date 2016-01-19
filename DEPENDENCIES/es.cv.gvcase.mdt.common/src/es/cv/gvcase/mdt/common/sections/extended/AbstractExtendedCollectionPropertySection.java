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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.provider.EcoreItemProviderAdapterFactory;
import org.eclipse.emf.ecoretools.tabbedproperties.providers.TabbedPropertiesLabelProvider;
import org.eclipse.emf.ecoretools.tabbedproperties.sections.AbstractTabbedPropertySection;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import es.cv.gvcase.mdt.common.composites.extended.ExtendedTableMembersComposite;
import es.cv.gvcase.mdt.common.sections.descriptions.ControlDescriptionDecoratorProvider;

/**
 * A Property Section to show the members of an EModelElement with an extended
 * feature.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 */
public abstract class AbstractExtendedCollectionPropertySection extends
		AbstractTabbedPropertySection {

	/** Widgets. */

	/** The Group hosting the composite */
	private Group groupMembers;

	/** The composite. */
	private ExtendedTableMembersComposite membersComposite;

	@Override
	protected EditingDomain getEditingDomain() {
		try {
			return super.getEditingDomain();
		} catch (IllegalArgumentException ex) {
			return null;
		}
	}
	
	/**
	 * Sets the members composite.
	 * 
	 * @param membersComposite
	 *            the new members composite
	 */
	public void setMembersComposite(
			ExtendedTableMembersComposite membersComposite) {
		this.membersComposite = membersComposite;
	}

	/**
	 * Gets the members composite.
	 * 
	 * @return the members composite
	 */
	public ExtendedTableMembersComposite getMembersComposite() {
		return membersComposite;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.emf.ecoretools.tabbedproperties.sections.
	 * AbstractTabbedPropertySection
	 * #createWidgets(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createWidgets(Composite composite) {

		groupMembers = getWidgetFactory()
				.createGroup(composite, getLabelText());
		GridLayout gl = new GridLayout(1, false);
		groupMembers.setLayout(gl);

		// add control decorators that show the description of features
		ControlDescriptionDecoratorProvider.addDescriptionDecoration(groupMembers,
				getExtendedFeatureID());
		
		membersComposite = new ExtendedTableMembersComposite(groupMembers,
				composite.getStyle(), getWidgetFactory(),
				getExtendedFeatureID(), getMembersText(), getEType());
		getMembersComposite().setEnableOrdering(enableOrdering());
		getMembersComposite().setLabelProvider(getLabelProvider());
		getMembersComposite().createWidgets(groupMembers, getWidgetFactory());

		if (getCandidateElementsViewerDoubleClickListener() != null) {
			getMembersComposite()
					.setCandidateElementsViewerDoubleClickListener(
							getCandidateElementsViewerDoubleClickListener());
		}

		if (getSelectedElementsViewerDoubleClickListener() != null) {
			getMembersComposite().setSelectedElementsViewerDoubleClickListener(
					getSelectedElementsViewerDoubleClickListener());
		}

		if (getAddButtonSelectionListener() != null) {
			getMembersComposite().setAddSelectionListener(
					getAddButtonSelectionListener());
		}

		if (getRemoveButtonSelectionListener() != null) {
			getMembersComposite().setRemoveSelectionListener(
					getRemoveButtonSelectionListener());
		}

		if (getUpButtonSelectionListener() != null) {
			getMembersComposite().setUpSelectionListener(
					getUpButtonSelectionListener());
		}

		if (getDownButtonSelectionListener() != null) {
			getMembersComposite().setDownSelectionListener(
					getDownButtonSelectionListener());
		}

		getWidgetFactory().adapt(getMembersComposite());
	}

	protected SelectionListener getAddButtonSelectionListener() {
		return null;
	}

	protected SelectionListener getRemoveButtonSelectionListener() {
		return null;
	}

	protected SelectionListener getUpButtonSelectionListener() {
		return null;
	}

	protected SelectionListener getDownButtonSelectionListener() {
		return null;
	}

	protected IDoubleClickListener getSelectedElementsViewerDoubleClickListener() {
		return null;
	}

	protected IDoubleClickListener getCandidateElementsViewerDoubleClickListener() {
		return null;
	}

	protected Collection<?> getCandidateElements() {
		return null;
	}

	protected abstract EClassifier getEType();

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.emf.ecoretools.tabbedproperties.sections.
	 * AbstractTabbedPropertySection
	 * #setSectionData(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void setSectionData(Composite composite) {

		FormData fdata = new FormData();
		fdata.top = new FormAttachment(0, 0);
		fdata.left = new FormAttachment(0, 0);
		fdata.right = new FormAttachment(100, 0);
		fdata.bottom = new FormAttachment(100, 0);
		groupMembers.setLayoutData(fdata);

		GridData gdata = new GridData(GridData.FILL_BOTH);
		getMembersComposite().setLayoutData(gdata);
		getMembersComposite().setSectionData(composite);
	}

	/**
	 * Hook listeners.
	 * 
	 * @see org.eclipse.emfecoretools..tabbedproperties.sections.
	 *      AbstractTabbedPropertySection#hookListeners()
	 */
	@Override
	protected void hookListeners() {
		getMembersComposite().hookListeners();

	}

	/**
	 * Gets the label provider.
	 * 
	 * @return the label provider
	 * 
	 * @see org.eclipse.emfecoretools..tabbedproperties.sections.
	 *      AbstractListPropertySection#getLabelProvider()
	 */
	protected IBaseLabelProvider getLabelProvider() {

		return new TabbedPropertiesLabelProvider(
				new EcoreItemProviderAdapterFactory());
	}

	protected TransactionalEditingDomain getTransactionalEditingDomain() {
		EditingDomain domain = getEditingDomain();
		TransactionalEditingDomain transactionalDomain = (TransactionalEditingDomain) Platform
				.getAdapterManager().getAdapter(domain,
						TransactionalEditingDomain.class);
		return transactionalDomain;
	}

	/**
	 * Refresh.
	 * 
	 * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#refresh()
	 */
	@Override
	public void refresh() {

		if (getMembersComposite() != null) {
			if (getCandidateElements() != null) {
				membersComposite.getCandidateElementsViewer().setInput(
						getCandidateElements());
			}

			getMembersComposite().setEModelElement(getEModelElement());

			if (getEditingDomain() != null) {
				getMembersComposite().setEMFEditDomain(
						getTransactionalEditingDomain());
			}
			getMembersComposite().refresh();
		}
	}

	/**
	 * Gets the feature as list.
	 * 
	 * @return the feature as list
	 */
	protected EList<EObject> getFeatureAsList() {

		EObject eobject = getEObject();
		if (eobject == null)
			return null;

		Object featureValue = eobject.eGet(getFeature());

		if (featureValue instanceof EList) {
			return (EList<EObject>) featureValue;
		}

		return null;
	}

	/**
	 * Enable ordering.
	 * 
	 * @return whether the buttons Up and Down must be shown or not
	 */
	protected boolean enableOrdering() {
		return false;
	}

	/**
	 * Gets the members text.
	 * 
	 * @return the members text
	 */
	protected String getMembersText() {
		return "";
	}

	protected abstract String getExtendedFeatureID();

	/**
	 * NOT Used - implementd to comply with interface.
	 */
	@Override
	protected EStructuralFeature getFeature() {
		// fjcano :: no feature is used.
		return null;
	}

}
