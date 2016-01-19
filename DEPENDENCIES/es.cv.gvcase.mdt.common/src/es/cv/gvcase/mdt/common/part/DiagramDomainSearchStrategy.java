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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gmf.runtime.emf.core.resources.GMFResourceFactory;
import org.eclipse.gmf.runtime.notation.Diagram;

import es.cv.gvcase.emf.common.part.EditingDomainRegistry;
import es.cv.gvcase.emf.common.part.EditingDomainRegistry.IDomainSearchStrategy;
import es.cv.gvcase.emf.common.util.PathsUtil;

/**
 * An {@link IDomainSearchStrategy} for the {@link EditingDomainRegistry} that
 * can search resources of models under diagrams.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public class DiagramDomainSearchStrategy implements IDomainSearchStrategy {

	// //
	// Singleton
	// //

	private static final DiagramDomainSearchStrategy Instance = new DiagramDomainSearchStrategy();

	private DiagramDomainSearchStrategy() {
	}

	public static DiagramDomainSearchStrategy getInstance() {
		return Instance;
	}

	// //
	// resolve all flag
	// //
	private boolean resolveAllAllowed = true;

	public boolean isResolveAllAllowed() {
		return resolveAllAllowed;
	}

	public void setResolveAllAllowed(boolean resolveAllAllowed) {
		this.resolveAllAllowed = resolveAllAllowed;
	}

	// //
	// original resource to find editing domain for URI
	// //

	private URI originalURI = null;

	protected URI getOriginalURI() {
		return originalURI;
	}

	// //
	// Interface implementation
	// //

	public TransactionalEditingDomain get(String id, String uri,
			Map<String, TransactionalEditingDomain> mapKey2Domain) {

		// in case there is still no editing domain registered.
		if (mapKey2Domain == null || mapKey2Domain.isEmpty()) {
			return null;
		}

		// store the original URI that arrives
		storeOriginalURI(URI.createURI(uri));

		/*
		 * To find a TransactionalEditingDomain we will search in this order: 1)
		 * look for an editing domain that has the resource in its resource set.
		 * 2) if the resource to be loaded contains Diagrams, search for an
		 * editing domain that has any of the resources of the underlying model.
		 * 3) resolve all references of the resource and search for an editing
		 * that has any of those.
		 */

		TransactionalEditingDomain domain = null;

		// case 1, simple search by resource
		domain = getByResource(uri, mapKey2Domain);
		if (domain != null) {
			// a TransactionalEditingDomain was found, return it
			return domain;
		}

		// case 2, if a diagram containing resource, search resources under
		// the
		// diagrams
		// Load the resource via a ResourceSet
		ResourceSet resourceSet = new ResourceSetImpl();
		URI resourceUri = URI.createURI(uri);
		Resource loadedResource = resourceSet.getResource(resourceUri, true);
		try {
			loadedResource.load(GMFResourceFactory.getDefaultLoadOptions());
		} catch (IOException ex) {
			return null;
		}
		if (loadedResource != null) {
			// search for Diagrams in the resource
			for (EObject eObject : loadedResource.getContents()) {
				if (eObject instanceof Diagram) {
					// get the underlying model element from each Diagram
					Diagram diagram = (Diagram) eObject;
					// look for an editing domain via the diagram
					domain = getByDiagramElement(diagram, mapKey2Domain);
					if (domain != null) {
						return domain;
					}
				}
			}
		}

		// the search through all dependencies can be very heavy so it can
		// be
		// "turned off"
		if (isResolveAllAllowed()) {
			// case 3, resolve all dependencies from the resource and search
			// for
			// editing domains with those dependencies
			cleanResourceSet(resourceSet);
			EcoreUtil.resolveAll(resourceSet); // XXX: heavy operation
			// search by each resource in the resource set
			String resourceUriString = null;
			for (Resource resource : resourceSet.getResources()) {
				if (resource.getURI() != null && !resource.getURI().toString().equals("")) {
					resourceUriString = PathsUtil.getAdaptedURIWithFilename(
							getOriginalURI(), resource.getURI()).toString();
					// resourceUriString =
					// resource.getURI().trimFragment().resolve(
					// getOriginalURI()).toString();
					domain = getByResource(resourceUriString, mapKey2Domain);
					if (domain != null) {
						return domain;
					}
				}
			}
		}
		// if we get here, no suitable editing domain was found.
		return null;
	}

	protected void storeOriginalURI(URI originalURI) {
		this.originalURI = PathsUtil.getBaseWorkspacePathURI(originalURI);
	}

	protected TransactionalEditingDomain getByDiagramElement(Diagram diagram,
			Map<String, TransactionalEditingDomain> mapKey2Domain) {
		if (diagram == null || diagram.getElement() == null) {
			// we can't get a TransactionalEditingDomain without a resource
			return null;
		}
		if (diagram.getElement().eResource() == null) {
			resolveDiagramElement(diagram);
			if (diagram.getElement().eResource() == null) {
				return null;
			}
		}
		// get the uri of the resource of the model element under the diagram.
		String resourceUri = diagram.getElement().eResource().getURI()
				.trimFragment().toString();
		// look in the map for that resource via its uri
		return getByResource(resourceUri, mapKey2Domain);
	}

	/**
	 * Removes empty {@link Resource} from the {@link ResourceSet}.
	 * 
	 * @param resourceSet
	 */
	protected void cleanResourceSet(ResourceSet resourceSet) {
		if (resourceSet != null) {
			// visit all resources in the resource set and remove those that
			// have no contents
			List<Resource> resourcesToPurge = new ArrayList<Resource>();
			for (Resource resource : resourceSet.getResources()) {
				if (resource.getContents().isEmpty()) {
					resourcesToPurge.add(resource);
				}
			}
			for (Resource resource : resourcesToPurge) {
				resourceSet.getResources().remove(resource);
			}
		}
	}

	/**
	 * Resolves the element behind the diagram.
	 * 
	 * @param diagram
	 */
	protected void resolveDiagramElement(Diagram diagram) {
		if (diagram != null && diagram.getElement() != null
				&& diagram.eResource() != null
				&& diagram.eResource().getResourceSet() != null) {
			// cleans up the resource set of resources that have no contents
			cleanResourceSet(diagram.eResource().getResourceSet());
			// tries to resolve all references and dependencies
			EcoreUtil.resolveAll(diagram.getElement());
		}
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
		if (uri == null || mapKey2Domain == null || mapKey2Domain.isEmpty()) {
			// there is no way we can find and editing domain if this condition
			// is met
			return null;
		}
		// adapt and normalize the arriving URI.
		String adaptedUri = PathsUtil
				.fromAbsoluteFileSystemToAbsoluteWorkspace(uri);
		boolean found = false;
		TransactionalEditingDomain domain = null;
		Iterator<String> keysIterator = mapKey2Domain.keySet().iterator();
		// search through all stored TeansactionalEditingDomains
		while ((!found) && (keysIterator.hasNext())) {
			domain = mapKey2Domain.get(keysIterator.next());
			// Search for resource matching given uri in the ResourceSet of this
			// editing domain
			Iterator<Resource> resIterator = domain.getResourceSet()
					.getResources().iterator();
			String resourceURI = null;
			while ((!found) && (resIterator.hasNext())) {
				Resource resource = resIterator.next();
				// get this resource URI
				resourceURI = PathsUtil
						.fromAbsoluteFileSystemToAbsoluteWorkspace(resource
								.getURI().toString());
				// and compare it with the arriving resource's uri.
				if (resourceURI.equals(adaptedUri)) {
					found = true;
				}
			}
		}
		if (found) {
			// if a domain was found, return it.
			return domain;
		} else {
			// if no domain was found, return null.
			return null;
		}
	}

}
