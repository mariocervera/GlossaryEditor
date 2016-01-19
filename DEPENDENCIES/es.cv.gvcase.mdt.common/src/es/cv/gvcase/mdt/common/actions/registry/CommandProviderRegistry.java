/*******************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Mario Cervera Ubeda (Prodevelop) – Initial implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.actions.registry;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EClass;

import es.cv.gvcase.mdt.common.commandProviders.CommandProvider;
import es.cv.gvcase.mdt.common.util.ExtensionPointParser;
import es.cv.gvcase.mdt.common.util.MDTUtil;

public class CommandProviderRegistry {
	
	private static Map<String, ICommandProvider> commandProviderRegistry;
	
	public static boolean providesFor(EClass eclass) {
		if(getCommandProviderRegistry().containsKey(eclass.getInstanceTypeName())) {
			return true;
		}
		else {
			for(String key : getCommandProviderRegistry().keySet()) {
				if(MDTUtil.isOfType(eclass.getInstanceClass(), key)) return true;
			}
			return false;
		}
	}
	
	public static ICommandProvider getCommandProvider(EClass eclass) {
		if(getCommandProviderRegistry().get(eclass.getInstanceTypeName()) != null) {
			return getCommandProviderRegistry().get(eclass.getInstanceTypeName());
		}
		else {
			for(String key : getCommandProviderRegistry().keySet()) {
				if(MDTUtil.isOfType(eclass.getInstanceClass(), key)) {
					return getCommandProviderRegistry().get(key);
				}
			}
			
			return null;
		}
	}
	
	public static void addCommandProvider(String eclass, ICommandProvider provider) {
		getCommandProviderRegistry().put(eclass, provider);
	}
	
	
	private static Map<String, ICommandProvider> getCommandProviderRegistry() {
		
		if(commandProviderRegistry != null) {
			return commandProviderRegistry;
		}
		else {
			//If the registry is null then create it and fill it parsing the extension point
			
			commandProviderRegistry = new HashMap<String, ICommandProvider>();
			
			ExtensionPointParser extensionPointParser = new ExtensionPointParser(
					"es.cv.gvcase.mdt.common.commandProviders",
					new Class[] {CommandProvider.class});
			for(Object o : extensionPointParser.parseExtensionPoint()) {
				CommandProvider cp = (CommandProvider) Platform.getAdapterManager()
				.getAdapter(o, CommandProvider.class);
				if (cp != null) {
					if(cp.implementationClass instanceof ICommandProvider) {
						ICommandProvider prov = (ICommandProvider) cp.implementationClass;
						commandProviderRegistry.put(cp.type, prov);
					}
				}
			}
			
			return commandProviderRegistry;
		}
	}
}
