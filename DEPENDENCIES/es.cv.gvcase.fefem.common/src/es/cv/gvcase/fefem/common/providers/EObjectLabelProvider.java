/***************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte,
 * Generalitat de la Comunitat Valenciana and Open Canarias S.L. 
 * All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Jose Manuel García Valladolid (CIT) - Initial API and implementation
 * 		Adolfo Sanchez-Barbudo Herrera - Bug 2339 class refactoring 
 *
 **************************************************************************/
package es.cv.gvcase.fefem.common.providers;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory.Descriptor.Registry;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import es.cv.gvcase.emf.common.util.EMFUtil;

/**
 * <P>
 * A {@link ILabelProvider} implementation capable to obtain a suitable text and
 * image of any {@link EObject}
 * </P>
 * <P>
 * It's based on the {@link ComposedAdapterFactory}'s {@link Registry global
 * registry}
 * </P>
 * 
 * @author joseval
 * @author adolfosbh class refacting.
 */
public class EObjectLabelProvider extends LabelProvider {

	AdapterFactoryLabelProvider adapter = EMFUtil
			.getAdapterFactoryLabelProvider();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object element) {
		if (element instanceof EObject) {
			return adapter.getText(element);
		}
		return "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
	 */
	@Override
	public Image getImage(Object element) {
		if (element == null)
			return null;
		return adapter.getImage(element);
	}

	@Override
	public void dispose() {
		adapter.dispose();
	}
}
