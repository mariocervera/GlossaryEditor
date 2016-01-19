/*******************************************************************************
 * Copyright (c) 2010 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Marc Gil Sendra (Prodevelop) – Initial implementation.
 ******************************************************************************/
package es.cv.gvcase.mdt.common.model;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gmf.runtime.notation.Diagram;

/**
 * A Element Wrapper Class to be used in Runnable Hooks in the diagram creation
 * of the given element. After Runnable class is executed, the diagram attribute
 * should contains the new created diagram.
 * 
 * @author mgil
 */
public class CreateDiagramElementWrapper {
	/**
	 * The element of the diagram
	 */
	private EObject element;

	/**
	 * The new created diagram
	 */
	private Diagram diagram;

	/**
	 * The diagram kind
	 */
	private String diagramKind;

	/**
	 * A possible name for the diagram
	 */
	private String diagramName;

	/**
	 * Set if ask user about the name of the diagram
	 */
	private boolean askDiagramName;

	private boolean proceedWithCreationAndOpening;

	/**
	 * Constructor for the Element Wrapper to create diagram for the given
	 * eObject, of the indicated diagram kind, with the given name or indicating
	 * if the user should be asked about its name
	 * 
	 * @param eObject
	 * @param diagramKind
	 * @param diagramName
	 * @param askDiagramName
	 */
	public CreateDiagramElementWrapper(EObject eObject, String diagramKind,
			String diagramName, boolean askDiagramName) {
		this.element = eObject;
		this.diagramKind = diagramKind;
		if (diagramName != null || diagramName != "") {
			this.diagramName = diagramName;
		} else {
			this.diagramName = "";
		}
		this.askDiagramName = askDiagramName;

		this.diagram = null;
		this.proceedWithCreationAndOpening = true;
	}

	public Diagram getDiagram() {
		return diagram;
	}

	public void setDiagram(Diagram diagram) {
		this.diagram = diagram;
	}

	public EObject getElement() {
		return element;
	}

	public String getDiagramKind() {
		return diagramKind;
	}

	public String getDiagramName() {
		return diagramName;
	}

	public void setDiagramName(String name) {
		this.diagramName = name;
	}

	public boolean getAskDiagramName() {
		return askDiagramName;
	}

	public void setAskDiagramName(boolean askDiagramName) {
		this.askDiagramName = askDiagramName;
	}

	public boolean proceedWithCreationAndOpening() {
		return proceedWithCreationAndOpening;
	}

	public void setProceedWithCreationAndOpening(boolean proceed) {
		this.proceedWithCreationAndOpening = proceed;
	}
}
