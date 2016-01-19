/*******************************************************************************
 * Copyright (c) 2009 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana. All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Jose Manuel García Valladolid (Indra SL) - initial API implementation
 *
 ******************************************************************************/
package es.cv.gvcase.ide.navigator.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import es.cv.gvcase.ide.navigator.Activator;

/**
 * Preferences page for Resource navigator
 */

public class NavigatorPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	public NavigatorPreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Preferences for MOSKitt Resource Navigator");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public void createFieldEditors() {
		addField(new BooleanFieldEditor(
				PreferenceConstants.P_MODEL_BROKENREFERENCES,
				"Look for existing broken references when opening a model",
				getFieldEditorParent()));
	}

	public void init(IWorkbench workbench) {
	}

}