/*******************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – Initial API implementation.
 * 				 Javier Muñoz (Integranova) - Support for grouping by type
 *               Francisco Javier Cano Muñoz (Prodevelop) - support for Saveable and saving mechanism.
 *               Marc Gil Sendra (Prodevelop) - Improve for avoid stupid refreshes
 *
 ******************************************************************************/
package es.cv.gvcase.ide.navigator.view;

import java.util.List;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IOperationHistoryListener;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.commands.operations.ObjectUndoContext;
import org.eclipse.core.commands.operations.OperationHistoryEvent;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.ResourceSetChangeEvent;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.diagram.ui.parts.IDiagramGraphicalViewer;
import org.eclipse.gmf.runtime.diagram.ui.parts.IDiagramWorkbenchPart;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.Saveable;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import es.cv.gvcase.ide.navigator.Activator;
import es.cv.gvcase.ide.navigator.actions.GroupChildsAction;
import es.cv.gvcase.ide.navigator.actions.RemoveTypePrefixAction;
import es.cv.gvcase.ide.navigator.actions.SearchElementAction;
import es.cv.gvcase.ide.navigator.provider.IMOSKittNavigatorPropertySheetContributor;
import es.cv.gvcase.ide.navigator.provider.ToEditorSaveable;
import es.cv.gvcase.ide.navigator.util.NavigatorUtil;
import es.cv.gvcase.mdt.common.preferences.MOSKittPreferenceConstants;

/**
 * A specialization of {@link MOSKittCommonNavigator} to show the active
 * {@link IEditorPart} model in a explorer view.
 * 
 * @author <a href="maito:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 */
public class MOSKittModelNavigator extends MOSKittCommonNavigator/*
																 * implements
																 * ISaveablePart
																 */{

	private boolean activated = false;

	/** {@link IPartListener2} to react to editor activations and deactivations. */
	IPartListener2 partListener = new IPartListener2() {

		public void partActivated(IWorkbenchPartReference partRef) {
			handlePartActivated(partRef);
			if (!activated) {
				refreshViewer();
				activated = true;
			}
		}

		public void partBroughtToTop(IWorkbenchPartReference partRef) {
		}

		public void partClosed(IWorkbenchPartReference partRef) {
			handlePartDeactivated(partRef);
		}

		public void partDeactivated(IWorkbenchPartReference partRef) {
		}

		public void partHidden(IWorkbenchPartReference partRef) {
		}

		public void partInputChanged(IWorkbenchPartReference partRef) {
			handlePartActivated(partRef);
			activated = false;
		}

		public void partOpened(IWorkbenchPartReference partRef) {
			handlePartActivated(partRef);
			activated = false;
		}

		public void partVisible(IWorkbenchPartReference partRef) {
			handlePartActivated(partRef);
			activated = false;
		}

	};

	public void activate() {
		IEditorPart activeEditorPart = NavigatorUtil.getActiveEditor();
		// check if there has been a change in editors.
		boolean changedEditors = activeEditorPart != null
				&& !activeEditorPart.equals(editorPart);
		// if the editing domain is no longer valid or there has been a change
		// in editors, update everything
		if (editingDomain == null || changedEditors) {
			editorPart = NavigatorUtil.getActiveEditor();
			editingDomain = NavigatorUtil.getEditingDomainFromActiveEditor();
			if (editingDomain instanceof TransactionalEditingDomain) {
				((TransactionalEditingDomain) editingDomain)
						.addResourceSetListener(resourceSetListener);
			}
		}
		doUpdate();
	}

	protected IPartListener2 getListener() {
		return partListener;
	}

	IPropertyChangeListener preferenceListener = new IPropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent event) {
			if (event.getProperty().equals(
					MOSKittPreferenceConstants.NAVIGATOR_GROUPING)) {
				boolean value = false;
				if (event.getNewValue() instanceof String) {
					value = Boolean.valueOf((String) event.getNewValue());
				} else if (event.getNewValue() instanceof Boolean) {
					value = (Boolean) event.getNewValue();
				}
				setGroupChildsEnabled(value);
			} else if (event.getProperty().equals(
					MOSKittPreferenceConstants.NAVIGATOR_REMOVE_TYPE)) {
				boolean value = false;
				if (event.getNewValue() instanceof String) {
					value = Boolean.valueOf((String) event.getNewValue());
				} else if (event.getNewValue() instanceof Boolean) {
					value = (Boolean) event.getNewValue();
				}
				setRemovePrefixTypeEnabled(value);
			}
		}
	};

	public MOSKittModelNavigator() {
		super();
		OperationHistoryFactory.getOperationHistory()
				.addOperationHistoryListener(new IOperationHistoryListener() {

					public void historyNotification(OperationHistoryEvent event) {
						if (event.getEventType() == OperationHistoryEvent.OPERATION_ADDED) {
							// getNavigatorActionService().updateActionBars();
							getNavigatorActionService().fillActionBars(
									getViewSite().getActionBars());
						}

					}

				});

		es.cv.gvcase.mdt.common.Activator.getDefault().getPreferenceStore()
				.addPropertyChangeListener(preferenceListener);

		// mgil:: read the value from the preferences
		isGroupingChildsEnabled = Platform.getPreferencesService().getBoolean(
				"es.cv.gvcase.mdt.common",
				MOSKittPreferenceConstants.NAVIGATOR_GROUPING, true, null);

		// mgil:: read the value from the preferences
		isRemovePrefixTypeEnabled = Platform.getPreferencesService()
				.getBoolean("es.cv.gvcase.mdt.common",
						MOSKittPreferenceConstants.NAVIGATOR_REMOVE_TYPE, true,
						null);
	}

	@Override
	public void dispose() {
		super.dispose();

		es.cv.gvcase.mdt.common.Activator.getDefault().getPreferenceStore()
				.removePropertyChangeListener(preferenceListener);
		activated = false;
	}

	private ToEditorSaveable toEditorSaveable = new ToEditorSaveable(null, this);

	private Saveable[] toEditorSaveableArray = new Saveable[] { toEditorSaveable };

	public static final String PROPERTY_GROUPCHILDS = "es.cv.gvcase.ide.navigator.view.groupchilds";

	public static final String PROPERTY_REMOVEPREFIX = "es.cv.gvcase.ide.navigator.view.removeTypePrefix";

	/** Listens to changes in the editors and synchs with the modelViewer. */
	ISelectionListener mySelectionListener = new ISelectionListener() {
		public void selectionChanged(IWorkbenchPart part, ISelection selection) {
			if (part instanceof IEditorPart) {
				handleSelectionChangedInEditor(selection);
			}
		}
	};

	/** The handling selection changed. */
	private boolean handlingSelectionChanged = false;

	private boolean isGroupingChildsEnabled = true;

	private boolean isRemovePrefixTypeEnabled = true;

	public static final int IS_GROUPINGCHILDS_ENABLED_PROPERTY = 987;

	public static final int IS_REMOVEPREFIXTYPE_ENABLED_PROPERTY = 16774;

	public ToEditorSaveable getToEditorSaveable() {
		return toEditorSaveable;
	}

	/**
	 * Start handling selection change.
	 * 
	 * @return true, if successful
	 */
	private boolean startHandlingSelectionChange() {
		if (handlingSelectionChanged == false) {
			handlingSelectionChanged = true;
			return true;
		}
		return false;
	}

	/**
	 * End handling selection change.
	 */
	private void endHandlingSelectionChange() {
		handlingSelectionChanged = false;
	}

	/**
	 * Creates the default controls and install selection listeners in the
	 * viewer and the editors' workbench page.
	 * 
	 * @param parent
	 *            the parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		// // add selectionChangedListener to CommonViewer
		getCommonViewer().addPostSelectionChangedListener(
				new ISelectionChangedListener() {
					public void selectionChanged(SelectionChangedEvent event) {
						handleSelectionChangedToDiagram(event);
					}
				});
		// // add selectionListener to listen to editor selection changes
		getSite().getPage().addSelectionListener(mySelectionListener);
		// XXX: this may help preventing the model navigator from "trembling".
		// It does not, however.
		// getSite().getPage().addPostSelectionListener(mySelectionListener);

		getViewSite().getActionBars().getToolBarManager().add(
				getGroupChildsAction());
		getViewSite().getActionBars().getToolBarManager()
				.add(getSearchAction());
		getViewSite().getActionBars().getToolBarManager().add(
				getRemoveTypesPrefixAction());
	}

	/**
	 * Gets the {@link IPropertySheetPage} if there's any available.
	 * 
	 * @return the property sheet page
	 */
	protected IPropertySheetPage getPropertySheetPage() {
		if (getPropertySheetContributors() != null) {
			for (IMOSKittNavigatorPropertySheetContributor contributor : getPropertySheetContributors()) {
				if (contributor.hasPropertySheetPage()) {
					return new TabbedPropertySheetPage(
							new MOSKittPropertySheetContributor());
				}
			}
		}
		return null;
	}

	/**
	 * Helper class to give the correct {@link TabbedPropertySheetPage}.
	 * 
	 * @author fjcano
	 */
	public class MOSKittPropertySheetContributor implements
			ITabbedPropertySheetPageContributor {

		/*
		 * (non-Javadoc)
		 * 
		 * @seeorg.eclipse.ui.views.properties.tabbed.
		 * ITabbedPropertySheetPageContributor#getContributorId()
		 */
		public String getContributorId() {
			if (getPropertySheetContributors() != null) {
				for (IMOSKittNavigatorPropertySheetContributor contributor : getPropertySheetContributors()) {
					if (contributor.hasPropertySheetPage()) {
						return contributor.getContributorID();
					}
				}
			}
			return null;
		}
	}

	/**
	 * Adapts to {@link IPropertySheetPage}.
	 * 
	 * @param adapter
	 *            the adapter
	 * 
	 * @return the adapter
	 */
	@Override
	public Object getAdapter(Class adapter) {
		if (adapter == IPropertySheetPage.class) {
			return getPropertySheetPage();
		}
		if (IUndoContext.class.equals(adapter)) {
			return getUndoContext();
		}
		return super.getAdapter(adapter);
	}

	/**
	 * If linking, propagate the selection in the {@link CommonViewer} to the
	 * active {@link IEditorPart}.
	 * 
	 * @param event
	 *            the event
	 */
	protected void handleSelectionChangedToDiagram(SelectionChangedEvent event) {
		if (isLinkingEnabled() && startHandlingSelectionChange()) {
			IEditorPart activeEditor = NavigatorUtil.getActiveEditor();
			if (activeEditor instanceof IDiagramWorkbenchPart) {
				// set editor selection; select EditParts
				IDiagramGraphicalViewer viewer = ((IDiagramWorkbenchPart) activeEditor)
						.getDiagramGraphicalViewer();
				if (viewer == null) {
					return;
				}
				List<EditPart> editPartsToSelect = NavigatorUtil
						.getEditPartsFromSelection(event.getSelection(), viewer);
				StructuredSelection selectedEditParts = new StructuredSelection(
						editPartsToSelect);
				viewer.setSelection(selectedEditParts);
				if (!selectedEditParts.isEmpty()) {
					EditPart editPart = (EditPart) selectedEditParts
							.getFirstElement();
					viewer.reveal(editPart);
				}
			}
			endHandlingSelectionChange();
		}
	}

	/**
	 * Handle selection changed in editor.
	 * 
	 * @param selection
	 *            the selection
	 */
	protected void handleSelectionChangedInEditor(ISelection selection) {
		if (isLinkingEnabled() && startHandlingSelectionChange()) {
			ISelection unwrappedSelection = NavigatorUtil
					.unwrapSelection(selection);
			if (unwrappedSelection.isEmpty() == false) {
				getCommonViewer().setSelection(unwrappedSelection, true);
			}
			endHandlingSelectionChange();
		}
	}

	@Override
	protected void handlePartActivated(IWorkbenchPartReference partRef) {
		IWorkbenchPart part = partRef.getPart(false);
		if (part instanceof IEditorPart) {
			activate();
			part.addPropertyListener(this);
		}

		// startListening the IOperationHistory for the UndoContext
		startListening();
	}

	/**
	 * @author jmunoz
	 * @return
	 */
	public IAction getGroupChildsAction() {
		IAction groupChildsAction = new GroupChildsAction(this);
		ImageDescriptor folderIcon = PlatformUI.getWorkbench()
				.getSharedImages().getImageDescriptor(
						ISharedImages.IMG_OBJ_FOLDER);
		groupChildsAction.setImageDescriptor(folderIcon);
		groupChildsAction.setHoverImageDescriptor(folderIcon);
		return groupChildsAction;
	}

	/**
	 * 
	 * @return
	 */
	public IAction getRemoveTypesPrefixAction() {
		IAction removeTypesPrefixAction = new RemoveTypePrefixAction(this);
		ImageDescriptor clearIcon = PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_TOOL_CUT);
		removeTypesPrefixAction.setImageDescriptor(clearIcon);
		removeTypesPrefixAction.setHoverImageDescriptor(clearIcon);
		return removeTypesPrefixAction;
	}

	/**
	 * @author jmunoz
	 * @return
	 */
	public IAction getSearchAction() {
		IAction searchAction = new SearchElementAction(this);
		ImageDescriptor magnifyingGlassIcon = Activator
				.getImagedescriptor("icons/full/etool16/search.gif");
		searchAction.setImageDescriptor(magnifyingGlassIcon);
		searchAction.setHoverImageDescriptor(magnifyingGlassIcon);
		return searchAction;
	}

	/**
	 * @author jmunoz
	 * @param toGroupChilds
	 */
	public final void setGroupChildsEnabled(boolean toGroupChilds) {
		isGroupingChildsEnabled = toGroupChilds;
		firePropertyChange(IS_GROUPINGCHILDS_ENABLED_PROPERTY);
		ISelection sel = this.getCommonViewer().getSelection();
		if (sel instanceof ITreeSelection
				&& ((ITreeSelection) sel).getFirstElement() != null) {
			IStructuredSelection s = new StructuredSelection(
					((ITreeSelection) sel).getFirstElement());
			this.getCommonViewer().setSelection(s, true);
		}
		this.refreshViewer();

		// mgil:: update preference value
		IEclipsePreferences root = Platform.getPreferencesService()
				.getRootNode();
		root.node(InstanceScope.SCOPE).node("es.cv.gvcase.mdt.common")
				.putBoolean(MOSKittPreferenceConstants.NAVIGATOR_GROUPING,
						toGroupChilds);
	}

	/**
	 * @author jmunoz
	 * @return
	 */
	public boolean isGroupingChildsEnabled() {
		return this.isGroupingChildsEnabled;
	}

	/**
	 * Set the isRemovePrefixTypeEnabled to the given value and fire a property
	 * change event.
	 * 
	 * @param isRemovePrefixTypeEnabled
	 */
	public void setRemovePrefixTypeEnabled(boolean isRemovePrefixTypeEnabled) {
		this.isRemovePrefixTypeEnabled = isRemovePrefixTypeEnabled;
		firePropertyChange(IS_REMOVEPREFIXTYPE_ENABLED_PROPERTY);
		refreshViewer();

		// mgil:: update preference value
		IEclipsePreferences root = Platform.getPreferencesService()
				.getRootNode();
		root.node(InstanceScope.SCOPE).node("es.cv.gvcase.mdt.common")
				.putBoolean(MOSKittPreferenceConstants.NAVIGATOR_REMOVE_TYPE,
						isRemovePrefixTypeEnabled);
	}

	/**
	 * Gets whether the removal of prefix types is enabled or not.
	 * 
	 * @return
	 */
	public boolean isRemovePrefixTypeEnabled() {
		return isRemovePrefixTypeEnabled;
	}

	/**
	 * @author jmunoz
	 */
	@Override
	public String getFrameToolTipText(Object anElement) {
		return "MOSKitt Model Explorer";
	}

	// IPropertyListener
	/**
	 * When the dirty property changes we want the title to be updated.
	 * 
	 * @author fjcano
	 */
	@Override
	public void propertyChanged(Object source, int propId) {
		if (propId == PROP_DIRTY) {
			if (source != null && source.equals(getEditorPart())) {
				firePropertyChange(PROP_DIRTY);
			}
			return;
		}
	}

	//
	/**
	 * Due to the NavigatorSaveablesService not updating correctly the Saveables
	 * available via the ContentProviders, a new and direct way of getting the
	 * Saveables is implemented here.
	 * 
	 * @author fjcano
	 */
	@Override
	public Saveable[] getSaveables() {
		// return a Saveable that targets the doSave action to the Active
		// Editor.
		return toEditorSaveableArray;
	}

	/**
	 * Due to the NavigatorSaveablesService not updating correctly the Saveables
	 * available via the ContentProviders, a new and direct way of getting the
	 * Saveables is implemented here.
	 * 
	 * @author fjcano
	 */
	@Override
	public Saveable[] getActiveSaveables() {
		// return a Saveable that targets the doSave action to the Active
		// Editor.
		return toEditorSaveableArray;
	}

	/**
	 * When the active editor changes we have to update the Saveable's editor.
	 * 
	 * @author fjcano
	 */
	@Override
	public void doUpdate() {
		super.doUpdate();
		if (getToEditorSaveable() != null) {
			getToEditorSaveable().setEditor(getEditorPart());
		}
	}

	/**
	 * Select the affected element in the tree only if linking is enabled
	 */
	protected void handleResourceSetChanged(ResourceSetChangeEvent event) {
		if (isLinkingEnabled() && startHandlingSelectionChange()) {
			for (Notification notif : event.getNotifications()) {
				if ((notif.getEventType() == Notification.REMOVE || notif
						.getEventType() == Notification.REMOVE_MANY)
						&& notif.getNotifier() instanceof EObject
						&& !(notif.getNotifier() instanceof EAnnotation)
						&& !(notif.getNotifier() instanceof View)) {
					if (this.getCommonViewer().getContentProvider() instanceof ITreeContentProvider) {

						IStructuredSelection selection = new StructuredSelection(
								notif.getNotifier());
						this.getCommonViewer().setSelection(selection);
					}
				}
			}
			endHandlingSelectionChange();
		}
		super.handleResourceSetChanged(event);
	}

	// //
	// Undoing of operations ; the undoing of operations uses an
	// IOperationHistory from the workspace that uses IUndoContexts to
	// identify which operations can be undo from an active editor.
	// //

	protected IOperationHistoryListener operationHistoryListener = null;

	protected IOperationHistoryListener getOperationHistoryListener() {
		if (operationHistoryListener == null) {
			operationHistoryListener = createHistoryListener();
		}
		return operationHistoryListener;
	}

	/**
	 * Gets my operation history listener. By default it adds my undo context to
	 * operations that have affected my editing domain.
	 * <P>
	 * Subclasses may override this method to return a different history
	 * listener, or to return <code>null</code> if they do not want to listen to
	 * the operation history.
	 * 
	 * @return my operation history listener
	 */
	protected IOperationHistoryListener createHistoryListener() {

		return new IOperationHistoryListener() {

			public void historyNotification(final OperationHistoryEvent event) {

				if (event.getEventType() == OperationHistoryEvent.DONE) {
					IUndoableOperation operation = event.getOperation();

					if (shouldAddUndoContext(operation)) {
						// add my undo context to populate my undo
						// menu
						operation.addContext(getUndoContext());
					}
				}
			}
		};
	}

	/**
	 * Returns the undo context of the active editor. It is an
	 * {@link ObjectUndoContext} referencing that editor.
	 */
	protected IUndoContext getUndoContext() {
		if (editorPart != null) {
			return (IUndoContext) editorPart.getAdapter(IUndoContext.class);
		} else {
			return null;
		}
	}

	/**
	 * Because of the Editing Domain being shared among all open instances of
	 * MOSKitt editors, either GMF or FEFEM, this method must only return true
	 * if I am the current active editor in MOSKitt. This ensures that the only
	 * IUndoContext coming from MOSKitt editors that is added to the operation
	 * is the one from the current active editor, so that on an undo operation,
	 * only the operations performed from this editor can be undone.
	 */
	protected boolean shouldAddUndoContext(IUndoableOperation operation) {
		// find the current active editor in MOSKitt.
		IEditorPart activeEditor = getActiveWorkbenchEditor();
		// compare the active editor with this editor
		if (activeEditor != null && activeEditor.equals(this)) {
			// same editor, the IUndoContext must be added.
			return true;
		} else {
			// different editor, the IUndoContext must not be added.
			return false;
		}
	}

	/**
	 * Returns the active editor in the workbench.
	 * 
	 * @return
	 */
	protected IEditorPart getActiveWorkbenchEditor() {
		try {
			IWorkbenchWindow ww = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow();
			if (ww == null) {
				return null;
			}
			return ww.getActivePage().getActiveEditor();
		} catch (NullPointerException ex) {
			return null;
		}
	}

	/**
	 * Installs all the {@link IOperationHistoryListener}s needed by the editor.
	 */
	protected void startListening() {
		TransactionalEditingDomain domain = getTransactionalEditingDomain();
		if (domain != null) {
			if (getOperationHistoryListener() != null) {
				getOperationHistory().addOperationHistoryListener(
						getOperationHistoryListener());
			}
		}
	}

	/**
	 * Removes all the {@link IOperationHistoryListener}s used by the editor.
	 */
	protected void stopListening() {
		if (getOperationHistoryListener() != null) {
			if (getUndoContext() != null) {
				// dispose my undo context
				getOperationHistory().dispose(getUndoContext(), true, true,
						true);
			}
			// unistall the IOperationHistoryListener
			getOperationHistory().removeOperationHistoryListener(
					getOperationHistoryListener());
		}
	}

	/**
	 * Retrieves the {@link IOperationHistory} used and shared among all
	 * operations performed in the workbench.
	 * 
	 * @return
	 */
	protected IOperationHistory getOperationHistory() {
		return OperationHistoryFactory.getOperationHistory();
	}
}
