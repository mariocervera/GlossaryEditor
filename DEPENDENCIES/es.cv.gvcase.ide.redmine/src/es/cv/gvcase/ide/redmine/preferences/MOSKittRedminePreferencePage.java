/*******************************************************************************
 * Copyright (c) 2010 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana. All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Marc Gil Sendra (Prodevelop) - initial API implementation
 *
 ******************************************************************************/
package es.cv.gvcase.ide.redmine.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import es.cv.gvcase.ide.redmine.Activator;

public class MOSKittRedminePreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	public MOSKittRedminePreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Preferences for MOSKitt Redmine connector");
	}

	public void createFieldEditors() {
		RadioGroupFieldEditor rgfe = new RadioGroupFieldEditor(
				MOSKittRedminePreferenceConstants.RedmineAutentication,
				"Select autentication type on Redmine:",
				1,
				new String[][] {
						{
								"Anonymous",
								MOSKittRedminePreferenceConstants.RedmineAutenticationAnonymous },
						{
								"Personal API Access Key",
								MOSKittRedminePreferenceConstants.RedmineAutenticationUser } },
				getFieldEditorParent());
		addField(rgfe);

		StringFieldEditor sfe = new StringFieldEditor(
				MOSKittRedminePreferenceConstants.APIAccessKeyUser,
				"API Access Key", getFieldEditorParent());
		sfe.setTextLimit(40);
		addField(sfe);
	}

	public void init(IWorkbench workbench) {
	}

}