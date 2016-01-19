/*******************************************************************************
 * Copyright (c) 2008 - 2010 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Miguel LLacer (Prodevelop) – Initial implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.emf.common.part;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;

/**
 * A registry that provides AdapterFactories (usually adapting to ILabelProvider
 * and cubclasses and to IContentProvider and subclasses) by priority, as
 * contributed to the registry via extensions.
 * 
 * @author mllacer
 * 
 */
public class PriorityAdapterFactoryRegistry extends ComposedAdapterFactory {

	/**
	 * The Singleton
	 */
	private static final PriorityAdapterFactoryRegistry INSTANCE = new PriorityAdapterFactoryRegistry();

	private PriorityAdapterFactoryRegistry() {
		super(ComposedAdapterFactory.Descriptor.Registry.INSTANCE);
		readExtensionPoint();
	}

	public static PriorityAdapterFactoryRegistry getInstance() {
		return INSTANCE;
	}

	private static final String ExtensionPointID = "es.cv.gvcase.emf.common.itemProviderAdapterFactories";

	private Map<String, List<PriorityAdapterFactory>> mapNsURI2AdapterFactoryList = new HashMap<String, List<PriorityAdapterFactory>>();

	@Override
	public AdapterFactory getFactoryForTypes(Collection<?> types) {
		String nsURI = null;

		for (Object key : types) {
			if (key instanceof EPackage) {
				nsURI = ((EPackage) key).getNsURI();
				break;
			}
		}

		if (nsURI == null) {
			return super.getFactoryForTypes(types);
		}

		AdapterFactory factory = getFactoryForNsURI(nsURI);

		if (factory == null) {
			return super.getFactoryForTypes(types);
		}

		return factory;
	}

	protected String getExtensionPointID() {
		return ExtensionPointID;
	}

	/**
	 * Read extension point's
	 */
	protected void readExtensionPoint() {
		ExtensionPointParser parser = new ExtensionPointParser(
				ExtensionPointID, new Class[] { PriorityAdapterFactory.class });
		PriorityAdapterFactory factory = null;
		List<PriorityAdapterFactory> factories = new ArrayList<PriorityAdapterFactory>();
		// parse extension point
		for (Object object : parser.parseExtensionPoint()) {
			factory = (PriorityAdapterFactory) Platform.getAdapterManager()
					.getAdapter(object, PriorityAdapterFactory.class);
			if (factory != null) {
				factories.add(factory);
			}
		}
		// store all factories in the mapping
		for (PriorityAdapterFactory factoryElement : factories) {
			List<PriorityAdapterFactory> list = null;

			if (!mapNsURI2AdapterFactoryList.containsKey(factoryElement.uri)) {
				list = new ArrayList<PriorityAdapterFactory>();
			} else {
				list = mapNsURI2AdapterFactoryList.get(factoryElement.uri);
			}

			list.add(factoryElement);
			mapNsURI2AdapterFactoryList.put(factoryElement.uri, list);
		}
	}

	protected AdapterFactory getFactoryForNsURI(String nsURI) {
		if (!mapNsURI2AdapterFactoryList.containsKey(nsURI)) {
			return null;
		}
		List<PriorityAdapterFactory> list = mapNsURI2AdapterFactoryList
				.get(nsURI);
		PriorityAdapterFactory factory = list.get(0);

		for (PriorityAdapterFactory af : list) {
			if (factory.priority == null || af.priority == null) {
				continue;
			}
			if (Integer.parseInt(factory.priority) < Integer
					.parseInt(af.priority)) {
				factory = af;
			}
		}

		if (!(factory.factoryClass instanceof AdapterFactory)) {
			return null;
		}

		return (AdapterFactory) factory.factoryClass;
	}

}
