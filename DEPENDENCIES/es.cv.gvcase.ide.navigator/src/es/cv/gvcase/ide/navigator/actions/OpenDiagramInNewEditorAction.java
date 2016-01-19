/*******************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) - initial API implementation
 * [03/04/08] Francisco Javier Cano Muñoz (Prodevelop) - adaptation to Common Navigator Framework
 *
 ******************************************************************************/
package es.cv.gvcase.ide.navigator.actions;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.gmf.runtime.emf.commands.core.command.AbstractTransactionalCommand;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.SelectionListenerAction;

import es.cv.gvcase.mdt.common.Activator;
import es.cv.gvcase.mdt.common.commands.OpenDiagramCommand;

/**
 * Action to open a {@link Diagram} in a new {@link IEditorPart}.
 * By default Diagrams are opened in the same IEditorPart.
 * 
 * @author <a href="maulto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 *
 */
public class OpenDiagramInNewEditorAction extends SelectionListenerAction {

	private static String ACTION_TEXT = "Open in new editor";
	
	public OpenDiagramInNewEditorAction() {
		super(ACTION_TEXT);
	}

	protected Diagram getDiagram() {
		IStructuredSelection selection = getStructuredSelection();
		if (selection != null && selection.size() == 1) {
			if (selection.getFirstElement() instanceof Diagram) {
				return (Diagram) selection.getFirstElement();
			}
		}
		return null;
	}
	
	@Override
	public boolean isEnabled() {
		return super.isEnabled() && getDiagram() != null;
	}

	@Override
	public void run() {
		Diagram diagram = getDiagram();
		if (getDiagram() == null) {
			return;
		}
		AbstractTransactionalCommand command = new OpenDiagramCommand(
				diagram, true);
		try {
			command.execute(new NullProgressMonitor(), null);
		} catch (ExecutionException ex) {
			Activator.getDefault().logError(
					"Cannot open diagram " + getDiagram().getName(), ex);
		}
	}

}
