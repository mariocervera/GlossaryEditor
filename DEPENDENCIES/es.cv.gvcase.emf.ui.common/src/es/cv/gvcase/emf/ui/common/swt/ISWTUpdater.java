/*******************************************************************************
 * Copyright (c) 2012 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana. All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Marc Gil Sendra (Prodevelop) [mgil@prodevelop.es] - initial API implementation
 ******************************************************************************/
package es.cv.gvcase.emf.ui.common.swt;

/**
 * This class contains the widget updating execution that should take effect
 * when the timer of the Asynch SWT Updater will be finished.
 * 
 * @see es.cv.gvcase.emf.ui.common.swt.AsynchSWTUpdater
 * 
 * @author Marc Gil (mgil@prodevelop.es)
 */
public interface ISWTUpdater {
	/**
	 * The execution that will be done when the timer of the Asynch SWT Updater
	 * will be finished
	 */
	void execute();
}
