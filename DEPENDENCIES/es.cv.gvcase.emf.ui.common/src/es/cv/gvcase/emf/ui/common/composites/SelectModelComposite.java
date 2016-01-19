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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFileState;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.team.core.history.IFileRevision;
import org.eclipse.team.internal.core.history.LocalFileRevision;
import org.eclipse.team.internal.ui.history.LocalHistoryTableProvider;

import es.cv.gvcase.emf.ui.common.dialogs.LoadEMFModelDialog;
import es.cv.gvcase.emf.ui.common.internal.Messages;

public class SelectModelComposite extends SelectResourceComposite {

	private Text paramValueText;
	private Label paramNameLabel;
	private Button browseButton;
	private ControlDecoration decoration;

	private TreeViewer treeViewer;
	private LocalFileHistoryTableProvider historyTableProvider;

	public SelectModelComposite(Composite parent, int style) {
		super(parent, style);
	}

	public SelectModelComposite(Composite parent, int style, boolean showHistory) {
		super(parent, style, showHistory);
	}

	@Override
	protected void createControls(Composite parent) {
		GridLayout layout = new GridLayout(3, false);
		layout.horizontalSpacing = 10;
		this.setLayout(layout);

		paramNameLabel = new Label(parent, SWT.LEFT);
		paramNameLabel.setText(getLabelText() + ":"); //$NON-NLS-1$

		paramValueText = createTextField(parent);

		decoration = new ControlDecoration(paramValueText, SWT.LEFT);
		decoration.setMarginWidth(3);
		updateFieldDecorators();

		browseButton = createButton(parent);
		browseButton.setText(Messages
				.getString("RequestCompareParametersPage.ButtonText") + "..."); //$NON-NLS-2$

		browseButton.addSelectionListener(getSelectionListener(browseButton,
				paramValueText));

		if (showFileHistory()) {
			Composite historyComposite = new Composite(parent, SWT.BORDER);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = 3;
			gd.heightHint = 150;
			historyComposite.setLayoutData(gd);

			GridLayout historyLayout = new GridLayout();
			historyLayout.marginWidth = 1;
			historyLayout.marginHeight = 1;
			historyComposite.setLayout(historyLayout);

			treeViewer = createTree(historyComposite);
		}
	}

	protected String getLabelText() {
		return Messages.getString("RequestCompareParametersPage.LabelText");
	}

	protected SelectionListener getSelectionListener(Button button,
			Text valueText) {
		return new ButtonFileSelectionListener(button, new LoadEMFModelDialog(
				getShell()), valueText);

	}

	@Override
	public String getResourceSelected() {
		return paramValueText.getText();
	}

	@Override
	public IFileRevision getRevisionSelected() {
		if (treeViewer == null
				|| !(treeViewer.getSelection() instanceof IStructuredSelection)) {
			return null;
		}
		Object selection = ((IStructuredSelection) treeViewer.getSelection())
				.getFirstElement();
		if (selection == null || !(selection instanceof IFileRevision)) {
			return null;
		}
		return (IFileRevision) selection;
	}

	@Override
	public void setResourceSelected(String resourcePath) {
		this.paramValueText.setText(resourcePath);
	}

	@Override
	public void updateFieldDecorators() {
		String resourcePathString = paramValueText.getText();
		String decorationDescription = "";
		Image decorationImage = null;

		this.decoration.hide();

		if (showFileHistory()) {
			// show history revisions
			inputSet(resourcePathString);
		}

		if (resourcePathString.equals("")) {
			decorationDescription = "Resource path can not be empty";
			decorationImage = FieldDecorationRegistry.getDefault()
					.getFieldDecoration(FieldDecorationRegistry.DEC_ERROR)
					.getImage();
			this.decoration.show();
		} else {
			if (!isFileValid()) {
				decorationDescription = "'" //$NON-NLS-1$
						+ paramValueText.getText()
						+ "' " //$NON-NLS-1$
						+ Messages
								.getString("RequestCompareParametersPage.ResourceNotExist");
				decorationImage = FieldDecorationRegistry.getDefault()
						.getFieldDecoration(FieldDecorationRegistry.DEC_ERROR)
						.getImage();
				this.decoration.show();
			}
		}

		this.decoration.setDescriptionText(decorationDescription);
		this.decoration.setImage(decorationImage);
	}

	@Override
	public void addModifyListener(ModifyListener listener) {
		super.addModifyListener(listener);
		if (this.getModifyListener() != null) {
			paramValueText.addModifyListener(this.getModifyListener());
		}
	}

	private class ButtonFileSelectionListener implements SelectionListener {

		LoadEMFModelDialog dialog;
		Text textField;

		ButtonFileSelectionListener(Button browseButton,
				LoadEMFModelDialog dialog, Text textField) {
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
			if (showFileHistory()) {
				inputSet(resource);
			}
		}

	}

	protected TreeViewer createTree(Composite parent) {
		historyTableProvider = new LocalFileHistoryTableProvider();
		TreeViewer viewer = historyTableProvider.createTree(parent);
		Rectangle bounds = viewer.getTree().getBounds();
		// hack to not show horizontal scroll bar
		viewer.getTree().setBounds(bounds.x, bounds.y, 100, bounds.height);
		viewer.setContentProvider(new LocalHistoryContentProvider());
		return viewer;
	}

	protected IFile getFile(String fileName) {
		if (fileName == null) {
			return null;
		}
		IPath path = new Path(fileName.replace("platform:/resource", ""));
		return ResourcesPlugin.getWorkspace().getRoot().getFile(path);
	}

	protected void inputSet(String resource) {
		if (treeViewer == null) {
			return;
		}

		// blank current input
		treeViewer.setInput(null);

		IFile file = getFile(resource);
		if (file == null) {
			return;
		}

		IFileState[] fileStates = null;

		try {
			fileStates = file.getHistory(null);
		} catch (CoreException e) {
			e.printStackTrace();
		}

		if (fileStates == null) {
			return;
		}

		// set history revisions to the tree viewer.
		int numRevisions = fileStates.length + 1;
		IFileRevision[] revisions = new LocalFileRevision[numRevisions];
		for (int i = 0; i < fileStates.length; i++) {
			revisions[i] = new LocalFileRevision(fileStates[i]);
		}
		revisions[fileStates.length] = new LocalFileRevision(file);

		treeViewer.setInput(revisions);
		treeViewer.setSelection(new StructuredSelection(
				revisions[fileStates.length]));
	}

	private class LocalFileHistoryTableProvider extends
			LocalHistoryTableProvider {
		protected IFileRevision adaptToFileRevision(Object element) {
			// Get the log entry for the provided object
			IFileRevision entry = null;
			if (element instanceof IFileRevision) {
				entry = (IFileRevision) element;
			}
			return entry;
		}

		private long getCurrentRevision() {
			if (paramValueText != null) {
				IFile file = getFile(paramValueText.getText());
				if (file != null) {
					return file.getLocalTimeStamp();
				}
			}

			return -1;
		}

		protected long getModificationDate(Object element) {
			IFileRevision entry = adaptToFileRevision(element);
			if (entry != null)
				return entry.getTimestamp();
			return -1;
		}

		protected boolean isCurrentEdition(Object element) {
			IFileRevision entry = adaptToFileRevision(element);
			if (entry == null)
				return false;
			long timestamp = entry.getTimestamp();
			long tempCurrentTimeStamp = getCurrentRevision();
			return (tempCurrentTimeStamp != -1 && tempCurrentTimeStamp == timestamp);
		}

		protected boolean isDeletedEdition(Object element) {
			IFileRevision entry = adaptToFileRevision(element);
			return (!entry.exists());
		}
	}

	private final class LocalHistoryContentProvider implements
			ITreeContentProvider {
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof IFileRevision[]) {
				return (IFileRevision[]) inputElement;
			}
			return new Object[0];
		}

		public void dispose() {
			// Nothing to do
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// Nothing to do
		}

		public Object[] getChildren(Object parentElement) {
			return new Object[0];
		}

		public Object getParent(Object element) {
			return null;
		}

		public boolean hasChildren(Object element) {
			return false;
		}
	}

}
