/*******************************************************************************
 * Copyright (c) 2008, 2009 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – Initial implementation.
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

import es.cv.gvcase.mdt.common.util.MultiDiagramUtil;

/**
 * The Class OpenDiagramCommand.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 */
public class OpenDiagramCommand extends AbstractCommonTransactionalCommmand {

	/** The {@link Diagram} to open. */
	private Diagram diagramToOpen = null;

	/**
	 * Flag to close old {@link Diagram}.
	 */
	private boolean openInNew = false;

	/**
	 * Instantiates a new {@link OpenDiagramCommand}.
	 * 
	 * @param diagramToOpen
	 *            the diagram to open
	 */
	public OpenDiagramCommand(Diagram diagramToOpen) {
		this(diagramToOpen, true);
	}

	/**
	 * Instantiates a new {@link OpenDiagramCommand}.
	 * 
	 * @param diagramToOpen
	 * @param closeOld
	 */
	public OpenDiagramCommand(Diagram diagramToOpen, boolean openInNew) {
		// editing domain is taken for original diagram,
		// if we open diagram from another file, we should use another editing
		// domain
		super(TransactionUtil.getEditingDomain(diagramToOpen),
				"Open Diagram", null);
		this.diagramToOpen = diagramToOpen;
		this.openInNew = openInNew;
	}

	public boolean isOpenInNew() {
		return openInNew;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.gmf.runtime.emf.commands.core.command.
	 * AbstractTransactionalCommand
	 * #doExecuteWithResult(org.eclipse.core.runtime.IProgressMonitor,
	 * org.eclipse.core.runtime.IAdaptable)
	 */
	@Override
	protected CommandResult doExecuteWithResult(IProgressMonitor monitor,
			IAdaptable info) throws ExecutionException {
		try {
			// retrieve the Diagram to open.
			Diagram diagram = getDiagramToOpen();
			if (diagram == null) {
				// if there is no Diagram to open, cancel the command.
				return CommandResult.newCancelledCommandResult();
			}
			// open the diagram
			MultiDiagramUtil.openDiagram(diagram, isOpenInNew());
			// all went well.
			return CommandResult.newOKCommandResult();
		} catch (Exception ex) {
			// something went wrong
			throw new ExecutionException("Can't open diagram", ex);
		}
	}

	/**
	 * Gets the {@link Diagram} to open.
	 * 
	 * @return the diagram to open
	 */
	protected Diagram getDiagramToOpen() {
		if (diagramToOpen != null) {
			return diagramToOpen;
		}

		List<Diagram> diagrams = MultiDiagramUtil
				.getDiagramsAssociatedToView(diagramToOpen);
		if (diagrams.size() > 0) {
			return diagrams.get(0);
		}
		return null;
	}

	@Override
	public boolean canUndo() {
		return false;
	}
}