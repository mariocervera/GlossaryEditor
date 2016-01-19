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

import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gmf.runtime.diagram.ui.actions.DiagramAction;
import org.eclipse.gmf.runtime.diagram.ui.commands.ICommandProxy;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.ui.IWorkbenchPage;

import es.cv.gvcase.mdt.common.commands.OpenDiagramCommand;
import es.cv.gvcase.mdt.common.util.MDTUtil;

/**
 * "Open as diagram" action for context menu.
 * 
 * @author <a href="mailto:fjcano@prodeelop.es">Francisco Javier Cano Muñoz</a>
 */
public class OpenDiagramAction extends DiagramAction {

	/** The diagram to open. */
	private Diagram diagramToOpen = null;

	/** The view. */
	private View view = null;

	/**
	 * Instantiates a new open as diagram action.
	 * 
	 * @param page
	 *            the page
	 * @param view
	 *            the view
	 * @param diagram
	 *            the diagram
	 */
	public OpenDiagramAction(IWorkbenchPage page, View view, Diagram diagram) {
		super(page);
		this.view = view;
		this.diagramToOpen = diagram;
	}

	@Override
	protected Request createTargetRequest() {
		return null;
	}

	@Override
	protected boolean isSelectionListener() {
		return true;
	}

	@Override
	protected Command getCommand() {
		Command command = new ICommandProxy(new OpenDiagramCommand(
				diagramToOpen));
		return command;
	}

	@Override
	public boolean isEnabled() {
		return diagramToOpen != null && view != null;
	}

	@Override
	public void refresh() {
		super.refresh();
		setText(calculateText());
	}

	/**
	 * Calculate text.
	 * 
	 * @return the string
	 */
	private String calculateText() {
		return MDTUtil.getDiagramName(diagramToOpen);
	}

}
