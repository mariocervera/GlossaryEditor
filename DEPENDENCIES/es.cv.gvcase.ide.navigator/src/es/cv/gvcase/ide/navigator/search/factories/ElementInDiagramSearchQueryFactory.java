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
package es.cv.gvcase.ide.navigator.search.factories;

import org.eclipse.emf.search.core.engine.IModelSearchQuery;
import org.eclipse.emf.search.core.factories.IModelSearchQueryFactory;
import org.eclipse.emf.search.core.parameters.IModelSearchQueryParameters;

import es.cv.gvcase.ide.navigator.search.engine.ElementInDiagramSearchQuery;

/**
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 *
 */
public class ElementInDiagramSearchQueryFactory implements
		IModelSearchQueryFactory {

	private static ElementInDiagramSearchQueryFactory instance = null;
	
	public ElementInDiagramSearchQueryFactory() {}
	
	public static ElementInDiagramSearchQueryFactory getInstance() {
		return instance == null ? instance = new ElementInDiagramSearchQueryFactory() : instance;
	}
	
	public IModelSearchQuery createModelSearchQuery(String expr,
			IModelSearchQueryParameters p) {
		// TODO: this kind of search query is not intended to be created by this factory.
		return new ElementInDiagramSearchQuery(expr, p);
	}

}
