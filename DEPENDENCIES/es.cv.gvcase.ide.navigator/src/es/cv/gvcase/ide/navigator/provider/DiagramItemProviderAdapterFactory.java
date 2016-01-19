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

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITableItemLabelProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.NotationPackage;

/**
 * A factory for creating DiagramPackageProviderAdapter objects.
 */
public class DiagramItemProviderAdapterFactory extends AdapterFactoryImpl {

	/** The supported types. */
	protected static Collection<Object> supportedTypes = null;

	/**
	 * Instantiates a new diagram package provider adapter factory.
	 */
	public DiagramItemProviderAdapterFactory() {
		if (supportedTypes == null) {
			supportedTypes = new ArrayList<Object>();
			supportedTypes.add(IStructuredItemContentProvider.class);
			supportedTypes.add(ITreeItemContentProvider.class);
			supportedTypes.add(IItemPropertySource.class);
			supportedTypes.add(IEditingDomainItemProvider.class);
			supportedTypes.add(IItemLabelProvider.class);
			supportedTypes.add(ITableItemLabelProvider.class);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.emf.common.notify.impl.AdapterFactoryImpl#isFactoryForType
	 * (java.lang.Object)
	 */
	@Override
	public boolean isFactoryForType(Object type) {
		return type instanceof NotationPackage
				|| (supportedTypes != null && supportedTypes.contains(type));
	}

	protected static Adapter DiagramItemProviderAdapter = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.emf.common.notify.impl.AdapterFactoryImpl#createAdapter(org
	 * .eclipse.emf.common.notify.Notifier)
	 */
	@Override
	protected Adapter createAdapter(Notifier target) {
		if (target instanceof Diagram) {
			if (DiagramItemProviderAdapter == null) {
				DiagramItemProviderAdapter = new DiagramItemProvider(this);
			}
			return DiagramItemProviderAdapter;
		}
		return super.createAdapter(target);
	}

}
