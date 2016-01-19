/*******************************************************************************
 * Copyright (c) 2011 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana. All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Marc Gil Sendra (Prodevelop) [mgil@prodevelop.es] - initial API implementation
 ******************************************************************************/
package es.cv.gvcase.emf.ui.common.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.TreePath;

/**
 * This is a utility Class to store the path of a selected element in a
 * ChooseDialog. This path is built from the root element in the tree to the
 * selected element
 */
public class DataPath {
	/**
	 * The path
	 */
	private List<Object> path;

	/**
	 * The selected element
	 */
	private Object element;

	/**
	 * Create a new DataPath from a simple eObject. In this case, the tree
	 * should be composed by all the elements in the tree path
	 * 
	 * @see DataPath#DataPath(Object, boolean)
	 */
	public DataPath(Object object) {
		this(object, Collections.singletonList(Object.class));
	}

	/**
	 * Create a new DataPath from a simple eObject, including or not all the
	 * elements, depending on the value onlyEObjects
	 * 
	 * @see DataPath#DataPath(Object, TreePath, boolean)
	 */
	public DataPath(Object object, List<?> allowedTypes) {
		this(object, null, allowedTypes);
	}

	/**
	 * Create a new DataPath from a simple eObject. The tree should be composed
	 * by:
	 * 
	 * <li><b>tp == null: </b> the tree should be composed by the given object</li>
	 * 
	 * <li><b>tp != null: </b>the tree should be composed by the list of
	 * segments that the TreePath is composed (Only EObjects will be included if
	 * specified in the onlyEObjects parameter)</li> <br>
	 */
	public DataPath(Object object, TreePath tp, List<?> allowedTypes) {
		if (tp == null) {
			tp = new TreePath(new Object[] { object });
		}

		path = new ArrayList<Object>();
		element = object;

		buildPath(tp, allowedTypes);
	}

	/**
	 * Build the path
	 */
	private void buildPath(TreePath tp, List<?> allowedTypes) {
		for (int i = 0; i < tp.getSegmentCount(); i++) {
			Object o = tp.getSegment(i);
			if (o == null) {
				continue;
			}
			if (allowedTypes.isEmpty()) {
				path.add(o);
			} else {
				for (Object ob : allowedTypes) {
					if (((Class<?>) ob).isInstance(o)) {
						path.add(o);
					}
				}
			}
		}
	}

	/**
	 * Get the element of the Path
	 */
	public Object getElement() {
		return element;
	}

	/**
	 * Get the Path
	 */
	public List<Object> getPath() {
		return path;
	}

	@Override
	public String toString() {
		return element.toString() + ": " + path.toString();
	}
}