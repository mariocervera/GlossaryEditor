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
package es.cv.gvcase.gvm.glossary.formseditor.filters;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Text;

import es.cv.gvcase.gvm.glossary.Term;

public class GlossaryFilter extends ViewerFilter {
	
	private Text searchText;
	
	public GlossaryFilter(Text text) {
		searchText = text;
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if(element instanceof Term && searchText != null) {
			Term term = (Term) element;
			if(term.getName() != null
					&& term.getName().contains(searchText.getText())) {
				return true;
			}
		}
		
		return false;
	}

}
