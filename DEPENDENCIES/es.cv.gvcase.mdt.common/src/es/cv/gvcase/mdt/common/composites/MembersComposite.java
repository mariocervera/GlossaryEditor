/*******************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Javier Muñoz (Integranova) – Initial implementation
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.composites;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.MoveCommand;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

import es.cv.gvcase.mdt.common.internal.Messages;
import es.cv.gvcase.mdt.common.util.MDTUtil;

/**
 * The Class MembersComposite.
 */
public abstract class MembersComposite extends DetailComposite {

	/** The enable ordering. */
	private boolean enableOrdering = false;

	/** The lab text. */
	private String compositeNameLabelString;

	/** The compositeNameLabel provider. */
	private IBaseLabelProvider labelProvider = MDTUtil.getLabelProvider();

	/** The featureUnderEdition. */
	private EStructuralFeature featureUnderEdition;

	/** The candidateElements. */
	private Collection<?> candidateElements;

	/** Widgets. */

	private Label compositeNameLabel;

	/** Three composites to arrange the user interface. */
	private Composite compositeCandidatesElements;

	/** The composite buttons. */
	private Composite compositeButtons;

	/** The composite list members. */
	private Composite compositeMembersElements;

	/**
	 * List widget with the candidateElements that may be part of the
	 * collection.
	 */
	private StructuredViewer candidateElementsViewer = null;

	/** List widget with the members of the collection. */
	private StructuredViewer memberElementsViewer = null;

	/** Add / Remove buttons. */
	private Button buttonAdd = null;

	/** The button remove. */
	private Button buttonRemove = null;

	/** Up / Down buttons. */
	private Button buttonUp = null;

	/** The button down. */
	private Button buttonDown = null;

	private TabbedPropertySheetWidgetFactory factory;

	private IDoubleClickListener candidateElementsViewerDoubleClickListener = null;

	private IDoubleClickListener selectedElementsViewerDoubleClickListener = null;

	private SelectionListener downSelectionListener = null;

	private SelectionListener upSelectionListener = null;

	private SelectionListener removeSelectionListener = null;

	private SelectionListener addSelectionListener = null;

	/**
	 * Gets the featureUnderEdition.
	 * 
	 * @return the featureUnderEdition
	 */
	public EStructuralFeature getFeatureUnderEdition() {
		return featureUnderEdition;
	}

	public TabbedPropertySheetWidgetFactory getFactory() {
		return factory;
	}

	/**
	 * Sets the enable ordering.
	 * 
	 * @param enableOrdering
	 *            the new enable ordering
	 */
	public void setEnableOrdering(boolean enableOrdering) {
		this.enableOrdering = enableOrdering;
	}

	/**
	 * Checks if is enable ordering.
	 * 
	 * @return true, if is enable ordering
	 */
	protected boolean isEnabledOrdering() {
		return enableOrdering;
	}

	/**
	 * Sets the list candidateElements.
	 * 
	 * @param candidateElements
	 *            the new list candidateElements
	 */
	public void setCandidateElements(Collection<?> listElements) {
		this.candidateElements = listElements;
	}

	/**
	 * Gets the list candidateElements.
	 * 
	 * @return the list candidateElements
	 */
	public StructuredViewer getCandidateElementsViewer() {
		return candidateElementsViewer;
	}

	/**
	 * Gets the list members.
	 * 
	 * @return the list members
	 */
	public StructuredViewer getMemberElementsViewer() {
		return memberElementsViewer;
	}

	/**
	 * Gets the button add.
	 * 
	 * @return the button add
	 */
	protected Button getButtonAdd() {
		return buttonAdd;
	}

	/**
	 * Gets the button remove.
	 * 
	 * @return the button remove
	 */
	protected Button getButtonRemove() {
		return buttonRemove;
	}

	/**
	 * Gets the button down.
	 * 
	 * @return the button down
	 */
	protected Button getButtonDown() {
		return buttonDown;
	}

	protected Button getButtonUp() {
		return buttonUp;
	}

	/**
	 * Instantiates a new members composite.
	 * 
	 * @param parent
	 *            the parent
	 * @param style
	 *            the style
	 * @param labelText
	 *            the compositeNameLabel text
	 */
	public MembersComposite(Composite parent, int style,
			TabbedPropertySheetWidgetFactory widgetFactory,
			EStructuralFeature structuralFeature, String labelText) {
		super(parent, style);

		featureUnderEdition = structuralFeature;
		compositeNameLabelString = labelText;

		factory = widgetFactory;

	}

	/**
	 * Creates the widgets.
	 * 
	 * @param widgetFactory
	 *            the widget factory
	 * @param lp
	 *            the lp
	 * @param enableOrdering
	 *            the enable ordering
	 * @param labelText
	 *            the compositeNameLabel text
	 */
	public void createWidgets(TabbedPropertySheetWidgetFactory widgetFactory,
			IBaseLabelProvider lp, boolean enableOrdering, String labelText) {

		setEnableOrdering(enableOrdering);

		createWidgets(this, widgetFactory);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * es.cv.gvcase.mdt.common.composites.DetailComposite#createWidgets(org.
	 * eclipse.swt.widgets.Composite,
	 * org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory)
	 */
	@Override
	public void createWidgets(Composite composite,
			TabbedPropertySheetWidgetFactory widgetFactory) {

		super.createWidgets(composite, widgetFactory);

		this.setLayout(new GridLayout(3, false));

		if (!compositeNameLabelString.equals("")) { //$NON-NLS-1$
			compositeNameLabel = widgetFactory.createLabel(this,
					compositeNameLabelString);
		}

		compositeCandidatesElements = widgetFactory.createComposite(this);
		compositeCandidatesElements.setLayout(new GridLayout());

		compositeButtons = widgetFactory.createComposite(this);
		compositeButtons.setLayout(new GridLayout());

		compositeMembersElements = widgetFactory.createComposite(this);
		compositeMembersElements.setLayout(new GridLayout());

		candidateElementsViewer = getNewViewer(compositeCandidatesElements);
		candidateElementsViewer.setLabelProvider(getLabelProvider());
		candidateElementsViewer.setContentProvider(getContentProvider());
		candidateElementsViewer.setFilters(getFilters());

		memberElementsViewer = getNewViewer(compositeMembersElements);
		memberElementsViewer.setLabelProvider(getLabelProvider());
		memberElementsViewer.setContentProvider(getContentProvider());

		buttonAdd = widgetFactory.createButton(compositeButtons,
				"  " + Messages.getString("AddLabel") + "  ", //$NON-NLS-1$
				SWT.PUSH);

		buttonRemove = widgetFactory.createButton(compositeButtons,
				"  " + Messages.getString("RemoveLabel") + "  ", SWT.PUSH); //$NON-NLS-1$

		if (isEnabledOrdering()) {
			widgetFactory.createLabel(compositeButtons, " "); //$NON-NLS-1$

			buttonUp = widgetFactory.createButton(compositeButtons,
					"  " + Messages.getString("UpLabel") + "  ", //$NON-NLS-1$
					SWT.PUSH);

			buttonDown = widgetFactory.createButton(compositeButtons,
					"  " + Messages.getString("DownLabel") + "  ", SWT.PUSH); //$NON-NLS-1$
		}

		// hookListeners();
	}

	protected ViewerFilter[] getFilters() {
		return new ViewerFilter[] { new ViewerFilter() {

			@Override
			public boolean select(Viewer viewer, Object parentElement,
					Object element) {
				Collection<?> input = ((Collection<?>) memberElementsViewer
						.getInput());

				return input != null ? !input.contains(element) : true;
			}
		} };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * es.cv.gvcase.mdt.common.composites.DetailComposite#setSectionData(org
	 * .eclipse.swt.widgets.Composite)
	 */
	@Override
	public void setSectionData(Composite composite) {

		super.setSectionData(composite);

		GridData gdata = null;
		if (!compositeNameLabelString.equals("") && compositeNameLabel != null) { //$NON-NLS-1$
			gdata = new GridData(GridData.FILL_HORIZONTAL);
			gdata.horizontalSpan = 3;
			compositeNameLabel.setLayoutData(gdata);
		}
		gdata = new GridData(GridData.FILL_BOTH);
		gdata.heightHint = 200;
		if (this.getParent() != null) {
			gdata.widthHint = (this.getParent().getBounds().width * 40) / 100;
		}
		compositeCandidatesElements.setLayoutData(gdata);
		gdata = new GridData();
		compositeButtons.setLayoutData(gdata);
		gdata = new GridData(GridData.FILL_BOTH);
		gdata.heightHint = 200;
		if (this.getParent() != null) {
			gdata.widthHint = (this.getParent().getBounds().width * 40) / 100;
		}
		compositeMembersElements.setLayoutData(gdata);
		gdata = null;
		gdata = new GridData(GridData.FILL_BOTH);
		getCandidateElementsViewer().getControl().setLayoutData(gdata);
		getMemberElementsViewer().getControl().setLayoutData(gdata);
		getButtonAdd().setLayoutData(gdata);
		getButtonRemove().setLayoutData(gdata);
		if (isEnabledOrdering()) {
			getButtonUp().setLayoutData(gdata);
			getButtonDown().setLayoutData(gdata);
		}
	}

	public SelectionListener getAddButtonSelectionListener() {
		if (addSelectionListener == null) {
			addSelectionListener = new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent e) {
					return;
				}

				public void widgetSelected(SelectionEvent e) {
					handleAddButtonSelected();
				}
			};
		}
		return addSelectionListener;
	}

	public SelectionListener getRemoveButtonSelectionListener() {
		if (removeSelectionListener == null) {
			removeSelectionListener = new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent e) {
					return;
				}

				public void widgetSelected(SelectionEvent e) {
					handleRemoveButtonSelected();
				}
			};
		}
		return removeSelectionListener;
	}

	public SelectionListener getUpButtonSelectionListener() {
		if (upSelectionListener == null) {
			upSelectionListener = new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent e) {
					return;
				}

				public void widgetSelected(SelectionEvent e) {
					handleUpButtonSelected();
				}
			};
		}
		return upSelectionListener;
	}

	public SelectionListener getDownButtonSelectionListener() {
		if (downSelectionListener == null) {
			downSelectionListener = new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent e) {
					return;
				}

				public void widgetSelected(SelectionEvent e) {
					handleDownButtonSelected();
				}
			};
		}
		return downSelectionListener;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.cv.gvcase.mdt.common.composites.DetailComposite#hookListeners()
	 */
	@Override
	public void hookListeners() {

		super.hookListeners();

		getButtonAdd().addSelectionListener(getAddButtonSelectionListener());

		getButtonRemove().addSelectionListener(
				getRemoveButtonSelectionListener());

		if (isEnabledOrdering()) {
			getButtonUp().addSelectionListener(getUpButtonSelectionListener());

			getButtonDown().addSelectionListener(
					getDownButtonSelectionListener());
		}

		getCandidateElementsViewer().addDoubleClickListener(
				getCandidateElementsViewerDoubleClickListener());

		getMemberElementsViewer().addDoubleClickListener(
				getSelectedElementsViewerDoubleClickListener());
	}

	public IDoubleClickListener getCandidateElementsViewerDoubleClickListener() {
		if (candidateElementsViewerDoubleClickListener == null) {
			candidateElementsViewerDoubleClickListener = new IDoubleClickListener() {

				public void doubleClick(DoubleClickEvent event) {
					handleAddButtonSelected();
				}
			};
		}
		return candidateElementsViewerDoubleClickListener;
	}

	public IDoubleClickListener getSelectedElementsViewerDoubleClickListener() {
		if (selectedElementsViewerDoubleClickListener == null) {

			selectedElementsViewerDoubleClickListener = new IDoubleClickListener() {

				public void doubleClick(DoubleClickEvent event) {
					handleRemoveButtonSelected();
				}
			};
		}
		return selectedElementsViewerDoubleClickListener;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.cv.gvcase.mdt.common.composites.DetailComposite#loadData()
	 */
	@Override
	public void loadData() {

		refresh();
	}

	/**
	 * Refresh.
	 * 
	 * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#refresh()
	 */
	public void refresh() {
		Collection<Object> candidates = new ArrayList<Object>();
		if (this.candidateElements != null) {
			candidates.addAll(this.candidateElements);
		} else if (getElement() != null) {
			if (candidateElementsViewer.getInput() == null) {
				candidates.addAll(ItemPropertyDescriptor
						.getReachableObjectsOfType(getElement(),
								getFeatureUnderEdition().getEType()));
			}
		}

		candidateElementsViewer.setInput(candidates);
		memberElementsViewer.setInput(getMemberElements());

		this.candidateElementsViewer.refresh();
		if (this.candidateElementsViewer instanceof TreeViewer) {
			((TreeViewer) this.candidateElementsViewer).expandAll();
		}
		this.memberElementsViewer.refresh();
		if (this.memberElementsViewer instanceof TreeViewer) {
			((TreeViewer) this.memberElementsViewer).expandAll();
		}
	}

	public IContentProvider getContentProvider() {
		return new ArrayContentProvider();
	}

	/**
	 * Gets the compositeNameLabel provider.
	 * 
	 * @return the compositeNameLabel provider
	 */
	public IBaseLabelProvider getLabelProvider() {
		return labelProvider;
	}

	/**
	 * Handle add button selected.
	 */
	public void handleAddButtonSelected() {

		if (getCandidateElementsViewer().getSelection() instanceof StructuredSelection) {
			StructuredSelection selection = (StructuredSelection) getCandidateElementsViewer()
					.getSelection();

			executeAddCommand(selection.toList());

			refresh();
		}
	}

	/**
	 * Handle remove button selected.
	 */
	public void handleRemoveButtonSelected() {

		if (getMemberElementsViewer().getSelection() instanceof StructuredSelection) {
			StructuredSelection selection = (StructuredSelection) getMemberElementsViewer()
					.getSelection();

			executeRemoveCommand(selection.toList());

			refresh();
		}
	}

	/**
	 * Handle up button selected.
	 */
	public void handleUpButtonSelected() {
		executeMovement(true);
		refresh();
	}

	/**
	 * Handle down button selected.
	 */
	public void handleDownButtonSelected() {
		executeMovement(false);
		refresh();
	}

	/**
	 * Execute movement.
	 * 
	 * @param up
	 *            the up
	 */
	private void executeMovement(boolean up) {

		if (memberElementsViewer.getSelection() instanceof StructuredSelection) {
			StructuredSelection selection = (StructuredSelection) memberElementsViewer
					.getSelection();

			if (!selection.isEmpty()) {

				List membersToMove = selection.toList();

				List movedMembers = moveMembers(getMemberElements(),
						membersToMove, up);

				CompoundCommand c = new CompoundCommand();

				for (Object o : selection.toList()) {
					c.append(MoveCommand.create(getEMFEditDomain(),
							getElement(), getFeatureUnderEdition(), o,
							movedMembers.indexOf(o)));
				}
				getEMFEditDomain().getCommandStack().execute(c);
				memberElementsViewer.setSelection(selection);
			}

		}

		return;
	}

	/*
	 * Moves the columns contained in membersToMove one position up or down
	 * (depending on the boolean variable up) in the collection members
	 */
	/**
	 * Move members.
	 * 
	 * @param members
	 *            the members
	 * @param membersToMove
	 *            the members to move
	 * @param up
	 *            the up
	 * @param indexes
	 *            the indexes
	 * 
	 * @return the e list< e object>
	 */
	private List moveMembers(List members, List membersToMove, boolean up) {

		int[] indexes = new int[membersToMove.size()]; // /¿?¿?
		for (int k = 0; k < indexes.length; k++) {
			indexes[k] = -1;
		}

		List<Object> result = new ArrayList<Object>();

		if (firstOrLastElements(members, membersToMove, up, indexes)) {
			// Don't move the candidateElements
			result.addAll(members);
		} else {
			// Copy the candidateElements that won't be moved
			for (Object o : members) {
				if (o instanceof EObject) {
					EObject c = (EObject) o;
					if (!membersToMove.contains(c)) {
						result.add(c);
					}
				}
			}

			// Calculate the indexes of the candidateElements to move
			if (up) { // Case movement upwards

				for (int i = 0; i < membersToMove.size(); i++) {

					Object object = membersToMove.get(i);

					int index = members.indexOf(object);
					int newIndex = -1;

					if (index == 0) {
						newIndex = index;
					} else {
						if (!containsIndex(index - 1, indexes)) {
							newIndex = index - 1;
						} else
							newIndex = index;
					}

					result.add(newIndex, object);
					indexes[i] = newIndex;
				}
			} else { // Case movement downwards

				for (int i = membersToMove.size() - 1; i >= 0; i--) {

					Object eobject = membersToMove.get(i);

					// First get the new indexes for the candidateElements to be
					// moved

					int index = members.indexOf(eobject);
					int newIndex = -1;

					if (index == members.size() - 1) {
						newIndex = members.size() - 1;
					} else {
						if (!containsIndex(index + 1, indexes)) {
							newIndex = index + 1;
						} else
							newIndex = index;
					}

					indexes[i] = newIndex;
				}

				for (int i = 0; i < indexes.length; i++) {
					result.add(indexes[i], membersToMove.get(i));
				}
			}

		}
		return result;
	}

	/*
	 * This method returns true if the selected members to move are together and
	 * in the beginning of the collection (in case up is true) or the end of the
	 * collection (in case up is false) This is important because if this method
	 * returns true the candidateElements shouldn't be moved
	 */
	/**
	 * First or last candidateElements.
	 * 
	 * @param members
	 *            the members
	 * @param membersToMove
	 *            the members to move
	 * @param up
	 *            the up
	 * @param indexes
	 *            the indexes
	 * 
	 * @return true, if successful
	 */
	private boolean firstOrLastElements(List members, List membersToMove,
			boolean up, int[] indexes) {

		if (membersToMove.size() == members.size())
			return true;

		int size = membersToMove.size();
		int[] indexes2 = new int[size];

		int i = 0;
		for (Object member : membersToMove) {
			indexes2[i] = members.indexOf(member);
			i++;
		}

		// If the first or last member are not in the first or last position we
		// can stop here
		if ((indexes2[0] != 0 && up) || (indexes2[size - 1] != size - 1 && !up)) {
			return false;
		}

		// Are the members together?
		for (int j = 0; j < indexes2.length - 1; j++) {
			if (indexes2[j + 1] - indexes2[j] != 1) {
				return false;
			}
		}

		// Maybe the group of members is at the beginning or the end of the
		// collection
		// but we have to move them anyway because of the direction of the
		// movement
		if ((indexes2[0] == 0 && !up) || indexes2[size - 1] == size - 1 && up) {
			return false;
		}

		indexes = indexes2;

		return true;
	}

	/**
	 * Contains index.
	 * 
	 * @param i
	 *            the i
	 * @param indexes
	 *            the indexes
	 * 
	 * @return true, if successful
	 */
	private boolean containsIndex(int i, int[] indexes) {

		for (int j = 0; j < indexes.length; j++) {
			if (indexes[j] == i) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Execute add command.
	 * 
	 * @param objectsToAdd
	 *            the object to add
	 */
	protected void executeAddCommand(Collection<?> objectsToAdd) {

		if (getEMFEditDomain() == null || getElement() == null
				|| getFeatureUnderEdition() == null)
			return;

		Command command = AddCommand.create(getEMFEditDomain(), getElement(),
				getFeatureUnderEdition(), objectsToAdd);

		getEMFEditDomain().getCommandStack().execute(command);
	}

	/**
	 * Execute remove command.
	 * 
	 * @param objectsToRemove
	 *            the object to remove
	 */
	protected void executeRemoveCommand(Collection<?> objectsToRemove) {

		if (getEMFEditDomain() == null || getElement() == null
				|| getFeatureUnderEdition() == null)
			return;

		Command command = RemoveCommand.create(getEMFEditDomain(),
				getElement(), getFeatureUnderEdition(), objectsToRemove);

		getEMFEditDomain().getCommandStack().execute(command);
	}

	/**
	 * Execute add all command.
	 * 
	 * @param membersToAdd
	 *            the members to add
	 */
	// private void executeAddAllCommand(EList<EObject> membersToAdd) {
	//
	// if (getEMFEditDomain() == null || getElement() == null
	// || getFeatureUnderEdition() == null)
	// return;
	//
	// Command command = SetCommand.create(getEMFEditDomain(), getElement(),
	// getFeatureUnderEdition(), membersToAdd);
	//
	// getEMFEditDomain().getCommandStack().execute(command);
	// }

	/**
	 * Gets the members.
	 * 
	 * @return the members
	 */
	protected EList<EObject> getMemberElements() {

		if (getElement() != null) {
			Object featureValue = getElement().eGet(getFeatureUnderEdition());

			if (featureValue instanceof EList) {
				return (EList<EObject>) featureValue;
			}
		}

		return null;
	}

	/**
	 * Creates a new Viewer
	 * 
	 * @param parent
	 * @param lp
	 * @return
	 */
	protected abstract StructuredViewer getNewViewer(Composite parent);

	public void updateCandidateElements(Collection<?> elements) {
		if (elements != null) {
			candidateElementsViewer.setInput(elements);
		}
	}

	public void updateSelectedElements(Collection<?> elements) {
		if (elements != null) {
			memberElementsViewer.setInput(elements);
		}
	}

	public void setLabelProvider(IBaseLabelProvider labelProvider) {
		this.labelProvider = labelProvider;

	}

	/**
	 * @param candidateElementsViewerDoubleClickListener
	 *            the candidateElementsViewerDoubleClickListener to set
	 */
	public void setCandidateElementsViewerDoubleClickListener(
			IDoubleClickListener candidateElementsViewerDoubleClickListener) {
		this.candidateElementsViewerDoubleClickListener = candidateElementsViewerDoubleClickListener;
	}

	/**
	 * @param selectedElementsViewerDoubleClickListener
	 *            the selectedElementsViewerDoubleClickListener to set
	 */
	public void setSelectedElementsViewerDoubleClickListener(
			IDoubleClickListener selectedElementsViewerDoubleClickListener) {
		this.selectedElementsViewerDoubleClickListener = selectedElementsViewerDoubleClickListener;
	}

	/**
	 * @param downSelectionListener
	 *            the downSelectionListener to set
	 */
	public void setDownSelectionListener(SelectionListener downSelectionListener) {
		this.downSelectionListener = downSelectionListener;
	}

	/**
	 * @param upSelectionListener
	 *            the upSelectionListener to set
	 */
	public void setUpSelectionListener(SelectionListener upSelectionListener) {
		this.upSelectionListener = upSelectionListener;
	}

	/**
	 * @param removeSelectionListener
	 *            the removeSelectionListener to set
	 */
	public void setRemoveSelectionListener(
			SelectionListener removeSelectionListener) {
		this.removeSelectionListener = removeSelectionListener;
	}

	/**
	 * @param addSelectionListener
	 *            the addSelectionListener to set
	 */
	public void setAddSelectionListener(SelectionListener addSelectionListener) {
		this.addSelectionListener = addSelectionListener;
	}

	public void setAddButtonEnablement(boolean enablement) {
		if (buttonAdd != null && !buttonAdd.isDisposed()) {
			buttonAdd.setEnabled(enablement);
		}
	}

	public void setRemoveButtonEnablement(boolean enablement) {
		if (buttonRemove != null && !buttonRemove.isDisposed()) {
			buttonRemove.setEnabled(enablement);
		}
	}

}
