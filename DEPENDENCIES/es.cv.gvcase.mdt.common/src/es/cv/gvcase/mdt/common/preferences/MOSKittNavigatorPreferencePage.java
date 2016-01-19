/*******************************************************************************
 * Copyright (c) 2009 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana. All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Marc Gil Sendra (Prodevelop) - initial API implementation
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import es.cv.gvcase.mdt.common.Activator;

public class MOSKittNavigatorPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	public MOSKittNavigatorPreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Preferences for the MOSKitt Model Navigator");
	}

	public void createFieldEditors() {
		// grouping button
		addField(new BooleanFieldEditor(
				MOSKittPreferenceConstants.NAVIGATOR_GROUPING,
				"Grouping childs", getFieldEditorParent()));

		// grouping button
		addField(new BooleanFieldEditor(
				MOSKittPreferenceConstants.NAVIGATOR_REMOVE_TYPE,
				"Remove prefix type of the elements", getFieldEditorParent()));

	}

	public void init(IWorkbench workbench) {
	}

	public static void initDefaults(IPreferenceStore store) {
		store.setDefault(MOSKittPreferenceConstants.NAVIGATOR_GROUPING, true);
		store
				.setDefault(MOSKittPreferenceConstants.NAVIGATOR_REMOVE_TYPE,
						true);
	}

}