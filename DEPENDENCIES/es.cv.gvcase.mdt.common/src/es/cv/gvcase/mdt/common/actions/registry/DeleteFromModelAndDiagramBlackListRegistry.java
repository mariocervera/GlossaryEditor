/*******************************************************************************
 * Copyright (c) 2010 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Marc Gil Sendra (Prodevelop)
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.actions.registry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Platform;

import es.cv.gvcase.mdt.common.util.ExtensionPointParser;
import es.cv.gvcase.mdt.common.util.MDTUtil;

public class DeleteFromModelAndDiagramBlackListRegistry {
	/**
	 * The extension point identifier
	 */
	private final String extensionPointID = "es.cv.gvcase.mdt.common.deleteFromModelAndDiagramBlackList";

	/**
	 * List for black list objects to delete from model action
	 */
	private Map<String, Object> blackListModel;

	/**
	 * List for black list objects to delete from diagram action
	 */
	private Map<String, Object> blackListDiagram;

	/**
	 * Gets the instance for this registry
	 * 
	 * @return
	 */
	public static DeleteFromModelAndDiagramBlackListRegistry getInstance() {
		return new DeleteFromModelAndDiagramBlackListRegistry();
	}

	/**
	 * Checks if the delete from model action is provided for the given element
	 * 
	 * @param object
	 * @return
	 */
	public boolean removeDeleteFromModelAction(Object object) {
		if (blackListModel == null) {
			parseInitializers();
		}

		if (blackListModel.containsKey(object.getClass().getName())) {
			Object filter = blackListModel.get(object.getClass().getName());
			if (filter != null && filter instanceof IDeleteBlacklistFilter) {
				IDeleteBlacklistFilter bl = (IDeleteBlacklistFilter) filter;
				return bl.isAffected(object);
			} else {
				return MDTUtil.isOfType(object.getClass(), object.getClass()
						.getName());
			}
		}

		return false;
	}

	/**
	 * Checks if the delete from model action is provided for the given elements
	 * 
	 * @param object
	 * @return
	 */
	public boolean removeDeleteFromModelAction(List<Object> objects) {
		for (Object o : objects) {
			if (removeDeleteFromModelAction(o)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Checks if the delete from diagram action is provided for the given
	 * element
	 * 
	 * @param object
	 * @return
	 */
	public boolean removeDeleteFromDiagramAction(Object object) {
		if (blackListDiagram == null) {
			parseInitializers();
		}
		
		if (blackListDiagram.containsKey(object.getClass().getName())) {
			Object filter = blackListDiagram.get(object.getClass().getName());
			if (filter != null && filter instanceof IDeleteBlacklistFilter) {
				IDeleteBlacklistFilter bl = (IDeleteBlacklistFilter) filter;
				return bl.isAffected(object);
			} else {
				return MDTUtil.isOfType(object.getClass(), object.getClass()
						.getName());
			}
		}

		return false;
	}

	/**
	 * Checks if the delete from model action is provided for the given elements
	 * 
	 * @param object
	 * @return
	 */
	public boolean removeDeleteFromDiagramAction(List<Object> objects) {
		for (Object o : objects) {
			if (removeDeleteFromDiagramAction(o)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Parse the blackLists from the extension point
	 */
	private void parseInitializers() {
		blackListModel = new HashMap<String, Object>();
		blackListDiagram = new HashMap<String, Object>();

		parseBlackListFromExtensionPoint();
	}

	/**
	 * Parse the blackLists from the extension point and store them
	 */
	@SuppressWarnings("unchecked")
	private void parseBlackListFromExtensionPoint() {
		ExtensionPointParser extensionPointParser = new ExtensionPointParser(
				extensionPointID, new Class[] { DeleteFromModelBlackList.class,
						DeleteFromDiagramBlackList.class });

		for (Object o : extensionPointParser.parseExtensionPoint()) {
			if (o instanceof DeleteFromModelBlackList) {
				DeleteFromModelBlackList element = (DeleteFromModelBlackList) Platform
						.getAdapterManager().getAdapter(o,
								DeleteFromModelBlackList.class);
				if (element.affectedClass == null) {
					continue;
				}

				blackListModel.put(element.affectedClass, element.filterClass);
			} else if (o instanceof DeleteFromDiagramBlackList) {
				DeleteFromDiagramBlackList element = (DeleteFromDiagramBlackList) Platform
						.getAdapterManager().getAdapter(o,
								DeleteFromDiagramBlackList.class);
				if (element.affectedClass == null) {
					continue;
				}

				blackListDiagram
						.put(element.affectedClass, element.filterClass);
			}
		}
	}
}
