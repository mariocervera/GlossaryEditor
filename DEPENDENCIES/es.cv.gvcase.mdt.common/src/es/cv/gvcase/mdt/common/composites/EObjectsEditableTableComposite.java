/***************************************************************************
 * Copyright (c) 2007 Conselleria de Infraestructuras y Transporte,
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
import java.util.Comparator;
import java.util.List;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
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

import es.cv.gvcase.mdt.common.actions.LabelHelper;
import es.cv.gvcase.mdt.common.util.MDTUtil;

/**
 * Creates a Table with support to edit their Cells directly. The table is
 * created with multiple selection. The table has support to filter their
 * objects in order to search an object quickly. Also provides 2 buttons to add
 * or remove new elements. It can be indicated if the elements in the table can
 * be reordered or not, providing 2 other buttons for this purpose. The action
 * of every button can be override to give another behaviour.
 * 
 * @author mgil
 */
public abstract class EObjectsEditableTableComposite extends Composite {

	/** The section. */
	private ISection section;

	/** The EMF edit domain. */
	private EditingDomain editingDomain;

	/** The feature. */
	private EStructuralFeature feature;

	/** The eClass for the table EObject */
	private EClass tableEClass;

	/** The widgetFactory to use to create the widgets. */
	private TabbedPropertySheetWidgetFactory widgetFactory;

	/** A EObject from where the property is taken */
	private EObject parentEObject = null;

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

	/** The up button. */
	private Button buttonUp;

	/** The down button. */
	private Button buttonDown;

	/** The content provider. */
	private IStructuredContentProvider contentProvider;

	/**
	 * The Constructor
	 */
	public EObjectsEditableTableComposite(Composite parent, int style,
			TabbedPropertySheetWidgetFactory widgetFactory, ISection section,
			EStructuralFeature feature, EClass tableEClass) {

		super(parent, style);

		this.widgetFactory = widgetFactory;
		this.section = section;
		this.feature = feature;
		this.tableEClass = tableEClass;

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
		table = widgetFactory.createTable(compositeTable, getTableStyle());
		tableViewer = new TableViewer(table);
		tableViewer.setColumnProperties(getColumnNames());
		tableViewer.setCellEditors(getCellEditors(table));
		tableViewer.setCellModifier(getCellModifier());
		tableViewer.setContentProvider(getContentProvider());
		tableViewer.setLabelProvider(getLabelProvider());
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		setTableItemsListeners(table);

		createColumns(table);

		if (createButtons()) {
			// the composite for the buttons
			compositeButtons = widgetFactory.createComposite(parent, SWT.NONE);

			// add and remove buttons
			buttonAdd = widgetFactory.createButton(compositeButtons,
					getAddButtonLabel(), SWT.NONE);
			buttonRemove = widgetFactory.createButton(compositeButtons,
					getRemoveButtonLabel(), SWT.NONE);

			// up and down buttons
			if (enableOrdering()) {
				buttonUp = widgetFactory.createButton(compositeButtons,
						getUpButtonLabel(), SWT.NONE);
				buttonDown = widgetFactory.createButton(compositeButtons,
						getDownButtonLabel(), SWT.NONE);
			}
		}

		widgetFactory.adapt(parent);
	}

	protected int getTableStyle() {
		return SWT.FULL_SELECTION | SWT.MULTI;
	}

	protected String getAddButtonLabel() {
		return "Add";
	}

	protected String getRemoveButtonLabel() {
		return "Remove";
	}

	protected String getUpButtonLabel() {
		return "Up";
	}

	protected String getDownButtonLabel() {
		return "Down";
	}

	/**
	 * Set the sections data for all the widgets
	 */
	protected void setSectionData(Composite parent) {
		parent.setLayout(new FormLayout());

		// the composite for the table and the filter
		FormData data = new FormData();
		data.left = new FormAttachment(0, 0);
		if (createButtons()) {
			data.right = new FormAttachment(85, 0);
		} else {
			data.right = new FormAttachment(100, 0);
		}
		data.top = new FormAttachment(0, 0);
		data.bottom = new FormAttachment(100, 0);
		compositeTable.setLayoutData(data);
		compositeTable.setLayout(new GridLayout(2, false));

		if (createButtons()) {
			data = new FormData();
			data.left = new FormAttachment(compositeTable, 0);
			data.right = new FormAttachment(100, 0);
			data.top = new FormAttachment(0, 0);
			data.bottom = new FormAttachment(100, 0);
			compositeButtons.setLayoutData(data);
			compositeButtons.setLayout(new GridLayout());
		}

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

		if (createButtons()) {
			// the add button
			gData = new GridData(GridData.FILL_HORIZONTAL);
			buttonAdd.setLayoutData(gData);

			// the remove button
			gData = new GridData(GridData.FILL_HORIZONTAL);
			buttonRemove.setLayoutData(gData);

			if (enableOrdering()) {
				// the up button
				gData = new GridData(GridData.FILL_HORIZONTAL);
				buttonUp.setLayoutData(gData);

				// the down button
				gData = new GridData(GridData.FILL_HORIZONTAL);
				buttonDown.setLayoutData(gData);
			}
		}
	}

	protected boolean createButtons() {
		return true;
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

		// listener for up button
		if (buttonUp != null) {
			buttonUp.addSelectionListener(getUpButtonListener());
		}

		// listener for down button
		if (buttonDown != null) {
			buttonDown.addSelectionListener(getDownButtonListener());
		}
	}

	/**
	 * Create the columns for the table, one for every attibute to show
	 */
	protected void createColumns(Table table) {
		int ncolumns = getListOfFeatures().size();

		for (int i = 0; i < ncolumns; i++) {
			TableColumn column = new TableColumn(table, SWT.LEFT);
			column.setText(getColumnNames()[i]);
			column.setToolTipText(getColumnNames()[i]);
			column.setWidth(getColumnSizes()[i]);
		}
	}

	/**
	 * listener for the add button
	 */
	SelectionAdapter addListener = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent event) {
			if (parentEObject == null)
				return;

			EObject newChild = createNewChild();
			EObject parent = parentEObject;

			String name = LabelHelper.INSTANCE.findName(parent,
					(ENamedElement) newChild);
			Command command1 = SetCommand.create(editingDomain, newChild,
					EcorePackage.eINSTANCE.getENamedElement_Name(), name);

			Command command2 = AddCommand.create(editingDomain, parentEObject,
					feature, newChild);

			editingDomain.getCommandStack().execute(command1);
			editingDomain.getCommandStack().execute(command2);

			refresh();
			tableViewer.setSelection(new StructuredSelection(newChild));
			section.refresh();
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
			if (parentEObject == null)
				return;

			if (table.getSelection().length > 0) {
				Object object = table.getSelection()[0].getData();

				tableViewer.setSelection(null);

				Command command = RemoveCommand.create(editingDomain,
						parentEObject, feature, object);

				editingDomain.getCommandStack().execute(command);

				refresh();

				if (getTable().getItems().length > 0) {
					getTableViewer().setSelection(
							new StructuredSelection(getTable().getItems()[0]
									.getData()));
				}

				section.refresh();
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
	 * listener for the up button
	 */
	SelectionAdapter upListener = new SelectionAdapter() {
		@SuppressWarnings("unchecked")
		public void widgetSelected(SelectionEvent event) {
			if (parentEObject == null)
				return;

			if (tableViewer.getSelection() instanceof StructuredSelection) {
				Object elementToMove = ((StructuredSelection) tableViewer
						.getSelection()).getFirstElement();
				if (elementToMove != null) {
					Object o = parentEObject.eGet(feature);
					if (o instanceof EList) {
						EList<Object> list = (EList<Object>) o;
						if (list.indexOf(elementToMove) > 0) {
							list.move(list.indexOf(elementToMove) - 1,
									elementToMove);
							refresh();
						}
					}
				}
			}
		}
	};

	/**
	 * gets the listener for the up button
	 */
	protected SelectionAdapter getUpButtonListener() {
		return upListener;
	}

	/**
	 * listener for the down button
	 */
	SelectionAdapter downListener = new SelectionAdapter() {
		@SuppressWarnings("unchecked")
		public void widgetSelected(SelectionEvent event) {
			if (parentEObject == null)
				return;

			if (tableViewer.getSelection() instanceof StructuredSelection) {
				Object elementToMove = ((StructuredSelection) tableViewer
						.getSelection()).getFirstElement();
				if (elementToMove != null) {
					Object o = parentEObject.eGet(feature);
					if (o instanceof EList) {
						EList<Object> list = (EList<Object>) o;
						if (list.indexOf(elementToMove) < list.size() - 1) {
							list.move(list.indexOf(elementToMove) + 1,
									elementToMove);
							refresh();
						}
					}
				}
			}
		}
	};

	/**
	 * gets the listener for the down button
	 */
	protected SelectionAdapter getDownButtonListener() {
		return downListener;
	}

	/**
	 * Add listeners to the Items in the Table i.e. to show a ToolTip when the
	 * mouse is over one TableItem. By default, do nothing
	 */
	protected void setTableItemsListeners(Table table) {
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
	 * Creates a new child for the table
	 */
	protected EObject createNewChild() {
		EFactory factory = getTableEClass().getEPackage().getEFactoryInstance();

		if (factory != null && !(getTableEClass().isAbstract())) {
			return factory.create(getTableEClass());
		}
		return null;
	}

	/**
	 * gets the eClass for the objects in the table
	 */
	protected EClass getTableEClass() {
		return tableEClass;
	}

	/**
	 * gets the value for the feature of the given eObject
	 */
	protected Object getValue(EObject object, EStructuralFeature feature) {
		if (feature == null)
			return null;

		EObject container = object;

		boolean exist = false;
		while (!exist && container != null) {
			if (container.eClass().getEAllStructuralFeatures()
					.contains(feature))
				exist = true;
			else
				container = container.eContainer();
		}

		if (exist)
			return container.eGet(feature);
		else
			return null;
	}

	/**
	 * refresh the table
	 */
	@SuppressWarnings("unchecked")
	public void refresh() {
		if (parentEObject == null) {
			tableViewer.setInput(new BasicEList<EObject>());
		} else {
			EList<EObject> tableElements = new BasicEList<EObject>();
			Object value = getValue(parentEObject, feature);
			if (value instanceof EList) {
				EList<EObject> list = (EList<EObject>) value;

				for (EObject eo : list) {
					if (passFilter(tableEClass, eo)) {
						tableElements.add(eo);
					}
				}
			}

			if (sortTableItems()) {
				Collections.sort(tableElements, new SortElements());
			}

			tableViewer.setInput(tableElements);
		}
	}

	/**
	 * Indicates if the items in the table should be ordered by its Text
	 * returned by the Generic LabelProvider
	 * 
	 * @return
	 */
	protected boolean sortTableItems() {
		return false;
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
	 * indicates the given eObject pass this filter to show it in the table
	 */
	protected boolean passFilter(EClass featureClass, EObject eo) {
		return true;
	}

	/**
	 * indicates if the filter field should be shown or not
	 */
	protected boolean canFilter() {
		return true;
	}

	/**
	 * indicates if the ordering buttons should be enabled or not
	 */
	protected boolean enableOrdering() {
		return true;
	}

	/**
	 * sets the parent eObject of the section
	 */
	public void setEObject(EObject eo) {
		parentEObject = eo;
		refresh();
	}

	/**
	 * sets the editing domain
	 */
	public void setEditingDomain(EditingDomain ed) {
		editingDomain = ed;
	}

	/**
	 * sets the feature
	 */
	public void setFeature(EStructuralFeature f) {
		feature = f;
	}

	/**
	 * gets the editing domain
	 */
	public EditingDomain getEditingDomain() {
		return editingDomain;
	}

	/**
	 * gets the feature for the objects of the table
	 */
	public EStructuralFeature getTableFeature() {
		return feature;
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
	 * gets the up button
	 */
	public Button getUpButton() {
		return buttonUp;
	}

	/**
	 * gets the down button
	 */
	public Button getDownButton() {
		return buttonDown;
	}

	/**
	 * gets the text filter
	 */
	public Text getTextFilter() {
		return textFilter;
	}

	/**
	 * gets this section
	 */
	public ISection getSection() {
		return section;
	}

	protected Object getValueForSelectedEStructuralFeature(EObject object, int i) {
		if (i >= getListOfFeatures().size())
			return null;

		return getValue(object, getListOfFeatures().get(i));
	}

	/**
	 * gets the name for every column. The length of the array should be the
	 * same given by getColumnSizes and getListOfFeatures
	 */
	protected abstract String[] getColumnNames();

	/**
	 * gets the size for evety column. The length of the array should be the
	 * same given by getColumnNames and getListOfFeatures
	 */
	protected abstract int[] getColumnSizes();

	/**
	 * gets a cell editor to edit every column. The cell editor type depends on
	 * the attribute type
	 */
	protected abstract CellEditor[] getCellEditors(Table table);

	/**
	 * gets a cell modifier to save the changes into the model doing something
	 */
	protected abstract ICellModifier getCellModifier();

	/**
	 * gets the list of features of the table, one for every column. The length
	 * of the array should be the same given by getColumnNames and
	 * getColumnSizes
	 */
	protected abstract List<EStructuralFeature> getListOfFeatures();

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
			return getGenericLabelProvider().getText(
					getValueForSelectedEStructuralFeature((EObject) element,
							columnIndex));
		}
	}

	/**
	 * content provider class for the table
	 */
	class EObjectContentProvider implements IStructuredContentProvider {

		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof EList) {
				return ((EList<?>) inputElement).toArray();
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
	class ElementTablesFilter extends ViewerFilter {

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

	public class SortElements implements Comparator<Object> {
		public int compare(Object o1, Object o2) {
			String name1 = MDTUtil.getLabelProvider().getText(o1);
			String name2 = MDTUtil.getLabelProvider().getText(o2);

			if (name1.toLowerCase().compareTo(name2.toLowerCase()) < 0)
				return -1;
			else if (name1.toLowerCase().compareTo(name2.toLowerCase()) == 0)
				return 0;
			else
				return 1;
		}
	}
}
