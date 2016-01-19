/*******************************************************************************
 * Copyright (c) 2002-2007 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   IBM - Initial API and implementation
 * 
 * Copyright (c) 2012 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana. All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Marc Gil Sendra (Prodevelop) - initial API implementation
 *
 ******************************************************************************/
package es.cv.gvcase.emf.ui.common.dialogs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.ui.celleditor.ExtendedComboBoxCellEditor;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.ItemProvider;
import org.eclipse.emf.edit.ui.EMFEditUIPlugin;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.dialogs.PatternFilter;

/**
 * A extended dialog to edit any kind of feature (adapted from
 * org.eclipse.emf.edit.ui.celleditor.FeatureEditorDialog to specify a
 * ContentProvider)
 */
public class FeatureEditorDialog extends Dialog {
	protected ILabelProvider labelProvider;
	protected IContentProvider contentProvider;
	protected Object object;
	protected EClassifier eClassifier;
	protected String displayName;
	protected ItemProvider values;
	protected List<Object> currentValues;
	protected List<?> choiceOfValues;
	protected EList<?> result;
	protected boolean multiLine;

	/**
	 * @param parent
	 *            A container Shell (could not be null)
	 * @param labelProvider
	 *            A label provider for choices values and current values Tree
	 *            (could not be null)
	 * @param contentProvider
	 *            A content provider for choices values and current values Tree
	 *            (should not be null)
	 * @param object
	 *            The object wanted to be edited (could not be null)
	 * @param eClassifier
	 *            The type of the Feature wanted to be edited (could not be
	 *            null)
	 * @param currentValues
	 *            The list of current values of the feature being edited (could
	 *            not be null but empty)
	 * @param displayName
	 *            A display name for the dialog. Typically the name of the
	 *            feature being edited (could be null)
	 * @param choiceOfValues
	 *            A list of available values. If null, a text field should be
	 *            shown to add new values to the list (typically for String
	 *            typed features); if it's not null, a list with this values
	 *            should be shown
	 * @param multiLine
	 *            When a multi-line value is allowed. If false, when enter key
	 *            is pressed the new entered value is added automatically to the
	 *            list
	 * @param sortChoices
	 *            When the list of available values should be ordered or not
	 */
	public FeatureEditorDialog(Shell parent, ILabelProvider labelProvider,
			IContentProvider contentProvider, Object object,
			EClassifier eClassifier, List<Object> currentValues,
			String displayName, List<?> choiceOfValues, boolean multiLine,
			boolean sortChoices) {
		super(parent);
		setShellStyle(getShellStyle() | SWT.RESIZE | SWT.MAX);
		this.labelProvider = labelProvider;
		this.contentProvider = contentProvider;
		this.object = object;
		this.eClassifier = eClassifier;
		this.displayName = displayName;
		this.choiceOfValues = choiceOfValues;
		this.multiLine = multiLine;

		if (contentProvider == null) {
			AdapterFactory adapterFactory = new ComposedAdapterFactory(
					Collections.<AdapterFactory> emptyList());
			values = new ItemProvider(adapterFactory, currentValues);
			this.contentProvider = new AdapterFactoryContentProvider(
					adapterFactory);
		} else {
			this.contentProvider = contentProvider;
			this.currentValues = currentValues;
		}
		if (sortChoices && choiceOfValues != null) {
			this.choiceOfValues = new ArrayList<Object>(choiceOfValues);
			ExtendedComboBoxCellEditor.createItems(this.choiceOfValues,
					labelProvider, true);
		}
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(EMFEditUIPlugin.INSTANCE.getString(
				"_UI_FeatureEditorDialog_title", new Object[] { displayName,
						labelProvider.getText(object) }));
		shell.setImage(labelProvider.getImage(object));
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite contents = (Composite) super.createDialogArea(parent);

		GridLayout contentsGridLayout = (GridLayout) contents.getLayout();
		contentsGridLayout.numColumns = 3;

		GridData contentsGridData = (GridData) contents.getLayoutData();
		contentsGridData.horizontalAlignment = SWT.FILL;
		contentsGridData.verticalAlignment = SWT.FILL;

		Text patternText = null;

		if (choiceOfValues != null) {
			Group filterGroupComposite = new Group(contents, SWT.NONE);
			filterGroupComposite.setText(EMFEditUIPlugin.INSTANCE
					.getString("_UI_Choices_pattern_group"));
			filterGroupComposite.setLayout(new GridLayout(2, false));
			filterGroupComposite.setLayoutData(new GridData(SWT.FILL,
					SWT.DEFAULT, true, false, 3, 1));

			Label label = new Label(filterGroupComposite, SWT.NONE);
			label.setText(EMFEditUIPlugin.INSTANCE
					.getString("_UI_Choices_pattern_label"));

			patternText = new Text(filterGroupComposite, SWT.BORDER);
			patternText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		}

		Composite choiceComposite = new Composite(contents, SWT.NONE);
		{
			GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
			data.horizontalAlignment = SWT.END;
			choiceComposite.setLayoutData(data);

			GridLayout layout = new GridLayout();
			data.horizontalAlignment = SWT.FILL;
			layout.marginHeight = 0;
			layout.marginWidth = 0;
			layout.numColumns = 1;
			choiceComposite.setLayout(layout);
		}

		Label choiceLabel = new Label(choiceComposite, SWT.NONE);
		choiceLabel.setText(choiceOfValues == null ? EMFEditUIPlugin.INSTANCE
				.getString("_UI_Value_label") : EMFEditUIPlugin.INSTANCE
				.getString("_UI_Choices_label"));
		GridData choiceLabelGridData = new GridData();
		choiceLabelGridData.verticalAlignment = SWT.FILL;
		choiceLabelGridData.horizontalAlignment = SWT.FILL;
		choiceLabel.setLayoutData(choiceLabelGridData);

		final Tree choiceTree = choiceOfValues == null ? null : new Tree(
				choiceComposite, SWT.MULTI | SWT.BORDER);
		if (choiceTree != null) {
			GridData choiceTtreeGridData = new GridData();
			choiceTtreeGridData.widthHint = Display.getCurrent().getBounds().width / 5;
			choiceTtreeGridData.heightHint = Display.getCurrent().getBounds().height / 3;
			choiceTtreeGridData.verticalAlignment = SWT.FILL;
			choiceTtreeGridData.horizontalAlignment = SWT.FILL;
			choiceTtreeGridData.grabExcessHorizontalSpace = true;
			choiceTtreeGridData.grabExcessVerticalSpace = true;
			choiceTree.setLayoutData(choiceTtreeGridData);
		}

		final TreeViewer choiceTreeViewer = choiceOfValues == null ? null
				: new TreeViewer(choiceTree);
		if (choiceTreeViewer != null) {
			choiceTreeViewer.setContentProvider(contentProvider);
			choiceTreeViewer.setLabelProvider(labelProvider);
			final PatternFilter filter = new PatternFilter() {
				@Override
				protected boolean isParentMatch(Viewer viewer, Object element) {
					return viewer instanceof AbstractTreeViewer
							&& super.isParentMatch(viewer, element);
				}
			};
			choiceTreeViewer.addFilter(filter);
			assert patternText != null;
			patternText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					filter.setPattern(((Text) e.widget).getText());
					choiceTreeViewer.refresh();
				}
			});
			choiceTreeViewer.setInput(choiceOfValues);

			choiceTreeViewer.expandToLevel(2);
		}

		// We use multi even for a single line because we want to respond to the
		// enter key.
		//
		int style = multiLine ? SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.BORDER : SWT.MULTI | SWT.BORDER;
		final Text choiceText = choiceOfValues == null ? new Text(
				choiceComposite, style) : null;
		if (choiceText != null) {
			GridData choiceTextGridData = new GridData();
			choiceTextGridData.widthHint = Display.getCurrent().getBounds().width / 5;
			choiceTextGridData.verticalAlignment = SWT.BEGINNING;
			choiceTextGridData.horizontalAlignment = SWT.FILL;
			choiceTextGridData.grabExcessHorizontalSpace = true;
			if (multiLine) {
				choiceTextGridData.verticalAlignment = SWT.FILL;
				choiceTextGridData.grabExcessVerticalSpace = true;
			}
			choiceText.setLayoutData(choiceTextGridData);
		}

		Composite controlButtons = new Composite(contents, SWT.NONE);
		GridData controlButtonsGridData = new GridData();
		controlButtonsGridData.verticalAlignment = SWT.FILL;
		controlButtonsGridData.horizontalAlignment = SWT.FILL;
		controlButtons.setLayoutData(controlButtonsGridData);

		GridLayout controlsButtonGridLayout = new GridLayout();
		controlButtons.setLayout(controlsButtonGridLayout);

		new Label(controlButtons, SWT.NONE);

		final Button addButton = new Button(controlButtons, SWT.PUSH);
		addButton.setText(EMFEditUIPlugin.INSTANCE.getString("_UI_Add_label"));
		GridData addButtonGridData = new GridData();
		addButtonGridData.verticalAlignment = SWT.FILL;
		addButtonGridData.horizontalAlignment = SWT.FILL;
		addButton.setLayoutData(addButtonGridData);

		final Button removeButton = new Button(controlButtons, SWT.PUSH);
		removeButton.setText(EMFEditUIPlugin.INSTANCE
				.getString("_UI_Remove_label"));
		GridData removeButtonGridData = new GridData();
		removeButtonGridData.verticalAlignment = SWT.FILL;
		removeButtonGridData.horizontalAlignment = SWT.FILL;
		removeButton.setLayoutData(removeButtonGridData);

		Label spaceLabel = new Label(controlButtons, SWT.NONE);
		GridData spaceLabelGridData = new GridData();
		spaceLabelGridData.verticalSpan = 2;
		spaceLabel.setLayoutData(spaceLabelGridData);

		final Button upButton = new Button(controlButtons, SWT.PUSH);
		upButton.setText(EMFEditUIPlugin.INSTANCE.getString("_UI_Up_label"));
		GridData upButtonGridData = new GridData();
		upButtonGridData.verticalAlignment = SWT.FILL;
		upButtonGridData.horizontalAlignment = SWT.FILL;
		upButton.setLayoutData(upButtonGridData);

		final Button downButton = new Button(controlButtons, SWT.PUSH);
		downButton
				.setText(EMFEditUIPlugin.INSTANCE.getString("_UI_Down_label"));
		GridData downButtonGridData = new GridData();
		downButtonGridData.verticalAlignment = SWT.FILL;
		downButtonGridData.horizontalAlignment = SWT.FILL;
		downButton.setLayoutData(downButtonGridData);

		Composite featureComposite = new Composite(contents, SWT.NONE);
		{
			GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
			data.horizontalAlignment = SWT.END;
			featureComposite.setLayoutData(data);

			GridLayout layout = new GridLayout();
			data.horizontalAlignment = SWT.FILL;
			layout.marginHeight = 0;
			layout.marginWidth = 0;
			layout.numColumns = 1;
			featureComposite.setLayout(layout);
		}

		Label featureLabel = new Label(featureComposite, SWT.NONE);
		featureLabel.setText(EMFEditUIPlugin.INSTANCE
				.getString("_UI_Feature_label"));
		GridData featureLabelGridData = new GridData();
		featureLabelGridData.horizontalSpan = 2;
		featureLabelGridData.horizontalAlignment = SWT.FILL;
		featureLabelGridData.verticalAlignment = SWT.FILL;
		featureLabel.setLayoutData(featureLabelGridData);

		final Tree featureTree = new Tree(featureComposite, SWT.MULTI
				| SWT.BORDER);
		GridData featureTreeGridData = new GridData();
		featureTreeGridData.widthHint = Display.getCurrent().getBounds().width / 5;
		featureTreeGridData.heightHint = Display.getCurrent().getBounds().height / 3;
		featureTreeGridData.verticalAlignment = SWT.FILL;
		featureTreeGridData.horizontalAlignment = SWT.FILL;
		featureTreeGridData.grabExcessHorizontalSpace = true;
		featureTreeGridData.grabExcessVerticalSpace = true;
		featureTree.setLayoutData(featureTreeGridData);

		final TreeViewer featureTreeViewer = new TreeViewer(featureTree);
		featureTreeViewer.setContentProvider(contentProvider);
		featureTreeViewer.setLabelProvider(labelProvider);
		if (values != null) {
			featureTreeViewer.setInput(values);
			if (!values.getChildren().isEmpty()) {
				featureTreeViewer.setSelection(new StructuredSelection(values
						.getChildren().get(0)));
			}
		} else {
			featureTreeViewer.setInput(currentValues);
			if (!currentValues.isEmpty()) {
				featureTreeViewer.setSelection(new StructuredSelection(
						currentValues.get(0)));
			}
		}
		featureTreeViewer.expandToLevel(2);

		if (choiceTreeViewer != null) {
			choiceTreeViewer.addDoubleClickListener(new IDoubleClickListener() {
				public void doubleClick(DoubleClickEvent event) {
					if (addButton.isEnabled()) {
						addButton.notifyListeners(SWT.Selection, null);
					}
				}
			});

			featureTreeViewer
					.addDoubleClickListener(new IDoubleClickListener() {
						public void doubleClick(DoubleClickEvent event) {
							if (removeButton.isEnabled()) {
								removeButton.notifyListeners(SWT.Selection,
										null);
							}
						}
					});
		}

		if (choiceText != null) {
			choiceText.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent event) {
					if (!multiLine
							&& (event.character == '\r' || event.character == '\n')) {
						try {
							Object value = EcoreUtil.createFromString(
									(EDataType) eClassifier, choiceText
											.getText());
							if (values != null) {
								values.getChildren().add(value);
							} else {
								currentValues.add(value);
							}
							choiceText.setText("");
							featureTreeViewer
									.setSelection(new StructuredSelection(value));
							event.doit = false;
						} catch (RuntimeException exception) {
							// Ignore
						}
					} else if (event.character == '\33') {
						choiceText.setText("");
						event.doit = false;
					}
				}
			});
		}

		upButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				IStructuredSelection selection = (IStructuredSelection) featureTreeViewer
						.getSelection();
				int minIndex = 0;
				for (Iterator<?> i = selection.iterator(); i.hasNext();) {
					Object value = i.next();
					if (values != null) {
						int index = values.getChildren().indexOf(value);
						values.getChildren().move(
								Math.max(index - 1, minIndex++), value);
					} else {
						int index = currentValues.indexOf(value);
						currentValues.remove(value);
						currentValues.add(Math.max(index - 1, minIndex++),
								value);
					}
				}
				featureTreeViewer.refresh();
				featureTreeViewer.expandToLevel(2);
				featureTreeViewer.setSelection(selection);
			}
		});

		downButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				IStructuredSelection selection = (IStructuredSelection) featureTreeViewer
						.getSelection();
				int maxIndex = 0;
				if (values != null) {
					maxIndex = values.getChildren().size();
				} else {
					maxIndex = currentValues.size();
				}
				maxIndex = maxIndex - selection.size();
				for (Iterator<?> i = selection.iterator(); i.hasNext();) {
					Object value = i.next();
					if (values != null) {
						int index = values.getChildren().indexOf(value);
						values.getChildren().move(
								Math.min(index + 1, maxIndex++), value);
					} else {
						int index = currentValues.indexOf(value);
						currentValues.remove(value);
						currentValues.add(Math.min(index + 1, maxIndex++),
								value);
					}
				}
				featureTreeViewer.refresh();
				featureTreeViewer.expandToLevel(2);
				featureTreeViewer.setSelection(selection);
			}
		});

		addButton.addSelectionListener(new SelectionAdapter() {
			// event is null when choiceTableViewer is double clicked
			@Override
			public void widgetSelected(SelectionEvent event) {
				if (choiceTreeViewer != null) {
					IStructuredSelection selection = (IStructuredSelection) choiceTreeViewer
							.getSelection();
					for (Iterator<?> i = selection.iterator(); i.hasNext();) {
						Object value = i.next();
						if (!eClassifier.isInstance(value)) {
							continue;
						}
						if (values != null) {
							if (!values.getChildren().contains(value)) {
								values.getChildren().add(value);
							}
						} else {
							if (!currentValues.contains(value)) {
								currentValues.add(value);
							}
						}
					}
					featureTreeViewer.refresh();
					featureTreeViewer.expandToLevel(2);
					featureTreeViewer.setSelection(selection);
				} else if (choiceText != null) {
					try {
						Object value = EcoreUtil.createFromString(
								(EDataType) eClassifier, choiceText.getText());
						if (values != null) {
							values.getChildren().add(value);
						} else {
							currentValues.add(value);
						}
						choiceText.setText("");
						featureTreeViewer.refresh(value);
						featureTreeViewer.setSelection(new StructuredSelection(
								value));
					} catch (RuntimeException exception) {
						// Ignore
					}
				}
			}
		});

		removeButton.addSelectionListener(new SelectionAdapter() {
			// event is null when featureTableViewer is double clicked
			@Override
			public void widgetSelected(SelectionEvent event) {
				IStructuredSelection selection = (IStructuredSelection) featureTreeViewer
						.getSelection();
				Object firstValue = null;
				for (Iterator<?> i = selection.iterator(); i.hasNext();) {
					Object value = i.next();
					if (!eClassifier.isInstance(value)) {
						continue;
					}
					if (firstValue == null) {
						firstValue = value;
					}
					if (values != null) {
						values.getChildren().remove(value);
					} else {
						currentValues.remove(value);
					}
				}

				if (values != null) {
					if (!values.getChildren().isEmpty()) {
						featureTreeViewer.setSelection(new StructuredSelection(
								values.getChildren().get(0)));
					}
				} else {
					if (!currentValues.isEmpty()) {
						featureTreeViewer.setSelection(new StructuredSelection(
								currentValues.get(0)));
					}
				}

				if (choiceTreeViewer != null) {
					choiceTreeViewer.refresh();
					choiceTreeViewer.expandToLevel(2);
					featureTreeViewer.refresh();
					featureTreeViewer.expandToLevel(2);
					choiceTreeViewer.setSelection(selection);
				} else if (choiceText != null) {
					if (firstValue != null) {
						String value = EcoreUtil.convertToString(
								(EDataType) eClassifier, firstValue);
						choiceText.setText(value);
					}
				}
			}
		});

		return contents;
	}

	@Override
	protected void okPressed() {
		if (values != null) {
			result = new BasicEList<Object>(values.getChildren());
		} else {
			result = new BasicEList<Object>(currentValues);
		}
		super.okPressed();
	}

	@Override
	public boolean close() {
		contentProvider.dispose();
		return super.close();
	}

	public EList<?> getResult() {
		return result;
	}
}