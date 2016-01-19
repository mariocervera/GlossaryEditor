/*******************************************************************************
 * Copyright (c) 2010 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana. All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Marc Gil Sendra (Prodevelop) - initial API implementation
 * 	Miguel Llacer San Fernando (Prodevelop)
 *  	- migration from es.cv.gvcase.mdt.common to es.cv.gvcase.emf.common
 *
 ******************************************************************************/
package es.cv.gvcase.emf.ui.common.providers;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;

/**
 * Wrapper to adapt the ArrayContentProvider to a TreeViewer
 * 
 * @author <a href="david.sciamma@anyware-tech.com">David Sciamma</a>
 */
public class TreeArrayContentProvider extends ArrayContentProvider implements
		ITreeContentProvider {

	public Object[] getChildren(Object parentElement) {
		return new Object[0];
	}

	public Object getParent(Object element) {
		return null;
	}

	public boolean hasChildren(Object element) {
		return false;
	}

}
