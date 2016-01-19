/***************************************************************************
 * Copyright (c) 2008 - 2011 Conselleria de Infraestructuras y Transporte,
 * Generalitat de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – initial API and implementation
 * 				 Marc Gil Sendra (Prodevelop) - externalize the class
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.diagram.ui.commands.CreateCommand;
import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateViewRequest.ViewDescriptor;
import org.eclipse.gmf.runtime.emf.core.util.EObjectAdapter;
import org.eclipse.gmf.runtime.notation.Bounds;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.Node;
import org.eclipse.gmf.runtime.notation.NotationFactory;
import org.eclipse.gmf.runtime.notation.View;

import es.cv.gvcase.mdt.common.commands.wrappers.GMFtoEMFCommandWrapper;
import es.cv.gvcase.mdt.common.edit.policies.ViewAndFeatureResolver;
import es.cv.gvcase.mdt.common.edit.policies.ViewResolver;
import es.cv.gvcase.mdt.common.provider.IDiagramLinksViewInfo;
import es.cv.gvcase.mdt.common.provider.ILinkDescriptor;
import es.cv.gvcase.mdt.common.util.DiagramEditPartsUtil;
import es.cv.gvcase.mdt.common.util.MDTUtil;
import es.cv.gvcase.mdt.common.util.MultiDiagramUtil;

public class RestoreViewCommand extends AbstractCommonTransactionalCommmand {

	private IGraphicalEditPart finalEditPartToUse = null;

	protected static final Point OffsetPerIndex = new Point(30, 30);

	public RestoreViewCommand(IGraphicalEditPart editPart,
			TransactionalEditingDomain domain, String label) {
		super(domain, label, null);
		this.finalEditPartToUse = editPart;
	}

	@Override
	protected CommandResult doExecuteWithResult(IProgressMonitor monitor,
			IAdaptable info) throws ExecutionException {
		View viewToUse = finalEditPartToUse.getNotationView();
		// add all eContents of the selected view's semantic model
		// element.
		Diagram diagram = viewToUse.getDiagram();
		EObject eObject = viewToUse.getElement();
		if (diagram == null || eObject == null) {
			return CommandResult
					.newErrorCommandResult("No diagram or EObject available");
		}
		// List of additional elements that need their contents updated
		List<EObject> elementsThatNeedUpdate = new ArrayList<EObject>();
		// Add all elements links.
		// This will add those elements from other resources that are
		// related to the element being updated to the diagram
		addAllElementLinks(finalEditPartToUse, elementsThatNeedUpdate);
		// add the elements that need an update now
		boolean intermediateRefreshRequired = false;
		for (EObject eObjectToUpdate : elementsThatNeedUpdate) {
			addAllElementChildren(diagram, eObjectToUpdate);
			MultiDiagramUtil.AddEAnnotationReferenceToDiagram(diagram,
					eObjectToUpdate);
			intermediateRefreshRequired = true;
		}
		if (intermediateRefreshRequired) {
			// Refresh the diagram for the creation of the edit parts of
			// the new views
			DiagramEditPartsUtil.updateDiagram(finalEditPartToUse);
		}
		if (!(viewToUse instanceof Diagram)) {
			// Add all elements children only when the view to restore
			// is not diagram.
			// This is done to prevent that all the elements of a model
			// are added to a diagram when it is a small view of a
			// rather large model
			addAllElementChildren(finalEditPartToUse);
		} else {
			// If the view to restore is a Diagram, add all the child
			// elements of the already existing views to the diagram
			Map editPartsRegistry = null;
			IGraphicalEditPart childEditPart = null;
			for (Object child : viewToUse.getChildren()) {
				if (child instanceof View) {
					editPartsRegistry = finalEditPartToUse.getViewer()
							.getEditPartRegistry();
					childEditPart = (IGraphicalEditPart) editPartsRegistry
							.get(child);
					if (childEditPart != null) {
						addAllElementChildren(childEditPart);
					}
				}
			}
		}
		// refresh the affected editPart
		DiagramEditPartsUtil.updateEditPartAndChildren(finalEditPartToUse,
				EcorePackage.eINSTANCE.getEObject());
		// refresh the diagram for the links
		DiagramEditPartsUtil.updateDiagram(finalEditPartToUse);
		// every went ok
		return CommandResult.newOKCommandResult();
	}

	protected void addAllElementChildren(IGraphicalEditPart editPart) {
		View viewToUse = finalEditPartToUse.getNotationView();
		Diagram diagram = viewToUse.getDiagram();
		EObject eObject = editPart.resolveSemanticElement();
		addAllElementChildren(diagram, eObject);
	}

	protected void addAllElementChildren(Diagram diagram, EObject eObject) {
		if (eObject == null) {
			return;
		}
		for (Iterator<EObject> iterator = eObject.eAllContents(); iterator
				.hasNext();) {
			MultiDiagramUtil.AddEAnnotationReferenceToDiagram(diagram, iterator
					.next(), false);
		}
	}

	protected void addAllElementLinks(IGraphicalEditPart editPart,
			List<EObject> elementsThatNeedUpdate) {
		DiagramEditPart diagramEditPart = DiagramEditPartsUtil
				.getDiagramEditPart(editPart);
		View viewToUse = editPart.getNotationView();
		Diagram diagram = viewToUse.getDiagram();
		IDiagramLinksViewInfo adapted = (IDiagramLinksViewInfo) diagramEditPart
				.getAdapter(IDiagramLinksViewInfo.class);
		EObject candidateForView = null;
		if (adapted instanceof IDiagramLinksViewInfo) {
			List<EObject> objectsThatNeedView = new ArrayList<EObject>();
			IDiagramLinksViewInfo diagramLinksViewInfo = (IDiagramLinksViewInfo) adapted;
			List<ILinkDescriptor> linkDescriptors = diagramLinksViewInfo
					.getAllLinks(viewToUse);
			Resource diagramElementResource = diagram.getElement().eResource();
			for (ILinkDescriptor linkDescriptor : linkDescriptors) {
				// Add all the link elements to the eannotation to be
				// drawn in the diagram
				if (linkDescriptor.getModelElement() != null) {
					MultiDiagramUtil.AddEAnnotationReferenceToDiagram(diagram,
							linkDescriptor.getModelElement(), false);
				}
				// Find all those source or target elements that are not
				// in the same resource as the diagram and create views
				// for them.
				candidateForView = linkDescriptor.getSource();
				if (!isFromResource(candidateForView, diagramElementResource)) {
					if (!objectsThatNeedView.contains(candidateForView)) {
						objectsThatNeedView.add(candidateForView);
					}
				}
				candidateForView = linkDescriptor.getDestination();
				if (!isFromResource(candidateForView, diagramElementResource)) {
					if (!objectsThatNeedView.contains(candidateForView)) {
						objectsThatNeedView.add(candidateForView);
					}
				}
			}
			// Create the views for the elements from other resources
			// that need them
			createViewsForElements(diagramEditPart, objectsThatNeedView);
			// Store the objects for which we have created views as
			// objects that need their contents updated
			elementsThatNeedUpdate.addAll(objectsThatNeedView);
		}
	}

	protected void createViewsForElements(DiagramEditPart diagramEditPart,
			List<EObject> elementsThatNeedView) {
		if (diagramEditPart == null
				|| diagramEditPart.getNotationView() == null
				|| elementsThatNeedView == null
				|| elementsThatNeedView.size() <= 0) {
			return;
		}
		Diagram diagram = diagramEditPart.getDiagramView();
		ViewResolver viewResolver = (ViewResolver) diagramEditPart
				.getAdapter(ViewAndFeatureResolver.class);
		TransactionalEditingDomain domain = diagramEditPart.getEditingDomain();
		if (diagram == null || viewResolver == null || domain == null) {
			return;
		}
		String semanticHint = null;
		Point basePoint = calculateBasePointForNewFigures();
		Point newFigureLocation = null;
		int index = 1;
		// Create views for the given elements that need a view.
		for (EObject eObject : elementsThatNeedView) {
			if (!MultiDiagramUtil.findEObjectReferencedInEAnnotation(diagram,
					eObject)) {
				semanticHint = ""
						+ viewResolver.getEObjectSemanticHint(eObject);
				ViewDescriptor viewDescriptor = new ViewDescriptor(
						new EObjectAdapter(eObject), Node.class, semanticHint,
						true, MDTUtil.getPreferencesHint(diagram.getType()));
				CreateCommand createCommand = new CreateCommand(domain,
						viewDescriptor, diagram);
				domain.getCommandStack().execute(
						new GMFtoEMFCommandWrapper(createCommand));
				View createdView = (View) viewDescriptor.getAdapter(View.class);
				if (createdView instanceof Node) {
					// set new bounds for figure.
					newFigureLocation = calculateLocationForNewFigure(
							basePoint, index, OffsetPerIndex);
					setNodeAtLocation((Node) createdView, newFigureLocation);
					//
					index++;
				}
			}
		}
	}

	protected boolean isFromResource(EObject eObject, Resource resource) {
		if (eObject == null || eObject.eResource() == null || resource == null) {
			return false;
		}
		Resource eObjectResource = eObject.eResource();
		return eObjectResource.equals(resource);
	}

	protected void setNodeAtLocation(Node node, Point location) {
		Bounds bounds = null;
		if (node.getLayoutConstraint() instanceof Bounds) {
			bounds = (Bounds) node.getLayoutConstraint();
		} else {
			bounds = NotationFactory.eINSTANCE.createBounds();
		}
		bounds.setX(location.x);
		bounds.setY(location.y);
		node.setLayoutConstraint(bounds);
	}

	protected Point calculateLocationForNewFigure(Point basePoint, int index,
			Point offsetPerIndex) {
		Point newLocation = basePoint.getCopy();
		newLocation.x += offsetPerIndex.x * index;
		newLocation.y += offsetPerIndex.y * index;
		return newLocation;
	}

	protected Point calculateBasePointForNewFigures() {
		Point basePoint = finalEditPartToUse.getFigure().getBounds()
				.getLocation();
		basePoint.x += finalEditPartToUse.getFigure().getSize().width;
		basePoint.y += finalEditPartToUse.getFigure().getSize().height;
		return basePoint;
	}

}