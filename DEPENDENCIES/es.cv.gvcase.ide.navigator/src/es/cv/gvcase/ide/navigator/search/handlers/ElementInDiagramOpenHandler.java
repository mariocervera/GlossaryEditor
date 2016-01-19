/***************************************************************************
 * Copyright (c) 2008, 2009 Conselleria de Infraestructuras y Transporte,
 * Generalitat de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – initial api implementation
 *
 ******************************************************************************/

package es.cv.gvcase.ide.navigator.search.handlers;

import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.search.ui.handlers.AbstractOpenDiagramHandler;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.NotationPackage;
import org.eclipse.ui.IEditorPart;

import es.cv.gvcase.ide.navigator.Activator;
import es.cv.gvcase.mdt.common.util.MultiDiagramUtil;

/**
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 *
 */
public class ElementInDiagramOpenHandler extends AbstractOpenDiagramHandler {

	@Override
	public IStatus openDiagramEditor(String editorID, List<Object> list) {

		if (list.isEmpty() == false) {
			Object o = list.get(0);
			if (o instanceof Diagram) {
				Diagram diagram = (Diagram) o;
				try {
					MultiDiagramUtil.openDiagram(diagram);
					return Status.OK_STATUS;
				} catch (ExecutionException ex) {
					IStatus status = new Status(IStatus.ERROR,
							Activator.PLUGIN_ID, "Error opening diagram", ex);
					Activator.getDefault().getLog().log(status);
				}
			}
		}

		return super.openDiagramEditor(editorID, list);
	}

	@Override
	protected String getDiagramEditorID() {
		// return UMLPackage.eNS_URI;
		return NotationPackage.eNS_URI;
	}

	@Override
	protected String getDiagramFileExtension() {
		return "uml_diagram";
	}

	@Override
	protected void selectElements(IEditorPart editorPart, List<Object> selection) {
		// TODO Auto-generated method stub

	}

}
