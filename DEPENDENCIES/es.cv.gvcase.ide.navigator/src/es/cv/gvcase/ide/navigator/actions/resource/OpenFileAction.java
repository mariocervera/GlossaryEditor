/*******************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – Initial API 
 * implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.ide.navigator.actions.resource;

import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.SelectionListenerAction;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.ErrorEditorPart;
import org.eclipse.ui.internal.WorkbenchPage;
import org.eclipse.ui.internal.ide.DialogUtil;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.internal.ide.IIDEHelpContextIds;
import org.eclipse.ui.part.FileEditorInput;

import es.cv.gvcase.ide.navigator.part.OpenMOSKittCustomEditor;
import es.cv.gvcase.ide.navigator.util.BrokenReferencesChecker;
import es.cv.gvcase.mdt.common.util.MDTUtil;

/**
 * Action to open a {@link Diagram} closing all other {@link IEditorPart}s that
 * are editing the same resource the {@link Diagram} to open uses.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 */
@SuppressWarnings("restriction")
public class OpenFileAction extends SelectionListenerAction {

	/**
	 * The id of this action.
	 */
	public static final String ID = "MOSKittOpenFileAction";//$NON-NLS-1$

	/**
	 * The editor to open.
	 */
	private IEditorDescriptor editorDescriptor;

	/**
	 * The workbench page to open the editor in.
	 */
	private IWorkbenchPage workbenchPage;

	/**
	 * Creates a new action that will open editors on the then-selected file
	 * resources. Equivalent to <code>OpenFileAction(page,null)</code>.
	 * 
	 * @param page
	 *            the workbench page in which to open the editor
	 */
	public OpenFileAction(IWorkbenchPage page) {
		this(page, null);
	}

	/**
	 * Creates a new action that will open instances of the specified editor on
	 * the then-selected file resources.
	 * 
	 * @param page
	 *            the workbench page in which to open the editor
	 * @param descriptor
	 *            the editor descriptor, or <code>null</code> if unspecified
	 */
	public OpenFileAction(IWorkbenchPage page, IEditorDescriptor descriptor) {
		super(ID);
		if (page == null) {
			throw new IllegalArgumentException();
		}
		page.getWorkbenchWindow().getWorkbench().getHelpSystem().setHelp(this,
				IIDEHelpContextIds.OPEN_SYSTEM_EDITOR_ACTION);
		this.workbenchPage = page;

		setText(descriptor == null ? IDEWorkbenchMessages.OpenFileAction_text
				: descriptor.getLabel());
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this,
				IIDEHelpContextIds.OPEN_FILE_ACTION);
		setToolTipText(IDEWorkbenchMessages.OpenFileAction_toolTip);
		setId(ID);
		this.editorDescriptor = descriptor;
	}

	protected IWorkbenchPage getWorkbenchPage() {
		return workbenchPage;
	}

	@SuppressWarnings("unchecked")
	public void run() {
		Iterator itr = getSelectedResources().iterator();
		while (itr.hasNext()) {
			IResource resource = (IResource) itr.next();
			if (resource instanceof IFile) {
				// remove this property that can make the copy-paste of diagram
				// editors not work
				MDTUtil.removeEditorForDiagramProperty((IFile) resource);
				// open the file
				openFile((IFile) resource);
			}
		}
	}

	/**
	 * Opens an editor on the given file resource.
	 * 
	 * @param file
	 *            the file resource
	 */
	private void openFile(IFile file) {
		try {
			FileEditorInput input = new FileEditorInput(file);

			editorDescriptor = IDE.getEditorDescriptor(file);
			if (editorDescriptor == null) {
				return;
			}

			boolean shouldOpen = false;
			if (editorDescriptor.getId().equals(
					"org.eclipse.ui.DefaultTextEditor")) {
				shouldOpen = true;
			} else {
				// check broken references
				BrokenReferencesChecker brc = new BrokenReferencesChecker(input);
				shouldOpen = brc.continueOpeningEditor();
				if (brc.hasBrokenReferences()
						&& brc.shouldOpenBrokenReferencesEditor()) {
					OpenMOSKittCustomEditor.openMOSKittBrokenReferencesEditor(
							brc.getErrorLogFile(), input, getWorkbenchPage());
				}
			}

			IEditorPart ep = null;
			if (shouldOpen) {
				ep = ((WorkbenchPage) getWorkbenchPage()).openEditor(input,
						editorDescriptor.getId(), true,
						IWorkbenchPage.MATCH_INPUT | IWorkbenchPage.MATCH_ID);
			}

			if (ep instanceof ErrorEditorPart) {
				OpenMOSKittCustomEditor.openMOSKittErrorEditor(input,
						getWorkbenchPage(), ep);
			}
		} catch (PartInitException e) {
			DialogUtil.openError(getWorkbenchPage().getWorkbenchWindow()
					.getShell(),
					IDEWorkbenchMessages.OpenFileAction_openFileShellTitle, e
							.getMessage(), e);
		}
	}

	protected boolean updateSelection(IStructuredSelection selection) {
		return super.updateSelection(selection)
				&& selectionIsOfType(IResource.FILE);
	}

}
