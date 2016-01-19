/*******************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – Initial implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.composites.extended;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
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

import es.cv.gvcase.mdt.common.commands.AbstractCommonTransactionalCommmand;
import es.cv.gvcase.mdt.common.commands.AddExtendedFeatureValueCommand;
import es.cv.gvcase.mdt.common.commands.RemoveExtendedFeatureValueCommand;
import es.cv.gvcase.mdt.common.internal.Messages;
import es.cv.gvcase.mdt.common.model.ExtendedFeatureElement;
import es.cv.gvcase.mdt.common.model.ExtendedFeatureElementFactory;
import es.cv.gvcase.mdt.common.util.MDTUtil;

/**
 * Composite that shows the members of an EModelElement with extended features.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public abstract class ExtendedMembersComposite extends ExtendedDetailsComposite {

	/** The enable ordering. */
	private boolean enableOrdering = false;

	/** The lab text. */
	private String compositeNameLabelString;

	/** The compositeNameLabel provider. */
	private IBaseLabelProvider labelProvider = MDTUtil.getLabelProvider();

	/**
	 * The extended feature identifier.
	 */
	private String extendedFeatureUnderEditionID = null;

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
	 * The type of EObjects the extended feature can store.
	 */
	private EClassifier eType = null;

	/**
	 * Gets the extended feature's id being edited.
	 */
	public String getExtendedFeatureUnderEditionID() {
		return extendedFeatureUnderEditionID;
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
	public ExtendedMembersComposite(Composite parent, int style,
			TabbedPropertySheetWidgetFactory widgetFactory,
			String extendedFeatureID, String labelText, EClassifier type) {
		super(parent, style);

		// featureUnderEdition = structuralFeature;
		extendedFeatureUnderEditionID = extendedFeatureID;
		compositeNameLabelString = labelText;

		factory = widgetFactory;
		eType = type;
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
		candidateElementsViewer.setContentProvider(new ArrayContentProvider());
		candidateElementsViewer
				.setFilters(new ViewerFilter[] { new ViewerFilter() {

					@Override
					public boolean select(Viewer viewer, Object parentElement,
							Object element) {
						Collection<?> input = ((Collection<?>) memberElementsViewer
								.getInput());

						return memberElementsViewer.getInput() != null ? !input
								.contains(element) : true;
					}
				} });

		memberElementsViewer = getNewViewer(compositeMembersElements);
		memberElementsViewer.setLabelProvider(getLabelProvider());
		memberElementsViewer.setContentProvider(new ArrayContentProvider());

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
		if (getCandidateElements() != null) {
			candidateElementsViewer.setInput(getCandidateElements());
		} else if (candidateElementsViewer.getInput() == null) {
			candidateElementsViewer.setInput(getCandidateElements());
		}
		List<EObject> members = getMemberElements();
		memberElementsViewer.setInput(members);
		this.memberElementsViewer.refresh();

		if (members != null) {
			// Remove the members from the candidates
			if (this.candidateElementsViewer.getInput() instanceof Collection<?>) {
				Collection<?> input = (Collection<?>) this.candidateElementsViewer
						.getInput();
				Collection<EObject> candidates = new ArrayList<EObject>();
				for (Object o : input) {
					if (o instanceof EObject && !members.contains(o)) {
						candidates.add((EObject) o);
					}
				}
				candidateElementsViewer.setInput(candidates);
			}
		}
		this.candidateElementsViewer.refresh();
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
		// executeMovement(true);
		refresh();
	}

	/**
	 * Handle down button selected.
	 */
	public void handleDownButtonSelected() {
		// executeMovement(false);
		refresh();
	}

	/**
	 * Execute add command to add the selected values to the extended feature..
	 * 
	 * @param objectsToAdd
	 *            the object to add
	 */
	private void executeAddCommand(Collection<?> objectsToAdd) {

		if (getEMFEditDomain() == null || getEModelElement() == null
				|| getExtendedFeatureUnderEditionID() == null) {
			return;
		}

		AbstractCommonTransactionalCommmand command = new AddExtendedFeatureValueCommand(
				getEMFEditDomain(), getEModelElement(),
				getExtendedFeatureUnderEditionID(), objectsToAdd);

		getEMFEditDomain().getCommandStack().execute(command.toEMFCommand());
	}

	/**
	 * Execute remove command to remove the selected values from the extended
	 * feature..
	 * 
	 * @param objectsToRemove
	 *            the object to remove
	 */
	private void executeRemoveCommand(Collection<?> objectsToRemove) {

		if (getEMFEditDomain() == null || getEModelElement() == null
				|| getExtendedFeatureUnderEditionID() == null)
			return;

		AbstractCommonTransactionalCommmand command = new RemoveExtendedFeatureValueCommand(
				getEMFEditDomain(), getEModelElement(),
				getExtendedFeatureUnderEditionID(), objectsToRemove);

		getEMFEditDomain().getCommandStack().execute(command.toEMFCommand());
	}

	/**
	 * Gets the candidate members selecting the ones of proper type from all the
	 * available EObjects.
	 * 
	 * @return
	 */
	protected List<EObject> getCandidateElements() {
		return new ArrayList<EObject>(ItemPropertyDescriptor
				.getReachableObjectsOfType(getEModelElement(), getEType()));
	}

	/**
	 * Gets the members elements from the extended feature..
	 * 
	 * @return the members
	 */
	protected List<EObject> getMemberElements() {

		if (getEModelElement() != null) {
			ExtendedFeatureElement extendedElement = ExtendedFeatureElementFactory
					.getInstance().asExtendedFeatureElement(getEModelElement());
			if (extendedElement != null) {
				return extendedElement
						.getReferenceList(getExtendedFeatureUnderEditionID());
			}
		}
		return Collections.emptyList();
	}

	/**
	 * Gets the EClassifier type for candidate members.
	 * 
	 * @return
	 */
	public EClassifier getEType() {
		return eType;
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
