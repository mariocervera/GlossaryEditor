/*******************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Jose Manuel García Valladolid (Indra SL- CIT) – Initial implementation
 *
 ******************************************************************************/
package es.cv.gvcase.fefem.common.utils;

import java.util.Vector;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.part.Page;

import es.cv.gvcase.fefem.common.FEFEMEditor;


/**
 * This interface defines the operations that a EObject Selection Synchronizer Strategy should implements
 * in order to makes the editor reacting to external EObject selections
 * 
 * @author Jose Manuel García Valladolid (garcia_josval@gva.es)
 *
 */
public interface ISelectionSynchronizerStrategy {

	public void synchronizeEObjectSelection(ISelection selection, FEFEMEditor editor, Vector<Page> pages);
}
