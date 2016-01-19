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
package es.cv.gvcase.emf.ui.common.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

public class PackagingNode {
	private String name;
	private Object parent;
	private Collection<Object> containedNodes = new ArrayList<Object>();

	public PackagingNode(String key, Object parent) {
		this.name = key;
		this.parent = parent;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Collection<Object> getContainedNodes() {
		return containedNodes;
	}

	public Object getParent() {
		return parent;
	}

	public static class PackagingNodeComparator implements
			Comparator<PackagingNode> {
		public int compare(PackagingNode arg0, PackagingNode arg1) {
			return arg0.getName().compareTo(arg1.getName());
		}
	}
}
