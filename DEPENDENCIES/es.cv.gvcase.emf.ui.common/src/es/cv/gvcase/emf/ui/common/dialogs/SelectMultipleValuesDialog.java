/*******************************************************************************
 * Copyright (c) 2012 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Marc Gil Sendra (Prodevelop) – Initial implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.emf.ui.common.dialogs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
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
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PatternFilter;

import es.cv.gvcase.emf.ui.common.swt.AsynchSWTUpdater;
import es.cv.gvcase.emf.ui.common.swt.ISWTUpdater;

/**
 * @author Marc Gil Sendra (mgil@prodevelop.es)
 */
public class SelectMultipleValuesDialog extends Dialog {
	private final ILabelProvider labelProvider;
	private IContentProvider contentProvider;
	private EObject eObject;
	private EStructuralFeature feature;
	private String title;
	private ItemProvider choices;
	private ItemProvider values;
	private EList<Object> result;

	private AsynchSWTUpdater searchAsynchUpdater = AsynchSWTUpdater
			.getInstance();

	public SelectMultipleValuesDialog(EObject eObject,
			EStructuralFeature feature, List<?> choices,
			ILabelProvider labelProvider, String title) {
		super(Display.getCurrent().getActiveShell());
		this.labelProvider = labelProvider;
		AdapterFactory adapterFactory = new ComposedAdapterFactory(Collections
				.<AdapterFactory> emptyList());
		this.contentProvider = new AdapterFactoryContentProvider(adapterFactory);
		this.eObject = eObject;
		this.feature = feature;
		this.title = title;

		this.values = new ItemProvider(adapterFactory, getCurrentValues());
		List<Object> choiceOfValues = new ArrayList<Object>();
		choiceOfValues.addAll(choices);
		choiceOfValues.removeAll(values.getChildren());
		this.choices = new ItemProvider(adapterFactory, choiceOfValues);

		this.result = new BasicEList<Object>();
	}

	@Override
	protected int getShellStyle() {
		return super.getShellStyle() | SWT.RESIZE | SWT.MAX;
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(title);
		shell.setImage(labelProvider.getImage(eObject));
	}

	private List<Object> getCurrentValues() {
		List<Object> list = new ArrayList<Object>();

		if (eObject != null) {
			Object value = eObject.eGet(feature);
			if (value instanceof List<?>) {
				list.addAll((List<?>) value);
			} else {
				list.add(value);
			}
		}

		return list;
	}

	@Override
	protected void okPressed() {
		result.addAll(values.getChildren());
		super.okPressed();
	}

	@Override
	public boolean close() {
		contentProvider.dispose();
		return super.close();
	}

	public List<Object> getResult() {
		return result;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite contents = (Composite) super.createDialogArea(parent);

		GridLayout contentsGridLayout = (GridLayout) contents.getLayout();
		contentsGridLayout.numColumns = 3;

		GridData contentsGridData = (GridData) contents.getLayoutData();
		contentsGridData.horizontalAlignment = SWT.FILL;
		contentsGridData.verticalAlignment = SWT.FILL;

		Composite choiceComposite = new Composite(contents, SWT.NONE);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.horizontalAlignment = SWT.END;
		choiceComposite.setLayoutData(data);
		GridLayout layout = new GridLayout();
		data.horizontalAlignment = SWT.FILL;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.numColumns = 1;
		choiceComposite.setLayout(layout);

		Label choiceLabel = new Label(choiceComposite, SWT.NONE);
		choiceLabel.setText("Available values:");
		data = new GridData();
		data.verticalAlignment = SWT.FILL;
		data.horizontalAlignment = SWT.FILL;
		choiceLabel.setLayoutData(data);

		Composite filterGroupComposite = new Composite(choiceComposite,
				SWT.NONE);
		layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		filterGroupComposite.setLayout(layout);
		data = new GridData(SWT.FILL, SWT.DEFAULT, true, false, 3, 1);
		filterGroupComposite.setLayoutData(data);

		Label label = new Label(filterGroupComposite, SWT.NONE);
		label.setText("Filter "
				+ EMFEditUIPlugin.INSTANCE
						.getString("_UI_Choices_pattern_label"));

		Text patternText = new Text(filterGroupComposite, SWT.BORDER);
		patternText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Table choiceTable = new Table(choiceComposite, SWT.MULTI | SWT.BORDER);
		data = new GridData();
		data.widthHint = Display.getCurrent().getBounds().width / 10;
		data.heightHint = Display.getCurrent().getBounds().height / 6;
		data.verticalAlignment = SWT.FILL;
		data.horizontalAlignment = SWT.FILL;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		choiceTable.setLayoutData(data);

		final TableViewer choiceTableViewer = new TableViewer(choiceTable);
		choiceTableViewer.setContentProvider(new AdapterFactoryContentProvider(
				new AdapterFactoryImpl()));
		choiceTableViewer.setLabelProvider(labelProvider);
		final PatternFilter filter = new PatternFilter() {
			@Override
			protected boolean isParentMatch(Viewer viewer, Object element) {
				return viewer instanceof AbstractTreeViewer
						&& super.isParentMatch(viewer, element);
			}
		};
		choiceTableViewer.addFilter(filter);
		patternText.addListener(SWT.Modify, new Listener() {
			public void handleEvent(final Event e) {
				ISWTUpdater updater = new ISWTUpdater() {
					public void execute() {
						filter.setPattern(((Text) e.widget).getText());
						choiceTableViewer.refresh();
					}
				};
				searchAsynchUpdater.addExecution(e, updater);
			}
		});
		choiceTableViewer.setComparator(new ViewerComparator() {
			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				String s1 = SelectMultipleValuesDialog.this.labelProvider
						.getText(e1);
				String s2 = SelectMultipleValuesDialog.this.labelProvider
						.getText(e2);
				return s1.compareToIgnoreCase(s2);
			}
		});
		choiceTableViewer.setInput(choices);

		Composite controlButtons = new Composite(contents, SWT.NONE);
		data = new GridData(GridData.VERTICAL_ALIGN_CENTER
				| GridData.HORIZONTAL_ALIGN_FILL);
		controlButtons.setLayoutData(data);

		layout = new GridLayout();
		controlButtons.setLayout(layout);

		new Label(controlButtons, SWT.NONE);

		final Button addButton = new Button(controlButtons, SWT.PUSH);
		addButton.setText(EMFEditUIPlugin.INSTANCE.getString("_UI_Add_label"));
		data = new GridData();
		data.verticalAlignment = SWT.FILL;
		data.horizontalAlignment = SWT.FILL;
		addButton.setLayoutData(data);

		final Button removeButton = new Button(controlButtons, SWT.PUSH);
		removeButton.setText(EMFEditUIPlugin.INSTANCE
				.getString("_UI_Remove_label"));
		data = new GridData();
		data.verticalAlignment = SWT.FILL;
		data.horizontalAlignment = SWT.FILL;
		removeButton.setLayoutData(data);

		Label spaceLabel = new Label(controlButtons, SWT.NONE);
		data = new GridData();
		data.verticalSpan = 2;
		spaceLabel.setLayoutData(data);

		final Button upButton = new Button(controlButtons, SWT.PUSH);
		upButton.setText(EMFEditUIPlugin.INSTANCE.getString("_UI_Up_label"));
		data = new GridData();
		data.verticalAlignment = SWT.FILL;
		data.horizontalAlignment = SWT.FILL;
		upButton.setLayoutData(data);

		Button downButton = new Button(controlButtons, SWT.PUSH);
		downButton
				.setText(EMFEditUIPlugin.INSTANCE.getString("_UI_Down_label"));
		data = new GridData();
		data.verticalAlignment = SWT.FILL;
		data.horizontalAlignment = SWT.FILL;
		downButton.setLayoutData(data);

		Composite featureComposite = new Composite(contents, SWT.NONE);
		data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.horizontalAlignment = SWT.END;
		featureComposite.setLayoutData(data);
		layout = new GridLayout();
		data.horizontalAlignment = SWT.FILL;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.numColumns = 1;
		featureComposite.setLayout(layout);

		Label featureLabel = new Label(featureComposite, SWT.NONE);
		featureLabel.setText("Selected values:");
		data = new GridData();
		data.horizontalSpan = 2;
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.FILL;
		featureLabel.setLayoutData(data);

		Table featureTable = new Table(featureComposite, SWT.MULTI | SWT.BORDER);
		data = new GridData();
		data.widthHint = Display.getCurrent().getBounds().width / 10;
		data.heightHint = Display.getCurrent().getBounds().height / 6;
		data.verticalAlignment = SWT.FILL;
		data.horizontalAlignment = SWT.FILL;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		featureTable.setLayoutData(data);

		final TableViewer featureTableViewer = new TableViewer(featureTable);
		featureTableViewer.setContentProvider(contentProvider);
		featureTableViewer.setLabelProvider(labelProvider);
		featureTableViewer.setInput(values);
		if (!values.getChildren().isEmpty()) {
			featureTableViewer.setSelection(new StructuredSelection(values
					.getChildren().get(0)));
		}

		choiceTableViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				if (addButton.isEnabled()) {
					addButton.notifyListeners(SWT.Selection, null);
				}
			}
		});

		featureTableViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				if (removeButton.isEnabled()) {
					removeButton.notifyListeners(SWT.Selection, null);
				}
			}
		});

		upButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				IStructuredSelection selection = (IStructuredSelection) featureTableViewer
						.getSelection();
				int minIndex = 0;
				for (Iterator<?> i = selection.iterator(); i.hasNext();) {
					Object value = i.next();
					int index = values.getChildren().indexOf(value);
					values.getChildren().move(Math.max(index - 1, minIndex++),
							value);
				}
			}
		});

		downButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				IStructuredSelection selection = (IStructuredSelection) featureTableViewer
						.getSelection();
				int maxIndex = values.getChildren().size() - selection.size();
				for (Iterator<?> i = selection.iterator(); i.hasNext();) {
					Object value = i.next();
					int index = values.getChildren().indexOf(value);
					values.getChildren().move(Math.min(index + 1, maxIndex++),
							value);
				}
			}
		});

		addButton.addSelectionListener(new SelectionAdapter() {
			// event is null when choiceTableViewer is double clicked
			@Override
			public void widgetSelected(SelectionEvent event) {
				Object value = null;
				IStructuredSelection selection = (IStructuredSelection) choiceTableViewer
						.getSelection();
				for (Iterator<?> i = selection.iterator(); i.hasNext();) {
					value = i.next();
					if (!values.getChildren().contains(value)) {
						values.getChildren().add(value);
						choices.getChildren().remove(value);
					}
				}
				choiceTableViewer.refresh();
				featureTableViewer.refresh();
				if (value != null) {
					featureTableViewer.setSelection(new StructuredSelection(
							value));
				}
			}
		});

		removeButton.addSelectionListener(new SelectionAdapter() {
			// event is null when featureTableViewer is double clicked
			@Override
			public void widgetSelected(SelectionEvent event) {
				IStructuredSelection selection = (IStructuredSelection) featureTableViewer
						.getSelection();
				Object firstValue = null;
				for (Iterator<?> i = selection.iterator(); i.hasNext();) {
					Object value = i.next();
					if (firstValue == null) {
						firstValue = value;
					}
					values.getChildren().remove(value);
					choices.getChildren().add(value);
				}

				choiceTableViewer.refresh();
				if (!values.getChildren().isEmpty()) {
					featureTableViewer.setSelection(new StructuredSelection(
							values.getChildren().get(0)));
				}

				choiceTableViewer.setSelection(selection);
			}
		});

		return contents;
	}
}
