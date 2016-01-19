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
package es.cv.gvcase.mdt.common.palette;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gmf.runtime.diagram.ui.parts.DiagramEditorWithFlyOutPalette;
import org.eclipse.jface.resource.ImageDescriptor;

import es.cv.gvcase.emf.common.part.ExtensionPointParser;
import es.cv.gvcase.mdt.common.Activator;
import es.cv.gvcase.mdt.common.part.CachedResourcesDiagramEditor;

/**
 * Holds the information provided by the TemplateTool extension and
 * contributions from third parties. <br>
 * Allows the addition of specific tools to a diagram palette using the
 * information from the extension points. <br>
 * 
 * @author <a href="mailto:fjcano@prodeelop.es">Francisco Javier Cano Muñoz</a>
 * 
 * @see TemplateTool
 * @see Element
 * @see EditorGroup
 * 
 * @see ElementCreator
 * @see ElementConfigurator
 */
public class TemplateToolRegistry {

	// //
	// Template Tool registry.
	// //

	/**
	 * Singleton instance for this registry.
	 */
	public static TemplateToolRegistry INSTANCE = null;

	/**
	 * Instance getter method.
	 * 
	 * @return
	 */
	public static TemplateToolRegistry getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new TemplateToolRegistry();
		}
		return INSTANCE;
	}

	/**
	 * provate constructor for singleton.
	 */
	private TemplateToolRegistry() {

	}

	// //
	// read extension point
	// //

	/**
	 * Extension point identifier to parse.
	 */
	public static final String ExtensionPointID = "es.cv.gvcase.mdt.common.templateElementTool";

	/**
	 * Storing map from identifier to TemplateTool
	 */
	private Map<String, TemplateTool> mapID2TemplateTool = null;

	/**
	 * clears the storing map and reads and parses the extension point
	 */
	protected void readExtensionPoint() {
		if (mapID2TemplateTool == null) {
			// create needed maps
			mapID2TemplateTool = new HashMap<String, TemplateTool>();
		}
		// clear maps; start from scratch every time the extension point is
		// read,
		mapID2TemplateTool.clear();
		// read the extension point
		ExtensionPointParser parser = new ExtensionPointParser(
				ExtensionPointID, new Class[] { TemplateTool.class,
						Element.class, EditorGroup.class });
		for (Object object : parser.parseExtensionPoint()) {
			if (object instanceof TemplateTool) {
				TemplateTool tool = (TemplateTool) object;
				if (tool.Element == null || tool.Element.size() <= 0
						|| tool.EditorGroup == null
						|| tool.EditorGroup.size() <= 0) {
					// a tool with no elements is of no use
					// a tool with no editors to which to apply is of no use
					continue;
				}
				// add to id -> TemplateTool
				if (tool.id != null) {
					mapID2TemplateTool.put(tool.id, tool);
				}
			}
		}
	}

	// //
	// utility methods
	// //

	/**
	 * Gets the whole mapping from identifier to TemplateTool
	 */
	public Map<String, TemplateTool> getMapID2TemplateTool() {
		readExtensionPoint();
		return mapID2TemplateTool;
	}

	/**
	 * Gets all the template tools
	 * 
	 * @return
	 */
	public Collection<TemplateTool> getAllTemplateTools() {
		readExtensionPoint();
		return mapID2TemplateTool.values();
	}

	/**
	 * Gets a TemplateTool from the given identifier
	 * 
	 * @param id
	 * @return
	 */
	public TemplateTool getTemplateToolByID(String id) {
		readExtensionPoint();
		return mapID2TemplateTool.get(id);
	}

	/**
	 * Gets a list of TemplateTools for the given editor. <br>
	 * 
	 * @param editor
	 * @return
	 */
	public List<TemplateTool> getAllToolsForEditor(String editorID) {
		if (editorID == null) {
			// if no valid identifier can be got from the editor, there's
			// nothing we can do
			return Collections.emptyList();
		}
		// get all the template tools and work with them
		Collection<TemplateTool> allTools = getAllTemplateTools();
		List<TemplateTool> applicableTools = new ArrayList<TemplateTool>();
		if (allTools != null) {
			// search those template tools that specify that are available for
			// the given editor by looking into the EditorGroup elements that
			// specify an editor that matches the given editor.
			for (TemplateTool tool : allTools) {
				if (tool.EditorGroup != null) {
					for (Object o : tool.EditorGroup) {
						if (o instanceof EditorGroup) {
							EditorGroup editorGroup = (EditorGroup) o;
							if (editorID.equals(editorGroup.editor)) {
								applicableTools.add(tool);
							}
						}
					}
				}
			}
		}
		return applicableTools;
	}

	/**
	 * Try and get an editor identifier from an editor instance. <br>
	 * If a {@link CachedResourcesDiagramEditor} is given, its identifier is its
	 * getEditorID() value. <br>
	 * Otherwise, its identifier is its getSite().getID() value.
	 * 
	 * @param editor
	 * @return
	 */
	protected String getEditorID(DiagramEditorWithFlyOutPalette editor) {
		if (editor instanceof CachedResourcesDiagramEditor) {
			((CachedResourcesDiagramEditor) editor).getEditorID();
		}
		if (editor.getSite() != null) {
			return editor.getSite().getId();
		}
		return null;
	}

	// //
	// adding tools to palettes
	// //

	/**
	 * Adds all the tools to the palette of the given editor accoding to the
	 * information given in the TemplateTool extension point.
	 */
	public void addToolsToEditorPalette(String editorID, PaletteRoot paletteRoot) {
		// get tools applicable for this editor.
		List<TemplateTool> toolsForEditor = getAllToolsForEditor(editorID);
		if (toolsForEditor == null || toolsForEditor.size() <= 0) {
			return;
		}
		// add tools to their indicated groups in the editor's palette.
		addToolsToTheirGroups(paletteRoot, toolsForEditor);
	}

	/**
	 * Recursively traverses the PaletteContainer adding all required tools to
	 * their proper containers.
	 * 
	 * @param container
	 * @param tools
	 */
	protected void addToolsToTheirGroups(PaletteContainer container,
			List<TemplateTool> tools) {
		if (container == null || container.getId() == null || tools == null) {
			// nothing to do; return
			return;
		}
		// add all tools to this container
		for (TemplateTool tool : tools) {
			for (Object o : tool.EditorGroup) {
				EditorGroup editorGroup = (EditorGroup) o;
				if (container.getId().equals(editorGroup.group)) {
					container.add(createToolEntryForTool(tool));
				}
			}
		}
		// add tools to children containers
		for (Object o : container.getChildren()) {
			if (o instanceof PaletteContainer) {
				addToolsToTheirGroups((PaletteContainer) o, tools);
			}
		}
	}

	// //
	// Palette tool creation.
	// //

	/**
	 * Creates a ToolEntry that can take care of the creation of the element's
	 * structure defined in the given TemplateTool.
	 */
	public ToolEntry createToolEntryForTool(TemplateTool templateTool) {
		ImageDescriptor smallImage = null;
		if (templateTool.imageSmall != null) {
			smallImage = Activator.imageDescriptorFromPlugin(templateTool
					.getContributorId(), templateTool.imageSmall);
		}
		ImageDescriptor largeImage = null;
		if (templateTool.imageLarge != null) {
			smallImage = Activator.imageDescriptorFromPlugin(templateTool
					.getContributorId(), templateTool.imageLarge);
		}
		return new TemplateToolEntry(templateTool.name,
				templateTool.description, smallImage, largeImage,
				TemplateCreationTool.class, templateTool);
	}

	// //
	// Tool to create elements from a palette tool.
	// //

	/**
	 * Identifier for the template tool property in the tool
	 */
	public static final String TemplateToolPropertyKey = "es.cv.gvcase.mdt.common.palette.templateTool";

	/**
	 * A templateToolEntry to be added to a palette. <br>
	 * When the tool of this entry is used, the specified structured template of
	 * elements will be created.
	 * 
	 * @author <a href="mailto:fjcano@prodeelop.es">Francisco Javier Cano
	 *         Muñoz</a>
	 * 
	 */
	protected class TemplateToolEntry extends ToolEntry {

		/**
		 * tool process
		 */
		protected TemplateTool tool = null;

		public TemplateToolEntry(String label, String description,
				ImageDescriptor iconSmall, ImageDescriptor iconLarge,
				Class toolClass, TemplateTool tool) {
			super(label, description, iconSmall, iconLarge, toolClass);
			this.tool = tool;
		}

		@Override
		protected Map getToolProperties() {
			Map properties = new HashMap();
			properties.put(TemplateToolPropertyKey, this.tool);
			return properties;
		}

	}

}
