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

package es.cv.gvcase.mdt.common.sections.composites;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class MultiChooseDialog extends ChooseDialog {

	public MultiChooseDialog(Shell parentShell, Object[] objects) {
		super(parentShell, objects);
	}

	protected SearchableTree createTree(Composite parent) {
		return new SearchableTree(parent, SWT.MULTI);
	}

}