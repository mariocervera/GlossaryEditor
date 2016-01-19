/***************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte,
 * Generalitat de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Marc Gil Sendra (Prodevelop) - initial API and implementation
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.composites;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ISection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

import es.cv.gvcase.mdt.common.commands.AbstractCommonTransactionalCommmand;
import es.cv.gvcase.mdt.common.util.MDTUtil;

/**
 * This Editable Table only lets to modify eAnnotations that has details without
 * value, only has the keys.
 * 
 * @author mgil
 */
public class ExtendedEObjectsEditableTableComposite extends Composite {

	/** The section. */
	private ISection section;

	/** The EMF edit domain. */
	private TransactionalEditingDomain editingDomain;

	/** The feature. */
	private String featureID;

	/** The widgetFactory to use to create the widgets. */
	private TabbedPropertySheetWidgetFactory widgetFactory;

	/** A EObject from where the property is taken */
	private EModelElement parentEObject = null;

	/** The EObjects table viewer. */
	private TableViewer tableViewer;

	/************/
	/** Widgets */
	/************/

	/** The composite for the table and the filter */
	private Composite compositeTable;

	/** The composite for the buttons */
	private Composite compositeButtons;

	/** The EObjects table. */
	private Table table;

	/** The filter label */
	private Label labelFilter;

	/** The filter */
	private Text textFilter;

	/** The add button. */
	private Button buttonAdd;

	/** The remove button. */
	private Button buttonRemove;

	/** The content provider. */
	private IStructuredContentProvider contentProvider;

	protected static String NAME_COLUMN;

	/**
	 * The Constructor
	 */
	public ExtendedEObjectsEditableTableComposite(Composite parent, int style,
			TabbedPropertySheetWidgetFactory widgetFactory, ISection section,
			String featureID) {

		super(parent, style);

		this.widgetFactory = widgetFactory;
		this.section = section;
		this.featureID = featureID;

		NAME_COLUMN = getColumnName();

		widgetFactory.adapt(this);
		createContents(this);
		setSectionData(this);
		hookListeners();
	}

	/**
	 * Creates the widgets for the table: table and buttons
	 */
	protected void createContents(Composite parent) {
		// the composite for the table and the filter
		compositeTable = widgetFactory.createComposite(parent, SWT.NONE);

		// the filter
		if (canFilter()) {
			labelFilter = widgetFactory.createLabel(compositeTable, "Filter:");
			textFilter = widgetFactory.createText(compositeTable, "",
					SWT.BORDER);
		}

		// the table and her tableViewer
		table = widgetFactory.createTable(compositeTable, SWT.NONE
				| SWT.FULL_SELECTION | SWT.SINGLE);
		tableViewer = new TableViewer(table);
		tableViewer.setColumnProperties(getColumnNames());
		tableViewer.setCellEditors(getCellEditors(table));
		tableViewer.setCellModifier(getCellModifier());
		tableViewer.setContentProvider(getContentProvider());
		tableViewer.setLabelProvider(getLabelProvider());
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		createColumns(table);

		// the composite for the buttons
		compositeButtons = widgetFactory.createComposite(parent, SWT.NONE);

		// add and remove buttons
		buttonAdd = widgetFactory.createButton(compositeButtons, "Add",
				SWT.NONE);
		buttonRemove = widgetFactory.createButton(compositeButtons, "Remove",
				SWT.NONE);

		widgetFactory.adapt(parent);
	}

	/**
	 * Set the sections data for all the widgets
	 */
	protected void setSectionData(Composite parent) {
		parent.setLayout(new FormLayout());

		// the composite for the table and the filter
		FormData data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(85, 0);
		data.top = new FormAttachment(0, 0);
		data.bottom = new FormAttachment(100, 0);
		compositeTable.setLayoutData(data);
		compositeTable.setLayout(new GridLayout(2, false));

		data = new FormData();
		data.left = new FormAttachment(compositeTable, 0);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, 0);
		data.bottom = new FormAttachment(100, 0);
		compositeButtons.setLayoutData(data);
		compositeButtons.setLayout(new GridLayout());

		GridData gData = null;

		// the filter
		if (canFilter()) {
			gData = new GridData();
			gData.widthHint = getStandardLabelWidth(parent,
					new String[] { "Filter:" });
			labelFilter.setLayoutData(gData);

			gData = new GridData(GridData.FILL_HORIZONTAL);
			textFilter.setLayoutData(gData);
		}

		// the table
		gData = new GridData(GridData.FILL_BOTH);
		gData.horizontalSpan = 2;
		table.setLayoutData(gData);

		// the add button
		gData = new GridData(GridData.FILL_HORIZONTAL);
		buttonAdd.setLayoutData(gData);

		// the remove button
		gData = new GridData(GridData.FILL_HORIZONTAL);
		buttonRemove.setLayoutData(gData);
	}

	/**
	 * Sets the listeners for the selected widgets: filter and buttons
	 */
	protected void hookListeners() {
		// listener for filter
		if (textFilter != null) {
			textFilter.addListener(SWT.Modify, new Listener() {

				public void handleEvent(Event e) {
					updateFilters();
				}
			});
		}

		// listener for add button
		if (buttonAdd != null) {
			buttonAdd.addSelectionListener(getAddButtonListener());
		}

		// listener for remove button
		if (buttonRemove != null) {
			buttonRemove.addSelectionListener(getRemoveButtonListener());
		}
	}

	/**
	 * Create the columns for the table, one for every attibute to show
	 */
	protected void createColumns(Table table) {
		int ncolumns = 1;

		for (int i = 0; i < ncolumns; i++) {
			TableColumn column = new TableColumn(table, SWT.LEFT);
			column.setText(getColumnNames()[i]);
			column.setToolTipText(getColumnNames()[i]);
			column.setWidth(getColumnSizes()[i]);
		}
	}

	protected EAnnotation getEAnnotation() {
		EAnnotation eAnnotation = parentEObject.getEAnnotation(featureID);
		return eAnnotation;
	}

	protected EAnnotation createEAnnotation() {
		EAnnotation eAnnotation = parentEObject.getEAnnotation(featureID);

		if (eAnnotation == null) {
			eAnnotation = EcoreFactory.eINSTANCE.createEAnnotation();
			eAnnotation.setSource(featureID);

			Command command = AddCommand.create(editingDomain, parentEObject,
					EcorePackage.eINSTANCE.getEModelElement_EAnnotations(),
					eAnnotation);
			editingDomain.getCommandStack().execute(command);
		}

		return eAnnotation;
	}

	/**
	 * listener for the add button
	 */
	SelectionAdapter addListener = new SelectionAdapter() {
		String newElement = "";

		@Override
		public void widgetSelected(SelectionEvent event) {
			if (parentEObject == null) {
				return;
			}

			// get the eAnnotation for the selected EObject
			final EAnnotation eAnnotation = createEAnnotation();

			AbstractCommonTransactionalCommmand command = new AbstractCommonTransactionalCommmand(
					editingDomain, "Put value in HashMap", null) {

				@Override
				protected CommandResult doExecuteWithResult(
						IProgressMonitor monitor, IAdaptable info)
						throws ExecutionException {
					int i = 1;
					String baseString = "NewLabel";

					while (eAnnotation.getDetails().keySet().contains(
							baseString + i)) {
						i++;
					}

					newElement = baseString + i;

					eAnnotation.getDetails().put(newElement, null);
					return CommandResult.newOKCommandResult();
				}
			};

			if (command != null && command.canExecute()) {
				command.executeInTransaction();
				refresh();
			}

			tableViewer.setSelection(new StructuredSelection(newElement));
		}
	};

	/**
	 * gets the listener for the add button
	 */
	protected SelectionAdapter getAddButtonListener() {
		return addListener;
	}

	/**
	 * listener for the remove button
	 */
	SelectionAdapter removeListener = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent event) {
			if (parentEObject == null) {
				return;
			}
			if (!(table.getSelection().length == 1)) {
				return;
			}

			final Object object = table.getSelection()[0].getData();
			tableViewer.setSelection(null);
			final EAnnotation eAnnotation = createEAnnotation();

			AbstractCommonTransactionalCommmand command = new AbstractCommonTransactionalCommmand(
					editingDomain, "Remove value in HashMap", null) {
				@Override
				protected CommandResult doExecuteWithResult(
						IProgressMonitor monitor, IAdaptable info)
						throws ExecutionException {
					eAnnotation.getDetails().removeKey(object);

					if (eAnnotation.getDetails().size() == 0) {
						parentEObject.getEAnnotations().remove(eAnnotation);
					}

					return CommandResult.newOKCommandResult();
				}
			};

			if (command != null && command.canExecute()) {
				command.executeInTransaction();
				refresh();
			}

			if (tableViewer.getTable().getItems().length > 0) {
				tableViewer.setSelection(new StructuredSelection(tableViewer
						.getTable().getItems()[0].getData()));
			}
		}
	};

	/**
	 * gets the listener for the remove button
	 */
	protected SelectionAdapter getRemoveButtonListener() {
		return removeListener;
	}

	/**
	 * Calculate the width for the label taking into account the text for the
	 * label
	 */
	protected int getStandardLabelWidth(Composite parent, String[] labels) {
		int standardLabelWidth = 65;
		GC gc = new GC(parent);
		int indent = gc.textExtent("XXX").x; //$NON-NLS-1$
		for (int i = 0; i < labels.length; i++) {
			int width = gc.textExtent(labels[i]).x;
			if (width + indent > standardLabelWidth) {
				standardLabelWidth = width + indent;
			}
		}
		gc.dispose();
		return standardLabelWidth;
	}

	/**
	 * gets the value for the feature of the given eObject
	 */
	private Object getValue() {
		EAnnotation eAnnotation = getEAnnotation();
		if (eAnnotation == null) {
			return null;
		}

		String[] array = eAnnotation.getDetails().keySet().toArray(
				new String[0]);

		List<String> list = new ArrayList<String>();
		for (int i = 0; i < array.length; i++) {
			list.add(array[i]);
		}

		Collections.sort(list);

		return list;
	}

	/**
	 * refresh the table
	 */
	public void refresh() {
		if (parentEObject == null) {
			tableViewer.setInput(new BasicEList<EObject>());
			return;
		}

		Object value = getValue();
		if (value == null || !(value instanceof List)) {
			tableViewer.setInput(new BasicEList<EObject>());
			return;
		}

		tableViewer.setInput(value);
	}

	/**
	 * update the filters for the filter field to search values into the table
	 */
	protected void updateFilters() {
		List<ViewerFilter> filters = new ArrayList<ViewerFilter>();
		ViewerFilter filter = getSearchFilter();
		if (this.canFilter() && filter != null) {
			filters.add(filter);
		}

		ViewerFilter[] filtersArray = new ViewerFilter[filters.size()];
		for (int i = 0; i < filters.size(); i++) {
			filtersArray[i] = filters.get(i);
		}

		tableViewer.setFilters(filtersArray);
	}

	/**
	 * gets the first selected item in the table
	 */
	public TableItem getFirstSelected() {
		TableItem firstItem = null;

		if (table.getSelection().length > 0) {
			firstItem = table.getSelection()[0];
		}

		return firstItem;

	}

	/**
	 * gets the first selected element (EObject) in the table
	 */
	public EObject getFirstSelectedEObject() {
		TableItem firstItem = getFirstSelected();

		if (firstItem != null) {
			return (EObject) firstItem.getData();
		}

		return null;

	}

	/**
	 * gets the filter for the filter field
	 */
	protected ViewerFilter getSearchFilter() {
		return new ElementTablesFilter(textFilter);
	}

	protected ILabelProvider getGenericLabelProvider() {
		return MDTUtil.getLabelProvider();
	}

	/**
	 * gets the correct provider for the table composite
	 */
	protected ILabelProvider getLabelProvider() {
		return new EObjectLabelProvider();
	}

	protected IStructuredContentProvider getContentProvider() {
		if (contentProvider == null) {
			contentProvider = new EObjectContentProvider();
		}

		return contentProvider;
	}

	/**
	 * indicates if the filter field should be shown or not
	 */
	protected boolean canFilter() {
		return true;
	}

	/**
	 * sets the parent eObject of the section
	 */
	public void setEObject(EModelElement eo) {
		parentEObject = eo;
		refresh();
	}

	/**
	 * sets the editing domain
	 */
	public void setEditingDomain(TransactionalEditingDomain ed) {
		editingDomain = ed;
	}

	/**
	 * gets the editing domain
	 */
	public TransactionalEditingDomain getEditingDomain() {
		return editingDomain;
	}

	/**
	 * gets the feature for the objects of the table
	 */
	public String getTableFeature() {
		return featureID;
	}

	/**
	 * gets the eObject of this section
	 */
	public EObject getTableEObject() {
		return parentEObject;
	}

	/**
	 * gets the table elements
	 */
	public Table getTable() {
		return table;
	}

	/**
	 * gets the viewer for the table elements
	 */
	public TableViewer getTableViewer() {
		return tableViewer;
	}

	/**
	 * gets the add button
	 */
	public Button getAddButton() {
		return buttonAdd;
	}

	/**
	 * gets the remove button
	 */
	public Button getRemoveButton() {
		return buttonRemove;
	}

	/**
	 * gets this section
	 */
	public ISection getSection() {
		return section;
	}

	/**
	 * gets the name for every column. The length of the array should be the
	 * same given by getColumnSizes and getListOfFeatures
	 */
	protected String[] getColumnNames() {
		String[] names = new String[1];
		for (int i = 0; i < names.length; i++) {
			names[i] = NAME_COLUMN;
		}

		return names;
	}

	protected String getColumnName() {
		return "Text";
	}

	/**
	 * gets the size for every column. The length of the array should be the
	 * same given by getColumnNames and getListOfFeatures
	 */
	protected int[] getColumnSizes() {
		int[] sizes = new int[1];
		for (int i = 0; i < sizes.length; i++) {
			sizes[i] = 500;
		}

		return sizes;
	}

	/**
	 * gets a cell editor to edit every column. The cell editor type depends on
	 * the attribute type
	 */
	protected CellEditor[] getCellEditors(Table table) {
		CellEditor[] editors = new CellEditor[1];
		for (int i = 0; i < editors.length; i++) {
			editors[i] = new TextCellEditor(table);
		}

		return editors;
	}

	/**
	 * gets a cell modifier to save the changes into the model doing something
	 */
	protected ICellModifier getCellModifier() {
		return new TableCellModifier();
	}

	/**
	 * gets the list of features of the table, one for every column. The length
	 * of the array should be the same given by getColumnNames and
	 * getColumnSizes
	 */
	protected EStructuralFeature getFeature() {
		return EcorePackage.eINSTANCE.getEAnnotation_Details();
	}

	/**
	 * label provider class for the table
	 */
	protected class EObjectLabelProvider extends LabelProvider implements
			ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			switch (columnIndex) {
			case 0:
				return getGenericLabelProvider().getImage(element);
			default:
				return null;
			}
		}

		public String getColumnText(Object element, int columnIndex) {
			return element.toString();
		}
	}

	/**
	 * content provider class for the table
	 */
	protected class EObjectContentProvider implements
			IStructuredContentProvider {

		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof List) {
				return ((List<?>) inputElement).toArray();
			}

			return new Object[0];
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}

	/**
	 * generic filter class for table
	 */
	protected class ElementTablesFilter extends ViewerFilter {

		private Text filterText;

		public ElementTablesFilter(Text filterText) {
			this.filterText = filterText;
		}

		@Override
		public boolean select(Viewer viewer, Object parentElement,
				Object element) {
			if (element != null
					&& getGenericLabelProvider().getText(element).contains(
							filterText.getText())) {
				return true;
			}
			return false;
		}

	}

	protected class TableCellModifier implements ICellModifier {

		public boolean canModify(Object element, String property) {
			for (int i = 0; i < getColumnNames().length; i++) {
				if (getColumnNames()[i].equals(property)) {
					return true;
				}
			}

			return false;
		}

		public Object getValue(Object element, String property) {
			return element.toString();
		}

		public void modify(Object element, String property, Object value) {

			if (getEditingDomain() == null) {
				return;
			}
			if (!(element instanceof TableItem)) {
				return;
			}

			final String oldValue = ((TableItem) element).getText().toString();
			final String newValue = value.toString();
			for (int i = 0; i < getColumnNames().length; i++) {
				if (getColumnNames()[i].equals(property)) {
					AbstractCommonTransactionalCommmand command = new AbstractCommonTransactionalCommmand(
							editingDomain, "Change value in HashMap", null) {
						@Override
						protected CommandResult doExecuteWithResult(
								IProgressMonitor monitor, IAdaptable info)
								throws ExecutionException {
							EAnnotation eAnnotation = getEAnnotation();
							eAnnotation.getDetails().removeKey(oldValue);
							eAnnotation.getDetails().put(newValue, null);

							return CommandResult.newOKCommandResult();
						}
					};

					if (command != null && command.canExecute()) {
						command.executeInTransaction();
						refresh();
					}
				}
			}
		}
	}
}
