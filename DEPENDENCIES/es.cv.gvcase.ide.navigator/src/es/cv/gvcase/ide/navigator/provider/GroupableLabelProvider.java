/*******************************************************************************
 * Copyright (c) 2008, 2009 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Javier Muñoz (Prodevelop) - Initial implementation
 *
 ******************************************************************************/
package es.cv.gvcase.ide.navigator.provider;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * 
 * @author <a href="mailto:jmunoz@prodevelop.es">Javier Muñoz</a>
 *
 */
public class GroupableLabelProvider extends LabelProvider {

	@Override
	public Image getImage(Object element) {
		if (element instanceof PackagingNode){
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
		}
		return super.getImage(element);
	}

	@Override
	public String getText(Object element) {
		if (element instanceof PackagingNode){
			return "<"+((PackagingNode)element).getName()+">";
		}
		return super.getText(element);
	}

}
