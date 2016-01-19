/*******************************************************************************
 * Copyright (c) 2009 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Carlos SÃ¡nchez PeriÃ±Ã¡n (Prodevelop) â€“ Initial implementation
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Platform;

import es.cv.gvcase.mdt.common.util.ExtensionPointParser;

/**
 * Registry for {@link TypesForDomain} structures. It stores TypesForDomain structures for
 * editor identifiers. It can provide TypesForDomain structures for an editor. It can
 * also provide TypesForDomain substructures for a visualID for an editor.
 * 
 * @author <a href="mailto:csanchez@prodevelop.es">Carlos SÃ¡nchez PeriÃ±Ã¡n</a>
 * @NOT-generated
 */
public class TypesForDomainRegistry {

	// // singleton

	/** The instance */
	private static final TypesForDomainRegistry INSTANCE = new TypesForDomainRegistry();

	/** Constructor */
	private TypesForDomainRegistry() {
	}

	/** Gets the singleton instance of this registry */
	public static TypesForDomainRegistry getInstance() {
		return INSTANCE;
	}

	/**
	 * Extension point identifier.
	 * 
	 * @NOT-generated
	 */
	private static final String extensionPointID = "es.cv.gvcase.mdt.common.typesForDomain";

	/** Extension Point identifier getter. */
	protected static String getExtensionPointID() {
		return extensionPointID;
	}

	/**
	 * Mapping storage for the editor identifier to TypesGroup structures.
	 * 
	 * @NOT-generated
	 */
	private static Map<String, ArrayList<TypesGroup>> mapEditorID2TypesGroup = null;

	/** 
	 * Mapping storage getter.
	 * 
	 */
	protected static Map<String, ArrayList<TypesGroup>> InitializeTypesGroupMap() {
		if (mapEditorID2TypesGroup == null) {
			mapEditorID2TypesGroup = new HashMap<String, ArrayList<TypesGroup>>();
		}
		return mapEditorID2TypesGroup;
	}

	/**
	 * Gets the TypesGroup structure for the given editor.
	 * 
	 * @param editorID
	 * @return
	 */
	public List<TypesGroup> getTypesGroupForEditor(String editorID) {
		return readMapEditorID2TypesGroup().get(editorID);
	}

	/**
	 * Reads, processes, and stores the TypesGroup structures defined in the
	 * extension point
	 * 
	 * @return
	 */
	protected Map<String, ArrayList<TypesGroup>> readMapEditorID2TypesGroup() {
		ArrayList <TypesGroup> list = null;
		Map<String, ArrayList<TypesGroup>> map = InitializeTypesGroupMap();
		ExtensionPointParser parser = new ExtensionPointParser(
				getExtensionPointID(),new Class[]{
					TypesGroup.class, 
					Type.class, 
					Property.class, 
					AvailableValue.class, 
					Editor.class}
				);
		for (Object object : parser.parseExtensionPoint()) {
			TypesGroup typesGroup = (TypesGroup) Platform
					.getAdapterManager().getAdapter(object, TypesGroup.class);
			if (typesGroup != null) {
				if (processTypesGroup(typesGroup)) {
					Iterator<Object> it = typesGroup.Editor.iterator();
					while(it.hasNext()){
						Editor editor = (Editor) it.next();
						if (map.containsKey(editor.editorID)){
							list= map.get(editor.editorID);
							if (compare(list,typesGroup)==false){
								list.add(typesGroup);
							}
						}else{
							list= new ArrayList<TypesGroup>();
							list.add(typesGroup);
							map.put(editor.editorID, list);
						}
					}
				}
			}
		}
		return map;
	}

	/**
	 * Processes a TypesGroup that has been read from the extension point. The
	 * child nodes defined in the extension point are structures hierarchically
	 * as defined by their parent visualIDs.
	 * 
	 * @param TypesGroup
	 * @return
	 */
	protected boolean processTypesGroup(TypesGroup TypesGroup) {
		if (TypesGroup == null 
				|| TypesGroup.Type == null
				|| TypesGroup.Editor == null
				|| TypesGroup.Type.size() <= 0
				|| TypesGroup.Editor.size() <= 0) {
			return false;
		}
		return true;
	}
	private boolean compare(ArrayList<TypesGroup> list, TypesGroup typesGroup){
		for (int i=0;i<list.size();i++){
			TypesGroup aux = list.get(i);
			if (aux.toString().equals(typesGroup.toString()))
				return true;
		}
		return false;
	}

}
