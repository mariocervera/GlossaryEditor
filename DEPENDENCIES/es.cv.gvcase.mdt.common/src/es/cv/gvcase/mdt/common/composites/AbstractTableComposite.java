/*******************************************************************************
 * Copyright (c) 2012 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana. All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Marc Gil Sendra (Integranova) - initial API implementation
 ******************************************************************************/
package es.cv.gvcase.mdt.common.composites;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.DeleteCommand;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.views.properties.tabbed.ISection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

import es.cv.gvcase.mdt.common.util.MDTUtil;

/**
 * An specialization of the EObjectsEditableTableComposite in order to make easy
 * its use.
 * 
 * @author mgil
 */
public abstract class AbstractTableComposite extends
		EObjectsEditableTableComposite {

	private List<TableColumnInfo> columns;

	public AbstractTableComposite(Composite parent, int style,
			TabbedPropertySheetWidgetFactory widgetFactory, ISection section,
			EStructuralFeature feature, EClass tableEClass) {
		super(parent, style, widgetFactory, section, feature, tableEClass);
	}

	@Override
	protected void createContents(Composite parent) {
		initializeTableColumnInfo();
		super.createContents(parent);
	}

	protected void initializeTableColumnInfo() {
		columns = new ArrayList<TableColumnInfo>();
		fillTableColumnInfo(columns);
	}

	protected List<TableColumnInfo> getTableColumnInfo() {
		return columns;
	}

	protected TableColumnInfo getTableColumnInfo(String property) {
		for (int i = 0; i < columns.size(); i++) {
			TableColumnInfo tci = columns.get(i);
			if (tci.text.equals(property)) {
				return tci;
			}
		}

		return null;
	}

	protected abstract void fillTableColumnInfo(List<TableColumnInfo> columns);

	@Override
	protected void hookListeners() {
		super.hookListeners();

		getAddButton().removeSelectionListener(getAddButtonListener());
		getAddButton().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if (getTableEObject() == null) {
					return;
				}

				EObject newChild = createNewChild();
				configureNewChild(newChild);

				Command command = new AddCommand(getEditingDomain(),
						getTableEObject(), getTableFeature(), newChild) {
					@Override
					public void doUndo() {
						super.doUndo();
						if (getTableViewer().getContentProvider() != null)
							getSection().refresh();
					}

					@Override
					public void doRedo() {
						super.doRedo();
						if (getTableViewer().getContentProvider() != null)
							getSection().refresh();
					}
				};

				getEditingDomain().getCommandStack().execute(command);

				refresh();
				getTableViewer()
						.setSelection(new StructuredSelection(newChild));
				getSection().refresh();
			}
		});

		getRemoveButton().removeSelectionListener(getRemoveButtonListener());
		getRemoveButton().addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				CompoundCommand cc = new CompoundCommand() {
					@Override
					public void undo() {
						super.undo();
						if (getTableViewer().getContentProvider() != null)
							getSection().refresh();
					}

					@Override
					public void redo() {
						super.redo();
						if (getTableViewer().getContentProvider() != null)
							getSection().refresh();
					}
				};

				for (TableItem ti : getTable().getSelection()) {
					Object object = ti.getData();

					cc.appendIfCanExecute(MDTUtil.removeEObjectReferences(
							getEditingDomain(), (EObject) object));

					cc.appendIfCanExecute(DeleteCommand.create(
							getEditingDomain(), object));
				}

				if (cc.canExecute()) {
					getEditingDomain().getCommandStack().execute(cc);
				}

				getTableViewer().setSelection(null);
				refresh();
				if (getTable().getItems().length > 0) {
					getTableViewer().setSelection(
							new StructuredSelection(getTable().getItems()[0]
									.getData()));
				}

				getSection().refresh();
			}
		});
	}

	protected void configureNewChild(EObject child) {
		// do nothing by default
	}

	@Override
	protected CellEditor[] getCellEditors(Table table) {
		CellEditor[] editors = new CellEditor[columns.size()];

		for (int i = 0; i < columns.size(); i++) {
			editors[i] = columns.get(i).cellEditor;
			editors[i].create(table);
		}

		return editors;
	}

	@Override
	protected boolean enableOrdering() {
		return false;
	}

	@Override
	protected String[] getColumnNames() {
		String[] names = new String[columns.size()];

		for (int i = 0; i < columns.size(); i++) {
			names[i] = columns.get(i).text;
		}

		return names;
	}

	@Override
	protected int[] getColumnSizes() {
		int[] sizes = new int[columns.size()];

		for (int i = 0; i < columns.size(); i++) {
			sizes[i] = columns.get(i).size;
		}

		return sizes;
	}

	@Override
	protected List<EStructuralFeature> getListOfFeatures() {
		List<EStructuralFeature> list = new ArrayList<EStructuralFeature>();

		for (TableColumnInfo tci : columns) {
			list.add(tci.feature);
		}

		return list;
	}

}
