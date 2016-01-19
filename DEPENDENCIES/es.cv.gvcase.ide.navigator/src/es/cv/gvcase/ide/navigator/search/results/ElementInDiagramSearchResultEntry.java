/***************************************************************************
 * Copyright (c) 2008, 2009 Conselleria de Infraestructuras y Transporte,
 * Generalitat de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – initial api implementation
 *
 ******************************************************************************/
package es.cv.gvcase.ide.navigator.search.results;

import java.io.File;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.search.core.results.AbstractModelSearchResultEntry;
import org.eclipse.emf.search.core.results.IModelResultEntry;

/**
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 *
 */
public class ElementInDiagramSearchResultEntry extends
		AbstractModelSearchResultEntry {

	public ElementInDiagramSearchResultEntry(IModelResultEntry p,
			Object object, Object source, boolean m) {
		super(p, object, source, m);
	}

	@Override
	public String getModelResultFullyQualifiedName() {
		String txt = ""; //$NON-NLS-1$
		if (getParent() instanceof IModelResultEntry) {
			for (IModelResultEntry e : getHierarchyFromRootToLeaf()) {
				if (e != null && e.getSource() instanceof ENamedElement) {
					txt = ((ENamedElement)e.getSource()).getName() + "." + txt; //$NON-NLS-1$
				}
			}
		}
		return txt.endsWith(".")?txt.substring(0, txt.lastIndexOf(".")):txt; //$NON-NLS-1$ //$NON-NLS-2$
	}

	public Object getFile() {
		if (target instanceof IAdaptable) {
		    return ((IAdaptable)target).getAdapter(File.class);
		}
		return null;
	}

	public boolean isExcluded() {
		return false;
	}

}
