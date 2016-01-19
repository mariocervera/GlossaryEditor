/*******************************************************************************
 * Copyright (c) 2010 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) - initial api implementation
 *
 ******************************************************************************/
package es.cv.gvcase.ide.navigator.treeviewers.filter;

import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import es.cv.gvcase.mdt.common.util.MDTUtil;

/**
 * Filter to hide those elements that have been marked with the EAnnotation to
 * hide them from content providers.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public class HideFromContentProviderFilter extends ViewerFilter {

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof EModelElement) {
			EModelElement eModelElement = (EModelElement) element;
			if (eModelElement
					.getEAnnotation(MDTUtil.HideFromContentProvidersEAnnotationSource) != null) {
				return false;
			}
		}
		return true;
	}

}
