/*******************************************************************************
 * Copyright (c) 2010 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Javier Muñoz Ferrara (Prodevelop) – Initial implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.commands;

import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import es.cv.gvcase.mdt.common.provider.GeneralLabelProvider;

/**
 * Command that opens the upper diagram.
 * 
 * @author <a href="mailto:jmunoz@prodevelop.es">Javier Muñoz Ferrara</a>
 *
 */
public class OpenUpperDiagramCommand extends
		AbstractCommonTransactionalCommmand {

	private List<Diagram> upperDiagrams;

	public OpenUpperDiagramCommand(List<Diagram> upperDiagrams) {
		super(TransactionUtil.getEditingDomain(upperDiagrams.get(0)),
				"Open Upper Diagram", null);
		this.upperDiagrams = upperDiagrams;
	}

	@Override
	protected CommandResult doExecuteWithResult(IProgressMonitor monitor,
			IAdaptable info) throws ExecutionException {
		try {
			// retrieve the Diagram to open.
			Diagram diagram = getUpperDiagram();
			if (diagram == null) {
				// if there is no Diagram to open, cancel the command.
				return CommandResult.newCancelledCommandResult();
			}

			(new OpenDiagramCommand(diagram)).executeInTransaction();

			// all went well.
			return CommandResult.newOKCommandResult();
		} catch (Exception ex) {
			// something went wrong
			throw new ExecutionException("Can't open diagram", ex);
		}
	}

	/**
	 * Gets the upper diagram.
	 * 
	 * @return the upper diagram
	 */
	private Diagram getUpperDiagram() {
		List<Diagram> diagramL = upperDiagrams;

		if (diagramL.isEmpty()) {
			return null;
		} else if (diagramL.size() == 1) {
			return diagramL.get(0);
		}

		ElementListSelectionDialog dialog = new ElementListSelectionDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				new GeneralLabelProvider());
		dialog.setMessage("Select the diagram to be open");
		dialog.setTitle("Diagram selection");
		dialog.setElements(diagramL.toArray());
		if (dialog.open() == Dialog.OK) {
			return (Diagram) dialog.getFirstResult();
		}

		return null;
	}

}
