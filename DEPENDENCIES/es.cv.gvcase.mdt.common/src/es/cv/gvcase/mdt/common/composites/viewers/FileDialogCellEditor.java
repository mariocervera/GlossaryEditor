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
package es.cv.gvcase.mdt.common.composites.viewers;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * An extension of DialogCellEditor to select Files from the File System
 * 
 * @author mgil
 */
public class FileDialogCellEditor extends DialogCellEditor {

	private String[] extensions = null;

	public FileDialogCellEditor(Composite parent, String[] extensions) {
		super(parent);
		setExtensions(extensions);
	}

	@Override
	protected boolean includeUnsetButton() {
		return true;
	}

	@Override
	protected Object openDialogBox(Control cellEditorWindow) {
		FileDialog dialog = new FileDialog(new Shell());
		dialog.setFilterExtensions(extensions);
		String filePath = dialog.open();
		if (filePath != null) {
			return filePath;
		}
		return "";
	}

	public String[] getExtensions() {
		return extensions;
	}

	public void setExtensions(String[] extensions) {
		this.extensions = extensions;
	}
}
