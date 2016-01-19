/***************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte,
 * Generalitat de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Jose Manuel García Valladolid (CIT) - Initial API and implementation
 *
 **************************************************************************/

package es.cv.gvcase.fefem.common.composites;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.forms.widgets.FormToolkit;

import es.cv.gvcase.fefem.common.FEFEMPage;

/**
 * Composite that binds a ecore Number feature (tipicaly an EAttribute) to a
 * Spinner control.
 * 
 * @author Jose Manuel García Valladolid
 */
public abstract class EMFPropertyIntegerComposite extends EMFPropertyComposite {

	public static int DEFAULT_MAX_VALUE = 10000;
	public static int DEFAULT_MIN_VALUE = -1;
	public static int DEFAULT_INC_VALUE = 1;

	protected Spinner spinner;

	/**
	 * @param parent
	 * @param style
	 * @param toolkit
	 * @param object
	 * @param page
	 */
	public EMFPropertyIntegerComposite(Composite parent, int style,
			FormToolkit toolkit, EObject object, FEFEMPage page) {
		super(parent, style, toolkit, object, page);
	}

	/**
	 * @param parent
	 * @param style
	 * @param toolkit
	 * @param viewer
	 * @param page
	 */
	public EMFPropertyIntegerComposite(Composite parent, int style,
			FormToolkit toolkit, Viewer viewer, FEFEMPage page) {
		super(parent, style, toolkit, viewer, page);
	}

	@Override
	protected void createWidgets(FormToolkit toolkit) {
		this.setLayout(new GridLayout(2, false));
		createLabel(toolkit);

		spinner = new Spinner(this, getStyle() | SWT.BORDER);
		spinner.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL));
		spinner.setMaximum(getMaxValue());
		spinner.setMinimum(getMinValue());
		spinner.setIncrement(getIncrementValue());
		toolkit.adapt(this);

	}

	@Override
	protected IObservableValue getTargetObservable() {
		return SWTObservables.observeSelection(spinner);
	}

	public Spinner getSpinner() {
		return spinner;
	}

	/**
	 * Maximun value for the spinner
	 * 
	 * @return
	 */
	protected int getMaxValue() {
		return DEFAULT_MAX_VALUE;
	}

	/**
	 * Minimun value for the spinner
	 * 
	 * @return
	 */
	protected int getMinValue() {
		return DEFAULT_MIN_VALUE;
	}

	/**
	 * Increment step value for the spinner
	 * 
	 * @return
	 */
	protected int getIncrementValue() {
		return DEFAULT_INC_VALUE;
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		spinner.setEnabled(enabled);
	}

	@Override
	public Control getRepresentativeControl() {
		return spinner;
	}

	protected List<Control> getWidgetsList() {
		List<Control> list = new ArrayList<Control>();
		list.add(spinner);
		return list;
	}

}
