/*******************************************************************************
 * Copyright (c) 2010 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana. All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Integranova) - initial API implementation
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.composites;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.swt.widgets.Composite;

import es.cv.gvcase.emf.common.part.ExtensionPointParser;

/**
 * A registry to collect all {@link IDetailsCompositeProvider}s that can provide
 * a detailed composite for an element or attribute of an element.
 * 
 * @author <a href="mailto:fjcano@prodeelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public class DetailsCompositeProviderRegistry {

	// //
	// Holder class
	// //

	public class DetailsCompositeProvider {
		public String id;
		public String label;
		public Object providerClass;
	}

	// //
	// Singleton
	// //

	private static DetailsCompositeProviderRegistry INSTANCE = null;

	public static DetailsCompositeProviderRegistry getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new DetailsCompositeProviderRegistry();
		}
		return INSTANCE;
	}

	private DetailsCompositeProviderRegistry() {
		// O_o
	}

	// //
	// read extension point
	// //

	protected Map<String, IDetailsCompositeProvider> mapId2DetailsComposite;

	private static final String ExtensionPointID = "es.cv.gvcase.mdt.common.detailsCompositeProvider";

	protected void readExtensionPoint() {
		if (mapId2DetailsComposite == null) {
			mapId2DetailsComposite = new HashMap<String, IDetailsCompositeProvider>();
		}
		mapId2DetailsComposite.clear();
		//
		ExtensionPointParser parser = new ExtensionPointParser(
				ExtensionPointID,
				new Class[] { DetailsCompositeProvider.class }, this);
		for (Object o : parser.parseExtensionPoint()) {
			if (o instanceof DetailsCompositeProvider) {
				DetailsCompositeProvider provider = (DetailsCompositeProvider) o;
				if (provider.providerClass instanceof IDetailsCompositeProvider)
					mapId2DetailsComposite.put(provider.id,
							(IDetailsCompositeProvider) provider.providerClass);
			}
		}
	}

	// //
	// utility methods
	// //

	/**
	 * Provides the {@link IDetailsCompositeProvider} for a given element.
	 */
	public IDetailsCompositeProvider getProviderFor(Object object) {
		if (object != null) {
			readExtensionPoint();
			for (IDetailsCompositeProvider provider : mapId2DetailsComposite
					.values()) {
				if (provider.providesFor(object)) {
					return provider;
				}
			}
		}
		return null;
	}

	/**
	 * Provides a {@link DetailComposite} for a given element if any is
	 * available.
	 * 
	 * @param object
	 * @param parent
	 * @param style
	 * @param eObject
	 * @param domain
	 * @return
	 */
	public DetailComposite getCompositeFor(Object object, Composite parent,
			int style, EObject eObject, EditingDomain domain) {
		IDetailsCompositeProvider provider = getProviderFor(object);
		if (provider != null) {
			return provider.provideFor(object, parent, style, eObject, domain);
		}
		return null;
	}

	/**
	 * Provides all {@link IDetailsCompositeProvider}s available.
	 * 
	 * @return
	 */
	public Collection<IDetailsCompositeProvider> getAllProviders() {
		readExtensionPoint();
		return mapId2DetailsComposite.values();
	}

}
