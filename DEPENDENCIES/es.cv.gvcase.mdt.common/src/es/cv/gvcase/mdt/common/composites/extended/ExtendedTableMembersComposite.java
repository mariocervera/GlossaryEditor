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

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

/**
 * Composite that shows the members of an EModelElement with an extended feature
 * in a TableViewer.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 */
public class ExtendedTableMembersComposite extends ExtendedMembersComposite {

	/**
	 * EClassifier type for candicate elements.
	 */
	private EClassifier eType = null;

	/**
	 * Instantiates a new members composite.
	 * 
	 * @param parent
	 *            the parent
	 * @param style
	 *            the style
	 * @param string
	 * @param baseLabelProvider
	 * @param structuralFeature
	 */
	public ExtendedTableMembersComposite(Composite parent, int style,
			TabbedPropertySheetWidgetFactory widgetFactory,
			String extendedFeatureID, String labelText, EClassifier type) {
		super(parent, style, widgetFactory, extendedFeatureID, labelText, type);

	}

	/**
	 * The viewer will be a TableViewer.
	 */
	@Override
	protected StructuredViewer getNewViewer(Composite parent) {
		return new TableViewer(parent);
	}

}
