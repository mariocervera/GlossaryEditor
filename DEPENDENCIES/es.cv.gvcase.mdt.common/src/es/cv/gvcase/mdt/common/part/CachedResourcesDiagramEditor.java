/*******************************************************************************
 * Copyright (c) 2008, 2009 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – Initial implementation.
 * 				 Marc Gil Sendra (Prodevelop) - add listeners to domain
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.part;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.commands.operations.ObjectUndoContext;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.ui.URIEditorInput;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.UpdateAction;
import org.eclipse.gmf.runtime.diagram.ui.resources.editor.document.IDocumentProvider;
import org.eclipse.gmf.runtime.diagram.ui.resources.editor.parts.DiagramDocumentEditor;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.FileEditorInput;

import es.cv.gvcase.emf.common.part.EditingDomainRegistry;
import es.cv.gvcase.emf.common.part.IEditingDomainRegistrable;
import es.cv.gvcase.emf.common.util.PathsUtil;
import es.cv.gvcase.mdt.common.listeners.DomainListenersRegistry;
import es.cv.gvcase.mdt.common.migrations.DiagramMigrationRegistry;
import es.cv.gvcase.mdt.common.migrations.MigratorService;
import es.cv.gvcase.mdt.common.migrations.MigratorVersionComparator;
import es.cv.gvcase.mdt.common.storage.store.EditorStorageApplicable;
import es.cv.gvcase.mdt.common.storage.store.IStorageApplicable;
import es.cv.gvcase.mdt.common.util.MDTUtil;

/**
 * The Class CachedResourcesDiagramEditor.
 */
public abstract class CachedResourcesDiagramEditor extends
		DiagramDocumentEditor implements IEditingDomainRegistrable,
		IEditingDomainProvider {

	/**
	 * Editor context to use when this editor is nested inside a
	 * {@link MOSKittMultiPageEditor}.
	 */
	IMOSKittMultiPageEditorContext multiPageEditorContext = null;

	/**
	 * Getter for the ,ulti page editor context when this editor is nested in a
	 * {@link MOSKittMultiPageEditor}.
	 * 
	 * @return
	 */
	protected IMOSKittMultiPageEditorContext getMultiPageEditorContext() {
		return multiPageEditorContext;
	}

	/**
	 * Instantiates a new cached resources diagram editor.
	 * 
	 * @param hasFlyoutPalette
	 *            the has flyout palette
	 */
	public CachedResourcesDiagramEditor(boolean hasFlyoutPalette) {
		super(hasFlyoutPalette);
	}

	protected IDocumentProvider cachedDocumentProvider = null;

	/**
	 * Tries to retrieve the DocuemtnProvider from the multi page editor contex.
	 */
	@Override
	public IDocumentProvider getDocumentProvider() {
		if (cachedDocumentProvider != null) {
			return cachedDocumentProvider;
		}
		if (getMultiPageEditorContext() != null) {
			cachedDocumentProvider = getMultiPageEditorContext()
					.createDocumentProvider(getEditingDomainID(),
							getAnEditorInput());
			return cachedDocumentProvider;
		}
		return super.getDocumentProvider();
	}

	/**
	 * Gets the cached resources document provider.
	 * 
	 * @return the cached resources document provider
	 */
	protected CachedResourcesDocumentProvider getCachedResourcesDocumentProvider() {
		if (getDocumentProvider() != null
				&& getDocumentProvider() instanceof CachedResourcesDocumentProvider == true) {
			return (CachedResourcesDocumentProvider) getDocumentProvider();
		}
		return null;
	}

	/**
	 * Tries to retrieve the editing domain from the multi page editor context.
	 */
	@Override
	protected TransactionalEditingDomain createEditingDomain() {
		String editingDomainID = getEditingDomainID();
		IEditorInput editorInput = getAnEditorInput();
		// if we have a IMOSKittMultiPageEditorContext, get the editing domain
		// from it
		if (getMultiPageEditorContext() != null) {
			return getMultiPageEditorContext().createEditingDomain(
					editingDomainID, editorInput);
		}
		// otherwise, get one form the editing domain registry.
		TransactionalEditingDomain editingDomain = EditingDomainRegistry
				.getInstance().get(editingDomainID, editorInput);
		if (editingDomain != null) {
			return editingDomain;
		}
		// if the registry cannot provide an editing domain, ask the default
		// method
		return super.createEditingDomain();
	}

	/**
	 * We want the EditingDomainRegistry to listen to editors closing. At each
	 * initialization we add the EditingDomainRegistry as a PartListener.
	 * 
	 * @param site
	 *            the site
	 * @param input
	 *            the input
	 * 
	 * @throws PartInitException
	 *             the part init exception
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		// If this editor is nested inside a multi page editor then a
		// MOSKittMultiPageEditorContext will be provided.
		MOSKittMultiPageEditorSite multiPageSite = (MOSKittMultiPageEditorSite) Platform
				.getAdapterManager().getAdapter(site,
						MOSKittMultiPageEditorSite.class);
		if (multiPageSite != null && multiPageSite.getEditorContext() != null) {
			multiPageEditorContext = multiPageSite.getEditorContext();
		}
		// perform default initialization.
		super.init(site, input);
		// make sure the EditingDomainRegistry is listening to the life cycle of
		// IWorkbenchParts.
		site.getPage().addPartListener(EditingDomainRegistry.getInstance());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.gmf.runtime.diagram.ui.resources.editor.parts.
	 * DiagramDocumentEditor#doSetInput(org.eclipse.ui.IEditorInput, boolean)
	 */
	@Override
	public void doSetInput(IEditorInput input, boolean releaseEditorContents)
			throws CoreException {
		if (getEditingDomain() != null)
			removeListeners();

		IEditorInput editorInput = getEditorInput();

		if (editorInput == null) {
			// check if theres is a previously opened diagram set as a property
			// and open that diagram instead
			if (!(input instanceof URIEditorInput)
					|| !((URIEditorInput) input).getURI().hasFragment()) {
				String filePath = PathsUtil
						.getRelativeWorkspaceFromEditorInput(input);
				String diagramFragment = MDTUtil
						.getLastOpenedDiagramPropertyForEditor(filePath,
								getEditorID());
				if (diagramFragment != null) {
					URI uri = URI.createURI(filePath).appendFragment(
							diagramFragment);
					// make sure the input is a good one; if not, get a good
					// one.
					input = MDTUtil.getValidEditorInput(uri,
							getEditingDomainID());
				}
			}
		}

		if (checkSameEditorInput(input, editorInput)) {
			return;
		}
		CachedResourcesDocumentProvider documentProvider = getCachedResourcesDocumentProvider();
		if (documentProvider != null) {
			if (input instanceof CachedResourcesEditorInput) {
				if (((CachedResourcesEditorInput) input).isUnload()) {
					documentProvider.setUnloadOnDispose(true);
					removeEditingDomainFromRegistry();
				} else {
					documentProvider.setUnloadOnDispose(false);
				}
			} else {
				documentProvider.setUnloadOnDispose(true);
				// removeEditingDomainFromRegistry();
			}
		}
		//
		super.doSetInput(input, releaseEditorContents);
		//
		addListeners();

	}

	/**
	 * Check same editor input.
	 * 
	 * @param input1
	 *            the input1
	 * @param input2
	 *            the input2
	 * 
	 * @return true, if successful
	 */
	protected boolean checkSameEditorInput(IEditorInput input1,
			IEditorInput input2) {
		String uri1 = "", uri2 = "";
		if (input1 instanceof FileEditorInput) {
			uri1 = ((FileEditorInput) input1).getURI().toString();
		}
		if (input1 instanceof URIEditorInput) {
			uri1 = ((URIEditorInput) input1).getURI().toString();
		}
		if (input2 instanceof FileEditorInput) {
			uri2 = ((FileEditorInput) input2).getURI().toString();
		}
		if (input2 instanceof URIEditorInput) {
			uri2 = ((URIEditorInput) input2).getURI().toString();
		}
		if (uri1.equals(uri2)) {
			return true;
		}

		return false;
	}

	/**
	 * Editor input stored before superclasses make it available via
	 * getEditorInput().
	 */
	IEditorInput newEditorInput = null;

	/**
	 * Editor input stored before superclasses make it available via
	 * getEditorInput().
	 * 
	 * @return
	 */
	private IEditorInput getNewEditorInput() {
		return newEditorInput;
	}

	/**
	 * Returns the editor input or the actual editor input or the new editor
	 * input, if the actual one is null.
	 * 
	 * @return
	 */
	protected IEditorInput getAnEditorInput() {
		IEditorInput editorInput = getEditorInput();
		if (editorInput == null) {
			editorInput = getNewEditorInput();
		}
		return editorInput;
	}

	/**
	 * Stores the new editor input before super classes make it available via
	 * getEditorInput().
	 */
	@Override
	public void setInput(IEditorInput input) {
		newEditorInput = input;

		// mgil: migrate diagrams
		if (input instanceof CachedResourcesEditorInput) {
			CachedResourcesEditorInput crei = (CachedResourcesEditorInput) input;
			if (!crei.isMigrated()) {
				Diagram diagram = MDTUtil.getDiagramFromEditorInput(input,
						getEditingDomain());
				if (diagram != null) {
					checkDiagramToOpen(diagram);
				}
			}
		}
		// mgil: end migrate diagrams

		super.setInput(input);
	}

	/**
	 * Check if there exists some migrator for the diagram, and execute them
	 * 
	 * @author mgil
	 * @param input
	 */
	protected void checkDiagramToOpen(Diagram diagram) {
		// read the migrators
		DiagramMigrationRegistry migrationRegistry = DiagramMigrationRegistry
				.getInstance();
		if (migrationRegistry == null) {
			return;
		}

		// compute all the migrators
		List<MigratorService> migrators = migrationRegistry.parseMigrators();
		List<MigratorService> validMigrators = new ArrayList<MigratorService>();
		for (MigratorService migrator : migrators) {
			if (migrator.provides(diagram)) {
				validMigrators.add(migrator);
			}
		}

		// if no valid migrators for this diagram, do nothing
		if (validMigrators.size() == 0) {
			return;
		}

		// sort the migrators by version (alphabetically ascendant way)
		Collections.sort(validMigrators, new MigratorVersionComparator());

		// if valid migrators founded, execute them. First, ask user if wants to
		// execute the migration...
		String title = "Diagram Migrations";
		String message = "This diagram needs to be migrated to work correctly. Following migrations should be applied:\n";
		for (MigratorService migrator : validMigrators) {
			String name = migrator.getLabel();
			message += "\t- " + name + "\n";
		}
		message += "Do you want to migrate?";
		boolean answer = MessageDialog
				.openQuestion(new Shell(), title, message);

		if (!answer) {
			return;
		}

		// ... and finally execute them. Check if we have an editing domain, to
		// execute the migration with a read/write transactions, or without it
		if (getEditingDomain() != null) {
			for (MigratorService migrator : validMigrators) {
				if (!migrator
						.executeMigrationWithTransaction(getEditingDomain())) {
					break;
				}
			}
		} else {
			for (MigratorService migrator : validMigrators) {
				if (!migrator.executeMigrationWithoutTransaction()) {
					break;
				}
			}
		}
	}

	/**
	 * Removes the editing domain from registry.
	 */
	protected void removeEditingDomainFromRegistry() {
		CachedResourcesDocumentProvider documentProvider = getCachedResourcesDocumentProvider();
		if (documentProvider != null) {
			EditingDomainRegistry.getInstance().remove(
					documentProvider.getEditingDomainID());
		}
	}

	/**
	 * @see org.eclipse.gmf.runtime.diagram.ui.resources.editor.parts.
	 *      DiagramDocumentEditor#close(boolean)
	 */
	@Override
	public void close(boolean save) {
		CachedResourcesDocumentProvider documentProvider = getCachedResourcesDocumentProvider();
		if (documentProvider != null) {
			documentProvider.setUnloadOnDispose(false);
			// fjcano : the EditingDomainRegistriy takes care of disposing
			// unused EditingDomains.
			// removeEditingDomainFromRegistry();
		}
		super.close(save);
	}

	/**
	 * Close but unload.
	 * 
	 * @param save
	 *            the save
	 */
	public void closeButUnload(boolean save) {
		CachedResourcesDocumentProvider documentProvider = getCachedResourcesDocumentProvider();
		if (documentProvider != null) {
			documentProvider.setUnloadOnDispose(false);
		}
		super.close(save);
	}

	/**
	 * Gets the editor id.
	 * 
	 * @return the editor id
	 */
	public abstract String getEditorID();

	/**
	 * Sets the unload on dispose.
	 * 
	 * @param unload
	 *            the new unload on dispose
	 */
	public void setUnloadOnDispose(boolean unload) {
		IDocumentProvider documentProvider = getDocumentProvider();
		if (documentProvider instanceof CachedResourcesDocumentProvider) {
			((CachedResourcesDocumentProvider) documentProvider)
					.setUnloadOnDispose(unload);
		}
	}

	/**
	 * Gets the URI from the editor input.
	 * 
	 * @param input
	 *            the input
	 * 
	 * @return the URI from i editor input
	 */
	public String getEditingDomainResourceURI() {
		IEditorInput input = getEditorInput();
		String uri = PathsUtil.fromEditorInputToURIString(input);
		return uri;
	}

	/**
	 * Gets the ID from editor.
	 * 
	 * @param editor
	 *            the editor
	 * 
	 * @return the ID from editor
	 */
	@Override
	public abstract String getEditingDomainID();

	/**
	 * Give the editing domain used in the context if any.
	 */
	@Override
	public TransactionalEditingDomain getEditingDomain() {
		if (multiPageEditorContext != null) {
			return multiPageEditorContext.getEditingDomain();
		}
		return super.getEditingDomain();
	}

	/**
	 * Adapts to EditingDomain.class returning the EditingDomain this editor is
	 * using.
	 */
	@Override
	public Object getAdapter(Class type) {
		if (EditingDomain.class.equals(type)) {
			return getEditingDomain();
		}
		if (TransactionalEditingDomain.class.equals(type)) {
			return getEditingDomain();
		}
		if (Diagram.class.equals(type)) {
			return getDiagram();
		}
		if (IEditingDomainProvider.class.equals(type)) {
			return this;
		}
		if (IEditorActionBarContributor.class.equals(type)) {
			return getActionBarContributor();
		}
		if (IStorageApplicable.class.equals(type)) {
			return new EditorStorageApplicable(this);
		}
		return super.getAdapter(type);
	}

	/**
	 * Returns the actionbar contributor for this editor. If in a
	 * MultiPageEditor, the ActionBar will be shared among all the editors in
	 * the MultiPageEditor.
	 * 
	 * @return
	 */
	protected IEditorActionBarContributor getActionBarContributor() {
		return null;
	}

	/**
	 * Overriden to update this editor actions even it this editor is nested
	 * inside a {@link MultiPageEditorPart}.
	 */
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		super.selectionChanged(part, selection);
		IEditorPart activeNestedEditor = MDTUtil.getActiveNestedEditor();
		if (this.equals(activeNestedEditor)) {
			updateActions();
		}
	}

	/**
	 * Forces the update of all actions of type {@link UpdateAction}.
	 */
	protected void updateActions() {
		ActionRegistry registry = getActionRegistry();
		Iterator iterator = registry.getActions();
		while (iterator.hasNext()) {
			Object object = iterator.next();
			if (object instanceof UpdateAction) {
				((UpdateAction) object).update();
			}
		}
	}

	private boolean disposed = false;

	@Override
	public void dispose() {
		if (disposed) {
			return;
		}
		disposed = true;
		removeListeners();
		super.dispose();
	}

	/**
	 * Add listeners to the created editing domain declared through extension
	 * points "es.cv.gvcase.mdt.common.listenerToDomain"
	 */
	protected void addListeners() {
		DomainListenersRegistry.getInstance().addListeners(getEditingDomain());
	}

	/**
	 * Remove listeners to the editing domain declared through extension points
	 * "es.cv.gvcase.mdt.common.listenerToDomain"
	 */
	protected void removeListeners() {
		DomainListenersRegistry.getInstance().removeListeners(
				getEditingDomain());
	}

	// //
	// Undo Context
	// //

	protected IUndoContext editorUndoContext = null;

	/**
	 * Returns the undo context for this editor. It is an
	 * {@link ObjectUndoContext} referencing this editor.
	 */
	@Override
	protected IUndoContext getUndoContext() {
		if (editorUndoContext == null) {
			editorUndoContext = new ObjectUndoContext(this);
		}
		return editorUndoContext;
	}

	/**
	 * Because of the Editing Domain being shared among all open instances of
	 * MOSKitt editors, either GMF or FEFEM, this method must only return true
	 * if I am the current active editor in MOSKitt. This ensures that the only
	 * IUndoContext coming from MOSKitt editors that is added to the operation
	 * is the one from the current active editor, so that on an undo operation,
	 * only the operations performed from this editor can be undone.
	 */
	@Override
	protected boolean shouldAddUndoContext(IUndoableOperation operation) {
		// find the current active editor in MOSKitt.
		IEditorPart activeEditor = MDTUtil.getActiveNestedEditor();
		// compare the active editor with this editor
		if (activeEditor != null && activeEditor.equals(this)) {
			// same editor, the IUndoContext must be added.
			return true;
		} else {
			// different editor, the IUndoContext must not be added.
			return false;
		}
	}

}
