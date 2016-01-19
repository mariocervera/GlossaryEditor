/*******************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Gabriel Merin Cubero (Integranova) – Initial API 
 * implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.ide.navigator.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.gmf.runtime.emf.core.resources.GMFResource;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * The Class NavigatorLoadedResourcesItem.
 * 
 * @author <a href="mailto:gmerin@prodevelop.es">Gabriel Merin Cubero</a>
 */
public class NavigatorLoadedResourcesItem implements IItemLabelProvider,
		ITreeItemContentProvider {

	/** The resources list. */
	private List<Resource> resourcesList = null;

	private Resource rootResource = null;

	private GMFResource diagramRootResource = null;

	/**
	 * Gets the resources list.
	 * 
	 * @return the resources list
	 */
	public List<Resource> getResourcesList() {
		if (resourcesList == null) {
			EcoreUtil.resolveAll(this.rootResource);
			resourcesList = new ArrayList<Resource>();
			for (Resource resource : rootResource.getResourceSet()
					.getResources()) {
				if (resource != rootResource && resource != diagramRootResource) {
					this.resourcesList.add(resource);
				}
			}
		}

		return resourcesList;
	}

	/**
	 * Instantiates a new navigator loaded resources item.
	 * 
	 * @param rootRes
	 *            the root resource whic may container external cross references
	 */
	public NavigatorLoadedResourcesItem(Resource rootRes) {
		this(rootRes, null);
	}

	/**
	 * Instantiates a new navigator loaded resources item.
	 * 
	 * @param rootRes
	 *            the root resource which may container external cross
	 *            references
	 */
	public NavigatorLoadedResourcesItem(Resource rootRes,
			GMFResource diagramRootResource) {
		rootResource = rootRes;
		this.diagramRootResource = diagramRootResource;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.emf.edit.provider.IItemLabelProvider#getImage(java.lang.Object
	 * )
	 */
	public Object getImage(Object object) {

		ISharedImages sImages = PlatformUI.getWorkbench().getSharedImages();
		Image img;
		// if (resourcesList == null || resourcesList.size() == 0)
		if (hasChildren(object))
			img = sImages.getImage(ISharedImages.IMG_TOOL_COPY);
		else
			img = sImages.getImage(ISharedImages.IMG_TOOL_COPY_DISABLED);
		return img;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.emf.edit.provider.IItemLabelProvider#getText(java.lang.Object
	 * )
	 */
	public String getText(Object object) {
		if (object instanceof NavigatorLoadedResourcesItem
				&& (!hasChildren(object))) {
			return "No Resources Loaded";
		} else {
			return "Loaded Resources:";
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.emf.edit.provider.ITreeItemContentProvider#getParent(java
	 * .lang.Object)
	 */
	public Object getParent(Object element) {
		// Extracted from: org.eclipse.emf.edit.provider.ItemProviderAdapter
		if (element instanceof EObject) {
			EObject eObject = (EObject) element;
			Object result = eObject.eContainer();
			if (result == null) {
				result = eObject.eResource();
			}
			return result;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.emf.edit.provider.ITreeItemContentProvider#hasChildren(java
	 * .lang.Object)
	 */
	public boolean hasChildren(Object element) {
		return element instanceof NavigatorLoadedResourcesItem
				&& getChildren(element).size() > 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.emf.edit.provider.ITreeItemContentProvider#getChildren(java
	 * .lang.Object)
	 */
	public Collection<?> getChildren(Object object) {
		if (object instanceof NavigatorLoadedResourcesItem) {
			return getResourcesList();
		}

		return Collections.emptyList();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.emf.edit.provider.IStructuredItemContentProvider#getElements
	 * (java.lang.Object)
	 */
	public Collection<?> getElements(Object object) {
		// Extracted from: org.eclipse.emf.edit.provider.ItemProviderAdapter
		return getChildren(object);
	}

}
