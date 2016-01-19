/*******************************************************************************
 * Copyright (c) 2008, 2009 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Marc Gil Sendra (Prodevelop) – Initial implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.transaction.ResourceSetListener;
import org.eclipse.emf.transaction.TransactionalEditingDomain;

import es.cv.gvcase.mdt.common.model.DomainListenerBinding;
import es.cv.gvcase.mdt.common.model.Listener;
import es.cv.gvcase.mdt.common.util.ExtensionPointParser;

public class DomainListenersRegistry {

	/**
	 * A Map to store the listeners and his identifier
	 */
	private HashMap<String, ResourceSetListener> mapListeners = null;

	/**
	 * A Map to store the domain and the associated listener identifiers
	 */
	private HashMap<String, List<String>> mapDomainsListeners = null;

	/**
	 * A Map to store the times listeners are applied to the domain
	 */
	private HashMap<TransactionalEditingDomain, Integer> mapDomainTimes = new HashMap<TransactionalEditingDomain, Integer>();

	/**
	 * The extension point identifier
	 */
	private final String extensionPointID = "es.cv.gvcase.mdt.common.listenerToDomain";

	/**
	 * The new instance for the listener registry
	 */
	private static final DomainListenersRegistry INSTANCE = new DomainListenersRegistry();

	/**
	 * Gets the instance for this registry
	 * 
	 * @return
	 */
	public static DomainListenersRegistry getInstance() {
		return INSTANCE;
	}

	/**
	 * Gets the mapping between domains and listeners
	 * 
	 * @return
	 */
	private HashMap<String, List<String>> getMapDomainsListeners() {
		if (mapDomainsListeners == null || mapDomainsListeners.size() == 0) {
			parseListenersFromExtensionPoint();
		}

		return mapDomainsListeners;
	}

	/**
	 * Initialize the mappings between listeners and his identifier, and domains
	 * and listeners
	 */
	private void parseListenersFromExtensionPoint() {
		mapListeners = new HashMap<String, ResourceSetListener>();
		mapDomainsListeners = new HashMap<String, List<String>>();

		ExtensionPointParser extensionPointParser = new ExtensionPointParser(
				extensionPointID, new Class[] { Listener.class,
						DomainListenerBinding.class });

		for (Object o : extensionPointParser.parseExtensionPoint()) {
			if (o instanceof Listener) {
				Listener listener = (Listener) Platform.getAdapterManager()
						.getAdapter(o, Listener.class);
				mapListeners.put(listener.ID,
						(ResourceSetListener) listener.ListenerClass);
			} else if (o instanceof DomainListenerBinding) {
				DomainListenerBinding domainListenerBinding = (DomainListenerBinding) Platform
						.getAdapterManager().getAdapter(o,
								DomainListenerBinding.class);

				if (!mapDomainsListeners
						.containsKey(domainListenerBinding.Domain)) {
					List<String> list = new ArrayList<String>();
					list.add(domainListenerBinding.Listener);
					mapDomainsListeners.put(domainListenerBinding.Domain, list);
				} else {
					mapDomainsListeners.get(domainListenerBinding.Domain).add(
							domainListenerBinding.Listener);
				}
			}
		}
	}

	/**
	 * Adds the listeners to the given domain, defined through the extension
	 * point "es.cv.gvcase.mdt.common.listenerToDomain"
	 * 
	 * @param domain
	 *            : the editing domain
	 */
	public void addListeners(TransactionalEditingDomain domain) {
		if (domain == null || (domain.getID() != null && domain.getID().equals("")))
			return;

		if (!mapDomainTimes.containsKey(domain)) {
			initializeDomainTimes(domain);
		}

		if (mapDomainTimes.get(domain).intValue() == 0) {
			if (getMapDomainsListeners().containsKey(domain.getID())) {
				for (String s : getMapDomainsListeners().get(domain.getID())) {
					ResourceSetListener listener = mapListeners.get(s);
					if (listener != null)
						domain.addResourceSetListener(listener);
				}
			}
		}

		incrementDomainTime(domain);
	}

	/**
	 * Removes the listeners to the given domain, defined through the extension
	 * point "es.cv.gvcase.mdt.common.listenerToDomain"
	 * 
	 * @param domain
	 *            : the editing domain
	 */
	public void removeListeners(TransactionalEditingDomain domain) {
		if (domain == null || domain.getID() == null
				|| domain.getID().equals(""))
			return;

		if (!mapDomainTimes.containsKey(domain))
			return;

		if (mapDomainTimes.get(domain).intValue() > 0) {
			decrementDomainTime(domain);
		}

		if (!mapDomainTimes.containsKey(domain)) {
			if (getMapDomainsListeners().containsKey(domain.getID())) {
				for (String s : getMapDomainsListeners().get(domain.getID())) {
					ResourceSetListener listener = mapListeners.get(s);
					if (listener != null)
						domain.removeResourceSetListener(listener);
				}
			}
		}
	}

	private void initializeDomainTimes(TransactionalEditingDomain domain) {
		mapDomainTimes.put(domain, new Integer(0));
	}

	private void incrementDomainTime(TransactionalEditingDomain domain) {
		int i = mapDomainTimes.get(domain).intValue();
		i++;
		mapDomainTimes.remove(domain);
		mapDomainTimes.put(domain, i);
	}

	private void decrementDomainTime(TransactionalEditingDomain domain) {
		int i = mapDomainTimes.get(domain).intValue();
		i--;
		mapDomainTimes.remove(domain);

		if (i > 0)
			mapDomainTimes.put(domain, i);
	}
}
