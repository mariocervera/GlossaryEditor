/*******************************************************************************
 * Copyright (c) 2009 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Carlos Sánchez Periñán (Prodevelop)
 *
 ******************************************************************************/

package es.cv.gvcase.mdt.common.listeners;

public interface ArraySelectionListener extends org.eclipse.swt.events.SelectionListener {
	/**
	 * set the new array position
	 * @param index
	 */
	public void setIndex(int index);
	/**
	 * Get the current array position
	 * @return position
	 */
	public int getIndex();
}
