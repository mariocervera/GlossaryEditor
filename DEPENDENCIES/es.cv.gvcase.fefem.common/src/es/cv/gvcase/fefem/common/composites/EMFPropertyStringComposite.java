/***************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte,
 * Generalitat de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Mario Cervera Ubeda (Integranova)
 * 	             Jose Manuel García Valladolid (CIT) - StyledText support
 *
 **************************************************************************/
package es.cv.gvcase.fefem.common.composites;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;

import es.cv.gvcase.fefem.common.FEFEMPage;
import es.cv.gvcase.fefem.common.providers.StyledTextSelectionProvider;

/**
 * An implementation of a composite with a text field. Invoke
 * bindFeatureToWidget to bind the text field with the feature
 * 
 * @author Mario Cervera
 */
public abstract class EMFPropertyStringComposite extends EMFPropertyComposite {

	/** The text control for the composite. */
	protected StyledText text;

	protected ISelectionProvider selectionProvider;

	/**
	 * When a EObject is passed the Text widget is binded with the feature of
	 * the EObject passed as parameter
	 */
	public EMFPropertyStringComposite(Composite parent, int style,
			FormToolkit toolkit, EObject eObject, FEFEMPage page) {
		super(parent, style, toolkit, eObject, page);

		page.getEditor().getSelectionProvider()
				.registerDelegatedSelectionProvider(
						this.getTextSelectionProvider());
	}

	/**
	 * When a Viewer is passed the Text widget is binded with the feature of the
	 * object selected in the Viewer
	 */
	public EMFPropertyStringComposite(Composite parent, int style,
			FormToolkit toolkit, Viewer viewer, FEFEMPage page) {
		super(parent, style, toolkit, viewer, page);

		page.getEditor().getSelectionProvider()
				.registerDelegatedSelectionProvider(
						this.getTextSelectionProvider());
	}

	protected StyledText getText() {
		return text;
	}

	protected ISelectionProvider getTextSelectionProvider() {
		if (selectionProvider == null) {
			selectionProvider = new StyledTextSelectionProvider(getText());
		}
		return selectionProvider;

	}

	protected void createWidgets(FormToolkit toolkit) {

		this.setLayout(new GridLayout(2, false));
		createLabel(toolkit);

		text = new StyledText(this, getStyle());
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.setEditable(isEditable());
		text.setEnabled(isEnabled());

		toolkit.adapt(this);
	}

	/**
	 * Get the style of the text widget
	 * 
	 * @return the style
	 */
	@Override
	public int getStyle() {
		return SWT.BORDER | SWT.SINGLE;
	}

	@Override
	protected IObservableValue getTargetObservable() {
		return SWTObservables.observeText(text, SWT.FocusOut);
	}

	protected void refresh() {
		this.text.redraw();
	}

	@Override
	public Control getRepresentativeControl() {
		return this.text;
	}

	protected List<Control> getWidgetsList() {
		List<Control> list = new ArrayList<Control>();
		list.add(text);
		return list;
	}

}
