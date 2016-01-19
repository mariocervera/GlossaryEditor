/***************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte,
 * Generalitat de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Mario Cervera Ubeda (Integranova)
 * 				Jose Manuel García Valladolid (CIT / Indra SL) - Maintenance support
 *
 **************************************************************************/
package es.cv.gvcase.fefem.common.composites;

import java.util.List;

import org.eclipse.core.databinding.AggregateValidationStatus;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.databinding.EMFDataBindingContext;
import org.eclipse.emf.databinding.edit.EMFEditObservables;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;

import es.cv.gvcase.fefem.common.FEFEMPage;

/**
 * An abstract implementation of a composite intended to be sub-classed to bind
 * the feature returned by the abstract method getFeature with a widget using
 * databinding.
 * 
 * @author Mario Cervera
 */
public abstract class EMFPropertyComposite extends Composite implements
		DisposeListener {

	/**
	 * The standard label width when labels for sections line up on the left
	 * hand side of the composite.
	 */
	public static final int STANDARD_LABEL_WIDTH = 40;

	protected FEFEMPage page;

	/** A EObject from where the property is taken */
	protected EObject eObject = null;

	/**
	 * A viewer from where the EObject with the property is taken
	 * 
	 * Either the eobject or viewer variable will be used but not both
	 */
	protected Viewer viewer = null;

	protected Object oldValue;

	/**
	 * The label for this section
	 */
	protected Label label;
	protected FormToolkit toolkit;

	protected IObservableValue modelObservable;

	protected EMFDataBindingContext bindingContext;

	protected Object lastModifiedObject = null;
	protected EObject lastModifiedObjectCopy = null;

	protected ISelectionChangedListener masterSelectionListener;
	protected IChangeListener changeListener;

	protected Color COLOR_ENABLED = Display.getCurrent().getSystemColor(
			SWT.COLOR_LIST_BACKGROUND);

	protected Color getColorEnabled() {
		return COLOR_ENABLED;
	}

	protected Color COLOR_DISABLED = Display.getCurrent().getSystemColor(
			SWT.COLOR_WIDGET_BACKGROUND);

	protected Color getColorDisabled() {
		return COLOR_DISABLED;
	}

	protected Color COLOR_MANDATORY = Display.getCurrent().getSystemColor(
			SWT.COLOR_RED);

	protected Color getColorMandatory() {
		return COLOR_MANDATORY;
	}

	public EMFPropertyComposite(Composite parent, int style,
			FormToolkit toolkit, EObject eObject, FEFEMPage page) {
		super(parent, style);
		this.eObject = eObject;
		this.page = page;
		this.toolkit = toolkit;

		createWidgets(toolkit);
		bindFeatureToWidget();
		// Escuchamos el evento del dispose para liberar los listeners.
		addDisposeListener(this);

		registerCompositeForEcoreValidation();
	}

	public EMFPropertyComposite(Composite parent, int style,
			FormToolkit toolkit, Viewer viewer, FEFEMPage page) {
		super(parent, style);
		this.viewer = viewer;
		this.page = page;
		this.toolkit = toolkit;

		createWidgets(toolkit);
		bindFeatureToWidget();

		registerCompositeForEcoreValidation();

		masterSelectionListener = new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				refreshWhenMasterChange();
			}
		};

		this.getMasterViewer().addSelectionChangedListener(
				masterSelectionListener);
		this.addDisposeListener(this);

		updateEnablement();
	}

	protected void refreshWhenMasterChange() {
		updateEnablement();
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		setCompositeEnablement(enabled);
	}

	/**
	 * Enable or disable the widgets of this composite
	 */
	protected void setCompositeEnablement(boolean enabled) {
		for (Control c : getWidgetsList()) {
			if (c != null) {
				c.setEnabled(enabled);
				if (enabled) {
					c.setBackground(getColorEnabled());
				} else {
					c.setBackground(getColorDisabled());
				}
			}
		}
	}

	/**
	 * The list of widgets of this composite
	 */
	protected abstract List<Control> getWidgetsList();

	protected boolean colorifyIfMandatory() {
		return false;
	}

	protected boolean isMandatory() {
		return getFeature().isRequired();
	}

	protected void registerCompositeForEcoreValidation() {

		if (enabledLiveEcoreValidation()) {
			if (page.getEditor().getEcoreValidationDecorator() != null)
				page.getEditor().getEcoreValidationDecorator()
						.registerComposite(this);
		}

	}

	protected abstract void createWidgets(FormToolkit toolkit);

	/**
	 * Get the standard label width when labels for sections line up on the left
	 * hand side of the composite. We line up to a fixed position, but if a
	 * string is wider than the fixed position, then we use that widest string.
	 * 
	 * @param parent
	 *            The parent composite used to create a GC.
	 * @param labels
	 *            The list of labels.
	 * @return the standard label width.
	 */
	protected int getStandardLabelWidth(Composite parent, String[] labels) {
		int standardLabelWidth = STANDARD_LABEL_WIDTH + labelWidthOffset();
		return standardLabelWidth;
	}

	protected int labelWidthOffset() {
		return 65;
	}

	protected Label getLabel() {
		return label;
	}

	protected void createLabel(FormToolkit toolkit) {
		label = toolkit.createLabel(this, getLabelText(), SWT.WRAP);
		if (colorifyIfMandatory() && isMandatory()) {
			label.setForeground(getColorMandatory());
		}
		GridData gd = new GridData(GridData.BEGINNING);
		gd.widthHint = getStandardLabelWidth(this,
				new String[] { getLabelText() });
		label.setLayoutData(gd);
	}

	public void setLabelText(String text) {
		if (label != null) {
			label.setText(text);
			GridData gd = new GridData(GridData.BEGINNING);
			gd.widthHint = getStandardLabelWidth(this, new String[] { text });
			label.setLayoutData(gd);
		}
	}

	public FEFEMPage getPage() {
		return page;
	}

	/**
	 * The EObject containing the feature
	 * 
	 * @return the EObject
	 */
	public EObject getEObject() {
		if (isDetailComposite()) {
			if (viewer.getSelection() != null) {
				Object selection = ((StructuredSelection) viewer.getSelection())
						.getFirstElement();
				if (selection instanceof EObject) {
					return (EObject) selection;
				}
			} else {
				return null;
			}
		} else {
			return eObject;
		}
		return null;
	}

	public TransactionalEditingDomain getEditingDomain() {
		return this.getPage().getEditor().getEditingDomain();
	}

	/**
	 * The Viewer containing the EObjects
	 * 
	 * @return the viewer
	 */
	protected Viewer getMasterViewer() {
		return viewer;
	}

	public boolean isDetailComposite() {
		return this.getMasterViewer() != null;
	}

	protected Object getFeatureValue() {
		if (getFeature() == null)
			return null;

		return getEObject() == null ? null : getEObject().eGet(getFeature());
	}

	protected void handlePropertyChanged() {
		getPage().setDirty(true);
		getPage().refresh();
	}

	/**
	 * This method is used to bind the feature to its corresponding widget in
	 * the composite by using databinding
	 */
	protected void bindFeatureToWidget() {

		if (getFeature() == null)
			return;

		bindingContext = new EMFDataBindingContext();

		IObservableValue targetObservable = getTargetObservable();

		if (!isDetailComposite()) {
			if (getEObject() == null) {
				return;
			}

			modelObservable = EMFEditObservables
					.observeValue(
							this.getPage().getEditor().getEditingDomain(),
							getEObject(), getFeature());

			changeListener = new IChangeListener() {
				public void handleChange(ChangeEvent event) {
					if (!isDisposed())
						handlePropertyChanged();
				}
			};

			modelObservable.addChangeListener(changeListener);
			if (targetObservable != null) { // A viewer has been passed through
				// the constructor
				bindingContext.bindValue(targetObservable, modelObservable,
						getTargetToModelUpdateValueStrategy(),
						getModelToTargetUpdateValueStrategy());
			}

		} else {

			IObservableValue selectionObservable = ViewersObservables
					.observeSingleSelection(getMasterViewer());

			modelObservable = EMFEditObservables.observeDetailValue(Realm
					.getDefault(), this.getPage().getEditor()
					.getEditingDomain(), selectionObservable, getFeature());
			if (targetObservable != null) { // A viewer has been passed through
				// the constructor
				bindingContext.bindValue(targetObservable, modelObservable,
						getTargetToModelUpdateValueStrategy(),
						getModelToTargetUpdateValueStrategy());
			}

			changeListener = new IChangeListener() {
				public void handleChange(ChangeEvent event) {
					if (!isDisposed()) {
						if (lastModifiedObject != null
								&& lastModifiedObject.equals(getEObject())) {
							if (!EcoreUtil.equals(lastModifiedObjectCopy,
									getEObject())) {
								handlePropertyChanged();
								if (getEObject() != null) {
									lastModifiedObjectCopy = EcoreUtil
											.copy(getEObject());
								}
							}
						} else {
							lastModifiedObject = getEObject();
							if (getEObject() != null) {
								lastModifiedObjectCopy = EcoreUtil
										.copy(getEObject());
							}
							handleSelectionChanged();
						}
					}
				}
			};
			modelObservable.addChangeListener(changeListener);
		}

		// Manage Databinding validation status
		AggregateValidationStatus avs = new AggregateValidationStatus(
				bindingContext.getBindings(),
				AggregateValidationStatus.MAX_SEVERITY);
		avs.addChangeListener(new IChangeListener() {

			public void handleChange(ChangeEvent event) {
				if (event.getSource() instanceof AggregateValidationStatus) {
					AggregateValidationStatus avs = (AggregateValidationStatus) event
							.getSource();

					IStatus s = (IStatus) avs.getValue();
					if (!s.isOK()) {
						if (s.getSeverity() == IStatus.ERROR)
							MessageDialog.openError(getShell(),
									"Form validation error", String.valueOf(s
											.getCode())
											+ " : " + s.getMessage());
						if (s.getSeverity() == IStatus.WARNING)
							MessageDialog.openWarning(getShell(),
									"Form validation warning", String.valueOf(s
											.getCode())
											+ " : " + s.getMessage());
						if (s.getSeverity() == IStatus.INFO)
							MessageDialog.openInformation(getShell(),
									"Form validation information", String
											.valueOf(s.getCode())
											+ " : " + s.getMessage());
					}

				}

			}

		});

	}

	protected void handleSelectionChanged() {
	}

	protected UpdateValueStrategy getTargetToModelUpdateValueStrategy() {
		return null;
	}

	protected UpdateValueStrategy getModelToTargetUpdateValueStrategy() {
		return null;
	}

	public boolean isEditable() {
		return true;
	}

	/**
	 * Descendants could override this method to customize custom control
	 * enablements
	 */
	protected void updateEnablement() {
		if (this.isDetailComposite() && !this.isDisposed()) {
			boolean enablement = !getMasterViewer().getSelection().isEmpty();

			this.setEnabled(enablement);
		}
	}

	protected void removeMasterViewerListener() {
		if (this.isDetailComposite()) {
			this.getMasterViewer().removeSelectionChangedListener(
					masterSelectionListener);
		}
	}

	/**
	 * Activates the registering of this composite for ecore validation This
	 * class may be overrided by subclasses
	 * 
	 * @return
	 */
	protected boolean enabledLiveEcoreValidation() {
		return true;
	}

	/**
	 * The feature the widget is binded with
	 * 
	 * @return the feature
	 */
	protected abstract EStructuralFeature getFeature();

	protected abstract IObservableValue getTargetObservable();

	protected abstract String getLabelText();

	public abstract Control getRepresentativeControl();

	/**
	 * Change the eObject: rebinding included
	 */
	public void setEObject(EObject eObject) {
		this.eObject = eObject;

		releaseData();
		bindFeatureToWidget();
	}

	/**
	 * Allows public access to the feature
	 * 
	 * @return
	 */
	public EStructuralFeature getEStructuralFeature() {
		return getFeature();
	}

	/**
	 * Allows public access to the Viewer of the master composite in
	 * master-detail binding
	 * 
	 * @return
	 */
	public Viewer getMasterCompositeViewer() {
		return getMasterViewer();
	}

	public void widgetDisposed(DisposeEvent e) {
		// recibiremos este evento cuando we widget haya sido liberado
		// y por tanto tendremos que liberar los datos
		if (e.widget == this) {
			removeMasterViewerListener();
			releaseData();
		}
	}

	/**
	 * <p>
	 * This method will be invoked when a {@link DisposeEvent} is received by
	 * the this composite. The intention of this method is to release all the
	 * data related to this composite so that the latter can be released by the
	 * garbage collector.
	 * </p>
	 * <p>
	 * Subclasses must override this method if they want properly manage the
	 * release of the data created when the composite is disposed. If
	 * overridden, remember to call <b>super.releaseData()</b> so that all the
	 * data is properly released.
	 * </p>
	 * 
	 */
	protected void releaseData() {
		if (modelObservable != null) {
			modelObservable.removeChangeListener(changeListener);
			if (!isDetailComposite()) {
				modelObservable.dispose();
				modelObservable = null;
			}
		}
	}
}
