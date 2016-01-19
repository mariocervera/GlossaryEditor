/*******************************************************************************
 * Copyright (c) 2012 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana. All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Marc Gil Sendra (Integranova) - initial API implementation
 ******************************************************************************/
package es.cv.gvcase.mdt.common.composites;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

/**
 * A Composite with the capability of refresh it's contents
 * 
 * @author mgil
 */
public abstract class RefresheableComposite extends Composite {
	protected TabbedPropertySheetWidgetFactory widgetFactory;
	protected StructuredViewer structuredViewer;
	protected EditingDomain domain;
	protected List<EClass> availableFor;
	protected EObject eObject;

	public RefresheableComposite(Composite parent, int style,
			StructuredViewer treeViewer, EditingDomain domain) {
		super(parent, style);
		this.widgetFactory = new TabbedPropertySheetWidgetFactory();
		this.structuredViewer = treeViewer;
		this.domain = domain;

		widgetFactory.adapt(this);
		availableFor = new ArrayList<EClass>();
		fillAvailableForList(availableFor);

		createContents();
		setSectionData();
		hookListeners();
	}

	/**
	 * Fill the given list with the eClasses that could activate this composite
	 */
	protected abstract void fillAvailableForList(List<EClass> availableFor);

	/**
	 * Creates the contents of this composite
	 */
	protected abstract void createContents();

	/**
	 * Sets the section data for this composite
	 */
	protected abstract void setSectionData();

	/**
	 * Set the listeners for every widget of this composite
	 */
	protected abstract void hookListeners();

	/**
	 * Set the eObject of this Composite
	 */
	public void setEObject(EObject eObject) {
		this.eObject = eObject;
		domain = TransactionUtil.getEditingDomain(eObject);
		if (domain == null) {
			domain = ((IEditingDomainProvider) eObject.eResource()
					.getResourceSet()).getEditingDomain();
		}
	}

	/**
	 * Returns the selected EObject in the tree
	 */
	public EObject getSelectedElement() {
		if (eObject != null) {
			return eObject;
		} else if (structuredViewer != null) {
			return (EObject) ((StructuredSelection) structuredViewer
					.getSelection()).getFirstElement();
		} else {
			return null;
		}
	}

	/**
	 * Executes the given command
	 */
	protected void executeCommand(Command command) {
		if (command != null && command.canExecute()) {
			domain.getCommandStack().execute(command);
		}
	}

	/**
	 * A method to refresh the contents of the composite
	 */
	public abstract void refresh();

	/**
	 * Check if the current Composite is available for the given selection in
	 * the Tree. By default, it checks is it's a StructuredSelection and if the
	 * first selected element is an EObject. In this case, checks if this
	 * composite is available for its eClass.
	 */
	public boolean isEnabledFor(ISelection selection) {
		if (!(selection instanceof StructuredSelection)) {
			return false;
		}

		StructuredSelection ss = (StructuredSelection) selection;
		if (!(ss.getFirstElement() instanceof EObject)) {
			return false;
		}

		EObject eo = (EObject) ss.getFirstElement();
		return isEnabledFor(eo.eClass());
	}

	/**
	 * Check whenever this composite is available for the given eClass of the
	 * selected element
	 */
	protected boolean isEnabledFor(EClass eClass) {
		if (!availableFor.contains(eClass)) {
			if (checkSuperTypes()) {
				for (EClass ec : eClass.getEAllSuperTypes()) {
					if (availableFor.contains(ec)) {
						return true;
					}
				}
			}
			return false;
		}

		return true;
	}

	/**
	 * Return true if should check super types to check if this composite needs
	 * to be shown or not
	 */
	protected boolean checkSuperTypes() {
		return false;
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);

		if (visible) {
			EObject selected = getSelectedElement();
			if (domain == null && selected != null) {
				domain = TransactionUtil.getEditingDomain(selected);
				if (domain == null) {
					domain = ((IEditingDomainProvider) selected.eResource()
							.getResourceSet()).getEditingDomain();
				}
			}
			refresh();
		}
	}
}