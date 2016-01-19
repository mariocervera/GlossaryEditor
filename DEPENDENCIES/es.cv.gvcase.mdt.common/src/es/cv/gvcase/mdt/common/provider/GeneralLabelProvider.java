/*******************************************************************************
 * Copyright (c) 2009 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – Initial implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.provider;

import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import es.cv.gvcase.mdt.common.util.MDTUtil;

/**
 * An implementation of {@link ILabelProvider} that can handle Arrays and
 * Collections for both images and texts. Delegates the text and image handling
 * to the default {@link ComposedAdapterFactory}.
 * 
 * <b>Please, use the GeneralLabelProvider provided in es.cv.gvcase.emf.common plugin instead of this.</b>
 * 
 * @author <a href="fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
@Deprecated
public class GeneralLabelProvider implements ILabelProvider {

	ILabelProvider labelProvider = MDTUtil.getLabelProvider();

	/**
	 * Gets the image for the given element. If the given element is an Array or
	 * a Collection the first element will be taken for the image.
	 */
	public Image getImage(Object element) {
		if (element instanceof Object[]) {
			Object[] elements = (Object[]) element;
			if (elements.length > 0) {
				return getImage(elements[0]);
			}
		} else if (element instanceof List) {
			List elements = (List) element;
			return getImage(elements.get(0));
		}
		return labelProvider.getImage(element);
	}

	/**
	 * Gets the text for the given element. If the given element is an Array or
	 * a Collection, a comma separated String will be built with the text of
	 * each element.
	 */
	public String getText(Object element) {
		String text = "";
		if (element instanceof Object[]) {
			Object[] elements = (Object[]) element;
			if (elements.length > 0) {
				String localText = null;
				for (int i = 0; i < elements.length; i++) {
					localText = getText(elements[i]);
					if (localText != null) {
						text += localText;
					}
					if (i < elements.length - 1) {
						text += ", ";
					}
				}
			}
		} else if (element instanceof List) {
			List elements = (List) element;
			String localText = null;
			for (Iterator it = elements.iterator(); it.hasNext(); /**/) {
				localText = getText(it.next());
				if (localText != null) {
					text += localText;
				}
				if (it.hasNext()) {
					text += ", ";
				}
			}
			return text;
		}
		return labelProvider.getText(element);
	}

	public void addListener(ILabelProviderListener listener) {
		labelProvider.addListener(listener);
	}

	public void dispose() {
		// nothing to do.
	}

	public boolean isLabelProperty(Object element, String property) {
		return labelProvider.isLabelProperty(element, property);
	}

	public void removeListener(ILabelProviderListener listener) {
		labelProvider.removeListener(listener);
	}

}
