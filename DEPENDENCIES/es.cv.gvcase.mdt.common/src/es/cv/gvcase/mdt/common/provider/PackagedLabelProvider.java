/*******************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Javier Muñoz (Integranova) – Initial implementation
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.provider;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import es.cv.gvcase.mdt.common.sections.composites.CSingleObjectChooser;

public class PackagedLabelProvider implements ILabelProvider {

	ILabelProvider wrappedProvider;

	public PackagedLabelProvider(ILabelProvider provider) {
		this.wrappedProvider = provider;
	}

	public Image getImage(Object element) {
		if (!(element instanceof PackagingNode)) {
			return this.wrappedProvider.getImage(element);
		} else {
			PackagingNode node = (PackagingNode) element;
			if (node.getContainedNodes().isEmpty()) {
				return this.wrappedProvider.getImage(element);
			} else if (((List<Object>) node.getContainedNodes()).get(0) instanceof CSingleObjectChooser.NullObject) {
				return this.wrappedProvider.getImage(element);
			} else {
				return this.wrappedProvider
						.getImage(((EObject) ((List<Object>) node
								.getContainedNodes()).get(0)).eContainer());
			}
		}
	}

	public String getText(Object element) {
		if (element instanceof PackagingNode) {
			return "< " + ((PackagingNode) element).getName() + " >";
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
		if (element instanceof PackagingNode) {
			return property.equals("name");
		} else {
			return this.wrappedProvider.isLabelProperty(element, property);
		}
	}

	public void removeListener(ILabelProviderListener listener) {
		this.wrappedProvider.removeListener(listener);
	}

}
