/*******************************************************************************
 * Copyright (c) 2011 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana. All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *  Miguel Llacer (Prodevelop) [mllacer@prodevelop.es] - initial API implementation
 ******************************************************************************/
package es.cv.gvcase.emf.ui.common.composites;

import java.io.File;
import java.net.URI;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import es.cv.gvcase.emf.ui.common.dialogs.LoadFolderDialog;
import es.cv.gvcase.emf.ui.common.internal.Messages;

public class SelectNewModelComposite extends SelectResourceComposite {

	private Text folderValueText;
	private Label folderSelectionLabel;
	private Button browseButton;
	private Text fileNameText;
	private ControlDecoration folderDecoration;
	private ControlDecoration fileDecoration;

	public SelectNewModelComposite(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	protected void createControls(Composite parent) {
		GridLayout layout = new GridLayout(1, false);
		this.setLayout(layout);

		Group group = createGroupField(parent);
		group.setText("");
		GridLayout groupLayout = new GridLayout(3, false);
		groupLayout.horizontalSpacing = 10;
		group.setLayout(groupLayout);

		folderSelectionLabel = new Label(group, SWT.LEFT);
		folderSelectionLabel.setText(Messages
				.getString("RequestCompareParametersPage.Folder") + " :"); //$NON-NLS-2$

		folderValueText = createTextField(group);

		folderDecoration = new ControlDecoration(folderValueText, SWT.LEFT);
		folderDecoration.setMarginWidth(3);

		browseButton = createButton(group);
		browseButton
				.setText(Messages
						.getString("RequestCompareParametersPage.SelectFolder") + "..."); //$NON-NLS-2$

		browseButton.addSelectionListener(getSelectionListener(browseButton,
				folderValueText));

		Label fileNameLabel = new Label(group, SWT.LEFT);
		fileNameLabel.setText(Messages
				.getString("RequestCompareParametersPage.FileName") + " :"); //$NON-NLS-2$

		fileNameText = new Text(group, SWT.SINGLE | SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.verticalAlignment = SWT.CENTER;
		data.grabExcessVerticalSpace = false;
		data.grabExcessHorizontalSpace = true;
		data.horizontalSpan = 2;
		fileNameText.setLayoutData(data);

		fileDecoration = new ControlDecoration(fileNameText, SWT.LEFT);
		fileDecoration.setMarginWidth(3);

		updateFieldDecorators();
	}

	protected Group createGroupField(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.verticalAlignment = SWT.CENTER;
		data.grabExcessVerticalSpace = false;
		group.setLayoutData(data);
		return group;
	}

	protected SelectionListener getSelectionListener(Button button,
			Text valueText) {
		return new ButtonFileSelectionListener(button, new LoadFolderDialog(
				getShell()), valueText);
	}

	@Override
	public String getResourceSelected() {
		if (folderValueText.getText() != null
				&& !folderValueText.getText().equals("")
				&& fileNameText.getText() != null
				&& !fileNameText.getText().equals("")) {
			if (folderValueText.getText().endsWith("/")) {
				return folderValueText.getText().concat(fileNameText.getText());
			} else {
				return folderValueText.getText().concat("/").concat(
						fileNameText.getText());
			}
		}
		return "";
	}

	@Override
	public void setResourceSelected(String resourcePath) {
		if (isContainer(resourcePath)) { // is a container (Project o Folder)
			this.folderValueText.setText(resourcePath);
		} else {
			IPath path = new Path(resourcePath);
			this.fileNameText.setText(path.lastSegment());
			this.folderValueText.setText(path.removeLastSegments(1).toString());
		}
	}

	private boolean isContainer(String resourceString) {
		URI resourceURI = URI.create(resourceString);

		if (resourceURI.getScheme().equals("platform")) {
			IResource r = ResourcesPlugin.getWorkspace().getRoot().findMember(
					resourceString.replaceFirst("platform:/resource", ""));
			return r instanceof IContainer;
		} else if (resourceURI.getScheme().equals("file")) {
			// Check if folder exists in updateFieldDecorators()
			return true;
		}
		return false;
	}

	@Override
	public void updateFieldDecorators() {
		// folderText decoration
		this.folderDecoration.hide();

		String folderPathString = folderValueText.getText();
		String decorationFolderDescription = "";
		Image decorationFolderImage = null;
		if (folderPathString.equals("")) {
			decorationFolderDescription = "Folder path can not be empty";
			decorationFolderImage = FieldDecorationRegistry.getDefault()
					.getFieldDecoration(FieldDecorationRegistry.DEC_ERROR)
					.getImage();
			this.folderDecoration.show();
		} else {
			if (folderValueText.getText().startsWith("platform:")) {
				IPath myPath = new Path(folderValueText.getText().replaceAll(
						"platform:/resource/", ""));
				myPath.addTrailingSeparator();
				try {
					if (myPath.segmentCount() == 1) {
						myPath.lastSegment();
						IProject project = ResourcesPlugin.getWorkspace()
								.getRoot().getProject(myPath.lastSegment());
						if (!project.isAccessible()) {
							decorationFolderDescription = "Unable to read the folder";
							decorationFolderImage = FieldDecorationRegistry
									.getDefault().getFieldDecoration(
											FieldDecorationRegistry.DEC_ERROR)
									.getImage();
							this.folderDecoration.show();
						}
					} else {

						IFolder folder = ResourcesPlugin.getWorkspace()
								.getRoot().getFolder(myPath);
						if (!folder.isAccessible()) {
							decorationFolderDescription = "Unable to read the folder";
							decorationFolderImage = FieldDecorationRegistry
									.getDefault().getFieldDecoration(
											FieldDecorationRegistry.DEC_ERROR)
									.getImage();
							this.folderDecoration.show();
						}
					}
				} catch (IllegalArgumentException e) {
					decorationFolderDescription = "Unable to read the folder";
					decorationFolderImage = FieldDecorationRegistry
							.getDefault().getFieldDecoration(
									FieldDecorationRegistry.DEC_ERROR)
							.getImage();
					this.folderDecoration.show();
				}
			} else if (folderValueText.getText().startsWith("file:")) {
				File file = new File(folderValueText.getText().replace(
						"file:/", ""));
				if (!Platform.getOS().equals(Platform.OS_WIN32)) {
					file = new File(folderValueText.getText().replace("file:",
							""));
				}
				if (!file.exists() || !file.isDirectory()) {
					decorationFolderDescription = "Unable to read the folder";
					decorationFolderImage = FieldDecorationRegistry
							.getDefault().getFieldDecoration(
									FieldDecorationRegistry.DEC_ERROR)
							.getImage();
					this.folderDecoration.show();
				}
			} else {
				this.folderDecoration.show();
			}
		}

		this.folderDecoration.setDescriptionText(decorationFolderDescription);
		this.folderDecoration.setImage(decorationFolderImage);

		// fileText decoration
		this.fileDecoration.hide();
		String fileNameString = this.fileNameText.getText();
		String decorationFileDescription = "";
		Image decorationFileImage = null;
		if (fileNameString.equals("")) {
			decorationFileDescription = "File name can not be empty";
			decorationFileImage = FieldDecorationRegistry.getDefault()
					.getFieldDecoration(FieldDecorationRegistry.DEC_ERROR)
					.getImage();
			this.fileDecoration.show();
		}

		this.fileDecoration.setDescriptionText(decorationFileDescription);
		this.fileDecoration.setImage(decorationFileImage);
	}

	@Override
	public void addModifyListener(ModifyListener listener) {
		super.addModifyListener(listener);
		if (this.getModifyListener() != null) {
			folderValueText.addModifyListener(this.getModifyListener());
			fileNameText.addModifyListener(this.getModifyListener());
		}
	}

	private class ButtonFileSelectionListener implements SelectionListener {

		LoadFolderDialog dialog;
		Text textField;

		ButtonFileSelectionListener(Button browseButton,
				LoadFolderDialog dialog, Text textField) {
			this.dialog = dialog;
			this.textField = textField;
		}

		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);

		}

		public void widgetSelected(SelectionEvent e) {
			if (dialog.open() == Window.CANCEL) {
				return;
			}
			String resource = dialog.getURIText() == null ? "" : dialog
					.getURIText();
			textField.setText(resource);
			setResourceSelected(resource);
		}

	}

}
