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

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.gmf.runtime.common.core.util.Log;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.SelectionListenerAction;

import es.cv.gvcase.ide.navigator.Activator;
import es.cv.gvcase.mdt.common.util.MultiDiagramUtil;

/**
 * Action to open a {@link Diagram} closing all other {@link IEditorPart}s that are editing
 * the same resource the {@link Diagram} to open uses.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 */
public class OpenDiagramAction extends SelectionListenerAction {

	/**
	 * Instantiates a new open diagram action.
	 */
	public OpenDiagramAction() {
		super("Open diagram");
	}

	/**
	 * Gets the diagram.
	 * 
	 * @return the diagram
	 */
	protected Diagram getDiagram() {
		IStructuredSelection selection = getStructuredSelection();
		Object selected = selection.getFirstElement();
		if (selected instanceof Diagram) {
			return ((Diagram) selected);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		super.run();
		// //
		try {
			Diagram diagram = getDiagram();
			if (diagram != null) {
				MultiDiagramUtil.openDiagram(diagram, true);
			}
		} catch (ExecutionException ex) {
			Log.error(Activator.getDefault(), 0, "Can't open diagram", ex);
		}
	}

}
