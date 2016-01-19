/***************************************************************************
 * Copyright (c) 2007-2011 Conselleria de Infraestructuras y Transporte,
 * Generalitat de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Mario Cervera Ubeda (Integranova)
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.actions.handlers;

import org.eclipse.gmf.runtime.common.ui.services.action.global.AbstractGlobalActionHandlerProvider;
import org.eclipse.gmf.runtime.common.ui.services.action.global.IGlobalActionHandler;
import org.eclipse.gmf.runtime.common.ui.services.action.global.IGlobalActionHandlerContext;
import org.eclipse.gmf.runtime.notation.Diagram;

import es.cv.gvcase.mdt.common.actions.registry.ClipboardActionHandlerRegistry;

/**
 * The Class ClipboardActionHandlerProvider.
 */
public class ClipboardActionHandlerProvider extends
		AbstractGlobalActionHandlerProvider {

	/**
	 * Returns a global action handler that supports operations (cut, copy, and
	 * paste).
	 * 
	 * @param context
	 *            the context
	 * 
	 * @return the global action handler
	 */
	@Override
	public IGlobalActionHandler getGlobalActionHandler(
			final IGlobalActionHandlerContext context) {
		if (context.getActivePart() != null) {
			Diagram diagram = (Diagram) context.getActivePart().getAdapter(
					Diagram.class);
			String diagramType = diagram.getType();
			return ClipboardActionHandlerRegistry.getInstance()
					.getClipboardHandlerFor(diagramType);
		}
		return null;
	}

	protected ClipboardActionHandler getClipboardActionHandler() {
		return new ClipboardActionHandler();
	}
}
