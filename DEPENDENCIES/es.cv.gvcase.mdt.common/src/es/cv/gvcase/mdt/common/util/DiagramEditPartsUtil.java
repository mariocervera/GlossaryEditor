/*******************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – Initial implementation.
 * 				 Marc Gil Sendra (Prodevelop) - select the diagramEditPart from a View
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gmf.runtime.diagram.ui.editparts.CompartmentEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ITextAwareEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.CanonicalEditPolicy;
import org.eclipse.gmf.runtime.diagram.ui.parts.DiagramEditor;
import org.eclipse.gmf.runtime.diagram.ui.parts.IDiagramGraphicalViewer;
import org.eclipse.gmf.runtime.diagram.ui.parts.IDiagramWorkbenchPart;
import org.eclipse.gmf.runtime.diagram.ui.requests.ArrangeRequest;
import org.eclipse.gmf.runtime.diagram.ui.requests.RequestConstants;
import org.eclipse.gmf.runtime.emf.core.util.EMFCoreUtil;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.NotationPackage;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import es.cv.gvcase.mdt.common.commands.wrappers.GEFtoEMFCommandWrapper;
import es.cv.gvcase.mdt.common.diagram.editparts.ListCompartmentEditPart;

/**
 * Different utils to manage and manipulate edit parts in diagrams.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 */
public class DiagramEditPartsUtil {

	/** The Constant BelongToDiagramSource. */
	public static final String BelongToDiagramSource = "es.cv.gvcase.mdt.uml2.diagram.common.Belongs_To_This_Diagram";

	/** EAnnotation Source for diagrams that grow from this a view. */
	public static final String DiagramsRelatedToElement = "es.cv.gvcase.mdt.uml2.diagram.common.DiagramsRelatedToElement";

	/**
	 * Gets a list of all {@link GraphicalEditPart}s in the {@link Diagram} of
	 * the given {@link EditPart}.
	 * 
	 * @param editPart
	 *            Any <code>EditPart</code> in the diagram. The TopGraphicalNode
	 *            will be found from this.
	 * 
	 * @return a list containing all <code>GraphicalEditPart</code> in the
	 *         diagram.
	 */
	public static List<IGraphicalEditPart> getAllEditParts(EditPart editPart) {

		if (editPart == null) {
			return null;
		}

		EditPart topEditPart = getTopMostEditPart(editPart);

		List<IGraphicalEditPart> editParts = new ArrayList<IGraphicalEditPart>();

		if (editPart instanceof IGraphicalEditPart) {
			editParts.add((IGraphicalEditPart) editPart);
		}
		addEditPartGraphicalChildren(editPart, editParts);

		return editParts;
	}

	/**
	 * Returns the upper most {@link EditPart} in the hierarchy of the given
	 * EditPart.
	 * 
	 * @param editPart
	 *            A non-null EditPart
	 * 
	 * @return The upper most EditPart in the hierarchy; may be the same
	 *         EditPart
	 */
	public static EditPart getTopMostEditPart(EditPart editPart) {

		if (editPart == null) {
			return null;
		}

		EditPart actual, parent;

		actual = editPart;

		while ((parent = actual.getParent()) != null) {
			actual = parent;
		}

		return actual;
	}

	/**
	 * Gets the {@link Diagram} {@link EditPart}.
	 * 
	 * @param editPart
	 *            the edit part
	 * 
	 * @return the diagram edit part
	 */
	public static DiagramEditPart getDiagramEditPart(EditPart editPart) {
		if (editPart == null) {
			return null;
		}

		if (editPart instanceof IGraphicalEditPart) {
			IGraphicalEditPart graphicalEditPart = (IGraphicalEditPart) editPart;
			View view = graphicalEditPart.getNotationView();
			Diagram diagram = view.getDiagram();
			if (graphicalEditPart.getViewer() != null) {
				Object object = graphicalEditPart.getViewer()
						.getEditPartRegistry().get(diagram);
				if (object instanceof DiagramEditPart) {
					return (DiagramEditPart) object;
				}
			}
		}

		if (editPart instanceof DiagramEditPart) {
			return (DiagramEditPart) editPart;
		}

		EditPart actual = editPart;
		EditPart parent = null;
		while ((parent = actual.getParent()) != null) {
			if (parent instanceof DiagramEditPart) {
				return (DiagramEditPart) parent;
			} else {
				actual = parent;
			}
		}

		return null;
	}

	/**
	 * Handle {@link Notification} for {@link Diagram}.
	 * 
	 * @param editPart
	 *            the edit part
	 * @param notification
	 *            the notification
	 * @param features
	 *            the features
	 */
	public static void handleNotificationForDiagram(
			IGraphicalEditPart editPart, Notification notification,
			List<EStructuralFeature> features) {
		EObject element = editPart.resolveSemanticElement();
		Object notifier = notification.getNotifier();
		Object feature = notification.getFeature();
		Object oldValue = notification.getOldValue();
		Object newValue = notification.getNewValue();
		if (notifier != null && notifier == element) {
			if (feature != null && oldValue != newValue
					&& features.contains(feature)) {
				DiagramEditPart activeDiagramEditPart = DiagramEditPartsUtil
						.getDiagramEditPart();
				DiagramEditPart diagramEditPart = DiagramEditPartsUtil
						.getDiagramEditPart(editPart);
				if (activeDiagramEditPart.equals(diagramEditPart)) {
					DiagramEditPartsUtil.updateDiagram(editPart);
				}

			}

		}
	}

	/**
	 * Handle {@link Notification} for {@link View}.
	 * 
	 * @param editPart
	 *            the edit part
	 * @param notification
	 *            the notification
	 * @param features
	 *            the features
	 */
	public static void handleNotificationForView(IGraphicalEditPart editPart,
			Notification notification, List<EStructuralFeature> features) {
		EObject element = editPart.resolveSemanticElement();
		Object notifier = notification.getNotifier();
		Object feature = notification.getFeature();
		Object oldValue = notification.getOldValue();
		Object newValue = notification.getNewValue();
		// if the editPart is a listCompartment, make sure it is added to the
		// diagram references so that the new element is shown.
		if (notifier != null && notifier == element) {
			if (feature != null && oldValue != newValue
					&& features.contains(feature)) {
				addNewValuesToDiagramReferences(editPart, newValue);
				DiagramEditPartsUtil.updateEditPart(editPart);
			}
		}
	}

	protected static void addNewValuesToDiagramReferences(
			IGraphicalEditPart editPart, Object newValue) {
		if (editPart instanceof ListCompartmentEditPart) {
			Collection newValuesCollection = null;
			if (newValue instanceof EObject) {
				newValuesCollection = Collections
						.singletonList((EObject) newValue);
			}
			if (newValue instanceof Collection) {
				newValuesCollection = (Collection) newValue;
			}
			if (newValuesCollection != null) {
				Diagram diagram = DiagramEditPartsUtil
						.findDiagramFromEditPart(editPart);
				for (Object o : newValuesCollection) {
					if (o instanceof EObject) {
						MultiDiagramUtil.AddEAnnotationReferenceToDiagram(
								diagram, (EObject) o, false);
					}
				}
			}
		}
	}

	/**
	 * Update a {@link View}.
	 * 
	 * @param view
	 *            the view
	 */
	public static void updateDiagram(View view) {
		if (view == null) {
			return;
		}
		view = view.getDiagram();
		if (view == null) {
			return;
		}
		EObject element = view.getElement();
		if (element == null) {
			return;
		}

		List editPolicies = CanonicalEditPolicy
				.getRegisteredEditPolicies(element);
		for (Iterator it = editPolicies.iterator(); it.hasNext();) {
			CanonicalEditPolicy nextEditPolicy = (CanonicalEditPolicy) it
					.next();
			nextEditPolicy.refresh();
		}
	}

	/**
	 * Update {@link Diagram}.
	 * 
	 * @param editPart
	 *            any edit part in the <Diagram>
	 */
	public static void updateDiagram(IGraphicalEditPart editPart) {
		DiagramEditPart diagramEditPart = getDiagramEditPart(editPart);
		if (diagramEditPart != null) {
			updateEditPartAndChildren(diagramEditPart, EcorePackage.eINSTANCE
					.getEObject());
		}
	}

	/**
	 * Update {@link EditPart}.
	 * 
	 * @param editPart
	 *            the edit part
	 */
	public static void updateEditPart(IGraphicalEditPart editPart) {
		if (editPart == null) {
			return;
		}
		View view = editPart.getNotationView();
		if (view == null) {
			return;
		}
		EObject element = view.getElement();
		if (element == null) {
			return;
		}

		List editPolicies = CanonicalEditPolicy
				.getRegisteredEditPolicies(element);
		for (Iterator it = editPolicies.iterator(); it.hasNext();) {
			CanonicalEditPolicy nextEditPolicy = (CanonicalEditPolicy) it
					.next();
			nextEditPolicy.refresh();
		}
	}

	/**
	 * Update {@link EditPart} and all contained EditPart that share the same
	 * type of model element.
	 * 
	 * @param editPart
	 *            the edit part
	 * @param eClass
	 *            the e class
	 */
	public static void updateEditPartAndChildren(IGraphicalEditPart editPart,
			EClass eClass) {
		if (editPart == null) {
			return;
		}
		View view = editPart.getNotationView();
		if (view == null) {
			return;
		}

		List<EditPart> children = null;
		List<EditPart> newChildren = null;

		// children = copyList(editPart.getChildren());
		// update all children
		List<Object> childEditParts = new ArrayList<Object>();
		for (Object o : editPart.getChildren()) {
			childEditParts.add(o);
		}
		for (Object child : childEditParts) {
			if (child instanceof IGraphicalEditPart) {
				updateEditPartAndChildren(((IGraphicalEditPart) child), eClass);
			}
		}

		// update via the canonical edit policies
		EObject element = view.getElement();
		if (eClass != null && eClass.isInstance(element)) {
			List editPolicies = CanonicalEditPolicy
					.getRegisteredEditPolicies(element);
			for (Iterator it = editPolicies.iterator(); it.hasNext();) {
				CanonicalEditPolicy nextEditPolicy = (CanonicalEditPolicy) it
						.next();
				nextEditPolicy.refresh();
			}
		}

	}

	public static void arrangeNewChildren(IGraphicalEditPart editPart,
			List newChildren) {
		if (editPart == null || newChildren == null || newChildren.isEmpty()) {
			return;
		}
		List<EditPart> editParts = new ArrayList<EditPart>();
		for (Object o : newChildren) {
			if (o instanceof EditPart && !(o instanceof ConnectionEditPart)
					&& !(o instanceof CompartmentEditPart)) {
				editParts.add((EditPart) o);
			}
		}
		if (editParts == null || editParts.isEmpty()) {
			return;
		}
		ArrangeRequest arrangeRequest = new ArrangeRequest(
				RequestConstants.REQ_ARRANGE_DEFERRED);
		arrangeRequest.setPartsToArrange(editParts);
		arrangeRequest.setViewAdaptersToArrange(editParts);
		Command command = editPart.getCommand(arrangeRequest);
		if (command != null) {
			editPart.getEditingDomain().getCommandStack().execute(
					new GEFtoEMFCommandWrapper(command));
		}
	}

	/**
	 * Gets the different elements from list1 with respect to list2.
	 * 
	 * @param list1
	 * @param list2
	 * @return
	 */
	public static List findDifferentInLists(List list1, List list2) {
		if (list1 == null || list2 == null) {
			return Collections.emptyList();
		}
		List differentList = new ArrayList();
		for (Object o : list1) {
			if (!list2.contains(o)) {
				differentList.add(o);
			}
		}
		for (Object o : list2) {
			if (!list1.contains(o)) {
				differentList.add(o);
			}
		}
		return differentList;
	}

	/**
	 * Copies a {@link List} into a new List.
	 * 
	 * @param objects
	 * @return
	 */
	public static List copyList(List objects) {
		if (objects == null) {
			return null;
		}
		List newList = new ArrayList(objects.size());
		for (Object o : objects) {
			newList.add(o);
		}
		return newList;
	}

	/**
	 * Adds the {@link EditPart} graphical children.
	 * 
	 * @param editPart
	 *            the edit part
	 * @param list
	 *            the list
	 */
	private static void addEditPartGraphicalChildren(EditPart editPart,
			List<IGraphicalEditPart> list) {

		if (editPart == null) {
			return;
		}

		List<EditPart> children = editPart.getChildren();

		for (EditPart ep : children) {
			if (ep instanceof IGraphicalEditPart) {
				list.add((IGraphicalEditPart) ep);
			}
			addEditPartGraphicalChildren(ep, list);
		}
	}

	// Code extracted from getViewReferers in CanonicalEditPolicy
	/**
	 * Gets the e object {@link View}s.
	 * 
	 * @param element
	 *            the element
	 * 
	 * @return the e object views
	 */
	public static List getEObjectViews(EObject element) {
		List views = new ArrayList();
		if (element != null) {
			EReference[] features = { NotationPackage.eINSTANCE
					.getView_Element() };
			views.addAll(EMFCoreUtil.getReferencers(element, features));
		}
		return views;
	}

	/**
	 * Finds the {@link EditPart}s for the {@link EObject}s in the selection.
	 * 
	 * @param selection
	 *            the selection
	 * @param viewer
	 *            the viewer
	 * 
	 * @return the edits the parts from selection
	 */
	public static List<EditPart> getEditPartsFromSelection(
			ISelection selection, IDiagramGraphicalViewer viewer) {
		if (selection instanceof StructuredSelection && !selection.isEmpty()) {
			StructuredSelection structuredSelection = (StructuredSelection) selection;
			// look for Views of the EObjects in the selection
			List<View> views = new ArrayList<View>();
			for (Object o : structuredSelection.toList()) {
				if (o instanceof EObject) {
					List referencerViews = getEObjectViews((EObject) o);
					for (Object ro : referencerViews) {
						if (ro instanceof View) {
							views.add((View) ro);
						}
					}
				}
			}
			if (!views.isEmpty()) {
				List<EditPart> editParts = new ArrayList<EditPart>();
				for (View view : views) {
					Object ep = viewer.getEditPartRegistry().get(view);
					if (ep instanceof EditPart) {
						editParts.add((EditPart) ep);
					}
				}
				if (!editParts.isEmpty()) {
					return editParts;
				}
			}
		}
		return Collections.EMPTY_LIST;
	}

	// Code extracted from PackageCanonicalEditPolicy
	/**
	 * Gets the {@link EditPart} from {@link View}.
	 * 
	 * @param view
	 *            the view
	 * @param anyEditPart
	 *            the any edit part
	 * 
	 * @return the edits the part from view
	 */
	public static EditPart getEditPartFromView(View view, EditPart anyEditPart) {
		if (view != null) {
			return (EditPart) anyEditPart.getViewer().getEditPartRegistry()
					.get(view);
		}
		return null;
	}

	/**
	 * Returns the diagramEditPart that includes the given view
	 */
	public static DiagramEditPart getDiagramEditPart() {
		if (PlatformUI.getWorkbench() == null
				|| PlatformUI.getWorkbench().getActiveWorkbenchWindow() == null
				|| PlatformUI.getWorkbench().getActiveWorkbenchWindow()
						.getActivePage() == null) {
			return null;
		}
		IEditorPart editorPart = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActiveEditor();

		if (!(editorPart instanceof IDiagramWorkbenchPart)) {
			return null;
		}

		IDiagramWorkbenchPart multiPage = (IDiagramWorkbenchPart) editorPart;

		return multiPage.getDiagramEditPart();
	}

	// *****************************************//

	// ********************************************//

	/**
	 * Find {@link Diagram} from {@link Plugin}.
	 * 
	 * @param plugin
	 *            the plugin
	 * 
	 * @return the diagram
	 */
	public static Diagram findDiagramFromPlugin(AbstractUIPlugin plugin) {
		IEditorPart editor = plugin.getWorkbench().getActiveWorkbenchWindow()
				.getActivePage().getActiveEditor();

		if (editor instanceof DiagramEditor) {
			return ((DiagramEditor) editor).getDiagram();
		}

		return null;
	}

	/**
	 * Find {@link Diagram} from {@link EditPart}.
	 * 
	 * @param editPart
	 *            the edit part
	 * 
	 * @return the diagram
	 */
	public static Diagram findDiagramFromEditPart(EditPart editPart) {
		Object object = editPart.getModel();

		if (object instanceof View) {
			return ((View) object).getDiagram();
		}

		return null;
	}

	// **//

	/**
	 * Refresh {@link ITextAwareEditPart} {@link EditPart}s.
	 * 
	 * @param editPart
	 *            the edit part
	 */
	public static void refreshITextAwareEditParts(EditPart editPart) {

		for (Object obj : editPart.getChildren()) {
			if (obj instanceof EditPart) {
				refreshITextAwareEditParts((EditPart) obj);
			}
		}

		if (editPart instanceof ITextAwareEditPart) {
			editPart.refresh();
		}
	}
}
