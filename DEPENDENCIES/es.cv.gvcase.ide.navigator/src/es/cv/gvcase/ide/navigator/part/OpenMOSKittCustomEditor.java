/*******************************************************************************
 * Copyright (c) 2012 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Marc Gil Sendra (Prodevelop) – Initial API implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.ide.navigator.part;

import java.io.File;
import java.lang.reflect.Field;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.ErrorEditorPart;
import org.eclipse.ui.part.FileEditorInput;

/**
 * @author mgil
 */
@SuppressWarnings("restriction")
public class OpenMOSKittCustomEditor {

	/**
	 * Closes the given editor part and opens a customized editor part for
	 * errors for MOSKitt
	 * 
	 * @param input
	 * @param page
	 * @param editorPart
	 */
	public static void openMOSKittErrorEditor(FileEditorInput input,
			IWorkbenchPage page, IEditorPart editorPart) {
		page.closeEditor(editorPart, false);

		IEditorRegistry editorReg = PlatformUI.getWorkbench()
				.getEditorRegistry();
		IEditorDescriptor desc = editorReg
				.findEditor("es.cv.gvcase.MOSKittErrorEditor");
		MOSKittErrorEditor mee = null;
		try {
			mee = (MOSKittErrorEditor) page.openEditor(input, desc.getId(),
					true);
		} catch (PartInitException e1) {
		}

		if (mee != null) {
			IStatus status = null;
			try {
				// bad way to retrieve a value from an object ^_^
				// but the only one available at this moment, so it's ok :)
				// Intrusive way status: ON
				Field field = ErrorEditorPart.class.getDeclaredField("error");
				field.setAccessible(true);
				status = (IStatus) field.get(editorPart);
				// Intrusive way status: OFF
			} catch (Exception e) {
			}
			mee.setStatus(status);
		}
	}

	/**
	 * Opens the editor for check Broken References
	 * 
	 * @param errrorFile
	 * @param input
	 * @param page
	 */
	public static void openMOSKittBrokenReferencesEditor(File errorFile,
			FileEditorInput input, IWorkbenchPage page) {
		IEditorRegistry editorReg = PlatformUI.getWorkbench()
				.getEditorRegistry();
		IEditorDescriptor desc = editorReg
				.findEditor("es.cv.gvcase.MOSKittBrokenReferencesEditor");
		MOSKittBrokenReferencesEditor mee = null;
		try {
			mee = (MOSKittBrokenReferencesEditor) page.openEditor(input, desc
					.getId(), true);
			mee.setBrokenReferencesFile(errorFile);
		} catch (PartInitException e1) {
		}
	}
}
