/*******************************************************************************
 * Copyright (c) 2010 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – Initial implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.runnable;

/**
 * A runnable with information.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public interface IRunnableWithInfo extends Runnable {

	public abstract void runWithInfo(Object info);

}
