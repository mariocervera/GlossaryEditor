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

import es.cv.gvcase.gvm.glossary.RedefinedTerm;
import es.cv.gvcase.gvm.glossary.ReferencedTerm;
import es.cv.gvcase.gvm.glossary.formseditor.composites.fefem.SourceComposite;
import es.cv.gvcase.gvm.glossary.formseditor.pages.MainPage;

public class RedefinedTermDetailsComposite extends LocalTermDetailsComposite {
	
	private int style;
	
	private ReferencedTerm redefinedTerm;
	
	public RedefinedTermDetailsComposite(Composite parent, int style,
			FormToolkit toolkit, RedefinedTerm redefinedTerm, MainPage page) {
		super(parent, style, toolkit, redefinedTerm, page);
		
		this.style = style;
		this.redefinedTerm = redefinedTerm;
		
		createWidgets(toolkit);
	}
	
	protected void createWidgets(FormToolkit toolkit) {
		
		super.createWidgets(toolkit);
		
		SourceComposite sc = new SourceComposite(this, style, toolkit, redefinedTerm, getPage());
		sc.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		toolkit.adapt(this);
	}
	
}