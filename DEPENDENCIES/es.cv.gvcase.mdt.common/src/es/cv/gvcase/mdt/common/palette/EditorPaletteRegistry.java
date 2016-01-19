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
package es.cv.gvcase.mdt.common.palette;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.gef.palette.PaletteRoot;

import es.cv.gvcase.mdt.common.util.ExtensionPointParser;

/**
 * A registry from where MOSKitt editors grab their palette modifications or
 * full palette.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public class EditorPaletteRegistry {

	// // Singleton registry.

	private static final EditorPaletteRegistry Instance = new EditorPaletteRegistry();

	private EditorPaletteRegistry() {
	}

	public static EditorPaletteRegistry getInstance() {
		return Instance;
	}

	// This registry is controlled via extension points.

	private static final String extensionPointID = "es.cv.gvcase.mdt.common.editorPalette";

	public static String getExtensionPointID() {
		return extensionPointID;
	}

	private static Map<String, Palette> mapEditorIDToPalette = null;

	public Map<String, Palette> getMapEditorIDToPalette() {
		if (mapEditorIDToPalette == null) {
			parseEditorPaletteExtensionPoint();
		}
		return mapEditorIDToPalette;
	}

	public Palette getPaletteForEditor(String editorID) {
		if (getMapEditorIDToPalette().containsKey(editorID)) {
			return getMapEditorIDToPalette().get(editorID);
		}
		return null;
	}

	public PaletteRoot customizePaletteForEditor(String editorID,
			PaletteRoot paletteRoot) {
		TemplateToolRegistry.getInstance().addToolsToEditorPalette(
				editorID, paletteRoot);
		Palette palette = getPaletteForEditor(editorID);
		if (palette != null) {
			return palette.customizePelette(paletteRoot);
		}
		return paletteRoot;
	}

	public void parseEditorPaletteExtensionPoint() {
		Class<Object>[] classes = new Class[] { Palette.class, Group.class,
				Tool.class, ElementType.class,
				es.cv.gvcase.mdt.common.palette.Runnable.class };
		ExtensionPointParser parser = new ExtensionPointParser(
				getExtensionPointID(), classes);
		mapEditorIDToPalette = new HashMap<String, Palette>();
		for (Object object : parser.parseExtensionPoint()) {
			if (object instanceof Palette) {
				Palette palette_ = (Palette) object;
				mapEditorIDToPalette.put(palette_.editorID, palette_);
			}
		}
	}

}
