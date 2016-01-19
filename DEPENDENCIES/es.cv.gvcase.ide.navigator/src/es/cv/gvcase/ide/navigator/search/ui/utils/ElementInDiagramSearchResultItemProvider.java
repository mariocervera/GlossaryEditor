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
package es.cv.gvcase.ide.navigator.search.ui.utils;

import java.util.Collection;
import java.util.Set;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.search.core.results.IModelResultEntry;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import es.cv.gvcase.ide.navigator.search.results.ElementInDiagramSearchResult;

/**
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 *
 */
public class ElementInDiagramSearchResultItemProvider implements
		ITreeContentProvider {
	private ElementInDiagramSearchResult input;

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof Resource) {
			return input.getRootResultHierarchies().get(
					(Resource) parentElement).toArray();
		} else if (parentElement instanceof IModelResultEntry) {
			return ((IModelResultEntry) parentElement).getResults().toArray();
		}
		return new Object[0];
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element) {
		if (element instanceof Resource) {
			return input;
		} else if (element instanceof IModelResultEntry) {
			return ((IModelResultEntry) element).getParent();
		}
		return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element) {
		if (element instanceof Resource) {
			return true;
		} else if (element instanceof IModelResultEntry) {
			return !((IModelResultEntry) element).getResults().isEmpty();
		}
		return false;
	}

	/**
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof ElementInDiagramSearchResult) {
			ElementInDiagramSearchResult input = (ElementInDiagramSearchResult) inputElement;
			Set<Object> files = input.getRootResultHierarchies().keySet();
			return files.toArray();
		} else if (inputElement instanceof Collection) {
			return ((Collection<?>) inputElement).toArray();
		}
		return new Object[0];
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
	 *      java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput instanceof ElementInDiagramSearchResult) {
			input = (ElementInDiagramSearchResult) newInput;
		}
	}

	public void dispose() {
	}

}
