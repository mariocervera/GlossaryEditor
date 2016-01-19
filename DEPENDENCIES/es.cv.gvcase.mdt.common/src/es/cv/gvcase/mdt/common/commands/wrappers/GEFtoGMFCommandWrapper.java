/***************************************************************************
 * Copyright (c) 2009 Conselleria de Infraestructuras y Transporte,
 * Generalitat de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Marc Gil Sendra (Prodevelop)
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.commands.wrappers;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.commands.Command;
import org.eclipse.gmf.runtime.common.core.command.AbstractCommand;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;

/**
 * A GMF Command that wraps a GEF command. Each method is redirected to the GEF
 * one.
 */
public class GEFtoGMFCommandWrapper extends AbstractCommand {

	/**
	 * The wrapped GEF Command. Package-level visibility so that the command
	 * stack wrapper can access the field.
	 */
	private Command gefCommand;

	/**
	 * Constructor.
	 * 
	 * @param gefCommand
	 *            the gef command
	 */
	public GEFtoGMFCommandWrapper(Command gefCommand) {
		super(gefCommand.getLabel());
		this.gefCommand = gefCommand;
	}

	/**
	 * Returns the wrapped GEF command.
	 * 
	 * @return the GEF command
	 */
	public Command getGEFCommand() {
		return gefCommand;
	}

	@Override
	public boolean canExecute() {
		return gefCommand.canExecute();
	}

	@Override
	public void dispose() {
		gefCommand.dispose();
	}

	@Override
	public boolean canUndo() {
		return gefCommand.canUndo();
	}

	@Override
	protected CommandResult doExecuteWithResult(
			IProgressMonitor progressMonitor, IAdaptable info)
			throws ExecutionException {
		gefCommand.execute();
		return CommandResult.newOKCommandResult();
	}

	@Override
	protected CommandResult doRedoWithResult(IProgressMonitor progressMonitor,
			IAdaptable info) throws ExecutionException {
		gefCommand.redo();
		return CommandResult.newOKCommandResult();
	}

	@Override
	protected CommandResult doUndoWithResult(IProgressMonitor progressMonitor,
			IAdaptable info) throws ExecutionException {
		gefCommand.undo();
		return CommandResult.newOKCommandResult();
	}
}
