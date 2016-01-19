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
package es.cv.gvcase.ide.navigator.actions.resource;

import org.eclipse.gmf.runtime.common.ui.util.WorkbenchPartActivator;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.SelectionListenerAction;

/**
 * Action to open a {@link Diagram} closing all other {@link IEditorPart}s that
 * are editing the same resource the Diagram to open uses.
 * 
 * @author <a href="maulto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 */
public class OpenPropertiesTabAction extends SelectionListenerAction {

	/**
	 * Instantiates a new open diagram action.
	 */
	public OpenPropertiesTabAction() {
		super("Open properties");
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		WorkbenchPartActivator.showPropertySheet();
	}


}
