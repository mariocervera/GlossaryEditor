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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class GroupableTreeArrayContentProvider extends ArrayContentProvider
		implements ITreeContentProvider {

	Map<Object, PackagingNode> parentNodes = new HashMap<Object, PackagingNode>();

	ITreeContentProvider wrappedProvider;

	public GroupableTreeArrayContentProvider(ITreeContentProvider provider) {
		wrappedProvider = provider;
	}

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof PackagingNode) {
			return ((PackagingNode) parentElement).getContainedNodes()
					.toArray();
		} else {
			return getVirtualSupernodes(wrappedProvider
					.getChildren(parentElement));
		}
	}

	public Object getParent(Object element) {
		if (element instanceof PackagingNode) {
			return ((PackagingNode) element).getParent();
		} else {
			return this.parentNodes.get(element);
		}
	}

	public boolean hasChildren(Object element) {
		if (element instanceof PackagingNode) {
			return true;
		} else {
			return wrappedProvider.hasChildren(element);
		}
	}

	@Override
	public Object[] getElements(Object inputElement) {

		return getVirtualSupernodes(inputElement);
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		super.inputChanged(viewer, oldInput, newInput);

	}

	private String getKey(Object o) {
		if (o instanceof EObject) {
			return ((EObject) o).eClass().getName();
		} else if (o == null || o.toString().equals("")) {
			return "";
		} else {
			return o.getClass().getInterfaces()[0].getSimpleName();
		}
	}

	private Object[] getVirtualSupernodes(Object node) {
		Map<String, PackagingNode> virtualSuperNodes = new HashMap<String, PackagingNode>();

		if (node instanceof Object[]) {
			Object[] n = (Object[]) node;
			for (int i = 0; i < n.length; i++) {
				String key = getKey(n[i]);
				if (!virtualSuperNodes.containsKey(key)) {
					PackagingNode ghostNode = new PackagingNode(key,
							wrappedProvider.getParent(n[i]));
					virtualSuperNodes.put(key, ghostNode);
					parentNodes.put(n[i], ghostNode);
				}
				virtualSuperNodes.get(key).getContainedNodes().add(n[i]);
			}
		}

		return virtualSuperNodes.values().toArray();
	}

	public void dispose() {
	}

}
