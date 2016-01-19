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
package es.cv.gvcase.emf.common.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.Resource.Factory.Registry;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.workspace.util.WorkspaceSynchronizer;

import es.cv.gvcase.emf.common.util.PathsUtil;

/**
 * Provides the unused {@link Resource}s in a given original set of Resources
 * comparing to another set of referenced given resources. <br>
 * All the unresolved references from the given referenced set of references are
 * resolved prior to calculating the unused resources.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public class UnusedResourcesProvider {

	// //
	// Resources to work with
	// //

	/**
	 * Original {@link ResourceSet} to compare with, and find {@link Resource}s
	 * no longer in use that can be unloaded. <br>
	 * This ResourceSet will usually be the one from the shared
	 * {@link TransactionalEditingDomain} that MOSKitt editors use.
	 */
	protected List<Resource> originalResources = null;

	protected List<String> resourcesToSolve = null;

	// //
	// Constructors
	// //

	protected UnusedResourcesProvider() {
		// TODO Auto-generated constructor stub
	}

	public UnusedResourcesProvider(ResourceSet originalResourceSet) {
		this(originalResourceSet, (List<String>) null);
	}

	public UnusedResourcesProvider(ResourceSet originalResourceSet,
			String resourceToSolve) {
		this(originalResourceSet, Collections.singletonList(resourceToSolve));
	}

	public UnusedResourcesProvider(ResourceSet originalResourceSet,
			List<String> resourcesToSolve) {
		this.originalResources = originalResourceSet.getResources();
		this.resourcesToSolve = resourcesToSolve;
	}

	public UnusedResourcesProvider(List<Resource> originalResources,
			List<String> resourcesToSolve) {
		this.originalResources = originalResources;
		this.resourcesToSolve = resourcesToSolve;
	}

	// //
	// Resource addition
	// //

	/**
	 * Adds a {@link Resource} to be solved.
	 */
	public void addResourceToSolve(String resourceToSolve) {
		if (this.resourcesToSolve == null) {
			resourcesToSolve = new ArrayList<String>();
		}
		try {
			resourcesToSolve.add(resourceToSolve);
		} catch (UnsupportedOperationException ex) {
			// The resourcesToSolve may be a non modifiable Collection.
			resourcesToSolve = new ArrayList<String>();
			resourcesToSolve.add(resourceToSolve);
		}
	}

	/**
	 * Adds a {@link Collection} of {@link Resource}s to be solved.
	 * 
	 * @param resourcesToSolve
	 */
	public void addAllResourcesToSolve(Collection<String> resourcesToSolve) {
		if (this.resourcesToSolve == null) {
			this.resourcesToSolve = new ArrayList<String>();
		}
		try {
			this.resourcesToSolve.addAll(resourcesToSolve);
		} catch (UnsupportedOperationException ex) {
			// The resourcesToSolve may be a non modifiable Collection.
			resourcesToSolve = new ArrayList<String>();
			resourcesToSolve.addAll(resourcesToSolve);
		}
	}

	// //
	// Resource comparing
	// //

	public List<Resource> getUnusedResources() {
		if (originalResources == null || originalResources.isEmpty()
				|| resourcesToSolve == null || resourcesToSolve.isEmpty()) {
			return Collections.emptyList();
		}
		/**
		 * How-to find the unused resources: 1) find, for all the given
		 * resources to solve, all the resources they reference 1.0) having a
		 * list of resources referenced originally equal to the list of
		 * resources to solve 1.1) for each resource to solve as resource 1.2)
		 * resolve all the references of that resource and add them to the list
		 * of referenced resources 1.3) repeat until there are no new resources
		 * added to the list of referenced resources 2) compare to the list of
		 * original resources and find the unused resources
		 */
		// get the collection of referenced resources
		List<String> referencedResourcesPaths = getAllResourcesReferenced(resourcesToSolve);
		// get the list of original resources' string paths
		Map<String, Resource> mapOriginalPath2Resource = new HashMap<String, Resource>();
		String originalPath = "";
		IFile originalFile = null;
		for (Resource originalResource : originalResources) {
			originalFile = WorkspaceSynchronizer
					.getUnderlyingFile(originalResource);
			if (originalFile != null) {
				originalPath = originalFile.getRawLocation().toString();
				originalPath = PathsUtil
						.fromAbsoluteFileSystemToAbsoluteWorkspace(originalPath);
				mapOriginalPath2Resource.put(originalPath, originalResource);
			}
		}
		// compare original resources to referenced resources by name and get a
		// list of unused resources
		List<Resource> unusedResources = new ArrayList<Resource>();
		for (String original : mapOriginalPath2Resource.keySet()) {
			if (!referencedResourcesPaths.contains(original)) {
				// if a resource path is in the original set but not in the
				// calculated references set, that resource is no longer needed
				// and such, unused
				unusedResources.add(mapOriginalPath2Resource.get(original));
			}
		}
		return unusedResources;
	}

	/**
	 * Gets all the resources that are referenced from the given set of
	 * resources, including the given set of resources.
	 * 
	 * @param resourcesToSolve
	 * @return
	 */
	protected List<String> getAllResourcesReferenced(
			List<String> resourcesToSolve) {
		if (resourcesToSolve == null || resourcesToSolve.isEmpty()) {
			return Collections.emptyList();
		}
		List<String> allResourcesPaths = new ArrayList<String>();
		// find all the paths of the resources.
		ResourceLoaderResolver resourceLoaderResolver = null;
		for (String resourcePath : resourcesToSolve) {
			resourceLoaderResolver = new ResourceLoaderResolver(resourcePath);
			allResourcesPaths.addAll(resourceLoaderResolver
					.getAllReferencedResources());
		}
		// all resources that are referenced are known at this point, return
		// them
		return allResourcesPaths;
	}

	// //
	// Resource resolver loader
	// //

	protected class ResourceLoaderResolver {

		protected String baseResourcePath = null;

		protected IPath basePath = null;

		public ResourceLoaderResolver(String basePath) {
			IPath path = new Path(basePath);
			if (path.segmentCount() > 0) {
				path = path.removeLastSegments(1);
			}
			this.basePath = path;
			this.baseResourcePath = basePath;
		}

		public List<String> getAllReferencedResources() {
			// load all resources that are referenced from this resource
			ResourceSet resourceSet = loadAllReferencedResources(baseResourcePath);
			// resolve the path off all those resources that are referenced in a
			// relative way
			String resourcePathString = null;
			IPath resourcePath = null;
			List<String> allResourcesPaths = new ArrayList<String>();
			for (Resource resource : resourceSet.getResources()) {
				resourcePathString = resource.getURI().toString();
				IPath path = new Path(resourcePathString);
				boolean isAbsolute = path.isAbsolute();
				if (isAbsolute) {
					resourcePathString = PathsUtil
							.fromAbsoluteFileSystemToAbsoluteWorkspace(resourcePathString);
				}
				resourcePath = new Path(resourcePathString);
				if (!resourcePath.isAbsolute()) {
					resourcePath = basePath.append(resourcePath);
				}
				allResourcesPaths.add(resourcePath.toString());
				resource.unload();
			}
			resourceSet = null;
			return allResourcesPaths;
		}

		protected ResourceSet loadAllReferencedResources(String basePath) {
			// use a ResourceSet to hold all the Resources to analyze.
			ResourceSet resourceSet = new ResourceSetImpl();
			resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
					.put(Registry.DEFAULT_EXTENSION,
							new XMIResourceFactoryImpl());
			URI resourceUri = null;
			// load all the given resources
			for (String resourcePath : resourcesToSolve) {
				resourceUri = URI.createURI(resourcePath);
				resourceSet.getResource(resourceUri, true);
			}
			// resolve all the given resources iteratively
			int oldSize = 0;
			int newSize = 0;
			int iters = 0; // common sense here
			do {
				oldSize = resourceSet.getResources().size();
				EcoreUtil.resolveAll(resourceSet);
				newSize = resourceSet.getResources().size();
				iters++;
			} while (newSize != oldSize && iters < 10);
			//
			return resourceSet;
		}
	}

}
