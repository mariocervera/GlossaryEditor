/*******************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – Initial API 
 * implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.ide.navigator.actions.resource;

import org.eclipse.emf.edit.ui.action.ControlAction;
import org.eclipse.emf.edit.ui.action.CopyAction;
import org.eclipse.emf.edit.ui.action.CutAction;
import org.eclipse.emf.edit.ui.action.LoadResourceAction;
import org.eclipse.emf.edit.ui.action.PasteAction;
import org.eclipse.emf.edit.ui.action.RedoAction;
import org.eclipse.emf.edit.ui.action.UndoAction;
import org.eclipse.emf.edit.ui.action.ValidateAction;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;

import es.cv.gvcase.ide.navigator.provider.MOSKittCommonActionProvider;
import es.cv.gvcase.ide.navigator.view.MOSKittCommonNavigator;
import es.cv.gvcase.mdt.common.actions.DeleteAction;

/**
 * {@link MOSKittCommonActionProvider} that contributes the copy, cut, paste,
 * delete, undo, redo, load resource, validate and control actions.
 * 
 * @author <a href="maulto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 */
public class EditingDomainActions extends MOSKittCommonActionProvider {

	/** The active view part. */
	MOSKittCommonNavigator activeViewPart;

	/** This is the action used to implement delete. */
	protected DeleteAction deleteAction;

	/** This is the action used to implement cut. */
	protected CutAction cutAction;

	/** This is the action used to implement copy. */
	protected CopyAction copyAction;

	/** This is the action used to implement paste. */
	protected PasteAction pasteAction;

	/** This is the action used to implement undo. */
	protected UndoAction undoAction;

	/** This is the action used to implement redo. */
	protected RedoAction redoAction;

	/** This is the action used to load a resource. */
	protected LoadResourceAction loadResourceAction;

	/** This is the action used to control or uncontrol a contained object. */
	protected ControlAction controlAction;

	/** This is the action used to perform validation. */
	protected ValidateAction validateAction;

	/**
	 * This style bit indicates that the "additions" separator should come after
	 * the "edit" separator.
	 */
	public static final int ADDITIONS_LAST_STYLE = 0x1;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.navigator.CommonActionProvider#init(org.eclipse.ui.navigator
	 * .ICommonActionExtensionSite)
	 */
	@Override
	public void init(ICommonActionExtensionSite site) {
		super.init(site);
		// //
		activeViewPart = getCommonNavigator();
		ISharedImages sharedImages = PlatformUI.getWorkbench()
				.getSharedImages();
		// deleteAction = new DeleteAction(removeAllReferencesOnDelete());
		deleteAction = new DeleteAction(removeAllReferencesOnDelete());
		deleteAction.setImageDescriptor(sharedImages
				.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
		cutAction = new CutAction();
		cutAction.setImageDescriptor(sharedImages
				.getImageDescriptor(ISharedImages.IMG_TOOL_CUT));
		copyAction = new CopyAction();
		copyAction.setImageDescriptor(sharedImages
				.getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
		pasteAction = new PasteAction();
		pasteAction.setImageDescriptor(sharedImages
				.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE));
		undoAction = new UndoAction();
		undoAction.setImageDescriptor(sharedImages
				.getImageDescriptor(ISharedImages.IMG_TOOL_UNDO));
		redoAction = new RedoAction();
		redoAction.setImageDescriptor(sharedImages
				.getImageDescriptor(ISharedImages.IMG_TOOL_REDO));
	}

	/**
	 * Removes the all references on delete.
	 * 
	 * @return true, if successful
	 */
	protected boolean removeAllReferencesOnDelete() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.actions.ActionGroup#fillActionBars(org.eclipse.ui.IActionBars
	 * )
	 */
	@Override
	public void fillActionBars(IActionBars actionBars) {
		super.fillActionBars(actionBars);
		// //
		actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(),
				deleteAction);
		actionBars.setGlobalActionHandler(ActionFactory.CUT.getId(), cutAction);
		actionBars.setGlobalActionHandler(ActionFactory.COPY.getId(),
				copyAction);
		actionBars.setGlobalActionHandler(ActionFactory.PASTE.getId(),
				pasteAction);
		actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(),
				undoAction);
		actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(),
				redoAction);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.actions.ActionGroup#fillContextMenu(org.eclipse.jface.
	 * action.IMenuManager)
	 */
	@Override
	public void fillContextMenu(IMenuManager menu) {
		super.fillContextMenu(menu);
		// //
		update();
		// Add the edit menu actions.
		menu.add(new ActionContributionItem(undoAction));
		menu.add(new ActionContributionItem(redoAction));
		menu.add(new Separator());
		menu.add(new ActionContributionItem(cutAction));
		menu.add(new ActionContributionItem(copyAction));
		menu.add(new ActionContributionItem(pasteAction));
		menu.add(new Separator());
		menu.add(new ActionContributionItem(deleteAction));
		menu.add(new Separator());
		// // activate
		activate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.actions.ActionGroup#updateActionBars()
	 */
	@Override
	public void updateActionBars() {
		super.updateActionBars();
		// //
		activate();
		update();
	}

	/**
	 * Activate.
	 */
	public void activate() {
		if (activeViewPart == null) {
			return;
		}

		deleteAction.setActiveWorkbenchPart(activeViewPart);
		cutAction.setActiveWorkbenchPart(activeViewPart);
		copyAction.setActiveWorkbenchPart(activeViewPart);
		pasteAction.setActiveWorkbenchPart(activeViewPart);
		undoAction.setActiveWorkbenchPart(activeViewPart);
		redoAction.setActiveWorkbenchPart(activeViewPart);

		if (loadResourceAction != null) {
			loadResourceAction.setActiveWorkbenchPart(activeViewPart);
		}

		if (controlAction != null) {
			controlAction.setActiveWorkbenchPart(activeViewPart);
		}

		if (validateAction != null) {
			validateAction.setActiveWorkbenchPart(activeViewPart);
		}

		if (activeViewPart != null) {
			ISelectionProvider selectionProvider = activeViewPart
					.getCommonViewer() instanceof ISelectionProvider ? (ISelectionProvider) activeViewPart
					.getCommonViewer()
					: null;

			if (selectionProvider != null) {
				selectionProvider.addSelectionChangedListener(deleteAction);
				selectionProvider.addSelectionChangedListener(cutAction);
				selectionProvider.addSelectionChangedListener(copyAction);
				selectionProvider.addSelectionChangedListener(pasteAction);

				if (validateAction != null) {
					selectionProvider
							.addSelectionChangedListener(validateAction);
				}

				if (controlAction != null) {
					selectionProvider
							.addSelectionChangedListener(controlAction);
				}
			}
		}

		update();
	}

	/**
	 * Deactivate.
	 */
	public void deactivate() {
		deleteAction.setActiveWorkbenchPart(null);
		cutAction.setActiveWorkbenchPart(null);
		copyAction.setActiveWorkbenchPart(null);
		pasteAction.setActiveWorkbenchPart(null);
		undoAction.setActiveWorkbenchPart(null);
		redoAction.setActiveWorkbenchPart(null);

		if (loadResourceAction != null) {
			loadResourceAction.setActiveWorkbenchPart(null);
		}

		if (controlAction != null) {
			controlAction.setActiveWorkbenchPart(null);
		}

		if (validateAction != null) {
			validateAction.setActiveWorkbenchPart(null);
		}

		if (activeViewPart != null) {
			ISelectionProvider selectionProvider = activeViewPart
					.getCommonViewer() instanceof ISelectionProvider ? (ISelectionProvider) activeViewPart
					.getCommonViewer()
					: null;

			if (selectionProvider != null) {
				selectionProvider.removeSelectionChangedListener(deleteAction);
				selectionProvider.removeSelectionChangedListener(cutAction);
				selectionProvider.removeSelectionChangedListener(copyAction);
				selectionProvider.removeSelectionChangedListener(pasteAction);

				if (validateAction != null) {
					selectionProvider
							.removeSelectionChangedListener(validateAction);
				}

				if (controlAction != null) {
					selectionProvider
							.removeSelectionChangedListener(controlAction);
				}
			}
		}
	}

	/**
	 * Update.
	 */
	public void update() {
		ISelection selection = null;
		try {
			if (getCommonNavigator() != null
					&& getCommonNavigator().getCommonViewer() != null) {
				selection = getCommonNavigator().getCommonViewer()
						.getSelection();
			}
		} catch (NullPointerException ex) {
			selection = null;
		}
		IStructuredSelection structuredSelection = selection instanceof IStructuredSelection ? (IStructuredSelection) selection
				: StructuredSelection.EMPTY;

		if (structuredSelection.isEmpty()) {
			return;
		}

		if (deleteAction != null) {
			deleteAction.updateSelection(structuredSelection);
		}
		if (cutAction != null) {
			cutAction.updateSelection(structuredSelection);
		}
		if (copyAction != null) {
			copyAction.updateSelection(structuredSelection);
		}
		if (pasteAction != null) {
			pasteAction.updateSelection(structuredSelection);
		}
		if (validateAction != null) {
			validateAction.updateSelection(structuredSelection);
		}
		if (controlAction != null) {
			controlAction.updateSelection(structuredSelection);
		}
		if (undoAction != null) {
			undoAction.update();
		}
		if (redoAction != null) {
			redoAction.update();
		}
		if (loadResourceAction != null) {
			loadResourceAction.update();
		}
	}

}
