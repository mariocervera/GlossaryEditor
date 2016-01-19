/*******************************************************************************
 * Copyright (c) 2010 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – Initial implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;

import es.cv.gvcase.emf.common.part.ExtensionPointParser;

/**
 * A registry holding TypesFactories by editor identifier. Provides
 * ITypesFactories so that basic types can be created for different domains. A
 * domain is specified by an editor identifier.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public class TypesFactoryRegistry {

	// //
	// Singleton
	// //

	private static TypesFactoryRegistry INSTANCE = null;

	private TypesFactoryRegistry() {
	}

	public static TypesFactoryRegistry getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new TypesFactoryRegistry();
		}
		return INSTANCE;
	}

	// //
	// Holder classes
	// //

	public class TypesFactory {
		public String factoryID;
		public String editorID;
		public Object factoryClass;
	}

	// //
	// Registry
	// //

	private Map<String, ITypesFactory> mapEditorID2TypesFactory = null;

	private static final String extensionPointID = "es.cv.gvcase.mdt.common.typesForDomainFactory";

	private void readTypesFactoryExtensionPoint() {
		if (mapEditorID2TypesFactory == null) {
			mapEditorID2TypesFactory = new HashMap<String, ITypesFactory>();
		}
		mapEditorID2TypesFactory.clear();
		ExtensionPointParser parser = new ExtensionPointParser(
				extensionPointID, new Class[] { TypesFactory.class }, INSTANCE);
		for (Object object : parser.parseExtensionPoint()) {
			if (object instanceof TypesFactory) {
				TypesFactory factory = (TypesFactory) object;
				if (factory.editorID != null
						&& factory.factoryClass instanceof ITypesFactory) {
					mapEditorID2TypesFactory.put(factory.editorID,
							(ITypesFactory) factory.factoryClass);
				}
			}
		}
	}

	/**
	 * Retrieves a registry of editor ids to type factories from contribtion to
	 * the "es.cv.gvcase.mdt.common.typesForDomainFactory" extension point.
	 * 
	 * @return
	 */
	public Map<String, ITypesFactory> getMapEditorID2ITypesFactory() {
		readTypesFactoryExtensionPoint();
		return mapEditorID2TypesFactory;
	}

	/**
	 * Retrieves a ITypesFactory for the given editor identifier.
	 * 
	 * @param editorID
	 * @return
	 */
	public ITypesFactory getITypesFactoryForEditor(String editorID) {
		ITypesFactory factory = null;
		if (editorID != null
				&& getMapEditorID2ITypesFactory().containsKey(editorID)) {
			factory = getMapEditorID2ITypesFactory().get(editorID);
		}
		return factory;
	}

	// //
	// basic type creations methods
	// //

	/**
	 * Creates a basic type using the proper factory indicated by the editor
	 * identifier.
	 * 
	 */
	public EObject createTypeForEditor(Type type, String editorID) {
		return createTypeForEditor(type, editorID, null);
	}

	/**
	 * Creates a basic type using the proper factory indicated by the editor
	 * identifier and stores them in a model.
	 * 
	 */
	public EObject createTypeForEditor(Type type, String editorID, EObject model) {
		if (type == null || editorID == null) {
			return null;
		}
		ITypesFactory factory = getITypesFactoryForEditor(editorID);
		if (factory == null) {
			return null;
		}
		if (model != null) {
			return factory.createTypeInModel(type, model);
		} else {
			return factory.createType(type);
		}
	}

	/**
	 * Creates a group of basic types using the proper factory indicated by the
	 * editor identifier.
	 * 
	 * @param typesGroup
	 * @param editorID
	 * @return
	 */
	public Collection<EObject> createGroupForEditor(TypesGroup typesGroup,
			String editorID) {
		if (typesGroup == null || editorID == null) {
			return Collections.emptyList();
		}
		ITypesFactory factory = getITypesFactoryForEditor(editorID);
		if (factory == null) {
			return Collections.emptyList();
		}
		return factory.createTypeGroup(typesGroup);
	}

	/**
	 * Creates a group of basic types using the proper factory indicated by the
	 * editor identifier and stores them in a model.
	 * 
	 * @param typesGroup
	 * @param editorID
	 * @return
	 */
	public Collection<EObject> createGroupForEditor(TypesGroup typesGroup,
			String editorID, EObject model) {
		if (typesGroup == null || editorID == null) {
			return Collections.emptyList();
		}
		ITypesFactory factory = getITypesFactoryForEditor(editorID);
		if (factory == null) {
			return Collections.emptyList();
		}
		if (model != null) {
			return factory.createTypeGroupInModel(typesGroup, model);
		} else {
			return factory.createTypeGroup(typesGroup);
		}
	}

}
