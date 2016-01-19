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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import es.cv.gvcase.emf.ui.common.utils.ImageUtils;
import es.cv.gvcase.mdt.common.Activator;

// TODO: Auto-generated Javadoc
/**
 * A wizard page that allows selecting elements from a tree. Used in a new
 * diagram wizard. Those elements will be displayed in the newly created
 * diagram.
 */
public class DiagramElementsSelectionPage extends WizardPage {

	/** The label provider. */
	private IBaseLabelProvider labelProvider;

	/** The content provider. */
	private IContentProvider contentProvider;

	/** The selected elements. */
	private List<EObject> memberElements;

	/** Widgets. */

	/** Tree with the candidate elements that may be part of the collection. */
	private TreeViewer candidateElementsViewer;

	/** Tree with the member elements of the collection. */
	private TreeViewer memberElementsViewer;

	/** The candidate elements menu. */
	private ViewerPopUpMenu candidateElementsMenu;

	/** The add buttons. */
	private List<Button> addButtons;

	/** The remove buttons. */
	private List<Button> removeButtons;

	/** The hide selected elements button. */
	private Button hideElementsButton;

	/** The number of selected elements. */
	private Label numSelElements;

	/** The total elements to draw. */
	private Label totalElements;

	/**
	 * Instantiates a new select model elements for diagram dialog.
	 * 
	 * @param name
	 *            the name
	 * @param title
	 *            the title
	 * @param description
	 *            the description
	 */
	public DiagramElementsSelectionPage(String name, String title,
			String description) {
		super(name);
		setTitle(title);
		setDescription(description);
	}

	/**
	 * Gets the root model element from the wizard. The root model element must
	 * have been set beforehand.
	 * 
	 * @return the model element
	 */
	protected EObject getModelElement() {
		IWizard parentWizard = getWizard();
		if (parentWizard instanceof INewDiagramFileWizard) {
			return ((INewDiagramFileWizard) parentWizard).getRootModelElement();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.DialogPage#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			EObject input = getModelElement();
			if (input == null) {
				return;
			}
			if (input.equals(candidateElementsViewer.getInput())) {
				return;
			}
			candidateElementsViewer.setInput(input);
			memberElementsViewer.setInput(null);

			getMemberElements().clear();
		}
	}

	/**
	 * Gets the selected eObjects by user.
	 * 
	 * @return the selected eObjects
	 */
	public List<EObject> getSelectedEObjects() {
		List<EObject> elements = new ArrayList<EObject>();
		for (EObject eObject : getMemberElements()) {
			elements.add(eObject);
			elements.addAll(getContentsWithView(eObject));
		}
		return elements;
	}

	/** The all contents to draw. */
	private Map<EObject, List<EObject>> allContentsToDraw;

	/**
	 * Gets the contents with view.
	 * 
	 * @param eObject
	 *            the e object
	 * 
	 * @return the contents with view
	 */
	protected List<EObject> getContentsWithView(EObject eObject) {
		if (allContentsToDraw == null) {
			allContentsToDraw = new HashMap<EObject, List<EObject>>();
		}
		List<EObject> contents = allContentsToDraw.get(eObject);
		if (contents != null) {
			return contents;
		} else {
			contents = new ArrayList<EObject>();
			for (TreeIterator<EObject> iter = eObject.eAllContents(); iter
					.hasNext();) {
				EObject child = iter.next();
				if (hasVisualID(child)) {
					contents.add(child);
				}
			}
		}

		return contents;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		setControl(composite);

		createDialogArea(composite);

		Dialog.applyDialogFont(composite);
	}

	/**
	 * Creates the dialog area.
	 * 
	 * @param parent
	 *            the parent
	 */
	protected void createDialogArea(Composite parent) {
		// container composite
		Composite leftCenterRightComposite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.heightHint = convertHeightInCharsToPixels(20);
		leftCenterRightComposite.setLayoutData(gridData);
		GridLayout gridLayout = new GridLayout(3, false);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		leftCenterRightComposite.setLayout(gridLayout);

		// create left composite
		Composite leftComposite = new Composite(leftCenterRightComposite,
				SWT.NONE);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.widthHint = convertWidthInCharsToPixels(20);
		leftComposite.setLayoutData(gridData);
		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		leftComposite.setLayout(layout);

		// create center composite
		Composite centerComposite = new Composite(leftCenterRightComposite,
				SWT.NONE);
		layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		centerComposite.setLayout(layout);
		centerComposite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,
				false, false));

		// create right composite
		Composite rightComposite = new Composite(leftCenterRightComposite,
				SWT.NONE);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.widthHint = convertWidthInCharsToPixels(20);
		rightComposite.setLayoutData(gridData);
		layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		rightComposite.setLayout(layout);

		// create left search
		Text candidateElementsSearch = createSearch(leftComposite);

		// create left tree viewer
		candidateElementsViewer = createTreeViewer(leftComposite);
		candidateElementsViewer.setContentProvider(getContentProvider());
		candidateElementsViewer.setLabelProvider(getLabelProvider());

		// create buttons
		createButtons(centerComposite);

		// create right search
		Text memberElementsSearch = createSearch(rightComposite);

		// create right tree viewer
		memberElementsViewer = createTreeViewer(rightComposite);
		memberElementsViewer.setContentProvider(getContentProvider());
		memberElementsViewer.setLabelProvider(getLabelProvider());

		// create hide button
		createButton(leftComposite);

		// create feedback labels
		createLabels(rightComposite);

		// create filters
		candidateElementsViewer
				.setFilters(createFilters(candidateElementsSearch));
		memberElementsViewer.setFilters(createFilters(memberElementsSearch));

		// create menus
		candidateElementsMenu = createPopUpMenu(candidateElementsViewer, true);
		createPopUpMenu(memberElementsViewer, false);

		candidateElementsViewer
				.addSelectionChangedListener(new ViewerSelectionChangedListener());
		memberElementsViewer
				.addSelectionChangedListener(new ViewerSelectionChangedListener());
	}

	/**
	 * Creates the search elements section.
	 * 
	 * @param parent
	 *            the parent
	 * 
	 * @return the text field
	 */
	protected Text createSearch(Composite parent) {
		Composite search = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		search.setLayout(layout);

		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		search.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// create search field
		Text searchText = new Text(search, SWT.BORDER);
		searchText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// create search button
		Button searchButton = new Button(search, SWT.PUSH);
		searchButton.setImage(ImageUtils.getSharedImage(ImageUtils.IMG_SEARCH));

		searchButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (candidateElementsViewer != null) {
					candidateElementsViewer.refresh();
				}
				if (memberElementsViewer != null) {
					memberElementsViewer.refresh();
				}
			}
		});

		return searchText;
	}

	/**
	 * Creates the tree viewer.
	 * 
	 * @param parent
	 *            the parent
	 * 
	 * @return the tree viewer
	 */
	protected TreeViewer createTreeViewer(Composite parent) {
		Tree tree = new Tree(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.BORDER);
		TreeViewer treeViewer = new TreeViewer(tree);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		tree.setLayoutData(data);

		return treeViewer;
	}

	/**
	 * Creates the buttons.
	 * 
	 * @param parent
	 *            the parent
	 */
	protected void createButtons(Composite parent) {
		Button addButton = new Button(parent, SWT.PUSH);
		// addButton.setText("Add");
		addButton.setToolTipText("Add selected elements");
		addButton.setImage(PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_OBJ_ADD));
		addButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		addButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				handleAddButtonSelected();
			}
		});

		Button addReferencedButton = new Button(parent, SWT.PUSH);
		// addReferencedButton.setText("Add");
		addReferencedButton
				.setToolTipText("Add selected elements and referenced by them");
		addReferencedButton.setImage(Activator.getDefault().getBundledImage(
				"/icons/referencedElements.gif"));
		addReferencedButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true,
				false));
		addReferencedButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				handleAddReferencedElementsButtonSelected();
			}
		});

		Button addReferencingButton = new Button(parent, SWT.PUSH);
		// addReferencingButton.setText("Add");
		addReferencingButton
				.setToolTipText("Add selected elements and referencing to them");
		addReferencingButton.setImage(Activator.getDefault().getBundledImage(
				"/icons/referencingElements.gif"));
		addReferencingButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP,
				true, false));
		addReferencingButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				handleAddReferencingElementsButtonSelected();
			}
		});

		Button addAllRelatedButton = new Button(parent, SWT.PUSH);
		// addAllRelatedButton.setText("Add");
		addAllRelatedButton
				.setToolTipText("Add selected elements, referenced by them and referencing to them");
		addAllRelatedButton.setImage(Activator.getDefault().getBundledImage(
				"/icons/allRelatedElements.gif"));
		addAllRelatedButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true,
				false));
		addAllRelatedButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				handleAddAllRelatedElementsButtonSelected();
			}
		});

		// Button addAllButton = new Button(parent, SWT.PUSH);
		// addAllButton.setText("Add All -->");
		// addAllButton.setToolTipText("Add all elements");
		// addAllButton
		// .setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		// addAllButton.addSelectionListener(new SelectionListener() {
		// public void widgetDefaultSelected(SelectionEvent e) {
		// }
		//
		// public void widgetSelected(SelectionEvent e) {
		// handleAddAllButtonSelected();
		// }
		// });

		Button removeButton = new Button(parent, SWT.PUSH);
		// removeButton.setText("Remove");
		removeButton.setToolTipText("Remove selected elements");
		removeButton
				.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		removeButton.setImage(PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_TOOL_DELETE));
		removeButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				handleRemoveButtonSelected();
			}
		});

		// Button removeAllButton = new Button(parent, SWT.PUSH);
		// removeAllButton.setText("<-- Remove All");
		// removeAllButton.setToolTipText("Remove all elements");
		// removeAllButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true,
		// false));
		// removeAllButton.addSelectionListener(new SelectionListener() {
		// public void widgetDefaultSelected(SelectionEvent e) {
		// }
		//
		// public void widgetSelected(SelectionEvent e) {
		// handleRemoveAllButtonSelected();
		// }
		// });

		addButton.setEnabled(false);
		addReferencedButton.setEnabled(false);
		addReferencingButton.setEnabled(false);
		addAllRelatedButton.setEnabled(false);

		addButtons = new ArrayList<Button>();
		addButtons.add(addButton);
		addButtons.add(addReferencedButton);
		addButtons.add(addReferencingButton);
		addButtons.add(addAllRelatedButton);

		removeButton.setEnabled(false);

		removeButtons = new ArrayList<Button>();
		removeButtons.add(removeButton);
	}

	/**
	 * Creates the button to hide selected elements.
	 * 
	 * @param parent
	 *            the parent
	 */
	protected void createButton(Composite parent) {
		hideElementsButton = new Button(parent, SWT.CHECK);
		hideElementsButton.setText("Hide selected elements");
		GridData data = new GridData(GridData.VERTICAL_ALIGN_BEGINNING
				| GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		hideElementsButton.setLayoutData(data);

		hideElementsButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				if (candidateElementsViewer != null) {
					candidateElementsViewer.refresh();
				}
			}
		});
	}

	/**
	 * Creates the labels to give user feedback.
	 * 
	 * @param parent
	 *            the parent
	 */
	protected void createLabels(Composite parent) {
		numSelElements = new Label(parent, SWT.NONE);
		GridData data = new GridData(GridData.VERTICAL_ALIGN_BEGINNING
				| GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		numSelElements.setLayoutData(data);
		numSelElements.setText("- Selected elements: "
				+ String.valueOf(getMemberElements().size()));

		totalElements = new Label(parent, SWT.NONE);
		data = new GridData(GridData.VERTICAL_ALIGN_BEGINNING
				| GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		totalElements.setLayoutData(data);
		totalElements.setText(String.valueOf("- Elements to draw: "
				+ getSelectedEObjects().size()));
	}

	/**
	 * Creates the filters.
	 * 
	 * @param searchField
	 *            the search field
	 * 
	 * @return the viewer filter[]
	 */
	protected ViewerFilter[] createFilters(Text searchField) {
		if (searchField == null) {
			return new ViewerFilter[] { new SelectFilter() };
		} else {
			return new ViewerFilter[] { new SelectFilter(),
					new SearchFilter(searchField) };
		}
	}

	/**
	 * Creates the pop up menu.
	 * 
	 * @param viewer
	 *            the viewer
	 * @param addMMElementsFilter
	 *            the add mm elements filter
	 * 
	 * @return the viewer pop up menu
	 */
	protected ViewerPopUpMenu createPopUpMenu(TreeViewer viewer,
			boolean addMMElementsFilter) {
		return new ViewerPopUpMenu(viewer, getModelElement(),
				addMMElementsFilter);
	}

	/**
	 * Handle add button selected.
	 */
	protected void handleAddButtonSelected() {
		StructuredSelection selection = getCandidateElementsSelection();
		if (selection == null) {
			return;
		}

		if (selection.size() == 1) {
			Object element = selection.getFirstElement();
			if (element instanceof EObject) {
				addToMemberElements((EObject) element);
			}
		} else {
			for (Object element : selection.toList()) {
				if (element instanceof EObject) {
					addToMemberElements((EObject) element);
				}
			}
		}

		refreshMembersViewer();
		refreshCandidatesViewer();
		// enable add buttons depending on candidate elements selected
		selection = getCandidateElementsSelection();
		enableAddButtons(selection != null && selection.size() > 0);
	}

	/**
	 * Handle add referenced elements button selected.
	 */
	protected void handleAddReferencedElementsButtonSelected() {
		StructuredSelection selection = getCandidateElementsSelection();
		if (selection == null) {
			return;
		}

		if (selection.size() == 1) {
			if (!(selection.getFirstElement() instanceof EObject)) {
				return;
			}
			Object element = selection.getFirstElement();
			if (element instanceof EObject) {
				addToMemberElements((EObject) element);
				// Add related elements that have to be drawn on diagram
				addToMemberElements(getReferencedElements((EObject) element));
			}
		} else {
			for (Object element : selection.toList()) {
				if (element instanceof EObject) {
					addToMemberElements((EObject) element);
					// Add related elements that have to be drawn on diagram
					addToMemberElements(getReferencedElements((EObject) element));
				}
			}
		}

		refreshMembersViewer();
		refreshCandidatesViewer();
		// enable add buttons depending on candidate elements selected
		selection = getCandidateElementsSelection();
		enableAddButtons(selection != null && selection.size() > 0);
	}

	/**
	 * Handle add referencing elements button selected.
	 */
	protected void handleAddReferencingElementsButtonSelected() {
		StructuredSelection selection = getCandidateElementsSelection();
		if (selection == null) {
			return;
		}

		if (selection.size() == 1) {
			if (!(selection.getFirstElement() instanceof EObject)) {
				return;
			}
			Object element = selection.getFirstElement();
			if (element instanceof EObject) {
				addToMemberElements((EObject) element);
				// Add related elements that have to be drawn on diagram
				addToMemberElements(getReferencingElements((EObject) element));
			}
		} else {
			for (Object element : selection.toList()) {
				if (element instanceof EObject) {
					addToMemberElements((EObject) element);
					// Add related elements that have to be drawn on diagram
					addToMemberElements(getReferencingElements((EObject) element));
				}
			}
		}

		refreshMembersViewer();
		refreshCandidatesViewer();
		// enable add buttons depending on candidate elements selected
		selection = getCandidateElementsSelection();
		enableAddButtons(selection != null && selection.size() > 0);
	}

	/**
	 * Handle add all related elements button selected.
	 */
	protected void handleAddAllRelatedElementsButtonSelected() {
		StructuredSelection selection = getCandidateElementsSelection();
		if (selection == null) {
			return;
		}

		if (selection.size() == 1) {
			if (!(selection.getFirstElement() instanceof EObject)) {
				return;
			}
			Object element = selection.getFirstElement();
			if (element instanceof EObject) {
				addToMemberElements((EObject) element);
				// Add related elements that have to be drawn on diagram
				addToMemberElements(getAllRelatedElements((EObject) element));
			}
		} else {
			for (Object element : selection.toList()) {
				if (element instanceof EObject) {
					// Add related elements that have to be drawn on diagram
					addToMemberElements(getAllRelatedElements((EObject) element));
				}
			}
		}

		refreshMembersViewer();
		refreshCandidatesViewer();
		// enable add buttons depending on candidate elements selected
		selection = getCandidateElementsSelection();
		enableAddButtons(selection != null && selection.size() > 0);
	}

	/**
	 * Handle remove button selected.
	 */
	protected void handleRemoveButtonSelected() {
		StructuredSelection selection = getMemberElementsSelection();
		if (selection == null) {
			return;
		}

		if (selection.size() == 1) {
			getMemberElements().remove(selection.getFirstElement());
		} else {
			getMemberElements().removeAll(selection.toList());
		}

		refreshMembersViewer();
		refreshCandidatesViewer();
		// enable remove buttons depending on member elements selected
		selection = getMemberElementsSelection();
		enableRemoveButtons(selection != null && selection.size() > 0);
	}

	/**
	 * Handle add all button selected.
	 */
	protected void handleAddAllButtonSelected() {

	}

	/**
	 * Handle remove all button selected.
	 */
	protected void handleRemoveAllButtonSelected() {
		getMemberElements().clear();

		refreshMembersViewer();
		refreshCandidatesViewer();
	}

	/**
	 * Enable add buttons.
	 * 
	 * @param enabled
	 *            the enabled
	 */
	protected void enableAddButtons(boolean enabled) {
		for (Button b : addButtons) {
			b.setEnabled(enabled);
		}
	}

	/**
	 * Enable remove buttons.
	 * 
	 * @param enabled
	 *            the enabled
	 */
	protected void enableRemoveButtons(boolean enabled) {
		for (Button b : removeButtons) {
			b.setEnabled(enabled);
		}
	}

	/**
	 * Checks if the eObject can be drawn on diagram.
	 * 
	 * @param eObject
	 *            the eObject
	 * 
	 * @return true, if successful
	 */
	protected boolean hasVisualID(EObject eObject) {
		// it should be overridden for each diagram initialization
		return true;
	}

	/**
	 * Checks if eObject is a selectable element in the viewer.
	 * 
	 * @param eObject
	 *            the eObject
	 * 
	 * @return true, if is selectable element
	 */
	protected boolean isSelectableElement(EObject eObject) {
		// it could be overridden for each diagram initialization
		if (hasVisualID(eObject)
				&& getModelElement().equals(eObject.eContainer())) {
			return true;
		}
		return false;
	}

	/** The referenced elements to draw. */
	private Map<EObject, List<EObject>> referencedElementsToDraw;

	/**
	 * Gets the referenced elements.
	 * 
	 * @param eObject
	 *            the e object
	 * 
	 * @return the referenced elements
	 */
	protected List<EObject> getReferencedElements(EObject eObject) {
		if (referencedElementsToDraw == null) {
			referencedElementsToDraw = new HashMap<EObject, List<EObject>>();
		}
		List<EObject> elements = referencedElementsToDraw.get(eObject);
		if (elements != null) {
			return elements;
		} else {
			elements = referencedElements(eObject);
		}

		return elements;
	}

	/**
	 * Gets the referenced elements.
	 * 
	 * @param eObject
	 *            the e object
	 * 
	 * @return the list< e object>
	 */
	protected List<EObject> referencedElements(EObject eObject) {
		// it should be overridden for each diagram initialization
		return Collections.emptyList();
	}

	/** The referencing elements to draw. */
	private Map<EObject, List<EObject>> referencingElementsToDraw;

	/**
	 * Gets the referencing elements.
	 * 
	 * @param eObject
	 *            the e object
	 * 
	 * @return the referencing elements
	 */
	protected List<EObject> getReferencingElements(EObject eObject) {
		if (referencingElementsToDraw == null) {
			referencingElementsToDraw = new HashMap<EObject, List<EObject>>();
		}
		List<EObject> elements = referencingElementsToDraw.get(eObject);
		if (elements != null) {
			return elements;
		} else {
			elements = referencingElements(eObject);
		}

		return elements;
	}

	/**
	 * Gets the referencing elements.
	 * 
	 * @param eObject
	 *            the e object
	 * 
	 * @return the list< e object>
	 */
	protected List<EObject> referencingElements(EObject eObject) {
		// it should be overridden for each diagram initialization
		return Collections.emptyList();
	}

	/** The all related elements to draw. */
	private Map<EObject, List<EObject>> allRelatedElementsToDraw;

	/**
	 * Gets all related elements.
	 * 
	 * @param eObject
	 *            the e object
	 * 
	 * @return the all related elements
	 */
	protected List<EObject> getAllRelatedElements(EObject eObject) {
		if (allRelatedElementsToDraw == null) {
			allRelatedElementsToDraw = new HashMap<EObject, List<EObject>>();
		}
		List<EObject> elements = allRelatedElementsToDraw.get(eObject);
		if (elements != null) {
			return elements;
		} else {
			elements = allRelatedElements(eObject);
		}

		return elements;
	}

	/**
	 * All related elements.
	 * 
	 * @param eObject
	 *            the e object
	 * 
	 * @return the list< e object>
	 */
	protected List<EObject> allRelatedElements(EObject eObject) {
		// it should be overridden for each diagram initialization
		return Collections.emptyList();
	}

	/**
	 * Gets the candidate elements selection.
	 * 
	 * @return the candidate elements selection
	 */
	protected StructuredSelection getCandidateElementsSelection() {
		if (candidateElementsViewer == null
				|| !(candidateElementsViewer.getSelection() instanceof StructuredSelection)) {
			return null;
		}
		return (StructuredSelection) candidateElementsViewer.getSelection();
	}

	/**
	 * Gets the member elements selection.
	 * 
	 * @return the member elements selection
	 */
	protected StructuredSelection getMemberElementsSelection() {
		if (memberElementsViewer == null
				|| !(memberElementsViewer.getSelection() instanceof StructuredSelection)) {
			return null;
		}
		return (StructuredSelection) memberElementsViewer.getSelection();
	}

	/**
	 * Gets the member elements.
	 * 
	 * @return the member elements
	 */
	protected List<EObject> getMemberElements() {
		if (memberElements == null) {
			memberElements = new ArrayList<EObject>();
		}
		return memberElements;
	}

	/**
	 * Adds the to member elements.
	 * 
	 * @param element
	 *            the element
	 */
	protected void addToMemberElements(EObject element) {
		if (!getMemberElements().contains(element)) {
			getMemberElements().add(element);
		}
	}

	/**
	 * Adds the to member elements.
	 * 
	 * @param elements
	 *            the elements
	 */
	protected void addToMemberElements(List<EObject> elements) {
		for (EObject eObj : elements) {
			if (!getMemberElements().contains(eObj)) {
				getMemberElements().add(eObj);
			}
		}
	}

	/**
	 * Refresh members viewer.
	 */
	protected void refreshMembersViewer() {
		memberElementsViewer.setInput(getMemberElements());
		memberElementsViewer.refresh();

		numSelElements.setText("- Selected elements: "
				+ String.valueOf(getMemberElements().size()));
		totalElements.setText("- Elements to draw: "
				+ String.valueOf(getSelectedEObjects().size()));
	}

	/**
	 * Refresh candidates viewer.
	 */
	protected void refreshCandidatesViewer() {
		candidateElementsViewer.refresh();
	}

	/**
	 * Gets the label provider.
	 * 
	 * @return the label provider
	 */
	protected IBaseLabelProvider getLabelProvider() {
		if (labelProvider == null) {
			labelProvider = new AdapterFactoryLabelProvider(
					new ComposedAdapterFactory(
							ComposedAdapterFactory.Descriptor.Registry.INSTANCE));
		}
		return labelProvider;
	}

	/**
	 * Gets the content provider.
	 * 
	 * @return the content provider
	 */
	protected IContentProvider getContentProvider() {
		if (contentProvider == null) {
			contentProvider = new AdapterFactoryContentProvider(
					new ComposedAdapterFactory(
							ComposedAdapterFactory.Descriptor.Registry.INSTANCE)) {

				@Override
				public Object[] getElements(Object object) {
					if (object instanceof List) {
						List<Object> elements = new ArrayList<Object>();
						for (Object o : (List) object) {
							elements.add(o);
						}
						return elements.toArray();
					}
					return super.getElements(object);
				}

			};
		}
		return contentProvider;
	}

	/**
	 * The Class SelectFilter.
	 */
	class SelectFilter extends ViewerFilter {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers
		 * .Viewer, java.lang.Object, java.lang.Object)
		 */
		@Override
		public boolean select(Viewer viewer, Object parentElement,
				Object element) {
			if (!(element instanceof EObject)) {
				return false;
			}
			if (viewer.equals(candidateElementsMenu.getViewer())) {
				if (!candidateElementsMenu.getSelectedEClassifiers().contains(
						((EObject) element).eClass())) {
					return false;
				}
			}
			if (hideElementsButton != null
					&& viewer.equals(candidateElementsViewer)) {
				if (hideElementsButton.getSelection()) {
					// elements selected have to be hidden
					if (getMemberElements().contains(element)) {
						return false;
					}
				}
			}
			return true;
		}
	}

	/**
	 * The Class SearchFilter.
	 */
	class SearchFilter extends ViewerFilter {

		/** The search field. */
		Text searchField;

		/**
		 * Instantiates a new search filter.
		 * 
		 * @param searchField
		 *            the search field
		 */
		public SearchFilter(Text searchField) {
			this.searchField = searchField;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers
		 * .Viewer, java.lang.Object, java.lang.Object)
		 */
		@Override
		public boolean select(Viewer viewer, Object parentElement,
				Object element) {
			if (searchField == null) {
				return true;
			}
			String text = searchField.getText().toLowerCase();
			if (text == null || "".equals(text)) {
				return true;
			}
			IBaseLabelProvider provider = ((TreeViewer) viewer)
					.getLabelProvider();
			if (provider instanceof ILabelProvider) {
				String elementText = ((ILabelProvider) provider).getText(
						element).toLowerCase();
				if (elementText.indexOf(text) == -1) {
					return false;
				}
			}
			return true;
		}

	}

	/**
	 * The listener interface for receiving viewerSelectionChanged events. The
	 * class that is interested in processing a viewerSelectionChanged event
	 * implements this interface, and the object created with that class is
	 * registered with a component using the component's
	 * <code>addViewerSelectionChangedListener<code> method. When
	 * the viewerSelectionChanged event occurs, that object's appropriate
	 * method is invoked.
	 * 
	 * @see ViewerSelectionChangedEvent
	 */
	class ViewerSelectionChangedListener implements ISelectionChangedListener {

		/** The changed selection. */
		private boolean changedSelection = false;

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged
		 * (org.eclipse.jface.viewers.SelectionChangedEvent)
		 */
		public void selectionChanged(SelectionChangedEvent event) {
			if (!(event.getSource() instanceof Viewer)) {
				return;
			}
			if (changedSelection) {
				changedSelection = false;
				return;
			}
			if (!(event.getSelection() instanceof StructuredSelection)) {
				changedSelection = true;
				// remove selection
				((Viewer) event.getSource())
						.setSelection(new StructuredSelection());
				return;
			}

			StructuredSelection selection = (StructuredSelection) event
					.getSelection();
			if (selection.isEmpty()) {
				return;
			}
			List<Object> filterSelection = new ArrayList<Object>();
			for (Iterator iter = selection.iterator(); iter.hasNext();) {
				Object o = iter.next();
				if (o instanceof EObject && isSelectableElement((EObject) o)) {
					filterSelection.add(o);
				}
			}
			changedSelection = true;
			if (event.getSource().equals(candidateElementsViewer)) {
				enableAddButtons(filterSelection.size() > 0);
			} else {
				enableRemoveButtons(filterSelection.size() > 0);
			}
			((Viewer) event.getSource()).setSelection(new StructuredSelection(
					filterSelection));
		}

	}

}
