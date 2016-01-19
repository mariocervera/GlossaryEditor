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
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import es.cv.gvcase.gvm.glossary.Term;

public class SourceTermsLabelProvider extends LabelProvider {
	
	public String getText(Object object) {
		
		if(object instanceof Term) {
			Term term = (Term) object;
			String name = "";
			if(term.getName() != null) name = term.getName();
			String className = term.getClass().getSimpleName();
			//Remove the "Impl" part of the name
			className = className.substring(0, className.length() - 4);
			return "<" + className + "> " + name;
		}
		
		return "";
	}
	
	
	public Image getImage(Object object) {

		if(object instanceof Term) {
			Term term = (Term) object;
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
		
		return null;
	}

}
