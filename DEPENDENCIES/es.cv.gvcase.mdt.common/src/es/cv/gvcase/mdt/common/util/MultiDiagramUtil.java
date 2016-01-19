/*******************************************************************************
 * Copyright (c) 2008, 2010 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – Initial implementation.
 * 				 Gabriel Merin Cubero (Integranova) – Added version to diagrams
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.ui.URIEditorInput;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gmf.runtime.common.core.util.Log;
import org.eclipse.gmf.runtime.diagram.core.services.ViewService;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.parts.DiagramEditor;
import org.eclipse.gmf.runtime.diagram.ui.parts.IDiagramWorkbenchPart;
import org.eclipse.gmf.runtime.emf.core.resources.GMFResource;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorMatchingStrategy;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.EditorReference;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

import es.cv.gvcase.emf.common.part.EditingDomainRegistry;
import es.cv.gvcase.mdt.common.Activator;
import es.cv.gvcase.mdt.common.commands.AddIStorableElementsCommand;
import es.cv.gvcase.mdt.common.edit.policies.ShowInDiagramEditPartPolicyRegistry;
import es.cv.gvcase.mdt.common.ids.MOSKittEditorIDs;
import es.cv.gvcase.mdt.common.ids.MOSKittEditorIDs.modelToEditor;
import es.cv.gvcase.mdt.common.part.CachedResourcesDiagramEditor;
import es.cv.gvcase.mdt.common.part.CachedResourcesEditorInput;
import es.cv.gvcase.mdt.common.part.MOSKittDiagramsFileExtensionRegistry;
import es.cv.gvcase.mdt.common.part.MOSKittMultiPageEditor;
import es.cv.gvcase.mdt.common.provider.IDiagramInitializer;

/**
 * The Class MultiDiagramUtil.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * @author <a href="mailto:gmerin@prodevelop.es">Gabriel Merin Cubero</a>
 */
public class MultiDiagramUtil {

	/**
	 * {@link EAnnotation} Source for elements that belong to this
	 * {@link Diagram}.
	 */
	public static final String BelongToDiagramSource = "es.cv.gvcase.mdt.uml2.diagram.common.Belongs_To_This_Diagram";

	/**
	 * {@link EAnnotation} Source for elements that do not belong to this
	 * {@link Diagram}.
	 */
	public static final String DoesNotBelongToDiagramSource = "es.cv.gvcase.mdt.uml2.diagram.common.Does_Not_Belong_To_This_Diagram";

	/** {@link EAnnotation} Source for diagrams that grow from this {@link View}. */
	public static final String DiagramsRelatedToElement = "es.cv.gvcase.mdt.uml2.diagram.common.DiagramsRelatedToElement";

	/**
	 * {@link EAnnotation} Source for diagrams that open the correct Upper
	 * Diagram {@link View}.
	 */
	public static final String UpperDiagram = "es.cv.gvcase.mdt.diagram.common.UpperDiagram";

	/**
	 * {@link EAnnotation} Source for diagrams that can be opened from this
	 * {@link View}.
	 */
	public static final String AllDiagramsRelatedToElement = "es.cv.gvcase.mdt.uml2.diagram.common.AllDiagramsRelatedToElement";

	/**
	 * Associate {@link Diagram} to view.
	 * 
	 * @param diagram
	 *            the diagram
	 */
	public static void associateDiagramToView(Diagram diagram) {
		EAnnotation eAnnotation = diagram
				.getEAnnotation(DiagramsRelatedToElement);
		if (eAnnotation == null) {
			eAnnotation = EcoreFactory.eINSTANCE.createEAnnotation();
			eAnnotation.setSource(DiagramsRelatedToElement);
			diagram.getEAnnotations().add(eAnnotation);
		}
		eAnnotation.getReferences().add(diagram.getElement());
	}

	/**
	 * Gets the {@link Diagram}s associated to view.
	 * 
	 * @param diagram
	 *            the diagram
	 * 
	 * @return the diagrams associated to view
	 */
	public static List<Diagram> getDiagramsAssociatedToView(Diagram diagram) {
		EObject domainElement = diagram != null ? diagram.getElement() : null;
		Resource resource = diagram != null ? diagram.eResource() : null;
		String diagramKind = diagram != null ? diagram.getType() : null;
		if (domainElement == null || resource == null || diagramKind == null) {
			return Collections.EMPTY_LIST;
		}
		List<Diagram> diagrams = new ArrayList<Diagram>();
		for (EObject eObject : resource.getContents()) {
			if (eObject instanceof Diagram) {
				Diagram containedDiagram = (Diagram) eObject;
				EAnnotation eAnnotation = containedDiagram
						.getEAnnotation(DiagramsRelatedToElement);
				if (eAnnotation != null) {
					if (eAnnotation.getReferences().contains(domainElement)) {
						diagrams.add(containedDiagram);
					}
				}
			}
		}
		return diagrams;
	}

	/**
	 * Gets the {@link Diagram}s associated to element.
	 * 
	 * @param element
	 *            the element
	 * 
	 * @return the diagrams associated to element
	 */
	public static List<Diagram> getDiagramsAssociatedToElement(EObject element) {
		if (element instanceof View) {
			element = ((View) element).getElement();
		}
		Resource resource = getDiagramResource(element);
		if (resource == null || element == null) {
			return Collections.emptyList();
		}
		List<Diagram> diagrams = new ArrayList<Diagram>();
		for (EObject child : resource.getContents()) {
			if (child instanceof Diagram) {
				Diagram diagram = (Diagram) child;
				if (element.equals(diagram.getElement())) {
					diagrams.add(diagram);
				} else {
					EAnnotation eAnnotation = diagram
							.getEAnnotation(AllDiagramsRelatedToElement);
					if (eAnnotation != null
							&& eAnnotation.getReferences().contains(element)) {
						diagrams.add(diagram);
					}
				}
			}
		}
		return diagrams;
	}

	/**
	 * Gets a list of elements associated to the given eObject via the
	 * 'DiagramsRelatedToElement' EAnnotation.
	 * 
	 * @param eObject
	 * @return
	 */
	public static List<Diagram> getDiagramsAssociatedToElementByEAnnotation(
			EObject eObject) {
		if (eObject instanceof EModelElement) {
			EModelElement element = (EModelElement) eObject;
			EAnnotation eAnnotation = element
					.getEAnnotation(AddIStorableElementsCommand.StoredElementRelatedDiagrams);
			if (eAnnotation != null) {
				List<Diagram> diagrams = new ArrayList<Diagram>();
				for (EObject referenced : eAnnotation.getReferences()) {
					if (referenced instanceof Diagram) {
						diagrams.add((Diagram) referenced);
					}
				}
				return diagrams;
			}
		}
		return Collections.emptyList();
	}

	/**
	 * Gets the {@link Diagram}s associated to an {@link EditPart}
	 * 
	 * @param element
	 *            the element
	 * 
	 * @return the diagrams associated to element
	 */
	public static List<Diagram> getDiagramsAssociatedToEditPart(
			IGraphicalEditPart editPart) {
		if (editPart == null) {
			return Collections.emptyList();
		}
		Resource resource = editPart.getNotationView().eResource();
		EObject element = editPart.resolveSemanticElement();
		if (resource == null || element == null) {
			return Collections.emptyList();
		}
		List<Diagram> diagrams = new ArrayList<Diagram>();
		for (EObject child : resource.getContents()) {
			if (child instanceof Diagram) {
				Diagram diagram = (Diagram) child;
				if (element.equals(diagram.getElement())) {
					diagrams.add(diagram);
				}
			}
		}
		return diagrams;
	}

	public static List<IEditorInput> getDiagramsWhereElementIsShown(
			EObject element) {
		List<IEditorInput> editorInputsOfDiagramsThatShowElement = new ArrayList<IEditorInput>();
		// search in already loaded resources
		editorInputsOfDiagramsThatShowElement
				.addAll(getDiagramsWhereElementIsShownInResourceSet(element));
		// search in the megamodel
		editorInputsOfDiagramsThatShowElement
				.addAll(getDiagramsWhereElementIsShownInWorkspace(element));
		// clean up the calculated editor inputs as there may be some duplicates
		editorInputsOfDiagramsThatShowElement = removeDuplicatedURIEditorInputs(editorInputsOfDiagramsThatShowElement);
		//
		return editorInputsOfDiagramsThatShowElement;
	}

	public static List<IEditorInput> getDiagramsWhereElementIsShownInResourceSet(
			EObject element) {
		if (element == null || element.eResource() == null
				|| element.eResource().getResourceSet() == null) {
			return Collections.emptyList();
		}
		// search in all the Resources in the ResourceSet for a Diagram that
		// shows the given element
		List<Diagram> diagramsThatShowElement = new ArrayList<Diagram>();
		ResourceSet resourceSet = element.eResource().getResourceSet();
		Diagram diagram = null;
		for (Resource resource : resourceSet.getResources()) {
			for (EObject content : resource.getContents()) {
				if (content instanceof Diagram) {
					diagram = (Diagram) content;
					if (MultiDiagramUtil.findEObjectReferencedInEAnnotation(
							diagram, element)) {
						diagramsThatShowElement.add(diagram);
					}
				}
			}
		}
		return fromDiagramsToEditorInputs(diagramsThatShowElement);
	}

	/**
	 * Gets a List of {@link Diagram}s where the given EObject is shown as a
	 * View.
	 * 
	 * @return
	 */
	public static List<IEditorInput> getDiagramsWhereElementIsShownInWorkspace(
			EObject element) {
		if (element == null) {
			return Collections.emptyList();
		}

		final List<Diagram> list = new ArrayList<Diagram>();
		final ResourceSet resourceSet = new ResourceSetImpl();
		Resource lastResource = null;
		// fill gmf resources one while the resource of the given element
		// doesn't change
		if (lastResource == null || lastResource != element.eResource()) {
			resourceSet.getResources().clear();
			resourceSet.getResource(element.eResource().getURI(), true);

			try {
				IResourceVisitor visitor = new IResourceVisitor() {
					public boolean visit(IResource res) throws CoreException {
						if (!(res instanceof IFile)) {
							return true;
						}

						URI uri = URI.createURI(((IFile) res).getFullPath()
								.toString());
						if (MOSKittDiagramsFileExtensionRegistry.getInstance()
								.isMOSKittDiagramFileExtension(
										uri.fileExtension())) {
							try {
								resourceSet.getResource(uri, true);
							} catch (Exception e) {
								return false;
							}
						}
						return false;
					}
				};

				ResourcesPlugin.getWorkspace().getRoot().accept(visitor);
				lastResource = element.eResource();
			} catch (CoreException ex) {
				IStatus status = new Status(IStatus.WARNING,
						Activator.PLUGIN_ID, "Error visiting workspace.");
				Activator.getDefault().getLog().log(status);
				return Collections.emptyList();
			}
		}

		// look for the element (by the fragment) through all the GMF Resources
		final String fragment = element.eResource().getURIFragment(element)
				.toString();
		List<Resource> resources = new ArrayList<Resource>();
		for (Resource r : resourceSet.getResources()) {
			if (r instanceof GMFResource) {
				resources.add(r);
			}
		}
		for (Resource resource : resources) {
			for (TreeIterator<EObject> ti = resource.getAllContents(); ti
					.hasNext();) {
				EObject next = ti.next();
				if (!(next instanceof View)) {
					continue;
				}

				View view = (View) next;
				EObject eObj = view.getElement();
				if (eObj == null || !eObj.eIsProxy()) {
					continue;
				}

				URI proxyURI = ((InternalEObject) eObj).eProxyURI();
				if (fragment.equals(proxyURI.fragment())) {
					list.add(view.getDiagram());
					continue;
				}
			}
		}
		// convert Diagrams to URIEditorInput
		List<IEditorInput> editorInputsOfDiagramsThatShowElement = fromDiagramsToEditorInputs(list);
		//
		return editorInputsOfDiagramsThatShowElement;
	}

	// private static ResourceSet resourceSet = new ResourceSetImpl();
	// private static Resource lastResource = null;

	protected static List<IEditorInput> fromDiagramsToEditorInputs(
			List<Diagram> diagrams) {
		List<IEditorInput> editorInputs = new ArrayList<IEditorInput>();
		String editorInputStringURI = null;
		String diagramFragment = null;
		URI editorInputURI = null;
		URIEditorInput uriEditorInput = null;
		for (Diagram diagram : diagrams) {
			editorInputStringURI = diagram.eResource().getURI().toString();
			diagramFragment = diagram.eResource().getURIFragment(diagram);
			editorInputStringURI += ("#" + editorInputStringURI);
			editorInputURI = URI.createURI(editorInputStringURI);
			editorInputURI = editorInputURI.appendFragment(diagramFragment);
			uriEditorInput = new URIEditorInput(editorInputURI, diagram
					.getName()
					+ " - " + diagram.getType());
			editorInputs.add(uriEditorInput);
		}
		return editorInputs;
	}

	/**
	 * Removes the duplicated editor inputs comparing by URI.
	 * 
	 * @param editorInputs
	 */
	protected static List<IEditorInput> removeDuplicatedURIEditorInputs(
			List<IEditorInput> editorInputs) {
		if (editorInputs == null) {
			return Collections.emptyList();
		}
		URIEditorInput uriEditorInput = null;
		String uriEditorInputString = null;
		List<String> analyzedEditorInputStrings = new ArrayList<String>();
		Iterator<IEditorInput> editorInputsIterator = editorInputs.iterator();
		while (editorInputsIterator.hasNext()) {
			Object editorInput = editorInputsIterator.next();
			// compare by URI String to find the duplicated ones and remove them
			if (editorInput instanceof URIEditorInput) {
				uriEditorInput = (URIEditorInput) editorInput;
				uriEditorInputString = uriEditorInput.toString();
				if (!analyzedEditorInputStrings.contains(uriEditorInputString)) {
					analyzedEditorInputStrings.add(uriEditorInputString);
				} else {
					editorInputsIterator.remove();
				}
			}
		}
		return editorInputs;
	}

	/**
	 * Gets the {@link Diagram} {@link Resource}.
	 * 
	 * @return the diagram resource
	 */
	public static GMFResource getDiagramResource() {
		Resource resource = getDiagramResource(null);
		if (resource instanceof GMFResource) {
			return (GMFResource) resource;
		} else {
			return null;
		}
	}

	/**
	 * Gets the {@link Diagram} {@link Resource}.
	 * 
	 * @return the diagram resource
	 */
	public static Resource getDiagramResource(EObject element) {
		if (element != null && element.eResource() != null
				&& element.eResource().getResourceSet() != null) {
			ResourceSet resourceSet = element.eResource().getResourceSet();

			for (Resource resource : resourceSet.getResources()) {
				if (resource instanceof GMFResource) {
					for (EObject eo : resource.getContents()) {
						if (eo instanceof Diagram) {
							Diagram d = (Diagram) eo;
							if (d.getElement() != null
									&& d.getElement().equals(element)) {
								return resource;
							} else {
								for (TreeIterator<EObject> ti = d
										.eAllContents(); ti.hasNext();) {
									if (ti.next().equals(element)) {
										return resource;
									}
								}
							}
						}
					}
				}
			}
			for (Resource resource : resourceSet.getResources()) {
				for (EObject eo : resource.getContents()) {
					if (eo instanceof Diagram) {
						Diagram d = (Diagram) eo;
						if (d.getElement() != null
								&& d.getElement().equals(element)) {
							return resource;
						} else {
							for (TreeIterator<EObject> ti = d.eAllContents(); ti
									.hasNext();) {
								if (ti.next().equals(element)) {
									return resource;
								}
							}
						}
					}
				}
			}
			for (Resource resource : resourceSet.getResources()) {
				for (EObject eo : resource.getContents()) {
					if (eo instanceof Diagram) {
						for (TreeIterator<EObject> ti = eo.eAllContents(); ti
								.hasNext();) {
							EObject eObject = ti.next();
							if (eObject instanceof EAnnotation) {
								if (((EAnnotation) eObject).getReferences()
										.contains(element)) {
									return resource;
								}
							}
						}
					}
				}
			}
			return null;
		} else {
			IEditorPart activeEditor;
			try {
				activeEditor = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage()
						.getActiveEditor();
			} catch (NullPointerException ex) {
				return null;
			}
			if (activeEditor != null && activeEditor instanceof DiagramEditor) {
				TransactionalEditingDomain domain = ((DiagramEditor) activeEditor)
						.getEditingDomain();
				if (domain == null) {
					return null;
				}
				for (Resource resource : domain.getResourceSet().getResources()) {
					if (resource instanceof GMFResource) {
						for (EObject eo : resource.getContents()) {
							if (eo instanceof Diagram) {
								Diagram d = (Diagram) eo;
								if (d.getElement() != null
										&& d.getElement().equals(element)) {
									return resource;
								} else {
									for (TreeIterator<EObject> ti = d
											.eAllContents(); ti.hasNext();) {
										if (ti.next().equals(element)) {
											return resource;
										}
									}
								}
							}
						}
					}
				}
			} else {
				if (activeEditor != null) {
					Diagram diagram = (Diagram) activeEditor
							.getAdapter(Diagram.class);
					if (diagram != null && diagram.eResource() != null) {
						GMFResource resource = (GMFResource) Platform
								.getAdapterManager().getAdapter(
										diagram.eResource(), GMFResource.class);
						return resource;
					}
				}
			}
			return null;
		}
	}

	public static List<Diagram> getAllDiagramsInResource(GMFResource resource) {
		if (resource == null) {
			return getAllDiagramsInResource();
		}

		List<Diagram> diagrams = new ArrayList<Diagram>();

		for (EObject child : resource.getContents()) {
			if (child instanceof Diagram) {
				diagrams.add((Diagram) child);
			}
		}

		return diagrams;
	}

	public static List<Diagram> getAllDiagramsInResource() {
		List<Diagram> diagrams = new ArrayList<Diagram>();

		GMFResource resource = getDiagramResource();
		if (resource == null)
			return diagrams;

		for (EObject child : resource.getContents()) {
			if (child instanceof Diagram) {
				diagrams.add((Diagram) child);
			}
		}

		return diagrams;
	}

	// //****////
	/**
	 * Adds the {@link EAnnotation} reference to {@link Diagram}.
	 * 
	 * @param plugin
	 *            the plugin
	 * @param eObject
	 *            the e object
	 * 
	 * @return true, if successful
	 */
	public static boolean addEAnnotationReferenceToDiagram(
			AbstractUIPlugin plugin, EObject eObject) {
		return AddEAnnotationReferenceToDiagram(DiagramEditPartsUtil
				.findDiagramFromPlugin(plugin), eObject);
	}

	/**
	 * Adds the {@link EAnnotation} reference to {@link Diagram}.
	 * 
	 * @param editPart
	 *            the edit part
	 * @param eObject
	 *            the e object
	 * 
	 * @return true, if successful
	 */
	public static boolean addEAnnotationReferenceToDiagram(EditPart editPart,
			EObject eObject) {
		return AddEAnnotationReferenceToDiagram(DiagramEditPartsUtil
				.findDiagramFromEditPart(editPart), eObject);
	}

	/**
	 * Adds the {@link EAnnotation} reference to {@link Diagram}.
	 * 
	 * @param diagram
	 *            the diagram
	 * @param eObject
	 *            the e object
	 * 
	 * @return true, if successful
	 */
	public static boolean AddEAnnotationReferenceToDiagram(Diagram diagram,
			EObject eObject) {

		if (diagram != null) {
			EAnnotation eAnnotation = diagram
					.getEAnnotation(BelongToDiagramSource);
			if (eAnnotation == null) {
				eAnnotation = EcoreFactory.eINSTANCE.createEAnnotation();
				eAnnotation.setSource(BelongToDiagramSource);
				diagram.getEAnnotations().add(eAnnotation);
			}
			// if (eAnnotation.getReferences().contains(eObject) == false) {
			eAnnotation.getReferences().add(eObject);
			// }
			return true;
		}
		return false;
	}

	/**
	 * Adds the {@link EAnnotation} reference to {@link Diagram}.
	 * 
	 * @param diagram
	 *            the diagram
	 * @param eObject
	 *            the e object
	 * 
	 * @return true, if successful
	 */
	public static boolean AddEAnnotationReferenceToDiagram(Diagram diagram,
			EObject eObject, boolean addIfExists) {

		if (diagram != null) {
			EAnnotation eAnnotation = diagram
					.getEAnnotation(BelongToDiagramSource);
			if (eAnnotation == null) {
				eAnnotation = EcoreFactory.eINSTANCE.createEAnnotation();
				eAnnotation.setSource(BelongToDiagramSource);
				diagram.getEAnnotations().add(eAnnotation);
			}
			// if (eAnnotation.getReferences().contains(eObject) == false) {
			if (eAnnotation.getReferences().contains(eObject) && addIfExists) {
				eAnnotation.getReferences().add(eObject);
			} else {
				eAnnotation.getReferences().add(eObject);
			}
			// }
			return true;
		}
		return false;
	}

	/**
	 * Adds the {@link EAnnotation} reference to {@link Diagram}.
	 * 
	 * @param editPart
	 *            the edit part
	 * @param eObject
	 *            the e object
	 * 
	 * @return true, if successful
	 */
	public static boolean AddDoesNotBelongEAnnotationReferenceToDiagram(
			EditPart editPart, EObject eObject) {
		return AddDoesNotBelongEAnnotationReferenceToDiagram(
				DiagramEditPartsUtil.findDiagramFromEditPart(editPart), eObject);
	}

	/**
	 * Adds the {@link EAnnotation} reference to {@link Diagram}.
	 * 
	 * @param diagram
	 *            the diagram
	 * @param eObject
	 *            the e object
	 * 
	 * @return true, if successful
	 */
	public static boolean AddDoesNotBelongEAnnotationReferenceToDiagram(
			Diagram diagram, EObject eObject) {

		if (diagram != null) {
			EAnnotation eAnnotation = diagram
					.getEAnnotation(DoesNotBelongToDiagramSource);
			if (eAnnotation == null) {
				eAnnotation = EcoreFactory.eINSTANCE.createEAnnotation();
				eAnnotation.setSource(DoesNotBelongToDiagramSource);
				diagram.getEAnnotations().add(eAnnotation);
			}
			// if (eAnnotation.getReferences().contains(eObject) == false) {
			eAnnotation.getReferences().add(eObject);
			// }
			return true;
		}
		return false;
	}

	/**
	 * Removes the {@link EAnnotation} reference from {@link Diagram}.
	 * 
	 * @param plugin
	 *            the plugin
	 * @param eObject
	 *            the e object
	 * 
	 * @return true, if successful
	 */
	public static boolean RemoveEAnnotationReferenceFromDiagram(
			AbstractUIPlugin plugin, EObject eObject) {
		return RemoveEAnnotationReferenceFromDiagram(DiagramEditPartsUtil
				.findDiagramFromPlugin(plugin), eObject);
	}

	/**
	 * Removes the {@link EAnnotation} reference from {@link Diagram}.
	 * 
	 * @param editPart
	 *            the edit part
	 * @param eObject
	 *            the e object
	 * 
	 * @return true, if successful
	 */
	public static boolean RemoveEAnnotationReferenceFromDiagram(
			EditPart editPart, EObject eObject) {
		return RemoveEAnnotationReferenceFromDiagram(DiagramEditPartsUtil
				.findDiagramFromEditPart(editPart), eObject);
	}

	/**
	 * Removes the {@link EAnnotation} reference from {@link Diagram}.
	 * 
	 * @param diagram
	 *            the diagram
	 * @param eObject
	 *            the e object
	 * 
	 * @return true, if successful
	 */
	public static boolean RemoveEAnnotationReferenceFromDiagram(
			Diagram diagram, EObject eObject) {

		if (diagram != null) {
			EAnnotation eAnnotation = diagram
					.getEAnnotation(BelongToDiagramSource);
			if (eAnnotation == null) {
				return false;
			}
			if (eAnnotation.getReferences().contains(eObject) == true) {
				eAnnotation.getReferences().remove(eObject);
				return true;
			}
		}
		return false;
	}

	/**
	 * Removes the {@link EAnnotation} reference from {@link Diagram}.
	 * 
	 * @param editPart
	 *            the edit part
	 * @param eObject
	 *            the e object
	 * 
	 * @return true, if successful
	 */
	public static boolean RemoveDoesNotBelongEAnnotationReferenceFromDiagram(
			EditPart editPart, EObject eObject) {
		return RemoveDoesNotBelongEAnnotationReferenceFromDiagram(
				DiagramEditPartsUtil.findDiagramFromEditPart(editPart), eObject);
	}

	/**
	 * Removes the {@link EAnnotation} reference from {@link Diagram}.
	 * 
	 * @param diagram
	 *            the diagram
	 * @param eObject
	 *            the e object
	 * 
	 * @return true, if successful
	 */
	public static boolean RemoveDoesNotBelongEAnnotationReferenceFromDiagram(
			Diagram diagram, EObject eObject) {

		if (diagram != null) {
			EAnnotation eAnnotation = diagram
					.getEAnnotation(DoesNotBelongToDiagramSource);
			if (eAnnotation == null) {
				return false;
			}
			if (eAnnotation.getReferences().contains(eObject) == true) {
				eAnnotation.getReferences().remove(eObject);
				return true;
			}
		}
		return false;
	}

	/**
	 * Find {@link EObject} referenced in {@link EAnnotation}.
	 * 
	 * @param editPart
	 *            the edit part
	 * @param eObject
	 *            the e object
	 * 
	 * @return true, if successful
	 */
	public static boolean findEObjectReferencedInEAnnotation(EditPart editPart,
			EObject eObject) {
		return findEObjectReferencedInEAnnotation(DiagramEditPartsUtil
				.findDiagramFromEditPart(editPart), eObject);
	}

	/**
	 * Find {@link EObject} referenced in {@link EAnnotation}.
	 * 
	 * @param plugin
	 *            the plugin
	 * @param eObject
	 *            the e object
	 * 
	 * @return true, if successful
	 */
	public static boolean findEObjectReferencedInEAnnotation(
			AbstractUIPlugin plugin, EObject eObject) {
		return findEObjectReferencedInEAnnotation(DiagramEditPartsUtil
				.findDiagramFromPlugin(plugin), eObject);
	}

	/**
	 * Find {@link EObject} referenced in {@link EAnnotation}.
	 * 
	 * @param diagram
	 *            the diagram
	 * @param eObject
	 *            the e object
	 * 
	 * @return true, if successful
	 */
	public static boolean findEObjectReferencedInEAnnotation(Diagram diagram,
			EObject eObject) {

		if (diagram != null) {
			EAnnotation eAnnotation = diagram
					.getEAnnotation(BelongToDiagramSource);
			if (eAnnotation == null
					|| !eAnnotation.getReferences().contains(eObject)) {
				return false;
			}
			return true;
		}
		return false;
	}

	/**
	 * Find {@link EObject} referenced in {@link EAnnotation}.
	 * 
	 * @param plugin
	 *            the plugin
	 * @param eObject
	 *            the e object
	 * 
	 * @return true, if successful
	 */
	public static boolean findEObjectDoesNotBelongReferencedInEAnnotation(
			EditPart editPart, EObject eObject) {
		return findEObjectDoesNotBelongReferencedInEAnnotation(
				DiagramEditPartsUtil.findDiagramFromEditPart(editPart), eObject);
	}

	/**
	 * Find {@link EObject} referenced in {@link EAnnotation}.
	 * 
	 * @param diagram
	 *            the diagram
	 * @param eObject
	 *            the e object
	 * 
	 * @return true, if successful
	 */
	public static boolean findEObjectDoesNotBelongReferencedInEAnnotation(
			Diagram diagram, EObject eObject) {

		if (diagram != null) {
			EAnnotation eAnnotation = diagram
					.getEAnnotation(DoesNotBelongToDiagramSource);
			if (eAnnotation == null
					|| !eAnnotation.getReferences().contains(eObject)) {
				return false;
			}
			return true;
		}
		return false;
	}

	/**
	 * Gets the all references in {@link EAnnotation}.
	 * 
	 * @param diagram
	 *            the diagram
	 * 
	 * @return the all references in e annotation
	 */
	public static List<EObject> getAllReferencesInEAnnotation(Diagram diagram) {
		EAnnotation eAnnotation = diagram.getEAnnotation(BelongToDiagramSource);
		if (eAnnotation != null) {
			return eAnnotation.getReferences();
		}
		return Collections.emptyList();
	}

	/**
	 * Gets the all references in {@link EAnnotation}.
	 * 
	 * @param diagram
	 *            the diagram
	 * 
	 * @return the all references in e annotation
	 */
	public static List<EObject> getAllReferencesInDoesNotBelongEAnnotation(
			Diagram diagram) {
		EAnnotation eAnnotation = diagram
				.getEAnnotation(DoesNotBelongToDiagramSource);
		if (eAnnotation != null) {
			return eAnnotation.getReferences();
		}
		return Collections.emptyList();
	}

	/**
	 * Method to trim and filter the views that will be returned from a
	 * getSemanticChildren() from a CanonicalEditPolicy. Default behavior checks
	 * whether a whitelist or a blacklist policy is applied and filter
	 * accordingly.
	 * 
	 * @param semanticChildren
	 * @param policy
	 * @return
	 */
	public static List<EObject> trimElementsToShow(
			List<EObject> semanticChildren, EditPolicy policy) {
		if (semanticChildren == null || semanticChildren.size() <= 0
				|| policy == null || policy.getHost() == null) {
			return Collections.emptyList();
		}
		EditPart hostEditPart = policy.getHost();
		if (isEditPartWhiteList(policy.getHost())) {
			// white list behavior
			return trimWhiteList(semanticChildren, hostEditPart);
		} else {
			// black list behavior
			return trimBlackList(semanticChildren, hostEditPart);
		}
	}

	/**
	 * Filter elements in the according to a blacklist policy.
	 * 
	 * @param semanticChildren
	 * @param editPart
	 * @return
	 */
	protected static List<EObject> trimBlackList(
			List<EObject> semanticChildren, EditPart editPart) {
		List<EObject> trimmedSemanticChildren = new ArrayList<EObject>();
		// black list
		for (EObject eObject : semanticChildren) {
			if (!findEObjectDoesNotBelongReferencedInEAnnotation(editPart,
					eObject)) {
				// add objects that are not in the list to the return list
				trimmedSemanticChildren.add(eObject);
			}
		}
		return trimmedSemanticChildren;
	}

	/**
	 * Filter elements in the list according to a whitelist policy.
	 * 
	 * @param semanticChildren
	 * @param editPart
	 * @return
	 */
	protected static List<EObject> trimWhiteList(
			List<EObject> semanticChildren, EditPart editPart) {
		List<EObject> trimmedSemanticChildren = new ArrayList<EObject>();
		// white list
		for (EObject eObject : semanticChildren) {
			if (findEObjectReferencedInEAnnotation(editPart, eObject)) {
				// add objects that are in the list to the return list.
				trimmedSemanticChildren.add(eObject);
			}
		}
		return trimmedSemanticChildren;
	}

	/**
	 * Check that an edit part has a whitelist behavior.
	 * 
	 * @param editPart
	 * @return
	 */
	public static boolean isEditPartWhiteList(EditPart editPart) {
		return ShowInDiagramEditPartPolicyRegistry.getInstance()
				.isEditPartWhiteListPolicy(editPart);
	}

	// //****////
	/**
	 * Add to a {@link Diagram} the version of the first editor that will modify
	 * the diagram. It uses MOSKittEditorsIDs.
	 * 
	 * @param diagram
	 *            the diagram
	 * 
	 * @author gmerin
	 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano
	 *         Muñoz</a>
	 */
	public static void addMultiDiagramVersion(Diagram diagram) {
		String diagramKind = diagram.getType();
		if (diagramKind == null)
			return;

		modelToEditor modelToEditorInstance = MOSKittEditorIDs.getInstance()
				.getModelToEditorForModel(diagramKind);
		if (modelToEditorInstance == null
				|| modelToEditorInstance.getContributorId() == null) {
			return;
		}
		String contributorID = modelToEditorInstance.getContributorId();
		Bundle bundle = Platform.getBundle(contributorID);
		if (bundle == null) {
			return;
		}
		String version = (String) bundle.getHeaders().get(
				Constants.BUNDLE_VERSION);

		MDTUtil.addDiagramVersion(diagram, version);
	}

	/**
	 * Intialize new {@link Diagram}.
	 * 
	 * @param kind
	 *            the kind
	 * @param domainElement
	 *            the domain element
	 * @param resource
	 *            the resource
	 * @param initializers
	 *            the initializers
	 * 
	 * @return the diagram
	 * 
	 * @throws ExecutionException
	 *             the execution exception
	 */
	public static Diagram intializeNewDiagram(String kind,
			EObject domainElement, Resource resource,
			Map<String, IDiagramInitializer> initializers)
			throws ExecutionException {
		return intializeNewDiagram(kind, domainElement, resource, initializers,
				true);
	}

	/**
	 * Intialize new {@link Diagram}.
	 * 
	 * @param kind
	 *            the kind
	 * @param domainElement
	 *            the domain element
	 * @param resource
	 *            the resource
	 * @param initializers
	 *            the initializers
	 * @param askName
	 *            the ask name
	 * 
	 * @return the diagram
	 * 
	 * @throws ExecutionException
	 *             the execution exception
	 */
	public static Diagram intializeNewDiagram(String kind,
			EObject domainElement, Resource resource,
			Map<String, IDiagramInitializer> initializers, boolean askName)
			throws ExecutionException {
		Diagram d = null;
		d = ViewService.createDiagram(domainElement, kind, MDTUtil
				.getPreferencesHint(kind));
		if (askName && !setDigramName(d)) {
			return null;
		}
		if (d == null) {
			throw new ExecutionException("Can't create diagram of '" + kind
					+ "' kind");
		}
		// add the diagram to its container resource
		resource.getContents().add(d);

		// insert the eAnnotation to set the diagram to be opened in the
		// OpenUpper action
		IEditorPart activeEditor = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		Diagram activeDiagram = null;
		if (activeEditor instanceof DiagramEditor) {
			DiagramEditor diagramEditor = (DiagramEditor) activeEditor;
			activeDiagram = diagramEditor.getDiagram();
		} else if (activeEditor != null) {
			activeDiagram = (Diagram) activeEditor.getAdapter(Diagram.class);
		}

		associateDiagramToView(d);
		createInitialElements(d, initializers);
		// This will add the version of the editor that will edit the diagram
		addMultiDiagramVersion(d);
		// resource.getContents().add(d);
		return d;
	}

	/**
	 * Sets the {@link Diagram}'s name.
	 * 
	 * @param diagram
	 *            the diagram
	 * 
	 * @return true, if successful
	 */
	public static boolean setDigramName(Diagram diagram) {
		String message = "";
		message += "New " + diagram.getType() + " diagram name";
		InputDialog dialog = new InputDialog(Display.getCurrent()
				.getActiveShell(), "Diagram name", message, diagram.getType(),
				null);
		int result = dialog.open();
		if (result == Window.OK) {
			String name = dialog.getValue();
			diagram.setName(name);
			return true;
		}
		return false;
	}

	/**
	 * Initialize new {@link Diagram}.
	 * 
	 * @param kind
	 *            the kind
	 * @param domainElement
	 *            the domain element
	 * @param resource
	 *            the resource
	 * @param initializers
	 *            the initializers
	 * @param askName
	 *            the ask name
	 * @param name
	 *            the name
	 * 
	 * @return the diagram
	 * 
	 * @throws ExecutionException
	 *             the execution exception
	 */
	public static Diagram intializeNewDiagram(String kind,
			EObject domainElement, Resource resource,
			Map<String, IDiagramInitializer> initializers, boolean askName,
			String name) throws ExecutionException {
		return intializeNewDiagram(kind, domainElement, resource, initializers,
				askName, name, null);
	}

	/**
	 * Initialize new {@link Diagram}.
	 * 
	 * @param kind
	 *            the kind
	 * @param domainElement
	 *            the domain element
	 * @param resource
	 *            the resource
	 * @param initializers
	 *            the initializers
	 * @param askName
	 *            the ask name
	 * @param name
	 *            the name
	 * 
	 * @param upperDiagram
	 *            the upper diagram
	 * 
	 * @return the diagram
	 * 
	 * @throws ExecutionException
	 *             the execution exception
	 */
	public static Diagram intializeNewDiagram(String kind,
			EObject domainElement, Resource resource,
			Map<String, IDiagramInitializer> initializers, boolean askName,
			String name, Diagram upperDiagram) throws ExecutionException {

		Diagram d = null;
		d = ViewService.createDiagram(domainElement, kind, MDTUtil
				.getPreferencesHint(kind));
		if (askName && !setDigramName(d, name)) {
			return null;
		}
		if (d == null) {
			throw new ExecutionException("Can't create diagram of '" + kind
					+ "' kind");
		}

		// insert the eAnnotation to set the diagram to be opened in the
		// OpenUpper action
		if (upperDiagram == null) {
			IEditorPart activeEditor = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage()
					.getActiveEditor();
			if (activeEditor != null) {
				upperDiagram = (Diagram) activeEditor.getAdapter(Diagram.class);
			}
		}

		associateDiagramToView(d);
		createInitialElements(d, initializers);
		// This will add the version of the editor that will edit the diagram
		addMultiDiagramVersion(d);
		resource.getContents().add(d);
		return d;
	}

	/**
	 * Sets the {@link Diagram} name.
	 * 
	 * @param diagram
	 *            the diagram
	 * @param name
	 *            the name
	 * 
	 * @return true, if successful
	 */
	public static boolean setDigramName(Diagram diagram, String name) {
		String message = "";
		message += "New " + diagram.getType() + " diagram name";
		InputDialog dialog = new InputDialog(Display.getCurrent()
				.getActiveShell(), "Diagram name", message, name, null);
		int result = dialog.open();
		if (result == Window.OK) {
			String n = dialog.getValue();
			diagram.setName(n);
			return true;
		}
		return false;
	}

	/**
	 * Sets the editor for {@link Diagram}.
	 * 
	 * @param uri
	 *            the uri
	 * @param editorID
	 *            the editor id
	 * 
	 * @throws CoreException
	 *             the core exception
	 */
	public static void setEditorForDiagram(URI uri, String editorID)
			throws CoreException {
		String part1 = uri.path().replaceFirst("resource", "");
		IPath path = new Path(part1);
		ResourcesPlugin.getWorkspace().getRoot().refreshLocal(
				IResource.DEPTH_INFINITE, new NullProgressMonitor());
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		if (file != null) {
			file.setPersistentProperty(IDE.EDITOR_KEY, editorID);
			return;
		}
		throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
				"Error setting file property"));
	}

	/**
	 * Creates the initial elements.
	 * 
	 * @param diagram
	 *            the diagram
	 * @param initializers
	 *            the initializers
	 */
	private static void createInitialElements(Diagram diagram,
			Map<String, IDiagramInitializer> initializers) {
		if (initializers != null) {
			String kind = diagram.getType();
			if (initializers.containsKey(kind)) {
				IDiagramInitializer initializer = initializers.get(kind);
				initializer.init(diagram);
			}
		}
	}

	/**
	 * Gets the save options.
	 * 
	 * @return the save options
	 */
	public static Map getSaveOptions() {
		Map saveOptions = new HashMap();
		saveOptions.put(XMLResource.OPTION_ENCODING, "UTF-8"); //$NON-NLS-1$
		saveOptions.put(Resource.OPTION_SAVE_ONLY_IF_CHANGED,
				Resource.OPTION_SAVE_ONLY_IF_CHANGED_MEMORY_BUFFER);
		return saveOptions;
	}

	/**
	 * Perform delete {@link Diagram}.
	 * 
	 * @param diagram
	 *            the diagram
	 * @param confirm
	 *            the confirm
	 * 
	 * @return the diagram
	 */
	public static Diagram performDeleteDiagram(Diagram diagram, boolean confirm) {
		if (diagram == null) {
			return null;
		}
		// Get upper diagram to open in case the one deleted is active.
		Diagram diagramToOpen = getUpperDiagram(diagram);
		if (diagramToOpen == null || diagramToOpen.equals(diagram)) {
			// This is the uppest diagram we'll look for a diagram at the same
			// level
			diagramToOpen = getOtherDiagram(diagram);
			if (diagramToOpen == null) {
				// no suitable diagram to open
				return null;
			}
		}

		// The diagram is Ok to be deleted. Ask user confirmation.
		if (confirm) {
			MessageDialog confirmDialog = new MessageDialog(Display
					.getCurrent().getActiveShell(), "Delete diagram?", null,
					"Are oyu sure you want to delete the selected diagram?",
					MessageDialog.WARNING, new String[] { "Yes", "No" }, 1);
			int result = confirmDialog.open();
			if (result == Window.CANCEL) {
				return null;
			}
		}

		if (!isDiagramActive(diagram)) {
			// If the diagram to delete is not active it can be deleted without
			// problems.
			deleteDiagramAndSave(diagram);
		} else {
			// If the diagram to delete is active, a complex process must be
			// folowed to delete it.
			// Close all diagram editors that have the diagram to be deleted
			// active.
			// EditingDomainRegistry.getInstance().setChangingCachedEditors(true);
			closeEditorsThatShowDiagram(diagram);
			// Delete diagram
			deleteDiagramAndSave(diagram);
			// Open its upper diagram
			try {
				openDiagram(diagramToOpen);
			} catch (ExecutionException ex) {
				IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
						"Can't open diagram");
				Activator.getDefault().getLog().log(status);
				return null;
			} finally {
				// EditingDomainRegistry.getInstance().setChangingCachedEditors(
				// false);
			}
		}
		return diagramToOpen;
	}

	/**
	 * Checks if is {@link Diagram} active.
	 * 
	 * @param diagram
	 *            the diagram
	 * 
	 * @return true, if is diagram active
	 */
	public static boolean isDiagramActive(Diagram diagram) {
		IEditorPart activeEditor = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (activeEditor instanceof DiagramEditor) {
			DiagramEditor diagramEditor = (DiagramEditor) activeEditor;
			Diagram activeDiagram = diagramEditor.getDiagram();
			if (diagram.equals(activeDiagram)) {
				return true;
			}
		} else if (activeEditor != null) {
			Diagram activeDiagram = (Diagram) activeEditor
					.getAdapter(Diagram.class);
			if (activeDiagram != null && activeDiagram.equals(diagram)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Adds a {@link Diagram} to a {@link Resource} and saves the resource.
	 * 
	 * @param diagram
	 * @param resource
	 * @return
	 */
	public static boolean addDiagramAndSave(Diagram diagram, Resource resource) {
		return addDiagramAndSave(diagram, resource, true);
	}

	/**
	 * Adds a {@link Diagram} to a {@link Resource} and saves the resource.
	 * 
	 * @param diagram
	 * @param resource
	 * @param save
	 * @return
	 */
	public static boolean addDiagramAndSave(Diagram diagram, Resource resource,
			boolean save) {
		if (diagram == null || resource == null) {
			return false;
		}
		resource.getContents().add(diagram);
		if (save) {
			try {
				resource.save(getSaveOptions());
			} catch (IOException ex) {
				IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
						"Error saving resource");
				Activator.getDefault().getLog().log(status);
				return false;
			}
		}
		return true;
	}

	/**
	 * Delete {@link Diagram} and save.
	 * 
	 * @param diagram
	 *            the diagram
	 * 
	 * @return true, if successful
	 */
	public static boolean deleteDiagramAndSave(Diagram diagram) {
		return deleteDiagramAndSave(diagram, true);
	}

	/**
	 * Delete {@link Diagram} and save.
	 * 
	 * @param diagram
	 *            the diagram
	 * 
	 * @return true, if successful
	 */
	public static boolean deleteDiagramAndSave(Diagram diagram, boolean save) {
		if (diagram != null && diagram.eResource() != null) {
			Resource diagramResource = diagram.eResource();
			MDTUtil.removeLastOpenedDiagramProperty(diagram);
			MDTUtil
					.removeEditorForDiagramProperty(diagram.eResource()
							.getURI());
			if (deleteDiagramFromResource(diagram, diagram.eResource()) == false) {
				return false;
			}
			if (save) {
				try {
					diagramResource.save(getSaveOptions());
				} catch (IOException ex) {
					IStatus status = new Status(IStatus.ERROR,
							Activator.PLUGIN_ID, "Error saving resource");
					Activator.getDefault().getLog().log(status);
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Deletes a {@link Diagram} in the given {@link Resource}.
	 * 
	 * @param diagram
	 *            the diagram
	 * @param resource
	 *            the resource
	 * 
	 * @return true, if delete diagram from resource
	 */
	public static boolean deleteDiagramFromResource(Diagram diagram,
			Resource resource) {
		if (diagram == null || resource == null) {
			return false;
		}
		resource.getContents().remove(diagram);
		return resource.getContents().contains(diagram) == false;
	}

	/**
	 * Closes all {@link Diagram} editors that are showing the given Diagram.
	 * 
	 * @param diagram
	 *            diagram to be closed
	 */
	public static void closeEditorsThatShowDiagram(Diagram diagram) {
		if (diagram == null) {
			return;
		}
		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		IEditorReference[] editors = page.getEditorReferences();
		for (IEditorReference editorReference : editors) {
			IEditorPart editorPart = editorReference.getEditor(false);
			if (editorPart instanceof MOSKittMultiPageEditor) {
				MOSKittMultiPageEditor multiPageEditor = (MOSKittMultiPageEditor) editorPart;
				multiPageEditor.closePageForEObject(diagram);
			}
			if (editorPart instanceof IDiagramWorkbenchPart) {
				IDiagramWorkbenchPart diagramEditor = (IDiagramWorkbenchPart) editorPart;
				Diagram editorDiagram = diagramEditor.getDiagram();
				if (diagram.equals(editorDiagram)) {
					page.closeEditor(editorPart, true);
				}
			}
		}
	}

	/**
	 * Delete and save {@link EObject} in {@link Resource}.
	 * 
	 * @param uri
	 *            the uri
	 * @param fragment
	 *            the fragment
	 * 
	 * @return true, if successful
	 */
	public static boolean deleteAndSaveEObjectInResource(URI uri,
			String fragment) {
		URI resourceURI = uri;
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
				.put(Resource.Factory.Registry.DEFAULT_EXTENSION,
						new XMIResourceFactoryImpl());
		Resource resource = resourceSet.getResource(resourceURI, true);
		EObject toDelete = resource.getEObject(fragment);
		if (toDelete != null && resource.getContents().contains(toDelete)) {
			resource.getContents().remove(toDelete);
			try {
				resource.save(getSaveOptions());
			} catch (IOException e) {
				Log.error(null, 0, "Error saving resource "
						+ resource.toString(), e);
				return false;
			}
		}
		return true;
	}

	// //****////

	/**
	 * Tries to open the given IEditorInput in one of the already open editors.
	 */
	public static IEditorPart openEditorInputInOpenDiagram(
			IEditorInput editorInput) {
		IEditorReference[] editorReferences = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage()
				.getEditorReferences();
		IEditorReference editorToOpen = null;
		// look for an editor that can open the given input
		for (IEditorReference editorReference : editorReferences) {
			if (editorReference instanceof EditorReference) {
				IEditorDescriptor editorDescriptor = ((EditorReference) editorReference)
						.getDescriptor();
				IEditorMatchingStrategy matchingStrategy = editorDescriptor
						.getEditorMatchingStrategy();
				if (matchingStrategy.matches(editorReference, editorInput)) {
					editorToOpen = editorReference;
					break;
				}
			}
		}
		if (editorToOpen != null) {
			// an editor was found, check whether it is a MOSKitt editor and
			// open it
			IEditorPart editorPart = editorToOpen.getEditor(true);

			if (editorPart instanceof MOSKittMultiPageEditor) {
				((MOSKittMultiPageEditor) editorPart).openInput(editorInput);
				return editorPart;
			}
		}
		return null;
	}

	/**
	 * Open {@link Diagram} and close the one we come from.
	 * 
	 * @param diagram
	 *            the diagram
	 * 
	 * @return the i editor part
	 * 
	 * @throws ExecutionException
	 *             the execution exception
	 */
	public static IEditorPart openDiagram(Diagram diagram)
			throws ExecutionException {
		return openDiagram(diagram, false);
	}

	/**
	 * Open {@link Diagram} and optionally close the one we come from.
	 * 
	 * @param diagram
	 *            the diagram
	 * 
	 * @return the i editor part
	 * 
	 * @throws ExecutionException
	 *             the execution exception
	 */
	public static IEditorPart openDiagram(Diagram diagram, boolean openInNew)
			throws ExecutionException {
		//
		URI uri = diagram.eResource().getURI();
		uri = uri.appendFragment(diagram.eResource().getURIFragment(diagram));
		String diagramName = MDTUtil.getDiagramName(diagram);
		IEditorInput editorInput = new CachedResourcesEditorInput(uri,
				diagramName, false, openInNew);
		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		IEditorPart activeEditor = page.getActiveEditor();
		// EditingDomainRegistry.getInstance().setChangingCachedEditors(true);
		try {
			if (activeEditor != null
					&& activeEditor instanceof MOSKittMultiPageEditor) {
				MOSKittMultiPageEditor multiPageEditor = (MOSKittMultiPageEditor) Platform
						.getAdapterManager().getAdapter(activeEditor,
								MOSKittMultiPageEditor.class);
				IEditorInput oldInput = multiPageEditor.getEditorInput();
				if (MultiDiagramUtil.checkSameResourceForEditorInputs(oldInput,
						editorInput)) {
					multiPageEditor.openPageForEObject(diagram);
					return multiPageEditor;
				}
			}
			String editorID = MOSKittEditorIDs.getAllExtensionModelToEditor()
					.get(diagram.getType());
			IEditorPart openedEditor = page.openEditor(editorInput, editorID);
			page.activate(openedEditor);
			return openedEditor;
		} catch (PartInitException ex) {
			throw new ExecutionException("Can't open diagram", ex);
		} finally {
			// EditingDomainRegistry.getInstance().setChangingCachedEditors(false);
		}
	}

	/**
	 * Check {@link Diagram} for editor.
	 * 
	 * @param diagram
	 *            the diagram
	 * @param editor
	 *            the editor
	 * 
	 * @return true, if successful
	 */
	protected static boolean checkDiagramForEditor(Diagram diagram,
			CachedResourcesDiagramEditor editor) {
		String kind = diagram.getType();
		String diagramEditorID = MOSKittEditorIDs
				.getAllExtensionModelToEditor().get(kind);
		String editorID = editor.getEditorID();
		return (diagramEditorID != null && diagramEditorID.equals(editorID));
	}

	public static boolean checkSameResourceForEditorInputs(
			IEditorInput oldInput, IEditorInput newInput) {
		boolean openInNewEditor = false;

		CachedResourcesEditorInput newCachedEditorInput = null;
		if (newInput instanceof CachedResourcesEditorInput) {
			newCachedEditorInput = (CachedResourcesEditorInput) newInput;
			openInNewEditor = newCachedEditorInput.isOpenInNewEditor();
		}

		if (openInNewEditor) {
			// we want to open in a new editor, alas if the IEditorInput is
			// exactly the same (the same resource AND diagram) we can reuse
			// this editor
			if (CachedResourcesEditorInput.checkSameEditorInput(oldInput,
					newCachedEditorInput)) {
				return true;
			}
			return false;
		}

		// get real URIs for both the old input ant the new input
		URI oldUri = URIUtil.getUri(oldInput).trimFragment();
		URI newUri = URIUtil.getUri(newInput).trimFragment();

		if (oldUri != null && newUri != null) {
			return oldUri.equals(newUri);
		}

		return false;
	}

	/**
	 * Close other {@link Diagram}s.
	 * 
	 * @param diagramToOpen
	 *            the diagram to open
	 * @param page
	 *            the page
	 * @param unload
	 *            the unload
	 */
	protected static void closeOtherDiagrams(Diagram diagramToOpen,
			IWorkbenchPage page, boolean unload) {
		EObject rootEObject = diagramToOpen.getElement();
		if (rootEObject == null) {
			return;
		}
		Resource activeResource = rootEObject.eResource();
		String activeUri = activeResource.getURI().trimFragment().toString();
		for (IEditorReference editorReference : page.getEditorReferences()) {
			IEditorPart editor = editorReference.getEditor(true);
			Resource resource = MDTUtil.getRootElementResource(editor);
			if (resource == null) {
				continue;
			}
			String uri = resource.getURI().trimFragment().toString();
			if (activeUri.equals(uri)) {
				editor.doSave(new NullProgressMonitor());
				if (editor instanceof CachedResourcesDiagramEditor) {
					((CachedResourcesDiagramEditor) editor)
							.setUnloadOnDispose(unload);
				}
				page.closeEditor(editor, true);
			}
		}
	}

	public static List<Diagram> getUpperDiagrams(Diagram diagram) {
		if (diagram == null) {
			return Collections.emptyList();
		}

		EObject element = diagram.getElement();
		List<Diagram> diagrams = new ArrayList<Diagram>();
		while (diagrams.isEmpty() && element.eContainer() != null) {
			element = element.eContainer();
			diagrams.addAll(getDiagramsAssociatedToElement(element));
		}

		return diagrams;

	}

	// // Get upper diagram

	/**
	 * Gets the upper {@link Diagram}.
	 * 
	 * @param diagram
	 *            the diagram
	 * 
	 * @return the upper diagram
	 */
	public static Diagram getUpperDiagram(Diagram diagram) {
		if (diagram == null) {
			return null;
		}

		List<Diagram> diagrams = getUpperDiagrams(diagram);
		if (diagrams.isEmpty()) {
			return null;
		}
		return diagrams.get(0);
	}

	/**
	 * Gets the other {@link Diagram}.
	 * 
	 * @param diagram
	 *            the diagram
	 * 
	 * @return the other diagram
	 */
	public static Diagram getOtherDiagram(Diagram diagram) {
		if (diagram == null) {
			return null;
		}

		EObject domainElement = diagram.getElement();
		Resource gmfResource = diagram.eResource();
		for (EObject eObject : gmfResource.getContents()) {
			if (eObject instanceof Diagram) {
				Diagram newDiagram = (Diagram) eObject;
				if (diagram.equals(newDiagram) == false
						&& domainElement.equals(newDiagram.getElement())) {
					return newDiagram;
				}
			}
		}
		// no diagram that includes this element was found
		// search for diagrams that include the parent element
		domainElement = domainElement.eContainer();

		return null;
	}

	/**
	 * Checks whether a {@link Diagram} contains a {@link View} of the given
	 * element.
	 * 
	 * @param diagram
	 *            the diagram
	 * @param element
	 *            the element
	 * 
	 * @return true, if successful
	 */
	private static boolean diagramHasElement(Diagram diagram, EObject element) {

		for (TreeIterator<EObject> iterator = diagram.eAllContents(); iterator
				.hasNext();) {
			EObject eObject = iterator.next();
			if (eObject instanceof View) {
				if (((View) eObject).getElement() != null
						&& ((View) eObject).getElement().equals(element)) {
					return true;
				}
			}
		}

		return false;
	}

	// //
	/**
	 * Storing and retrieveing info about open diagrams for
	 * {@link MOSKittMultiPageEditor}.
	 */
	// //
	// qualified name for the IFile property that will store the info about the
	// open diagrams.
	private static final QualifiedName OpenDiagramsFileProperty = new QualifiedName(
			"es.cv.gvcase.mdt.common.part.MOSKittMultiPageEditor",
			"openDiagrams");
	private static final String OpenDiagramsSeparator = ";";

	/**
	 * Stores the list of given diagrams in their resources' IFiles' properties.
	 * 
	 * @param openDiagrams
	 */
	public static void storeOpenDiagrams(List<Diagram> openDiagrams) {
		List<Resource> affectedResources = new ArrayList<Resource>();
		List<EObject> affectedEObjects = new ArrayList<EObject>();

		for (EObject eObject : openDiagrams) {
			if (eObject != null && eObject.eResource() != null) {
				if (!affectedResources.contains(eObject.eResource())) {
					affectedResources.add(eObject.eResource());
				}
				affectedEObjects.add(eObject);
			}
		}

		String openToStore = "";
		for (Resource resource : affectedResources) {
			for (EObject eObject : affectedEObjects) {
				if (eObject.eResource() == resource) {
					openToStore += (resource.getURIFragment(eObject) + OpenDiagramsSeparator);
				}
			}
			String path = es.cv.gvcase.emf.common.util.PathsUtil
					.fromAbsoluteFileSystemToAbsoluteWorkspace(resource
							.getURI().toString(), false);
			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(
					new Path(path));
			try {
				file.setPersistentProperty(OpenDiagramsFileProperty,
						openToStore);
			} catch (CoreException ex) {
				// normally, the file doesn't exist

				// Activator.getDefault().logError(
				// "Error saving list of open diagrams", ex);
			}
			openToStore = "";
		}
	}

	/**
	 * Gets the list of open {@link Diagram}s for the given {@link IEditorInput}
	 * .
	 * 
	 * @param editorInput
	 * @return
	 */
	public static List<EObject> getOpenDiagrams(IEditorInput editorInput) {
		List<EObject> openDiagrams = new ArrayList<EObject>();
		if (editorInput instanceof URIEditorInput) {
			URIEditorInput uriEditorInput = (URIEditorInput) editorInput;
			if (uriEditorInput.getURI().fragment() != null) {
				// this URI dictates a diagram to open in the URI, get that
				// diagram and return it
				TransactionalEditingDomain domain = EditingDomainRegistry
						.getInstance().get("none", editorInput);
				ResourceSet resourceSet = domain.getResourceSet();
				Resource diagramResource = MDTUtil.getResourceByURI(
						uriEditorInput.getURI(), resourceSet, true);
				EObject fragmentDiagram = diagramResource
						.getEObject(uriEditorInput.getURI().fragment());
				if (fragmentDiagram instanceof Diagram) {
					openDiagrams.add((Diagram) fragmentDiagram);
					return openDiagrams;
				}
			}
		}
		String path = es.cv.gvcase.emf.common.util.PathsUtil
				.getRelativeWorkspaceFromEditorInput(editorInput);
		if (path != null) {
			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(
					new Path(path));
			try {
				String diagramsToOpenProperty = file
						.getPersistentProperty(OpenDiagramsFileProperty);
				if (diagramsToOpenProperty == null
						|| diagramsToOpenProperty.length() <= 0) {
					return Collections.emptyList();
				}
				List<String> diagramsToOpen = Arrays
						.asList(diagramsToOpenProperty
								.split(OpenDiagramsSeparator));
				if (diagramsToOpen == null || diagramsToOpen.size() <= 0) {
					return openDiagrams;
				}
				List<EObject> rootEObjects = MDTUtil
						.getRootElementsFromFile(editorInput);
				Diagram diagram = null;
				for (EObject eObject : rootEObjects) {
					diagram = (Diagram) Platform.getAdapterManager()
							.getAdapter(eObject, Diagram.class);
					if (diagram != null && diagram.eResource() != null) {
						String fragment = diagram.eResource().getURIFragment(
								diagram);
						if (diagramsToOpen.contains(fragment)) {
							openDiagrams.add(diagram);
						}
					}
				}
			} catch (CoreException ex) {
				return openDiagrams;
			}
		}
		return openDiagrams;
	}

	/**
	 * Gets the diagram resources for the model resource.
	 * 
	 * @param modelResource
	 *            the model resource
	 * 
	 * @return the diagram resources list
	 */
	public static List<Resource> getDiagramResources(Resource modelResource) {
		List<Resource> diagrams = new ArrayList<Resource>();
		List<IPath> paths = Collections.emptyList();

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		ResourceSet resourceSet = modelResource.getResourceSet();
		Resource diagramResource = null;

		if (resourceSet == null || modelResource.getContents() == null
				|| modelResource.getContents().isEmpty()) {
			return diagrams;
		}

		// root element of the model
		EObject eObject = modelResource.getContents().get(0);

		// find all diagrams path in the workspace
		paths = visitWorkspace(workspace, resourceSet);

		for (IPath path : paths) {
			Diagram diagram = getDiagram(path, resourceSet);
			if (diagram == null) {
				continue;
			}

			EObject reference = diagram.getElement();
			reference = resolve(modelResource, reference);

			while (reference != null) {
				if (reference.equals(eObject)) {
					break;
				}
				reference = reference.eContainer();
			}
			if (reference == null || !reference.equals(eObject)) {
				continue;
			}

			// diagramResource is a diagram for the modelResource
			diagramResource = diagram.eResource();

			if (!(diagramResource instanceof GMFResource)) {
				continue;
			}

			diagrams.add(diagramResource);
		}

		return diagrams;

	}

	/**
	 * Gets the <IPath>s of all diagrams in workspace.
	 * 
	 * @param workspace
	 *            the workspace
	 * @param resourceSet
	 *            the resourceSet
	 * 
	 * @return the list<IPath>s of diagrams
	 */
	private static List<IPath> visitWorkspace(IWorkspace workspace,
			final ResourceSet resourceSet) {
		final List<IPath> paths = new ArrayList<IPath>();
		IResourceVisitor visitor = new IResourceVisitor() {
			public boolean visit(IResource resource) throws CoreException {
				if (resource instanceof IFile) {
					if (isDiagramFile(((IFile) resource).getFullPath(),
							resourceSet)) {
						paths.add(((IFile) resource).getFullPath());
					}
					return false;
				}
				return true;
			}
		};
		try {
			workspace.getRoot().accept(visitor);
		} catch (CoreException ex) {
			IStatus status = new Status(IStatus.WARNING, Activator.PLUGIN_ID,
					"Error visiting workspace.");
			Activator.getDefault().getLog().log(status);
			return Collections.emptyList();
		}
		return paths;
	}

	/**
	 * Checks whether the given path is a diagram resource.
	 * 
	 * @param path
	 *            the path
	 * 
	 * @return true, if checks it is diagram file
	 */
	private static boolean isDiagramFile(IPath path, ResourceSet resourceSet) {
		URI uri = URI.createURI(path.toString());
		Resource resource = null;
		try {
			resource = resourceSet.getResource(uri, true);
		} catch (Exception ex) {
			return false;
		}
		if (resource instanceof GMFResource) {
			if (!resource.getContents().isEmpty()) {
				if (resource.getContents().get(0) instanceof Diagram) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Gets the diagram by the given path.
	 * 
	 * @param path
	 *            the path
	 * 
	 * @return diagram
	 */
	private static Diagram getDiagram(IPath path, ResourceSet resourceSet) {
		URI uri = URI.createURI(path.toString());
		Resource resource = null;
		try {
			resource = resourceSet.getResource(uri, true);
		} catch (Exception ex) {
			return null;
		}
		if (resource instanceof GMFResource) {
			if (!resource.getContents().isEmpty()) {
				if (resource.getContents().get(0) instanceof Diagram) {
					return (Diagram) resource.getContents().get(0);
				}
			}
		}
		return null;
	}

	/**
	 * Resolve the eObject if it is a eProxyURI.
	 * 
	 * @param resource
	 *            the resource
	 * @param eObj
	 *            the eObject to resolve
	 * 
	 * @return the resolved eObject
	 */
	public static EObject resolve(Resource resource, EObject eObj) {
		if (eObj.eIsProxy()) {
			URI proxyURI = ((InternalEObject) eObj).eProxyURI();
			// if fragment starts with / the model of eObj has not UUIDs and it
			// can not be resolved
			if (!proxyURI.fragment().startsWith("/")) {
				return resource.getEObject(proxyURI.fragment());
			}
		}
		return eObj;
	}

}
