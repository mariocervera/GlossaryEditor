/***************************************************************************
 * Copyright (c) 2010 Conselleria de Infraestructuras y Transporte,
 * Generalitat de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – initial API and implementation
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.commands.diagram;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.notation.Diagram;

import es.cv.gvcase.emf.common.part.ExtensionPointParser;
import es.cv.gvcase.mdt.common.util.MDTUtil;

/**
 * Registry to store the {@link Diagram}s that can be created from a given
 * {@link IGraphicalEditPart} ot from a given {@link Class}
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public class EClassToDiagramRegistry {

	public class EditPartDiagrams {
		public String label;
		public String eClass;
		public List<ModelID> ModelID;
		public List<Exclude> Exclude;
	}

	public class ModelID {
		public String modelID;
	}

	public class Exclude {
		public String eClass;
	}

	// //
	// Singleton
	// //

	private EClassToDiagramRegistry() {

	}

	protected static EClassToDiagramRegistry INSTANCE = null;

	public static EClassToDiagramRegistry getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new EClassToDiagramRegistry();
		}
		return INSTANCE;
	}

	// //
	// Map registry
	// //

	public static final String ExtensionPointID = "es.cv.gvcase.mdt.common.editPartCreateDiagram";

	protected Map<String, List<String>> mapEClassToModelIDS = new HashMap<String, List<String>>();

	protected List<EditPartDiagrams> editPartDiagramsList = new ArrayList<EditPartDiagrams>();

	protected void readExtensionPoint() {
		// clear the storing map and the list to start each time from scratch
		mapEClassToModelIDS.clear();
		editPartDiagramsList.clear();
		// parse the extension point using classes defined inside this class
		ExtensionPointParser parser = new ExtensionPointParser(
				ExtensionPointID, new Class[] { EditPartDiagrams.class,
						ModelID.class, Exclude.class }, this);
		// loop all over the contributions
		EditPartDiagrams editPartDiagrams = null;
		List<String> modelIDS = null;
		for (Object o : parser.parseExtensionPoint()) {
			if (o instanceof EditPartDiagrams) {
				editPartDiagrams = (EditPartDiagrams) o;
				// store in the general list of DiagramEditParts
				editPartDiagramsList.add(editPartDiagrams);
				// get the list of model ids to a specific eclass
				if (mapEClassToModelIDS.containsKey(editPartDiagrams.eClass)) {
					modelIDS = mapEClassToModelIDS.get(editPartDiagrams.eClass);
				} else {
					modelIDS = new ArrayList<String>();
					mapEClassToModelIDS.put(editPartDiagrams.eClass, modelIDS);
				}
				// store the model ids to the specific eclass
				if (editPartDiagrams.ModelID != null) {
					for (ModelID modelID : editPartDiagrams.ModelID) {
						if (modelID.modelID != null) {
							modelIDS.add(modelID.modelID);
						}
					}
				}
			}
		}
	}

	// //
	// Utility methods
	// //

	public List<String> getDiagramsForEditPart(IGraphicalEditPart editPart) {
		if (editPart == null) {
			return Collections.emptyList();
		} else {
			EObject semanticElement = editPart.resolveSemanticElement();
			if (semanticElement == null) {
				return Collections.emptyList();
			}
			Class semanticTypeClass = semanticElement.getClass();
			return getDiagramsForClass(semanticTypeClass);
		}
	}

	public List<String> getDiagramsForClass(Class clazz) {
		if (clazz == null) {
			return Collections.emptyList();
		} else {
			readExtensionPoint();
			Class semanticTypeClass = clazz;
			for (String type : mapEClassToModelIDS.keySet()) {
				if (MDTUtil.isOfType(semanticTypeClass, type)) {
					if (!isExcluded(type, semanticTypeClass)) {
						return mapEClassToModelIDS.get(type);
					}
				}
			}
			return Collections.emptyList();
		}
	}

	protected boolean isExcluded(String type, Class semanticTypeClass) {
		if (semanticTypeClass == null) {
			return false;
		}
		String className = semanticTypeClass.getCanonicalName();
		for (EditPartDiagrams editPartDiagrams : editPartDiagramsList) {
			if (editPartDiagrams.eClass.equals(type)) {
				if (editPartDiagrams.Exclude != null) {
					for (Exclude exclude : editPartDiagrams.Exclude) {
						if (MDTUtil.isOfType(semanticTypeClass, exclude.eClass)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

}
