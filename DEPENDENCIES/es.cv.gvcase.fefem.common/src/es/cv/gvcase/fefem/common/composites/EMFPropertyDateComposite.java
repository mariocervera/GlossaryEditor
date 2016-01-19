/***************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte,
 * Generalitat de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Jose Manuel García Valladolid (CIT/Indra SL) - Initial API and implementation
 *
 **************************************************************************/

package es.cv.gvcase.fefem.common.composites;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.widgets.datechooser.DateChooserCombo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;

import es.cv.gvcase.fefem.common.FEFEMPage;
import es.cv.gvcase.fefem.common.databinding.DateChooserComboObservableValue;

public abstract class EMFPropertyDateComposite extends EMFPropertyComposite {

	/** The datecombo control for the composite. */
	protected DateChooserCombo dateCombo;

	/**
	 * @param parent
	 * @param style
	 * @param toolkit
	 * @param object
	 * @param page
	 */
	public EMFPropertyDateComposite(Composite parent, int style,
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
	public EMFPropertyDateComposite(Composite parent, int style,
			FormToolkit toolkit, Viewer viewer, FEFEMPage page) {
		super(parent, style, toolkit, viewer, page);
	}

	@Override
	protected void createWidgets(FormToolkit toolkit) {

		this.setLayout(new GridLayout(2, false));
		createLabel(toolkit);

		dateCombo = new DateChooserCombo(this, SWT.BORDER);
		dateCombo.setLayoutData(new GridData(GridData.BEGINNING
				| GridData.FILL_HORIZONTAL));

		toolkit.adapt(this);

	}

	protected void refresh() {
		this.dateCombo.redraw();
	}

	@Override
	protected IObservableValue getTargetObservable() {
		return new DateChooserComboObservableValue(dateCombo);
	}

	/**
	 * Get the style of the text widget
	 * 
	 * @return the style
	 */
	@Override
	public int getStyle() {
		return SWT.BORDER;
	}

	@Override
	public Control getRepresentativeControl() {
		return dateCombo;
	}

	protected List<Control> getWidgetsList() {
		List<Control> list = new ArrayList<Control>();
		list.add(dateCombo);
		return list;
	}

}
