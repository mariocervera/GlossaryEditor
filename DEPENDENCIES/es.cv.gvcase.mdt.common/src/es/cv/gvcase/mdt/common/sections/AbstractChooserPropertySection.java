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

package es.cv.gvcase.mdt.common.sections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecoretools.tabbedproperties.sections.AbstractTabbedPropertySection;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.transaction.ResourceSetChangeEvent;
import org.eclipse.emf.transaction.ResourceSetListener;
import org.eclipse.emf.transaction.ResourceSetListenerImpl;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import es.cv.gvcase.emf.ui.common.composites.EObjectChooserComposite;
import es.cv.gvcase.emf.ui.common.composites.EObjectChooserComposite.INavigationHandler;
import es.cv.gvcase.mdt.common.sections.controls.NavigateToElementDiagramHandler;
import es.cv.gvcase.mdt.common.sections.controls.NavigateToElementDiagramHandler.IElementToShowProvider;
import es.cv.gvcase.mdt.common.sections.descriptions.ControlDescriptionDecoratorProvider;
import es.cv.gvcase.mdt.common.util.MDTUtil;

/**
 * An abstract implementation of a section with a field using a
 * CSingleObjectChooser composite (CCombo with a Button).
 * 
 * Creation 5 apr. 2006 Updated 7 aug. 2006
 * 
 * @author Jacques LESCOT
 * @author Alfredo SERRANO
 */
public abstract class AbstractChooserPropertySection extends
		AbstractTabbedPropertySection {

	/**
	 * A boolean that store if refreshing is happening and no model
	 * modifications should be performed
	 */
	private boolean isRefreshing = false;

	/**
	 * The combo box control for the section.
	 */
	private EObjectChooserComposite cSingleObjectChooser;

	/**
	 * The label for this section.
	 */
	private CLabel labelCombo;

	/**
	 * Flag to set the selection tree as single or multi selection.
	 */
	private boolean singleSelection = true;

	@Override
	protected EditingDomain getEditingDomain() {
		try {
			return super.getEditingDomain();
		} catch (IllegalArgumentException ex) {
			return null;
		}
	}

	/**
	 * Indicates whether the selection tree must be single selection.
	 * 
	 * @return
	 */
	protected boolean isSingleSelection() {
		return singleSelection;
	}

	public AbstractChooserPropertySection() {
		this(true);
	}

	public AbstractChooserPropertySection(boolean singleSelection) {
		this.singleSelection = singleSelection;
	}

	protected ITreeContentProvider getFlatContentProvider() {
		return null;
	}

	protected ITreeContentProvider getGroupedContentProvider() {
		return null;
	}

	protected ITreeContentProvider getResourceContentProvider() {
		return null;
	}

	/**
	 * Section composite. This composite can be return if client desire to
	 * implement other widgets in relation with the list represented by this
	 * instance.
	 */
	// private Composite sectionComposite;
	/**
	 * @see org.eclipse.ui.views.properties.tabbed.ISection#createControls(org.eclipse.swt.widgets.Composite,
	 *      org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage)
	 */
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
	}

	protected ResourceSetListener referencedElementChangedListener = null;

	protected ResourceSetListener createResourceSetListener() {
		if (referencedElementChangedListener == null) {
			referencedElementChangedListener = new ResourceSetListenerImpl() {
				@Override
				public boolean isPostcommitOnly() {
					return true;
				}

				@Override
				public void resourceSetChanged(ResourceSetChangeEvent event) {
					super.resourceSetChanged(event);
					//
					handleSelectedEObjectChanged(event);
				}
			};
		}
		return referencedElementChangedListener;
	}

	protected void handleSelectedEObjectChanged(ResourceSetChangeEvent event) {
		EObject eObject = getEObject();
		EStructuralFeature feature = getFeature();
		if (event != null && eObject != null && feature != null) {
			for (Notification notification : event.getNotifications()) {
				if (notification != null) {
					if (eObject.equals(notification.getNotifier())) {
						if (feature.equals(notification.getFeature())) {
							refresh();
						}
					}
				}
			}
		}
	}

	protected TransactionalEditingDomain getTransactionalEditingDomain() {
		EditingDomain domain = getEditingDomain();
		if (domain instanceof TransactionalEditingDomain) {
			return (TransactionalEditingDomain) domain;
		}
		return null;
	}

	/**
	 * @see org.eclipse.emf.ecoretools.tabbedproperties.sections.AbstractTabbedPropertySection#createWidgets(org.eclipse.swt.widgets.Composite)
	 */
	protected void createWidgets(Composite composite) {
		// label
		labelCombo = getWidgetFactory().createCLabel(composite, getLabelText());
		labelCombo.setEnabled(isEnabled());
		// object chooser
		boolean hasNavigatorToElement = hasNavigatorToElement();
		cSingleObjectChooser = new EObjectChooserComposite(composite,
				getWidgetFactory(), isSingleSelection(), hasNavigatorToElement,
				getFlatContentProvider(), getGroupedContentProvider(),
				getResourceContentProvider()) {
			@Override
			protected void fillObjects() {
				setChoices(getComboFeatureValues());
			}

			protected boolean checkSelectionValid(Object o) {
				return super.checkSelectionValid(o)
						&& AbstractChooserPropertySection.this
								.checkSelectionValid(o);
			}

			@Override
			protected boolean proceedWithHandleChoose() {
				return super.proceedWithHandleChoose()
						&& AbstractChooserPropertySection.this
								.proceedWithHandleChoose();
			}

			@Override
			protected List<?> getAllowedTypesInThePath() {
				return AbstractChooserPropertySection.this
						.getAllowedTypesInThePath();
			}

			@Override
			protected List<?> getInitialSelection() {
				List<?> list = AbstractChooserPropertySection.this
						.getInitialSelection();
				if (!list.isEmpty()) {
					return list;
				} else {
					return super.getInitialSelection();
				}
			}

			@Override
			protected boolean selectAfterRefreshTree() {
				return AbstractChooserPropertySection.this
						.selectAfterRefreshTree();
			}
		};
		cSingleObjectChooser.setLabelProvider(getLabelProvider());
		cSingleObjectChooser.setEnabled(isEnabled());

		if (getFeature() != null) {
			cSingleObjectChooser.setChangeable(getFeature().isChangeable());
		}
		if (hasNavigatorToElement) {
			cSingleObjectChooser.setNavigationHandler(getNavigationHandler());
		}

		// add control decorators that show the description of features
		ControlDescriptionDecoratorProvider.addDescriptionDecoration(
				cSingleObjectChooser, getFeature());
	}

	protected boolean selectAfterRefreshTree() {
		return true;
	}

	protected boolean checkSelectionValid(Object o) {
		return true;
	}

	protected boolean proceedWithHandleChoose() {
		return true;
	}

	protected List<?> getAllowedTypesInThePath() {
		return Collections.singletonList(Object.class);
	}

	protected List<?> getInitialSelection() {
		return Collections.emptyList();
	}

	@Override
	public void aboutToBeShown() {
		super.aboutToBeShown();
		//
		TransactionalEditingDomain domain = getTransactionalEditingDomain();
		if (domain != null) {
			domain.addResourceSetListener(createResourceSetListener());
		}
	}

	@Override
	public void aboutToBeHidden() {
		super.aboutToBeHidden();
		//
		TransactionalEditingDomain domain = getTransactionalEditingDomain();
		if (domain != null) {
			domain.removeResourceSetListener(referencedElementChangedListener);
		}
	}

	/**
	 * @see org.eclipse.emf.ecoretools.tabbedproperties.sections.AbstractTabbedPropertySection#setSectionData(org.eclipse.swt.widgets.Composite)
	 */
	protected void setSectionData(Composite composite) {
		FormData data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(cSingleObjectChooser,
				-ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
		labelCombo.setLayoutData(data);

		data = new FormData();
		data.left = new FormAttachment(0, getStandardLabelWidth(composite,
				new String[] { getLabelText() }));
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(labelCombo, 0, SWT.CENTER);
		cSingleObjectChooser.setLayoutData(data);

	}

	/**
	 * Adds the listeners on the widgets
	 */
	protected void hookListeners() {
		cSingleObjectChooser.addSelectionListener(new SelectionAdapter() {

			/**
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			public void widgetSelected(SelectionEvent e) {
				handleComboModified();
			}
		});
	}

	/**
	 * Handle the combo modified event.
	 */
	protected void handleComboModified() {

		if (!isRefreshing
				&& getFeatureValue() != cSingleObjectChooser.getSelection()) {
			EditingDomain editingDomain = getEditingDomain();
			EStructuralFeature feature = getFeature();
			if (feature.isMany()) {
				// a feature that is multivalued is handled in a different way.
				List value = Collections.singletonList(cSingleObjectChooser
						.getSelection());
				if (!value.isEmpty()) {
					editingDomain.getCommandStack().execute(
							SetCommand.create(editingDomain, getEObject(),
									feature, value));
				} else {
					editingDomain.getCommandStack().execute(
							SetCommand.create(editingDomain, getEObject(),
									feature, SetCommand.UNSET_VALUE));
				}
			} else {

				if (getEObjectList().size() == 1) {
					/* apply the property change to single selected object */
					editingDomain.getCommandStack().execute(
							SetCommand.create(editingDomain, getEObject(),
									getFeature(), cSingleObjectChooser
											.getSelection()));
				} else {
					CompoundCommand compoundCommand = new CompoundCommand();
					/* apply the property change to all selected elements */
					for (EObject nextObject : getEObjectList()) {
						compoundCommand.append(SetCommand.create(editingDomain,
								nextObject, getFeature(), cSingleObjectChooser
										.getSelection()));
					}
					editingDomain.getCommandStack().execute(compoundCommand);
				}
			}
		}
	}

	/**
	 * @see org.eclipse.ui.views.properties.tabbed.ISection#refresh()
	 */
	public void refresh() {
		isRefreshing = true;
		cSingleObjectChooser.setChoices(null);
		cSingleObjectChooser.setSelection(getFeatureValue());
		isRefreshing = false;

		labelCombo.setEnabled(isEnabled());
		cSingleObjectChooser.setEnabled(isEnabled());
	}

	/**
	 * @return the cSingleObjectChooser
	 */
	protected EObjectChooserComposite getCSingleObjectChooser() {
		return cSingleObjectChooser;
	}

	/**
	 * @return the isRefreshing
	 */
	protected boolean isRefreshing() {
		return isRefreshing;
	}

	protected void setRefreshing(boolean refreshing) {
		isRefreshing = refreshing;
	}

	/**
	 * Returns an array of all reachable objects of a given type from the
	 * current selection.
	 * 
	 * @param object
	 *            current EObject selection
	 * @param type
	 *            Reachable object which have this type
	 * @return An array of objects of the given type
	 */
	protected Object[] getChoices(EObject object, EClassifier type) {
		List<Object> choices = new ArrayList<Object>();
		choices.add(null);
		choices.addAll(ItemPropertyDescriptor.getReachableObjectsOfType(
				getEObject(), type));
		removeHideFromContentProviderElements(choices);
		return choices.toArray();
	}

	/**
	 * Remove those elements marked as elements that must be hidden from content
	 * providers from the available choices.
	 * 
	 * @param choices
	 */
	protected void removeHideFromContentProviderElements(
			Collection<Object> choices) {
		if (choices == null || choices.isEmpty()) {
			return;
		}
		Collection<EObject> elementsToRemove = new ArrayList<EObject>();
		// search all elements that have the eannotation marking them as not
		// showable.
		for (Object eObject : choices) {
			if (eObject instanceof EModelElement) {
				EModelElement element = (EModelElement) eObject;
				if (element
						.getEAnnotation(MDTUtil.HideFromContentProvidersEAnnotationSource) != null) {
					elementsToRemove.add(element);
				}
			}
		}
		// remove all the not showable elements
		choices.removeAll(elementsToRemove);
	}

	/**
	 * Returns the label text for the given item
	 * 
	 * @param object
	 *            the item to find the name
	 * @return The found name of the given item
	 */
	protected String getItemLabelText(EObject object) {
		return object.toString();
	}

	/**
	 * Get the LabelProvider to use to display the Object
	 * 
	 * @return ILabelProvider
	 */
	protected ILabelProvider getLabelProvider() {
		return MDTUtil.getLabelProvider();
	}

	protected CLabel getLabel() {
		return labelCombo;
	}

	public boolean isEnabled() {
		return true;
	}

	protected boolean hasNavigatorToElement() {
		return false;
	}

	/**
	 * Provides the navigation handler that opens the target diagram or model to
	 * show.
	 * 
	 * @return
	 */
	protected INavigationHandler getNavigationHandler() {
		// we will use the general navigation handler from MDT-common
		return new NavigateToElementDiagramHandler(
				new IElementToShowProvider() {
					public EObject getElementToShow() {
						if (getFeatureValue() instanceof EObject) {
							return (EObject) getFeatureValue();
						}
						return null;
					}
				});
	}

	/**
	 * Get the current feature value of the selected model object.
	 * 
	 * @return the feature value to select in the ccombo.
	 */
	protected abstract Object getFeatureValue();

	/**
	 * Get the enumeration values of the feature for the combo field for the
	 * section.
	 * 
	 * @return the list of values of the feature as text.
	 */
	protected abstract Object[] getComboFeatureValues();
}