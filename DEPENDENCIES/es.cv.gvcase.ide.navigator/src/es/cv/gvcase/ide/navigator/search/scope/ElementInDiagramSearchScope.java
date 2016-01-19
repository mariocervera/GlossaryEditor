/***************************************************************************
 * Copyright (c) 2008, 2009 Conselleria de Infraestructuras y Transporte,
 * Generalitat de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – initial api implementation
 *
 ******************************************************************************/
package es.cv.gvcase.ide.navigator.search.scope;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.search.core.scope.IModelSearchScope;
import org.eclipse.gmf.runtime.diagram.ui.parts.IDiagramWorkbenchPart;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.ui.IEditorPart;

/**
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 * @param <P>
 * @param <O>
 */
public class ElementInDiagramSearchScope<P, O> implements
		IModelSearchScope<P, O> {

	private static ElementInDiagramSearchScope<?, Resource> instance = null;

	/**
	 * Model Search scope label
	 */
	private String kindLabel;

	private IEditorPart activeEditor = null;

	public IEditorPart getActiveEditor() {
		return activeEditor;
	}

	public void setActiveEditor(IEditorPart activeEditor) {
		this.activeEditor = activeEditor;
	}

	private Resource diagramsResource = null;

	public Resource getDiagramsResource() {
		return diagramsResource;
	}

	public void setDiagramsResource(Resource diagramsResource) {
		this.diagramsResource = diagramsResource;
	}

	public ElementInDiagramSearchScope(String label) {
		this.kindLabel = label;
	}

	public void addParticipant(P resource) {
		// nothing to do
		return;
	}

	public void addParticipants(P[] resources) {
		// nothing to do
		return;
	}

	public List<P> findPartcipant(Class<O> clazz) {
		ArrayList<P> subList = new ArrayList<P>();
		for (P participant : getParticipants()) {
			if (participant.getClass().equals(clazz)) {
				subList.add(participant);
			}
		}
		return subList;
	}

	public String getLabel() {
		return kindLabel;
	}

	/**
	 * The participant is the {@link Resource} where references inside
	 * {@link Diagram}s will be searched.
	 */
	public List<P> getParticipants() {
		List<P> resources = new ArrayList<P>();
		// Return the Resources if already known.
		Resource diagramResource = getDiagramsResource();
		if (diagramResource != null) {
			resources.add((P) diagramResource);
			return resources;
		}
		// Return the resources from the Active Editor
		IEditorPart activeEditor = getActiveEditor();
		if (activeEditor == null) {
			return Collections.emptyList();
		}
		if (activeEditor instanceof IDiagramWorkbenchPart) {
			IDiagramWorkbenchPart diagramEditor = (IDiagramWorkbenchPart) activeEditor;
			Diagram diagram = diagramEditor.getDiagram();
			if (diagram != null) {
				resources.add((P) (diagram.eResource()));
				return resources;
			}
		}
		return Collections.emptyList();
	}

	public void removeParticipant(P resource) {
		// nothing to do
		return;
	}

	public void removeParticipants(P[] resources) {
		// nothing to do
		return;
	}

	public static ElementInDiagramSearchScope<?, Resource> getInstance() {
		if (instance == null) {
			instance = new ElementInDiagramSearchScope<Object, Resource>(
					"ActiveDiagramEditor");
		}
		return instance;
	}

}
