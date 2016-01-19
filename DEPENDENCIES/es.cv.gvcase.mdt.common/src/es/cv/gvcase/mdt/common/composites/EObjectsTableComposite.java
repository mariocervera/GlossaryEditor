/*******************************************************************************
 * Copyright (c) 2005 AIRBUS FRANCE. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Copyright (c) of modifications Conselleria de Infraestructuras y Transporte, 
 * Generalitat de la Comunitat Valenciana. All rights reserved. Modifications 
 * are made available under the terms of the Eclipse Public License v1.0.
 * 
 * Contributors:
 * Jacques Lescot (Anyware Technologies) - initial API and implementation
 * Javier Mu�oz (Integranova) - support to use with GMF editors
 * Mario Cervera Ubeda (Integranova)
 * 				
 ******************************************************************************/
package es.cv.gvcase.mdt.common.composites;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
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
import es.cv.gvcase.mdt.common.internal.Messages;
import es.cv.gvcase.mdt.common.util.MDTUtil;

/**
 * The Class EObjectsTableComposite.
 * 
 * @author jlescot
 */
public class EObjectsTableComposite extends Composite {

	/**
	 * A boolean that store if refreshing is happening and no model
	 * modifications should be performed.
	 */
	private boolean isRefreshing = false;

	/** The EObject that contains the EObjects from the table. */
	private EObject eobject;

	/** The EMF edit domain. */
	private EditingDomain EMFEditDomain;

	/** The widgetFactory to use to create the widgets. */
	private TabbedPropertySheetWidgetFactory widgetFactory;

	/** Label for the filter */
	private Label filterLabel;

	/** The filter */
	private Text filterText;

	/** The E objects table viewer. */
	private TableViewer EObjectsTableViewer;

	/** The E objects table. */
	private Table EObjectsTable;

	/** The column names. */
	private String[] columnNames;

	/** The add button. */
	private Button addButton;

	/** The remove button. */
	private Button removeButton;

	/** The section. */
	private ISection section;

	/** The feature. */
	private EStructuralFeature feature;

	/** The feature class. */
	private EClass featureClass;

	/** The add button selection adapter. */
	private SelectionAdapter addButtonSelectionAdapter = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent event) {
			Object newChild = getNewChild();
			EObject parent = eobject;

			String name = LabelHelper.INSTANCE.findName(parent,
					(ENamedElement) newChild);
			SetCommand setCommand = (SetCommand) SetCommand.create(
					EMFEditDomain, newChild, EcorePackage.eINSTANCE
							.getENamedElement_Name(), name);

			AddCommand addCommand = (AddCommand) AddCommand.create(
					EMFEditDomain, eobject, getFeature(), newChild);

			EMFEditDomain.getCommandStack().execute(setCommand);
			EMFEditDomain.getCommandStack().execute(addCommand);

			refresh();
			EObjectsTableViewer.setSelection(new StructuredSelection(newChild));
			section.refresh();

		}
	};

	/** The remove button selection adapter. */
	private SelectionAdapter removeButtonSelectionAdapter = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent event) {
			if (EObjectsTable.getSelection().length > 0) {
				Object object = EObjectsTable.getSelection()[0].getData();

				EObjectsTableViewer.setSelection(null);

				RemoveCommand command = (RemoveCommand) RemoveCommand.create(
						EMFEditDomain, eobject, getFeature(), object);

				EMFEditDomain.getCommandStack().execute(command);

				refresh();
				section.refresh();
			}
		}
	};

	/**
	 * The constructor.
	 * 
	 * @param parent
	 *            a widget which will be the parent of the new instance (cannot
	 *            be null)
	 * @param style
	 *            the style of widget to construct
	 * @param widgetFactory
	 *            the widgetFactory to use to create the widgets
	 * @param section
	 *            the section
	 * @param feature
	 *            the feature
	 * @param tableName
	 *            the table name
	 */
	public EObjectsTableComposite(Composite parent, int style,
			TabbedPropertySheetWidgetFactory widgetFactory, ISection section,
			EStructuralFeature feature, String tableName) {
		super(parent, style);

		this.columnNames = new String[] { tableName };

		this.widgetFactory = widgetFactory;
		setLayout(new GridLayout(2, false));

		this.section = section;
		this.feature = feature;

		widgetFactory.adapt(this);
		createContents(this);
	}

	/**
	 * Gets the e objects table viewer.
	 * 
	 * @return the e objects table viewer
	 */
	public TableViewer getEObjectsTableViewer() {
		return EObjectsTableViewer;
	}

	/**
	 * Gets the e objects table.
	 * 
	 * @return the e objects table
	 */
	public Table getEObjectsTable() {
		return EObjectsTable;
	}

	/**
	 * Gets the adds the button.
	 * 
	 * @return the adds the button
	 */
	public Button getAddButton() {
		return addButton;
	}

	/**
	 * Gets the removes the button.
	 * 
	 * @return the removes the button
	 */
	public Button getRemoveButton() {
		return removeButton;
	}

	/**
	 * Gets the section.
	 * 
	 * @return the section
	 */
	public ISection getSection() {
		return section;
	}

	/**
	 * Sets the feature class.
	 * 
	 * @param featureClass
	 *            the new feature class
	 */
	public void setFeatureClass(EClass featureClass) {
		this.featureClass = featureClass;
	}

	/**
	 * Gets the feature class.
	 * 
	 * @return the feature class
	 */
	public EClass getFeatureClass() {
		return featureClass;
	}

	/**
	 * Gets the e object.
	 * 
	 * @return the e object
	 */
	public EObject getEObject() {
		return eobject;
	}

	/**
	 * Sets the e object.
	 * 
	 * @param eobject
	 *            the new e object
	 */
	public void setEObject(EObject eobject) {
		this.eobject = eobject;

		refresh();
	}

	/**
	 * Gets the edits the domain.
	 * 
	 * @return the edits the domain
	 */
	public EditingDomain getEditDomain() {
		return EMFEditDomain;
	}

	/**
	 * Sets the edits the domain.
	 * 
	 * @param editDomain
	 *            the new edits the domain
	 */
	public void setEditDomain(EditingDomain editDomain) {
		this.EMFEditDomain = editDomain;
	}

	/**
	 * Sets the label provider.
	 * 
	 * @param lp
	 *            the new label provider
	 */
	public void setLabelProvider(IBaseLabelProvider lp) {
		EObjectsTableViewer.setLabelProvider(lp);
	}

	/**
	 * Gets the adds the button selection adapter.
	 * 
	 * @return the adds the button selection adapter
	 */
	public SelectionAdapter getAddButtonSelectionAdapter() {
		return addButtonSelectionAdapter;
	}

	/**
	 * Gets the removes the button selection adapter.
	 * 
	 * @return the removes the button selection adapter
	 */
	public SelectionAdapter getRemoveButtonSelectionAdapter() {
		return removeButtonSelectionAdapter;
	}

	/**
	 * Gets the first selected.
	 * 
	 * @return the first selected
	 */
	public TableItem getFirstSelected() {
		TableItem firstItem = null;

		if (EObjectsTable.getSelection().length > 0) {
			firstItem = EObjectsTable.getSelection()[0];
		}

		return firstItem;

	}

	/**
	 * Refresh selected e object.
	 * 
	 * @param itemSelected
	 *            the item selected
	 */
	public void refreshSelectedEObject(TableItem itemSelected) {
		if (itemSelected != null && !itemSelected.isDisposed()) {
			updateSelectedObject((EObject) itemSelected.getData());
		} else {
			updateSelectedObject(null);
		}
	}

	/**
	 * Create the Composite composed of a Table and two Buttons.
	 * 
	 * @param parent
	 *            the parent Composite
	 */
	protected void createContents(Composite parent) {
		GridData data = new GridData();

		if (canFilter()) {
			parent.setLayout(new GridLayout(3, false));

			filterLabel = widgetFactory.createLabel(parent, Messages
					.getString("EObjectsTableComposite.FilterLabel") + ':'); //$NON-NLS-1$
			data.widthHint = getStandardLabelWidth(parent,
					new String[] { "Filter:" }); //$NON-NLS-1$
			data.horizontalSpan = 1;
			filterLabel.setLayoutData(data);

			filterText = widgetFactory.createText(parent, "", SWT.BORDER); //$NON-NLS-1$
			data = new GridData(GridData.FILL_HORIZONTAL);
			data.horizontalSpan = 2;
			filterText.setLayoutData(data);
			filterText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			filterText.addListener(SWT.Modify, new Listener() {

				public void handleEvent(Event e) {
					updateFilters();
				}
			});
		}

		EObjectsTable = widgetFactory.createTable(parent, SWT.BORDER
				| SWT.FULL_SELECTION);
		data = new GridData(GridData.FILL_BOTH);
		data.verticalSpan = 2;
		if (canFilter())
			data.horizontalSpan = 2;
		EObjectsTable.setLayoutData(data);

		data = new GridData(GridData.FILL_HORIZONTAL);
		addButton = widgetFactory.createButton(parent, Messages
				.getString("AddLabel"), SWT.NONE);
		addButton.setLayoutData(data);

		removeButton = widgetFactory.createButton(parent, Messages
				.getString("RemoveLabel"), SWT.NONE);
		removeButton.setLayoutData(data);

		TableColumn directionColumn = new TableColumn(EObjectsTable, SWT.LEFT);
		directionColumn.setText(columnNames[0]);
		directionColumn.setWidth(getColumnWidth());

		EObjectsTable.setHeaderVisible(true);
		EObjectsTable.setLinesVisible(true);

		EObjectsTableViewer = new TableViewer(EObjectsTable);
		EObjectsTableViewer.setContentProvider(new EObjectContentProvider());
		EObjectsTableViewer.setLabelProvider(getLabelProvider());

		EObjectsTableViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {
					/**
					 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
					 */
					public void selectionChanged(SelectionChangedEvent event) {
						if (!isRefreshing) {
							refreshSelectedEObject(getFirstSelected());
							refresh();
							section.refresh();
						}

					}
				});

		addButton.addSelectionListener(addButtonSelectionAdapter);

		removeButton.addSelectionListener(removeButtonSelectionAdapter);
	}

	protected ILabelProvider getLabelProvider() {
		return MDTUtil.getLabelProvider();
	}

	protected int getColumnWidth() {
		return 600;
	}

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

	protected void updateFilters() {
		List<ViewerFilter> filters = new ArrayList<ViewerFilter>();
		ViewerFilter filter = this.getSearchFilter();
		if (this.canFilter() && filter != null) {
			filters.add(filter);
		}

		ViewerFilter[] filtersArray = new ViewerFilter[filters.size()];
		for (int i = 0; i < filters.size(); i++) {
			filtersArray[i] = filters.get(i);
		}

		this.getEObjectsTableViewer().setFilters(filtersArray);
	}

	protected ViewerFilter getSearchFilter() {
		return null;
	}

	public Text getFilterText() {
		return filterText;
	}

	protected boolean canFilter() {
		return false;
	}

	/**
	 * Refresh the Table contents.
	 */
	protected void refresh() {
		if (!EObjectsTableViewer.getTable().isDisposed()) {
			isRefreshing = true;

			EList<EObject> tableElements = new BasicEList<EObject>();

			Object value = getValue(eobject, feature);
			if (value instanceof EList) {
				EList<EObject> list = (EList<EObject>) value;

				for (EObject eo : list) {
					if (passFilter(featureClass, eo)) {
						tableElements.add(eo);
					}
				}
			}

			EObjectsTableViewer.setInput(tableElements);
			isRefreshing = false;
		}
	}

	/**
	 * Get the feature for the table field for the section.
	 * 
	 * @return the feature for the table.
	 */
	protected EStructuralFeature getFeature() {
		return feature;
	}

	/**
	 * Get a new child instance for the result of clicking the add button.
	 * 
	 * @return a new child instance.
	 */
	protected Object getNewChild() {

		EObject newChild = null;
		EClass eclass = getFeatureClass();
		EObject container = eclass.eContainer();
		if (container instanceof EPackage) {
			EPackage epackage = (EPackage) container;
			EFactory factory = epackage.getEFactoryInstance();
			if (factory != null) {
				if (!eclass.isAbstract()) {
					newChild = factory.create(eclass);
				}
			}
		}

		return newChild;
	}

	/**
	 * This method is called each time a new Parameter is selected in the Table.
	 * 
	 * @param newEObject
	 *            the new e object
	 */
	public void updateSelectedObject(EObject newEObject) {
		// By default, do nothing
	}

	/**
	 * Internal class to handle modification.
	 * 
	 * @author jlescot
	 */
	class EObjectContentProvider implements IStructuredContentProvider {

		/**
		 * Gets the elements.
		 * 
		 * @param inputElement
		 *            the input element
		 * 
		 * @return the elements
		 * 
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
		 */
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof EList) {
				return ((EList<?>) inputElement).toArray();
			}

			return new Object[0];
		}

		/**
		 * Dispose.
		 * 
		 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
		 */
		public void dispose() {
			// nothing to do
		}

		/**
		 * Input changed.
		 * 
		 * @param viewer
		 *            the viewer
		 * @param oldInput
		 *            the old input
		 * @param newInput
		 *            the new input
		 * 
		 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
		 *      java.lang.Object, java.lang.Object)
		 */
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// nothing to do
		}
	}

	/**
	 * Pass filter.
	 * 
	 * @param featureClass
	 *            the feature class
	 * @param eo
	 *            the eo
	 * 
	 * @return true, if successful
	 */
	protected boolean passFilter(EClass featureClass, EObject eo) {
		return true;
	}

	/**
	 * Gets the value.
	 * 
	 * @param object
	 *            the object
	 * @param feature
	 *            the feature
	 * 
	 * @return the value
	 */
	private Object getValue(EObject object, EStructuralFeature feature) {

		Object result = null;
		EObject container = object;

		do {
			result = container.eGet(feature);
			container = container.eContainer();
		} while (result == null && container != null);

		return result;
	}

}
