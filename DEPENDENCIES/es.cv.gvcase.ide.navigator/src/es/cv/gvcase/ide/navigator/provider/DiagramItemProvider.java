/***************************************************************************
 * Copyright (c) 2007 Conselleria de Infraestructuras y Transporte,
 * Generalitat de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – initial api implementation
 *
 ******************************************************************************/
package es.cv.gvcase.ide.navigator.provider;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.provider.EObjectItemProvider;
import org.eclipse.gmf.runtime.notation.Diagram;

import es.cv.gvcase.mdt.common.util.MDTUtil;

/**
 * The Class DiagramPackageItemProvider.
 */
public class DiagramItemProvider extends EObjectItemProvider {

	/**
	 * Instantiates a new diagram package item provider.
	 * 
	 * @param adapterFactory
	 *            the adapter factory
	 */
	public DiagramItemProvider(AdapterFactory adapterFactory) {
		super(adapterFactory);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.ecore.provider.EObjectItemProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object object) {
		if (object instanceof Diagram) {
			Diagram diagram = (Diagram) object;
			return MDTUtil.getDiagramName(diagram);
		}
		return super.getText(object);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.edit.provider.ItemProviderAdapter#getChildren(java.lang.Object)
	 */
	@Override
	public Collection<?> getChildren(Object object) {
		return Collections.EMPTY_LIST;
	}

}
