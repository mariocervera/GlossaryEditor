/*******************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – Initial implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.edit.policies;

import java.util.Collections;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.OpenEditPolicy;
import org.eclipse.gmf.runtime.notation.Diagram;

import es.cv.gvcase.mdt.common.commands.OpenAsDiagramCommand;
import es.cv.gvcase.mdt.common.provider.IDiagramInitializer;
import es.cv.gvcase.mdt.common.util.MDTUtil;

/**
 * A policy to create new diagrams. Can initialize the diagram to create. The
 * new diagram can be opened.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public class CreateDiagramEditPolicy extends OpenEditPolicy {

	private String diagramKind = null;

	private Map<String, IDiagramInitializer> initializer = null;

	private boolean openCreatedDiagram = true;

	/**
	 * Simple constructor. Will create a new diagram of specified type. The
	 * diagram will not be initialized. The diagram will be opened in a new
	 * editor.
	 * 
	 * @param diagramKind
	 */
	public CreateDiagramEditPolicy(String diagramKind) {
		this(diagramKind, (IDiagramInitializer) null);
	}

	/**
	 * Simple constructor. Will create a new diagram of specified type. The
	 * diagram will not be initialized. The diagram will be opened in a new
	 * editor if specified.
	 * 
	 * @param diagramKind
	 * @param openNewDiagram
	 */
	public CreateDiagramEditPolicy(String diagramKind, boolean openNewDiagram) {
		this(diagramKind, (IDiagramInitializer) null);
	}

	/**
	 * Simple constructor. Will create a new diagram of specified type. The
	 * diagram will be initialized as specified by the initializer. The diagram
	 * will be opened in a new editor.
	 * 
	 * @param diagramKind
	 * @param initializer
	 */
	public CreateDiagramEditPolicy(String diagramKind,
			IDiagramInitializer initializer) {
		this(diagramKind, Collections.singletonMap(diagramKind, initializer),
				true);
	}

	/**
	 * Simple constructor. Will create a new diagram of specified type. The
	 * diagram will be initialized as specified by the initializer. The diagram
	 * will be opened in a new editor if specified.
	 * 
	 * @param diagramKind
	 * @param initializer
	 * @param openNewDiagram
	 */
	public CreateDiagramEditPolicy(String diagramKind,
			IDiagramInitializer initializer, boolean openNewDiagram) {
		this(diagramKind, Collections.singletonMap(diagramKind, initializer),
				openNewDiagram);
	}

	/**
	 * Simple constructor. Will create a new diagram of specified type. The
	 * diagram will be initialized as specified by the initializer. The diagram
	 * will be opened in a new editor if specified.
	 * 
	 * @param diagramKind
	 * @param initializers
	 * @param openNewDiagram
	 */
	public CreateDiagramEditPolicy(String diagramKind,
			Map<String, IDiagramInitializer> initializers,
			boolean openNewDiagram) {
		this.diagramKind = diagramKind;
		if (initializers != null) {
			this.initializer = initializers;
		} else {
			this.initializer = Collections.emptyMap();
		}
		this.openCreatedDiagram = openNewDiagram;
	}

	/**
	 * The command returned will create a new diagram of type diagramKind. That
	 * diagram will be initialized if an IDiagramInitializer was specified. That
	 * diagram will be opened in a new editor if that option was specified.
	 */
	@Override
	protected Command getOpenCommand(Request request) {
		if (getElement() != null && getResource() != null) {
			OpenAsDiagramCommand command = new OpenAsDiagramCommand(
					getResource(), getElement(), getDiagramKind(),
					getInitializer());
			command.setOpenNewDiagram(isOpenCreatedDiagram());
			return command.toGEFCommand();
		}
		return null;
	}

	/**
	 * Provides the resource in which to create the new diagram.
	 * 
	 * @return
	 */
	protected Resource getResource() {
		Diagram diagram = MDTUtil.getHostDiagram(this);
		if (diagram != null) {
			return diagram.eResource();
		}
		return null;
	}

	/**
	 * Provides the semantic element of the EditPart that hosts this policy.
	 * 
	 * @return
	 */
	protected EObject getElement() {
		return MDTUtil.resolveSemantic(getHost());
	}

	/**
	 * Gets the diagram kind of the diagram to create.
	 * 
	 * @return
	 */
	protected String getDiagramKind() {
		return diagramKind;
	}

	/**
	 * Gets the initializer for the diagram to create.
	 * 
	 * @return
	 */
	protected Map<String, IDiagramInitializer> getInitializer() {
		return initializer;
	}

	/**
	 * Gets whether the created diagram must be opened in a new editor.
	 * 
	 * @return
	 */
	protected boolean isOpenCreatedDiagram() {
		return openCreatedDiagram;
	}

}
