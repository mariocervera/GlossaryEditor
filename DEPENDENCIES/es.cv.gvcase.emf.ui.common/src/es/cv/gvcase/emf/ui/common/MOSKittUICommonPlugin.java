/*******************************************************************************
* Copyright (c) 2011 Conselleria de Infraestructuras y Transporte, Generalitat 
* de la Comunitat Valenciana. All rights reserved. This program
* and the accompanying materials are made available under the terms of the
* Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
* 
* Contributors:
*  Miguel Llacer (Prodevelop) [mllacer@prodevelop.es] - initial API implementation
******************************************************************************/
package es.cv.gvcase.emf.ui.common;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class MOSKittUICommonPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "es.cv.gvcase.emf.ui.common";

	// The shared instance
	private static MOSKittUICommonPlugin plugin;

	/**
	 * The constructor
	 */
	public MOSKittUICommonPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static MOSKittUICommonPlugin getDefault() {
		return plugin;
	}

}
