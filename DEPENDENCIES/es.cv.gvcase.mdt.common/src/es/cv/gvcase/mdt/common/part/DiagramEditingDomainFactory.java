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
package es.cv.gvcase.mdt.common.part;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.TransactionalEditingDomain.Factory;

/**
 * A proxy {@link org.eclipse.emf.transaction.impl.TransactionValidator.Factory}
 * that delegates the creation of editing dominas to the gmf factory that
 * creates DiagramEditingDomains.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public class DiagramEditingDomainFactory implements Factory {

	/**
	 * The {@link org.eclipse.emf.transaction.impl.TransactionValidator.Factory}
	 * to which all operations are delegated.
	 */
	private static final Factory RealFactoryInstance = org.eclipse.gmf.runtime.diagram.core.DiagramEditingDomainFactory
			.getInstance();

	/**
	 * Create a {@link TransactionalEditingDomain} with an empty
	 * {@link ResourceSet}.
	 */
	public TransactionalEditingDomain createEditingDomain() {
		if (RealFactoryInstance != null) {
			TransactionalEditingDomain domain = RealFactoryInstance
					.createEditingDomain();
			mapResourceSet(domain);
			return domain;
		}
		return null;
	}

	/**
	 * Create a {@link TransactionalEditingDomain} from an existing
	 * {@link ResourceSet}.
	 */
	public TransactionalEditingDomain createEditingDomain(ResourceSet rset) {
		if (RealFactoryInstance != null) {
			return RealFactoryInstance.createEditingDomain(rset);
		}
		return null;
	}

	/**
	 * Get a {@link TransactionalEditingDomain} for the given
	 * {@link ResourceSet}.
	 */
	public TransactionalEditingDomain getEditingDomain(ResourceSet rset) {
		if (RealFactoryInstance != null) {
			return RealFactoryInstance.getEditingDomain(rset);
		}
		return null;
	}

	/**
	 * Adds the specified editing domain to the global reverse mapping of
	 * resource sets.
	 * 
	 * @param domain
	 *            the editing domain to add to the resource set mapping
	 */
	public synchronized void mapResourceSet(TransactionalEditingDomain domain) {
		domain.getResourceSet().eAdapters().add(
				new ResourceSetDomainLink(domain));
	}

	// //
	// Helper class to relate a ResourceSet to a TransactionalEditingDomain
	// //

	/**
	 * An adapter that attaches a weak reference to the editing domain onto the
	 * resource set that it manages.
	 * 
	 * @author Christian W. Damus (cdamus)
	 */
	private static class ResourceSetDomainLink extends AdapterImpl implements
			IEditingDomainProvider {

		private final Reference<TransactionalEditingDomain> domain;

		ResourceSetDomainLink(TransactionalEditingDomain domain) {
			this.domain = new WeakReference<TransactionalEditingDomain>(domain);
		}

		@Override
		public boolean isAdapterForType(Object type) {
			return (type == ResourceSetDomainLink.class)
					|| (type == IEditingDomainProvider.class);
		}

		final TransactionalEditingDomain getDomain() {
			TransactionalEditingDomain result = domain.get();

			if (result == null) {
				// no longer need the adapter
				getTarget().eAdapters().remove(this);
			}

			return result;
		}

		public final EditingDomain getEditingDomain() {
			return getDomain();
		}
	}
}
