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

package es.cv.gvcase.mdt.common.sections.composites;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.SelectionDialog;

import es.cv.gvcase.mdt.common.internal.Messages;
import es.cv.gvcase.mdt.common.util.MDTUtil;

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

	private SearchableTree tree;

	private ILabelProvider labelProvider;

	private ITreeContentProvider groupedTreeContentProvider;

	private Object[] objects;

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
		super(parentShell);

		this.objects = replaceNulls(objects);
		this.singleSelection = singleSelection;
		this.groupedTreeContentProvider = groupedContentProvider;

		setTitle(Messages.getString("ChooseDialog.ObjectSelection")); //$NON-NLS-1$
		setMessage(Messages.getString("ChooseDialog.ChooseObject")); //$NON-NLS-1$
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	private Object[] replaceNulls(Object[] objects) {
		List<Object> l = new ArrayList<Object>();

		if (objects == null)
			return l.toArray();

		for (Object o : objects) {
			if (o == null) {
				l.add(new CSingleObjectChooser.NullObject());
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
		tree.setContentProvider(getTreeContentProvider());
		if (groupedTreeContentProvider != null) {
			tree.setGroupedContentProvider(groupedTreeContentProvider);
		}
		if (this.labelProvider != null) {
			tree.setLabelProvider(this.labelProvider);
		} else {
			tree.setLabelProvider(MDTUtil.getLabelProvider());
		}

		tree.setInput(this.objects);
		tree.setInitialSelection(new StructuredSelection(
				getInitialElementSelections()));

		hookListeners();

		return dialogComposite;
	}

	protected ITreeContentProvider getTreeContentProvider() {
		return new TreeArrayContentProvider();
	}

	protected SearchableTree createTree(Composite parent) {
		return new SearchableTree(parent, isSingleSelection() ? SWT.SINGLE
				: SWT.MULTI);
	}

	protected void changeItemHandle(SelectionChangedEvent event) {
		if (event.getSelection() instanceof IStructuredSelection) {
			IStructuredSelection selection = (IStructuredSelection) event
					.getSelection();
			for (Object o : selection.toList()) {
				if (!((o instanceof EObject) || (o instanceof CSingleObjectChooser.NullObject))) {
					getOkButton().setEnabled(false);
					return;
				}
			}
			getOkButton().setEnabled(true);
			return;
		}
		getOkButton().setEnabled(false);
		return;
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
				okPressed();

			}

		});
	}

	/**
	 * Gets the initial selected elements. Before returning the selected
	 * elements, all nested Arrays and Collections are de-nested.
	 */
	@Override
	protected List getInitialElementSelections() {
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

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	protected void okPressed() {
		IStructuredSelection selection = (IStructuredSelection) tree
				.getTreeViewer().getSelection();
		List<Object> filteredSelection = new ArrayList<Object>();
		for (Object o : selection.toList()) {
			if ((o instanceof EObject)
					|| (o instanceof CSingleObjectChooser.NullObject)) {
				filteredSelection.add(o);
			}
		}
		setResult(filteredSelection);
		super.okPressed();
	}

	protected Object[] getElements() {
		return objects;
	}

}