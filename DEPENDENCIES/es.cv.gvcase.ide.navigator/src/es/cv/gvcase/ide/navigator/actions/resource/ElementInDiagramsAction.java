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
package es.cv.gvcase.ide.navigator.actions.resource;

import java.util.Collections;

import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.search.core.engine.IModelSearchQuery;
import org.eclipse.gmf.runtime.diagram.ui.parts.IDiagramWorkbenchPart;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.SelectionListenerAction;

import es.cv.gvcase.ide.navigator.search.engine.ElementInDiagramSearchQuery;
import es.cv.gvcase.ide.navigator.search.engine.ElementInDiagramSearchQueryParameters;
import es.cv.gvcase.ide.navigator.search.evaluator.ElementInDiagramSearchQueryEvaluator;
import es.cv.gvcase.ide.navigator.search.scope.ElementInDiagramSearchScope;

/**
 * Action to search for references to a single {@link EObject} in the open editor's
 * {@link Diagram}s.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public class ElementInDiagramsAction extends SelectionListenerAction {

	public ElementInDiagramsAction() {
		super("Search references in diagrams");
	}

	protected EObject getEObject() {
		IStructuredSelection selection = getStructuredSelection();
		Object selected = selection.getFirstElement();
		if (selected instanceof EObject) {
			return ((EObject) selected);
		}
		return null;
	}

	@Override
	public void run() {
		super.run();

		NewSearchUI.runQueryInBackground(buildSearchQuery());

	}

	protected ElementInDiagramSearchQuery buildSearchQuery() {
		EObject element = getEObject();
		if (element == null) {
			return null;
		}
		IEditorPart editorPart = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		Resource resource = getDiagramResourceFromEditor(editorPart);
		ElementInDiagramSearchQueryParameters parameters = new ElementInDiagramSearchQueryParameters();
		parameters
				.setEvaluator(new ElementInDiagramSearchQueryEvaluator<IModelSearchQuery, Resource>());
		ElementInDiagramSearchScope<Object, Resource> scope = new ElementInDiagramSearchScope<Object, Resource>(
				"ActiveEditor");
		scope.setActiveEditor(editorPart);
		scope.setDiagramsResource(resource);
		parameters.setScope(scope);
		parameters.setParticipantElements(Collections.singletonList(element));
		ElementInDiagramSearchQuery query = new ElementInDiagramSearchQuery("",
				parameters);
		return query;
	}

	protected Resource getDiagramResourceFromEditor(IEditorPart editorPart) {
		if (editorPart != null) {
			IDiagramWorkbenchPart diagramEditor = (IDiagramWorkbenchPart) Platform
					.getAdapterManager().getAdapter(editorPart,
							IDiagramWorkbenchPart.class);
			if (diagramEditor != null) {
				Diagram diagram = diagramEditor.getDiagram();
				if (diagram != null) {
					Resource resource = diagram.eResource();
					if (resource != null) {
						return resource;
					}
				}
			}
		}
		return null;
	}

}
