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
 **********************************************************************/
package es.cv.gvcase.emf.ui.common.composites;

import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TypedListener;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.ide.IDE.SharedImages;

import es.cv.gvcase.emf.ui.common.dialogs.ChooseDialog;
import es.cv.gvcase.emf.ui.common.utils.DataPath;

/**
 * A field widget and a Button that allow you to retrieve an object contained in
 * a list of objects
 * 
 * Creation 6 avr. 2006
 * 
 * @author David Sciamma
 */
public class EObjectChooserComposite extends Composite {

	// //
	// Navigation interface
	// //

	/**
	 * Stub interface to handle the navigation operation of the navigate button.
	 * </br> This EObjectChooserComposite only uses this interface when it is
	 * created with a navigation button.
	 */
	public interface INavigationHandler {
		void handleNavigate();
	}

	// //
	//
	// //

	private Text field;

	private Button chooseBt;

	/**
	 * The button that opens a Diagram where the related element is shown.
	 */
	private Button navigationButton = null;
	/**
	 * Flag indicating that a navigation button is required.
	 */
	private boolean hasNavigationButton = false;
	/**
	 * The navigation handler that handles the operation of navigating.
	 */
	private INavigationHandler navigationHandler = null;

	private Object[] objects;

	private FormToolkit toolkit;

	private ILabelProvider labelProvider;

	private Object selectedObject;

	private IStructuredContentProvider contentProvider;

	private ITreeContentProvider groupedTreeContentProvider;

	private ITreeContentProvider resourceTreeContentProvider;

	private List<DataPath> paths;

	private ChooseDialog dialog;

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
	 * @param parent
	 *            the parent Composite
	 * @param toolkit
	 *            the toolkit necessary to create the widget
	 */
	public EObjectChooserComposite(Composite parent, FormToolkit toolkit) {
		this(parent, toolkit, true);
	}

	/**
	 * Constructor
	 * 
	 * @param parent
	 *            the parent Composite
	 * @param toolkit
	 *            the toolkit necessary to create the widget
	 */
	public EObjectChooserComposite(Composite parent, FormToolkit toolkit,
			boolean singleSelection) {
		this(parent, toolkit, singleSelection, null);
	}

	/**
	 * Constructor
	 * 
	 * @param parent
	 *            the parent Composite
	 * @param toolkit
	 *            the toolkit necessary to create the widget
	 * @param singleSelection
	 *            specify if the Dialog is single selection
	 * @param groupedContentProvider
	 *            specify the Content Provider for the Dialog when it's grouped
	 */
	public EObjectChooserComposite(Composite parent, FormToolkit toolkit,
			boolean singleSelection, ITreeContentProvider groupedContentProvider) {
		this(parent, toolkit, singleSelection, null, groupedContentProvider);
	}

	/**
	 * Constructor
	 * 
	 * @param parent
	 *            the parent Composite
	 * @param toolkit
	 *            the toolkit with which to create the widgets
	 * @param singleSelection
	 *            flag indicating if it is single selection or multiple
	 *            selection
	 * @param flatTreeContentProvider
	 *            flat content provider for the selection dialog
	 * @param groupedContentProvider
	 *            grouped content provider for the selection dialog
	 */
	public EObjectChooserComposite(Composite parent, FormToolkit toolkit,
			boolean singleSelection,
			ITreeContentProvider flatTreeContentProvider,
			ITreeContentProvider groupedContentProvider) {
		this(parent, toolkit, singleSelection, flatTreeContentProvider,
				groupedContentProvider, null);
	}

	/**
	 * 
	 @param parent
	 *            the parent Composite
	 * @param toolkit
	 *            the toolkit with which to create the widgets
	 * @param singleSelection
	 *            flag indicating if it is single selection or multiple
	 *            selection
	 * @param flatTreeContentProvider
	 *            flat content provider for the selection dialog
	 * @param groupedContentProvider
	 *            grouped content provider for the selection dialog
	 * @param resourceContentProvider
	 *            content provider for the resources
	 */
	public EObjectChooserComposite(Composite parent, FormToolkit toolkit,
			boolean singleSelection,
			ITreeContentProvider flatTreeContentProvider,
			ITreeContentProvider groupedContentProvider,
			ITreeContentProvider resourceContentProvider) {
		this(parent, toolkit, singleSelection, false, flatTreeContentProvider,
				groupedContentProvider, resourceContentProvider);
	}

	/**
	 * 
	 * @param parent
	 *            the parent Composite
	 * @param toolkit
	 *            the toolkit with which to create the widgets
	 * @param singleSelection
	 *            flag indicating whether it is single or multiple selection
	 * @param createNavigationButton
	 *            flag indicating if a navigation button is required
	 * @param flatTreeContentProvider
	 *            flat tree content provider
	 * @param groupedContentProvider
	 *            grouped content provider
	 * @param resourceContentProvider
	 *            resource content provider
	 */
	public EObjectChooserComposite(Composite parent, FormToolkit toolkit,
			boolean singleSelection, boolean createNavigationButton,
			ITreeContentProvider flatTreeContentProvider,
			ITreeContentProvider groupedContentProvider,
			ITreeContentProvider resourceContentProvider) {
		super(parent, SWT.NONE);
		this.contentProvider = flatTreeContentProvider;
		this.hasNavigationButton = createNavigationButton;
		this.singleSelection = singleSelection;
		this.groupedTreeContentProvider = groupedContentProvider;
		this.resourceTreeContentProvider = resourceContentProvider;

		this.toolkit = toolkit;
		createContents(this);
		this.toolkit.adapt(this);
		hookListeners();
	}

	/**
	 * Creates the UI. User must call the super method to create the main
	 * widgets (buttons) to this composite.
	 * 
	 * @param parent
	 *            this widget
	 */
	protected void createContents(Composite parent) {
		setLayout(parent);
		/**/
		field = toolkit.createText(parent, "", SWT.FLAT | SWT.BORDER
				| SWT.READ_ONLY);
		field.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		/**/
		chooseBt = toolkit.createButton(parent, "...", SWT.PUSH); //$NON-NLS-1$
		if (hasNavigationButton) {
			// creation of the navigation button if it was requested
			navigationButton = toolkit.createButton(parent, "", SWT.PUSH); //$NON-NLS-1$
			Image navigateButtonImage = PlatformUI.getWorkbench()
					.getSharedImages().getImage(SharedImages.IMG_OPEN_MARKER);
			navigationButton.setImage(navigateButtonImage);
		}
	}

	/**
	 * This method sets a gridlayout to the composite. The number of columns is
	 * determined by the getNumberOfColumns method. The minimum number of
	 * columns is 2.
	 * 
	 * @param parent
	 *            the composite featuring a gridlayout
	 */
	private void setLayout(Composite parent) {
		int numColumns = getNumberOfColumns();
		if (numColumns < 2) {
			numColumns = 2;
		}
		GridLayout layout = new GridLayout(numColumns, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		parent.setLayout(layout);
	}

	/**
	 * Returns the number of columns in this composite. The default object is 2
	 * because the main composite have 2 widgets.
	 * 
	 * Returning a number less than 2 will be ingnored.
	 * 
	 * @return The number of columns to set in this composite. It must be
	 *         greater or equals than 2
	 */
	protected int getNumberOfColumns() {
		if (hasNavigationButton) {
			// if this composite has a navigation button, 3 columns are needed
			return 3;
		}
		return 2;
	}

	/**
	 * Adds the listeners on the choose button. If user overrides this method,
	 * he must call the super method to add the corresponding selection
	 * listener, otherwise disfunctions may occur
	 */
	protected void hookListeners() {
		chooseBt.addSelectionListener(getButtonSelectionListener());
		if (navigationButton != null) {
			// if a navigation button is created, a handler is required
			navigationButton.addSelectionListener(getNavigationListener());
		}
	}

	private SelectionListener selectionListener = null;

	public SelectionListener getButtonSelectionListener() {
		if (selectionListener == null) {
			selectionListener = new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					handleChoose();
				}
			};
		}
		return selectionListener;
	}

	private SelectionListener navigationListener = null;

	public SelectionListener getNavigationListener() {
		if (navigationListener == null) {
			navigationListener = new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					navigateTo();
				}
			};
		}
		return navigationListener;
	}

	protected void navigateTo() {
		if (getNavigationHandler() != null) {
			getNavigationHandler().handleNavigate();
		}
	}

	/**
	 * Set the objects in which the user can choose.
	 * 
	 * @param objs
	 *            the list of objects
	 */
	public void setChoices(Object[] objs) {
		if (objs != null && objs.length > 0) {
			// replace any null entry with a NullObject
			Object[] objects2 = new Object[objs.length];
			for (int cpt = 0; cpt < objs.length; cpt++) {
				if (objs[cpt] == null) {
					objects2[cpt] = new NullObject();
				} else {
					objects2[cpt] = objs[cpt];
				}
			}

			this.objects = objects2;
		}
	}

	/**
	 * Sets the editable state of the text field. The default value is
	 * READ-ONLY. However clients may set this value as true by calling this
	 * method
	 * 
	 * @param isEditable
	 */
	public void setEditable(boolean isEditable) {
		if (field != null) {
			field.setEditable(isEditable);
		}
	}

	/**
	 * Set the provider that displays the objects
	 * 
	 * @param provider
	 *            the LabelProvider
	 */
	public void setLabelProvider(ILabelProvider provider) {
		labelProvider = provider;
	}

	protected ILabelProvider getLabelProvider() {
		return labelProvider;
	}

	protected void fillObjects() {
	}

	protected boolean proceedWithHandleChoose() {
		return true;
	}

	/**
	 * Open the dialog to choose in the searchable list
	 */
	protected void handleChoose() {
		if (!proceedWithHandleChoose()) {
			return;
		}

		fillObjects();

		dialog = createChooseDialog();

		if (dialog.open() == Window.OK) {
			Object[] selection = dialog.getResult();

			if (selection != null && selection.length > 0
					&& isSingleSelection()) {
				setSelection(selection[0]);
			} else if (selection != null) {
				setSelection(selection);
			} else {
				setSelection(null);
			}

			paths = dialog.getPaths();

			Event e = new Event();
			notifyListeners(SWT.Selection, e);
		}
	}

	protected ChooseDialog createChooseDialog() {
		ChooseDialog cd = new ChooseDialog(null, objects, isSingleSelection(),
				groupedTreeContentProvider, resourceTreeContentProvider) {
			@Override
			protected boolean checkSelectionValid(Object o) {
				return super.checkSelectionValid(o)
						&& EObjectChooserComposite.this.checkSelectionValid(o);
			}

			@Override
			public List<?> getAllowedTypesInThePath() {
				return EObjectChooserComposite.this.getAllowedTypesInThePath();
			}

			@Override
			protected List<?> getInitialElementSelections() {
				List<?> list = getInitialSelection();
				if (!list.isEmpty()) {
					return list;
				} else {
					return super.getInitialElementSelections();
				}
			}

			@Override
			protected boolean selectAfterRefreshTree() {
				return EObjectChooserComposite.this.selectAfterRefreshTree();
			}
		};

		cd.setLabelProvider(labelProvider);
		cd.setContentProvider(contentProvider);

		// Marc Gil: doesn't set the initial elements
		// List<Object> selectedObjects = new ArrayList<Object>();
		// selectedObjects.add(selectedObject);
		// cd.setInitialElementSelections(selectedObjects);

		return cd;
	}

	protected boolean selectAfterRefreshTree() {
		return true;
	}

	public ChooseDialog getChooseDialog() {
		return dialog;
	}

	protected boolean checkSelectionValid(Object o) {
		return true;
	}

	protected List<?> getAllowedTypesInThePath() {
		return Collections.singletonList(Object.class);
	}

	protected List<?> getInitialSelection() {
		return Collections.emptyList();
	}

	/**
	 * Set whether the Choose button is enabled
	 * 
	 * @param isChangeable
	 */
	public void setChangeable(boolean isChangeable) {
		chooseBt.setEnabled(isChangeable);
	}

	/**
	 * Returns the selected object
	 * 
	 * @return the selection
	 */
	public Object getSelection() {
		return selectedObject;
	}

	public List<DataPath> getPaths() {
		return paths;
	}

	public DataPath getPathForElement(Object o) {
		for (DataPath dp : paths) {
			if (dp.getElement() == o) {
				return dp;
			}
		}

		return null;
	}

	/**
	 * Set the selection of the comboViewer
	 * 
	 * @param selection
	 *            the selected object
	 */
	public void setSelection(Object selection) {
		if (selection instanceof NullObject) {
			selectedObject = null;
		} else {
			selectedObject = selection;
		}

		String name = "";
		if (selectedObject != null) {
			name = labelProvider.getText(selectedObject);
			if (name == null) {
				name = "";
			}
		}
		field.setText(name);
	}

	/**
	 * Add a SelectionListener on both the CCombo and the Button
	 * 
	 * @param listener
	 */
	public void addSelectionListener(SelectionListener listener) {
		if (listener == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		TypedListener typedListener = new TypedListener(listener);
		addListener(SWT.Selection, typedListener);
	}

	/**
	 * Remove the SelectionListener of the CCombo and the Button
	 * 
	 * @param listener
	 */
	public void removeSelectionListener(SelectionListener listener) {
		if (listener == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		removeListener(SWT.Selection, listener);
	}

	/**
	 * An object used to replace a null entry in the comboViewer choices tab
	 * 
	 * Creation 6 avr. 2006
	 * 
	 * @author jlescot
	 */
	public static class NullObject {

		/**
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return "";
		}

		/**
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		public boolean equals(Object obj) {
			return obj instanceof NullObject;
		}

	}

	/**
	 * @return the toolkit
	 */
	protected FormToolkit getToolkit() {
		return this.toolkit;
	}

	/**
	 * Enables/disables itself, and also its contained text and button
	 * 
	 * @see org.eclipse.swt.widgets.Control#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		field.setEnabled(enabled);
		chooseBt.setEnabled(enabled);
		if (hasNavigationButton) {
			navigationButton.setEnabled(enabled);
		}
	}

	public boolean hasNavigationButton() {
		return hasNavigationButton;
	}

	public Button getChooseButton() {
		return chooseBt;
	}

	public Button getNavigationButton() {
		return navigationButton;
	}

	public void setText(String text) {
		field.setText(text);
	}

	public Text getText() {
		return field;
	}

	public void setContentProvider(IStructuredContentProvider provider) {
		this.contentProvider = provider;
	}

	public void setGroupedTreeContentProvider(
			ITreeContentProvider goupedContentProvider) {
		groupedTreeContentProvider = goupedContentProvider;
	}

	public void setResourceTreeContentProvider(
			ITreeContentProvider resourceContentProvider) {
		resourceTreeContentProvider = resourceContentProvider;
	}

	/**
	 * Getter for the navigation handler of the navigation button.
	 * 
	 * @return
	 */
	public INavigationHandler getNavigationHandler() {
		return navigationHandler;
	}

	/**
	 *Setter for the navigation handler of the navigation button.
	 * 
	 * @param navigationHandler
	 */
	public void setNavigationHandler(INavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;
	}

}
