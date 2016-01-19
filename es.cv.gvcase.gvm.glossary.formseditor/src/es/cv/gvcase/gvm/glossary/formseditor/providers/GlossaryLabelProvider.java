/***************************************************************************
* Copyright (c) 2008 Conselleria de Infraestructuras y Transporte,
* Generalitat de la Comunitat Valenciana . All rights reserved. This program
* and the accompanying materials are made available under the terms of the
* Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors: Mario Cervera Ubeda (Integranova) - Initial API and implementation
*
**************************************************************************/
package es.cv.gvcase.gvm.glossary.formseditor.providers;

import java.io.IOException;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import es.cv.gvcase.gvm.glossary.Term;



public class GlossaryLabelProvider extends CellLabelProvider {

	@Override
	public void update(ViewerCell cell) {
		if(cell.getColumnIndex() == 0) { //Name
			cell.setText(((Term)cell.getElement()).getName());
		}
		else { //Type
			String className = cell.getElement().getClass().getSimpleName();
			//Remove the "Impl" part of the name
			className = className.substring(0, className.length() - 4);
			cell.setText(className);
			if(cell.getElement() instanceof EObject) {
				cell.setImage(getImage((EObject)cell.getElement()));
			}
			
		}
	}

	private Image getImage(EObject term) {
		
		if (term == null) return null;
		
		String imagePath = "";
		try {
			imagePath = FileLocator.toFileURL(Platform.getBundle("es.cv.gvcase.gvm.glossary.edit").getResource("icons/full/obj16")).getPath();
		}
		catch(IOException e) {return null;}
		
		String className = term.getClass().getSimpleName();
		//Remove the "Impl" part of the name
		className = className.substring(0, className.length() - 4);
		imagePath += className + ".gif";
		Image image = new Image(Display.getCurrent(), imagePath);
		return image;
	}
}
