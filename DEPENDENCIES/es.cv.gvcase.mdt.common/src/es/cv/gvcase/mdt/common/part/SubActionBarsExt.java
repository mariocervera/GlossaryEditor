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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.ContributionManager;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.SubCoolBarManager;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IActionBars2;
import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.SubActionBars2;

/**
 * An extended sub cool bar manager for used by the
 * <code>MOSKittMultiPageEditorActionBarContributor</code>.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 */
public class SubActionBarsExt extends SubActionBars2 {

	private IEditorActionBarContributor myContributor;

	private String myType;

	private IToolBarManager myToolBarManager;

	private ToolBarContributionItem myToolBarContributionItem;

	private IEditorPart lastActiveEditor = null;

	/**
	 * Default constructor.
	 * 
	 * @param page
	 * @param parent
	 * @param subContributor
	 * @param type
	 */
	public SubActionBarsExt(IWorkbenchPage page, IActionBars2 parent,
			IEditorActionBarContributor subContributor, String type) {
		super(parent, parent.getServiceLocator());
		assert type != null;
		myType = type;
		assert page != null;
		assert subContributor != null;
		myContributor = subContributor;
		myContributor.init(this, page);
	}

	/**
	 * @return the action bar contributor
	 */
	public IEditorActionBarContributor getContributor() {
		return myContributor;
	}

	/**
	 * Changes the active editor part.
	 * 
	 * @param editorPart
	 */
	public void setEditorPart(IEditorPart editorPart) {
		myContributor.setActiveEditor(editorPart);
	}

	/**
	 * Gets the ToolBarManager looking for the parent tool bar and cleaning it
	 * from duplicated items.
	 */
	public IToolBarManager getToolBarManager() {
		if (myToolBarManager == null) {
			ICoolBarManager parentCoolBarManager = getTopCoolBarManager();
			if (parentCoolBarManager == null) {
				return null;
			}
			IContributionItem foundItem = parentCoolBarManager.find(myType);
			if (foundItem instanceof ToolBarContributionItem
					&& ((ToolBarContributionItem) foundItem)
							.getToolBarManager() != null) {
				myToolBarContributionItem = (ToolBarContributionItem) foundItem;
				myToolBarManager = myToolBarContributionItem
						.getToolBarManager();
			} else {
				if (false == parentCoolBarManager instanceof ContributionManager) {
					return null;
				}
				myToolBarManager = new ToolBarManager(SWT.FLAT | SWT.RIGHT);
				myToolBarContributionItem = new ToolBarContributionItem(
						myToolBarManager, myType);
				if (!((ContributionManager) parentCoolBarManager).replaceItem(
						myType, myToolBarContributionItem)) {
					parentCoolBarManager.add(myToolBarContributionItem);
				}
			}
			myToolBarContributionItem.setVisible(getActive());
			myToolBarManager.markDirty();
		}

		return myToolBarManager;
	}

	/**
	 * @return the top-level cool bar manager instance
	 */
	private ICoolBarManager getTopCoolBarManager() {
		ICoolBarManager coolBarManager = getCastedParent().getCoolBarManager();
		while (coolBarManager instanceof SubCoolBarManager
				&& ((SubCoolBarManager) coolBarManager).getParent() instanceof ICoolBarManager) {
			coolBarManager = (ICoolBarManager) ((SubCoolBarManager) coolBarManager)
					.getParent();
		}
		return coolBarManager;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.SubActionBars2#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
		myContributor.dispose();
		myContributor = null;
		myToolBarContributionItem = null;
		myToolBarManager = null;
	}

	/**
	 * Sets this SubActionBar active. Updates the toolbars cleaning them from
	 * all the duplicated contributed items.
	 * 
	 * @see org.eclipse.ui.SubActionBars2#setActive(boolean)
	 */
	@Override
	protected void setActive(boolean value) {
		if (!value) {
			setAllItemsVisibility(false);
		}
		// Force the Zoom toolbar action item to go non-visible so it's
		// refreshed later.
		setZoomToolbarActionVisibility(false);
		updateSomeActionBars();
		super.setActive(value);

		ICoolBarManager parentCoolBarManager = getTopCoolBarManager();
		if (parentCoolBarManager != null) {
			parentCoolBarManager.markDirty();
		}
		if (myToolBarManager != null && parentCoolBarManager != null) {
			IContributionItem[] items = myToolBarManager.getItems();
			List<String> appliedItems = new ArrayList<String>();
			for (int i = 0; i < items.length; i++) {
				if (appliedItems.contains(items[i].getId())) {
					items[i].setVisible(false);
					continue;
				}
				appliedItems.add(items[i].getId());
				IContributionItem item = items[i];
				item.setVisible(value);
			}
			myToolBarManager.markDirty();
			myToolBarManager.update(true);
		}

		if (value) {
			@SuppressWarnings("unchecked")
			Map<String, IAction> globals = getGlobalActionHandlers();
			if (globals != null) {
				for (Map.Entry<String, IAction> nextEntry : globals.entrySet()) {
					getParent().setGlobalActionHandler(nextEntry.getKey(),
							nextEntry.getValue());
				}
			}
		} else {
			getParent().clearGlobalActionHandlers();
		}
		getParent().updateActionBars();
	}

	private static final String ZoomToolbarActionID = "zoomContributionItem";

	protected void setZoomToolbarActionVisibility(boolean value) {
		IContributionItem[] items = getToolBarManager().getItems();
		if (items == null || items.length == 0) {
			return;
		}
		// search for the ZoomToolBarAction in the toolbar manager and set its
		// visibility
		for (IContributionItem item : items) {
			if (item.getId() != null
					&& item.getId().equals(ZoomToolbarActionID)) {
				item.setVisible(value);
			}
		}
		items = getCoolBarManager().getItems();
		if (items == null || items.length == 0) {
			return;
		}
		// search for the ZoomToolBarAction in the coolbar manager and set its
		// visibility
		for (IContributionItem item : items) {
			if (item.getId() != null
					&& item.getId().equals(ZoomToolbarActionID)) {
				item.setVisible(value);
			}
		}

		// return;
	}

	/**
	 * Checks whether the last active editor has changed and updates the active
	 * editor.
	 * 
	 * @return
	 */
	protected boolean checkLastActiveEditorChanged() {
		if (myContributor instanceof MOSKittActionBarContributor) {
			MOSKittActionBarContributor moskittContributor = (MOSKittActionBarContributor) myContributor;
			IEditorPart myContributorActiveEditor = moskittContributor
					.getActiveEditor();
			if (!myContributorActiveEditor.equals(lastActiveEditor)) {
				lastActiveEditor = myContributorActiveEditor;
				return true;
			}
		}
		return false;
	}

	/**
	 * Sets all the items by this SubActionBarsExt to the given visibility.
	 * 
	 * @param visible
	 */
	protected void setAllItemsVisibility(boolean visible) {
		if (getToolBarManager() != null) {
			for (IContributionItem item : getToolBarManager().getItems()) {
				item.setVisible(visible);
			}
		}
		if (getCoolBarManager() != null) {
			for (IContributionItem item : getCoolBarManager().getItems()) {
				item.setVisible(visible);
			}
		}
	}

	/**
	 * Update this SubActionBars' action bars
	 */
	protected void updateSomeActionBars() {
		if (getToolBarManager() != null) {
			getToolBarManager().markDirty();
			getToolBarManager().update(true);
		}
		if (getCoolBarManager() != null) {
			getCoolBarManager().markDirty();
			getCoolBarManager().update(true);
		}
	}

	/**
	 * Sets the duplicated items's visibility to false.
	 * 
	 * @param items
	 */
	protected void setDuplicatedItemsInvisible(IContributionItem[] items) {
		if (items == null || items.length <= 0) {
			return;
		}
		List<String> appliedItems = new ArrayList<String>();
		for (IContributionItem item : items) {
			String itemID = item.getId();
			if (appliedItems.contains(itemID)) {
				item.setVisible(false);
				continue;
			}
			appliedItems.add(itemID);
		}
		return;
	}

	/**
	 * Update the action bars removing the duplicated items:
	 */
	protected void updateRemovingDuplicatedItems() {
		removeDuplicatedContributedItems();
		if (getToolBarManager() != null) {
			getToolBarManager().update(true);
		}
		if (getCoolBarManager() != null) {
			getCoolBarManager().update(true);
		}
	}

	/**
	 * Removes the duplicated items from the contributions of this
	 * subactionbars.
	 * 
	 */
	protected void removeDuplicatedContributedItems() {
		if (getToolBarManager() != null) {
			setDuplicatedItemsInvisible(getToolBarManager().getItems());
		}
		if (getCoolBarManager() != null) {
			setDuplicatedItemsInvisible(getCoolBarManager().getItems());
		}
	}

}
