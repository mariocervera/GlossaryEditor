/***************************************************************************
 * Copyright (c) 2007 Conselleria de Infraestructuras y Transporte,
 * Generalitat de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Marc Gil Sendra (Prodevelop) - initial API and implementation
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.sections;

import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecoretools.tabbedproperties.sections.AbstractTabbedPropertySection;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import es.cv.gvcase.mdt.common.composites.ExtendedEObjectsEditableTableComposite;
import es.cv.gvcase.mdt.common.sections.descriptions.ControlDescriptionDecoratorProvider;
import es.cv.gvcase.mdt.common.util.MDTUtil;

/**
 * @author Marc Gil Sendra
 */
public abstract class AbstractContainedEditableExtendedObjectsPropertySection
		extends AbstractTabbedPropertySection {

	/** The widgets. */
	private ExtendedEObjectsEditableTableComposite table;

	/** The tabbed property sheet page. */
	protected TabbedPropertySheetPage tabbedPropertySheetPage;

	/** The feature ID */
	protected String featureID;

	@Override
	protected EditingDomain getEditingDomain() {
		try {
			return super.getEditingDomain();
		} catch (IllegalArgumentException ex) {
			return null;
		}
	}
	
	public AbstractContainedEditableExtendedObjectsPropertySection(
			String featureID) {
		this.featureID = featureID;
	}

	/**
	 * Create the entry section: widgets, listeners...
	 */
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage tabbedPropertySheetPage) {

		super.createControls(parent, tabbedPropertySheetPage);
		this.tabbedPropertySheetPage = tabbedPropertySheetPage;
	}

	/**
	 * Creates the widgets: creates a table and the details composites
	 */
	@Override
	protected void createWidgets(Composite composite) {
		table = new ExtendedEObjectsEditableTableComposite(composite, SWT.NONE,
				getWidgetFactory(), this, featureID) {
			@Override
			protected String getColumnName() {
				return getLabelText();
			}
		};
		
		// add control decorators that show the description of features
		ControlDescriptionDecoratorProvider.addDescriptionDecoration(table,
				getFeature());
	}

	/**
	 * Sets the section data.
	 */
	@Override
	protected void setSectionData(Composite composite) {
		FormData data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, 0);
		data.height = 200;
		getTable().setLayoutData(data);
	}

	/**
	 * Refresh all the section
	 */
	@Override
	public void refresh() {
		refreshTable();
	}

	/**
	 * Refresh the table
	 */
	public void refreshTable() {
		try {
			if (table != null) {
				table
						.setEditingDomain((TransactionalEditingDomain) getEditingDomain());
				table.setEObject((EModelElement) getEObject());
			}
		} catch (IllegalArgumentException e) {
		}
	}

	/**
	 * Gets the table.
	 */
	public ExtendedEObjectsEditableTableComposite getTable() {
		return table;
	}

	/**
	 * Gets the generic label provider for the Details
	 */
	protected IBaseLabelProvider getLabelProvider() {
		return MDTUtil.getLabelProvider();
	}

	@Override
	protected EStructuralFeature getFeature() {
		return EcorePackage.eINSTANCE.getEModelElement_EAnnotations();
	}

	protected String getFeatureID() {
		return featureID;
	}
}
