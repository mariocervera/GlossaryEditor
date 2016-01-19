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
package es.cv.gvcase.mdt.common.ids;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import es.cv.gvcase.emf.common.part.ExtensionPointParser;
import es.cv.gvcase.emf.common.part.IObjectWithContributorId;

// TODO: Auto-generated Javadoc
/**
 * The Class MOSKittEditorIDs.
 */
public class MOSKittEditorIDs {

	// //
	// Singleton
	// //

	protected static MOSKittEditorIDs INSTANCE = null;

	public static MOSKittEditorIDs getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new MOSKittEditorIDs();
		}
		return INSTANCE;
	}

	private MOSKittEditorIDs() {
	}

	// //
	//
	// //

	// // UML2 Editor IDs
	/** The Constant UML2ClazzEditorID. */
	@Deprecated
	public static final String UML2ClazzEditorID = "es.cv.gvcase.mdt.uml2.diagram.clazz.part.UMLDiagramEditorID";

	/** The Constant UML2ActivityEditorID. */
	@Deprecated
	public static final String UML2ActivityEditorID = "es.cv.gvcase.mdt.uml2.diagram.activity.part.UMLDiagramEditorID";

	/** The Constant UML2UseCaseEditorID. */
	@Deprecated
	public static final String UML2UseCaseEditorID = "es.cv.gvcase.mdt.uml2.diagram.usecase.part.UMLDiagramEditorID";

	/** The Constant UML2StateMachineEditorID. */
	@Deprecated
	public static final String UML2StateMachineEditorID = "es.cv.gvcase.mdt.uml2.diagram.statemachine.part.UMLDiagramEditorID";

	/** The Constant UML2SequenceEditorID. */
	@Deprecated
	public static final String UML2SequenceEditorID = "es.cv.gvcase.mdt.uml2.diagram.sequence.part.UMLDiagramEditorID";

	/** The Constant UML2ProfileEditorID. */
	@Deprecated
	public static final String UML2ProfileEditorID = "es.cv.gvcase.mdt.uml2.diagram.profile.part.UMLDiagramEditorID";

	/** The all editor i ds. */
	@Deprecated
	private static List<String> allEditorIDs = null;

	// // SQL IDs
	/** The Constant SQLEditorID. */
	@Deprecated
	public static final String SQLEditorID = "es.cv.gvcase.mdt.db.diagram.part.SqlmodelDiagramEditorID";

	// // WBD IDs
	/** The Constant WBSEditorID. */
	@Deprecated
	public static final String WBSEditorID = "es.cv.gvcase.mdt.wbs.diagram.part.WbsDiagramEditorID";

	/**
	 * Gets the all editor i ds.
	 * 
	 * @return the all editor i ds
	 */
	@Deprecated
	public static List<String> getAllEditorIDs() {
		if (allEditorIDs == null) {
			allEditorIDs = new ArrayList<String>();
			allEditorIDs.add(UML2ClazzEditorID);
			allEditorIDs.add(UML2ActivityEditorID);
			allEditorIDs.add(UML2UseCaseEditorID);
			allEditorIDs.add(UML2StateMachineEditorID);
			allEditorIDs.add(UML2SequenceEditorID);
			allEditorIDs.add(SQLEditorID);
			allEditorIDs.add(WBSEditorID);
		}
		return allEditorIDs;
	}

	/** The Map model to editor. */
	@Deprecated
	public static Map<String, String> MapModelToEditor = null;

	/** The Map editor to model. */
	@Deprecated
	public static Map<String, String> MapEditorToModel = null;
	static {
		MapModelToEditor = new HashMap<String, String>();
		MapEditorToModel = new HashMap<String, String>();
		List<String> models = MOSKittModelIDs.getAllModelIDs();
		List<String> editors = getAllEditorIDs();
		for (int i = 0; i < models.size() && i < editors.size(); i++) {
			MapModelToEditor.put(models.get(i), editors.get(i));
			MapModelToEditor.put(editors.get(i), models.get(i));
		}
	}

	/** Extension Point functionality. */

	private static final String mapModelToEditorExtensionPoint = "es.cv.gvcase.mdt.common.modelToEditorMap";

	/** The Constant ATT_MODELID. */
	private static final String ATT_MODELID = "modelID";

	/** The Constant ATT_EDITORID. */
	private static final String ATT_EDITORID = "editorID";

	/** The Constant ATT_LABEL. */
	private static final String ATT_LABEL = "label";

	/** The cached map model to editor. */
	private static Map<String, String> cachedMapModelToEditor = null;

	/** The cached map model to label. */
	private static Map<String, String> cachedMapModelToLabel = null;

	/**
	 * Gets the all extension model to editor.
	 * 
	 * @return the all extension model to editor
	 */
	public static Map<String, String> getAllExtensionModelToEditor() {
		return getExtensionsMapModelToEditor();
	}

	/**
	 * Gets the all extensions editor i ds.
	 * 
	 * @return the all extensions editor i ds
	 */
	public static List<String> getAllExtensionsEditorIDs() {
		return new ArrayList<String>(getExtensionsMapModelToEditor().values());
	}

	/**
	 * Gets the extensions map model to editor.
	 * 
	 * @return the extensions map model to editor
	 */
	protected static Map<String, String> getExtensionsMapModelToEditor() {
		if (cachedMapModelToEditor == null) {
			buildModelToEditorMap();
		}
		return cachedMapModelToEditor;
	}

	/**
	 * Builds the model to editor map.
	 */
	protected static void buildModelToEditorMap() {
		cachedMapModelToEditor = new HashMap<String, String>();
		IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = extensionRegistry
				.getExtensionPoint(mapModelToEditorExtensionPoint);
		for (IExtension extension : extensionPoint.getExtensions()) {
			processExtensionEditor(extension, cachedMapModelToEditor);
		}
	}

	/**
	 * Process extension editor.
	 * 
	 * @param extension
	 *            the extension
	 * @param map
	 *            the map
	 */
	protected static void processExtensionEditor(IExtension extension,
			Map<String, String> map) {
		for (IConfigurationElement configElement : extension
				.getConfigurationElements()) {
			String model = configElement.getAttribute(ATT_MODELID);
			String editor = configElement.getAttribute(ATT_EDITORID);
			map.put(model, editor);
		}
	}

	/**
	 * Gets the extensions map model to label.
	 * 
	 * @return the extensions map model to label
	 */
	public static Map<String, String> getExtensionsMapModelToLabel() {
		if (cachedMapModelToLabel == null) {
			buildMapModelToLabel();
		}
		return cachedMapModelToLabel;
	}

	/**
	 * Builds the map model to label.
	 */
	protected static void buildMapModelToLabel() {
		cachedMapModelToLabel = new HashMap<String, String>();
		IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = extensionRegistry
				.getExtensionPoint(mapModelToEditorExtensionPoint);
		for (IExtension extension : extensionPoint.getExtensions()) {
			processExtensionLabel(extension, cachedMapModelToLabel);
		}
	}

	/**
	 * Process extension label.
	 * 
	 * @param extension
	 *            the extension
	 * @param map
	 *            the map
	 */
	protected static void processExtensionLabel(IExtension extension,
			Map<String, String> map) {
		for (IConfigurationElement configElement : extension
				.getConfigurationElements()) {
			String model = configElement.getAttribute(ATT_MODELID);
			String label = configElement.getAttribute(ATT_LABEL);
			if (label != null) {
				map.put(model, label);
			}
		}
	}

	// //
	// extension point parser
	// //

	public class modelToEditor implements IObjectWithContributorId {
		public String modelID;
		public String editorID;
		public String label;
		//
		protected String contributorID;

		public String getContributorId() {
			return contributorID;
		}

		public void setContributorId(String contributorId) {
			this.contributorID = contributorId;
		}
	}

	protected List<modelToEditor> readExtensionPoint() {
		ExtensionPointParser parser = new ExtensionPointParser(
				mapModelToEditorExtensionPoint,
				new Class[] { modelToEditor.class }, this);
		modelToEditor modelToEditorInstance = null;
		List<modelToEditor> modelToEditors = new ArrayList<modelToEditor>();
		for (Object o : parser.parseExtensionPoint()) {
			modelToEditorInstance = null;
			if (o instanceof modelToEditor) {
				modelToEditorInstance = (modelToEditor) o;
				modelToEditors.add(modelToEditorInstance);
			}
		}
		return modelToEditors;
	}

	public List<modelToEditor> getAllModelToEditor() {
		return readExtensionPoint();
	}

	public modelToEditor getModelToEditorForModel(String modelID) {
		if (modelID != null && !modelID.equals("")) {
			for (modelToEditor modelToEditorInstance : getAllModelToEditor()) {
				if (modelID.equals(modelToEditorInstance.modelID)) {
					return modelToEditorInstance;
				}
			}
		}
		return null;
	}

}
