/*******************************************************************************
 * Copyright (c) 2010 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – Initial implementation
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.provider;

import java.util.Collection;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

/**
 * A common label provider that can handle collections of elements in the getText method.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 *
 */
public class MOSKittCommonLabelProvider implements ILabelProvider {

	ILabelProvider realLabelProvider = null;

	public MOSKittCommonLabelProvider(ILabelProvider realLabelProvider) {
		if (realLabelProvider != null) {
			this.realLabelProvider = realLabelProvider;
		} else {
			throw new IllegalArgumentException(
					"A real albel provider must be provided");
		}
	}

	public Image getImage(Object element) {
		return realLabelProvider.getImage(element);
	}

	public String getText(Object element) {
		if (element instanceof Collection) {
			Collection collection = (Collection) element;
			if (collection.size() == 1) {
				return realLabelProvider.getText(collection.iterator().next());
			}
		}
		return realLabelProvider.getText(element);
	}

	public void addListener(ILabelProviderListener listener) {
		realLabelProvider.addListener(listener);
	}

	public void dispose() {
	}

	public boolean isLabelProperty(Object element, String property) {
		return realLabelProvider.isLabelProperty(element, property);
	}

	public void removeListener(ILabelProviderListener listener) {
		realLabelProvider.removeListener(listener);
	}

}
