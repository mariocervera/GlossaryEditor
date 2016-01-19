/***************************************************************************
 * Copyright (c) 2010 Conselleria de Infraestructuras y Transporte,
 * Generalitat de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – initial API and implementation
 *
 ******************************************************************************/

package es.cv.gvcase.mdt.common.commands.diagram;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gmf.runtime.diagram.ui.actions.DiagramAction;
import org.eclipse.gmf.runtime.diagram.ui.commands.ICommandProxy;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.ui.IWorkbenchPage;

import es.cv.gvcase.mdt.common.commands.OpenAsDiagramCommand;
import es.cv.gvcase.mdt.common.ids.MOSKittEditorIDs;

/**
 * "Create diagram" context menu action. <br>
 * Allows creating a {@link Diagram} associated to a model element. <br>
 * By default, the created diagram will be opened in its default editor.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 */
public class CreateDiagramAction extends DiagramAction {

	/** The diagram kind. */
	private String diagramKind = null;

	private EObject selectedElement = null;

	private Resource diagramResource = null;

	private IGraphicalEditPart editPart = null;

	/**
	 * Instantiates a new creates the diagram action.
	 * 
	 * @param workbenchPage
	 *            the workbench page
	 * @param diagramKind
	 *            the diagram kind
	 */
	public CreateDiagramAction(IWorkbenchPage workbenchPage,
			String diagramKind, IGraphicalEditPart editPart, Resource resource) {
		super(workbenchPage);
		this.diagramKind = diagramKind;
		setText(diagramKind);
		this.editPart = editPart;
		this.selectedElement = editPart.resolveSemanticElement();
		this.diagramResource = resource;
	}

	public EObject getSelectedElement() {
		return selectedElement;
	}

	public Resource getDiagramResource() {
		return diagramResource;
	}

	public String getDiagramKind() {
		return diagramKind;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gmf.runtime.diagram.ui.actions.DiagramAction#createTargetRequest
	 * ()
	 */
	@Override
	protected Request createTargetRequest() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gmf.runtime.diagram.ui.actions.DiagramAction#isSelectionListener
	 * ()
	 */
	@Override
	protected boolean isSelectionListener() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gmf.runtime.diagram.ui.actions.DiagramAction#getCommand()
	 */
	@Override
	protected Command getCommand() {
		Resource resource = getDiagramResource();
		EObject selectedEObject = getSelectedElement();
		if (resource != null && selectedEObject != null) {
			Command command = new ICommandProxy(new OpenAsDiagramCommand(
					resource, selectedEObject, getDiagramKind()));
			return command;
		} else {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		return !EClassToDiagramRegistry.getInstance()
				.getDiagramsForEditPart(editPart).isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gmf.runtime.diagram.ui.actions.DiagramAction#refresh()
	 */
	@Override
	public void refresh() {
		super.refresh();
		setText(calculateText());
	}

	private String calculateText() {
		String label = MOSKittEditorIDs.getExtensionsMapModelToLabel().get(
				diagramKind);
		label = label != null ? label : diagramKind;
		label = "MSKT Create " + label + " diagram";
		return label;
	}

}
