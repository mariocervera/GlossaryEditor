/***************************************************************************
 * Copyright (c) 2008, 2009 Conselleria de Infraestructuras y Transporte,
 * Generalitat de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – initial API and implementation
 *
 ******************************************************************************/

package es.cv.gvcase.mdt.common.commands;

import org.eclipse.gef.commands.Command;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;

import es.cv.gvcase.mdt.common.util.DiagramEditPartsUtil;

/**
 * The Class UMLUpdateDiagramCommand.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 */
public class UpdateDiagramCommand extends Command {

	/** The edit part. */
	private IGraphicalEditPart editPart;

	/**
	 * Instantiates a new uML update diagram command.
	 * 
	 * @param editPart
	 *            the edit part
	 */
	public UpdateDiagramCommand(IGraphicalEditPart editPart) {
		this.editPart = editPart;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#canExecute()
	 */
	@Override
	public boolean canExecute() {
		return editPart != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#canUndo()
	 */
	@Override
	public boolean canUndo() {
		return editPart != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	@Override
	public void execute() {
		DiagramEditPartsUtil.updateDiagram(editPart);
	}

	@Override
	public void undo() {
		// super.undo();
		//
		DiagramEditPartsUtil.updateDiagram(editPart);
	}

}
