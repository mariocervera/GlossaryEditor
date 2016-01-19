/******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation 
 ****************************************************************************/

package es.cv.gvcase.mdt.common.actions;

import org.eclipse.gmf.runtime.common.ui.action.actions.IPrintActionHelper;
import org.eclipse.gmf.runtime.common.ui.services.action.global.IGlobalActionContext;

/**
 * A specialized <code>{@link org.eclipse.gmf.runtime.diagram.ui.printing.render.providers.DiagramWithPrintGlobalActionHandler}</code> that supports
 * printing of images.
 * 
 * @author cmahoney
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 */
public class DiagramWithPrintGlobalActionHandler
		extends
		org.eclipse.gmf.runtime.diagram.ui.printing.providers.DiagramWithPrintGlobalActionHandler {

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.gmf.runtime.diagram.ui.printing.providers.
	 * DiagramWithPrintGlobalActionHandler
	 * #doPrint(org.eclipse.gmf.runtime.common
	 * .ui.services.action.global.IGlobalActionContext)
	 */
	protected void doPrint(IGlobalActionContext cntxt) {
		IPrintActionHelper helper = new MultiPagePrintActionHelper();
		helper.doPrint(cntxt.getActivePart());
	}
}