/*******************************************************************************
 * Copyright (c) 2010 - 2011 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – Initial implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.View;

import es.cv.gvcase.mdt.common.edit.policies.ViewAndFeatureResolver;
import es.cv.gvcase.mdt.common.edit.policies.ViewFeatureParentResolver;
import es.cv.gvcase.mdt.common.model.ExtendedFeatureElement;
import es.cv.gvcase.mdt.common.model.ExtendedFeatureElementFactory;
import es.cv.gvcase.mdt.common.util.CopyUtils;
import es.cv.gvcase.mdt.common.util.MultiDiagramUtil;

/**
 * An {@link AbstractCommonTransactionalCommmand} that copies a collection of
 * IStorableElements in the target editPart.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public class AddIStorableElementsCommand extends
		AbstractCommonTransactionalCommmand {

	/**
	 * Stored element helper
	 */
	public static final String StoredElementHelper = "es.cv.gvcase.mdt.common.storage.storable.helper";

	/**
	 * Related diagrams helper
	 */
	public static final String StoredElementRelatedDiagrams = "es.cv.gvcase.mdt.common.storage.storable.relatedDiagrams";

	protected Collection<EObject> eObjects = null;

	protected Collection<EObject> copiedEObjects = null;

	protected List<Diagram> relatedDiagrams = null;

	protected Point location = null;

	public Collection<EObject> getCopiedEObjects() {
		if (copiedEObjects == null) {
			copiedEObjects = new ArrayList<EObject>();
		}
		return copiedEObjects;
	}

	protected IGraphicalEditPart parentEditPart = null;

	public AddIStorableElementsCommand(TransactionalEditingDomain domain,
			EObject eObject, IGraphicalEditPart parentEditPart, Point location) {
		this(domain, Collections.singletonList(eObject), parentEditPart,
				location);
	}

	public AddIStorableElementsCommand(TransactionalEditingDomain domain,
			Collection<EObject> eObjects, IGraphicalEditPart parentEditPart,
			Point location) {
		super(domain, "Add IStorableElements", null);
		this.eObjects = eObjects;
		this.parentEditPart = parentEditPart;
		this.location = location;
	}

	@Override
	public boolean canExecute() {
		return eObjects != null
				&& eObjects.size() > 0
				&& parentEditPart != null
				&& parentEditPart.getAdapter(ViewAndFeatureResolver.class) != null;
	}

	protected IProgressMonitor monitor = null;

	@Override
	protected CommandResult doExecuteWithResult(IProgressMonitor monitor,
			IAdaptable info) throws ExecutionException {
		return statusToCommandResult(doRedo(monitor, info));
	}

	@Override
	protected IStatus doRedo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		this.monitor = monitor;
		monitor.beginTask("Pasting elements from Storage", eObjects.size());
		getCopiedEObjects().clear();
		// copy all new values and store them in new parent
		copyAndStore();
		// add the required references to the diagram
		addReferencesToDiagram();
		// status return
		monitor.done();
		return OKStatus;
	}

	@Override
	protected IStatus doUndo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		this.monitor = monitor;
		int totalWork = getCopiedEObjects().size();
		monitor.beginTask("Undo pastes elements from Storage", totalWork);
		for (EObject eObject : getCopiedEObjects()) {
			deleteEObject(eObject);
			monitor.worked(1);
		}
		monitor.done();
		return OKStatus;
	}

	protected void deleteEObject(EObject eObject) {
		EObject semanticElement = null;
		if (eObject instanceof View) {
			// in case it is a View, the semantic element must be deleted
			// explicitilly
			semanticElement = ((View) eObject).getElement();
		}
		EcoreUtil.delete(eObject);
		if (semanticElement != null) {
			// delete the semantic element in case there was one
			EcoreUtil.delete(semanticElement);
		}
	}

	protected void addReferencesToDiagram() {
		Diagram diagram = parentEditPart.getNotationView().getDiagram();
		for (EObject eObject : getCopiedEObjects()) {
			addReferencesToDiagram(diagram, eObject);
		}
	}

	protected void addReferencesToDiagram(Diagram diagram, EObject eObject) {
		MultiDiagramUtil.AddEAnnotationReferenceToDiagram(diagram, eObject);
		for (EObject child : eObject.eContents()) {
			if (child instanceof View == false
					&& child instanceof EAnnotation == false) {
				MultiDiagramUtil.AddEAnnotationReferenceToDiagram(diagram,
						child);
			}
			addReferencesToDiagram(diagram, child);
		}
	}

	protected EObject getParentEObject() {
		return parentEditPart.resolveSemanticElement();
	}

	protected EObject getParentEObject(EObject eObject,
			ViewFeatureParentResolver resolver) {
		return resolver.getParentForEClass(eObject.eClass());
	}

	protected void copyAndStore() {
		for (EObject eObject : eObjects) {
			EObject copiedEobject = copyEObject(eObject);
			if (copiedEobject != null) {
				getCopiedEObjects().add(copiedEobject);
				storeEObject(copiedEobject);
				cleanEObject(copiedEobject);
			}
			monitor.worked(1);
		}
	}

	@SuppressWarnings(value = "unchecked")
	protected void storeEObject(EObject eObject) {
		CopyUtils.storeEObjectInEditPart(eObject, parentEditPart, location);
	}

	/**
	 * Copies the EObject and then cleans it.
	 * 
	 * @param eObject
	 * @return
	 */
	protected EObject copyEObject(EObject eObject) {
		if (eObject != null) {
			// EObject copiedEObject = CopyUtils.copyEObject(eObject);
			EObject copiedEObject = CopyUtils.copyWithDiagrams(eObject);
			return copiedEObject;
		}
		return null;
	}

	/**
	 * Removes the EAnnotations marking the element as an IStorableElement
	 * 
	 * @param eObject
	 */
	protected void cleanEObject(EObject eObject) {
		if (eObject instanceof EModelElement) {
			// remove helper storing EAnnotation
			EModelElement element = (EModelElement) eObject;
			EAnnotation eAnnotation = element
					.getEAnnotation(StoredElementHelper);
			element.getEAnnotations().remove(eAnnotation);
			// remove helper diagrams EAnnotation
			eAnnotation = element.getEAnnotation(StoredElementRelatedDiagrams);
			element.getEAnnotations().remove(eAnnotation);
		}
		for (EObject child : eObject.eContents()) {
			cleanEObject(child);
		}
		if (eObject instanceof View) {
			cleanEObject(((View) eObject).getElement());
		}
		// clean references to external ResourceSets
		cleanReferencesToExternalResourceSet(eObject);
		return;
	}

	/**
	 * Sets all the references of EObjects that are referencing Resources from a
	 * ResourceSet different from the target one to target those same EObjects
	 * but in the target ResourceSet.
	 * 
	 * @param eObject
	 */
	protected void cleanReferencesToExternalResourceSet(EObject eObject) {
		// go though all the references of the EObject
		List<EReference> references = eObject.eClass().getEAllReferences();
		ResourceSet targetResourceSet = getTargetResourceSet();
		if (references == null || references.isEmpty()
				|| targetResourceSet == null) {
			return;
		}
		for (EReference reference : references) {
			// clean all the external references of the given EObject
			cleanExternalReference(eObject, reference, targetResourceSet);
		}
	}

	/**
	 * Looks in all the {@link EReference}s of the given {@link EObject} looking
	 * for referenced {@link EObject}s that are not in the targetResourceSet. <br />
	 * Those referenced EObjects are changed to other EObjects that are in the
	 * targetResourceSet.
	 * 
	 * @param eObject
	 * @param reference
	 * @param targetResourceSet
	 */
	protected void cleanExternalReference(EObject eObject,
			EReference reference, ResourceSet targetResourceSet) {
		if (eObject == null || reference == null || targetResourceSet == null) {
			return;
		}
		Object value = eObject.eGet(reference, true);
		List<EObject> referencedEObjects = new ArrayList<EObject>();
		List<EObject> newReferences = new ArrayList<EObject>();
		// put the referenced elements in a local List
		if (value instanceof EObject) {
			// the value of the reference can be monovalued
			referencedEObjects.add((EObject) value);
		}
		if (value instanceof Collection) {
			// the value of the reference can be multivalued
			for (Object o : ((Collection) value)) {
				if (o instanceof EObject) {
					referencedEObjects.add((EObject) o);
				}
			}
		}
		// go through all the elements in a reference and find those that do not
		// belong to the target ResourceSet
		boolean changed = false;
		String referencedFragment = null;
		URI referencedResourceURI = null;
		Resource targetResource = null;
		EObject newReferenced = null;
		for (EObject referenced : referencedEObjects) {
			if (referenced.eResource() != null
					&& referenced.eResource().getResourceSet() != null
					&& !referenced.eResource().getResourceSet().equals(
							targetResourceSet)) {
				// if the referenced EObject does not belong to the target
				// ResourceSet, load it in the target ResourceSet and store it
				// in the local collection to set the correct reference to the
				// EObject.
				referencedFragment = referenced.eResource().getURIFragment(
						referenced);
				referencedResourceURI = referenced.eResource().getURI();
				targetResource = targetResourceSet.getResource(
						referencedResourceURI, true);
				if (targetResource != null) {
					newReferenced = targetResource
							.getEObject(referencedFragment);
					if (newReferenced != null) {
						newReferences.add(newReferenced);
						changed = true;
					}
				}
			} else {
				// if the referenced EObject does belong to the target
				// ResourceSet, keep it.
				newReferences.add(referenced);
			}
		}
		// set the new values of the reference of the EObject, in case it has
		// changed.
		if (changed && newReferences != null && !newReferences.isEmpty()) {
			if (reference.isMany()) {
				EList<EObject> newValues = new BasicEList<EObject>();
				newValues.addAll(newReferences);
				eObject.eSet(reference, newValues);
			} else {
				EObject newValue = newReferences.get(0);
				eObject.eSet(reference, newValue);
			}
		}
	}

	protected List<Diagram> findRelatedDiagrams(EObject eObject) {
		if (eObject instanceof EModelElement) {
			ExtendedFeatureElement element = ExtendedFeatureElementFactory
					.getInstance().asExtendedFeatureElement(eObject);
			if (element != null) {
				List<EObject> values = element
						.getReferenceList(StoredElementRelatedDiagrams);
				for (EObject value : values) {
					if (value instanceof Diagram) {
						getRelatedDiagrams().add((Diagram) value);
					}
				}
			}
		}
		return getRelatedDiagrams();
	}

	protected List<Diagram> getRelatedDiagrams() {
		if (relatedDiagrams == null) {
			relatedDiagrams = new ArrayList<Diagram>();
		}
		return relatedDiagrams;
	}

	protected ResourceSet getTargetResourceSet() {
		if (parentEditPart != null) {
			EObject parentEObject = parentEditPart.resolveSemanticElement();
			if (parentEObject != null && parentEObject.eResource() != null) {
				return parentEObject.eResource().getResourceSet();
			}
		}
		return null;
	}

	protected ResourceSet getResourceSetForEObject(EObject eObject) {
		if (eObject != null && eObject.eResource() != null) {
			return eObject.eResource().getResourceSet();
		}
		return null;
	}

}
