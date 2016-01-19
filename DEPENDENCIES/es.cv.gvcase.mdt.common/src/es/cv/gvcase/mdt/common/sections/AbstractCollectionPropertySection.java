/***************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte,
 * Generalitat de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Mario Cervera Ubeda (Integranova) - initial API and implementation
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.sections;

import java.util.Collection;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.provider.EcoreItemProviderAdapterFactory;
import org.eclipse.emf.ecoretools.tabbedproperties.providers.TabbedPropertiesLabelProvider;
import org.eclipse.emf.ecoretools.tabbedproperties.sections.AbstractTabbedPropertySection;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import es.cv.gvcase.mdt.common.composites.MembersComposite;
import es.cv.gvcase.mdt.common.composites.TableMembersComposite;
import es.cv.gvcase.mdt.common.sections.descriptions.ControlDescriptionDecoratorProvider;

// TODO: Auto-generated Javadoc
/**
 * An abstract implementation of a section prepared to deal with collections.
 * 
 * Creation 3 apr. 2008
 * 
 * @author Mario Cervera Ubeda (Integranova)
 */
public abstract class AbstractCollectionPropertySection extends
		AbstractTabbedPropertySection {

	/** Widgets. */

	/** The Group hosting the composite */
	protected Group groupMembers;

	/** The composite. */
	protected MembersComposite membersComposite;

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
	public void setMembersComposite(MembersComposite membersComposite) {
		this.membersComposite = membersComposite;
	}

	/**
	 * Gets the members composite.
	 * 
	 * @return the members composite
	 */
	public MembersComposite getMembersComposite() {
		return membersComposite;
	}
	
	public Group getGroupMembers() {
		return groupMembers;
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

		if (groupMembers == null) {
			groupMembers = getWidgetFactory().createGroup(composite,
					getLabelText());
			GridLayout gl = new GridLayout(1, false);
			groupMembers.setLayout(gl);
		}

		if (membersComposite == null) {
			membersComposite = createMemberComposite();
		}
		
		// add control decorators that show the description of features
		ControlDescriptionDecoratorProvider.addDescriptionDecoration(membersComposite,
				getFeature());

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

	protected MembersComposite createMemberComposite() {
		MembersComposite member = new TableMembersComposite(groupMembers,
				groupMembers.getStyle(), getWidgetFactory(), getFeature(),
				getMembersText());
		member.setEnableOrdering(enableOrdering());
		member.setLabelProvider(getLabelProvider());
		member.createWidgets(groupMembers, getWidgetFactory());

		return member;
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

	/**
	 * Refresh.
	 * 
	 * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#refresh()
	 */
	@Override
	public void refresh() {

		if (getMembersComposite() != null) {
			if (getCandidateElements() != null) {
				membersComposite.setCandidateElements(getCandidateElements());
			}

			getMembersComposite().setElement(getEObject());
			try {
				if (getEditingDomain() != null) {
					getMembersComposite().setEMFEditDomain(getEditingDomain());
				}
				getMembersComposite().refresh();
			} catch (IllegalArgumentException e) {
			}
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

}
