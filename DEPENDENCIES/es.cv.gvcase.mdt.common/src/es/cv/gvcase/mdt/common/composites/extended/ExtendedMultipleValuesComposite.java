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

import java.util.Collection;
import java.util.Collections;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
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
import es.cv.gvcase.mdt.common.model.Feature;
import es.cv.gvcase.mdt.common.util.MDTUtil;

/**
 * Allows the edition of a multivalued extended feature. <br>
 * Allows the addition or removal of String, Integer, Double, Boolean values.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public class ExtendedMultipleValuesComposite extends ExtendedDetailsComposite {

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

	/** The composite buttons. */
	private Composite compositeButtons;

	/** The composite list members. */
	private Composite compositeMembersElements;

	/** List widget with the members of the collection. */
	private StructuredViewer memberElementsViewer = null;

	/** Add / Remove buttons. */
	private Button buttonAdd = null;

	/** The button remove. */
	private Button buttonRemove = null;

	private TabbedPropertySheetWidgetFactory factory;

	private SelectionListener removeSelectionListener = null;

	private SelectionListener addSelectionListener = null;

	/**
	 * Gets the extended feature's id being edited.
	 */
	public String getExtendedFeatureUnderEditionID() {
		return extendedFeatureUnderEditionID;
	}

	protected Feature getExtendedFeature() {
		return ExtendedFeatureElementFactory.getInstance()
				.getMapFeatureIDToFeature().get(
						getExtendedFeatureUnderEditionID());
	}

	public TabbedPropertySheetWidgetFactory getFactory() {
		return factory;
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
	 * Instantiates a new members composite.
	 * 
	 * @param parent
	 *            the parent
	 * @param style
	 *            the style
	 * @param labelText
	 *            the compositeNameLabel text
	 */
	public ExtendedMultipleValuesComposite(Composite parent, int style,
			TabbedPropertySheetWidgetFactory widgetFactory,
			String extendedFeatureID, String labelText) {
		super(parent, style);

		extendedFeatureUnderEditionID = extendedFeatureID;
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

		this.setLayout(new GridLayout(2, false));

		// if (!compositeNameLabelString.equals("")) {
		// compositeNameLabel = widgetFactory.createLabel(this,
		// compositeNameLabelString);
		// }

		compositeMembersElements = widgetFactory.createComposite(this);
		compositeMembersElements.setLayout(new GridLayout());

		compositeButtons = widgetFactory.createComposite(this);
		compositeButtons.setLayout(new GridLayout());

		memberElementsViewer = getNewViewer(compositeMembersElements);
		memberElementsViewer.setLabelProvider(getLabelProvider());
		memberElementsViewer.setContentProvider(new ArrayContentProvider());

		buttonAdd = widgetFactory.createButton(compositeButtons, "  "
				+ Messages.getString("AddLabel") + "  ", SWT.PUSH);

		buttonRemove = widgetFactory.createButton(compositeButtons, "  "
				+ Messages.getString("RemoveLabel") + "  ", SWT.PUSH);

		buttonAdd.addSelectionListener(getAddButtonSelectionListener());
		buttonRemove.addSelectionListener(getRemoveButtonSelectionListener());

		widgetFactory.adapt(this);
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
		if (!compositeNameLabelString.equals("") && compositeNameLabel != null) {
			gdata = new GridData(GridData.FILL_HORIZONTAL);
			gdata.horizontalSpan = 3;
			compositeNameLabel.setLayoutData(gdata);
		}
		gdata = new GridData(GridData.FILL_BOTH);
		gdata.heightHint = 200;
		if (this.getParent() != null) {
			gdata.widthHint = (this.getParent().getBounds().width * 40) / 100;
		}
		compositeMembersElements.setLayoutData(gdata);
		gdata = new GridData();
		compositeButtons.setLayoutData(gdata);
		gdata = new GridData(GridData.FILL_BOTH);
		getMemberElementsViewer().getControl().setLayoutData(gdata);
		getButtonAdd().setLayoutData(gdata);
		getButtonRemove().setLayoutData(gdata);
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
		Object members = getMemberElements();
		memberElementsViewer.setInput(members);
		memberElementsViewer.refresh();
	}

	/**
	 * Gets the compositeNameLabel provider.
	 * 
	 * @return the compositeNameLabel provider
	 */
	public IBaseLabelProvider getLabelProvider() {

		return labelProvider;
	}

	protected boolean checkInputString(String string) {
		Feature feature = getExtendedFeature();
		if (feature.valueOf(string) != null) {
			return true;
		}
		return false;
	}

	/**
	 * Handle add button selected.
	 */
	public void handleAddButtonSelected() {
		// TODO
		// open InputDialog
		// if OK
		// retrieve typed value
		// parse value according to type.
		// else
		// do nothing
		InputDialog dialog = new InputDialog(getShell(), "Enter value",
				"Enter value to add", "", new IInputValidator() {
					public String isValid(String newText) {
						if (checkInputString(newText)) {
							return null;
						}
						return "No valid value";
					}
				});
		if (dialog.open() == Dialog.OK) {
			// dialog returned OK
			String valueString = dialog.getValue();
			if (valueString != null && valueString.length() > 0) {
				Object value = getExtendedFeature().valueOf(valueString);
				// we have a valid value
				if (value != null) {
					AddExtendedFeatureValueCommand command = new AddExtendedFeatureValueCommand(
							getEMFEditDomain(), getEModelElement(),
							getExtendedFeatureUnderEditionID(), value);
					// this command adds the value to the extended feature.
					command.executeInTransaction();
				}
			}
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

	// /**
	// * Execute add command to add the selected values to the extended
	// feature..
	// *
	// * @param objectsToAdd
	// * the object to add
	// */
	// private void executeAddCommand(Collection<?> objectsToAdd) {
	//
	// if (getEMFEditDomain() == null || getEModelElement() == null
	// || getExtendedFeatureUnderEditionID() == null) {
	// return;
	// }
	//
	// AbstractCommonTransactionalCommmand command = new
	// AddExtendedFeatureValueCommand(
	// getEMFEditDomain(), getEModelElement(),
	// getExtendedFeatureUnderEditionID(), objectsToAdd);
	//
	// getEMFEditDomain().getCommandStack().execute(command.toEMFCommand());
	// }

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
	 * Gets the members elements from the extended feature..
	 * 
	 * @return the members
	 */
	protected Object getMemberElements() {

		if (getEModelElement() != null) {
			ExtendedFeatureElement extendedElement = ExtendedFeatureElementFactory
					.getInstance().asExtendedFeatureElement(getEModelElement());
			if (extendedElement != null) {
				return extendedElement
						.getValue(getExtendedFeatureUnderEditionID());
			}
		}
		return Collections.emptyList();
	}

	/**
	 * Creates a new Viewer
	 * 
	 * @param parent
	 * @param lp
	 * @return
	 */
	protected StructuredViewer getNewViewer(Composite parent) {
		return new TableViewer(parent);
	}

	public void updateSelectedElements(Collection<?> elements) {
		if (elements != null) {
			memberElementsViewer.setInput(elements);
		}
	}

	public void setLabelProvider(IBaseLabelProvider labelProvider) {
		this.labelProvider = labelProvider;

	}

}
