/*******************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Gabriel Merin (Prodevelop) – Initial implementation
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.ui.CommonUIPlugin;
import org.eclipse.emf.common.ui.dialogs.WorkspaceResourceDialog;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.edit.ui.action.LoadResourceAction.LoadResourceDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

public class LoadFilteredResourceDialog extends LoadResourceDialog {

	Boolean browseFileSystem;
	Boolean allowMultipleSelection;
	String fileExtension;
	List<ViewerFilter> filters;

	List<String> selectedResources;

	public LoadFilteredResourceDialog(Shell arg0, boolean browseFileSystem,
			List<ViewerFilter> filters) {
		this(arg0, browseFileSystem, false, null, filters);
	}

	public LoadFilteredResourceDialog(Shell arg0, boolean browseFileSystem,
			boolean allowMultipleSelection, String fileExtension,
			List<ViewerFilter> filters) {
		super(arg0);
		this.browseFileSystem = browseFileSystem;
		this.allowMultipleSelection = allowMultipleSelection;
		this.fileExtension = fileExtension;
		this.filters = filters;

		this.selectedResources = new ArrayList<String>();
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Control control = super.createDialogArea(parent);
		uriField.setText(getDefaultText());

		return control;
	}

	public String getDefaultText() {
		return "";
	}

	public List<String> getSelectedResources() {
		return selectedResources;
	}

	@Override
	protected void prepareBrowseWorkspaceButton(Button browseWorkspaceButton) {
		{
			browseWorkspaceButton.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent event) {

					IFile[] files = openFileSelection(getShell(), null, null,
							allowMultipleSelection, null, filters);

					if (files.length == 0) {
						return;
					}

					for (int i = 0; i < files.length; i++) {
						String fileUri = URI.createPlatformResourceURI(
								files[i].getFullPath().toString(), false)
								.toString();
						if (i == 0) {
							uriField.setText(fileUri);
						} else {
							uriField.setText(uriField.getText() + ", "
									+ fileUri);
						}
						selectedResources.add(fileUri);
					}

				}
			});
		}
	}

	@Override
	protected void prepareBrowseFileSystemButton(Button browseFileSystemButton) {
		if (browseFileSystem)
			super.prepareBrowseFileSystemButton(browseFileSystemButton);
		else
			browseFileSystemButton.setEnabled(false);
	}

	private IFile[] openFileSelection(Shell parent, String title,
			String message, boolean allowMultipleSelection,
			Object[] initialSelection, List<ViewerFilter> viewerFilters) {

		WorkspaceResourceDialog dialog = new InterpreterWorkspaceResourceDialog(
				parent, new WorkbenchLabelProvider(),
				new WorkbenchContentProvider(), title, message,
				allowMultipleSelection, initialSelection, viewerFilters);

		return dialog.open() == Window.OK ? dialog.getSelectedFiles()
				: new IFile[0];
	}

	class InterpreterWorkspaceResourceDialog extends WorkspaceResourceDialog {

		public InterpreterWorkspaceResourceDialog(Shell parent,
				ILabelProvider labelProvider,
				ITreeContentProvider contentProvider, String title,
				String message, boolean allowMultipleSelection,
				Object[] initialSelection, List<ViewerFilter> viewerFilters) {
			super(parent, labelProvider, contentProvider);

			this.setTitle(title != null ? title : CommonUIPlugin.INSTANCE
					.getString("_UI_FileSelection_title"));
			this.setMessage(message);
			this.setAllowMultiple(allowMultipleSelection);

			if (initialSelection != null) {
				this.setInitialSelections(initialSelection);
			}
			if (viewerFilters != null) {
				for (ViewerFilter viewerFilter : viewerFilters) {
					this.addFilter(viewerFilter);
				}
			}
			this.loadContents();
		}

		@Override
		public IStatus validate(Object[] selectedElements) {
			if (!allowMultipleSelection) {
				return super.validate(selectedElements);
			}

			boolean enableOK = false;
			for (int i = 0; i < selectedElements.length; i++) {
				if (selectedElements[i] instanceof IFolder) {
					if (getFiles((IFolder) selectedElements[i]).size() > 0) {
						enableOK = true;
					}
				} else if (selectedElements[i] instanceof IFile) {
					if (isValid((IFile) selectedElements[i])) {
						enableOK = true;
					}
				}
				if (enableOK) {
					break;
				}
			}

			return enableOK ? new Status(IStatus.OK,
					"org.eclipse.emf.common.ui", 0, "", null) : new Status(
					IStatus.ERROR, "org.eclipse.emf.common.ui", 0, "", null);
		}

		@Override
		public IFile[] getSelectedFiles() {
			if (!allowMultipleSelection) {
				return super.getSelectedFiles();
			}
			List<IFile> files = new ArrayList<IFile>();
			Object[] result = getResult();

			for (int i = 0; i < result.length; i++) {
				if (result[i] instanceof IFolder) {
					files.addAll(getFiles((IFolder) result[i]));
				} else if (result[i] instanceof IFile) {
					if (isValid((IFile) result[i])) {
						files.add((IFile) result[i]);
					}
				}
			}
			return files.toArray(new IFile[files.size()]);
		}

		private List<IFile> getFiles(IFolder folder) {
			List<IFile> files = new ArrayList<IFile>();

			try {
				for (int i = 0; i < folder.members().length; i++) {
					if (folder.members()[i] instanceof IFolder) {
						files.addAll(getFiles((IFolder) folder.members()[i]));
					} else if (folder.members()[i] instanceof IFile) {
						if (isValid((IFile) folder.members()[i])) {
							files.add((IFile) folder.members()[i]);
						}
					}
				}
			} catch (CoreException e) {
				e.printStackTrace();
			}

			return files;
		}

		private boolean isValid(IFile file) {
			if (fileExtension == null) {
				return true;
			}
			if (file.getFileExtension().equalsIgnoreCase(fileExtension)) {
				return true;
			}
			return false;
		}

	}
}
