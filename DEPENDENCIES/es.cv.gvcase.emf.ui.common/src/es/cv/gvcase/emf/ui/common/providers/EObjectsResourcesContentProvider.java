/*******************************************************************************
 * Copyright (c) 2009-2010 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana. All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Javier Muñoz (Integranova) - Initial implementation
 * 	Miguel Llacer San Fernando (Prodevelop S.L.)
 * 		- Migration from es.cv.gvcase.mdt.common to es.cv.gvcase.emf.common
 *
 ******************************************************************************/
package es.cv.gvcase.emf.ui.common.providers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class EObjectsResourcesContentProvider extends ArrayContentProvider
		implements ITreeContentProvider {

	protected ITreeContentProvider wrappedProvider;
	protected Object[] input = null;

	public EObjectsResourcesContentProvider(ITreeContentProvider provider) {
		wrappedProvider = provider;
	}

	public Object[] getElements(Object inputElement) {
		HashMap<Resource, Resource> resourcesList = new HashMap<Resource, Resource>();
		Object[] inputArray = null;

		if (inputElement instanceof List<?>) {
			inputArray = ((List<?>) inputElement).toArray();
		} else if (inputElement instanceof Object[]) {
			inputArray = (Object[]) inputElement;
		} else if (inputElement instanceof Object) {
			inputArray = new Object[] { inputElement };
		} else {
			return null;
		}

		for (Object o : inputArray) {
			if (o instanceof EObject) {
				Resource r = ((EObject) o).eResource();
				if (!resourcesList.containsKey(r)) {
					resourcesList.put(r, r);
				}
			}
		}

		return resourcesList.keySet().toArray();
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput instanceof List<?>) {
			input = ((List<?>) newInput).toArray();
		} else if (newInput instanceof Object[]) {
			input = (Object[]) newInput;
		} else if (newInput instanceof Object) {
			input = new Object[] { newInput };
		}
		wrappedProvider.inputChanged(viewer, oldInput, newInput);
	}

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof Resource) {
			List<Object> children = new ArrayList<Object>();
			for (Object o : input) {
				if (o instanceof EObject) {
					if (((EObject) o).eResource().equals(parentElement)) {
						children.add(o);
					}
				}
			}
			return wrappedProvider.getElements(children.toArray());
		}
		return wrappedProvider.getChildren(parentElement);
	}

	public Object getParent(Object element) {
		if (element instanceof Resource) {
			return null;
		}
		return wrappedProvider.getParent(element);
	}

	public boolean hasChildren(Object element) {
		if (element instanceof Resource) {
			return true;
		}
		return wrappedProvider.hasChildren(element);
	}

}
