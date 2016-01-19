/***************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte,
 * Generalitat de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Mario Cervera Ubeda (Prodevelop)
 * 				Jose Manuel García Valladolid (CIT) - Maintenance support
 *
 **************************************************************************/
package es.cv.gvcase.fefem.common.composites;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;

import es.cv.gvcase.fefem.common.FEFEMPage;
import es.cv.gvcase.fefem.common.databinding.StyledTextObservableValue;
import es.cv.gvcase.fefem.common.widgets.StyledTextViewer;

public abstract class EMFPropertyStringStyledTextComposite extends
		EMFPropertyComposite {
	protected StyledTextViewer styledTextViewer;

	public EMFPropertyStringStyledTextComposite(Composite parent, int style,
			FormToolkit toolkit, EObject eObject, FEFEMPage page) {
		super(parent, style, toolkit, eObject, page);

		page.getEditor().getSelectionProvider()
				.registerDelegatedSelectionProvider(styledTextViewer);
	}

	public EMFPropertyStringStyledTextComposite(Composite parent, int style,
			FormToolkit toolkit, Viewer viewer, FEFEMPage page) {
		super(parent, style, toolkit, viewer, page);

		page.getEditor().getSelectionProvider()
				.registerDelegatedSelectionProvider(styledTextViewer);
	}

	public StyledTextViewer getStyledTextViewer() {
		return styledTextViewer;
	}

	protected boolean showLabel() {
		return true;
	}

	@Override
	protected void createWidgets(FormToolkit toolkit) {

		if (showLabel()) {
			this.setLayout(new GridLayout(2, false));
			createLabel(toolkit);

			GridData gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
			gd.widthHint = getStandardLabelWidth(this,
					new String[] { getLabelText() });
			getLabel().setLayoutData(gd);
		} else {
			this.setLayout(new GridLayout(1, false));
		}

		styledTextViewer = new StyledTextViewer(this, getStyle());
		GridData gd2 = new GridData(GridData.FILL_HORIZONTAL);
		gd2.heightHint = 200;
		styledTextViewer.getControl().setLayoutData(gd2);

	}

	@Override
	protected IObservableValue getTargetObservable() {
		return new StyledTextObservableValue(styledTextViewer);
	}

	@Override
	public int getStyle() {
		return SWT.NONE;
	}

	@Override
	public Control getRepresentativeControl() {
		return styledTextViewer.getControl();
	}

	@Override
	public void dispose() {
		if ((styledTextViewer != null)
				&& (!styledTextViewer.getControl().isDisposed())) {
			styledTextViewer.getControl().dispose();
		}
		super.dispose();
	}

	protected List<Control> getWidgetsList() {
		List<Control> list = new ArrayList<Control>();
		list.add(styledTextViewer.getControl());
		return list;
	}

}
