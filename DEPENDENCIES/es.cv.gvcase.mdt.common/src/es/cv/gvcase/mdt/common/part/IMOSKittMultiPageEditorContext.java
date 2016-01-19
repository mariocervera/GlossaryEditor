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

import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.ui.IEditorInput;

/**
 * A context for a {@link MOSKittMultiPageEditor}. This context is given to
 * nested {@link CachedResourcesDiagramEditor}s to share the
 * {@link TransactionalEditingDomain} and the
 * {@link CachedResourcesDocumentProvider}.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public interface IMOSKittMultiPageEditorContext {

	/**
	 * Gets the shared {@link TransactionalEditingDomain}.
	 * 
	 * @return
	 */
	TransactionalEditingDomain getEditingDomain();

	/**
	 * Gets the shared {@link CachedResourcesDocumentProvider}
	 * 
	 * @return
	 */
	CachedResourcesDocumentProvider getDocumentsProvider();

	/**
	 * Gets or creates a {@link TransactionalEditingDomain}.
	 * 
	 * @param editingDomainID
	 * @param editorInput
	 * @return
	 */
	TransactionalEditingDomain createEditingDomain(String editingDomainID,
			IEditorInput editorInput);

	/**
	 * Gets or creates a {@link CachedResourcesDocumentProvider}.
	 * 
	 * @param editingDomainID
	 * @param editorInput
	 * @return
	 */
	CachedResourcesDocumentProvider createDocumentProvider(
			String editingDomainID, IEditorInput editorInput);

}
