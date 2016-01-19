/*******************************************************************************
 * Copyright (c) 2008, 2009 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – Initial API 
 * implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.ide.navigator.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.eclipse.gmf.runtime.diagram.ui.parts.DiagramEditor;
import org.eclipse.gmf.runtime.diagram.ui.parts.IDiagramGraphicalViewer;
import org.eclipse.gmf.runtime.emf.core.resources.GMFResource;
import org.eclipse.gmf.runtime.emf.core.resources.GMFResourceFactory;
import org.eclipse.gmf.runtime.emf.core.util.EMFCoreUtil;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.NotationPackage;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import es.cv.gvcase.ide.navigator.actions.resource.OpenDiagramAction;

/**
 * Class with different utility methods.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 */
public class NavigatorUtil {

	/**
	 * Gets the active {@link IEditorPart}'s model resource. If the active
	 * editor is a GMF editor, that model will be a {@link GMFResource}.
	 * 
	 * @param editor
	 *            the editor
	 * @param factories
	 *            the factories
	 * 
	 * @return the resource from editor
	 */
	public static Resource getReourceFromEditor(IEditorPart editor,
			Map<String, Object> factories) {

		if (editor != null) {
			Diagram diagram = (Diagram) editor.getAdapter(Diagram.class);
			if (diagram != null) {
				return diagram.eResource();
			}
		}

		if (editor instanceof IEditingDomainProvider) {
			EditingDomain domain = ((IEditingDomainProvider) editor)
					.getEditingDomain();
			if (domain != null) {
				ResourceSet resourceSet = domain.getResourceSet();
				if (resourceSet != null) {
					return (resourceSet.getResources().size() > 0) ? resourceSet
							.getResources().get(0)
							: null;
				}
			}
		}

		EditingDomain domain = (EditingDomain) editor
				.getAdapter(EditingDomain.class);
		if (domain != null) {
			ResourceSet resourceSet = domain.getResourceSet();
			if (resourceSet != null && resourceSet.getResources() != null
					&& resourceSet.getResources().size() > 0) {
				return resourceSet.getResources().get(0);
			}
		}

		if (editor != null && factories != null) {
			IEditorInput input = editor.getEditorInput();
			Object inputObject = input.getAdapter(IFile.class);
			if (inputObject instanceof IFile) {
				ResourceSet resourceSet = new ResourceSetImpl();
				for (Entry<String, Object> entry : factories.entrySet()) {
					resourceSet.getResourceFactoryRegistry()
							.getExtensionToFactoryMap().put(entry.getKey(),
									entry.getValue());
				}
				URI uri = URI.createPlatformResourceURI(((IFile) inputObject)
						.getFullPath().toString(), true);
				try {
					return resourceSet.getResource(uri, true);
				} catch (Exception ex) {
					return null;
				}
			}
		}

		return null;
	}

	/**
	 * Gets the active {@link IEditorPart}'s underlying model resource.
	 * 
	 * @param editor
	 *            the editor
	 * @param factories
	 *            the factories
	 * 
	 * @return the model resource from editor
	 */
	public static Resource getModelResourceFromEditor(IEditorPart editor,
			Map<String, Object> factories) {
		Resource resource = getReourceFromEditor(editor, factories);
		if (resource instanceof GMFResource) {
			GMFResource gmfResource = (GMFResource) resource;
			if (gmfResource.getContents().size() > 0) {
				EObject eObject = gmfResource.getContents().get(0);
				if (eObject instanceof Diagram) {
					EObject element = ((Diagram) eObject).getElement();
					return element != null ? element.eResource() : null;
				}
			}
		}
		return resource;
	}

	/**
	 * Gets the active {@link IEditorPart}.
	 * 
	 * @return the active editor
	 */
	public static IEditorPart getActiveEditor() {
		IWorkbench wb = PlatformUI.getWorkbench();
		if (wb != null) {
			IWorkbenchWindow ww = wb.getActiveWorkbenchWindow();
			if (ww != null) {
				IWorkbenchPage wp = ww.getActivePage();
				if (wp != null) {
					return wp.getActiveEditor();
				}
			}
		}
		return null;
	}

	/**
	 * Gets the active {@link IEditorPart}'s {@link EditingDomain}, if any.
	 * 
	 * @return the editing domain from active editor
	 */
	public static EditingDomain getEditingDomainFromActiveEditor() {
		IEditorPart activeEditor = getActiveEditor();
		if (activeEditor instanceof IEditingDomainProvider) {
			return ((IEditingDomainProvider) activeEditor).getEditingDomain();
		} else if (activeEditor instanceof DiagramEditor) {
			return ((DiagramEditor) activeEditor).getEditingDomain();
		} else if (activeEditor instanceof IAdaptable) {
			EditingDomain domain = (EditingDomain) ((IAdaptable) activeEditor)
					.getAdapter(EditingDomain.class);
			if (domain != null) {
				return domain;
			}
		}
		return null;
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
		return Collections.emptyList();
	}

	/**
	 * Gets the given {@link EObject} views.
	 * 
	 * @param element
	 *            the element
	 * 
	 * @return the e object views
	 */
	public static List<View> getEObjectViews(EObject element) {
		List<View> views = new ArrayList<View>();
		if (element != null) {
			EReference[] features = { NotationPackage.eINSTANCE
					.getView_Element() };
			views.addAll(EMFCoreUtil.getReferencers(element, features));
		}
		return views;
	}

	/**
	 * Unwraps selection. Gets {@link EObject}s from {@link EditPart}s, from
	 * {@link View}s or from {@link EObject}s
	 * 
	 * @param selection
	 *            the selection
	 * 
	 * @return the i selection
	 */
	public static ISelection unwrapSelection(ISelection selection) {
		if (selection instanceof StructuredSelection && !selection.isEmpty()) {
			List<EObject> selectionList = new ArrayList<EObject>();
			StructuredSelection structuredSelection = (StructuredSelection) selection;
			for (Iterator iterator = structuredSelection.iterator(); iterator
					.hasNext();) {
				Object next = iterator.next();
				if (next instanceof EditPart) {
					Object model = ((EditPart) next).getModel();
					EObject element = null;
					if (model instanceof View) {
						element = ((View) model).getElement();
					} else if (model instanceof EObject) {
						element = (EObject) model;
					}
					if (element != null) {
						selectionList.add(element);
					}
				} else if (next instanceof View) {
					EObject element = ((View) next).getElement();
					if (element != null) {
						selectionList.add(element);
					}
				} else if (next instanceof EObject) {
					selectionList.add((EObject) next);
				}
			}
			return new StructuredSelection(selectionList);
		} else
			return selection;
	}

	/**
	 * Find a {@link IViewPart} by it's id string.
	 * 
	 * @param viewID
	 *            the view id
	 * 
	 * @return the i view part
	 */
	public static IViewPart findViewPart(String viewID) {
		try {
			IWorkbenchWindow ww = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow();
			if (ww == null || ww.getActivePage() == null) {
				return null;
			}
			IViewReference reference = ww.getActivePage().findViewReference(
					viewID);
			if (reference == null) {
				return null;
			}
			IWorkbenchPart part = reference.getPart(false);
			if (part instanceof IViewPart) {
				return (IViewPart) part;
			} else {
				return null;
			}
		} catch (NullPointerException ex) {
			return null;
		}
	}

	/**
	 * Registry store for all MOSKitt model identifiers. Is used by the
	 * {@link OpenDiagramAction} to open each diagram with its correct editor.
	 * 
	 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano
	 *         Muñoz</a>
	 */
	@Deprecated
	public static class MOSKittModelIDs {

		/** The map model to editor. */
		protected static Map<String, String> mapModelToEditor = null;

		/**
		 * Gets the model to editor map.
		 * 
		 * @return the model to editor map
		 */
		public static Map<String, String> getModelToEditorMap() {
			if (mapModelToEditor == null) {
				mapModelToEditor = new HashMap<String, String>();
			}
			return mapModelToEditor;
		}
	}

	/**
	 * Gets underlying model {@link Resource} from the Resource specified by
	 * uri. Will unwrap {@link GMFResource}s.
	 * 
	 * @param uri
	 *            the uri
	 * 
	 * @return the root element resource
	 */
	protected static Resource getRootElementResource(URI uri) {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
				.put(Resource.Factory.Registry.DEFAULT_EXTENSION,
						new GMFResourceFactory());
		Resource inputResource = resourceSet.getResource(uri, true);
		// this should be a GMF resource
		if (inputResource instanceof GMFResource) {
			EObject eObject = inputResource.getContents().get(0);
			if (eObject instanceof Diagram) {
				return ((Diagram) eObject).getElement().eResource();
			}
		}
		return null;
	}

	/**
	 * Gets the editor {@link EObject} root or canvas element.
	 * 
	 * @param editorPart
	 *            the editor part
	 * 
	 * @return the editor rootelement
	 */
	protected static EObject getEditorRootelement(IEditorPart editorPart) {
		if (editorPart instanceof GraphicalEditor) {
			EditPart rootEditPart = (EditPart) editorPart
					.getAdapter(EditPart.class);
			if (rootEditPart == null) {
				return null;
			}
			EObject rootElement = null;
			Object object = ((EditPart) rootEditPart.getChildren().get(0))
					.getModel();
			if (object instanceof View) {
				rootElement = ((View) object).getElement();
			}
			return rootElement;
		}
		if (editorPart != null) {
			Diagram diagram = (Diagram) editorPart.getAdapter(Diagram.class);
			if (diagram != null) {
				return diagram.getElement();
			}
		}
		return null;
	}

	/**
	 * Gets the root element resource.
	 * 
	 * @param editorPart
	 *            the editor part
	 * 
	 * @return the root element resource
	 */
	protected static Resource getRootElementResource(IEditorPart editorPart) {
		EObject rootElement = getEditorRootelement(editorPart);
		Resource resource = null;
		if (rootElement != null) {
			resource = rootElement.eResource();
		}
		return resource;
	}

	/**
	 * Gets a {@link Diagram} name.
	 * 
	 * @param diagram
	 *            the diagram
	 * 
	 * @return the diagram name
	 */
	public static String getDiagramName(Diagram diagram) {
		if (diagram != null) {
			return diagram.getName();
		}
		return "";
	}

	/**
	 * Returns a list with the {@link Diagram}s that have no underlying semantic
	 * element.
	 * 
	 * @param resource
	 * @return
	 */
	public static List<Diagram> getNullElementDiagrams(Resource resource) {
		if (resource instanceof GMFResource == false) {
			return Collections.emptyList();
		}
		List<Diagram> diagrams = new ArrayList<Diagram>();
		for (Object content : resource.getContents()) {
			if (content instanceof Diagram) {
				if (((Diagram) content).getElement() == null) {
					diagrams.add((Diagram) content);
				}
			}
		}
		if (diagrams.size() > 0) {
			return diagrams;
		}
		return Collections.emptyList();
	}
}
