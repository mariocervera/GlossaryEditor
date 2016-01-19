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

import java.util.ArrayList;

import org.eclipse.emf.edit.ui.action.LoadResourceAction.LoadResourceDialog;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import es.cv.gvcase.mdt.common.dialogs.LoadFilteredResourceDialog;
import es.cv.gvcase.mdt.common.viewer.filters.FileFilter;

/**
 * An extension of DialogCellEditor to select Files from the File System
 * 
 * @author mgil
 */
public class FileWorkspaceDialogCellEditor extends DialogCellEditor {

	private String[] extensions = null;

	public FileWorkspaceDialogCellEditor(Composite parent, String[] extensions) {
		super(parent);
		setExtensions(extensions);
	}

	@Override
	protected boolean includeUnsetButton() {
		return true;
	}

	@Override
	protected Object openDialogBox(Control cellEditorWindow) {
		ArrayList<ViewerFilter> filterList = new ArrayList<ViewerFilter>();
		if (extensions != null) {
			filterList.add(new FileFilter(extensions));
		}

		LoadResourceDialog dialog = new LoadFilteredResourceDialog(new Shell(),
				true, filterList);
		if (dialog.open() == LoadResourceDialog.CANCEL) {
			return "";
		}

		return dialog.getURIText();
	}

	public String[] getExtensions() {
		return extensions;
	}

	public void setExtensions(String[] extensions) {
		this.extensions = extensions;
	}
}
