/*******************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – Initial API 
 * implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.ide.navigator.provider;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.CommonViewer;

import es.cv.gvcase.ide.navigator.util.NavigatorUtil;
import es.cv.gvcase.ide.navigator.view.MOSKittCommonNavigator;

/**
 * Specialization of {@link CommonActionProvider} to be used as MOSKitt action
 * provider.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 */
public class MOSKittCommonActionProvider extends CommonActionProvider {

	/**
	 * Gets the ID of the {@link CommonViewer} this
	 * {@link MOSKittCommonActionProvider} is associated to.
	 * 
	 * @return the viewer id
	 */
	protected String getViewerID() {
		return getActionSite().getViewSite().getId();
	}

	/**
	 * Gets the {@link MOSKittCommonNavigator} this
	 * {@link MOSKittCommonActionProvider} is associated with, via the viewerID.
	 * 
	 * @return the common navigator
	 */
	protected MOSKittCommonNavigator getCommonNavigator() {
		IViewPart part = NavigatorUtil.findViewPart(getViewerID());
		if (part instanceof MOSKittCommonNavigator) {
			return ((MOSKittCommonNavigator) part);
		}
		return null;
	}

	/**
	 * Gets the current context's selection.
	 * 
	 * @return the selection
	 */
	protected ISelection getSelection() {
		ActionContext context = getContext();
		return (context != null) ? context.getSelection() : null;
	}

	/**
	 * Gets the first element of the current context's selection.
	 * 
	 * @return the first selected element
	 */
	protected Object getFirstSelectedElement() {
		ISelection selection = getSelection();
		if (selection instanceof StructuredSelection) {
			return ((StructuredSelection) selection).getFirstElement();
		}
		return null;
	}

}
