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
package es.cv.gvcase.emf.common.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMLParserPoolImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;
import org.eclipse.emf.edit.domain.EditingDomain;

import es.cv.gvcase.emf.common.Activator;

/**
 * Holder class for several utility methods for use with {@link Resource}s.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public class ResourceUtil {

	/**
	 * Gets the default save options.
	 * 
	 * @return the save options
	 */
	public static Map getSaveOptions() {
		Map saveOptions = new HashMap();
		saveOptions.put(XMLResource.OPTION_ENCODING, "UTF-8"); //$NON-NLS-1$
		saveOptions.put(Resource.OPTION_SAVE_ONLY_IF_CHANGED,
				Resource.OPTION_SAVE_ONLY_IF_CHANGED_MEMORY_BUFFER);
		return saveOptions;
	}

	/**
	 * Loads a {@link Resource} specified by the given {@link URI} into the
	 * given {@link EditingDomain}. <br>
	 * If the Resource is already loaded, the resource is unloaded and reloaded
	 * again.
	 * 
	 * @param editingDomain
	 * @param resourceUri
	 * @return
	 */
	public static Resource loadFreshResource(EditingDomain editingDomain,
			URI resourceUri) {
		Resource mResource = editingDomain.getResourceSet().getResource(
				resourceUri, false);
		if (mResource == null || mResource.getContents().isEmpty()) {
			mResource = editingDomain.loadResource(resourceUri.toString());
		} else {
			mResource.unload();
			try {
				mResource.load(getSaveOptions());
			} catch (IOException ex) {
				Activator.getDefault().logError(
						"Error loading dashboard model", ex);
				mResource = null;
			}
		}
		return mResource;
	}

	/**
	 * Loads a {@link Resource} with load options that optimize the loading of
	 * the resource. <br />
	 * Use only with resources that use an UUID to identify their elements.
	 * Using this method to load a Resource will increase the memory usage.
	 */
	public static Resource loadResourceFastOptions(URI uri,
			ResourceSet resourceSet) {
		if (uri == null || resourceSet == null) {
			return null;
		}
		// The resource may already be loaded in the ResourceSet.
		Resource resourceToLoad = resourceSet.getResource(uri, false);
		if (resourceToLoad == null) {
			resourceToLoad = resourceSet.createResource(uri);
		}
		if (!resourceToLoad.isLoaded()) {
			// In case it is not loaded, load it with fast loading options.
			Map<Object, Object> loadOptions = ((XMLResourceImpl) resourceToLoad)
					.getDefaultLoadOptions();
			// These options allow for a faster loading, albeit at using more
			// memory.
			loadOptions.put(XMLResource.OPTION_DEFER_ATTACHMENT, Boolean.TRUE);
			loadOptions.put(XMLResource.OPTION_DEFER_IDREF_RESOLUTION,
					Boolean.TRUE);
			loadOptions.put(XMLResource.OPTION_USE_DEPRECATED_METHODS,
					Boolean.TRUE);
			loadOptions.put(XMLResource.OPTION_USE_PARSER_POOL,
					new XMLParserPoolImpl(true));
			loadOptions.put(XMLResource.OPTION_USE_XML_NAME_TO_FEATURE_MAP,
					new HashMap());
			// This map is safe to use when using XMI resources.
			((ResourceImpl) resourceToLoad)
					.setIntrinsicIDToEObjectMap(new HashMap());
			try {
				// Load the resource using the fast loading options.
				resourceToLoad.load(loadOptions);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		//
		return resourceToLoad;
	}

}
