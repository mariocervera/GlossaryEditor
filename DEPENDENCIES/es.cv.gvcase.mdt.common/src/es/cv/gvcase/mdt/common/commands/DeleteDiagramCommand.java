/*******************************************************************************
 * Copyright (c) 2008, 2009 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) - initial API implementation
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.commands;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.command.AbstractCommand;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.gmf.runtime.diagram.ui.parts.DiagramEditor;
import org.eclipse.gmf.runtime.emf.core.resources.GMFResource;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

import es.cv.gvcase.mdt.common.Activator;
import es.cv.gvcase.mdt.common.part.MOSKittMultiPageEditor;
import es.cv.gvcase.mdt.common.util.MDTUtil;
import es.cv.gvcase.mdt.common.util.MultiDiagramUtil;

/**
 * Deletes a {@link Diagram} from a GMF resource. First all editors showing the
 * Diagram are closed, then the Diagram is removed from the {@link GMFResource}.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 */
public class DeleteDiagramCommand extends AbstractCommand {

	/** {@link Diagram} to delete. */
	private Diagram diagramToDelete = null;

	/** {@link Resource} to be edited; usually a {@link GMFResource} */
	private Resource resourceToEdit = null;

	/** {@link Diagram} to delete */
	public Diagram getDiagramToDelete() {
		return diagramToDelete;
	}

	/** {@link Resource} to be edited */
	public Resource getResourceToEdit() {
		return resourceToEdit;
	}

	/**
	 * Instantiates a new delete {@link Diagram} command.
	 * 
	 * @param diagram
	 *            the diagram
	 */
	public DeleteDiagramCommand(Diagram diagram) {
		this.diagramToDelete = diagram;
		if (diagram != null) {
			resourceToEdit = diagram.eResource();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.common.command.AbstractCommand#canExecute()
	 */
	@Override
	public boolean canExecute() {
		return getDiagramToDelete() != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.common.command.AbstractCommand#canUndo()
	 */
	@Override
	public boolean canUndo() {
		// fjcano :: was return false;
		return getDiagramToDelete() != null;
	}

	/**
	 * Will close all open editors that are showing this {@link Diagram}, then
	 * delete the diagram from the {@link GMFResource} containing it.
	 */
	public void execute() {
		// delegate to redo();
		redo();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.common.command.Command#redo()
	 */
	public void redo() {
		if (getDiagramToDelete() == null) {
			return;
		}
		// Get upper diagram to open in case the one deleted is active.
		Diagram diagramToOpen = MultiDiagramUtil
				.getUpperDiagram(getDiagramToDelete());
		if (diagramToOpen == null || diagramToOpen.equals(getDiagramToDelete())) {
			// This is the uppest diagram we'll look for a diagram at the same
			// level
			diagramToOpen = MultiDiagramUtil
					.getOtherDiagram(getDiagramToDelete());
			if (diagramToOpen == null) {
				// no suitable diagram to open
				return;
			}
		}

		// The diagram is Ok to be deleted. Ask user confirmation.
		MessageDialog confirmDialog = new MessageDialog(Display.getCurrent()
				.getActiveShell(), "Delete diagram?", null,
				"Are you sure you want to delete the selected diagram?",
				MessageDialog.WARNING, new String[] { "Yes", "No" }, 1);
		int result = confirmDialog.open();
		if (result == Window.CANCEL) {
			return;
		}

		// modify the parent diagram of the diagrams that has this deleted
		// diagram as parent
		boolean hasParent = getDiagramToDelete().getEAnnotation(
				MultiDiagramUtil.UpperDiagram) == null ? false : true;
		for (Resource r : diagramToOpen.eResource().getResourceSet()
				.getResources()) {
			if (r instanceof GMFResource) {
				for (EObject eo : r.getContents()) {
					if (eo instanceof Diagram) {
						Diagram son = (Diagram) eo;
						EAnnotation eAnnotation = son
								.getEAnnotation(MultiDiagramUtil.UpperDiagram);
						if (eAnnotation != null
								&& eAnnotation.getReferences().size() > 0
								&& (eAnnotation.getReferences().get(0) instanceof Diagram)) {
							Diagram parent = (Diagram) eAnnotation
									.getReferences().get(0);
							if (parent.equals(getDiagramToDelete())) {
								if (!hasParent) {
									// remove the eAnnotation
									son.getEAnnotations().remove(
											MultiDiagramUtil.UpperDiagram);
									if (diagramToOpen != null)
										diagramToOpen = son;
								} else {
									// change the parent diagram
									Diagram parentDiagram = (Diagram) getDiagramToDelete()
											.getEAnnotation(
													MultiDiagramUtil.UpperDiagram)
											.getReferences().get(0);
									eAnnotation.getReferences().clear();
									eAnnotation.getReferences().add(
											parentDiagram);
								}
							}
						}
					}
				}
			}
		}

		IEditorPart editorPart = MDTUtil.getActiveEditor();
		if (!isDiagramActive()) {
			// If the diagram to delete is not active it can be deleted without
			// problems.
			MultiDiagramUtil.deleteDiagramAndSave(getDiagramToDelete(),
					!(editorPart instanceof MOSKittMultiPageEditor));
		} else {
			// If the diagram to delete is active, a complex process must be
			// followed to delete it.
			// Close all diagram editors that have the diagram to be deleted
			// active.
			// EditingDomainRegistry.getInstance().setChangingCachedEditors(true);
			MultiDiagramUtil.closeEditorsThatShowDiagram(getDiagramToDelete());
			// Delete diagram
			MultiDiagramUtil.deleteDiagramAndSave(getDiagramToDelete(),
					!(editorPart instanceof MOSKittMultiPageEditor));
			// Open its upper diagram
			try {
				MultiDiagramUtil.openDiagram(diagramToOpen);
			} catch (ExecutionException ex) {
				IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
						"Can't open diagram");
				Activator.getDefault().getLog().log(status);
			} finally {
				// EditingDomainRegistry.getInstance().setChangingCachedEditors(
				// false);
				return;
			}
		}
	}

	/**
	 * Restore the {@link Diagram} to its {@link Resource}.
	 */
	@Override
	public void undo() {
		if (getDiagramToDelete() != null && getResourceToEdit() != null) {
			// if we have the deleted diagram and the resource it was deleted
			// from, we can restore it.
			boolean save = !(MDTUtil.getActiveEditor() instanceof MOSKittMultiPageEditor);
			MultiDiagramUtil.addDiagramAndSave(getDiagramToDelete(),
					getResourceToEdit(), save);
			try {
				// EditingDomainRegistry.getInstance().setChangingCachedEditors(
				// true);
				MultiDiagramUtil.openDiagram(getDiagramToDelete(), true);
			} catch (ExecutionException ex) {
				IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
						"Can't open diagram");
				Activator.getDefault().getLog().log(status);
			} finally {
				// EditingDomainRegistry.getInstance().setChangingCachedEditors(
				// false);
				return;
			}
		}
	}

	/**
	 * Returns true if the active editor {@link Diagram} is the one to delete.
	 * 
	 * @return true, if checks if is diagram active
	 */
	protected boolean isDiagramActive() {
		IEditorPart activeEditor = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (activeEditor instanceof DiagramEditor) {
			DiagramEditor diagramEditor = (DiagramEditor) activeEditor;
			Diagram activeDiagram = diagramEditor.getDiagram();
			if (getDiagramToDelete().equals(activeDiagram)) {
				return true;
			}
		}
		return false;
	}
}
