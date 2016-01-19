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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecoretools.tabbedproperties.sections.AbstractTabbedPropertySection;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import es.cv.gvcase.mdt.common.composites.DetailComposite;
import es.cv.gvcase.mdt.common.composites.EObjectsEditableTableComposite;
import es.cv.gvcase.mdt.common.sections.descriptions.ControlDescriptionDecoratorProvider;
import es.cv.gvcase.mdt.common.util.MDTUtil;

/**
 * @author Marc Gil Sendra
 */
public abstract class AbstractDetailedContainedEditableObjectsPropertySection
		extends AbstractTabbedPropertySection {

	/** The widgets. */
	private EObjectsEditableTableComposite table;
	private Group groupDetails;

	/** The tabbed property sheet page. */
	TabbedPropertySheetPage tabbedPropertySheetPage;

	@Override
	protected EditingDomain getEditingDomain() {
		try {
			return super.getEditingDomain();
		} catch (IllegalArgumentException ex) {
			return null;
		}
	}
	
	/**
	 * Create the entry section: widgets, listeners...
	 */
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage tabbedPropertySheetPage) {

		super.createControls(parent, tabbedPropertySheetPage);

		this.tabbedPropertySheetPage = tabbedPropertySheetPage;

		if (groupDetails == null)
			return;

		for (Control c : groupDetails.getChildren()) {
			if (c instanceof DetailComposite) {
				((DetailComposite) c).setSheetPage(tabbedPropertySheetPage);
			}
		}
	}

	/**
	 * Creates the widgets: creates a table and the details composites
	 */
	@Override
	protected void createWidgets(Composite composite) {

		table = new EObjectsEditableTableComposite(composite, SWT.BORDER,
				getWidgetFactory(), this, getFeature(), null) {

			@Override
			protected List<EStructuralFeature> getListOfFeatures() {
				List<EStructuralFeature> list = new ArrayList<EStructuralFeature>();

				return list;
			}

			@Override
			protected String[] getColumnNames() {
				return new String[] { "" };
			}

			@Override
			protected int[] getColumnSizes() {
				return new int[] { 150 };
			}

			@Override
			protected CellEditor[] getCellEditors(Table table) {
				CellEditor[] editors = new CellEditor[getListOfFeatures()
						.size()];

				return editors;
			}

			@Override
			protected ICellModifier getCellModifier() {
				return null;
			}
		};
		
		// add control decorators that show the description of features
		ControlDescriptionDecoratorProvider.addDescriptionDecoration(table,
				getFeature());

		groupDetails = createNewGroup(composite);
		fillGroupDetails(groupDetails);
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
		data.bottom = new FormAttachment(23, 0);
		data.height = 60;
		table.setLayoutData(data);

		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(table, ITabbedPropertyConstants.VSPACE);
		// data.bottom = new FormAttachment(100, 0);
		groupDetails.setLayoutData(data);

	}

	/**
	 * Refresh all the section
	 */
	@Override
	public void refresh() {
		refreshTable();
		refreshDetails();
	}

	/**
	 * Refresh the table
	 */
	public void refreshTable() {
		try {
			if (table != null) {
				table.setEditingDomain(getEditingDomain());
				table.setEObject(getEObject());
			}
		} catch (IllegalArgumentException e) {
		}
	}

	/**
	 * Refresh the details composite
	 */
	public void refreshDetails() {
		if (groupDetails != null) {
			EObject selected = table.getFirstSelectedEObject();

			if (selected == null) {
				groupDetails.setVisible(false);
			} else {
				groupDetails.setVisible(true);

				for (Control c : groupDetails.getChildren()) {
					if (c instanceof DetailComposite) {
						((DetailComposite) c)
								.setEMFEditDomain(getEditingDomain());
						((DetailComposite) c).setElement(getTable()
								.getFirstSelectedEObject());
					}
				}

				groupDetails.redraw();
				groupDetails.update();

				for (Control c : groupDetails.getChildren()) {
					if (c instanceof DetailComposite) {
						((DetailComposite) c).loadData();
					}
				}

				groupDetails.getParent().layout();
				groupDetails.layout();
			}
		}
	}

	/**
	 * Creates a new group for the details
	 */
	public Group createNewGroup(Composite parent) {
		Group g = new Group(parent, parent.getStyle());
		g.setBackground(parent.getBackground());
		g.setText(getLabelText());

		GridLayout gl = new GridLayout();
		g.setLayout(gl);

		return g;
	}

	/**
	 * Gets the table.
	 */
	public EObjectsEditableTableComposite getTable() {
		return table;
	}

	/**
	 * Sets the table.
	 */
	public void setTable(EObjectsEditableTableComposite table) {
		this.table = table;
	}

	/**
	 * Sets the group details.
	 */
	public void setGroupDetails(Group group) {
		groupDetails = group;
	}

	/**
	 * Gets the group details.
	 */
	public Group getGroupDetails() {
		return groupDetails;
	}

	/**
	 * Gets the generic label provider for the Details
	 */
	protected IBaseLabelProvider getLabelProvider() {
		return MDTUtil.getLabelProvider();
	}

	/**
	 * Fills the Details group with the widgets given by the user
	 */
	protected abstract void fillGroupDetails(Group g);

	/**
	 * Gets the table elements class.
	 */
	protected abstract EClass getTableElementsClass();

}
