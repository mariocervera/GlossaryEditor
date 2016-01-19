/***************************************************************************
 * Copyright (c) 2011 Conselleria de Infraestructuras y Transporte,
 * Generalitat de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Miguel Llacer San Fernanado (Prodevelop) – initial API and implementation
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.part;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import es.cv.gvcase.mdt.common.dialogs.FilterByMetamodelElementsDialog;

// TODO: Auto-generated Javadoc
/**
 * The Class ViewerPopUpMenu.
 */
public class ViewerPopUpMenu {

	private EObject root;

	private List<EClassifier> selectableEClassifiers;

	/** The selected e classifiers. */
	private List<EClassifier> selectedEClassifiers;

	/** The viewer. */
	private StructuredViewer viewer;

	/** The add meta model elements filter. */
	private boolean addMetaModelElementsFilter;

	/** The menu. */
	private Menu menu;

	/** The name sub item. */
	private MenuItem nameSubItem;

	/** The type sub item. */
	private MenuItem typeSubItem;

	/** The element type selection listener. */
	private SelectionAdapter elementTypeSelectionListener;

	/** The filters selection listener. */
	private SelectionAdapter filtersSelectionListener;

	/** The element name selection listener. */
	private SelectionAdapter elementNameSelectionListener;

	/** The expand selection listener. */
	private SelectionAdapter expandSelectionListener;

	/** The collapse selection listener. */
	private SelectionAdapter collapseSelectionListener;

	/**
	 * Instantiates a new viewer pop up menu.
	 * 
	 * @param viewer
	 *            the viewer
	 * @param addMetaModelElementsFilter
	 *            the add meta model elements filter
	 */
	public ViewerPopUpMenu(StructuredViewer viewer, EObject root,
			boolean addMetaModelElementsFilter) {
		this.root = root;
		this.viewer = viewer;
		this.addMetaModelElementsFilter = addMetaModelElementsFilter;

		createMenu();
	}

	public Viewer getViewer() {
		return this.viewer;
	}

	/**
	 * Creates the menu.
	 */
	protected void createMenu() {
		menu = new Menu(viewer.getControl().getShell(), SWT.POP_UP);
		Menu subMenu = new Menu(viewer.getControl().getShell(), SWT.DROP_DOWN);

		if (addMetaModelElementsFilter) {
			selectableEClassifiers = createListEClassifiers();
			selectedEClassifiers = createListEClassifiers();

			MenuItem filterItem = new MenuItem(menu, SWT.PUSH);
			filterItem.setText("Filter by Meta-Model elements");
			filterItem.addSelectionListener(getFiltersSelectionListener());

			new MenuItem(menu, SWT.SEPARATOR);
		}

		MenuItem sorterItem = new MenuItem(menu, SWT.CASCADE);
		sorterItem.setText("Sort by");
		sorterItem.setMenu(subMenu);

		nameSubItem = new MenuItem(subMenu, SWT.CHECK);
		nameSubItem.setText("Element name");
		nameSubItem.addSelectionListener(getElementNameSelectionListener());

		// typeSubItem = new MenuItem(subMenu, SWT.CHECK);
		// typeSubItem.setText("Element type");
		// typeSubItem.addSelectionListener(getElementTypeSelectionListener());

		if (viewer instanceof TreeViewer) {
			new MenuItem(menu, SWT.SEPARATOR);

			MenuItem expandItem = new MenuItem(menu, SWT.PUSH);
			expandItem.setText("Expand All");
			expandItem
					.addSelectionListener(getExpandSelectionListener((TreeViewer) viewer));

			MenuItem collapseItem = new MenuItem(menu, SWT.PUSH);
			collapseItem.setText("Collapse All");
			collapseItem
					.addSelectionListener(getCollapseSelectionListener((TreeViewer) viewer));
		}

		viewer.getControl().setMenu(menu);
	}

	protected List<EClassifier> createListEClassifiers() {
		List<EClassifier> allEClassifiers = new ArrayList<EClassifier>();

		if (root != null) {
			for (TreeIterator<EObject> iter = root.eAllContents(); iter
					.hasNext();) {
				EObject eObject = iter.next();
				if (!allEClassifiers.contains(eObject.eClass())) {
					allEClassifiers.add(eObject.eClass());
				}
			}
		}

		return allEClassifiers;
	}

	/**
	 * Gets the selected e classifiers.
	 * 
	 * @return the selected e classifiers
	 */
	public List<EClassifier> getSelectedEClassifiers() {
		if (selectedEClassifiers == null) {
			selectedEClassifiers = new ArrayList<EClassifier>();
		}
		return selectedEClassifiers;
	}

	/**
	 * Sets the selected e classifiers.
	 * 
	 * @param newSelection
	 *            the new selected e classifiers
	 */
	protected void setSelectedEClassifiers(List<EClassifier> newSelection) {
		selectedEClassifiers = newSelection;
	}

	/**
	 * Gets the filters selection listener.
	 * 
	 * @return the filters selection listener
	 */
	private SelectionAdapter getFiltersSelectionListener() {
		if (filtersSelectionListener == null) {
			filtersSelectionListener = new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					// create dialog to filter tree viewer by meta-model element
					// types
					FilterByMetamodelElementsDialog dialog = new FilterByMetamodelElementsDialog(
							null, selectableEClassifiers,
							getSelectedEClassifiers());

					if (dialog.open() == Dialog.OK) {
						setSelectedEClassifiers(getSelectedEClassifiers());
						// refresh viewer
						viewer.refresh();
					}
				}
			};
		}
		return filtersSelectionListener;
	}

	/**
	 * Gets the element name selection listener.
	 * 
	 * @return the element name selection listener
	 */
	private SelectionAdapter getElementNameSelectionListener() {
		if (elementNameSelectionListener == null) {
			elementNameSelectionListener = new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (!(e.getSource() instanceof MenuItem)) {
						return;
					}
					if (((MenuItem) e.getSource()).getSelection()) {
						// deselect sorter by element name
						// nameSubItem.setSelection(false);
						// apply sorter by element name
						viewer.setSorter(new NameSorter());
					} else {
						// unapply sorter
						viewer.setSorter(null);
					}
					viewer.refresh();
				}
			};
		}
		return elementNameSelectionListener;
	}

	// private SelectionAdapter getElementTypeSelectionListener() {
	// if (elementTypeSelectionListener == null) {
	// elementTypeSelectionListener = new SelectionAdapter() {
	// @Override
	// public void widgetSelected(SelectionEvent e) {
	// if (!(e.getSource() instanceof MenuItem)) {
	// return;
	// }
	// if (((MenuItem) e.getSource()).getSelection()) {
	// // deselect sorter by element type
	// // typeSubItem.setSelection(false);
	// // apply sorter by element type
	// viewer.setSorter(new TypeSorter());
	// } else {
	// // unapply sorter
	// viewer.setSorter(null);
	// }
	// viewer.refresh();
	// }
	// };
	// }
	// return elementTypeSelectionListener;
	// }

	/**
	 * Gets the expand selection listener.
	 * 
	 * @param treeViewer
	 *            the tree viewer
	 * 
	 * @return the expand selection listener
	 */
	private SelectionAdapter getExpandSelectionListener(
			final TreeViewer treeViewer) {
		if (expandSelectionListener == null) {
			expandSelectionListener = new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					treeViewer.expandAll();
				}
			};
		}
		return expandSelectionListener;
	}

	/**
	 * Gets the collapse selection listener.
	 * 
	 * @param treeViewer
	 *            the tree viewer
	 * 
	 * @return the collapse selection listener
	 */
	private SelectionAdapter getCollapseSelectionListener(
			final TreeViewer treeViewer) {
		if (collapseSelectionListener == null) {
			collapseSelectionListener = new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					treeViewer.collapseAll();
				}
			};
		}
		return collapseSelectionListener;
	}

	/**
	 * The Class NameSorter.
	 */
	class NameSorter extends ViewerSorter {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse.jface
		 * .viewers.Viewer, java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			if (e1 instanceof ENamedElement && e2 instanceof ENamedElement) {
				String name1 = ((ENamedElement) e1).getName();
				String name2 = ((ENamedElement) e2).getName();

				if (name1 != null && name2 != null) {
					return name1.compareTo(name2);
				}
			}
			return super.compare(viewer, e1, e2);
		}

	}

	// class TypeSorter extends ViewerSorter {
	//
	// @Override
	// public int compare(Viewer viewer, Object e1, Object e2) {
	// if (e1 instanceof EObject && e2 instanceof EObject) {
	// String name1 = ((EObject) e1).eClass().getName();
	// String name2 = ((EObject) e2).eClass().getName();
	//
	// if (name1 != null && name2 != null) {
	// return name1.compareTo(name2);
	// }
	// }
	// return super.compare(viewer, e1, e2);
	// }
	//
	// }

}
