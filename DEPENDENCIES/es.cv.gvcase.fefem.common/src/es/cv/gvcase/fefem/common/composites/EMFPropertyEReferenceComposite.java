/***************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte,
 * Generalitat de la Comunitat Valenciana, Open Canarias, S.L . All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Mario Cervera Ubeda (Integranova)
 * 		Adolfo Sanchez-Barbudo Herrera (Open Canarias, S.L.).
 *
 **************************************************************************/
package es.cv.gvcase.fefem.common.composites;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.emf.databinding.EMFUpdateValueStrategy;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;

import es.cv.gvcase.emf.ui.common.composites.EObjectChooserComposite;
import es.cv.gvcase.emf.ui.common.utils.PackagingNode;
import es.cv.gvcase.fefem.common.FEFEMPage;
import es.cv.gvcase.fefem.common.providers.EObjectLabelProvider;

/**
 * An implementation of a composite with a chooser. Invoke bindFeatureToWidget
 * to bind the chooser with the feature
 * 
 * @author Mario Cervera
 */
public abstract class EMFPropertyEReferenceComposite extends
		EMFPropertyComposite {

	/**
	 * The combo box control for the section.
	 */
	protected EObjectChooserComposite chooser;

	/**
	 * The label provider to show info about the control
	 */
	protected ILabelProvider labelProvider;

	public EMFPropertyEReferenceComposite(Composite parent, int style,
			FormToolkit toolkit, EObject eObject, FEFEMPage page) {
		super(parent, style, toolkit, eObject, page);

		hookListeners();
	}

	public EMFPropertyEReferenceComposite(Composite parent, int style,
			FormToolkit toolkit, Viewer viewer, FEFEMPage page) {
		super(parent, style, toolkit, viewer, page);

		hookListeners();
	}

	protected EObjectChooserComposite getChooser() {
		if (chooser == null)
			chooser = createChooser();
		return chooser;
	}

	protected EObjectChooserComposite createChooser() {
		return new EObjectChooserComposite(this, getPage().getEditor()
				.getToolkit(), true) {

			@Override
			protected void fillObjects() {

				setChoices(getChoices());
			}

			@Override
			protected boolean checkSelectionValid(Object o) {
				return super.checkSelectionValid(o)
						&& EMFPropertyEReferenceComposite.this
								.checkSelectionValid(o);
			}
		};
	}

	protected boolean checkSelectionValid(Object o) {
		if ((o instanceof PackagingNode) || (o instanceof Resource))
			return false;

		return true;
	}

	public void setLabelProvider(ILabelProvider lp) {
		getChooser().setLabelProvider(lp);
	}

	public void setChoices(Object[] choices) {
		getChooser().setChoices(choices);
	}

	protected void createWidgets(FormToolkit toolkit) {

		this.setLayout(new GridLayout(2, false));

		createLabel(toolkit);

		EObjectChooserComposite chooser = getChooser();
		chooser.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		chooser.setLabelProvider(getLabelProvider());
		chooser.setContentProvider(getChooserContentProvider());

		setChoices(getChoices());

		if (getFeature() != null) {
			chooser.setChangeable(getFeature().isChangeable()
					&& this.isEditable());
		}

		toolkit.adapt(this);
	}

	protected ITreeContentProvider getChooserContentProvider() {
		return new ITreeContentProvider() {

			public Object[] getElements(Object inputElement) {
				Object[] choices = getChoices();
				for (int i = 0; i < choices.length; i++) {
					if (choices[i] == null) {
						choices[i] = getUndefinedValue();
					}
				}
				return choices;
			}

			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
			}

			public boolean hasChildren(Object element) {
				return false;
			}

			public Object getParent(Object element) {
				return null;
			}

			public Object[] getChildren(Object parentElement) {
				return new Object[] {};
			}
		};
	}

	protected void hookListeners() {
		getChooser().addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleComboModified();
			}
		});
	}

	/**
	 * Handle the combo modified event.
	 */
	protected void handleComboModified() {
		// FIXME if no editor exploits this method and hookListeners, it should
		// be removed
		// Do nothing.
	}

	protected EMFUpdateValueStrategy modelToTargetStrategy;
	protected EMFUpdateValueStrategy targetToModelStrategy;

	@Override
	protected UpdateValueStrategy getModelToTargetUpdateValueStrategy() {
		if (modelToTargetStrategy == null) {
			modelToTargetStrategy = new EMFUpdateValueStrategy();
			modelToTargetStrategy.setConverter(createModelToTargetConverter());
		}
		return modelToTargetStrategy;
	}

	@Override
	protected UpdateValueStrategy getTargetToModelUpdateValueStrategy() {
		if (targetToModelStrategy == null) {
			targetToModelStrategy = new EMFUpdateValueStrategy();
			targetToModelStrategy.setConverter(createTargetToModelConverter());
		}
		return targetToModelStrategy;
	}

	protected IConverter createModelToTargetConverter() {
		return new Converter(EObject.class, String.class) {

			public Object convert(Object fromObject) {
				if (fromObject instanceof EObject) {
					return getLabelProvider().getText(fromObject);
				}
				return getUndefinedValue();
			}
		};
	}

	protected IConverter createTargetToModelConverter() {

		return new Converter(String.class, EObject.class) {

			public Object convert(Object fromObject) {

				if (getUndefinedValue().equals(fromObject))
					return null;
				else
					return getChooser().getSelection();
			}
		};
	}

	/**
	 * The text showed in the text box when there is no value in the reference
	 */
	protected String getUndefinedValue() {
		return "";
	}

	@Override
	protected IObservableValue getTargetObservable() {
		return SWTObservables.observeText(getChooser().getText(), SWT.Modify);
	}

	protected ILabelProvider getLabelProvider() {
		if (labelProvider == null)
			labelProvider = createLabelProvider();
		return labelProvider;
	}

	protected ILabelProvider createLabelProvider() {
		return new EObjectLabelProvider();
	}

	@Override
	public void dispose() {
		if (labelProvider != null)
			labelProvider.dispose();
		super.dispose();
	}

	protected abstract Object[] getChoices();

	@Override
	public Control getRepresentativeControl() {
		return chooser;
	}

	protected List<Control> getWidgetsList() {
		List<Control> list = new ArrayList<Control>();
		list.add(chooser.getText());
		list.add(chooser.getChooseButton());
		if (chooser.hasNavigationButton()) {
			list.add(chooser.getNavigationButton());
		}
		return list;
	}
}