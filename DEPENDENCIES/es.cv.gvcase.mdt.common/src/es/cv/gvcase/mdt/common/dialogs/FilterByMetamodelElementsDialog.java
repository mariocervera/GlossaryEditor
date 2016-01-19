/*******************************************************************************
 * Copyright (c) 2011 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana. All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Miguel Llacer San Fernando (Prodevelop) - initial API implementation
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.dialogs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import es.cv.gvcase.emf.common.util.EMFUtil;

/**
 * The Class FilterByMetamodelElementsDialog.
 */
public class FilterByMetamodelElementsDialog extends Dialog {

	private List<EClassifier> selectableEClassifiers;
	private List<EClassifier> selectedEClassifiers;

	private TreeViewer treeContents;

	private int shellMinHeigh = 400;
	private int shellMinWidth = 500;

	/** The label provider. */
	private IBaseLabelProvider labelProvider;

	/** The content provider. */
	private IContentProvider contentProvider;

	public FilterByMetamodelElementsDialog(Shell shell,
			List<EClassifier> selectableEClassifiers,
			List<EClassifier> selectedEClassifiers) {
		super(shell);

		this.selectableEClassifiers = selectableEClassifiers;
		this.selectedEClassifiers = selectedEClassifiers;

	}

	/**
	 * Sets a minimum size to the Shell
	 */
	@Override
	public void create() {
		super.create();
		getShell().setMinimumSize(getShell().getSize());
	}

	/**
	 * Make the Shell resizable
	 */
	@Override
	protected int getShellStyle() {
		return super.getShellStyle() | SWT.RESIZE;
	}

	/**
	 * The main creation control. Creates the content of the dialog
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		getShell().setText("Meta model elements");

		// External Composite
		Composite composite = (Composite) super.createDialogArea(parent);

		createContentsSection(composite);

		fillTreeContents();
		checkItems(treeContents.getTree(), selectedEClassifiers);

		return composite;
	}

	/**
	 * Creates the contents section for the dialog.
	 * 
	 * @param parent
	 *            the parent
	 */
	private void createContentsSection(Composite parent) {
		treeContents = new TreeViewer(parent, SWT.BORDER | SWT.CHECK);
		treeContents.setLabelProvider(getLabelProvider());
		treeContents.setContentProvider(getContentProvider());
		treeContents.setFilters(getFilters());

		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = shellMinHeigh;
		gd.widthHint = shellMinWidth;
		treeContents.getTree().setLayoutData(gd);
		treeContents.getTree().addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				treeItemSelected(e);
			}
		});

	}

	/**
	 * Fill tree with eClassifiers.
	 */
	private void fillTreeContents() {
		if (treeContents == null) {
			return;
		}
		treeContents.getTree().removeAll();

		List<EPackage> list = new ArrayList<EPackage>();

		for (EClassifier eClassifier : selectableEClassifiers) {
			if (!list.contains(eClassifier.getEPackage())) {
				list.add(eClassifier.getEPackage());
			}
		}

		treeContents.setInput(list);
		treeContents.expandAll();
	}

	public List<EClassifier> getSelectedEClassifiers() {
		return this.selectedEClassifiers;
	}

	private boolean isChecking = false;

	private void treeItemSelected(SelectionEvent e) {
		if (e.detail == SWT.CHECK && isChecking == false) {
			isChecking = true;
			try {
				TreeItem selectedItem = (TreeItem) Platform.getAdapterManager()
						.getAdapter(e.item, TreeItem.class);
				TreeItem parentItem = selectedItem.getParentItem() != null ? selectedItem
						.getParentItem()
						: selectedItem;
				EObject selectedElement = (EObject) selectedItem.getData();
				EObject parentElement = (EObject) parentItem.getData();
				if (selectedElement == null || parentElement == null) {
					return;
				}

				if (selectedItem.getChecked()) {
					// is checked
					if (selectedElement.equals(parentElement)) {
						// the element is an EPackage
						// select the children and store into e-classifiers list
						TreeItem[] children = selectedItem.getItems();
						for (int i = 0; i < children.length; i++) {
							children[i].setChecked(true);
							if (children[i].getData() instanceof EClassifier
									&& !selectedEClassifiers
											.contains(children[i].getData())) {
								selectedEClassifiers
										.add((EClassifier) children[i]
												.getData());
							}
						}

					} else {
						// the element is an EClassifier
						// add it to the e-classifiers list
						selectedEClassifiers.add((EClassifier) selectedElement);

					}
				} else {
					// isn't checked
					if (selectedElement.equals(parentElement)) {
						// the element is an EPackage
						// deselect children and store into e-classifiers list
						TreeItem[] children = selectedItem.getItems();
						for (int i = 0; i < children.length; i++) {
							children[i].setChecked(false);
							if (children[i].getData() instanceof EClassifier
									&& selectedEClassifiers
											.contains(children[i].getData())) {
								selectedEClassifiers
										.remove((EClassifier) children[i]
												.getData());
							}
						}

					} else {
						// the element is an EClassifier
						// remove it from e-classifiers list
						selectedEClassifiers.remove(selectedElement);

					}
				}
			} finally {
				isChecking = false;
			}
		}
	}

	private void checkItems(Tree rootItem, Collection<EClassifier> list) {
		for (TreeItem treeItem : rootItem.getItems()) {
			// treeItem is an EPackage
			boolean checkEPackage = true;

			for (TreeItem subItem : treeItem.getItems()) {
				// subItem is an EClassifier
				if (subItem.getData() != null
						&& list.contains(subItem.getData())) {
					subItem.setChecked(true);
				} else {
					checkEPackage = false;
				}
			}
			treeItem.setChecked(checkEPackage);
		}
	}

	protected IBaseLabelProvider getLabelProvider() {
		if (labelProvider == null) {
			labelProvider = new LabelProvider() {
				AdapterFactoryLabelProvider adapter = EMFUtil
						.getAdapterFactoryLabelProvider();

				@Override
				public String getText(Object element) {
					if (element instanceof ENamedElement) {
						return ((ENamedElement) element).getName();
					}
					return super.getText(element);
				}

				@Override
				public Image getImage(Object element) {
					if (element == null)
						return null;
					return adapter.getImage(element);
				}

				@Override
				public void dispose() {
					adapter.dispose();
				}
			};
		}
		return labelProvider;
	}

	protected IContentProvider getContentProvider() {
		if (contentProvider == null) {
			contentProvider = new ITreeContentProvider() {

				public Object[] getChildren(Object parentElement) {
					if (!(parentElement instanceof EObject)) {
						return new Object[0];
					}

					List<Object> list = new ArrayList<Object>();

					for (EObject eObj : ((EObject) parentElement).eContents()) {
						if (!(eObj instanceof EClassifier)) {
							continue;
						}
						EClassifier classifier = (EClassifier) eObj;

						if (!(classifier instanceof EClass)) {
							continue;
						}
						if (list.contains(classifier)) {
							continue;
						}
						for (EClassifier obj : selectableEClassifiers) {
							if (obj.equals(classifier)) {
								list.add(classifier);
							}
						}
					}
					return list.toArray();
				}

				public Object getParent(Object element) {
					return null;
				}

				public boolean hasChildren(Object element) {
					if (element instanceof EPackage
							&& !((EPackage) element).getEClassifiers()
									.isEmpty()) {
						return true;
					}
					return false;
				}

				public void dispose() {
				}

				public void inputChanged(Viewer viewer, Object oldInput,
						Object newInput) {
				}

				public Object[] getElements(Object inputElement) {
					if (inputElement instanceof List<?>) {
						return ((List<?>) inputElement).toArray();
					} else {
						return new Object[] { inputElement };
					}
				}
			};
		}
		return contentProvider;
	}

	protected ViewerFilter[] getFilters() {
		ViewerFilter[] filters = new ViewerFilter[1];
		filters[0] = new TreeViewerFilter();
		return filters;
	}

	class TreeViewerFilter extends ViewerFilter {
		@Override
		public boolean select(Viewer viewer, Object parentElement,
				Object element) {
			return true;
		}
	}

}
