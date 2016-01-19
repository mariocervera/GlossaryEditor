/***************************************************************************
 * Copyright (c) 2008, 2009 Conselleria de Infraestructuras y Transporte,
 * Generalitat de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – initial API and implementation
 * 08/10/07 - Francisco Javier Cano Muñoz (Prodevelop) - changed to an AbstractCommonTransctionalCommand
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.requests.EditCommandRequestWrapper;
import org.eclipse.gmf.runtime.emf.type.core.requests.DestroyElementRequest;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

import es.cv.gvcase.mdt.common.Activator;
import es.cv.gvcase.mdt.common.commands.wrappers.EMFtoGEFCommandWrapper;
import es.cv.gvcase.mdt.common.util.MDTUtil;
import es.cv.gvcase.mdt.common.util.MultiDiagramUtil;

/**
 * A DeleteCommand that deletes a {@link Collection} of {@link EObject}s and
 * their associated Diagrams.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 */
public class DeleteCommand extends AbstractCommonTransactionalCommmand {

	/** The {@link Diagram}s {@link Resource}. */
	private Resource diagramsResource = null;

	/** The affected {@link Diagram}s. */
	private List<Diagram> affectedDiagrams = null;

	/** The active {@link Diagram}. */
	private Diagram activeDiagram = null;

	/**
	 * Elements to be deleted
	 */
	private Collection<Object> elementsToDelete = null;

	/**
	 * Delete the {@link Diagram}s without confirmation
	 */
	private boolean deleteWithConfirmation = true;

	/**
	 * The delete {@link Command}s from the {@link EditPart}s to be deleted.
	 */
	protected CompoundCommand deleteCommands = null;

	/**
	 * Instantiates a new delete command.
	 * 
	 * @param domain
	 * @param label
	 * @param affectedFiles
	 */
	public DeleteCommand(TransactionalEditingDomain domain, String label,
			List affectedFiles, Collection<Object> elementsToDelete) {
		super(domain, label, affectedFiles);
		this.elementsToDelete = elementsToDelete;
	}

	/**
	 * Gets the affected {@link Diagram}s.
	 * 
	 * @return the affected diagrams
	 */
	public List<Diagram> getAffectedDiagrams() {
		if (affectedDiagrams == null) {
			affectedDiagrams = new ArrayList<Diagram>();
		}
		return affectedDiagrams;
	}

	/**
	 * Gets the diagrams resource.
	 * 
	 * @return the diagrams resource
	 */
	public Resource getDiagramsResource() {
		return diagramsResource;
	}

	/**
	 * Gets the active {@link Diagram}.
	 * 
	 * @return the active diagram
	 */
	public Diagram getActiveDiagram() {
		return activeDiagram;
	}

	public Collection<Object> getElementsToDelete() {
		if (elementsToDelete == null) {
			elementsToDelete = Collections.emptyList();
		}
		return elementsToDelete;
	}

	public void setElementsToDelete(Collection<Object> list) {
		elementsToDelete = list;
	}

	/**
	 * As long as there are some elements to delete, this {@link Command} will
	 * be executable.
	 */
	@Override
	public boolean canExecute() {
		return getElementsToDelete().size() > 0;
	}

	/**
	 * Deletes the selected elements. Checks whether the elements to be deleted
	 * have any {@link Diagram}s associated and deletes those Diagrams too.
	 * 
	 */
	@Override
	public CommandResult doExecuteWithResult(IProgressMonitor monitor,
			IAdaptable info) {
		IStatus status;
		try {
			status = doRedo(monitor, info);
		} catch (ExecutionException e) {
			return CommandResult.newErrorCommandResult(e);
		}
		return statusToCommandResult(status);
	}

	@Override
	protected IStatus doRedo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		// clean up
		getAffectedDiagrams().clear();
		// search for affected <Diagram>s if elements in collection are deleted
		for (Object object : getElementsToDelete()) {
			if (object instanceof IGraphicalEditPart) {
				object = ((IGraphicalEditPart) object).resolveSemanticElement();
			}
			if (object instanceof EObject) {
				EObject eObject = (EObject) object;
				for (Diagram diagram : MDTUtil.getDiagramsInHierarchy(eObject,
						null)) {
					if (getAffectedDiagrams().contains(diagram) == false) {
						getAffectedDiagrams().add(diagram);
					}
				}
			}
		}
		// if there's any <Diagram> affected, prompt the user
		boolean performDelete = true;
		if (deleteWithConfirmation == true) {
			if (getAffectedDiagrams().size() > 0) {
				String message = createMessage(getAffectedDiagrams());
				performDelete = MessageDialog.openConfirm(PlatformUI
						.getWorkbench().getDisplay().getActiveShell(),
						"Some diagrams will be deleted", message);
			}
		}
		// if the user agrees or there are no affected <Diagram>s, delete all
		// affected <Diagram>s and perform default delete
		if (performDelete) {
			// if the active <Diagram> is to be deleted, we must have another
			// <Diagram> to open
			Diagram diagramToOpen = null;
			boolean checkActiveDiagram = false;
			if (getAffectedDiagrams().size() > 0) {
				// CompoundCommand cc = new CompoundCommand("Delete diagrams");
				for (Diagram diagram : getAffectedDiagrams()) {
					diagramsResource = diagram.eResource();
					if (MultiDiagramUtil.isDiagramActive(diagram)) {
						diagramToOpen = MultiDiagramUtil
								.getUpperDiagram(diagram);
						checkActiveDiagram = true;
						activeDiagram = diagram;
						break;
					}
				}
				// if the diagram to open is to be deleted, look for one that
				// won't be deleted
				while (diagramToOpen != null
						&& getAffectedDiagrams().contains(diagramToOpen)) {
					diagramToOpen = MultiDiagramUtil
							.getUpperDiagram(diagramToOpen);
				}
				// not ready to delete diagrams; all delete aborted
				if (checkActiveDiagram && diagramToOpen == null) {
					errorActiveDiagram();
					getAffectedDiagrams().clear();
					return ErrorStatus;
				}
				// open new diagram
				if (diagramToOpen != null) {
					try {
						MultiDiagramUtil.openDiagram(diagramToOpen);
					} catch (ExecutionException ex) {
						IStatus status = new Status(IStatus.ERROR,
								Activator.PLUGIN_ID, "Cannot open diagram: "
										+ ex.getLocalizedMessage());
						Activator.getDefault().getLog().log(status);
						return ErrorStatus;
					}
				}
				// close affected diagrams, if any was opened
				for (Diagram diagram : getAffectedDiagrams()) {
					MultiDiagramUtil.closeEditorsThatShowDiagram(diagram);
				}
				// all ready, delete affected diagrams
				for (Diagram diagram : getAffectedDiagrams()) {
					MultiDiagramUtil.deleteDiagramFromResource(diagram, diagram
							.eResource());
				}
			}
			// delete elements
			if (deleteCommands != null) {
				// in case the command was executed before
				deleteCommands.redo();
				return OKStatus;
			}
			// create the required delete commands
			Request request = new EditCommandRequestWrapper(
					new DestroyElementRequest(getEditingDomain(), false));
			deleteCommands = new CompoundCommand("Remove elements");
			for (Object object : getElementsToDelete()) {
				if (object instanceof IGraphicalEditPart) {
					Command command = ((IGraphicalEditPart) object)
							.getCommand(request);
					deleteCommands.add(command);
				} else if (object instanceof EObject) {
					Command command = getCustomDeleteCommand(object);
					if (command != null) {
						deleteCommands.add(command);
						continue;
					}
					if (DeleteRootElementCommand
							.isRootElement((EObject) object)) {
						command = new EMFtoGEFCommandWrapper(
								new DeleteRootElementCommand(
										getEditingDomain(), (EObject) object,
										null));
					} else {
						command = new EMFtoGEFCommandWrapper(
								new org.eclipse.emf.edit.command.DeleteCommand(
										getEditingDomain(), Collections
												.singletonList(object)));
					}
					deleteCommands.add(command);
				}
			}
			if (deleteCommands.canExecute()) {
				deleteCommands.execute();
			}
			return OKStatus;
		} else {
			deleteCommands = null;
			return ErrorStatus;
		}
	}

	protected Command getCustomDeleteCommand(Object object) {
		return null;
	}

	/**
	 * Undo must reopen the last active {@link Diagram}.
	 */
	@Override
	protected IStatus doUndo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		// undo the removal of the elements
		if (deleteCommands == null) {
			return CancelStatus;
		}
		deleteCommands.undo();
		// undo the removal of the diagrams
		if (diagramsResource != null) {
			for (Diagram diagram : getAffectedDiagrams()) {
				diagramsResource.getContents().add(diagram);
			}
		}
		// add deleted elements to the diagram again
		addElementsToDiagram();
		// in case we changed diagrams when the delete occurred, we'll return to
		// that diagram.
		if (getActiveDiagram() != null) {
			try {
				MultiDiagramUtil.openDiagram(getActiveDiagram());
			} catch (ExecutionException ex) {
				IStatus logStatus = new Status(IStatus.ERROR,
						Activator.PLUGIN_ID, "Cannot open diagram: "
								+ ex.getLocalizedMessage());
				Activator.getDefault().getLog().log(logStatus);
			}
		}
		return new Status(IStatus.OK, Activator.PLUGIN_ID,
				"Classes removed Undone");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.common.command.CompoundCommand#canUndo()
	 */
	@Override
	public boolean canUndo() {
		boolean needsResource = getAffectedDiagrams().size() > 0;
		return super.canUndo()
				&& (needsResource ? getDiagramsResource() != null : true)
				&& deleteCommands != null && deleteCommands.size() > 0 ? deleteCommands
				.canUndo()
				: true;
	}

	protected void addElementsToDiagram() {
		for (Object object : getElementsToDelete()) {
			if (object instanceof IGraphicalEditPart) {
				IGraphicalEditPart editPart = (IGraphicalEditPart) object;
				View editPartView = editPart.getNotationView();
				Diagram diagram = editPartView.getDiagram();
				EObject semanticElement = editPart.resolveSemanticElement();
				MultiDiagramUtil.AddEAnnotationReferenceToDiagram(diagram,
						semanticElement);
			}
		}
	}

	/**
	 * Creates the message.
	 * 
	 * @param diagrams
	 *            the diagrams
	 * 
	 * @return the string
	 */
	protected String createMessage(List<Diagram> diagrams) {
		if (diagrams.size() <= 0) {
			return "Some diagrams will be deleted";
		}
		if (diagrams.size() == 1) {
			return "This diagram:\n\t"
					+ MDTUtil.getDiagramName(diagrams.get(0))
					+ "\nwill be deleted";
		}
		String message = "These diagrams: ";
		Iterator<Diagram> iterator = diagrams.iterator();
		message += "\n\t" + MDTUtil.getDiagramName(iterator.next());
		while (iterator.hasNext()) {
			message += ",\n\t" + MDTUtil.getDiagramName(iterator.next());
		}
		message += "\nwill be deleted";
		return message;
	}

	/**
	 * A message that informs the user that the elements can't be deleted.
	 */
	protected void errorActiveDiagram() {
		MessageDialog.openInformation(PlatformUI.getWorkbench().getDisplay()
				.getActiveShell(), "Cannot delete elements",
				"A root active Diagram would be deleted");
	}

	public void setDeleteWithConfirmation(boolean delete) {
		this.deleteWithConfirmation = delete;
	}
}
