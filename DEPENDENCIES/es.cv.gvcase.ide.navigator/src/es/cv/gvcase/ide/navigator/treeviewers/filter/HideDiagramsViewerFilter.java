/*******************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) - initial api implementation
 * [02/04/08] Francisco Javier Cano Muñoz (Prodevelop) - adaptation to Common Navigator Framework
 *
 ******************************************************************************/
package es.cv.gvcase.ide.navigator.treeviewers.filter;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * Removes {@link Diagram} internal structure that should not be shown in the
 * model.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es>Francisco Javier Cano Muñoz</a>
 */
public class HideDiagramsViewerFilter extends ViewerFilter {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers
	 * .Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof Diagram) {
			if (!(parentElement instanceof Resource)) {
				return false;
			}
		}
		return true;
	}
}
