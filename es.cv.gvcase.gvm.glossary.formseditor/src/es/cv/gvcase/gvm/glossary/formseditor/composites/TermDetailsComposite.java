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

import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import es.cv.gvcase.gvm.glossary.formseditor.pages.MainPage;

public class TermDetailsComposite extends Composite {
	
	private EObject term;
	
	private MainPage page;
	
	public TermDetailsComposite(Composite parent, int style,
			FormToolkit toolkit, EObject term, MainPage page) {
		super(parent, style);
		
		this.term = term;
		this.page = page;
	}
	
	public MainPage getPage() {return page;}
	
	public EObject getTerm() {return term;}
	
	protected void createWidgets(FormToolkit toolkit) {
		
		this.setLayout(new GridLayout(1, false));
	}
	
}

