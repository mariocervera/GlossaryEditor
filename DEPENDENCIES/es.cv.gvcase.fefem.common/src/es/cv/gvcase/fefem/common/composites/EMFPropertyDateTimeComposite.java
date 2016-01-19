/***************************************************************************
 * Copyright (c) 2010 Conselleria de Infraestructuras y Transporte,
 * Generalitat de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Miguel Llacer San Fernando - (Prodevelop)
 *
 **************************************************************************/
package es.cv.gvcase.fefem.common.composites;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;

import es.cv.gvcase.fefem.common.FEFEMPage;
import es.cv.gvcase.fefem.common.databinding.CDateTimeObservableValue;

/**
 * The Class EMFPropertyDateTimeComposite.
 */
public abstract class EMFPropertyDateTimeComposite extends EMFPropertyComposite {

	/** The date time control for the composite. */
	protected CDateTime cDateTime;

	/**
	 * Instantiates a new eMF property date time composite.
	 * 
	 * @param parent
	 *            the parent
	 * @param style
	 *            the style
	 * @param toolkit
	 *            the toolkit
	 * @param eObject
	 *            the e object
	 * @param page
	 *            the page
	 */
	public EMFPropertyDateTimeComposite(Composite parent, int style,
			FormToolkit toolkit, EObject eObject, FEFEMPage page) {
		super(parent, style, toolkit, eObject, page);
	}

	/**
	 * Instantiates a new eMF property date time composite.
	 * 
	 * @param parent
	 *            the parent
	 * @param style
	 *            the style
	 * @param toolkit
	 *            the toolkit
	 * @param viewer
	 *            the viewer
	 * @param page
	 *            the page
	 */
	public EMFPropertyDateTimeComposite(Composite parent, int style,
			FormToolkit toolkit, Viewer viewer, FEFEMPage page) {
		super(parent, style, toolkit, viewer, page);
	}

	@Override
	protected void createWidgets(FormToolkit toolkit) {

		this.setLayout(new GridLayout(2, false));
		createLabel(toolkit);

		cDateTime = new CDateTime(this, getStyle());
		cDateTime.setLayoutData(new GridData(GridData.BEGINNING
				| GridData.FILL_HORIZONTAL));

		toolkit.adapt(this);

		setLocale();
		setFormat();

	}

	@Override
	public Control getRepresentativeControl() {
		return cDateTime;
	}

	@Override
	protected IObservableValue getTargetObservable() {
		return new CDateTimeObservableValue(cDateTime);
	}

	@Override
	public int getStyle() {
		return CDT.BORDER | CDT.DROP_DOWN | CDT.COMPACT | CDT.SPINNER
				| CDT.CLOCK_24_HOUR;
	}

	protected void refresh() {
		this.cDateTime.redraw();
	}

	/**
	 * Sets the spanish language as default.
	 * 
	 * @param cDateTime
	 * 
	 */
	protected void setLocale() {
		for (int i = 0; i < Locale.getAvailableLocales().length; i++) {
			if (Locale.getAvailableLocales()[i].getCountry().equals("ES")
					&& Locale.getAvailableLocales()[i].getLanguage().equals(
							"es")) {
				// set spanish language
				cDateTime.setLocale(Locale.getAvailableLocales()[i]);
				break;
			}
		}
	}

	/**
	 * Sets the format.
	 * 
	 * @param cDateTime
	 * 
	 */
	protected void setFormat() {
		cDateTime.setFormat(CDT.DATE_SHORT | CDT.TIME_SHORT);
	}

	protected List<Control> getWidgetsList() {
		List<Control> list = new ArrayList<Control>();
		list.add(cDateTime);
		return list;
	}

}
