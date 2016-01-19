/*******************************************************************************
 * Copyright (c) 2009 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Javier Muñoz (Prodevelop) – Initial implementation
 *
 ******************************************************************************/
package es.cv.gvcase.ide.navigator.dialogs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import es.cv.gvcase.ide.navigator.Activator;
import es.cv.gvcase.ide.navigator.view.MOSKittModelNavigator;

public class NavigatorSearchDialog extends TrayDialog {

	private ITreeContentProvider contentProvider = null;
	private ILabelProvider labelProvider = null;
	private Object root = null;
	private List<Object> matchedObjects = Collections.emptyList();

	private Viewer viewer = null;
	private Label matchesLabel;
	private Text searchText;
	private Button backButton;
	private Button nextButton;
	private Button caseButton;

	public NavigatorSearchDialog(Shell shell,
			MOSKittModelNavigator modelNavigator) {
		super(shell);
		IContentProvider cprovider = modelNavigator.getCommonViewer()
				.getContentProvider();
		if (cprovider instanceof ITreeContentProvider) {
			contentProvider = (ITreeContentProvider) cprovider;
		}
		root = modelNavigator.getCommonViewer().getInput();
		viewer = modelNavigator.getCommonViewer();
		labelProvider = (ILabelProvider) modelNavigator.getCommonViewer()
				.getLabelProvider();
	}

	@Override
	public boolean isHelpAvailable() {
		return false;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite background = new Composite(parent, SWT.None);
		GridData bgData = new GridData(GridData.FILL_BOTH);
		bgData.minimumWidth = 300;
		background.setLayoutData(bgData);
		GridLayout bgLayout = new GridLayout();
		bgLayout.numColumns = 3;
		background.setLayout(bgLayout);

		createSearchTextComposite(background);
		return background;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		backButton = createButton(parent, IDialogConstants.BACK_ID,
				IDialogConstants.BACK_LABEL, false);
		backButton.setEnabled(false);
		backButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ISelection sel = viewer.getSelection();
				if (!(sel instanceof StructuredSelection)) {
					return;
				}
				StructuredSelection ssel = (StructuredSelection) sel;

				int index = matchedObjects.lastIndexOf(ssel.getFirstElement());
				if (index == 0) {
					index = matchedObjects.size() - 1;
				}
				index--;
				if (index < 0) {
					index = 0;
				}
				StructuredSelection ss = new StructuredSelection(matchedObjects
						.get(index));
				viewer.setSelection(ss, true);
			}
		});

		nextButton = createButton(parent, IDialogConstants.NEXT_ID,
				IDialogConstants.NEXT_LABEL, false);
		nextButton.setEnabled(false);
		nextButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ISelection sel = viewer.getSelection();
				if (!(sel instanceof StructuredSelection)) {
					return;
				}
				StructuredSelection ssel = (StructuredSelection) sel;

				int index = matchedObjects.lastIndexOf(ssel.getFirstElement());
				if (index == matchedObjects.size() - 1) {
					index = -1;
				}
				index++;
				StructuredSelection ss = new StructuredSelection(matchedObjects
						.get(index));
				viewer.setSelection(ss, true);
			}
		});

		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				false);
	}

	private void createSearchTextComposite(Composite background) {
		Label searchLabel = new Label(background, SWT.None);
		searchLabel.setText("Search:");
		searchLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

		searchText = new Text(background, SWT.SEARCH | SWT.ICON_CANCEL
				| SWT.ICON_SEARCH);
		searchText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		searchText.setToolTipText("Press 'Enter' key to start the search");
		searchText.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (e.character == '\r') {
					updateMatches();
				}
			}
		});

		Button searchButton = new Button(background, SWT.PUSH);
		searchButton.setLayoutData(new GridData(GridData.END));
		searchButton.setToolTipText("Press to start the search");
		searchButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateMatches();
			}
		});

		Image image = null;
		try {
			String path = FileLocator.toFileURL(
					Platform.getBundle(Activator.PLUGIN_ID).getResource(
							"/icons/full/etool16")).getPath();
			path += "search.gif";
			image = new Image(PlatformUI.getWorkbench().getDisplay(), path);
		} catch (IOException e) {
		}

		if (image != null) {
			searchButton.setImage(image);
		}

		caseButton = new Button(background, SWT.CHECK);
		caseButton.setText("Case sensitive?");
		GridData caseButtonData = new GridData(
				GridData.HORIZONTAL_ALIGN_BEGINNING);
		caseButtonData.horizontalSpan = 3;
		caseButton.setSelection(false);
		caseButton.setLayoutData(caseButtonData);
		caseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateMatches();
			}
		});

		Label resultsLabel = new Label(background, SWT.None);
		resultsLabel.setText("Results:");
		resultsLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

		matchesLabel = new Label(background, SWT.None);
		matchesLabel.setText("No matchings.");
		matchesLabel
				.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING
						| GridData.FILL_HORIZONTAL));
	}

	private void updateMatches() {
		if (contentProvider == null && labelProvider == null) {
			return;
		}

		String pattern = searchText.getText();
		if (pattern.length() == 0) {
			matchedObjects = Collections.emptyList();
			backButton.setEnabled(false);
			nextButton.setEnabled(false);
			matchesLabel.setText("No matchings.");
			return;
		}

		if (!caseButton.getSelection()) {
			pattern = pattern.toUpperCase();
		}

		matchedObjects = searchPattern(pattern, Arrays.asList(contentProvider
				.getElements(root)));

		// Update matches label
		matchesLabel.setText(matchedObjects.size() + " matches found");

		// Select first match and update buttons
		if (!matchedObjects.isEmpty()) {
			viewer.setSelection(new StructuredSelection(matchedObjects.get(0)),
					true);
			nextButton.setEnabled(true);
			backButton.setEnabled(true);
		} else {
			nextButton.setEnabled(false);
			backButton.setEnabled(false);
		}
	}

	private List<Object> searchPattern(String pattern, List<Object> objects) {
		List<Object> matches = new ArrayList<Object>();

		List<Object> childs = new ArrayList<Object>();
		String objectLabel;
		boolean caseSensitive = caseButton.getSelection();
		for (Object o : objects) {
			if (o instanceof Diagram) {
				continue;
			}

			// Search matches in this level
			objectLabel = caseSensitive ? labelProvider.getText(o)
					: labelProvider.getText(o).toUpperCase();

			if (o instanceof EObject && objectLabel.contains(pattern)) {
				matches.add(o);
			}

			// Find childs
			childs.addAll(Arrays.asList(contentProvider.getChildren(o)));
		}

		if (!childs.isEmpty()) {
			matches.addAll(searchPattern(pattern, childs));
		}

		return matches;
	}

}
