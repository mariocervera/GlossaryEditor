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
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecoretools.tabbedproperties.sections.AbstractTabbedPropertySection;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.views.contentoutline.ContentOutline;

import es.cv.gvcase.mdt.common.composites.extended.ExtendedMultipleValuesComposite;
import es.cv.gvcase.mdt.common.model.ExtendedFeatureElement;
import es.cv.gvcase.mdt.common.model.ExtendedFeatureElementFactory;
import es.cv.gvcase.mdt.common.model.Feature;

/**
 * Allows the edition of a multivalued extended feature. <br>
 * Allows the addition or removal of String, Integer, Double, Boolean values.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public abstract class AbstractExtendedMultipleValuesPropertySection extends
		AbstractTabbedPropertySection {

	private String featureID = null;

	@Override
	protected EditingDomain getEditingDomain() {
		try {
			return super.getEditingDomain();
		} catch (IllegalArgumentException ex) {
			return null;
		}
	}
	
	public AbstractExtendedMultipleValuesPropertySection(String featureID) {
		this.featureID = featureID;
	}

	protected EModelElement getEModelElement() {
		return (EModelElement) Platform.getAdapterManager().getAdapter(
				getEObject(), EModelElement.class);
	}

	protected List<EModelElement> getEModelElementsList() {
		List<EObject> eObjects = getEObjectList();
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

	protected TransactionalEditingDomain getTransctionalEditingDomain() {
		if (getEditingDomain() != null) {
			return (TransactionalEditingDomain) Platform.getAdapterManager()
					.getAdapter(getEditingDomain(),
							TransactionalEditingDomain.class);
		} else {
			return null;
		}
	}

	protected String getFeatureID() {
		return featureID;
	}

	protected Feature getExtendedFeature() {
		return ExtendedFeatureElementFactory.getInstance()
				.getMapFeatureIDToFeature().get(getFeatureID());
	}

	protected Object getFeatureValue() {
		ExtendedFeatureElement element = ExtendedFeatureElementFactory
				.getInstance().asExtendedFeatureElement(getEModelElement());
		if (element != null) {
			return element.getValue(getFeatureID());
		}
		return null;
	}

	/**
	 * NOT used when handling extended features.
	 */
	@Override
	protected EStructuralFeature getFeature() {
		return null;
	}

	@Override
	protected abstract String getLabelText();

	//

	private ExtendedMultipleValuesComposite multiValueComposite = null;

	private Group groupMembers = null;

	@Override
	protected void createWidgets(Composite composite) {
		groupMembers = getWidgetFactory()
				.createGroup(composite, getLabelText());
		GridLayout gl = new GridLayout(1, false);
		groupMembers.setLayout(gl);

		multiValueComposite = new ExtendedMultipleValuesComposite(groupMembers,
				composite.getStyle(), getWidgetFactory(), getFeatureID(),
				getLabelText());
		multiValueComposite.createWidgets(groupMembers, getWidgetFactory());

		getWidgetFactory().adapt(groupMembers);
	}

	@Override
	public void refresh() {
		super.refresh();
		if (multiValueComposite != null
				&& multiValueComposite.getMemberElementsViewer() != null) {
			multiValueComposite.setEModelElement(getEModelElement());
			multiValueComposite.getMemberElementsViewer().setInput(
					getFeatureValue());
		}
		if (!(getPart() instanceof ContentOutline) &&
				getTransctionalEditingDomain() != null) {
			multiValueComposite
					.setEMFEditDomain(getTransctionalEditingDomain());
		}
	}

	@Override
	protected void setSectionData(Composite composite) {

		FormData fdata = new FormData();
		fdata.top = new FormAttachment(0, 0);
		fdata.left = new FormAttachment(0, 0);
		fdata.right = new FormAttachment(100, 0);
		fdata.bottom = new FormAttachment(100, 0);
		groupMembers.setLayoutData(fdata);

		GridData gdata = new GridData(GridData.FILL_BOTH);
		multiValueComposite.setLayoutData(gdata);
		multiValueComposite.setSectionData(composite);
	}

}
