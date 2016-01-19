/*******************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – Initial implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.edit.parts;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.notation.View;

// TODO: Auto-generated Javadoc
/**
 * The Class CachedEditPartsRegistry.
 */
public class CachedEditPartsRegistry {

	/** The map view to edit part. */
	private static Map<String, EditPart> mapViewToEditPart = null;
	
	/**
	 * Gets the map.
	 * 
	 * @return the map
	 */
	private static Map<String, EditPart> getMap() {
		if (mapViewToEditPart == null) {
			mapViewToEditPart = new HashMap<String, EditPart>();
		}
		return mapViewToEditPart;
	}
	
	/**
	 * Put edit part.
	 * 
	 * @param view the view
	 * @param editPart the edit part
	 */
	public static void putEditPart(View view, EditPart editPart) {
		if (view == null || editPart == null) {
			return;
		}
		String key = getViewKey(view);
		if (key != null) {
			getMap().put(key, editPart);
		}
	}
	
	/**
	 * Gets the edits the part.
	 * 
	 * @param view the view
	 * 
	 * @return the edits the part
	 */
	public static EditPart getEditPart(View view) {
		if (view != null) {
			String key = getViewKey(view);
			if (key != null) {
				return getMap().get(key);
			}
		}
		return null;
	}
	
	/**
	 * Gets the view key.
	 * 
	 * @param view the view
	 * 
	 * @return the view key
	 */
	private static String getViewKey(View view) {
		if (view == null) {
			return null;
		}
		Resource resource = view.eResource();
		if (resource == null) {
			return null;
		}
		URI viewURI = resource.getURI();
		String fragment = resource.getURIFragment(view);
		viewURI = viewURI.appendFragment(fragment);
		return viewURI.toString();
	}
}
