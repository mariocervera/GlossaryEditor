/***************************************************************************
* Copyright (c) 2008 Conselleria de Infraestructuras y Transporte,
* Generalitat de la Comunitat Valenciana . All rights reserved. This program
* and the accompanying materials are made available under the terms of the
* Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors: Mario Cervera Ubeda (Integranova) - Initial API and implementation
*
**************************************************************************/
package es.cv.gvcase.gvm.glossary.formseditor.sorters;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import es.cv.gvcase.gvm.glossary.Term;

public class NameSorter extends ViewerSorter {

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		
		if(e1 instanceof Term && e2 instanceof Term) {
			String name1 = ((Term)e1).getName();
			String name2 = ((Term)e2).getName();
			if(name1 != null && name2 != null) {
				return name1.compareTo(name2);
			}
		}
		
		return super.compare(viewer, e1, e2);
	}
	
}
