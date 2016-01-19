/*******************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana. All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Mario Cervera Ubeda (Integranova) - initial API implementation
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.composites;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

/**
 * The Class MembersComposite.
 */
public class TableMembersComposite extends MembersComposite {

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
	public TableMembersComposite(Composite parent, int style, TabbedPropertySheetWidgetFactory widgetFactory, EStructuralFeature structuralFeature, String labelText) {
		super(parent, style, widgetFactory, structuralFeature, labelText);
	}

	@Override
	protected StructuredViewer getNewViewer(Composite parent) {
		return  new TableViewer(parent);
	}

//	protected void fillListElements() {
//		if (!(getListElements() instanceof TableViewer))
//			return;
//
//		EList<EObject> elements = getElements();
//		((TableViewer) getListElements()).getTable().removeAll();
//		if (elements != null) {
//			for (EObject object : elements) {
//				((TableViewer) getListElements()).add(object);
//			}
//		}
//	}

//	protected void fillListMembers() {
//		if (!(getListElements() instanceof TableViewer))
//			return;
//
//		EList<EObject> elements = getMembers();
//		((TableViewer) getListElements()).getTable().removeAll();
//		if (elements != null) {
//			for (EObject object : elements) {
//				((TableViewer) getListElements()).add(object);
//			}
//		}
//	}
}
