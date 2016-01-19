/*******************************************************************************
 * Copyright (c) 2008-2010 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – Initial implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.edit.policies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.tools.ToolUtilities;
import org.eclipse.gmf.runtime.diagram.core.util.ViewUtil;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.requests.DropObjectsRequest;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateViewRequest.ViewDescriptor;
import org.eclipse.gmf.runtime.emf.core.util.EObjectAdapter;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.Node;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.swt.dnd.DND;

import es.cv.gvcase.mdt.common.commands.AbstractCommonTransactionalCommmand;
import es.cv.gvcase.mdt.common.commands.AddDontBelongToReferencesToDiagram;
import es.cv.gvcase.mdt.common.commands.AddEObjectReferencesToDiagram;
import es.cv.gvcase.mdt.common.commands.AddIStorableElementsCommand;
import es.cv.gvcase.mdt.common.commands.UpdateDiagramCommand;

/**
 * A DragAndDropEditPolicy for <Diagram>s. Checks whether the dropped elements
 * can be shown as views in the canvas and if so, creates their views and add
 * their references to the diagram's list of references.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 */
public class DiagramDragDropEditPolicy
		extends
		org.eclipse.gmf.runtime.diagram.ui.editpolicies.DiagramDragDropEditPolicy {

	/**
	 * Stored element helper
	 */
	public static final String StoredElementHelper = "es.cv.gvcase.mdt.common.storage.storable.helper";

	/** The resolver. */
	private ViewResolver resolver = null;

	/**
	 * Instantiates a new diagram drag drop edit policy.
	 * 
	 * @param resolver
	 *            the resolver
	 */
	public DiagramDragDropEditPolicy(ViewResolver resolver) {
		this.resolver = resolver;
	}

	/**
	 * Gets the graphical host.
	 * 
	 * @return the graphical host
	 */
	public IGraphicalEditPart getGraphicalHost() {
		if (getHost() instanceof IGraphicalEditPart) {
			return (IGraphicalEditPart) getHost();
		}
		return null;
	}

	/**
	 * Retrieves the list of elements being dropped
	 * 
	 * @param request
	 *            the request
	 * @return List of elements
	 */
	@Override
	protected DropObjectsRequest castToDropObjectsRequest(
			ChangeBoundsRequest request) {
		Iterator editParts = ToolUtilities.getSelectionWithoutDependants(
				request.getEditParts()).iterator();

		List elements = new ArrayList();
		while (editParts.hasNext()) {
			EditPart editPart = (EditPart) editParts.next();
			if (editPart instanceof IGraphicalEditPart) {
				EObject element = ViewUtil
						.resolveSemanticElement((View) ((IGraphicalEditPart) editPart)
								.getModel());
				if (element != null)
					elements.add(element);
			}
		}

		DropObjectsRequest req = new DropObjectsRequest();
		req.setObjects(elements);
		req.setAllowedDetail(DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK);
		req.setLocation(request.getLocation());
		req.setRequiredDetail(getRequiredDragDetail(request));
		return req;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gmf.runtime.diagram.ui.editpolicies.DiagramDragDropEditPolicy
	 * #getDropObjectsCommand(org.eclipse.gmf.runtime.diagram.ui.requests.
	 * DropObjectsRequest)
	 */
	@Override
	public Command getDropObjectsCommand(DropObjectsRequest dropRequest) {
		// get all IStorableElements to add to this diagram
		List<EObject> storableElements = findIStorableElements(dropRequest);
		// get nodes and edges to add to this Diagram.
		List<EObject> nodeObjects = findNodesInDrop(dropRequest);
		// remove all the nodes that are already in the storable list
		nodeObjects.removeAll(storableElements);
		Command command = null;
		List<ViewDescriptor> viewDescriptors = null;
		if (nodeObjects.size() > 0) {
			viewDescriptors = createViewDescriptors(nodeObjects);
			// build commands that add references to the diagram.
			command = buildAddEObjectsReferencesCommand(nodeObjects);
		}
		List<EObject> edgeObjects = findEdgesInDrop(dropRequest);
		Command edgesCommand = null;
		if (edgeObjects.size() > 0) {
			edgesCommand = buildAddEObjectsReferencesCommand(edgeObjects);
			command = command == null ? edgesCommand : command
					.chain(edgesCommand);
		}
		// build the command that handles the IStorableElements
		if (storableElements != null && storableElements.size() > 0) {
			Command storablesCommand = buildAddIStorableElementsCommand(
					storableElements, dropRequest);
			if (command != null) {
				command = command.chain(storablesCommand);
			} else {
				command = storablesCommand;
			}
		}
		// if no nodes or edges are to be added, there is nothing to do.
		if (command == null) {
			return null;
		}
		if (viewDescriptors != null && viewDescriptors.size() > 0) {
			// build the create views commands.
			Command viewsCommand = createViewsAndArrangeCommand(dropRequest,
					viewDescriptors);
			if (viewsCommand != null && viewsCommand.canExecute()) {
				command = command.chain(viewsCommand);
			}
		}
		// update diagram.
		command = command.chain(new UpdateDiagramCommand(getGraphicalHost()));
		// return command
		return command;
	}

	/**
	 * Finds all the elements that come from a common storage. <br>
	 * These elements should receive a special treatment.
	 * 
	 * @param request
	 * @return
	 */
	protected List<EObject> findIStorableElements(DropObjectsRequest request) {
		if (request != null && request.getObjects() != null
				&& request.getObjects().size() > 0) {
			List<EObject> storableElements = new ArrayList<EObject>();
			List<Object> filteredObjects = new ArrayList<Object>();
			for (Object o : request.getObjects()) {
				if (o instanceof EModelElement) {
					if (((EModelElement) o).getEAnnotation(StoredElementHelper) != null) {
						storableElements.add((EObject) o);
					} else {
						filteredObjects.add(o);
					}
				}
			}
			request.setObjects(filteredObjects);
			return storableElements;
		} else {
			return Collections.emptyList();
		}
	}

	/**
	 * Finds the views to be dropped in the diagram.
	 * 
	 * @param request
	 * @return
	 */
	protected List<View> findViewsInDrop(DropObjectsRequest request) {
		List<View> views = new ArrayList<View>();
		for (Object object : request.getObjects()) {
			if (object instanceof View) {
				View view = (View) object;
				if (isViewOfDiagram(view)) {
					views.add(view);
				}
			}
		}
		return views;
	}

	/**
	 * Find nodes in drop.
	 * 
	 * @param request
	 *            the request
	 * 
	 * @return the list< e object>
	 */
	protected List<EObject> findNodesInDrop(DropObjectsRequest request) {
		List<EObject> nodes = new ArrayList<EObject>();
		for (Object object : request.getObjects()) {
			if (object instanceof View) {
				object = ((View) object).getElement();
			}
			if (object instanceof EObject) {
				EObject element = (EObject) object;
				if (resolver.isEObjectNode(element)) {
					nodes.add(element);
				}
			}
		}
		return nodes;
	}

	/**
	 * Find edges in drop.
	 * 
	 * @param request
	 *            the request
	 * 
	 * @return the list< e object>
	 */
	protected List<EObject> findEdgesInDrop(DropObjectsRequest request) {
		List<EObject> edges = new ArrayList<EObject>();
		for (Object object : request.getObjects()) {
			if (object instanceof EObject) {
				EObject element = (EObject) object;
				if (resolver.isEObjectLink(element)) {
					edges.add(element);
				}
			}
		}
		return edges;
	}

	protected boolean isViewOfDiagram(View view) {
		if (view == null) {
			return false;
		}
		EObject element = view.getElement();
		if (element == null) {
			return false;
		}
		try {
			int viewHint = Integer.valueOf(view.getType());
			int elementHint = resolver.getEObjectSemanticHint(element);
			if (viewHint != -1 && viewHint == elementHint) {
				return true;
			}
		} catch (NumberFormatException ex) {
			return false;
		}
		return false;
	}

	/**
	 * Creates the view descriptors.
	 * 
	 * @param elements
	 *            the elements
	 * 
	 * @return the list< view descriptor>
	 */
	protected List<ViewDescriptor> createViewDescriptors(List<EObject> elements) {
		List<ViewDescriptor> viewDescriptors = new ArrayList<ViewDescriptor>();
		for (EObject element : elements) {
			int semanticHint = resolver.getEObjectSemanticHint(element);
			if (semanticHint > -1) {
				ViewDescriptor viewDescriptor = new ViewDescriptor(
						new EObjectAdapter(element), Node.class, String
								.valueOf(semanticHint), getGraphicalHost()
								.getDiagramPreferencesHint());
				viewDescriptors.add(viewDescriptor);
			}
		}
		return viewDescriptors;
	}

	/**
	 * Builds the add e objects references command.
	 * 
	 * @param elements
	 *            the elements
	 * 
	 * @return the command
	 */
	protected Command buildAddEObjectsReferencesCommand(List<EObject> elements) {
		if (elements != null && elements.size() > 0) {
			TransactionalEditingDomain domain = getGraphicalHost()
					.getEditingDomain();
			View view = getGraphicalHost().getNotationView();
			if (view != null) {
				Diagram diagram = view.getDiagram();
				AbstractCommonTransactionalCommmand transactionalCommand = null;
				// the command to build will be different according to the type
				// of policy (whitelist | blacklist) that the editpart is using.
				if (ShowInDiagramEditPartPolicyRegistry.getInstance()
						.isEditPartWhiteListPolicy(getGraphicalHost())) {
					// in a whitelist, for an element to be visible it has to be
					// added to the list of references.
					transactionalCommand = new AddEObjectReferencesToDiagram(
							domain, diagram, elements);
				} else {
					// in a blacklist, for an element to be visible it must not
					// appear in the list of references.
					transactionalCommand = new AddDontBelongToReferencesToDiagram(
							domain, diagram, elements);
				}
				return transactionalCommand.toGEFCommand();
			}
		}
		return null;
	}

	protected Command buildAddIStorableElementsCommand(
			List<EObject> storableElements, DropObjectsRequest dropRequest) {
		return new AddIStorableElementsCommand(getGraphicalHost()
				.getEditingDomain(), storableElements, getGraphicalHost(),
				dropRequest.getLocation()).toGEFCommand();
	}

}
