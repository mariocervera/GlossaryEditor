/*******************************************************************************
 * Copyright (c) 2010 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Marc Gil Sendra (Prodevelop) – Initial API 
 * implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.ide.navigator.actions.resource;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.mapping.ResourceMapping;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.internal.navigator.AdaptabilityUtility;
import org.eclipse.ui.navigator.ICommonActionConstants;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;

import es.cv.gvcase.ide.navigator.provider.MOSKittCommonActionProvider;

/**
 * Specialization of {@link MOSKittCommonActionProvider} to contribute the
 * {@link OpenFileAction} global handler.
 * 
 * @author <a href="mailto:mgil@prodevelop.es">Marc Gil Sendra</a>
 */
public class OpenFileActionProvider extends MOSKittCommonActionProvider {

	private OpenFileAction openFileAction;

	private ICommonViewerWorkbenchSite viewSite = null;

	private boolean contribute = false;

	public void init(ICommonActionExtensionSite aConfig) {
		if (aConfig.getViewSite() instanceof ICommonViewerWorkbenchSite) {
			viewSite = (ICommonViewerWorkbenchSite) aConfig.getViewSite();
			openFileAction = new OpenFileAction(viewSite.getPage());
			contribute = true;
		}
	}

	public void fillContextMenu(IMenuManager aMenu) {
		if (!contribute || getContext().getSelection().isEmpty()) {
			return;
		}

		IStructuredSelection selection = (IStructuredSelection) getContext()
				.getSelection();

		openFileAction.selectionChanged(selection);
		if (openFileAction.isEnabled()) {
			aMenu.insertAfter(ICommonMenuConstants.GROUP_OPEN, openFileAction);
		}
		addOpenWithMenu(aMenu);
	}

	public void fillActionBars(IActionBars theActionBars) {
		if (!contribute) {
			return;
		}
		IStructuredSelection selection = (IStructuredSelection) getContext()
				.getSelection();
		if (selection.size() == 1
				&& selection.getFirstElement() instanceof IFile) {
			openFileAction.selectionChanged(selection);
			theActionBars.setGlobalActionHandler(ICommonActionConstants.OPEN,
					openFileAction);
		}

	}

	private void addOpenWithMenu(IMenuManager aMenu) {
		IStructuredSelection ss = (IStructuredSelection) getContext()
				.getSelection();

		if (ss == null || ss.size() != 1) {
			return;
		}

		Object o = ss.getFirstElement();

		// first try IResource
		IAdaptable openable = (IAdaptable) AdaptabilityUtility.getAdapter(o,
				IResource.class);
		// otherwise try ResourceMapping
		if (openable == null) {
			openable = (IAdaptable) AdaptabilityUtility.getAdapter(o,
					ResourceMapping.class);
		} else if (((IResource) openable).getType() != IResource.FILE) {
			openable = null;
		}

		if (openable != null) {
			// Create a menu flyout.
			IMenuManager submenu = new MenuManager("Open With",
					ICommonMenuConstants.GROUP_OPEN_WITH);
			submenu.add(new GroupMarker(ICommonMenuConstants.GROUP_TOP));
			// use our OpenWithMenu in order to check Broken References
			submenu.add(new OpenWithMenu(viewSite.getPage(), openable));
			submenu.add(new GroupMarker(ICommonMenuConstants.GROUP_ADDITIONS));

			// Add the submenu.
			if (submenu.getItems().length > 2 && submenu.isEnabled()) {
				aMenu.appendToGroup(ICommonMenuConstants.GROUP_OPEN_WITH,
						submenu);
			}
		}
	}

}
