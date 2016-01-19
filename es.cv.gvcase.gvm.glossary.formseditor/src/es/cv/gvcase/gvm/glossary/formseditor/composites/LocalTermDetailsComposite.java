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
package es.cv.gvcase.gvm.glossary.formseditor.composites;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import es.cv.gvcase.gvm.glossary.LocalTerm;
import es.cv.gvcase.gvm.glossary.RedefinedTerm;
import es.cv.gvcase.gvm.glossary.formseditor.composites.fefem.DefinitionComposite;
import es.cv.gvcase.gvm.glossary.formseditor.pages.MainPage;

public class LocalTermDetailsComposite extends TermDetailsComposite {
	
	private int style;
	
	private LocalTerm localTerm;
	
	public LocalTermDetailsComposite(Composite parent, int style,
			FormToolkit toolkit, LocalTerm localTerm, MainPage page) {
		super(parent, style, toolkit, localTerm, page);
		
		this.style = style;
		this.localTerm = localTerm;
		
		if(!(localTerm instanceof RedefinedTerm)) {
			createWidgets(toolkit);
		}
	}
	
	protected void createWidgets(FormToolkit toolkit) {
		
		super.createWidgets(toolkit);
		
		DefinitionComposite dc = new DefinitionComposite(this, style, toolkit, localTerm, getPage());
		dc.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		toolkit.adapt(this);
	}
	
}
