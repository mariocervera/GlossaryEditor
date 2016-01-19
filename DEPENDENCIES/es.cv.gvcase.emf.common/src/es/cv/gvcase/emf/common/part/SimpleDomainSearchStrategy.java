/*******************************************************************************
 * Copyright (c) 2009 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – Initial implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.emf.common.part;

import java.util.Iterator;
import java.util.Map;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.TransactionalEditingDomain;

import es.cv.gvcase.emf.common.part.EditingDomainRegistry.IDomainSearchStrategy;
import es.cv.gvcase.emf.common.util.PathsUtil;

/**
 * Simple IDomainSearchStrategy mpementation used when no other Strategy is
 * given to search for editing domains.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public class SimpleDomainSearchStrategy implements IDomainSearchStrategy {

	public TransactionalEditingDomain get(String editingDomainID, String uri,
			java.util.Map<String, TransactionalEditingDomain> mapKey2Domain) {
		// calculate key from the given editing domain identifier and the
		// given uri.
		String key = EditingDomainRegistry.getInstance().calculateKey(
				editingDomainID, uri);
		TransactionalEditingDomain oldDomain = null;
		if (EditingDomainRegistry.getInstance().containsKey(key)) {
			// check if there is any TransactionalEditingDomain already
			// registered with that key.
			// If there is one already registered, that one is returned.
			oldDomain = mapKey2Domain.get(key);
			oldDomain.getCommandStack().flush();
			return oldDomain;
		} else {
			// Search an editing domain that contains a resource that
			// matches the given uri.
			oldDomain = getByResource(uri, mapKey2Domain);
			if (oldDomain != null) {
				// Register the TransactionalEditingDomain with this key.
				EditingDomainRegistry.getInstance().put(key, oldDomain);
				oldDomain.getCommandStack().flush();
				return oldDomain;
			}
		}
		// no Editing domain was found
		return null;
	}

	/**
	 * Gets a registered {@link TransactionalEditingDomain} having a resource
	 * identified by the the given uri. If exists return the first
	 * {@link TransactionalEditingDomain} containing the resource. If not exists
	 * returns null;
	 * 
	 * @param uri
	 * @return
	 */
	protected TransactionalEditingDomain getByResource(String uri,
			Map<String, TransactionalEditingDomain> mapKey2Domain) {
		String adaptedUri = PathsUtil
				.fromAbsoluteFileSystemToAbsoluteWorkspace(uri);
		boolean found = false;
		TransactionalEditingDomain d = null;
		Iterator<String> keysIterator = mapKey2Domain.keySet().iterator();
		while ((!found) && (keysIterator.hasNext())) {
			d = mapKey2Domain.get(keysIterator.next());
			// Search for resource matching given uri in the ResourceSet
			Iterator<Resource> resIterator = d.getResourceSet().getResources()
					.iterator();
			while ((!found) && (resIterator.hasNext())) {
				Resource r = resIterator.next();
				String rURI = PathsUtil.fromAbsoluteFileSystemToAbsoluteWorkspace(r.getURI().toString());
				if (adaptedUri.equals(rURI)) {
					found = true;
				}
			}
		}
		if (found) {
			return d;
		} else {
			return null;
		}
	}

}
