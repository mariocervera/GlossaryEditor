/*******************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Jose Manuel García Valladolid (Indra SL- CIT) – Initial implementation
 *
 ******************************************************************************/

package es.cv.gvcase.fefem.common.composites;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.emf.databinding.EMFUpdateValueStrategy;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;

import es.cv.gvcase.fefem.common.FEFEMPage;
import es.cv.gvcase.fefem.common.providers.EObjectLabelProvider;

/**
 * Readonly composite for displaying feature label
 * 
 * @author Jose Manuel García Valladolid (garcia_josval@gva.es)
 * 
 */
public abstract class EMFPropertyLabelComposite extends EMFPropertyComposite {

	/** The text control for the composite. */
	protected Label label;

	protected EMFUpdateValueStrategy modelToTargetStrategy;

	/**
	 * The label provider to show info about the control
	 */
	protected ILabelProvider labelProvider;

	/**
	 * @param parent
	 * @param style
	 * @param toolkit
	 * @param eObject
	 * @param page
	 */
	public EMFPropertyLabelComposite(Composite parent, int style,
			FormToolkit toolkit, EObject eObject, FEFEMPage page) {
		super(parent, style, toolkit, eObject, page);
	}

	/**
	 * @param parent
	 * @param style
	 * @param toolkit
	 * @param viewer
	 * @param page
	 */
	public EMFPropertyLabelComposite(Composite parent, int style,
			FormToolkit toolkit, Viewer viewer, FEFEMPage page) {
		super(parent, style, toolkit, viewer, page);
	}

	@Override
	protected void createWidgets(FormToolkit toolkit) {

		this.setLayout(new GridLayout(2, false));
		createLabel(toolkit);

		label = new Label(this, SWT.LEFT);
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		toolkit.adapt(this);

	}

	@Override
	public Control getRepresentativeControl() {
		return label;
	}

	@Override
	protected IObservableValue getTargetObservable() {
		return SWTObservables.observeText(label);
	}

	@Override
	protected UpdateValueStrategy getModelToTargetUpdateValueStrategy() {
		if (modelToTargetStrategy == null) {
			modelToTargetStrategy = new EMFUpdateValueStrategy();
			modelToTargetStrategy.setConverter(createModelToTargetConverter());
		}
		return modelToTargetStrategy;
	}

	/**
	 * The text showed in the text box when there is no value in the reference
	 */
	protected String getUndefinedValue() {
		return "";
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

	protected List<Control> getWidgetsList() {
		List<Control> list = new ArrayList<Control>();
		list.add(label);
		return list;
	}
}
