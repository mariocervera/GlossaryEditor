/***************************************************************************
* Copyright (c) 2008 Conselleria de Infraestructuras y Transporte,
* Generalitat de la Comunitat Valenciana . All rights reserved. This program
* and the accompanying materials are made available under the terms of the
* Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors: Mario Cervera Ubeda (Integranova) - Initial API and implementation
*
**************************************************************************/
package es.cv.gvcase.gvm.glossary.formseditor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;

import es.cv.gvcase.fefem.common.FEFEMEditor;
import es.cv.gvcase.gvm.glossary.GlossaryPackage;
import es.cv.gvcase.gvm.glossary.formseditor.pages.MainPage;

public class GlossaryFormEditor extends FEFEMEditor {

	@Override
	protected List<FormPage> getEditorPagesList() {
		List<FormPage> pagesList = new ArrayList<FormPage>();
		pagesList.add(new MainPage(this));
		return pagesList;
	}

	@Override
	protected FormToolkit createToolkit(Display display) {
		// Create a toolkit that shares colors between editors.
		return new FormToolkit(GlossaryFormEditorPlugin.getDefault().getFormColors(
				display));
	}

	@Override
	protected EPackage get_eINSTANCE() {		
		return GlossaryPackage.eINSTANCE;
	}

	@Override
	protected String get_eNS_URI() {		
		return GlossaryPackage.eNS_URI;
	}


	@Override
	protected boolean enabledLiveEcoreValidation() {
		return true;
	}

	@Override
	protected boolean enabledSelectionSynch() {
		
		return true;
	}

	@Override
	protected boolean enabledUnresolvedProxiesDialog() {
		return true;
	}
	
	
}
