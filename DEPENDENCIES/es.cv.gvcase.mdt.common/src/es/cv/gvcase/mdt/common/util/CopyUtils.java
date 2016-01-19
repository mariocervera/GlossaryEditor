/*******************************************************************************
 * Copyright (c) 2010 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Héctor Iturria (Prodevelop) – Initial implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.notation.Bounds;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.Edge;
import org.eclipse.gmf.runtime.notation.LayoutConstraint;
import org.eclipse.gmf.runtime.notation.Node;
import org.eclipse.gmf.runtime.notation.View;

import es.cv.gvcase.mdt.common.commands.AddIStorableElementsCommand;
import es.cv.gvcase.mdt.common.edit.policies.ViewAndFeatureResolver;
import es.cv.gvcase.mdt.common.edit.policies.ViewFeatureParentResolver;
import es.cv.gvcase.mdt.common.model.ExtendedFeatureElement;
import es.cv.gvcase.mdt.common.model.ExtendedFeatureElementFactory;

/**
 * Holder for several utility methods for copy and copy-paste.
 * 
 * @author hiturria
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public class CopyUtils {

	// //
	// Ecore - GMf related
	// //

	/**
	 * Copies a {@link View} and its internal {@link EObject}, if any.
	 * 
	 * @param view
	 * @return
	 */
	public static EObject copyView(View view) {
		/**
		 * The copying of a View includes an operation to copy its model
		 * EObject, in case there's one.
		 */
		EObject eObject = null;
		Map<EObject, EObject> mapOriginal2Copied = null;
		if (view != null && view.getElement() != null) {
			eObject = EcoreUtil.copy(view.getElement());
			mapOriginal2Copied = buildOriginalToCopiedMap(view.getElement(),
					eObject);
		}
		View copiedView = null;
		if (view != null) {
			copiedView = (View) EcoreUtil.copy(view);
		}
		if (copiedView != null && eObject != null) {
			setCopiedViewElements(view, copiedView, mapOriginal2Copied);
		}
		return copiedView;
	}

	/**
	 * 
	 * @param diagramToCopy
	 * @param newEObject
	 * @return
	 */
	public static Diagram copyDiagram(Diagram diagramToCopy,
			EObject newEObject, Map<EObject, EObject> mapOriginal2Copied) {
		if (diagramToCopy == null || newEObject == null) {
			return null;
		}
		EObject element = diagramToCopy.getElement();
		if (element != null) {
			if (mapOriginal2Copied == null || mapOriginal2Copied.isEmpty()) {
				// build a map from old EObject to new EObject if not provided
				mapOriginal2Copied = buildOriginalToCopiedMap(element,
						newEObject);
			}
			// copy the diagram as an EObject
			Diagram newDiagram = (Diagram) EcoreUtil.copy(diagramToCopy);
			// set the new elements for the copied diagram views and nodes
			setCopiedViewElements(diagramToCopy, newDiagram, mapOriginal2Copied);
			// update the list of references in the diagram
			setCopiedElementsReferencesInDiagram(newDiagram, element,
					newEObject, mapOriginal2Copied);
			// remove the eAnnotation Related To Element
			removeDiagramRelatedToElementAnnotation(newDiagram);

			return newDiagram;
		}
		return null;
	}

	protected static void removeDiagramRelatedToElementAnnotation(
			Diagram diagram) {
		if (diagram
				.getEAnnotation(DiagramEditPartsUtil.DiagramsRelatedToElement) != null) {
			EAnnotation eAnnotation = diagram
					.getEAnnotation(DiagramEditPartsUtil.DiagramsRelatedToElement);
			diagram.getEAnnotations().remove(eAnnotation);
		}
	}

	//
	protected static void copyRelatedDiagrams(Map<View, View> mapViewToNewView) {
		if (mapViewToNewView == null || mapViewToNewView.isEmpty()) {
			return;
		}
		List<Diagram> copiedDiagrams = new ArrayList<Diagram>();
		EObject newEObject = null;
		EObject element = null;
		for (Entry<View, View> entry : mapViewToNewView.entrySet()) {
			element = entry.getKey();
			newEObject = entry.getValue();
			if (newEObject instanceof EModelElement) {
				List<Diagram> newRelatedDiagrams = new ArrayList<Diagram>();
				copyRelatedDiagrams(element, newEObject, newRelatedDiagrams,
						copiedDiagrams);
				EModelElement eModelElement = (EModelElement) newEObject;
				EAnnotation eAnnotation = eModelElement
						.getEAnnotation(AddIStorableElementsCommand.StoredElementRelatedDiagrams);
				if (eAnnotation == null) {
					eAnnotation = EcoreFactory.eINSTANCE.createEAnnotation();
					eAnnotation
							.setSource(AddIStorableElementsCommand.StoredElementRelatedDiagrams);
					eModelElement.getEAnnotations().add(eAnnotation);
				}
				eAnnotation.getReferences().clear();
				eAnnotation.getReferences().addAll(newRelatedDiagrams);
			}
		}
	}

	//
	protected static void copyRelatedDiagrams(EObject element,
			EObject newEObject) {
		if (newEObject instanceof EModelElement) {
			List<Diagram> newRelatedDiagrams = new ArrayList<Diagram>();
			List<Diagram> copiedDiagrams = new ArrayList<Diagram>();
			copyRelatedDiagrams(element, newEObject, newRelatedDiagrams,
					copiedDiagrams);
			EModelElement eModelElement = (EModelElement) newEObject;
			EAnnotation eAnnotation = eModelElement
					.getEAnnotation(AddIStorableElementsCommand.StoredElementRelatedDiagrams);
			if (eAnnotation == null) {
				eAnnotation = EcoreFactory.eINSTANCE.createEAnnotation();
				eAnnotation
						.setSource(AddIStorableElementsCommand.StoredElementRelatedDiagrams);
				eModelElement.getEAnnotations().add(eAnnotation);
			}
			eAnnotation.getReferences().clear();
			eAnnotation.getReferences().addAll(newRelatedDiagrams);
		}
	}

	/**
	 * Copies all the related {@link Diagram}s from the given element
	 * referencing them with the elements from newEobject structure and returns
	 * them via the newRelatedDiagrams collection.
	 * 
	 * @param element
	 *            Old elements to copy the diagrams from (must be a View)
	 * @param newEObject
	 *            new element to copy the diagrams to (must be a View)
	 * @param newRelatedDiagrams
	 *            list to return the new created diagrams
	 * @param copiedDiagrams
	 *            helper list to prevent copying a diagram twice
	 */
	protected static void copyRelatedDiagrams(EObject element,
			EObject newEObject, List<Diagram> newRelatedDiagrams,
			List<Diagram> copiedDiagrams) {
		if (element != null) {
			// get the diagram element from the old model
			EObject diagramElement = element;
			if (diagramElement instanceof View) {
				diagramElement = ((View) diagramElement).getElement();
			} else {
				diagramElement = element;
			}
			// get the diagram element from the new model
			EObject newEObjectElement = newEObject;
			if (newEObjectElement instanceof View) {
				newEObjectElement = ((View) newEObjectElement).getElement();
			} else {
				newEObjectElement = newEObject;
			}
			// fins all the related diagrams in the loaded model that reference
			// the old model diagram element
			List<Diagram> diagrams = getRelatedDiagrams(element, diagramElement);
			Map<EObject, EObject> mapOriginal2Copied = CopyUtils
					.buildOriginalToCopiedMap(diagramElement, newEObjectElement);
			List<Diagram> localNewDiagrams = new ArrayList<Diagram>();
			for (Diagram diagram : diagrams) {
				// copy each of the found elements that reference the old
				// diagram element
				if (!copiedDiagrams.contains(diagram)) {
					localNewDiagrams.add(CopyUtils.copyDiagram(diagram,
							newEObjectElement, mapOriginal2Copied));
					copiedDiagrams.add(diagram);
				}
			}
			newRelatedDiagrams.addAll(localNewDiagrams);
			// search recursively for more diagrams to copy in the elements
			// hierarchy
			if (localNewDiagrams != null && localNewDiagrams.size() > 0) {
				Diagram newDiagram = null;
				Iterator<Diagram> newDiagrams = localNewDiagrams.iterator();
				// iterate through all the newly created diagrams lloking for
				// new diagrams to copy
				for (Diagram diagram : diagrams) {
					newDiagram = newDiagrams.next();
					Iterator<EObject> allContents = diagram.eAllContents();
					EObject childEObject = null;
					EObject otherEObject = null;
					View childView = null;
					View otherView = null;
					for (; allContents.hasNext(); childEObject = allContents
							.next()) {
						// iterate through all the child views to look for new
						// diagrams to copy that are related to the elements
						// that have a view in the newly copied diagram
						if (childEObject instanceof View) {
							childView = (View) childEObject;
							if (childView.getElement() != null
									&& !childView.getElement().equals(
											diagramElement)
									&& getRelatedDiagrams(childView,
											childView.getElement()).size() > 0) {
								// if a new diagram to copy is found copy it
								otherEObject = mapOriginal2Copied.get(childView
										.getElement());
								otherView = findViewInDiagram(newDiagram,
										otherEObject, childView.getType());
								if (otherView != null) {
									copyRelatedDiagrams(childView, otherView,
											newRelatedDiagrams, copiedDiagrams);
								}
							}
						}
					}
				}
			}
		}
	}

	protected static View findViewInDiagram(Diagram diagram, EObject element,
			String type) {
		Iterator<EObject> children = diagram.eAllContents();
		EObject child = null;
		View childView = null;
		for (; children.hasNext(); child = children.next()) {
			if (child instanceof View) {
				childView = (View) child;
				if (childView.getElement() != null
						&& childView.getElement().equals(element)
						&& childView.getType() != null
						&& childView.getType().equals(type)) {
					return childView;
				}
			}
		}
		return null;
	}

	protected static List<Diagram> getRelatedDiagrams(EObject element,
			EObject diagramElement) {
		// search via the related diagrams eannotation
		List<Diagram> diagrams = null;
		diagrams = MultiDiagramUtil
				.getDiagramsAssociatedToElementByEAnnotation(element);
		// search via the related resources
		if (diagrams == null || diagrams.size() <= 0) {
			diagrams = MultiDiagramUtil
					.getDiagramsAssociatedToElement(diagramElement);
		}
		return diagrams;
	}

	/**
	 * Modifies the references list of a {@link Diagram} changing the old
	 * eobject by the new ones
	 * 
	 * @param diagram
	 * @param oldEObject
	 * @param newEObject
	 * @param mapOriginal2Copied
	 */
	protected static void setCopiedElementsReferencesInDiagram(Diagram diagram,
			EObject oldEObject, EObject newEObject,
			Map<EObject, EObject> mapOriginal2Copied) {
		if (diagram == null || oldEObject == null || newEObject == null) {
			return;
		}
		if (mapOriginal2Copied == null || mapOriginal2Copied.isEmpty()) {
			mapOriginal2Copied = buildOriginalToCopiedMap(oldEObject,
					newEObject);
		}
		List<EObject> newReferences = new ArrayList<EObject>();
		for (EObject eObject : MultiDiagramUtil
				.getAllReferencesInEAnnotation(diagram)) {
			if (mapOriginal2Copied.containsKey(eObject)) {
				newReferences.add(mapOriginal2Copied.get(eObject));
			}
		}
		EAnnotation referencesEAnnotation = diagram
				.getEAnnotation(MultiDiagramUtil.BelongToDiagramSource);
		if (referencesEAnnotation == null) {
			EAnnotation eAnnotation = EcoreFactory.eINSTANCE
					.createEAnnotation();
			eAnnotation.setSource(MultiDiagramUtil.BelongToDiagramSource);
			diagram.getEAnnotations().add(eAnnotation);
			referencesEAnnotation = eAnnotation;
		}
		referencesEAnnotation.getReferences().clear();
		referencesEAnnotation.getReferences().addAll(newReferences);
	}

	protected static void setCopiedViewElements(EObject originalEObject,
			EObject copiedEObject, Map<EObject, EObject> mapOriginal2Copied) {
		if (!(originalEObject instanceof View)
				|| !(copiedEObject instanceof View)
				|| mapOriginal2Copied == null) {
			return;
		}
		View originalView = (View) originalEObject;
		View copiedView = (View) copiedEObject;
		copyLayoutConstraints(originalView, copiedView);
		if (originalView.getElement() != null) {
			EObject toSetEObject = mapOriginal2Copied.get(originalView
					.getElement());
			if (toSetEObject != null) {
				copiedView.setElement(toSetEObject);
			}
		}
		EObject originalViewChild = null;
		EObject copiedViewChild = null;
		int size = originalView.eContents().size();
		for (int i = 0; i < size; i++) {
			originalViewChild = originalView.eContents().get(i);
			copiedViewChild = copiedView.eContents().get(i);
			setCopiedViewElements(originalViewChild, copiedViewChild,
					mapOriginal2Copied);
		}
	}

	protected static void copyLayoutConstraints(View original, View copied) {
		if (original instanceof Node && copied instanceof Node) {
			LayoutConstraint layout = ((Node) original).getLayoutConstraint();
			LayoutConstraint copiedLayout = (LayoutConstraint) EcoreUtil
					.copy(layout);
			((Node) copied).setLayoutConstraint(copiedLayout);
		}
	}

	public static Map<EObject, EObject> buildOriginalToCopiedMap(
			EObject original, EObject copied) {
		if (original == null || copied == null) {
			return Collections.emptyMap();
		}
		Map<EObject, EObject> map = new HashMap<EObject, EObject>();
		fillOriginalToCopiledMap(original, copied, map);
		return map;
	}

	protected static void fillOriginalToCopiledMap(EObject original,
			EObject copied, Map<EObject, EObject> map) {
		if (original == null || copied == null || map == null) {
			return;
		}
		map.put(original, copied);
		int size = original.eContents().size();
		EObject originalChild = null;
		EObject copiedChild = null;
		for (int i = 0; i < size; i++) {
			originalChild = original.eContents().get(i);
			copiedChild = copied.eContents().get(i);
			fillOriginalToCopiledMap(originalChild, copiedChild, map);
		}
	}

	/**
	 * Copies an {@link EObject}, including its children.
	 * 
	 * @param eObject
	 * @return
	 */
	public static EObject copyEObject(EObject eObject) {
		if (eObject != null) {
			if (eObject instanceof View) {
				// if the EObject to copy is a View, use a special operation for
				// the View
				return copyView((View) eObject);
			} else {
				return EcoreUtil.copy(eObject);
			}
		}
		return null;
	}

	/**
	 * Copies for an IStorableElement with the diagrams related to the element
	 * included
	 * 
	 * @param eObject
	 */
	public static EObject copyWithDiagrams(EObject eObject) {
		EObject newObject = copyEObject(eObject);
		copyRelatedDiagrams(eObject, newObject);
		return newObject;
	}

	/**
	 * Makes a copy of the given {@link Diagram} using {@link EcoreUtil}.
	 * 
	 * @param diagram
	 * @return
	 */
	public static Diagram copyDiagramOnly(Diagram diagram) {
		if (diagram != null) {
			EObject copiedDiagram = EcoreUtil.copy(diagram);
			if (copiedDiagram instanceof Diagram) {
				return (Diagram) copiedDiagram;
			}
		}
		return null;
	}

	public static void storeEObjectInEditPart(EObject eObject,
			EditPart editPart, Point location) {
		if (eObject == null || !(editPart instanceof IGraphicalEditPart)) {
			return;
		}
		IGraphicalEditPart parentEditPart = (IGraphicalEditPart) editPart;
		// get the resolver that indicated the feature that must hold an EObject
		// in the target
		ViewAndFeatureResolver resolver = (ViewAndFeatureResolver) parentEditPart
				.getAdapter(ViewAndFeatureResolver.class);
		View view = null;
		if (eObject instanceof View) {
			// in case we are dealing with a view, get it and set eObject to the
			// real semantic EObject.
			view = (View) eObject;
			eObject = view.getElement();
		}

		if (eObject != null && resolver != null) {
			// put the EObject where it belongs according to the
			// ViewAndFeatureResolver.
			EStructuralFeature feature = resolver
					.getEStructuralFeatureForEClass(eObject.eClass());
			if (feature != null) {
				EObject parent = eObject.eContainer();
				if (parent == null) {
					parent = parentEditPart.resolveSemanticElement();
				}
				if (resolver instanceof ViewFeatureParentResolver) {
					parent = ((ViewFeatureParentResolver) resolver)
							.getParentForEClass(eObject.eClass());
				}
				// check no duplicated names
				if (targetHasElementWithName(parent, eObject)) {
					setNewNameForEObject(parent, eObject);
				}
				// add the eobject where it belongs
				if (eObject.eContainer() == null) {
					putInContainer(eObject, feature, parent);
				}

				// add all the copied semantic EObjects to the Diagram
				// references
				addRecursiveEObjectToDiagram(eObject, parentEditPart);
				// add the views where they belong
				addViewToTarget(view, parentEditPart, location);
				// add diagrams to target diagram resource
				Resource diagramResource = parentEditPart.getNotationView()
						.eResource();
				// 
				addDiagramsToResource(view, diagramResource);
			}
		}
		return;
	}

	public static void putInContainer(EObject eObject,
			EStructuralFeature feature, EObject container) {
		Object value = container.eGet(feature);
		if (value != null) {
			if (value instanceof List) {
				((List) value).add(eObject);
			} else {
				container.eSet(feature, eObject);
			}
		} else {
			if (feature.isMany()) {
				Collection<EObject> values = new ArrayList<EObject>();
				values.add(eObject);
				container.eSet(feature, values);
			} else {
				container.eSet(feature, eObject);
			}
		}
	}

	protected static void addDiagramsToResource(View view,
			Resource diagramResource) {
		if (view != null && diagramResource != null) {
			for (Diagram diagram : findRelatedDiagrams(view)) {
				diagramResource.getContents().add(diagram);
			}
		}
	}

	protected static void addRecursiveEObjectToDiagram(EObject eObject,
			IGraphicalEditPart parentEditPart) {
		if (eObject != null && !(eObject instanceof EAnnotation)) {
			Diagram diagram = DiagramEditPartsUtil
					.findDiagramFromEditPart(parentEditPart);
			if (diagram != null) {
				MultiDiagramUtil.AddEAnnotationReferenceToDiagram(diagram,
						eObject);
				for (EObject child : eObject.eContents()) {
					addRecursiveEObjectToDiagram(child, parentEditPart);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected static void addViewToTarget(View view,
			IGraphicalEditPart parentEditPart, Point location) {
		if (view != null) {
			IFigure figure = parentEditPart.getContentPane();
			Point where = location.getCopy();
			figure.translateToRelative(where);
			figure.translateFromParent(where);
			where.translate(figure.getClientArea().getLocation());

			// set the location for the view
			if (view instanceof Node) {
				Node nodeView = (Node) view;
				if (nodeView.getLayoutConstraint() instanceof Bounds) {
					((Bounds) nodeView.getLayoutConstraint()).setX(where.x);
					((Bounds) nodeView.getLayoutConstraint()).setY(where.y);
				}
			}
			if (view instanceof Edge) {
				parentEditPart = DiagramEditPartsUtil
						.getDiagramEditPart(parentEditPart);
			}
			// find the target view where to copy it
			View targetView = parentEditPart.getNotationView();
			if (targetView instanceof Node) {
				// add the view as a persistent child
				Node nodeTargetView = (Node) targetView;
				nodeTargetView.getPersistedChildren().add(view);
			}
			if (targetView instanceof Diagram) {
				// add the view as a persistent child
				if (view instanceof Node) {
					((Diagram) targetView).getPersistedChildren().add(view);
				} else if (view instanceof Edge) {
					((Diagram) targetView).getPersistedEdges().add(view);
				}
			}
		}
	}

	public static final String StoredElementRelatedDiagrams = "es.cv.gvcase.mdt.common.storage.storable.relatedDiagrams";

	protected static List<Diagram> findRelatedDiagrams(EObject eObject) {
		List<Diagram> relatedDiagrams = new ArrayList<Diagram>();
		if (eObject instanceof EModelElement) {
			ExtendedFeatureElement element = ExtendedFeatureElementFactory
					.getInstance().asExtendedFeatureElement(eObject);
			if (element != null) {
				List<EObject> values = element
						.getReferenceList(StoredElementRelatedDiagrams);
				for (EObject value : values) {
					if (value instanceof Diagram) {
						relatedDiagrams.add((Diagram) value);
					}
				}
			}
		}
		return relatedDiagrams;
	}

	protected static boolean targetHasElementWithName(EObject target,
			EObject eObject) {
		if (target != null && eObject != null) {
			String objectName = MDTUtil.getObjectName(eObject);
			if (objectName != null) {
				String childName = null;
				for (EObject child : target.eContents()) {
					childName = MDTUtil.getObjectNameOrEmptyString(child);
					if (childName.equals(objectName)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	protected static String findNewNameForEObject(EObject target,
			EObject eObject) {
		String name = MDTUtil.getObjectName(eObject);
		if (name == null) {
			name = eObject.eClass().getName();
		}
		int counter = 1;
		String childName = null;
		String newName = name;
		boolean foundEqualName = false;
		do {
			foundEqualName = false;
			newName = name + counter;
			for (EObject child : target.eContents()) {
				childName = MDTUtil.getObjectNameOrEmptyString(child);
				if (childName.equals(newName)) {
					foundEqualName = true;
				}
			}
			counter++;
		} while (foundEqualName);
		return newName;
	}

	protected static String featureNameName = "name";

	protected static void setNewNameForEObject(EObject target, EObject eObject) {
		String newName = findNewNameForEObject(target, eObject);
		EStructuralFeature nameFeature = null;
		for (EStructuralFeature feature : eObject.eClass()
				.getEAllStructuralFeatures()) {
			if (featureNameName.equals(feature.getName())) {
				nameFeature = feature;
			}
		}
		if (nameFeature != null) {
			TransactionalEditingDomain domain = TransactionUtil
					.getEditingDomain(eObject);
			if (domain != null) {
				Command nameCommand = SetCommand.create(domain, eObject,
						nameFeature, newName);
				domain.getCommandStack().execute(nameCommand);
			} else {
				eObject.eSet(nameFeature, newName);
			}
		}
	}

	// //
	// Collection copying
	// //

	/**
	 * Copies a {@link Collection} of {@link EObject}s that can include
	 * {@link View}s. Resolves all references between them when copying. Sets
	 * the new Views elements to match the new copied elements. Copies all
	 * related diagrams to the newly copied Views, if any.
	 */
	public static Collection<EObject> copyEObjects(
			Collection<EObject> eObjects, boolean copyDiagrams) {
		return copyEObjects(eObjects, copyDiagrams, null);
	}

	/**
	 * Copies a {@link Collection} of {@link EObject}s that can include
	 * {@link View}s. Resolves all references between them when copying. Sets
	 * the new Views elements to match the new copied elements. Copies all
	 * related diagrams to the newly copied Views, if any.
	 */
	public static Collection<EObject> copyEObjects(
			Collection<EObject> eObjects, boolean copyDiagrams,
			Map<EObject, EObject> mapOld2New) {
		List<EObject> copiedEObjects = new ArrayList<EObject>();
		/**
		 * Copying a collection of eobjects involves these steps (keeping in
		 * mind that the given EObjects can be Views): -- 1) copy all semantic
		 * eobjects 2) build a mapping from old eobjects to new eobjects 3) copy
		 * all views, if any 4) set all views' eobjects 5) copy all related
		 * diagrams
		 */
		// copy all semantic EObjects:
		List<EObject> semanticEObjects = new ArrayList<EObject>();
		EObject semanticEObject = null;
		List<EObject> newSemanticEObjects = null;
		if (mapOld2New == null) {
			mapOld2New = new HashMap<EObject, EObject>();
		}
		mapOld2New.clear();
		Map<EObject, EObject> mapSemanticToNewSemantic = mapOld2New;
		for (EObject eObject : eObjects) {
			if (eObject instanceof View) {
				semanticEObject = ((View) eObject).getElement();
			} else {
				semanticEObject = eObject;
			}
			if (semanticEObject != null) {
				semanticEObjects.add(semanticEObject);
			}
		}
		// remove those Eobjects that are already contained in other EObjects as
		// those will be copied when the parent elements are copied.
		removeDuplicatedEObjects(semanticEObjects);
		// copy
		newSemanticEObjects = (List) EcoreUtil.copyAll(semanticEObjects);
		for (int i = 0; i < semanticEObjects.size(); i++) {
			mapSemanticToNewSemantic.put(semanticEObjects.get(i),
					newSemanticEObjects.get(i));
		}
		for (EObject eObject : mapSemanticToNewSemantic.keySet()) {
			// add those EObjects that came from the original list of EObjects
			// to copy
			if (eObjects.contains(eObject)) {
				copiedEObjects.add(mapSemanticToNewSemantic.get(eObject));
			}
		}
		// build a map from all old EObjects to all new EObjects
		Map<EObject, EObject> mapOldToOriginal = new HashMap<EObject, EObject>();
		for (Entry<EObject, EObject> entry : mapSemanticToNewSemantic
				.entrySet()) {
			mapOldToOriginal.putAll(buildOriginalToCopiedMap(entry.getKey(),
					entry.getValue()));
		}
		// copy all the views
		List<View> views = new ArrayList<View>();
		List<View> newViews = new ArrayList<View>();
		Map<View, View> mapViewToNewView = new HashMap<View, View>();
		View toCopy = null;
		View copiedView = null;
		for (EObject eObject : eObjects) {
			if (eObject instanceof View) {
				toCopy = (View) eObject;
				views.add(toCopy);
			}
		}
		newViews = (List) EcoreUtil.copyAll(views);
		for (int i = 0; i < views.size(); i++) {
			mapViewToNewView.put(views.get(i), newViews.get(i));
		}
		copiedEObjects.addAll(newViews);
		// set all the views references
		for (Entry<View, View> entry : mapViewToNewView.entrySet()) {
			setCopiedViewElements(entry.getKey(), entry.getValue(),
					mapOldToOriginal);
		}
		if (copyDiagrams) {
			// copy all related diagrams ; only views can have other related
			// diagrams that need to be copied
			copyRelatedDiagrams(mapViewToNewView);
		}
		//
		return copiedEObjects;
	}

	public static Collection<EObject> copyEObjects(Collection<EObject> eObjects) {
		return copyEObjects(eObjects, true);
	}

	public static void removeDuplicatedEObjects(Collection<EObject> elements) {
		if (elements == null || elements.size() <= 0) {
			return;
		}
		List<EObject> toRemove = new ArrayList<EObject>();
		// search for each element in the contents of the others to find out
		// whether it is contained in it.
		for (EObject innerEObject : elements) {
			for (EObject parentEObject : elements) {
				if (!innerEObject.equals(parentEObject)) {
					if (isContianedIn(parentEObject, innerEObject)) {
						toRemove.add(innerEObject);
					}
				}
			}
		}
		// purge
		elements.removeAll(toRemove);
	}

	protected static boolean isContianedIn(EObject parent,
			EObject childCandidate) {
		if (parent == null || childCandidate == null) {
			return false;
		}
		if (parent instanceof View) {
			parent = ((View) parent).getElement();
		}
		if (childCandidate instanceof View) {
			childCandidate = ((View) childCandidate).getElement();
		}
		if (parent == null || childCandidate == null) {
			return false;
		}
		EObject child = null;
		Iterator<EObject> allContents = parent.eAllContents();
		while (allContents.hasNext()) {
			child = allContents.next();
			if (child != null && child.equals(childCandidate)) {
				return true;
			}
		}
		return false;
	}

	// //
	// Other related
	// //

	/**
	 * Copies the source file to the dest file
	 * 
	 * @param source
	 * @param dest
	 */
	public static void copyFile(File source, File dest) {
		InputStream in = null;
		OutputStream out = null;

		try {
			if (!dest.exists()) {
				dest.createNewFile();
			}
			in = new FileInputStream(source);
			out = new FileOutputStream(dest);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
		} catch (IOException e) {
			System.out.print("Something went wrong while copying the file: "
					+ e.getMessage());
		} finally {
			try {
				in.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
