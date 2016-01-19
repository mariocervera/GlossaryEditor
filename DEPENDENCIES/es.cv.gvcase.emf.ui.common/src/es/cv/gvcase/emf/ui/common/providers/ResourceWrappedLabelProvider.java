/*******************************************************************************
 * Copyright (c) 2010 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana. All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Miguel Llacer San Fernando (Prodevelop S.L.) – Initial implementation,
 * 		migration from es.cv.gvcase.mdt.common to es.cv.gvcase.emf.common
 *
 ******************************************************************************/
package es.cv.gvcase.emf.ui.common.providers;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import es.cv.gvcase.emf.common.util.EMFUtil;

public class ResourceWrappedLabelProvider implements ILabelProvider {

	ILabelProvider wrappedProvider;

	public ResourceWrappedLabelProvider(ILabelProvider provider) {
		this.wrappedProvider = provider;
	}

	public Image getImage(Object element) {
		if (element instanceof Resource) {
			return EMFUtil.getAdapterFactoryLabelProvider().getImage(element);
		} else {
			return this.wrappedProvider.getImage(element);
		}
	}

	public String getText(Object element) {
		if (element instanceof Resource) {
			return EMFUtil.getAdapterFactoryLabelProvider().getText(element);
		} else {
			return this.wrappedProvider.getText(element);
		}
	}

	public void addListener(ILabelProviderListener listener) {
		this.wrappedProvider.addListener(listener);
	}

	public void dispose() {
		this.wrappedProvider.dispose();
	}

	public boolean isLabelProperty(Object element, String property) {
		if (element instanceof Resource) {
			return property.equals("uri");
		} else {
			return this.wrappedProvider.isLabelProperty(element, property);
		}
	}

	public void removeListener(ILabelProviderListener listener) {
		this.wrappedProvider.removeListener(listener);
	}

}
