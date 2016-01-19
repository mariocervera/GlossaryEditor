/*******************************************************************************
 * Copyright (c) 2010 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – Initial implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.edit.parts.group;

import java.util.Collection;
import java.util.List;

import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.viewers.ISelection;

import es.cv.gvcase.mdt.common.model.ExtendedFeatureElement;
import es.cv.gvcase.mdt.common.model.ExtendedFeatureElementFactory;
import es.cv.gvcase.mdt.common.util.MDTUtil;

/**
 * Registry to operate with {@link IViewGroup}s. <br>
 * Holds several helper methods to operate on IViewGroups.
 * 
 * @author <a href="mailto:fjcano@prodeelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public class ViewGroupRegistry {

	// //
	// Singleton
	// //

	protected static ViewGroupRegistry INSTANCE = null;

	/**
	 * Public INSTANCE of this registry.
	 * 
	 * @return
	 */
	public static ViewGroupRegistry getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ViewGroupRegistry();
		}
		return INSTANCE;
	}

	private ViewGroupRegistry() {
	}

	// //
	// Utils
	// //

	/**
	 * Returns the group identifier of the given {@link IGraphicalEditPart}, if
	 * any.
	 */
	public int getGroupID(IGraphicalEditPart editPart) {
		if (editPart != null && editPart.getNotationView() != null) {
			return getGroupID(editPart.getNotationView());
		}
		return -1;
	}

	/**
	 * Returns the group identifier of the given {@link View}, if any.
	 * 
	 * @param view
	 * @return
	 */
	public int getGroupID(View view) {
		if (view == null) {
			return -1;
		}
		ExtendedFeatureElement element = ExtendedFeatureElementFactory
				.getInstance().asExtendedFeatureElement(view);
		Integer groupID = element.getInteger(IViewGroup.ViewBelongToGroup);
		if (groupID == null) {
			return -1;
		}
		return groupID;
	}

	/**
	 * Returns the next free group identifier that can be used to create a new
	 * group for the given {@link IGraphicalEditPart}.
	 * 
	 * @param editPart
	 * @return
	 */
	public int getNextGroup(IGraphicalEditPart editPart) {
		if (editPart != null) {
			return getNextGroup(editPart.getNotationView());
		}
		return -1;
	}

	/**
	 * Returns the next group identifier that can be used to create a new group
	 * for the given {@link View}.
	 * 
	 * @param view
	 * @return
	 */
	public int getNextGroup(View view) {
		if (view != null && view.getDiagram() != null) {
			ExtendedFeatureElement element = ExtendedFeatureElementFactory
					.getInstance().asExtendedFeatureElement(view.getDiagram());
			if (element != null) {
				Integer count = element
						.getInteger(IViewGroup.DiagramGroupCountFeature);
				if (count != null) {
					element.setValue(IViewGroup.DiagramGroupCountFeature,
							count + 1);
					return count + 1;
				} else {
					element.setValue(IViewGroup.DiagramGroupCountFeature,
							IViewGroup.DiagramGroupCountInitialValue);
					return IViewGroup.DiagramGroupCountInitialValue;
				}
			}
		}
		return -1;
	}

	/**
	 * Returns the {@link IViewGroup} to which the given
	 * {@link IGraphicalEditPart} belongs to, if any.
	 * 
	 * @param editPart
	 * @return
	 */
	public IViewGroup getViewGroupFor(IGraphicalEditPart editPart) {
		return new ViewGroup(editPart);
	}

	/**
	 * Makes a new group with the collection of given {@link IGraphicalEditPart}
	 * s.
	 * 
	 * @param editParts
	 * @return
	 */
	public IViewGroup groupEditParts(Collection<IGraphicalEditPart> editParts) {
		if (editParts == null || editParts.isEmpty()) {
			return null;
		}
		IGraphicalEditPart first = editParts.iterator().next();
		int newGroupID = getNextGroup(first);
		setEditPartInGroup(first, newGroupID);
		ViewGroup viewGroup = new ViewGroup(first);
		for (IGraphicalEditPart editPart : editParts) {
			viewGroup.addEditPartToGroup(editPart);
		}
		return viewGroup;
	}

	/**
	 * Adds the given {@link IGraphicalEditPart} to the specified group.
	 * 
	 * @param editPart
	 * @param group
	 */
	public void setEditPartInGroup(IGraphicalEditPart editPart, int group) {
		if (editPart != null) {
			View view = editPart.getNotationView();
			if (view == null) {
				return;
			}
			ExtendedFeatureElement element = ExtendedFeatureElementFactory
					.getInstance().asExtendedFeatureElement(view);
			if (element == null) {
				return;
			}
			element.setValue(IViewGroup.ViewBelongToGroup, group);
		}
	}

	/**
	 * checks whether the given {@link IGraphicalEditPart} is a
	 * {@link IViewGroup}.
	 * 
	 * @param editPart
	 * @return
	 */
	public boolean isInGroup(IGraphicalEditPart editPart) {
		if (editPart != null && editPart.getNotationView() != null) {
			return isInGroup(editPart.getNotationView());
		}
		return false;
	}

	/**
	 * Checks whether the given {@link View} is in a {@link IViewGroup}.
	 * 
	 * @param view
	 * @return
	 */
	public boolean isInGroup(View view) {
		if (view != null) {
			ExtendedFeatureElement element = ExtendedFeatureElementFactory
					.getInstance().asExtendedFeatureElement(view);
			if (element != null) {
				Integer group = element
						.getInteger(IViewGroup.ViewBelongToGroup);
				return group != null && group != -1;
			}
		}
		return false;
	}

	/**
	 * Checks whether the given {@link ISelection} can be grouped.
	 * 
	 * @param selection
	 * @return
	 */
	public boolean canGroupSelection(ISelection selection) {
		return canGroupEditParts(MDTUtil
				.getGraphicalEditPartsFromSelection(selection));
	}

	/**
	 * Checks whether the given collection of {@link IGraphicalEditPart}s can be
	 * grouped.
	 * 
	 * @param editParts
	 * @return
	 */
	public boolean canGroupEditParts(List<IGraphicalEditPart> editParts) {
		if (editParts != null && editParts.size() > 1) {
			View containerView = getContainerView(editParts.get(0));
			if (containerView == null) {
				return false;
			}
			// check that all of them share the same container
			for (IGraphicalEditPart editPart : editParts) {
				if (!containerView.equals(getContainerView(editPart))) {
					return false;
				}
			}
			// check that they are not in a group already
			boolean allInGroup = true;
			for (IGraphicalEditPart editPart : editParts) {
				if (!isInGroup(editPart)) {
					allInGroup = false;
				}
			}
			return !allInGroup;
		}
		return false;
	}

	/**
	 * Gets the container {@link View} element of an {@link IGraphicalEditPart}.
	 * 
	 * @param editPart
	 * @return
	 */
	protected View getContainerView(IGraphicalEditPart editPart) {
		if (editPart != null) {
			if (editPart.getNotationView().eContainer() instanceof View) {
				return (View) editPart.getNotationView().eContainer();
			}
		}
		return null;
	}

	// //
	// Diagram selection
	// //

	/**
	 * Sets the selection in the GMF editor to hold all the elements in the
	 * group of the given {@link IGraphicalEditPart}.
	 */
	public void selectGroup(IGraphicalEditPart editPart) {
		if (editPart != null) {
			IViewGroup group = getViewGroupFor(editPart);
			if (group != null) {
				group.selectGroupEditPartsSelection();
			}
		}
	}

	/**
	 * Checks whether a whole group and only a whole group is selected.
	 * 
	 * @return
	 */
	public boolean isGroupSelected() {
		List<IGraphicalEditPart> editParts = MDTUtil
				.getGraphicalEditPartsFromSelection(MDTUtil
						.getSelectionFromActiveEditor());
		if (editParts == null || editParts.size() < 2) {
			return false;
		}
		int groupID = getGroupID(editParts.get(0));
		if (groupID == -1) {
			return false;
		}
		int id = 0;
		for (IGraphicalEditPart editPart : editParts) {
			id = getGroupID(editPart);
			if (groupID != id) {
				return false;
			}
		}
		return true;
	}

}
