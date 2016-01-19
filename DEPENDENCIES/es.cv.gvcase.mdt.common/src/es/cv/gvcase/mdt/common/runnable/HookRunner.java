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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.cv.gvcase.emf.common.part.ExtensionPointParser;

/**
 * Runs all the {@link Runnable}s that are hooked to a specific hook id.<br>
 * The Runnables are executed in order of priority.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public class HookRunner {

	/**
	 * Runnable implementation of the runnable element in the
	 * 'es.cv.gvcase.mdt.common.runnableHook' extension point.
	 */
	public class Runnable implements Comparable<Runnable> {
		/**
		 * This runnable identifier
		 */
		public String runnableId;
		/**
		 * This runnable hook identifier
		 */
		public String hookId;
		/**
		 * This runnable {@link Runnable} class
		 */
		public Object runnable;
		/**
		 * This runnable priority
		 */
		public String priority;

		/**
		 * Compare runnables by priority
		 */
		public int compareTo(Runnable o) {
			if (o == null || !(o instanceof Runnable)) {
				return -1;
			}
			Runnable runnable = (Runnable) o;
			if (runnable == this) {
				return 0;
			}

			int prio1 = Integer.valueOf(priority);
			int prio2 = Integer.valueOf(runnable.priority);

			if (prio2 > prio1) {
				return -1;
			}
			if (prio1 == prio2) {
				return 0;
			}
			if (prio2 < prio1) {
				return 1;
			}
			return -1;
		}
	}

	// //
	// hooked runnables registry
	// //

	// //
	// singleton
	// //

	/**
	 * Singleton instance
	 */
	public static HookRunner INSTANCE = null;

	/**
	 * HooKRunner INSTANCE getter
	 * 
	 * @return
	 */
	public static HookRunner getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new HookRunner();
		}
		return INSTANCE;
	}

	private HookRunner() {
	}

	// //
	// registry
	// //

	/**
	 * Mapping from hook identifier to list of runnables.
	 */
	private Map<String, List<Runnable>> mapId2Runnable = null;

	/**
	 * Hook runnables extension point identifier.
	 */
	private final static String extensionPointID = "es.cv.gvcase.mdt.common.runnableHook";

	/**
	 * Fills the hooked runnables registry from extension points.
	 */
	protected void readRegistry() {
		if (mapId2Runnable == null) {
			mapId2Runnable = new HashMap<String, List<Runnable>>();
		}
		mapId2Runnable.clear();
		ExtensionPointParser parser = new ExtensionPointParser(
				extensionPointID, new Class[] { Runnable.class }, this);
		for (Object o : parser.parseExtensionPoint()) {
			if (o instanceof Runnable) {
				Runnable runnable = (Runnable) o;
				if (runnable.hookId != null
						&& runnable.runnable instanceof java.lang.Runnable) {
					List<Runnable> hookRunnables = mapId2Runnable
							.get(runnable.hookId);
					if (hookRunnables == null) {
						hookRunnables = new ArrayList<Runnable>();
						mapId2Runnable.put(runnable.hookId, hookRunnables);
					}
					hookRunnables.add(runnable);
				}
			}
		}
	}

	// //
	// Hook Runner
	// //

	/**
	 * Runs all the {@link Runnable}s hooked for a given hook identifier by
	 * priority.
	 */
	public void runRunnablesForHook(String hookId) {
		// delegate to runRunnablesWithInfoForHook
		runRunnablesWithInfoForHook(hookId, null);
	}

	/**
	 * Runs all the {@link Runnable}s hooked for a given hook identifier by
	 * priority with additional information.
	 */
	public void runRunnablesWithInfoForHook(String hookId, Object info) {
		// fill the registry with all the hooked runnables
		readRegistry();
		// get all the runnables for the given hook identifier
		List<Runnable> hookRunnables = mapId2Runnable.get(hookId);
		if (hookRunnables == null || hookRunnables.size() <= 0) {
			// no runnable for the given identifier; return
			return;
		}
		// sort the runnables by priority
		Runnable[] runnablesArray = new Runnable[hookRunnables.size()];
		hookRunnables.toArray(runnablesArray);
		Arrays.sort(runnablesArray);
		// execute the runnables by order of priority
		for (Runnable runnable : runnablesArray) {
			if (runnable.runnable instanceof IRunnableWithInfo) {
				IRunnableWithInfo runnableClass = (IRunnableWithInfo) runnable.runnable;
				runnableClass.runWithInfo(info);
			}
		}
	}
}
