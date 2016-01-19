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

import org.eclipse.emf.search.core.factories.IModelSearchQueryParametersFactory;
import org.eclipse.emf.search.core.parameters.IModelSearchQueryParameters;

import es.cv.gvcase.ide.navigator.search.engine.ElementInDiagramSearchQueryParameters;

/**
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 *
 */
public class ElementInDiagramSearchQueryParametersFactory implements
		IModelSearchQueryParametersFactory {

	private static ElementInDiagramSearchQueryParametersFactory instance = null;
	
	public ElementInDiagramSearchQueryParametersFactory() {}
	
	public static ElementInDiagramSearchQueryParametersFactory getInstance() {
		return instance == null ? instance = new ElementInDiagramSearchQueryParametersFactory() : instance;
	}
	
	public IModelSearchQueryParameters createSearchQueryParameters() {
		return new ElementInDiagramSearchQueryParameters();
	}

}
