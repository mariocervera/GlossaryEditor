/*******************************************************************************
 * Copyright (c) 2008, 2009 Conselleria de Infraestructuras y Transporte, Generalitat 
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

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.gmf.runtime.emf.core.resources.GMFResource;

import es.cv.gvcase.ide.navigator.util.NavigatorUtil;
import es.cv.gvcase.ide.navigator.view.MOSKittCommonNavigator;

/**
 * The Class NavigatorRootItem.
 * 
 * @author <a href=mailto:gmerin@prodevelop.es">Gabriel Merin Cubero</a>
 */
public class NavigatorRootItem implements IStructuredItemContentProvider {

	/** The resource. */
	private Resource resource;

	/** The common navigator instance */
	private MOSKittCommonNavigator navigatorView = null;

	/**
	 * Instantiates a new navigator root item.
	 * 
	 * @param res
	 *            the res
	 * @deprecated
	 */
	public NavigatorRootItem(Resource res) {
		resource = res;
	}

	/**
	 * Instantiates a new navigator root item.
	 * 
	 * @param res
	 *            the res
	 */
	public NavigatorRootItem(Resource res, MOSKittCommonNavigator navigator) {
		resource = res;
		this.navigatorView = navigator;
	}

	/**
	 * Gets the resource.
	 * 
	 * @return the resource
	 */
	public Resource getResource() {
		return resource;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.emf.edit.provider.IStructuredItemContentProvider#getElements
	 * (java.lang.Object)
	 */
	public Collection<?> getElements(Object object) {
		if (object instanceof NavigatorRootItem) {
			NavigatorRootItem rootItem = (NavigatorRootItem) object;
			Resource rootRes = rootItem.getResource();

			List<Object> list = new ArrayList<Object>();
			// Add the Resource
			list.add(rootRes);

			// Try to find the GMF Resource associated with the current Editor
			EList<Resource> rList = rootRes.getResourceSet().getResources();
			GMFResource diagramRootResource = null;
			for (Resource res : rList) {
				if (res != rootRes && res instanceof GMFResource
						&& isResourceFromActiveEditor((GMFResource) res)) {
					diagramRootResource = (GMFResource) res;
					list.add(res);
				}
			}

			// Add a new element that is going to contain the rest of loaded
			// resources

			list.add(new NavigatorLoadedResourcesItem(rootRes,
					diagramRootResource));

			return list;
		}
		return Collections.EMPTY_LIST;
	}

	/**
	 * Checks if is resource from active editor.
	 * 
	 * @param gmfResource
	 *            the gmf resource
	 * 
	 * @return true, if is resource from active editor
	 */
	protected boolean isResourceFromActiveEditor(GMFResource gmfResource) {
		Resource diagramResource;
		if (navigatorView == null) {
			diagramResource = NavigatorUtil.getReourceFromEditor(NavigatorUtil
					.getActiveEditor(), null);
		} else {
			diagramResource = NavigatorUtil.getReourceFromEditor(navigatorView
					.getEditorPart(), null);
		}
		if (diagramResource != null) {
			return diagramResource == gmfResource;
		}

		return false;
	}

}
