/***************************************************************************
 * Copyright (c) 2010 Conselleria de Infraestructuras y Transporte,
 * Generalitat de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – initial API and implementation
 *
 ******************************************************************************/

package es.cv.gvcase.mdt.common.commands.diagram;

import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.gmf.runtime.common.core.service.IProvider;
import org.eclipse.gmf.runtime.common.ui.services.action.contributionitem.AbstractContributionItemProvider;
import org.eclipse.gmf.runtime.common.ui.util.IWorkbenchPartDescriptor;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPage;

/**
 * Contributes the "Create diagram" to the context menu. <br>
 * Will contribute the create diagram action to an editpart that has some
 * diagrams available to be created on it.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 */
public class CreateDiagramItemProvider extends AbstractContributionItemProvider
		implements IProvider {

	/** The Constant MENU_CREATE_DIAGRAM. */
	public static final String MENU_CREATE_DIAGRAM = "menu_create_diagram"; //$NON-NLS-1$

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.gmf.runtime.common.ui.services.action.contributionitem.
	 * AbstractContributionItemProvider#createMenuManager(java.lang.String,
	 * org.eclipse.gmf.runtime.common.ui.util.IWorkbenchPartDescriptor)
	 */
	@Override
	protected IMenuManager createMenuManager(String menuId,
			IWorkbenchPartDescriptor partDescriptor) {
		if (!MENU_CREATE_DIAGRAM.equals(menuId)) {
			return super.createMenuManager(menuId, partDescriptor);
		}
		MenuManager menuManager = new MenuManager("Create diagram");
		MenuBuilder builder = new MenuBuilder(partDescriptor);
		// XXX: build initial content -- otherwise menu is never shown
		builder.buildMenu(menuManager);

		menuManager.addMenuListener(builder);
		return menuManager;
	}

	/**
	 * The Class MenuBuilder.
	 */
	private class MenuBuilder implements IMenuListener {

		/** The my workbench part. */
		private final IWorkbenchPartDescriptor myWorkbenchPart;

		/**
		 * Instantiates a new menu builder.
		 * 
		 * @param workbenchPart
		 *            the workbench part
		 */
		public MenuBuilder(IWorkbenchPartDescriptor workbenchPart) {
			myWorkbenchPart = workbenchPart;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.action.IMenuListener#menuAboutToShow(org.eclipse
		 * .jface.action.IMenuManager)
		 */
		public void menuAboutToShow(IMenuManager manager) {
			buildMenu(manager);
		}

		/**
		 * Builds the menu.
		 * 
		 * @param manager
		 *            the manager
		 */
		public void buildMenu(IMenuManager manager) {
			manager.removeAll();
			IGraphicalEditPart editPart = getSelectedEditPart();
			Resource resource = getDiagramResource();
			if (editPart != null && resource != null) {
				for (String kind : getModelIDsForElement(editPart)) {
					CreateDiagramAction action = new CreateDiagramAction(
							getWorkbenchPage(), kind, editPart, resource);
					action.init();
					manager.add(action);
				}
			}
		}

		protected List<String> getModelIDsForElement(IGraphicalEditPart editPart) {
			return EClassToDiagramRegistry.getInstance()
					.getDiagramsForEditPart(editPart);
		}

		/**
		 * Gets the workbench page.
		 * 
		 * @return the workbench page
		 */
		private IWorkbenchPage getWorkbenchPage() {
			return myWorkbenchPart.getPartPage();
		}

		private List<Object> getSelectedObjects() {
			ISelection selection = getWorkbenchPage().getSelection();
			if (selection instanceof IStructuredSelection) {
				return ((IStructuredSelection) selection).toList();
			}
			return Collections.EMPTY_LIST;
		}

		/**
		 * Gets the selected e object.
		 * 
		 * @return the selected e object
		 */
		private EObject getSelectedEObject() {
			IGraphicalEditPart editPart = getSelectedEditPart();
			if (editPart != null) {
				return editPart.resolveSemanticElement();
			}
			// no GraphicalEditPart selected
			ISelection selection = getWorkbenchPage().getSelection();
			if (selection instanceof StructuredSelection) {
				StructuredSelection ss = (StructuredSelection) selection;
				Object object = ss.getFirstElement();
				if (object instanceof EObject) {
					return (EObject) object;
				}
			}
			// no element selected
			return null;
		}

		/**
		 * Gets the selected edit part.
		 * 
		 * @return the selected edit part
		 */
		private IGraphicalEditPart getSelectedEditPart() {
			for (Object next : getSelectedObjects()) {
				if (next instanceof IGraphicalEditPart) {
					IGraphicalEditPart editPart = (IGraphicalEditPart) next;
					return editPart;
				}
			}
			return null;
		}

		/**
		 * Gets the diagram resource.
		 * 
		 * @return the diagram resource
		 */
		private Resource getDiagramResource() {
			IGraphicalEditPart editPart = getSelectedEditPart();
			if (editPart != null) {
				return editPart.getNotationView().eResource();
			}
			return null;
		}
	}

}
