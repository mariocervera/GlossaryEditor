/*******************************************************************************
 * Copyright (c) 2009 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – Initial implementation
 * 				 Marc Gil Sendra (Prodevelop) - Add image to the page
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.part;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.draw2d.IFigure;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.ui.URIEditorInput;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.emf.transaction.ResourceSetChangeEvent;
import org.eclipse.emf.transaction.ResourceSetListener;
import org.eclipse.emf.transaction.ResourceSetListenerImpl;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.common.ui.action.actions.global.GlobalRedoAction;
import org.eclipse.gmf.runtime.common.ui.action.actions.global.GlobalUndoAction;
import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.parts.IDiagramEditDomain;
import org.eclipse.gmf.runtime.diagram.ui.parts.IDiagramGraphicalViewer;
import org.eclipse.gmf.runtime.diagram.ui.parts.IDiagramWorkbenchPart;
import org.eclipse.gmf.runtime.draw2d.ui.figures.WrappingLabel;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.NotationPackage;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.ISaveablePart;
import org.eclipse.ui.ISaveablePart2;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.LabelRetargetAction;
import org.eclipse.ui.internal.EditorActionBars;
import org.eclipse.ui.internal.EditorSite;
import org.eclipse.ui.operations.RedoActionHandler;
import org.eclipse.ui.operations.UndoActionHandler;
import org.eclipse.ui.views.contentoutline.ContentOutline;

import es.cv.gvcase.emf.common.part.EditingDomainRegistry;
import es.cv.gvcase.emf.common.util.PathsUtil;
import es.cv.gvcase.mdt.common.Activator;
import es.cv.gvcase.mdt.common.ids.MOSKittEditorIDs;
import es.cv.gvcase.mdt.common.internal.Messages;
import es.cv.gvcase.mdt.common.migrations.DiagramMigrationRegistry;
import es.cv.gvcase.mdt.common.migrations.MigratorService;
import es.cv.gvcase.mdt.common.migrations.MigratorVersionComparator;
import es.cv.gvcase.mdt.common.preferences.MOSKittPreferenceConstants;
import es.cv.gvcase.mdt.common.provider.IMOSKittEditorFactory;
import es.cv.gvcase.mdt.common.provider.MOSKittEditorFactoryRegistry;
import es.cv.gvcase.mdt.common.runnable.HookRunner;
import es.cv.gvcase.mdt.common.storage.store.IStorageApplicable;
import es.cv.gvcase.mdt.common.util.DiagramEditPartsUtil;
import es.cv.gvcase.mdt.common.util.MDTUtil;
import es.cv.gvcase.mdt.common.util.MultiDiagramUtil;

/**
 * A MultiPage editor for MOSKitt editors. <br>
 * It can hold diagram editors based on GMF. By default it will open all
 * diagrams in the given {@link IEditorInput}, each in a different page.
 * Creating or removing {@link Diagram}s from the {@link ResourceSet} makes
 * pages in the editor to be added or removed.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public class MOSKittMultiPageEditor extends MultiPageEditorPart implements
		IPartListener2, IDiagramWorkbenchPart, ISaveablePart2,
		es.cv.gvcase.emf.common.part.IEditingDomainRegistrable {

	/**
	 * Added an identifier for the MOSKittMultiPageEditor
	 */
	public static final String MOSKittMultiPageEditorID = "es.cv.gvcase.mdt.common.EditorSite.SharedIdentifier"; //$NON-NLS-1$

	// // context for this MultiPageEditor. It controls transactional editing
	// domains and documents providers.
	IMOSKittMultiPageEditorContext multiPageEditorContext = null;

	// EditPart that is being created in addPageForElement()
	IEditorPart creatingEditorPart = null;

	private boolean migrated = false;

	/**
	 * Retrieves the main resource that contains the diagrams this editor is
	 * editing.
	 * 
	 * @return
	 */
	public Resource getMainDiagramResource() {
		IEditorInput input = getEditorInput();
		TransactionalEditingDomain domain = getMultiPageEditorContext()
				.getEditingDomain();
		if (input != null && domain != null) {
			String uriString = es.cv.gvcase.emf.common.util.PathsUtil
					.fromEditorInputToURIString(input);
			URI inputURI = URI.createURI(uriString);
			return domain.getResourceSet().getResource(inputURI, false);
		}
		return null;
	}

	protected IMOSKittMultiPageEditorContext getMultiPageEditorContext() {
		return multiPageEditorContext;
	}

	protected void setMultiPageEditorContext(
			IMOSKittMultiPageEditorContext multiPageEditorContext) {
		this.multiPageEditorContext = multiPageEditorContext;
	}

	// // Map of editor input to editor index
	Map<EObject, Integer> mapEditorInput2EditorIndex = new HashMap<EObject, Integer>();

	public Map<EObject, Integer> getMapEditorInput2EditorIndex() {
		if (mapEditorInput2EditorIndex == null) {
			mapEditorInput2EditorIndex = new HashMap<EObject, Integer>();
		}
		return mapEditorInput2EditorIndex;
	}

	/**
	 * Creates a page for each different {@link Diagram} that is in the given
	 * {@link IEditorInput}.
	 */
	@Override
	protected void createPages() {
		// add a listener to the TabFolder so that the window title is updated
		addTabFolderListeners();
		// 1 - get input
		// 2 - get root elements to be opened from input
		// 3 - search IMOSKittEditorFactories
		// 4 - for each root element that has an IMOSKittEditorFactory
		// 4.1 - create an IEditorPart
		// 4.2 - add a page for that IEditorPart
		addPagesForInput(getEditorInput());
		// review that the mapping from editor input to index is correct
		// this has to be done because the EObjects that are originally read are
		// not in the same ResourceSet as the ones that the editors are really
		// editing
		reviewEObjectsInEditors();
		// listen to changes (adding/removing diagrams) in the ResourceSet so
		// that the pages in this multi tab editor are updated correctly.
		addDiagramsListener();
	}

	protected void addTabFolderListeners() {
		if (getTabFolder() != null) {
			getTabFolder().addSelectionListener(createTabSelectionListener());
		}
	}

	protected SelectionListener tabSelectedListener = null;

	protected SelectionListener createTabSelectionListener() {
		if (tabSelectedListener == null) {
			tabSelectedListener = new SelectionListener() {

				public void widgetSelected(SelectionEvent e) {
					// update the title of the editor
					if (e.item != null
							&& e.item.getData() instanceof CachedResourcesDiagramEditor) {
						CachedResourcesDiagramEditor diagrameditor = (CachedResourcesDiagramEditor) e.item
								.getData();
						updateEditorTitle(diagrameditor);
						//
						setUndoContextForUndoAction();
						setUndoContextForRedoAction();
					}
				}

				public void widgetDefaultSelected(SelectionEvent e) {
				}
			};
		}
		return tabSelectedListener;
	}

	protected void updateEditorTitle(IEditorPart editorPart) {
		if (editorPart instanceof CachedResourcesDiagramEditor) {
			// update this part's text
			CachedResourcesDiagramEditor diagramEditor = (CachedResourcesDiagramEditor) editorPart;
			Diagram diagram = diagramEditor.getDiagram();
			String type = diagram.getType();
			if (MOSKittEditorIDs.getExtensionsMapModelToLabel().containsKey(
					type)) {
				String label = MOSKittEditorIDs.getExtensionsMapModelToLabel()
						.get(type);
				setPartName(buildPartLabel(label));
			}
			// update this part's image
			// set image of the title. The image is the one that the label
			// provider provides for the given eObject (in this case the
			// element of the diagram)
			Image titleImage = getLabelProvider()
					.getImage(diagram.getElement());
			setTitleImage(titleImage);
		}
	}

	protected String buildPartLabel(String editorLabel) {
		if (editorLabel != null) {
			return editorLabel + Messages.MOSKittMultiPageEditor_1;
		}
		return Messages.MOSKittMultiPageEditor_2;
	}

	/**
	 * Adds a listener that listens to the creation or destruction of
	 * {@link Diagram}s in the {@link ResourceSet} and adds or removes pages in
	 * the editor accordingly.
	 * 
	 */
	protected void addDiagramsListener() {
		if (getMultiPageEditorContext() != null) {
			TransactionalEditingDomain domain = getMultiPageEditorContext()
					.getEditingDomain();
			if (domain != null) {
				domain.addResourceSetListener(createListenerForDiagrams());
			}
		}
	}

	/**
	 * Adds the initial pages for the given {@link IEditorInput}. <br>
	 * Tries to retrieve a list of the last open diagrams and restore only
	 * those.
	 * 
	 * @param editorInput
	 */
	protected void addPagesForInput(IEditorInput editorInput) {
		if (editorInput != null) {
			try {
				List<EObject> rootEObjects = MultiDiagramUtil
						.getOpenDiagrams(editorInput);
				if (rootEObjects == null || rootEObjects.size() <= 0) {
					List<EObject> rootElements = MDTUtil
							.getRootElementsFromFile(editorInput);
					if (rootElements != null && rootElements.size() >= 1) {
						removeAllExceptFirstDiagram(rootElements);
						rootEObjects = rootElements;
					}
				}
				if (rootEObjects == null || rootEObjects.size() <= 0) {
					addEmptyPage();
				}
				addPagesForElements(rootEObjects);
			} catch (PartInitException ex) {
				Activator.getDefault().logError(
						Messages.MOSKittMultiPageEditor_3, ex);
				addErrorPage(ex.getMessage());
			}
		}
	}

	/**
	 * Remove all {@link EObject}s except the first {@link Diagram}.
	 * 
	 * @param eObjects
	 */
	protected void removeAllExceptFirstDiagram(List<EObject> eObjects) {
		List<EObject> toRemove = new ArrayList<EObject>();
		Diagram diagram = null;
		for (EObject eObject : eObjects) {
			if (diagram != null) {
				toRemove.add(eObject);
			} else if (eObject != null) {
				diagram = (Diagram) Platform.getAdapterManager().getAdapter(
						eObject, Diagram.class);
				if (diagram == null) {
					toRemove.add(eObject);
				}
			}
		}
		eObjects.removeAll(toRemove);
	}

	/**
	 * A default empty page for when there is no other page for a
	 * {@link Diagram} or other {@link EObject} to show.
	 */
	protected void addEmptyPage() {
		Label label = new Label(getTabFolder(), SWT.BORDER);
		label.setText(Messages.MOSKittMultiPageEditor_4);
		addPage(label);
	}

	/**
	 * A default empty page for when there is no other page for a
	 * {@link Diagram} or other {@link EObject} to show.
	 */
	protected void addErrorPage(String errorMessage) {
		Label label = new Label(getTabFolder(), SWT.BORDER);
		label.setText(MessageFormat.format(Messages.MOSKittMultiPageEditor_5,
				(errorMessage == null ? errorMessage : ""))); //$NON-NLS-1$
		addPage(label);
	}

	/**
	 * Adds a page for each of the given {@link EObject}s.
	 * 
	 * @param rootEObjects
	 */
	protected void addPagesForElements(List<EObject> rootEObjects)
			throws PartInitException {
		for (EObject eObject : rootEObjects) {
			addPageForElement(eObject);
		}
	}

	/**
	 * Adds a page for the given {@link EObject}. <br>
	 * To add a page for this EObject an {@link IMOSKittEditorFactory} must
	 * exist that can handle this type of EObject.
	 * 
	 * @param eObject
	 * @return
	 */
	protected int addPageForElement(EObject eObject) throws PartInitException {
		// try to get an editor that can handle this EObejct.
		IEditorPart editorPart = MOSKittEditorFactoryRegistry.getInstance()
				.getEditorFor(eObject);
		// store the recently created editor part
		creatingEditorPart = editorPart;

		if (editorPart != null) {
			// if an editor for this kind of EObject exists, create a page for
			// it, with its IEditorInput pointing to this EObject via an
			// URIEditorInput.
			IEditorInput input = createEditorInputFor(eObject);
			try {
				// add the page
				int index = addPage(editorPart, input);
				if (index >= 0) {
					// set this editor part title
					updateEditorTitle(editorPart);
					// set text of the tab
					setPageText(index, MDTUtil
							.getObjectNameOrEmptyString(eObject));

					// set image of the tab. The image is the one that the label
					// provider provides for the given eObject (in this case the
					// element of the diagram)
					if (eObject instanceof Diagram
							&& ((Diagram) eObject).getElement() != null) {
						setPageImage(index, getLabelProvider().getImage(
								((Diagram) eObject).getElement()));
					}

					// add this editor's index to the mapping between editor
					// inputs and editor indexes.
					getMapEditorInput2EditorIndex().put(eObject, index);
					//
					updateUndoContextForActions();
				}
				return index;
			} catch (PartInitException ex) {
				// something went wrong, log it.
				Activator.getDefault().logError(
						MessageFormat.format(Messages.MOSKittMultiPageEditor_7,
								eObject), ex);
				// the editing domain may need removal
				cleanUpEditingDomain();
				throw ex;
			} catch (NullPointerException ex) {
				// something went wrong, log it.
				Activator.getDefault().logError(
						MessageFormat.format(Messages.MOSKittMultiPageEditor_8,
								eObject), ex);
				// the editing domain may need removal
				cleanUpEditingDomain();
				throw new PartInitException(MessageFormat.format(
						Messages.MOSKittMultiPageEditor_9, eObject));
			}
		}
		// we can't handle this kind of EObject.
		return -1;
	}

	/**
	 * Asks the editing domain registry for an editing domain clean up in case
	 * it is needed:
	 */
	protected void cleanUpEditingDomain() {
		// remove the editing domain in case it has been created
		if (getEditors().size() <= 0) {
			EditingDomainRegistry.getInstance().cleanRegistry(
					getSite().getPage());
		}
	}

	@Override
	public int addPage(IEditorPart editor, IEditorInput input)
			throws PartInitException {
		int addedIndex = -1;
		try {
			// add the page
			addedIndex = super.addPage(editor, input);
			// notify the actionbar contributor of the new active page
		} catch (PartInitException ex) {
			// set an invalid index
			addedIndex = -1;
			// remove the editing domain in case it has been created
			cleanUpEditingDomain();
		}
		// return the added index
		return addedIndex;
	}

	/**
	 * Returns a set of all the labelProviders registered in the system. Only
	 * one of them if used to return the text and image for the given object
	 */
	protected ILabelProvider getLabelProvider() {
		return MDTUtil.getLabelProvider();
	}

	/**
	 * Create a {@link CTabItem} with a special {@link CTabItem#dispose()} that
	 * 1) prevents the tab from closing if its the last open tab and 2) when the
	 * tab is disposed a reordering of the mapping of editor input to editor
	 * index is performed.
	 */
	@Override
	protected CTabItem createItem(int index, Control control) {
		CTabItem item = new CTabItem(getTabFolder(), SWT.CLOSE, index) {
			@Override
			public void dispose() {
				if (getPageCount() == 1) {
					// if there is only one page it can not be closed.
					return;
				}
				// get index
				int index = getParent().indexOf(this);
				if (index == 0) {
					// never allow the first tab to be closed
					return;
				}
				// if the control is an EditorPart, deactivate all edit parts in
				// that editor
				if (getData() instanceof IDiagramWorkbenchPart) {
					EditPart rootEditPart = ((IDiagramWorkbenchPart) getData())
							.getDiagramEditPart();
					if (rootEditPart != null) {
						rootEditPart.deactivate();
					}
				}
				// do normal dispose()
				super.dispose();
				// reorder, if all went well.
				if (index >= 0) {
					reorderMapEditorInput2EditorIndex(index);
				}
			}
		};
		item.setControl(control);
		return item;
	}

	/**
	 * When removing a page we must check if it was from one editor and update
	 * the mapping of editor input to editor index.
	 */
	@Override
	public void removePage(int pageIndex) {
		super.removePage(pageIndex);
		// Update mapping of editor input to page index when a page is removed.
		// after removing a page we will store the currently open diagrams.
		MultiDiagramUtil.storeOpenDiagrams(getDiagramInputs());
	}

	/**
	 * Forces a reordering of the mapping of editor input to editor index.
	 * Usually because a page was closed. <br>
	 * 
	 * @param removedIndex
	 */
	protected void reorderMapEditorInput2EditorIndex(int removedIndex) {
		// Update mapping of editor input to page index when a page is removed.
		// All indexes higher than the one removed must be taken 1.
		EObject toRemove = null;
		for (Entry<EObject, Integer> entry : getMapEditorInput2EditorIndex()
				.entrySet()) {
			if (entry.getValue() != null && entry.getValue() == removedIndex) {
				toRemove = entry.getKey();
			}
			if (entry.getValue() != null && entry.getValue() > removedIndex) {
				entry.setValue(entry.getValue() - 1);
			}
		}
		if (toRemove != null) {
			getMapEditorInput2EditorIndex().remove(toRemove);
		}
	}

	/**
	 * Reviews the diagrams being edited with those in the editor input to
	 * editor index mapping and updates the entries in the mapping with the
	 * correct keys. This must be done as some EObjects are initially loaded in
	 * different ResourceSets than the ones in which they are finally edited by
	 * their editors and being in different ResourceSets makes them return false
	 * when compared with #equals().
	 * 
	 */
	protected void reviewEObjectsInEditors() {
		Diagram diagram = null;
		List<EObject> toRemove = new ArrayList<EObject>();
		Map<EObject, Integer> toAdd = new HashMap<EObject, Integer>();
		// review every open editor.
		for (IEditorPart editorPart : getEditors()) {
			// get the Diagram being edited in that editor.
			diagram = (Diagram) editorPart.getAdapter(Diagram.class);
			if (diagram != null) {
				// we will compare via URI fragments.
				String oneFragment = diagram.eResource()
						.getURIFragment(diagram);
				for (EObject eObject : getMapEditorInput2EditorIndex().keySet()) {
					String otherFragment = eObject.eResource().getURIFragment(
							eObject);
					if (otherFragment != null
							&& otherFragment.equals(oneFragment)) {
						// objects to be removed
						toRemove.add(eObject);
						// entries to add
						toAdd.put(diagram, getMapEditorInput2EditorIndex().get(
								eObject));
					}
				}
			}
		}
		// remove some entries
		for (EObject eObject : toRemove) {
			getMapEditorInput2EditorIndex().remove(eObject);
		}
		// add some entries.
		getMapEditorInput2EditorIndex().putAll(toAdd);
	}

	/**
	 * Create an {@link IEditorInput} that points to the given {@link EObject}.
	 * 
	 * @param eObject
	 * @return
	 */
	protected IEditorInput createEditorInputFor(EObject eObject) {
		if (eObject == null || eObject.eResource() == null) {
			return null;
		}
		String uriPath = URI.decode(eObject.eResource().getURI().toString());
		uriPath = PathsUtil.fromAbsoluteFileSystemToAbsoluteWorkspace(uriPath,
				false);
		URI tempURI = URI.createURI(uriPath);
		URI uri = tempURI.appendFragment(eObject.eResource().getURIFragment(
				eObject));
		// the IEditorInput is an URIEditorInput that points to the given
		// EObject via URI fragments. If it has been migrated, it is a
		// CachedResourcesEditorInput
		IEditorInput input = null;
		input = new CachedResourcesEditorInput(uri);
		((CachedResourcesEditorInput) input).setMigrated(migrated);
		return input;
	}

	/**
	 * Adds the underlying model {@link EObject}s of the {@link Diagram}s in the
	 * list.
	 * 
	 * @param rootEObjects
	 */
	protected void addDiagramModelFiles(List<EObject> rootEObjects) {
		if (rootEObjects == null) {
			return;
		}
		Diagram diagram = null;
		EObject root = null;
		List<EObject> toAdd = new ArrayList<EObject>();
		for (EObject eObject : rootEObjects) {
			diagram = (Diagram) Platform.getAdapterManager().getAdapter(
					eObject, Diagram.class);
			if (diagram != null) {
				root = diagram.getElement();
				if (!toAdd.contains(root)) {
					toAdd.add(root);
				}
			}
		}
		for (EObject eObject : toAdd) {
			if (!rootEObjects.contains(eObject)) {
				rootEObjects.add(eObject);
			}
		}
	}

	/**
	 * Saving is delegated to each dirty nested {@link IEditorPart}.
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		IEditorPart editor = null;
		int saveNeeded = ISaveablePart2.DEFAULT;
		for (int i = 0; i < getPageCount(); i++) {
			editor = getEditor(i);
			// an editor must be dirty to be saved
			if (editor != null && editor.isDirty()) {
				// check the save option given by the user in the
				// propmtToSaveOnClose()
				if (mapEditorIndex2SaveOnClose.containsKey(i)) {
					saveNeeded = mapEditorIndex2SaveOnClose.get(i);
				} else {
					saveNeeded = ISaveablePart2.YES;
				}
				if (saveNeeded == ISaveablePart2.YES
						|| saveNeeded == ISaveablePart2.DEFAULT) {
					// if the save option is YEs or DEFAULT, the editor's
					// contents need to be saved
					editor.doSave(monitor);
				}
			}
		}
		// we store the open diagrams in each save operation.
		MultiDiagramUtil.storeOpenDiagrams(getDiagramInputs());
	}

	/**
	 * Save as is delegated to the currently active {@link IEditorPart}.
	 */
	@Override
	public void doSaveAs() {
		IEditorPart editor = getActiveEditor();
		if (editor != null) {
			editor.doSaveAs();
		}
	}

	/**
	 * Is save as allowed is delegated to the currently active editor.
	 */
	@Override
	public boolean isSaveAsAllowed() {
		IEditorPart editor = getActiveEditor();
		if (editor != null) {
			return editor.isSaveAsAllowed();
		}
		return false;
	}

	// add - remove Diagrams and EObjects pages

	/**
	 * Opens a page for the given {@link EObject}, with the proper
	 * {@link IEditorPart} (if one exists). If the given EObject is already
	 * being edited, the page with the editor editing it will be given focus.
	 */
	public boolean openPageForEObject(EObject eObject) {
		try {
			if (eObject != null) {
				int existingEditor = searchPageEditingEObject(eObject);
				if (existingEditor >= 0) {
					// if there is a page that has an editor that is editing the
					// element to open, set focus in that page.
					setActivePage(existingEditor);
					return true;
				} else {
					try {
						// otherwise, create an editor and a page for it.
						int index = addPageForElement(eObject);
						if (index >= 0) {
							setActivePage(index);
							return true;
						}
					} catch (PartInitException ex) {
						Activator.getDefault().logError(
								MessageFormat.format(
										Messages.MOSKittMultiPageEditor_10,
										eObject), ex);
						addErrorPage(ex.getMessage());
					}
				}
			}
			return false;
		} finally {
			// always revise that the maping between editor input and editor
			// index is correct.
			reviewEObjectsInEditors();
			// the pages that are open have changed, store them
			MultiDiagramUtil.storeOpenDiagrams(getDiagramInputs());
		}
	}

	/**
	 * Closes the page that has an {@link IEditorPart} that is editing the given
	 * {@link EObject}.
	 * 
	 * @param eObject
	 * @return
	 */
	public boolean closePageForEObject(EObject eObject) {
		try {
			int pageCount = getPageCount();
			if (pageCount <= 1) {
				return false;
			}
			// close the page
			return closeExistingPageForEObject(eObject);
		} finally {
			// always make sure that the mapping between editor input and editor
			// index is correct.
			reviewEObjectsInEditors();
			// the pages that are open have changed, store them.
			MultiDiagramUtil.storeOpenDiagrams(getDiagramInputs());
		}
	}

	protected boolean closeExistingPageForEObject(EObject eObject) {
		if (eObject != null) {
			// search for the page that is editing the EObject
			int index = searchPageEditingEObject(eObject);
			if (index >= 0) {
				// remove it if it exists
				removePage(index);
				return true;
			}
		}
		return false;
	}

	/**
	 * Get the index of the page that contains an editor that is editing the
	 * given {@link EObject}.
	 * 
	 * @param eObject
	 * @return
	 */
	protected int searchPageEditingEObject(EObject eObject) {
		if (eObject == null) {
			// no EObject, no page
			return -1;
		}
		if (getMapEditorInput2EditorIndex().containsKey(eObject)) {
			// the mapping has the info, return it.
			return getMapEditorInput2EditorIndex().get(eObject);
		}
		// no page is editing that EObject.
		return -1;
	}

	// // IAdaptable

	/**
	 * Adapts to {@link Diagram}. <br>
	 * Adapts to {@link EditingDomain}. <br>
	 * Adapts to {@link TransactionalEditingDomain}. <br>
	 * Other adaptations are handled by superclasses.
	 */
	@Override
	public Object getAdapter(Class adapter) {
		if (Diagram.class.equals(adapter)) {
			// if a Diagfram is wanted, return the one from the active editor,
			// if any.
			IEditorPart editorPart = getActiveEditor();
			if (editorPart != null) {
				return editorPart.getAdapter(Diagram.class);
			}
		}
		if (EditingDomain.class.equals(adapter)
				|| TransactionalEditingDomain.class.equals(adapter)) {
			// if an editing domain is wanted, return the one shared by this
			// multi page editor, if any.
			return getMultiPageEditorContext().getEditingDomain();
		}
		if (IEditingDomainProvider.class.equals(adapter)) {
			// return a proper provider that can return the editing domain used
			// by all the nested editors.
			if (getActiveEditor() instanceof IEditingDomainProvider) {
				return getActiveEditor();
			} else {
				return null;
			}
		}
		if (IStorageApplicable.class.equals(adapter)) {
			if (getActiveEditor() == null) {
				return null;
			}
			return getActiveEditor().getAdapter(adapter);
		}
		// other adaptations are delegated to the superclass.
		return super.getAdapter(adapter);
	}

	// // init

	/**
	 * The initialization must create the {@link MOSKittMultiPageEditorContext}
	 * context and add this edtor as an {@link IPartListener2}.
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		if (site.getActionBars() instanceof EditorActionBars) {
			((EditorActionBars) site.getActionBars())
					.setEditorContributor(MOSKittMultiPageEditorActionBarContributor
							.getDefault());
		}
		super.init(site, input);
		// create this editor's context.
		setMultiPageEditorContext(new MOSKittMultiPageEditorContext(this));
		// add this editor as a listener to listen to its own closing ;)
		addThisEditorAsPartListener(site.getPage());
	}

	/**
	 * Adds this editor as an {@link IPartListener2} to listen to its own
	 * closing.
	 * 
	 * @param page
	 */
	protected void addThisEditorAsPartListener(IWorkbenchPage page) {
		page.addPartListener(this);
	}

	// // create site

	/**
	 * Create a {@link MOSKittMultiPageEditorSite} that has info about the
	 * {@link MOSKittMultiPageEditorContext}.
	 */
	@Override
	protected IEditorSite createSite(IEditorPart editorPart) {
		// This editor's site must include information about the
		// IMOSKittMultiPageEditorContext.
		return new MOSKittMultiPageEditorSite(this, editorPart,
				getMultiPageEditorContext(), getEditorSite()
						.getActionBarContributor());
	}

	// // utilities

	/**
	 * Returns the active editor. If no editor is active yet but there is one
	 * being initialized, that one is returned. Otherwise, return null.
	 */
	@Override
	public IEditorPart getActiveEditor() {
		IEditorPart activeEditor = super.getActiveEditor();
		return activeEditor != null ? activeEditor : creatingEditorPart;
	}

	/**
	 * Returns a list with all the {@link IEditorPart}s open as pages.
	 */
	public List<IEditorPart> getEditors() {
		List<IEditorPart> editors = new ArrayList<IEditorPart>();
		IEditorPart editor = null;
		for (int i = 0; i < getPageCount(); i++) {
			editor = getEditor(i);
			if (editor != null) {
				editors.add(editor);
			}
		}
		return editors;
	}

	/**
	 * Get a list of diagrams that are inputs for any of the open editors.
	 * 
	 * @return
	 */
	protected List<Diagram> getDiagramInputs() {
		if (getMapEditorInput2EditorIndex() == null
				|| getMapEditorInput2EditorIndex().size() <= 0) {
			return Collections.emptyList();
		}
		List<Diagram> diagrams = new ArrayList<Diagram>();
		Diagram diagram = null;
		for (EObject eObject : getMapEditorInput2EditorIndex().keySet()) {
			diagram = (Diagram) Platform.getAdapterManager().getAdapter(
					eObject, Diagram.class);
			if (diagram != null) {
				diagrams.add(diagram);
			}
		}
		return diagrams;
	}

	protected IDiagramWorkbenchPart getDiagramWorkbenchPartEditor() {
		IEditorPart activeEditor = getActiveEditor();
		if (activeEditor != null) {
			IDiagramWorkbenchPart diagramWorkbenchPart = (IDiagramWorkbenchPart) Platform
					.getAdapterManager().getAdapter(activeEditor,
							IDiagramWorkbenchPart.class);
			if (diagramWorkbenchPart != null) {
				return diagramWorkbenchPart;
			}
		}
		return null;
	}

	/**
	 * Creates a listener that listens to changes in the {@link ResourceSet}
	 * regarding {@link Diagram} changed (additions, removal, name change).
	 * 
	 * @return
	 */
	protected ResourceSetListener createListenerForDiagrams() {
		// resource set listener to add and remove pages when diagrams are
		// created - deleted - name changed
		return new ResourceSetListenerImpl() {

			@Override
			public void resourceSetChanged(ResourceSetChangeEvent event) {
				if (event.getNotifications() != null) {
					List<Diagram> diagramsAdded = new ArrayList<Diagram>(1);
					List<Diagram> diagramsRemoved = new ArrayList<Diagram>(1);

					for (Notification notification : event.getNotifications()) {
						// check diagrams added
						if (notification.getEventType() == Notification.ADD) {
							if (notification.getNotifier() instanceof Resource) {
								if (notification.getNewValue() instanceof Diagram)
									// add diagram to toAdd list
									diagramsAdded.add((Diagram) notification
											.getNewValue());
							}
						}
						if (notification.getEventType() == Notification.ADD_MANY) {
							if (notification.getNotifier() instanceof Resource) {
								if (notification.getNewValue() instanceof Collection) {
									for (Object object : (Collection) notification
											.getNewValue()) {
										if (object instanceof Diagram) {
											diagramsAdded.add((Diagram) object);
										}
									}
								}
							}
						}
						// check diagrams removed
						if (notification.getEventType() == Notification.REMOVE) {
							if (notification.getNotifier() instanceof Resource) {
								if (notification.getOldValue() instanceof Diagram) {
									// add diagram to toRemove list
									diagramsRemoved.add((Diagram) notification
											.getOldValue());
								}
							}
						}
						if (notification.getEventType() == Notification.REMOVE_MANY) {
							if (notification.getNotifier() instanceof Resource) {
								if (notification.getOldValue() instanceof Collection) {
									for (Object object : (Collection) notification
											.getOldValue()) {
										if (object instanceof Diagram) {
											diagramsRemoved
													.add((Diagram) object);
										}
									}
								}
							}
						}
						// check for diagram name's changes
						if (notification.getEventType() == Notification.SET
								|| notification.getEventType() == Notification.UNSET) {
							if (notification.getNotifier() instanceof Diagram
									&& NotationPackage.eINSTANCE
											.getDiagram_Name().equals(
													notification.getFeature())) {
								Diagram diagram = (Diagram) notification
										.getNotifier();
								if (diagram != null) {
									String newName = diagram.getName() != null ? diagram
											.getName()
											: diagram.getType();
									for (EObject eObject : getMapEditorInput2EditorIndex()
											.keySet()) {
										if (eObject != null
												&& eObject.equals(diagram)) {
											Integer pageIndex = getMapEditorInput2EditorIndex()
													.get(eObject);
											if (pageIndex != null) {
												setPageText(pageIndex, newName);
											}
										}
									}
								}
							}
						}
					}

					for (Diagram diagram : diagramsAdded) {
						// handle added diagrams
						handleDiagramAdded(diagram);
					}

					for (Diagram diagram : diagramsRemoved) {
						// handle removed diagrams
						handleDiagramRemoved(diagram);
					}
				}
				super.resourceSetChanged(event);
			}
		};
	}

	protected void handleDiagramAdded(Diagram diagram) {
		// open a page for each added diagram.
		// if the diagram does not belong to the original Resource, do not add
		// it.
		if (diagram != null) {
			if (diagram.eResource() != null) {
				if (!diagram.eResource().equals(getMainDiagramResource())) {
					return;
				}
			}
		}
		boolean openNewDiagram = getOpenNewDiagramPreferenceValue();
		if (openNewDiagram) {
			openPageForEObject(diagram);
		}
	}

	private boolean getOpenNewDiagramPreferenceValue() {
		boolean value = Activator.getDefault().getPreferenceStore().getBoolean(
				MOSKittPreferenceConstants.P_NEW_DIAGRAMS);

		return value;
	}

	protected void handleDiagramRemoved(Diagram diagram) {
		// close each deleted diagram's page
		closeExistingPageForEObject(diagram);
	}

	// // IPartListener2

	public void partActivated(IWorkbenchPartReference partRef) {
		// nothing
	}

	public void partBroughtToTop(IWorkbenchPartReference partRef) {
		// nothing
	}

	public void partClosed(IWorkbenchPartReference partRef) {
		if (partRef != null) {
			IWorkbenchPart workbenchPart = partRef.getPart(false);
			if (workbenchPart != null && this.equals(workbenchPart)) {
				// if its us who is closing, store the open diagrams
				MultiDiagramUtil.storeOpenDiagrams(getDiagramInputs());
			}
		}
	}

	public void partDeactivated(IWorkbenchPartReference partRef) {
		// nothing
	}

	public void partHidden(IWorkbenchPartReference partRef) {
		// nothing
	}

	public void partInputChanged(IWorkbenchPartReference partRef) {
		// nothing
	}

	public void partOpened(IWorkbenchPartReference partRef) {
		// nothing
	}

	public void partVisible(IWorkbenchPartReference partRef) {
		// nothing
	}

	// // IDiagramWorkbenchPart

	/**
	 * {@link IDiagramWorkbenchPart} operations are delegated to the active
	 * editor.
	 */
	public Diagram getDiagram() {
		IDiagramWorkbenchPart diagramWorkbenchPart = getDiagramWorkbenchPartEditor();
		if (diagramWorkbenchPart != null) {
			return diagramWorkbenchPart.getDiagram();
		}
		return null;
	}

	/**
	 * {@link IDiagramWorkbenchPart} operations are delegated to the active
	 * editor.
	 */
	public IDiagramEditDomain getDiagramEditDomain() {
		IDiagramWorkbenchPart diagramWorkbenchPart = getDiagramWorkbenchPartEditor();
		if (diagramWorkbenchPart != null) {
			return diagramWorkbenchPart.getDiagramEditDomain();
		}
		return null;
	}

	/**
	 * {@link IDiagramWorkbenchPart} operations are delegated to the active
	 * editor.
	 */
	public DiagramEditPart getDiagramEditPart() {
		IDiagramWorkbenchPart diagramWorkbenchPart = getDiagramWorkbenchPartEditor();
		if (diagramWorkbenchPart != null) {
			return diagramWorkbenchPart.getDiagramEditPart();
		}
		return null;
	}

	/**
	 * {@link IDiagramWorkbenchPart} operations are delegated to the active
	 * editor.
	 */
	public IDiagramGraphicalViewer getDiagramGraphicalViewer() {
		IDiagramWorkbenchPart diagramWorkbenchPart = getDiagramWorkbenchPartEditor();
		if (diagramWorkbenchPart != null) {
			return diagramWorkbenchPart.getDiagramGraphicalViewer();
		}
		return null;
	}

	// // content outline update & refresh

	/** "Content Outline" view identifier */
	private static final String DiagramOutlineViewID = "org.eclipse.ui.views.ContentOutline"; //$NON-NLS-1$

	/**
	 * When a page changes we want to notify the content outline so that the new
	 * diagram is rendered in the diagram.
	 */
	@Override
	protected void pageChange(int newPageIndex) {
		IEditorPart iep = getEditor(newPageIndex);

		// refresh the disposed icons from the views
		if (iep instanceof IDiagramWorkbenchPart) {
			IDiagramWorkbenchPart diagramWP = (IDiagramWorkbenchPart) iep;
			DiagramEditPart dep = diagramWP.getDiagramEditPart();
			refreshChilds(dep);
		}
		// do default operations
		super.pageChange(newPageIndex);
		// notify outline view of the page change
		notifyContentOutlinePartActivated();
		// refresh the new active diagram
		refreshDiagram(newPageIndex);
	}

	private void refreshChilds(DiagramEditPart dep) {
		for (Object o : dep.getChildren()) {
			if (!(o instanceof IGraphicalEditPart))
				continue;

			IGraphicalEditPart igep = (IGraphicalEditPart) o;

			IFigure figure = igep.getFigure();
			if (figure != null) {
				refreshFigureLabel(figure, igep.resolveSemanticElement());
			}

			refreshChilds(igep);
		}
	}

	private void refreshChilds(IGraphicalEditPart igep) {
		for (Object o : igep.getChildren()) {
			if (!(o instanceof IGraphicalEditPart))
				continue;

			IGraphicalEditPart igep2 = (IGraphicalEditPart) o;

			IFigure figure = igep2.getFigure();
			if (figure != null) {
				refreshFigureLabel(figure, igep2.resolveSemanticElement());
			}

			refreshChilds(igep2);
		}
	}

	private void refreshFigureLabel(IFigure figure, EObject eObject) {
		if (eObject == null)
			return;

		if (figure instanceof org.eclipse.draw2d.Label) {
			org.eclipse.draw2d.Label label = (org.eclipse.draw2d.Label) figure;
			if (label.getIcon().isDisposed()) {
				label.setIcon(MDTUtil.getLabelProvider().getImage(eObject));
			}
		} else if (figure instanceof WrappingLabel) {
			WrappingLabel label = (WrappingLabel) figure;
			if (label.getIcon() != null && label.getIcon().isDisposed()) {
				label.setIcon(MDTUtil.getLabelProvider().getImage(eObject));
			}
		}
	}

	protected void refreshDiagram(int pageIndex) {
		IEditorPart editorPart = getEditor(pageIndex);
		if (editorPart instanceof IDiagramWorkbenchPart) {
			DiagramEditPart editPart = ((IDiagramWorkbenchPart) editorPart)
					.getDiagramEditPart();
			if (editPart != null) {
				DiagramEditPartsUtil.updateDiagram(editPart);
			}
		}
	}

	/**
	 * Will look for the "Content Outline" view and notify it that a new editor
	 * has been activated.
	 */
	protected void notifyContentOutlinePartActivated() {
		// get the active editor inside this multi page editor
		IEditorPart activeEditor = getActiveEditor();
		if (activeEditor == null) {
			// no active editor means nothing to do
			return;
		}
		IViewReference[] viewReferences = null;
		try {
			// get the view references in the workbench to search for the
			// "Content Outline" view.
			IWorkbenchWindow ww = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow();
			if (ww != null && ww.getActivePage() != null) {
				viewReferences = ww.getActivePage().getViewReferences();
			}
		} catch (NullPointerException ex) {
			// workbench not yet ready;
		}
		if (viewReferences == null) {
			// no views means nothing to do.
			return;
		}
		// search through all the view references
		for (IViewReference reference : viewReferences) {
			if (reference.getId() != null
					&& reference.getId().equals(DiagramOutlineViewID)) {
				// the "Content Outline" is among the open view parts
				IWorkbenchPart part = reference.getPart(false);
				if (part != null) {
					// check that it is ready
					ContentOutline contentOutline = (ContentOutline) Platform
							.getAdapterManager().getAdapter(part,
									ContentOutline.class);
					if (contentOutline != null) {
						// finally notify the "Content Outline" of the editor
						// change.
						contentOutline.partActivated(activeEditor);
					}
				}
			}
		}
	}

	/**
	 * Mapping that stores the last save option selected by the user for each
	 * nested editor.
	 */
	private Map<Integer, Integer> mapEditorIndex2SaveOnClose = new HashMap<Integer, Integer>();

	/**
	 * {@link ISaveablePart2} implementation. <br>
	 * Will ask nested editors that implement ISaveablePart2 to perform their
	 * prompt.
	 * 
	 */
	public int promptToSaveOnClose() {
		// clear the previous map
		mapEditorIndex2SaveOnClose.clear();
		int saveOption = ISaveablePart2.DEFAULT;
		boolean saveNeeded = false;
		ISaveablePart saveablePart1 = null;
		ISaveablePart2 saveablePart2 = null;
		String partsNamesToSave = ""; //$NON-NLS-1$
		for (int i = 0; i < getPageCount(); i++) {
			IEditorPart editor = getEditor(i);
			if (editor == null) {
				continue;
			}
			saveablePart1 = (ISaveablePart) Platform.getAdapterManager()
					.getAdapter(editor, ISaveablePart.class);
			saveablePart2 = (ISaveablePart2) Platform.getAdapterManager()
					.getAdapter(editor, ISaveablePart2.class);
			if (saveablePart2 != null) {
				saveOption = saveablePart2.promptToSaveOnClose();
			} else if (saveablePart1 != null) {
				saveOption = saveablePart1.isSaveOnCloseNeeded() ? ISaveablePart2.YES
						: ISaveablePart2.NO;
			} else {
				saveOption = ISaveablePart2.DEFAULT;
			}
			mapEditorIndex2SaveOnClose.put(i, saveOption);
			if (saveOption == ISaveablePart2.YES) {
				saveNeeded = true;
				partsNamesToSave += ((partsNamesToSave.length() == 0 ? "" //$NON-NLS-1$
						: ", ") + getPageText(i)); //$NON-NLS-1$
			}
		}

		if (saveNeeded) {
			String message = Messages.MOSKittMultiPageEditor_15;
			String[] buttons = new String[] { IDialogConstants.YES_LABEL,
					IDialogConstants.NO_LABEL, IDialogConstants.CANCEL_LABEL };
			MessageDialog dialog = new MessageDialog(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell(),
					Messages.MOSKittMultiPageEditor_16, null, message,
					MessageDialog.QUESTION, buttons, 0) {
				protected int getShellStyle() {
					return SWT.CLOSE | SWT.TITLE | SWT.BORDER
							| SWT.APPLICATION_MODAL | getDefaultOrientation();
				}
			};
			int choice = ISaveablePart2.NO;
			choice = dialog.open();
			// map value of choice back to ISaveablePart2 values
			switch (choice) {
			case 0: // Yes
				choice = ISaveablePart2.YES;
				break;
			case 1: // No
				choice = ISaveablePart2.NO;
				break;
			case 2: // Cancel
				choice = ISaveablePart2.CANCEL;
				break;
			default: // ??
				choice = ISaveablePart2.DEFAULT;
				break;
			}
			return choice;
		} else {
			return ISaveablePart2.NO;
		}
	}

	public String getEditingDomainID() {
		if (getActiveEditor() instanceof es.cv.gvcase.emf.common.part.IEditingDomainRegistrable) {
			return ((es.cv.gvcase.emf.common.part.IEditingDomainRegistrable) getActiveEditor())
					.getEditingDomainID();
		}
		return null;
	}

	public String getEditingDomainResourceURI() {
		if (getActiveEditor() instanceof es.cv.gvcase.emf.common.part.IEditingDomainRegistrable) {
			return ((es.cv.gvcase.emf.common.part.IEditingDomainRegistrable) getActiveEditor())
					.getEditingDomainResourceURI();
		}
		return null;
	}

	public static final String MOSKittEditorSetInputHookID = "es.cv.gvcase.mdt.common.part.MOSKittMultiPageEditor.setInput.HookID"; //$NON-NLS-1$

	@Override
	protected void setInput(IEditorInput input) {
		// remove the possible editor properties set to the file of the given
		// input; that property interferes with the copy/paste handler
		MDTUtil.removeEditorForDiagramProperty(input);
		// a MOSKitt editor is started or changed input
		Object[] info = new Object[] { this, input };
		HookRunner.getInstance().runRunnablesWithInfoForHook(
				MOSKittEditorSetInputHookID, info);
		// mgil: migrate diagrams
		TransactionalEditingDomain editingDomain = EditingDomainRegistry
				.getInstance().hasDomain("", input); //$NON-NLS-1$

		Diagram diagram = MDTUtil.getDiagramFromEditorInput(input,
				editingDomain);
		if (diagram != null) {
			checkDiagramToOpen(editingDomain, diagram);
		}
		// mgil: end migrate diagrams

		super.setInput(input);
	}

	/**
	 * Opens the given editor input after this editor has been created.
	 * 
	 * @param editorInput
	 */
	public void openInput(IEditorInput editorInput) {
		if (editorInput != null) {
			// make sure this editor is active and on the front
			getSite().getWorkbenchWindow().getActivePage().activate(this);
			if (editorInput instanceof URIEditorInput) {
				URIEditorInput uriEditorInput = (URIEditorInput) editorInput;
				URI inputURI = uriEditorInput.getURI();
				ResourceSet resourceSet = getDiagram().eResource()
						.getResourceSet();
				inputURI = PathsUtil.toWorkspaceURI(inputURI);
				Resource inputResource = resourceSet.getResource(inputURI
						.trimFragment(), true);
				if (uriEditorInput.getURI().hasFragment()) {
					// get the Diagram to open from the given editor input
					EObject eObject = inputResource.getEObject(uriEditorInput
							.getURI().fragment());
					String eObjectFragment = eObject.eResource()
							.getURIFragment(eObject);
					if (eObject instanceof Diagram) {
						boolean alreadyShown = false;
						int i = 0;
						// search for an already open tab that is showing that
						// diagram
						for (CTabItem tabItem : getTabFolder().getItems()) {
							Object data = tabItem.getData();
							if (data instanceof IDiagramWorkbenchPart) {
								Diagram editorDiagram = ((IDiagramWorkbenchPart) data)
										.getDiagram();
								String editorDiagramFragment = editorDiagram
										.eResource().getURIFragment(
												editorDiagram);
								if (eObjectFragment
										.equals(editorDiagramFragment)) {
									// a tab has been found that is already
									// showing the diagram, open that tab
									setActivePage(i);
									alreadyShown = true;
									break;
								}
							}
							i++;
						}
						if (!alreadyShown) {
							// if no tab that show the iven input exist, open a
							// new one
							try {
								addPageForElement(eObject);
							} catch (PartInitException ex) {
								return;
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Check if there exists some migrator for the diagram, and execute them
	 * 
	 * @author mgil
	 * @param input
	 */
	protected void checkDiagramToOpen(TransactionalEditingDomain editingDomain,
			Diagram diagram) {
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
		String title = Messages.MOSKittMultiPageEditor_19;
		String message = Messages.MOSKittMultiPageEditor_20;
		for (MigratorService migrator : validMigrators) {
			String name = migrator.getLabel();
			message += "\t- " + name + "\n"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		message += Messages.MOSKittMultiPageEditor_23;
		boolean answer = MessageDialog
				.openQuestion(new Shell(), title, message);

		if (!answer) {
			return;
		}

		// ... and finally execute them. Check if we have an editing domain, to
		// execute the migration with a read/write transactions, or without it
		if (editingDomain != null) {
			for (MigratorService migrator : validMigrators) {
				if (!migrator.executeMigrationWithTransaction(editingDomain)) {
					break;
				} else {
					migrated = true;
				}
			}
		} else {
			for (MigratorService migrator : validMigrators) {
				if (!migrator.executeMigrationWithoutTransaction()) {
					break;
				} else {
					migrated = true;
				}
			}
		}
	}

	protected void updateUndoContextForActions() {
		try {
			setUndoContextForUndoAction();
			setUndoContextForRedoAction();
		} catch (NullPointerException ex) {
			return;
		}
	}

	protected void setUndoContextForUndoAction() {
		// update undo/redo menu
		IWorkbenchPart workbenchpart = this;
		if (workbenchpart != null
				&& workbenchpart.getSite() instanceof EditorSite) {
			IContributionItem[] items = ((org.eclipse.ui.internal.EditorMenuManager) ((org.eclipse.ui.internal.EditorSite) workbenchpart
					.getSite()).getActionBars().getMenuManager()).getParent()
					.getItems();
			for (IContributionItem item : items) {
				if (item instanceof MenuManager) {
					MenuManager menuManager = ((MenuManager) item);
					if (menuManager.getId().equals("edit")) { //$NON-NLS-1$
						for (IContributionItem menuItem : menuManager
								.getItems()) {
							if (menuItem instanceof ActionContributionItem) {
								ActionContributionItem actionItem = (ActionContributionItem) menuItem;
								if (actionItem.getId().equals("undo")) { //$NON-NLS-1$
									if (actionItem.getAction() instanceof LabelRetargetAction) {
										LabelRetargetAction labelAction = (LabelRetargetAction) actionItem
												.getAction();
										if (labelAction.getActionHandler() instanceof GlobalUndoAction) {
											GlobalUndoAction globalUndoAction = (GlobalUndoAction) labelAction
													.getActionHandler();
											IUndoContext undoContext = (IUndoContext) getAdapter(IUndoContext.class);
											globalUndoAction
													.setUndoContext(undoContext);
											IUndoableOperation operation = PlatformUI
													.getWorkbench()
													.getOperationSupport()
													.getOperationHistory()
													.getUndoOperation(
															undoContext);
											if (operation != null) {
												String label = operation
														.getLabel();
												labelAction
														.setText(Messages.MOSKittMultiPageEditor_26
																+ label);
											}
										}
										if (labelAction.getActionHandler() instanceof UndoActionHandler) {
											UndoActionHandler undoAction = (UndoActionHandler) labelAction
													.getActionHandler();
											IUndoContext undoContext = (IUndoContext) getAdapter(IUndoContext.class);
											undoAction.setContext(undoContext);
											IUndoableOperation operation = PlatformUI
													.getWorkbench()
													.getOperationSupport()
													.getOperationHistory()
													.getUndoOperation(
															undoContext);
											if (operation != null) {
												String label = operation
														.getLabel();
												labelAction
														.setText(Messages.MOSKittMultiPageEditor_27
																+ label);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	protected void setUndoContextForRedoAction() {
		// update undo/redo menu
		IWorkbenchPart workbenchpart = this;
		if (workbenchpart != null
				&& workbenchpart.getSite() instanceof EditorSite) {
			IContributionItem[] items = ((org.eclipse.ui.internal.EditorMenuManager) ((org.eclipse.ui.internal.EditorSite) workbenchpart
					.getSite()).getActionBars().getMenuManager()).getParent()
					.getItems();
			for (IContributionItem item : items) {
				if (item instanceof MenuManager) {
					MenuManager menuManager = ((MenuManager) item);
					if (menuManager.getId().equals("edit")) { //$NON-NLS-1$
						for (IContributionItem menuItem : menuManager
								.getItems()) {
							if (menuItem instanceof ActionContributionItem) {
								ActionContributionItem actionItem = (ActionContributionItem) menuItem;
								if (actionItem.getId().equals("redo")) { //$NON-NLS-1$
									if (actionItem.getAction() instanceof LabelRetargetAction) {
										LabelRetargetAction labelAction = (LabelRetargetAction) actionItem
												.getAction();
										if (labelAction.getActionHandler() instanceof GlobalRedoAction) {
											GlobalRedoAction globalRedoAction = (GlobalRedoAction) labelAction
													.getActionHandler();
											IUndoContext undoContext = (IUndoContext) getAdapter(IUndoContext.class);
											globalRedoAction
													.setUndoContext(undoContext);
											IUndoableOperation operation = PlatformUI
													.getWorkbench()
													.getOperationSupport()
													.getOperationHistory()
													.getUndoOperation(
															undoContext);
											if (operation != null) {
												String label = operation
														.getLabel();
												labelAction
														.setText(Messages.MOSKittMultiPageEditor_30
																+ label);
											}
										}
										if (labelAction.getActionHandler() instanceof RedoActionHandler) {
											RedoActionHandler redoAction = (RedoActionHandler) labelAction
													.getActionHandler();
											IUndoContext undoContext = (IUndoContext) getAdapter(IUndoContext.class);
											redoAction.setContext(undoContext);
											IUndoableOperation operation = PlatformUI
													.getWorkbench()
													.getOperationSupport()
													.getOperationHistory()
													.getUndoOperation(
															undoContext);
											if (operation != null) {
												String label = operation
														.getLabel();
												labelAction
														.setText(Messages.MOSKittMultiPageEditor_31
																+ label);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void setFocus() {
		super.setFocus();
		//
		updateUndoContextForActions();
	}

}
