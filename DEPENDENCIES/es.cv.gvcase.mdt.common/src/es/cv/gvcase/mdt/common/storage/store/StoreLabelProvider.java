/*******************************************************************************
 * Copyright (c) 2011 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana. All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Marc Gil Sendra (Prodevelop) [mgil@prodevelop.es] - initial API implementation
 ******************************************************************************/
package es.cv.gvcase.mdt.common.storage.store;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import es.cv.gvcase.mdt.common.util.MDTUtil;

/**
 * This class is a Label Provider for the Storage Elements
 * 
 * @author <a href="mailto:mgil@prodevelop.es">Marc Gil Sendra</a>
 */
public class StoreLabelProvider implements ILabelProvider {

	public Image getImage(Object element) {
		if (element instanceof StorageCategory) {
			return PlatformUI.getWorkbench().getSharedImages().getImage(
					ISharedImages.IMG_OBJ_FOLDER);
		}
		return MDTUtil.getLabelProvider().getImage(element);
	}

	public String getText(Object element) {
		if (element instanceof StorageCategory) {
			StorageCategory category = (StorageCategory) element;
			if (category.category == null || category.category.length() <= 0) {
				return IStorableElement.DefaultCategoryString;
			} else {
				return category.category;
			}
		}
		if (element instanceof IStorableElement) {
			IStorableElement storable = (IStorableElement) element;
			return storable.getName() + " | " + storable.getDescription(); //$NON-NLS-1$
		} else {
			return MDTUtil.getLabelProvider().getText(element);
		}
	}

	public void addListener(ILabelProviderListener listener) {

	}

	public void dispose() {

	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {

	}

}