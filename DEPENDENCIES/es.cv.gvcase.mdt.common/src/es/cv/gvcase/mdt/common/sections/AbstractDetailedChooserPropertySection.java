/*******************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana. All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Mario Cervera Ubeda (Prodevelop) - initial API and implementation
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.sections;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecoretools.tabbedproperties.sections.AbstractChooserPropertySection;
import org.eclipse.emf.edit.domain.EditingDomain;
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
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabContents;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import es.cv.gvcase.mdt.common.composites.DetailComposite;

public abstract class AbstractDetailedChooserPropertySection extends
		AbstractChooserPropertySection {

	private EObject selectedObject;

	private Group groupDetails;

	private DetailComposite detailsComposite;

	private Composite parent;

	private TabbedPropertySheetPage tabbedPropertySheetPage;

	@Override
	protected EditingDomain getEditingDomain() {
		try {
			return super.getEditingDomain();
		} catch (IllegalArgumentException ex) {
			return null;
		}
	}
	
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage tabbedPropertySheetPage) {

		super.createControls(parent, tabbedPropertySheetPage);

		this.tabbedPropertySheetPage = tabbedPropertySheetPage;
	}

	@Override
	protected void createWidgets(Composite composite) {
		super.createWidgets(composite);
		this.parent = composite;
	}

	protected Group getGroupDetails() {
		return groupDetails;
	}

	protected DetailComposite getDetailsComposite() {
		return detailsComposite;
	}

	protected TabbedPropertySheetPage getTabbedPropertySheetPage() {
		return tabbedPropertySheetPage;
	}

	public void refresh() {

		if (!getCSingleObjectChooser().isDisposed()) {

			super.refresh();

			EObject selected = getChooserSelection();

			if (selected != selectedObject) {

				disposeDetailsComposite();
				disposeGroupDetails();

				if (selected != null) {
					createGroupDetails();
					createDetailsComposite();
				}
			}

			if (groupDetails != null && !groupDetails.isDisposed()) {

				if (detailsComposite != null && !detailsComposite.isDisposed()) {
					detailsComposite.loadData();
				}

				groupDetails.getParent().layout();
				groupDetails.layout();
			}

			if (parent.getParent() != null
					&& parent.getParent().getParent() != null) {

				parent.getParent().getParent().layout(true, true);
				resizeScrolledComposite();
			}

			selectedObject = selected;
		}
	}

	@Override
	public boolean shouldUseExtraSpace() {
		return true;
	}

	protected void disposeDetailsComposite() {
		if (detailsComposite != null && !detailsComposite.isDisposed()) {
			detailsComposite.dispose();
		}
	}

	protected void disposeGroupDetails() {

		if (groupDetails != null && !groupDetails.isDisposed()) {
			groupDetails.dispose();
		}
	}

	protected void createGroupDetails() {

		if (parent != null) {
			groupDetails = getWidgetFactory().createGroup(parent,
					getDetailsLabelText());
			GridLayout gl = new GridLayout();
			groupDetails.setLayout(gl);

			FormData data = new FormData();
			data.left = new FormAttachment(0, 0);
			data.right = new FormAttachment(100, 0);
			data.top = new FormAttachment(getCSingleObjectChooser(),
					ITabbedPropertyConstants.VSPACE);

			groupDetails.setLayoutData(data);
		}
	}

	protected void createDetailsComposite() {

		detailsComposite = getComposite(groupDetails);
		if (detailsComposite != null) {
			getWidgetFactory().adapt(detailsComposite);
			detailsComposite.createWidgets(groupDetails, getWidgetFactory());
			GridData gdata = new GridData(GridData.FILL_BOTH);
			detailsComposite.setLayoutData(gdata);
			detailsComposite.setSectionData(groupDetails);
			detailsComposite.hookListeners();
			detailsComposite.setEMFEditDomain(this.getEditingDomain());
		}

	}

	protected EObject getChooserSelection() {

		if (this.getCSingleObjectChooser().getSelection() instanceof EObject) {
			return (EObject) this.getCSingleObjectChooser().getSelection();
		}

		return null;
	}

	protected void resizeScrolledComposite() {

		Point currentTabSize = new Point(0, 0);
		TabContents currentTab = this.getTabbedPropertySheetPage()
				.getCurrentTab();

		if (currentTab != null && parent != null && parent.getParent() != null
				&& parent.getParent().getParent() != null) {

			Composite sizeReference = parent.getParent().getParent();
			if (sizeReference != null) {
				currentTabSize = sizeReference.computeSize(SWT.DEFAULT,
						SWT.DEFAULT);
			}
		}

		// Get the ScrolledComposite and resize it

		if (this.getTabbedPropertySheetPage() != null
				&& this.getTabbedPropertySheetPage().getControl() instanceof Composite) {

			if (((Composite) this.getTabbedPropertySheetPage().getControl())
					.getChildren().length == 1) {

				Control layoutComposite = ((Composite) this
						.getTabbedPropertySheetPage().getControl())
						.getChildren()[0];

				for (Control con : ((Composite) layoutComposite).getChildren()) {
					if (con instanceof ScrolledComposite) {
						((ScrolledComposite) con).setMinSize(currentTabSize.x,
								currentTabSize.y);
					}
				}
			}
		}
	}

	protected abstract String getDetailsLabelText();

	protected abstract DetailComposite getComposite(Composite parent);
}
