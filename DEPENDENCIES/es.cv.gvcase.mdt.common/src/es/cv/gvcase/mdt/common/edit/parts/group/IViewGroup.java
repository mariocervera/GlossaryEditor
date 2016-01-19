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

import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.viewers.ISelection;

import es.cv.gvcase.mdt.common.actions.GroupViewsAction;
import es.cv.gvcase.mdt.common.edit.policies.GroupSelectionEditPolicy;

/**
 * Representation of a group in GMf diagrams. <br>
 * A group is selected when on of its elements is selected and acts as a whole
 * element. <br>
 * The grouping mechanism is based on some Extended properties:
 * <ul>
 * <li>"es.cv.gvcase.mdt.common.view.group.GroupCount" : takes care of the
 * number of groups in a diagram. Used as an identifier for new groups created.
 * Applied to {@link Diagram} elements.
 * <li>"es.cv.gvcase.mdt.common.view.group.belongToGroup" : hols the identifier
 * of the group to which a {@link View} belongs. Applied to {@link View}
 * elements.
 * </ul>
 * A group of views is an agruppation of views that, when one of them is
 * selected, the whole group is selected.
 * 
 * @see ViewGroup
 * @see ViewGroupRegistry
 * @see GroupViewsAction
 * @see GroupSelectionEditPolicy
 * 
 * @author <a href="mailto:fjcano@prodeelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public interface IViewGroup {

	/**
	 * Counter of groups in a diagram, extended feature.
	 */
	public static final String DiagramGroupCountFeature = "es.cv.gvcase.mdt.common.view.group.GroupCount";

	/**
	 * Group identifier extended feature.
	 */
	public static final String ViewBelongToGroup = "es.cv.gvcase.mdt.common.view.group.belongToGroup";

	/**
	 * Default initial value of group counter in a diagram.
	 */
	public static final int DiagramGroupCountInitialValue = 0;

	/**
	 * Add the given {@link IGraphicalEditPart} to this group.
	 * 
	 * @param editPart
	 * @return
	 */
	boolean addEditPartToGroup(IGraphicalEditPart editPart);

	/**
	 * Adds the given {@link View} to the this group.
	 * 
	 * @param view
	 * @return
	 */
	boolean addViewToGroup(View view);

	/**
	 * Gets all the {@link View}s that belong to this Group.
	 * 
	 * @return
	 */
	Collection<View> getAllViewsInGroup();

	/**
	 * Gets all the {@link IGraphicalEditPart}s in this group as an
	 * {@link ISelection}
	 * 
	 * @return
	 */
	ISelection getGroupEditPartsSelection();

	/**
	 * Sets the selection of the diagram editor to hold all the edit parts of
	 * this group.
	 */
	void selectGroupEditPartsSelection();

	/**
	 * Ungroups all the editparts of this group.
	 */
	void unGroup();

	/**
	 * Removes an editpart from the group.
	 * 
	 * @param editPart
	 */
	void removeEditPart(IGraphicalEditPart editPart);
}
