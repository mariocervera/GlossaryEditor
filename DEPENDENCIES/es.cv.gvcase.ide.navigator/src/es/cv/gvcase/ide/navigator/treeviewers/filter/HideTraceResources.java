/*******************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Javier Muñoz (Integranova) - initial api implementation
 * 
 *
 ******************************************************************************/
package es.cv.gvcase.ide.navigator.treeviewers.filter;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * Removes resources with .gvcase extension
 * 
 * @author <a href="mailto:jmunoz@prodevelop.es">Javier Muñoz</a>
 */
public class HideTraceResources extends ViewerFilter {

	private final static String traceExtension = "gvtrace";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers
	 * .Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof IFile) {
			if (((IFile) element).getFileExtension() != null
					&& ((IFile) element).getFileExtension().equals(
							traceExtension)) {
				return false;
			}
		}
		return true;
	}
}
