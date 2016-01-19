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
import java.util.List;

import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.commands.UnexecutableCommand;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gmf.runtime.diagram.core.commands.DeleteCommand;
import org.eclipse.gmf.runtime.diagram.ui.commands.ICommandProxy;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.requests.ArrangeRequest;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateViewRequest;
import org.eclipse.gmf.runtime.diagram.ui.requests.DropObjectsRequest;
import org.eclipse.gmf.runtime.diagram.ui.requests.RefreshConnectionsRequest;
import org.eclipse.gmf.runtime.diagram.ui.requests.RequestConstants;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateViewRequest.ViewDescriptor;
import org.eclipse.gmf.runtime.emf.core.util.EObjectAdapter;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.Node;
import org.eclipse.gmf.runtime.notation.View;

import es.cv.gvcase.mdt.common.commands.AbstractCommonTransactionalCommmand;
import es.cv.gvcase.mdt.common.commands.AddDontBelongToReferencesToDiagram;
import es.cv.gvcase.mdt.common.commands.AddEObjectReferencesToDiagram;
import es.cv.gvcase.mdt.common.commands.AddIStorableElementsCommand;
import es.cv.gvcase.mdt.common.commands.UpdateDiagramCommand;
import es.cv.gvcase.mdt.common.util.DiagramEditPartsUtil;
import es.cv.gvcase.mdt.common.util.MDTUtil;

/**
 * A DragAndDropEditPolicy for compartment nodes. Checks whether the dropped
 * elements can be shown as views in the compartment and if so, created their
 * views and add their references to the diagram's list of references.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 */
public class DragDropEditPolicy extends
		org.eclipse.gmf.runtime.diagram.ui.editpolicies.DragDropEditPolicy {

	/** The view resolver. */
	private ViewResolver viewResolver = null;

	/**
	 * Instantiates a new drag drop edit policy.
	 * 
	 * @param resolver
	 *            the resolver
	 */
	public DragDropEditPolicy(ViewResolver resolver) {
		this.viewResolver = resolver;
	}

	/**
	 * Gets the view resolver.
	 * 
	 * @return the view resolver
	 */
	protected ViewResolver getViewResolver() {
		return viewResolver;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.gmf.runtime.diagram.ui.editpolicies.DragDropEditPolicy#
	 * getDropCommand(org.eclipse.gef.requests.ChangeBoundsRequest)
	 */
	@Override
	protected Command getDropCommand(ChangeBoundsRequest request) {
		if (request.getType() != null
				&& request.getType().equals(RequestConstants.REQ_DROP)) {
			// for each EditPart, get its semantic element and check if there's
			// any View available for that kind of element in this container.
			for (Object o : request.getEditParts()) {
				EObject element = MDTUtil.resolveSemantic(o);
				if (getViewResolver().isEObjectNode(element) == false) {
					return UnexecutableCommand.INSTANCE;
				}
			}
		}
		return super.getDropCommand(request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.gmf.runtime.diagram.ui.editpolicies.DragDropEditPolicy#
	 * getDropObjectsCommand
	 * (org.eclipse.gmf.runtime.diagram.ui.requests.DropObjectsRequest)
	 */
	@Override
	public Command getDropObjectsCommand(DropObjectsRequest dropRequest) {
		// get nodes and edges to add to this Diagram.
		List<EObject> storableElements = findIStorableElements(dropRequest);
		List<EObject> nodeObjects = findNodesInDrop(dropRequest);
		nodeObjects.removeAll(storableElements);
		List<ViewDescriptor> viewDescriptors = createViewDescriptors(nodeObjects);
		List<EObject> edgeObjects = findEdgesInDrop(dropRequest);
		Command command = buildDropCommand(dropRequest, nodeObjects,
				viewDescriptors, edgeObjects, storableElements);
		return command;
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
			if (object instanceof EObject) {
				EObject element = (EObject) object;
				if (getViewResolver().isEObjectNode(element)) {
					nodes.add(element);
				}
			}
		}
		return nodes;
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
					if (((EModelElement) o)
							.getEAnnotation(DiagramDragDropEditPolicy.StoredElementHelper) != null) {
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
				if (getViewResolver().isEObjectLink(element)) {
					edges.add(element);
				}
			}
		}
		return edges;
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
			int semanticHint = viewResolver.getEObjectSemanticHint(element);
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
	 * Builds the drop command.
	 * 
	 * @param request
	 *            the request
	 * @param nodes
	 *            the nodes
	 * @param views
	 *            the views
	 * @param edges
	 *            the edges
	 * 
	 * @return the command
	 */
	protected Command buildDropCommand(DropObjectsRequest request,
			List<EObject> nodes, List<ViewDescriptor> views,
			List<EObject> edges, List<EObject> storableElements) {
		CompoundCommand cc = new CompoundCommand();

		// call the hook method of Drag&DropResolvers to ask for additional
		// Commands that should be executed.
		if (viewResolver instanceof DragAndDropResolver) {
			cc.add(getDragAndDropResolverCommands(request));
		}

		// remove the old view
		Command remove1 = removeOldViewsCommand(nodes);
		// if no nodes or edges are to be added, there is nothing to do.
		if (remove1 != null && remove1.canExecute()) {
			cc.add(remove1);
		}
		Command remove2 = removeOldViewsCommand(edges);
		if (remove2 != null && remove2.canExecute()) {
			cc.add(remove2);
		}

		// build the command that adds the IStorableElements
		Command storablesCommand = buildAddIStorableElementsCommand(
				storableElements, request);
		if (storablesCommand != null && storablesCommand.canExecute()) {
			cc.add(storablesCommand);
		}

		// build commands that add references to the diagram.
		Command nodesCommand = buildAddEObjectsReferencesCommand(nodes);
		if (nodesCommand != null && nodesCommand.canExecute()) {
			cc.add(nodesCommand);
		}
		Command edgesCommand = buildAddEObjectsReferencesCommand(edges);
		if (edgesCommand != null && edgesCommand.canExecute()) {
			cc.add(edgesCommand);
		}

		// build the create views commands.
		Command viewsCommand = createViewsAndArrangeCommand(request, views);
		if (viewsCommand != null && viewsCommand.canExecute()) {
			cc.add(viewsCommand);
		}

		// update diagram.
		cc.add(new UpdateDiagramCommand(getGraphicalHost()));

		// return command
		return cc.unwrap();
	}

	/**
	 * Get the additional commands to be executed when drag and dropping to a
	 * specific EditPart. <br>
	 * These additional commands are specified by the
	 * {@link DragAndDropResolver}.
	 * 
	 * @param request
	 * @return
	 */
	protected Command getDragAndDropResolverCommands(DropObjectsRequest request) {
		IGraphicalEditPart hostEditPart = getGraphicalHost();
		DragAndDropResolver resolver = viewResolver instanceof DragAndDropResolver ? (DragAndDropResolver) viewResolver
				: null;
		if (hostEditPart == null || resolver == null || request == null
				|| request.getObjects().size() <= 0) {
			return null;
		}
		CompoundCommand cc = new CompoundCommand(
				"Get DragAndDrop resolver commands");
		AbstractCommonTransactionalCommmand transactionalCommand = null;
		for (Object o : request.getObjects()) {
			if (o instanceof EObject) {
				transactionalCommand = resolver
						.getDroppingElementResolverCommand((EObject) o,
								hostEditPart);
				if (transactionalCommand != null) {
					cc.add(transactionalCommand.toGEFCommand());
				}
			}
		}
		if (cc.canExecute() && !cc.isEmpty()) {
			return cc;
		}
		return null;
	}

	/**
	 * Removes the old views from the diagram
	 * 
	 * @param elements
	 * @return
	 */
	protected Command removeOldViewsCommand(List<EObject> elements) {
		if (elements != null && elements.size() <= 0) {
			return null;
		}

		CompoundCommand cc = new CompoundCommand();
		for (EObject eo : elements) {
			List l = DiagramEditPartsUtil.getEObjectViews(eo);
			if (l.size() > 0) {
				DeleteCommand c = new DeleteCommand((View) l.get(0));
				cc.add(new ICommandProxy(c));
			}
		}
		if (cc.size() == 0)
			return new CompoundCommand("nothing");
		else
			return cc;
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

	/**
	 * createViewsAndArrangeCommand Method to create all the view based on the
	 * viewDescriptors list and provide a default arrangement of them.
	 * 
	 * @param dropRequest
	 *            the drop request
	 * @param viewDescriptors
	 *            the view descriptors
	 * 
	 * @return command
	 */
	protected Command createViewsAndArrangeCommand(
			DropObjectsRequest dropRequest, List viewDescriptors) {
		CreateViewRequest createViewRequest = new CreateViewRequest(
				viewDescriptors);
		createViewRequest.setLocation(dropRequest.getLocation());
		Command createCommand = getHost().getCommand(createViewRequest);

		if (createCommand != null) {
			List result = (List) createViewRequest.getNewObject();
			dropRequest.setResult(result);

			RefreshConnectionsRequest refreshRequest = new RefreshConnectionsRequest(
					result);
			Command refreshCommand = getHost().getCommand(refreshRequest);

			ArrangeRequest arrangeRequest = new ArrangeRequest(
					RequestConstants.REQ_ARRANGE_DEFERRED);
			arrangeRequest.setViewAdaptersToArrange(result);
			Command arrangeCommand = getHost().getCommand(arrangeRequest);

			CompoundCommand cc = new CompoundCommand(createCommand.getLabel());
			cc.add(createCommand.chain(refreshCommand));
			cc.add(arrangeCommand);

			return cc;
		}
		return null;
	}
}
