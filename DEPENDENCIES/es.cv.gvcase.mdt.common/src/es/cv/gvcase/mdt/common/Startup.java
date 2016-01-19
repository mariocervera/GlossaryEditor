/*******************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – Initial implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IStartup;

import es.cv.gvcase.mdt.common.listeners.ModelNameChangeListener;

// TODO: Auto-generated Javadoc
/**
 * Will install a <ResourceChangeListener> to update diagram references when a
 * model file name changes.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 */
public class Startup implements IStartup {

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IStartup#earlyStartup()
	 */
	public void earlyStartup() {
		try {
			IResourceChangeListener listener = new ModelNameChangeListener();
			ResourcesPlugin.getWorkspace().addResourceChangeListener(listener,
					IResourceChangeEvent.POST_BUILD);
		} catch (NullPointerException ex) {
			IStatus status = new Status(
					IStatus.ERROR,
					Activator.PLUGIN_ID,
					"Error installing resource listener. Model name changes won't update references in diagrams.");
			Activator.getDefault().getLog().log(status);
		}

	}

}
