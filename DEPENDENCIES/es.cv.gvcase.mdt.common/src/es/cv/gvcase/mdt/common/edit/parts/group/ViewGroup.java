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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;

import es.cv.gvcase.mdt.common.model.ExtendedFeatureElement;
import es.cv.gvcase.mdt.common.model.ExtendedFeatureElementFactory;
import es.cv.gvcase.mdt.common.util.DiagramEditPartsUtil;

/**
 * Basic implementation of {@link IViewGroup}. <br>
 * Requires an initial {@link IGraphicalEditPart} to work. <br>
 * 
 * @author <a href="mailto:fjcano@prodeelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public class ViewGroup implements IViewGroup {

	/**
	 * This group's id.
	 */
	private int groupID = -1;

	/**
	 * This group's main diagram.
	 */
	private Diagram diagram = null;

	/**
	 * The initial {@link IGraphicalEditPart} with which this group was created.
	 */
	private IGraphicalEditPart editPart = null;

	/**
	 * Constructor with parameters. <br>
	 * Requires an initial {@link IGraphicalEditPart} from which information
	 * about the group will be used.
	 * 
	 * @param editPart
	 */
	public ViewGroup(IGraphicalEditPart editPart) {
		if (editPart == null) {
			throw new IllegalArgumentException("A view must be given");
		}
		this.editPart = editPart;
		View view = editPart.getNotationView();
		if (view == null) {
			throw new IllegalArgumentException("A view must be given");
		}
		this.diagram = view.getDiagram();
		ExtendedFeatureElement element = ExtendedFeatureElementFactory
				.getInstance().asExtendedFeatureElement(view);
		Integer groupID = element.getInteger(IViewGroup.ViewBelongToGroup);
		if (groupID != null) {
			this.groupID = groupID;
		}
	}

	public boolean addEditPartToGroup(IGraphicalEditPart editPart) {
		if (editPart == null) {
			return false;
		}
		return addViewToGroup(editPart.getNotationView());
	}

	public boolean addViewToGroup(View view) {
		if (view == null) {
			return false;
		}
		ExtendedFeatureElement element = ExtendedFeatureElementFactory
				.getInstance().asExtendedFeatureElement(view);
		if (element == null) {
			return false;
		}
		element.setValue(ViewBelongToGroup, groupID);
		return true;
	}

	public List<IGraphicalEditPart> getAllEditPartsInGroup() {
		List<IGraphicalEditPart> allViews = new ArrayList<IGraphicalEditPart>();
		IGraphicalEditPart diagramEditPart = DiagramEditPartsUtil
				.getDiagramEditPart(editPart);
		addEditPartsOfGroup(diagramEditPart, allViews);
		return allViews;
	}

	protected void addEditPartsOfGroup(IGraphicalEditPart parentEditPart,
			List<IGraphicalEditPart> editParts) {
		if (parentEditPart != null) {
			if (isEditPartOfGroup(parentEditPart)) {
				editParts.add(parentEditPart);
			}
			for (Object o : parentEditPart.getChildren()) {
				if (o instanceof IGraphicalEditPart) {
					addEditPartsOfGroup((IGraphicalEditPart) o, editParts);
				}
			}
		}
	}

	protected boolean isEditPartOfGroup(IGraphicalEditPart editPart) {
		if (editPart != null && editPart.getNotationView() != null) {
			return isViewOfGroup(editPart.getNotationView());
		}
		return false;
	}

	protected boolean isViewOfGroup(View view) {
		if (view != null) {
			ExtendedFeatureElement element = ExtendedFeatureElementFactory
					.getInstance().asExtendedFeatureElement(view);
			Integer group = element.getInteger(IViewGroup.ViewBelongToGroup);
			if (group != null && group.equals(this.groupID)) {
				return true;
			}
		}
		return false;
	}

	public Collection<View> getAllViewsInGroup() {
		List<View> allViews = new ArrayList<View>();
		Iterator iterator = diagram.eAllContents();
		for (; iterator.hasNext();) {
			Object object = iterator.next();
			if (object instanceof View) {
				View view = (View) object;
				if (isViewOfGroup(view)) {
					allViews.add(view);
				}
			}
		}
		return allViews;
	}

	public void addEditPartToGroup(IGraphicalEditPart editPart, int group) {
		if (editPart == null) {
			return;
		}
		addViewToGroup(editPart.getNotationView(), group);
	}

	public void addViewToGroup(View view, int group) {
		if (view == null) {
			return;
		}
		ExtendedFeatureElement element = ExtendedFeatureElementFactory
				.getInstance().asExtendedFeatureElement(view);
		if (element == null) {
			return;
		}
		element.setValue(ViewBelongToGroup, group);
	}

	public void unGroup() {
		for (IGraphicalEditPart editPart : getAllEditPartsInGroup()) {
			removeEditPart(editPart);
		}
	}

	public void removeEditPart(IGraphicalEditPart editPart) {
		if (editPart != null && editPart.getNotationView() != null) {
			unGroupView(editPart.getNotationView());
		}
	}

	public void unGroupView(View view) {
		if (view != null) {
			ExtendedFeatureElement element = ExtendedFeatureElementFactory
					.getInstance().asExtendedFeatureElement(view);
			element.unsetValue(IViewGroup.ViewBelongToGroup);
		}
	}

	public ISelection getGroupEditPartsSelection() {
		StructuredSelection selection = new StructuredSelection(
				getAllEditPartsInGroup());
		return selection;
	}

	public void selectGroupEditPartsSelection() {
		editPart.getViewer().setSelection(getGroupEditPartsSelection());
	}
}
