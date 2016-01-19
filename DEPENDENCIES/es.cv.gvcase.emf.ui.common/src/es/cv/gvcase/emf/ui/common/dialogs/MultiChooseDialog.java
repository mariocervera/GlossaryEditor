/***********************************************************************
 * Copyright (c) 2007 Anyware Technologies
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Marc Gil Sendra (Prodevelop) - Initial implementation
 **********************************************************************/
package es.cv.gvcase.emf.ui.common.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import es.cv.gvcase.emf.ui.common.composites.SearchableTreeComposite;

public class MultiChooseDialog extends ChooseDialog {

	public MultiChooseDialog(Shell parentShell, Object[] objects) {
		super(parentShell, objects);
	}

	protected SearchableTreeComposite createTree(Composite parent) {
		return new SearchableTreeComposite(parent, SWT.MULTI);
	}

}