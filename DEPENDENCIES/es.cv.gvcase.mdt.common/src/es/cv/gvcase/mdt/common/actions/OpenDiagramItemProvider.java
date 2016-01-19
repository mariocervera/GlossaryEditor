/***************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte,
 * Generalitat de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – initial API and implementation
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.actions;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gmf.runtime.common.ui.services.action.contributionitem.AbstractContributionItemProvider;
import org.eclipse.gmf.runtime.common.ui.util.IWorkbenchPartDescriptor;
import org.eclipse.gmf.runtime.diagram.ui.editparts.GraphicalEditPart;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.IWorkbenchPage;

import es.cv.gvcase.mdt.common.util.MultiDiagramUtil;

/**
 * Contributes the "Open as diagram" action to the context menu.
 * 
 * @author <a href="mailto:fjcano@prodeelop.es">Francisco Javier Cano Muñoz</a>
 */
public class OpenDiagramItemProvider extends AbstractContributionItemProvider {

	/** The Constant MENU_OPEN_AS_DIAGRAM. */
	public static final String MENU_OPEN_DIAGRAM = "menu_open_diagram"; //$NON-NLS-1$

	@Override
	protected IMenuManager createMenuManager(String menuId,
			IWorkbenchPartDescriptor partDescriptor) {
		if (!MENU_OPEN_DIAGRAM.equals(menuId)) {
			return super.createMenuManager(menuId, partDescriptor);
		}
		MenuManager menuManager = new MenuManager("Open diagram");
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
			GraphicalEditPart selected = (GraphicalEditPart) getSelectedObject(myWorkbenchPart);
			for (Diagram diagram : getAssociatedDiagramsToElement(selected
					.resolveSemanticElement())) {
				OpenDiagramAction action = new OpenDiagramAction(
						getWorkbenchPage(), selected.getNotationView(), diagram);
				action.init();
				manager.add(action);
			}
		}

		/**
		 * Gets the workbench page.
		 * 
		 * @return the workbench page
		 */
		private IWorkbenchPage getWorkbenchPage() {
			return myWorkbenchPart.getPartPage();
		}
	}

	/**
	 * Gets the associated diagrams to element.
	 * 
	 * @param view
	 *            the view
	 * 
	 * @return the associated diagrams to element
	 */
	private List<Diagram> getAssociatedDiagramsToElement(EObject eObject) {
		return MultiDiagramUtil.getDiagramsAssociatedToElement(eObject);
	}

}
