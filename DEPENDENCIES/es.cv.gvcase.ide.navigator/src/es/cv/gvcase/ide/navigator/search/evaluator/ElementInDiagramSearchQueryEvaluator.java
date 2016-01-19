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
package es.cv.gvcase.ide.navigator.search.evaluator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.search.core.engine.IModelSearchQuery;
import org.eclipse.emf.search.core.eval.IModelSearchQueryEvaluator;
import org.eclipse.gmf.runtime.emf.core.resources.GMFResource;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.View;

import es.cv.gvcase.ide.navigator.search.engine.ElementInDiagramSearchQuery;

/**
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 *
 * @param <Q>
 * @param <T>
 */
public class ElementInDiagramSearchQueryEvaluator<Q extends IModelSearchQuery, T>
		implements IModelSearchQueryEvaluator<Q, T> {

	public java.util.List<?> eval(Q query, T target, boolean notify) {
		List<Object> results = new ArrayList<Object>();
		if (query instanceof ElementInDiagramSearchQuery) {
			ElementInDiagramSearchQuery searchQuery = (ElementInDiagramSearchQuery) query;
			for (Object o : searchQuery.getModelSearchParameters()
					.getParticipantElements()) {
				if (o instanceof EObject && target instanceof GMFResource) {
					EObject element = (EObject) o;
					for (EObject content : ((Resource) target).getContents()) {
						if (content instanceof Diagram) {
							Diagram diagram = (Diagram) content;
							if (isEObjectInDiagram(element, diagram)) {
								results.add(searchQuery
										.processSearchResultMatching(target,
												diagram, notify));
							}
						}
					}
				}
			}
		}
		return results;
	};

	protected boolean isEObjectInDiagram(EObject eObject, Diagram diagram) {
		if (diagram.getElement() != null
				&& diagram.getElement().equals(eObject)) {
			return true;
		}
		Iterator iterator = diagram.eAllContents();
		for (; iterator.hasNext();) {
			Object o = iterator.next();
			if (o instanceof View) {
				View view = (View) o;
				if (view.getElement() != null
						&& view.getElement().equals(eObject)) {
					return true;
				}
			}
		}
		return false;
	}

	public String getLabel() {
		return "Message_from_ElementIndiagramSearchQueryEvaluator#getLabel()";
	}

}
