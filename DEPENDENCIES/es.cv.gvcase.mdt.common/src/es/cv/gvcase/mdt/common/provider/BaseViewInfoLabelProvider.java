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

import org.eclipse.core.runtime.Platform;
import org.eclipse.gmf.runtime.emf.type.core.ElementTypeRegistry;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

/**
 * An implementation of {@link IBaseLabelProvider} and {@link ILabelProvider}
 * for {@link ViewInfo} elements.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * @NOT-generated
 */
public class BaseViewInfoLabelProvider implements IBaseLabelProvider,
		ILabelProvider {

	/**
	 * Can get images from a {@link BaseViewInfo}.
	 * 
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object element) {
		BaseViewInfo baseViewInfo = (BaseViewInfo) Platform.getAdapterManager()
				.getAdapter(element, BaseViewInfo.class);
		if (baseViewInfo != null && baseViewInfo.rootViewInfo != null
				&& baseViewInfo.rootViewInfo.getModelTypesProvider() != null) {
			IElementType hint = ElementTypeRegistry.getInstance().getType(
					baseViewInfo.elementType);
			if (hint != null) {
				return baseViewInfo.rootViewInfo.getModelTypesProvider()
						.getImageHelper(hint);
			}
		}
		return null;
	}

	/**
	 * Can get label text from a {@link BaseViewInfo}.
	 * 
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object element) {
		BaseViewInfo baseViewInfo = (BaseViewInfo) Platform.getAdapterManager()
				.getAdapter(element, BaseViewInfo.class);
		if (baseViewInfo != null && baseViewInfo.rootViewInfo != null
				&& baseViewInfo.rootViewInfo.getModelTypesProvider() != null) {
			if (baseViewInfo.label != null && baseViewInfo.label.length() > 0) {
				return baseViewInfo.label;
			}
			IElementType hint = ElementTypeRegistry.getInstance().getType(
					baseViewInfo.elementType);
			if (hint != null) {
				return baseViewInfo.rootViewInfo.getModelTypesProvider()
						.getTextHelper(hint);
			}
		}
		else if (baseViewInfo != null && baseViewInfo.label != null) {
			return baseViewInfo.label;
		}
		return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.
	 *      jface.viewers.ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener listener) {
		// nothing to do
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	public void dispose() {
		// nothing to do
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang
	 *      .Object, java.lang.String)
	 */
	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse
	 *      .jface.viewers.ILabelProviderListener)
	 */
	public void removeListener(ILabelProviderListener listener) {
		// nothing to do
	}

}
