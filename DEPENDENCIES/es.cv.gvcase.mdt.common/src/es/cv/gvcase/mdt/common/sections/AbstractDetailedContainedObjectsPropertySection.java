/***************************************************************************
 * Copyright (c) 2007 Conselleria de Infraestructuras y Transporte,
 * Generalitat de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Mario Cervera Ubeda (Integranova) - initial API and implementation
 *
 ******************************************************************************/

package es.cv.gvcase.mdt.common.sections;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.provider.EcoreItemProviderAdapterFactory;
import org.eclipse.emf.ecoretools.tabbedproperties.providers.TabbedPropertiesLabelProvider;
import org.eclipse.emf.ecoretools.tabbedproperties.sections.AbstractTabbedPropertySection;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabContents;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import es.cv.gvcase.mdt.common.composites.DetailComposite;
import es.cv.gvcase.mdt.common.composites.EObjectsTableComposite;
import es.cv.gvcase.mdt.common.sections.descriptions.ControlDescriptionDecoratorProvider;

// TODO: Auto-generated Javadoc
/**
 * An abstract section used to create model objects inside the selected element.
 * You can then, depending on the selected model object edit its properties.
 * 
 * Creation 23 apr. 08
 * 
 * @author Mario Cervera Ubeda
 */

public abstract class AbstractDetailedContainedObjectsPropertySection extends
		AbstractTabbedPropertySection {

	/** The selected object. */
	private EObject selectedObject;

	/** The widgets. */
	private EObjectsTableComposite table;

	/** The group details. */
	private Group groupDetails;

	/** The composites. */
	EList<Composite> composites = new BasicEList<Composite>();

	/** The tabbed property sheet page. */
	TabbedPropertySheetPage tabbedPropertySheetPage;

	/** The parent of the section */
	protected Composite parent;

	@Override
	protected EditingDomain getEditingDomain() {
		try {
			return super.getEditingDomain();
		} catch (IllegalArgumentException ex) {
			return null;
		}
	}
	
	/**
	 * Gets the table.
	 * 
	 * @return the table
	 */
	public EObjectsTableComposite getTable() {
		return table;
	}

	/**
	 * Sets the table.
	 * 
	 * @param table
	 *            the new table
	 */
	public void setTable(EObjectsTableComposite table) {
		this.table = table;
	}

	/**
	 * Sets the group details.
	 * 
	 * @param group
	 *            the new group details
	 */
	public void setGroupDetails(Group group) {
		groupDetails = group;
	}

	/**
	 * Gets the group details.
	 * 
	 * @return the group details
	 */
	public Group getGroupDetails() {
		return groupDetails;
	}

	/**
	 * Gets the composites.
	 * 
	 * @return the composites
	 */
	public EList<Composite> getComposites() {
		return composites;
	}

	public EObject getSelectedEObject() {
		return this.getTableSelection();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.emf.ecoretools.tabbedproperties.sections.
	 * AbstractTabbedPropertySection
	 * #createControls(org.eclipse.swt.widgets.Composite,
	 * org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage)
	 */
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage tabbedPropertySheetPage) {

		super.createControls(parent, tabbedPropertySheetPage);

		this.tabbedPropertySheetPage = tabbedPropertySheetPage;
		for (Composite c : composites) {
			if (c instanceof DetailComposite) {
				((DetailComposite) c).setSheetPage(tabbedPropertySheetPage);
			}
		}
	}

	/**
	 * Creates the widgets.
	 * 
	 * @param composite
	 *            the composite
	 * 
	 * @see org.topcased.tabbedproperties.sections.AbstractTabbedPropertySection#createWidgets(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createWidgets(Composite composite) {

		this.parent = composite;

		table = new EObjectsTableComposite(composite, SWT.NONE,
				getWidgetFactory(), this, getFeature(), getTableName()) {
			@Override
			public void updateSelectedObject(EObject newObject) {
				for (Composite c : composites) {
					if (c instanceof DetailComposite) {
						((DetailComposite) c).setElement(newObject);
					}
				}
				refresh();
			}
		};
		table.setFeatureClass(getTableElementsClass());
		table.setLabelProvider(getLabelProvider());

		// add control decorators that show the description of features
		ControlDescriptionDecoratorProvider.addDescriptionDecoration(table,
				getFeature());

		groupDetails = getWidgetFactory()
				.createGroup(composite, getLabelText());
		GridLayout gl = new GridLayout();
		groupDetails.setLayout(gl);

	}

	/**
	 * Sets the section data.
	 * 
	 * @param composite
	 *            the composite
	 * 
	 * @see org.topcased.tabbedproperties.sections.AbstractTabbedPropertySection#setSectionData(org.eclipse.swt.widgets.Composite)
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
	 * Refresh.
	 * 
	 * @see org.eclipse.ui.views.properties.tabbed.ISection#refresh()
	 */
	@Override
	public void refresh() {

		if (table != null && groupDetails != null && !table.isDisposed()
				&& !groupDetails.isDisposed()) {
			super.refresh();

			TableItem selected = table.getFirstSelected();

			if (getTableSelection() == null || selectedObject == null
					|| getTableSelection() != selectedObject) {
				disposeComposites();
				createDetailComposites(groupDetails);
			}

			table.setEditDomain(this.getEditingDomain());
			table.setEObject(getEObject());

			if (selected != null) {
				groupDetails.setVisible(true);
				for (Composite c : composites) {
					if (c instanceof DetailComposite) {
						((DetailComposite) c).setEMFEditDomain(this
								.getEditingDomain());
						((DetailComposite) c).setElement(null);
					}
				}

			} else {
				groupDetails.setVisible(false);
			}

			groupDetails.redraw();
			groupDetails.update();

			table.refreshSelectedEObject(selected);

			for (Composite c : composites) {
				if (c instanceof DetailComposite) {
					((DetailComposite) c).loadData();
				}
			}

			groupDetails.getParent().layout();
			groupDetails.layout();

			resizeScrolledComposite();

			selectedObject = getTableSelection();
		}
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
	 * Creates the detail composites.
	 * 
	 * @param composite
	 *            the composite
	 */
	private void createDetailComposites(Composite composite) {

		composites.clear();
		composites.addAll(createComposites(groupDetails));
		for (Composite c : composites) {
			if (c instanceof DetailComposite) {
				DetailComposite dc = (DetailComposite) c;
				getWidgetFactory().adapt(dc);
				dc.createWidgets(groupDetails, getWidgetFactory());
				GridData gdata = new GridData(GridData.FILL_BOTH);
				dc.setLayoutData(gdata);
				dc.setSectionData(composite);
				dc.hookListeners();
				((DetailComposite) c).setSheetPage(tabbedPropertySheetPage);
			}
		}

	}

	/**
	 * Dispose composites.
	 */
	private void disposeComposites() {
		for (Composite c : composites) {
			if (c instanceof DetailComposite) {
				DetailComposite detailsComposite = (DetailComposite) c;
				if (!detailsComposite.isDisposed()) {
					detailsComposite.dispose();
				}
			}
		}
	}

	/**
	 * Gets the table selection.
	 * 
	 * @return the table selection
	 */
	private EObject getTableSelection() {

		TableItem[] items = this.table.getEObjectsTableViewer().getTable()
				.getSelection();
		if (items.length > 0) {
			if (items[0].getData() instanceof EObject) {
				return (EObject) items[0].getData();
			}
		}

		return null;
	}

	protected void resizeScrolledComposite() {

		Point currentTabSize = new Point(0, 0);
		TabContents currentTab = this.tabbedPropertySheetPage.getCurrentTab();

		if (currentTab != null && parent != null && parent.getParent() != null
				&& parent.getParent().getParent() != null) {

			Composite sizeReference = parent.getParent().getParent();
			if (sizeReference != null) {
				currentTabSize = sizeReference.computeSize(SWT.DEFAULT,
						SWT.DEFAULT);
			}
		}

		// Get the ScrolledComposite and resize it

		if (this.tabbedPropertySheetPage != null
				&& this.tabbedPropertySheetPage.getControl() instanceof Composite) {

			if (((Composite) this.tabbedPropertySheetPage.getControl())
					.getChildren().length == 1) {

				Control layoutComposite = ((Composite) this.tabbedPropertySheetPage
						.getControl()).getChildren()[0];

				for (Control con : ((Composite) layoutComposite).getChildren()) {
					if (con instanceof ScrolledComposite) {
						((ScrolledComposite) con).setMinSize(currentTabSize.x,
								currentTabSize.y);
					}
				}
			}
		}
	}

	/**
	 * Gets the table elements class.
	 * 
	 * @return the table elements class
	 */
	protected abstract EClass getTableElementsClass();

	/**
	 * Creates the detail composites.
	 * 
	 * @param parent
	 *            the parent
	 * 
	 * @return the e list< detail composite>
	 */
	protected abstract EList<DetailComposite> createComposites(Composite parent);

	/**
	 * Gets the table name.
	 * 
	 * @return the table name
	 */
	protected abstract String getTableName();

}
