/*******************************************************************************
 * Copyright (c) 2011 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Marc Gil Sendra (Prodevelop) – Initial implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.ide.navigator.actions.resource;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.internal.ErrorEditorPart;
import org.eclipse.ui.internal.WorkbenchPage;
import org.eclipse.ui.internal.ide.DialogUtil;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.part.FileEditorInput;

import es.cv.gvcase.ide.navigator.part.OpenMOSKittCustomEditor;
import es.cv.gvcase.ide.navigator.util.BrokenReferencesChecker;

@SuppressWarnings("restriction")
public class OpenWithMenu extends org.eclipse.ui.actions.OpenWithMenu {

	private IAdaptable file;
	private IWorkbenchPage page;

	public OpenWithMenu(IWorkbenchPage page, IAdaptable file) {
		super(page, file);
		this.file = file;
		this.page = page;
	}

	@Override
	protected void openEditor(IEditorDescriptor editorDescriptor,
			boolean openUsingDescriptor) {
		try {
			IFile f = (IFile) file.getAdapter(IFile.class);
			if (f == null) {
				return;
			}

			FileEditorInput input = new FileEditorInput(f);

			IEditorPart ep = null;
			if (openUsingDescriptor) {
				ep = ((WorkbenchPage) page).openEditorFromDescriptor(
						new FileEditorInput(f), editorDescriptor, true, null);
			} else {
				String editorId = editorDescriptor == null ? IEditorRegistry.SYSTEM_EXTERNAL_EDITOR_ID
						: editorDescriptor.getId();

				boolean shouldOpen = false;
				if (editorId.equals("org.eclipse.ui.DefaultTextEditor")) {
					shouldOpen = true;
				} else {
					// check broken references
					BrokenReferencesChecker brc = new BrokenReferencesChecker(
							input);
					shouldOpen = brc.continueOpeningEditor();
					if (brc.hasBrokenReferences()
							&& brc.shouldOpenBrokenReferencesEditor()) {
						OpenMOSKittCustomEditor
								.openMOSKittBrokenReferencesEditor(brc
										.getErrorLogFile(), input, page);
					}
				}

				if (shouldOpen) {
					ep = ((WorkbenchPage) page).openEditor(new FileEditorInput(
							f), editorId, true, IWorkbenchPage.MATCH_INPUT
							| IWorkbenchPage.MATCH_ID);
				}
			}

			if (ep instanceof ErrorEditorPart) {
				OpenMOSKittCustomEditor.openMOSKittErrorEditor(input, page, ep);
			}
		} catch (PartInitException e) {
			DialogUtil.openError(page.getWorkbenchWindow().getShell(),
					IDEWorkbenchMessages.OpenWithMenu_dialogTitle, e
							.getMessage(), e);
		}
	}
}
