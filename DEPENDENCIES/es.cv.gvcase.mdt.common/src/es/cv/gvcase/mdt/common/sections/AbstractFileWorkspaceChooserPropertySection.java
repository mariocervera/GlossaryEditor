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
package es.cv.gvcase.mdt.common.sections;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.ui.action.LoadResourceAction.LoadResourceDialog;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;

import es.cv.gvcase.mdt.common.dialogs.LoadFilteredResourceDialog;

public abstract class AbstractFileWorkspaceChooserPropertySection extends
		AbstractStringPropertySection {

	private Button browseButton;

	@Override
	protected EditingDomain getEditingDomain() {
		try {
			return super.getEditingDomain();
		} catch (IllegalArgumentException ex) {
			return null;
		}
	}

	/**
	 * It creates a label, text and file button widget.
	 */
	@Override
	protected void createWidgets(Composite composite) {
		// Label and text widgets
		super.createWidgets(composite);

		// Button
		browseButton = getWidgetFactory().createButton(composite,
				getButtonText(), SWT.PUSH | SWT.CENTER);

		// If the feature is not changeable, disable the button
		if (getFeature() != null) {
			boolean isChangeable = getFeature().isChangeable();
			browseButton.setEnabled(isChangeable);
		}

		// the text is not editable here
		getText().setEditable(false);
	}

	/**
	 * Layout is set to the created widgets.
	 */
	@Override
	protected void setSectionData(Composite composite) {

		FormData data = new FormData();
		data.left = new FormAttachment(0, getStandardLabelWidth(composite,
				new String[] { getLabelText() }));
		data.right = new FormAttachment(browseButton, 0);
		data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
		data.bottom = new FormAttachment(100, -ITabbedPropertyConstants.VSPACE);
		getText().setLayoutData(data);

		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(getText(),
				-ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(getText(), 0, SWT.TOP);
		getNameLabel().setLayoutData(data);

		data = new FormData();
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(getText(), 0, SWT.CENTER);
		browseButton.setLayoutData(data);
	}

	/**
	 * Hooks listeners both to the text box and to the file selection button.
	 */
	@Override
	protected void hookListeners() {
		super.hookListeners();

		browseButton.addSelectionListener(new ButtonFileSelectionListener());
	}

	/**
	 * Button text. Override if any other text than ... should be displayed.
	 * 
	 * @return it returns the following string: "..."
	 */
	protected String getButtonText() {
		return "...";
	}

	/**
	 * Checks if the given path is well formed and if the file exists. Override
	 * to return true always if no check should be done.
	 * 
	 * @return true if the path is well formed and if the file exists. False
	 *         otherwise.
	 */
	@Override
	protected boolean isTextValid() {
		String filePath = getText().getText();
		String absoluteFilePath;
		URL fileURL;

		// Allow empty empty text
		if (allowEmptyText() && filePath.length() == 0)
			return true;

		// If not empty, check if exists
		try {
			fileURL = new URL(filePath);
		} catch (MalformedURLException e) {
			getStatusLineManager().setErrorMessage(e.getMessage());
			return false;
		}

		try {
			absoluteFilePath = FileLocator.toFileURL(fileURL).getFile();
		} catch (IOException e) {
			getStatusLineManager().setErrorMessage(e.getMessage());
			return false;
		}

		File file = new File(absoluteFilePath);
		if (!file.exists()) {
			getStatusLineManager().setErrorMessage(
					"File: " + getText().getText() + " does not exist.");
			return false;
		}

		// Everything went ok, so the file exists
		getStatusLineManager().setErrorMessage("");
		return true;
	}

	/**
	 * Set a default text in the TextBox of the dialog
	 * 
	 * @return
	 */
	protected String getDefaultText() {
		return getFeatureAsString();
	}

	/**
	 * Listener to be added to the file selection button.
	 * 
	 * @author gmerin
	 * 
	 */
	private class ButtonFileSelectionListener implements SelectionListener {

		// Button browseButton;
		LoadResourceDialog dialog;

		ButtonFileSelectionListener() {
			// this.browseButton = browseButton;
			this.dialog = new LoadFilteredResourceDialog(getActivePage()
					.getActivePart().getSite().getShell(), browseFileSystem(),
					getFilters()) {
				@Override
				public String getDefaultText() {
					return AbstractFileWorkspaceChooserPropertySection.this
							.getDefaultText();
				}
			};
		}

		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);

		}

		public void widgetSelected(SelectionEvent e) {
			dialog.open();
			if (dialog.getReturnCode() == Dialog.OK) {
				String resource = dialog.getURIText() == null ? "" : dialog
						.getURIText();
				// To avoid undesirable characters, we should decode the URI
				// string in order to use it after as an URL
				getText().setText(URI.decode(resource));
				handleTextModified();
			}
		}
	}

	/**
	 * Browse file system button is shown by default. Override this operation
	 * returning false if no browse file system button should be shown.
	 * 
	 * @return true if the file system button should be shown. False otherwise
	 */
	protected Boolean browseFileSystem() {
		return true;
	}

	/**
	 * File text field can be left empty any time. Override this operation
	 * returning false if once the user has selected one valid file, the text
	 * filed cannot be left empty.
	 */
	protected Boolean allowEmptyText() {
		return true;
	}

	/**
	 * No resources are filtered by default. Override this operation if some
	 * resources should be filtered (i.e. images, hidden files and folders,
	 * resources, etc.)
	 * 
	 * @return ArrayList<ViewerFilter> the list of filters
	 */
	protected ArrayList<ViewerFilter> getFilters() {
		ArrayList<ViewerFilter> filters = new ArrayList<ViewerFilter>();
		return filters;
	}

	protected Button getBrowseButton() {
		return browseButton;
	}

}
