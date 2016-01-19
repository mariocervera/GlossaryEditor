/*******************************************************************************
 * Copyright (c) 2009 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – initial API and
 * implementation
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.actions;

import org.eclipse.core.runtime.Platform;
import org.eclipse.gmf.runtime.diagram.ui.printing.render.actions.EnhancedPrintActionHelper;
import org.eclipse.ui.IWorkbenchPart;

import es.cv.gvcase.mdt.common.part.MultiPageEditorPart;

/**
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public class MultiPagePrintActionHelper extends EnhancedPrintActionHelper {

	@Override
	public void doPrint(IWorkbenchPart workbenchPart) {
		if (workbenchPart == null) {
			return;
		}
		// get the true active editor that is nested inside the multi page
		// editor.
		MultiPageEditorPart multiPageEditor = (MultiPageEditorPart) Platform
				.getAdapterManager().getAdapter(workbenchPart,
						MultiPageEditorPart.class);
		// use the nested editor.
		if (multiPageEditor != null && multiPageEditor.getActiveEditor() != null) {
			super.doPrint(multiPageEditor.getActiveEditor());
		} else {
			super.doPrint(workbenchPart);
		}
	}

}
