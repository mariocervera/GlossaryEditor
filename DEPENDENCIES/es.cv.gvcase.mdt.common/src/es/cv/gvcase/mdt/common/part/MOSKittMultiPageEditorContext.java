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

import es.cv.gvcase.emf.common.part.EditingDomainRegistry;

/**
 * A basic implementation of {@link IMOSKittMultiPageEditorContext} for its use
 * in {@link MOSKittMultiPageEditor}.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public class MOSKittMultiPageEditorContext implements
		IMOSKittMultiPageEditorContext {

	// //
	// MultiPageEditor pointer.
	// //

	private MOSKittMultiPageEditor multiPageEditor = null;

	public MOSKittMultiPageEditorContext(MOSKittMultiPageEditor editor) {
		multiPageEditor = editor;
	}

	public MOSKittMultiPageEditor getMultiPageEditor() {
		return multiPageEditor;
	}

	// //
	// Editing Domain
	// //

	private TransactionalEditingDomain editingDomain = null;

	public TransactionalEditingDomain getEditingDomain() {
		return editingDomain;
	}

	public TransactionalEditingDomain createEditingDomain(
			String editingDomainID, IEditorInput editorInput) {
		/*
		 * If there is no TransactionalEditingDomain then create one with the
		 * EditingDomainRegistry. if there is an already existing
		 * TransactionalEditingDomain, register the existing editing domain for
		 * the new input in the EditingDomainRegistry.
		 */

		// check if there is an editing domain
		TransactionalEditingDomain domain = getEditingDomain();
		if (domain == null) {
			// there is no editing domain yet.
			// ask for one and return it
			domain = EditingDomainRegistry.getInstance().get(editingDomainID,
					editorInput, DiagramDomainSearchStrategy.getInstance());
			// add a resource set listener do that we can listen to diagrams
			// being created and deleted and add or remove their pages.
			editingDomain = domain;
			return domain;
		} else {
			// there is an already existing editing domain.
			// register it for the new input (if not already) and return it.
			if (!EditingDomainRegistry.getInstance().contains(editingDomainID,
					editorInput)) {
				EditingDomainRegistry.getInstance().put(editingDomainID,
						editorInput, domain);
			}
			return domain;
		}
	}

	// //
	// DocumentProvider
	// //

	private CachedResourcesDocumentProvider documentsProvider = null;

	public CachedResourcesDocumentProvider getDocumentsProvider() {
		return documentsProvider;
	}

	public CachedResourcesDocumentProvider createDocumentProvider(
			String editingDomainID, IEditorInput editorInput) {
		/*
		 * if there is no document provider create one with the registry. if
		 * there is a document provider, register the existing document provider
		 * for the new input.
		 */

		// check if there is a documents provider
		CachedResourcesDocumentProvider provider = getDocumentsProvider();
		if (provider == null) {
			// there is no documents provider, ask for a new one and return it.
			provider = DocumentProviderRegistry.getInstance().get(
					editingDomainID, editorInput);
			provider.setMultiPageEditorContext(this);
			documentsProvider = provider;
			return provider;
		} else {
			// there is an already existing documents provider.
			// register it for the new input (if not already) and return it.
			if (!DocumentProviderRegistry.getInstance().containsKey(
					editingDomainID, editorInput)) {
				DocumentProviderRegistry.getInstance().put(editingDomainID,
						editorInput, provider);
			}
			return provider;
		}
	}

}
