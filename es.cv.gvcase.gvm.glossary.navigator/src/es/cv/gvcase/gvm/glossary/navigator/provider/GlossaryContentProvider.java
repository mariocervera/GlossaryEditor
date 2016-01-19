/*******************************************************************************
 * Copyright (c) 2009 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Javier Muñoz Ferrara (Prodevelop) – initial API and
 * implementation
 *
 ******************************************************************************/
package es.cv.gvcase.gvm.glossary.navigator.provider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;

import es.cv.gvcase.gvm.glossary.Glossary;
import es.cv.gvcase.gvm.glossary.provider.GlossaryItemProviderAdapterFactory;
import es.cv.gvcase.ide.navigator.provider.DiagramItemProviderAdapterFactory;
import es.cv.gvcase.ide.navigator.provider.MOSKittCommonContentProvider;
import es.cv.gvcase.ide.navigator.util.NavigatorUtil;
import es.cv.gvcase.ide.navigator.view.MOSKittCommonNavigator;

public class GlossaryContentProvider extends MOSKittCommonContentProvider {

	/** The resource factories. */
	protected static Map<String, Object> resourceFactories = null;

	/** The ecore. */
	private final String ModelExtension = "glossary";

	/** The Model id. */
	private final String ModelID = "Glossary";

	/** The Editor id. */
	private final String EditorID = "es.cv.gvcase.gvm.glossary.formseditor.editors.GlossaryMultiPageEditor";

	public GlossaryContentProvider() {
		// Common factories
		addFactory(new ResourceItemProviderAdapterFactory());
		//addFactory(new DiagramItemProviderAdapterFactory());
		addFactory(new GlossaryItemProviderAdapterFactory(), true);

		// resource factories
		if (resourceFactories == null) {
			resourceFactories = new HashMap<String, Object>();
			resourceFactories.put(ModelExtension, XMIResourceFactoryImpl.class);
			resourceFactories.put("ecore", ResourceFactoryImpl.class);
		}

		// model to editor mapping
		//NavigatorUtil.MOSKittModelIDs.getModelToEditorMap().put(ModelID, EditorID);
	}

	@Override
	protected boolean canHandleResource(Resource resource) {
		if (resource != null) {
			List<EObject> contents = resource.getContents();
			if (contents.size() > 0
					&& contents.get(0) instanceof Glossary) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getContributorID() {
		return "es.cv.gvcase.gvm.glossary.properties";
	}

	@Override
	protected Map<String, Object> getResourceFactories() {
		return resourceFactories;
	}

	@Override
	protected String getViewerID() {
		return contentExtensionSite.getExtensionStateModel().getViewerId();
	}

	@Override
	public boolean hasPropertySheetPage() {
		Resource resource = getResourceFromEditor();
		return canHandleResource(resource);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * es.cv.gvcase.ide.navigator.provider.MOSKittCommonContentProvider#init
	 * (org.eclipse.ui.navigator.ICommonContentExtensionSite)
	 */
	@Override
	public void init(ICommonContentExtensionSite config) {
		super.init(config);
		MOSKittCommonNavigator.addPropertySheetContributor(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * es.cv.gvcase.ide.navigator.provider.MOSKittCommonContentProvider#dispose
	 * ()
	 */
	@Override
	public void dispose() {
		super.dispose();
		MOSKittCommonNavigator.removePropertySheetContributor(this);
	}

}
