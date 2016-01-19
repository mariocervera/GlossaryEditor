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
package es.cv.gvcase.mdt.common.actions;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.UnexecutableCommand;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.gef.tools.ToolUtilities;
import org.eclipse.gmf.runtime.diagram.ui.actions.ActionIds;
import org.eclipse.gmf.runtime.diagram.ui.actions.DiagramAction;
import org.eclipse.gmf.runtime.diagram.ui.editparts.GroupEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import es.cv.gvcase.mdt.common.Activator;
import es.cv.gvcase.mdt.common.edit.parts.group.IViewGroup;
import es.cv.gvcase.mdt.common.edit.parts.group.ViewGroupRegistry;
import es.cv.gvcase.mdt.common.edit.policies.GroupSelectionEditPolicy;

/**
 * Action to group / ungroup views in diagrams.
 * 
 * @author <a href="mailto:fjcano@prodeelop.es">Francisco Javier Cano Muñoz</a>
 * 
 * @see IViewGroup
 * @see ViewGroupRegistry
 * @see GroupSelectionEditPolicy
 * 
 */
public class GroupViewsAction extends DiagramAction {

	public GroupViewsAction(IWorkbenchPage page) {
		super(page);
		ImageDescriptor disabledImage = getImageDescriptor("icons/GroupDisabled.png");
		setDisabledImageDescriptor(disabledImage);
	}

	protected ImageDescriptor groupImageDescriptor;

	protected ImageDescriptor getGroupImageDescriptor() {
		if (groupImageDescriptor == null) {
			groupImageDescriptor = getImageDescriptor("icons/Group.png");
		}
		return groupImageDescriptor;
	}

	protected ImageDescriptor ungroupImageDescriptor;

	protected ImageDescriptor getUngroupImageDescriptor() {
		if (ungroupImageDescriptor == null) {
			ungroupImageDescriptor = getImageDescriptor("icons/Ungroup.png");
		}
		return ungroupImageDescriptor;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		if (isGroupSelected()) {
			return getUngroupImageDescriptor();
		} else {
			return getGroupImageDescriptor();
		}
	}

	@Override
	public String getText() {
		if (isGroupSelected()) {
			return "Ungroup";
		} else {
			return "Group";
		}
	}

	@Override
	public String getToolTipText() {
		if (isGroupSelected()) {
			return "Ungroup selected elements";
		} else {
			return "Group selected elements";
		}
	}

	protected boolean isGroupSelected() {
		List selectedObjects = getSelectedObjects();
		if (selectedObjects == null || selectedObjects.size() <= 0) {
			return false;
		}
		Object selectedFirst = selectedObjects.get(0);
		if (selectedFirst instanceof org.eclipse.gmf.runtime.diagram.ui.editparts.GroupEditPart) {
			return true;
		}
		return false;
	}

	protected ImageDescriptor getImageDescriptor(String imagePath) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
				imagePath);
	}

	@Override
	protected Request createTargetRequest() {
		if (isGroupSelected()) {
			// the request for a group is an Ungroup Request
			return new Request(ActionIds.ACTION_UNGROUP);
		} else {
			// the request for a bunch of edit parts is a Group request
			GroupRequest request = new GroupRequest(ActionIds.ACTION_GROUP);
			request.setEditParts(getOperationSet());
			return request;
		}
	}

	@Override
	protected void updateTargetRequest() {
		setTargetRequest(createTargetRequest());
	}

	@Override
	protected List createOperationSet() {
		List selection = getSelectedObjects();
		if (selection.size() == 1 && selection.get(0) instanceof GroupEditPart) {
			return ToolUtilities.getSelectionWithoutDependants(selection);
		}
		if (selection.size() <= 1
				|| !(selection.get(0) instanceof IGraphicalEditPart)) {
			return Collections.EMPTY_LIST;
		}
		return ToolUtilities.getSelectionWithoutDependants(selection);
	}

	@Override
	protected Command getCommand() {
		if (!isGroupSelected() && getOperationSet().size() > 1) {
			EditPart parent = ((EditPart) getOperationSet().get(0)).getParent();
			return parent.getCommand(getTargetRequest());
		} else if (isGroupSelected()) {
			return super.getCommand();
		}
		return UnexecutableCommand.INSTANCE;
	}

	@Override
	protected boolean isSelectionListener() {
		return true;
	}

	@Override
	protected void doRun(IProgressMonitor progressMonitor) {
		super.doRun(progressMonitor);
		if (getTargetRequest() instanceof GroupRequest) {
			// now select the new group if the action was of grouping
			Object model = ((EditPart) ((GroupRequest) getTargetRequest())
					.getEditParts().get(0)).getModel();
			if (model instanceof View) {
				Object groupView = ((View) model).eContainer();
				final Object groupEP = getDiagramGraphicalViewer()
						.getEditPartRegistry().get(groupView);
				if (groupEP != null) {
					getDiagramGraphicalViewer().setSelection(
							new StructuredSelection(groupEP));
					getDiagramGraphicalViewer().reveal((EditPart) groupEP);
				}
			}
		}
	}

}
