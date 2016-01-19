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

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.Viewer;

/**
 * Show only elements that are not shown in any diagram.
 * 
 * @author <a href="mailto:jmunoz@prodevelop.es">Javier Muñoz</a>
 */
public class InNoDiagramViewerFilter extends InDiagramViewerFilter {

	/**
	 * For an {@link EObject} will return if it is shown in the active diagram.
	 * 
	 * @param viewer
	 *            the viewer
	 * @param parentElement
	 *            the parent element
	 * @param element
	 *            the element
	 * 
	 * @return true, if select
	 */
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (!(element instanceof EObject)) {
			return true;
		}
		EObject eo = (EObject) element;

		boolean foundElementWithoutView = elementWithoutView(eo);
		 
		
		TreeIterator<EObject> iter = eo.eAllContents();
		while (iter.hasNext() && ! foundElementWithoutView){			
			EObject child = iter.next(); 
			foundElementWithoutView = foundElementWithoutView || elementWithoutView(child);			
		}
		
		return foundElementWithoutView;
	}
	
	public static boolean elementWithoutView(EObject eo){
		return getEObjectViews(eo).size() == 0;		 
	}

}
