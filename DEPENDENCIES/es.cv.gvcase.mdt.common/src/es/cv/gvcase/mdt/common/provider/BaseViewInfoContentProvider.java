/*******************************************************************************
 * Copyright (c) 2009 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – Initial implementation
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.provider;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * An implementation of {@link ITreeContentProvider} for {@link ViewInfo} elements.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * @NOT-generated
 */
public class BaseViewInfoContentProvider implements ITreeContentProvider {

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.
	 *      Object)
	 */
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof ViewInfo) {
			ViewInfo viewInfo = (ViewInfo) parentElement;
			return viewInfo.getChildren().toArray();
		} else if (parentElement instanceof RootViewInfo) {
			return getChildren(((RootViewInfo) parentElement).headViewInfo);
		}
		return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object
	 *      )
	 */
	public Object getParent(Object element) {
		if (element instanceof ViewInfo) {
			ViewInfo viewInfo = (ViewInfo) element;
			return viewInfo.getParent();
		}
		return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.
	 *      Object)
	 */
	public boolean hasChildren(Object element) {
		if (element instanceof ViewInfo) {
			ViewInfo viewInfo = (ViewInfo) element;
			return viewInfo.getChildren().size() > 0;
		}
		return false;
	}

	/**
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java
	 *      .lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
		// nothing to do
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface
	 *      .viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// nothing to do
	}

}
