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

import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.transaction.Transaction;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.diagram.ui.commands.ICommandProxy;
import org.eclipse.gmf.runtime.emf.commands.core.command.AbstractTransactionalCommand;

import es.cv.gvcase.mdt.common.Activator;
import es.cv.gvcase.mdt.common.commands.wrappers.GMFtoEMFCommandWrapper;

/**
 * An {@link AbstractTransactionalCommand} that can provide proxies for EMF and
 * GEF. <br>
 * Has one-call methods to execute the command both inside or outside an editing
 * domain.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public abstract class AbstractCommonTransactionalCommmand extends
		AbstractTransactionalCommand {

	/**
	 * Default constructor.
	 * 
	 * @param domain
	 * @param label
	 * @param affectedFiles
	 */
	public AbstractCommonTransactionalCommmand(
			TransactionalEditingDomain domain, String label, List affectedFiles) {
		super(domain, label, affectedFiles);
	}

	/**
	 * To EMF proxy.
	 * 
	 * @return
	 */
	public Command toEMFCommand() {
		return new GMFtoEMFCommandWrapper(this);
	}

	/**
	 * To GEF proxy.
	 * 
	 * @return
	 */
	public org.eclipse.gef.commands.Command toGEFCommand() {
		return new ICommandProxy(this);
	}

	/**
	 * Execute this command in the {@link EditingDomain}'s {@link CommandStack}
	 * as a {@link Transaction}.
	 */
	public void executeInTransaction() {
		if (getEditingDomain() != null) {
			getEditingDomain().getCommandStack().execute(toEMFCommand());
		}
	}

	/**
	 * Execute this {@link Command} without a {@link TransactionalEditingDomain}
	 * .
	 */
	public void executeOutTransaction() {
		executeOutTransaction(null);
	}

	/**
	 * Execute this {@link AbstractCommonTransactionalCommmand} with a
	 * {@link TransactionalEditingDomain}.
	 * 
	 * @param info
	 */
	public void executeOutTransaction(IAdaptable info) {
		try {
			doExecuteWithResult(new NullProgressMonitor(), info);
		} catch (ExecutionException ex) {
			ex.printStackTrace();
			Activator.getDefault().logError("Error executing operation", ex);
		}
	}

	/**
	 * Execute this {@link AbstractCommonTransactionalCommmand} without a
	 * {@link TransactionalEditingDomain}.
	 * 
	 */
	public void executeOperation() {
		executeOperation(null);
	}

	/**
	 * Execute this {@link AbstractCommonTransactionalCommmand} without a
	 * {@link TransactionalEditingDomain}.
	 * 
	 * @param info
	 */
	public void executeOperation(IAdaptable info) {
		TransactionalEditingDomain domain = getEditingDomain();
		if (domain == null) {
			executeOutTransaction(info);
		} else {
			executeInTransaction();
		}
	}

	// // IStatus helpers

	protected static final IStatus OKStatus = new Status(IStatus.OK,
			Activator.PLUGIN_ID, "Ok");

	protected static final IStatus WarningStatus = new Status(IStatus.WARNING,
			Activator.PLUGIN_ID, "Warning");

	protected static final IStatus CancelStatus = new Status(IStatus.CANCEL,
			Activator.PLUGIN_ID, "Cancel");

	protected static final IStatus ErrorStatus = new Status(IStatus.ERROR,
			Activator.PLUGIN_ID, "Error");

	/**
	 * Bridge from {@link IStatus} to {@link CommandResult}.
	 * 
	 * @param status
	 * @return
	 */
	protected CommandResult statusToCommandResult(IStatus status) {
		if (status != null) {
			switch (status.getCode()) {
			case IStatus.OK:
				return CommandResult.newOKCommandResult();
			case IStatus.WARNING:
				return CommandResult.newWarningCommandResult(status
						.getMessage(), null);
			case IStatus.CANCEL:
				return CommandResult.newCancelledCommandResult();
			case IStatus.ERROR:
				return CommandResult.newErrorCommandResult(status.getMessage());
			case IStatus.INFO:
				return CommandResult.newWarningCommandResult(status
						.getMessage(), null);
			default:
				break;
			}
		}
		return CommandResult.newOKCommandResult();
	}

}
