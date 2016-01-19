/*******************************************************************************
 * Copyright (c) 2010 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – Initial implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.palette;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.UnexecutableCommand;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.tools.CreationTool;

import es.cv.gvcase.mdt.common.commands.CreateStructuredTemplateElementsCommand;
import es.cv.gvcase.mdt.common.util.MDTUtil;

/**
 * An specialization of {@link CreationTool} to create structured templates of
 * elements defined via the 'es.cv.gvcase.mdt.common.TemplateElementTool'
 * extension point.
 * 
 * @author <a href="mailto:fjcano@prodeelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public class TemplateCreationTool extends CreationTool {

	// //
	//
	// //

	/**
	 * The TemplateTool with the info of the elements to create.
	 */
	TemplateTool templateTool = null;

	/**
	 * Getter for TemplateTool
	 * 
	 * @return
	 */
	public TemplateTool getTemplateTool() {
		return templateTool;
	}

	/**
	 * Setter for TemplateTool
	 * 
	 * @param templateTool
	 */
	public void setTemplateTool(TemplateTool templateTool) {
		this.templateTool = templateTool;
	}

	/**
	 * Contruction
	 * 
	 * @param tool
	 */
	public TemplateCreationTool() {
	}

	/**
	 * Sets the TemplateTool on case it is given.
	 */
	@Override
	protected void applyProperty(Object key, Object value) {
		super.applyProperty(key, value);
		// look for a key in that has info about the TemplateTool
		if (TemplateToolRegistry.TemplateToolPropertyKey.equals(key)) {
			if (value instanceof TemplateTool) {
				setTemplateTool((TemplateTool) value);
			}
		}
	}

	// //
	// Utility
	// //

	/**
	 * Gets the EObject behind the target EditPart.
	 */
	protected EObject getTargetEObject() {
		EditPart editPart = getTargetEditPart();
		EObject eObject = MDTUtil.resolveSemantic(editPart);
		return eObject;
	}

	protected IGraphicalEditPart getGraphicalEditPart() {
		EditPart editPart = getTargetEditPart();
		if (editPart instanceof IGraphicalEditPart) {
			return (IGraphicalEditPart) editPart;
		}
		return null;
	}

	// //
	// Command that executes the creation of the structured elements.
	// //

	/**
	 * This command is a composition of several commands that take care of
	 * creating the structured template of elements.
	 */
	@Override
	protected Command getCommand() {
		// if no template tool was provided, nothing to do
		TemplateTool tool = getTemplateTool();
		if (tool == null) {
			return UnexecutableCommand.INSTANCE;
		}
		IGraphicalEditPart editPart = getGraphicalEditPart();
		if (editPart == null) {
			return UnexecutableCommand.INSTANCE;
		}
		return new CreateStructuredTemplateElementsCommand(editPart, tool)
				.toGEFCommand();
	}

}
