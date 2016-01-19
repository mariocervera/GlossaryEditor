/***********************************************************************
 * Copyright (c) 2007 Anyware Technologies
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Anyware Technologies - initial API and implementation
 *    Francisco Javier Cano Muñoz (Prodevelop) - improved initial selection
 **********************************************************************/

package es.cv.gvcase.emf.ui.common.dialogs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.SelectionDialog;

import es.cv.gvcase.emf.common.util.EMFUtil;
import es.cv.gvcase.emf.ui.common.composites.EObjectChooserComposite;
import es.cv.gvcase.emf.ui.common.composites.SearchableTreeComposite;
import es.cv.gvcase.emf.ui.common.providers.TreeArrayContentProvider;
import es.cv.gvcase.emf.ui.common.utils.DataPath;

/**
 * The dialog used to choose between the different objects
 * 
 * @author <a href="david.sciamma@anyware-tech.com">David Sciamma</a>
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 */
public class ChooseDialog extends SelectionDialog {

	/**
	 * The default width of a dialog
	 */
	private int DEFAULT_DIALOG_WIDTH = 400;

	/**
	 * The default height of a dialog
	 */
	private int DEFAULT_DIALOG_HEIGHT = 300;

	/**
	 * The minimum width of a dialog
	 */
	private int MIN_DIALOG_WIDTH = 300;

	/**
	 * The minimum height of a dialog
	 */
	private int MIN_DIALOG_HEIGHT = 300;

	private SearchableTreeComposite tree;

	private ILabelProvider labelProvider;

	private IStructuredContentProvider contentProvider;

	private ITreeContentProvider groupedTreeContentProvider;

	private ITreeContentProvider resourceTreeContentProvider;

	private Object[] objects;

	private List<DataPath> paths;

	/**
	 * Flag to set the selection tree as single or multi selection.
	 */
	private boolean singleSelection = true;

	/**
	 * Indicates whether the selection tree must be single selection.
	 * 
	 * @return
	 */
	protected boolean isSingleSelection() {
		return singleSelection;
	}

	/**
	 * Constructor
	 * 
	 * @param parentShell
	 *            the paren shell
	 * @param objects
	 *            The available objects
	 */
	public ChooseDialog(Shell parentShell, Object[] objects) {
		this(parentShell, objects, true);
	}

	/**
	 * Constructor
	 * 
	 * @param parentShell
	 *            the paren shell
	 * @param objects
	 *            The available objects
	 * @param singleSelection
	 *            Flag to set the selection tree as single or multi selection
	 */
	public ChooseDialog(Shell parentShell, Object[] objects,
			boolean singleSelection) {
		this(parentShell, objects, singleSelection, null);
	}

	public ChooseDialog(Shell parentShell, Object[] objects,
			boolean singleSelection, ITreeContentProvider groupedContentProvider) {
		this(parentShell, objects, singleSelection, null,
				groupedContentProvider);
	}

	public ChooseDialog(Shell parentShell, Object[] objects,
			boolean singleSelection,
			ITreeContentProvider groupedContentProvider,
			ITreeContentProvider resourceContentProvider) {
		super(parentShell);

		this.objects = replaceNulls(objects);
		this.singleSelection = singleSelection;
		this.groupedTreeContentProvider = groupedContentProvider;
		this.resourceTreeContentProvider = resourceContentProvider;
		this.paths = new ArrayList<DataPath>();

		setTitle("Object selection");
		setMessage("Choose an object in the list");
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	private Object[] replaceNulls(Object[] objects) {
		List<Object> l = new ArrayList<Object>();

		if (objects == null)
			return l.toArray();

		for (Object o : objects) {
			if (o == null) {
				l.add(new EObjectChooserComposite.NullObject());
			} else {
				l.add(o);
			}
		}
		return l.toArray();
	}

	/**
	 * @see org.eclipse.ui.dialogs.SelectionDialog#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	protected void configureShell(Shell shell) {
		shell.setMinimumSize(MIN_DIALOG_WIDTH, MIN_DIALOG_HEIGHT);

		super.configureShell(shell);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Control createContents(Composite parent) {
		Control c = super.createContents(parent);

		// by default, ok button is not enabled. Only will be enabled if has
		// elements in the selection or when select an element
		getOkButton().setEnabled(false);

		// Doesn't expand all the tree looking for the element to be selected,
		// due to it's heavy for huge models
		// tree.getTreeViewer().expandAll();
		// for (Object o : selection) {
		// tree.getTreeViewer().expandToLevel(o, 1);
		// }

		// if there are no elements in the selection, nothing to do. In other
		// case, if selection has a null, change it for a NullObject element. In
		// other case, return the selection
		List selection = getInitialElementSelections();

		if (selection.isEmpty()) {
			return c;
		} else {
			List list = null;
			if (selection.size() == 1 && selection.get(0) == null) {
				list = new ArrayList();
				list.add(new EObjectChooserComposite.NullObject());
			} else {
				list = selection;
			}
			tree.setInitialSelection(new StructuredSelection(list));
		}

		return c;
	}

	/**
	 * Create the Dialog area
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent) {
		// Dialog
		Composite dialogComposite = (Composite) super.createDialogArea(parent);

		GridLayout dialogLayout = new GridLayout();
		dialogLayout.marginWidth = 10;
		dialogLayout.marginHeight = 10;
		GridData dialogLayoutData = new GridData(GridData.FILL_BOTH);
		dialogLayoutData.widthHint = DEFAULT_DIALOG_WIDTH;
		dialogLayoutData.heightHint = DEFAULT_DIALOG_HEIGHT;
		dialogComposite.setLayout(dialogLayout);
		dialogComposite.setLayoutData(dialogLayoutData);

		tree = createTree(dialogComposite);
		tree.setLayoutData(new GridData(GridData.FILL_BOTH));
		if (this.contentProvider != null) {
			tree.setContentProvider(this.contentProvider);
		} else {
			tree.setContentProvider(getTreeContentProvider());
		}
		if (groupedTreeContentProvider != null) {
			tree.setGroupedContentProvider(groupedTreeContentProvider);
		}
		if (resourceTreeContentProvider != null) {
			tree.setResourceContentProvider(resourceTreeContentProvider);
		}
		if (this.labelProvider != null) {
			tree.setLabelProvider(this.labelProvider);
		} else {
			tree.setLabelProvider(EMFUtil.getAdapterFactoryLabelProvider());
		}

		tree.setInput(this.objects);

		hookListeners();

		return dialogComposite;
	}

	@Override
	protected Button createButton(Composite parent, int id, String label,
			boolean defaultButton) {
		if (id == IDialogConstants.OK_ID) {
			return super.createButton(parent, id, label, false);
		}
		return super.createButton(parent, id, label, defaultButton);
	}

	protected ITreeContentProvider getTreeContentProvider() {
		return new TreeArrayContentProvider();
	}

	protected SearchableTreeComposite createTree(Composite parent) {
		SearchableTreeComposite tree = new SearchableTreeComposite(parent,
				isSingleSelection() ? SWT.SINGLE : SWT.MULTI,
				selectAfterRefreshTree());
		return tree;
	}

	protected boolean selectAfterRefreshTree() {
		return true;
	}

	protected void changeItemHandle(SelectionChangedEvent event) {
		if (event.getSelection() instanceof IStructuredSelection) {
			IStructuredSelection selection = (IStructuredSelection) event
					.getSelection();
			if (selection.isEmpty()) {
				getOkButton().setEnabled(false);
				return;
			}
			for (Object o : selection.toList()) {
				if (!((checkSelectionValid(o)) || (o instanceof EObjectChooserComposite.NullObject))) {
					getOkButton().setEnabled(false);
					return;
				}
			}
			getOkButton().setEnabled(true);
			if (selection instanceof TreeSelection) {
				TreeSelection ts = (TreeSelection) selection;
				modifySelectionPath(ts);
			}
			return;
		}
		getOkButton().setEnabled(false);
		return;
	}

	protected boolean checkSelectionValid(Object o) {
		return true;
	}

	/**
	 * This method had the UI listeners on the SWT widgets
	 */
	private void hookListeners() {
		tree.getTreeViewer().addSelectionChangedListener(
				new ISelectionChangedListener() {

					public void selectionChanged(SelectionChangedEvent event) {
						changeItemHandle(event);
					}

				});
		tree.getTreeViewer().addDoubleClickListener(new IDoubleClickListener() {

			public void doubleClick(DoubleClickEvent event) {
				if (getOkButton().isEnabled()) {
					okPressed();
				}
			}

		});
	}

	/**
	 * Gets the initial selected elements. Before returning the selected
	 * elements, all nested Arrays and Collections are de-nested.
	 */
	@Override
	protected List<?> getInitialElementSelections() {
		List initialElementsSelection = new ArrayList();
		// get the normal initial selection
		List superInitialElementsSelection = super
				.getInitialElementSelections();
		// find any nested List in the initial selection and de-nest them
		if (superInitialElementsSelection != null) {
			for (Object object : superInitialElementsSelection) {
				// an array can contain more objects
				if (object instanceof Object[]) {
					Object[] objects = (Object[]) object;
					for (Object innerObject : objects) {
						initialElementsSelection.add(innerObject);
					}
				}
				// a Collection can contain more objects
				else if (object instanceof Collection) {
					Collection objects = (Collection) object;
					for (Object innerObject : objects) {
						initialElementsSelection.add(innerObject);
					}
				}
				// a regular object that is neither an Array or a Collection is
				// added to the selection as-is
				else {
					initialElementsSelection.add(object);
				}
			}
		}
		return initialElementsSelection;
	}

	/**
	 * Set the provider that displays the objects
	 * 
	 * @param provider
	 *            the LabelProvider
	 */
	public void setLabelProvider(ILabelProvider provider) {
		this.labelProvider = provider;
	}

	public void setContentProvider(IStructuredContentProvider provider) {
		this.contentProvider = provider;
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	protected void okPressed() {
		IStructuredSelection selection = (IStructuredSelection) tree
				.getTreeViewer().getSelection();
		List<Object> filteredSelection = new ArrayList<Object>();
		for (Object o : selection.toList()) {
			filteredSelection.add(o);
		}
		setResult(filteredSelection);
		super.okPressed();
	}

	protected void modifySelectionPath(TreeSelection ts) {
		paths.clear();

		for (Object o : ts.toList()) {
			DataPath dp = new DataPath(o, ts.getPathsFor(o)[0],
					getAllowedTypesInThePath());
			paths.add(dp);
		}
	}

	protected List<?> getAllowedTypesInThePath() {
		return Collections.singletonList(Object.class);
	}

	public List<DataPath> getPaths() {
		return paths;
	}

	public DataPath getPathForElement(Object obj) {
		for (DataPath dp : paths) {
			if (dp.getElement() == obj) {
				return dp;
			}
		}

		return null;
	}

	protected Object[] getElements() {
		return objects;
	}

	public SearchableTreeComposite getTreeComposite() {
		return tree;
	}

}