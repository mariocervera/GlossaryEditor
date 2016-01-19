/*******************************************************************************
 * Copyright (c) 2008 - 2011 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – Initial implementation.
 * 				 Gabriel Merin Cubero (Integranova) – Operations to add a diagram version
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.ui.URIEditorInput;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.SnapToGeometry;
import org.eclipse.gef.SnapToHelper;
import org.eclipse.gmf.runtime.diagram.core.preferences.PreferencesHint;
import org.eclipse.gmf.runtime.diagram.core.util.ViewType;
import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.parts.DiagramEditor;
import org.eclipse.gmf.runtime.diagram.ui.parts.IDiagramGraphicalViewer;
import org.eclipse.gmf.runtime.diagram.ui.parts.IDiagramWorkbenchPart;
import org.eclipse.gmf.runtime.emf.core.resources.GMFResource;
import org.eclipse.gmf.runtime.emf.core.util.EMFCoreUtil;
import org.eclipse.gmf.runtime.emf.type.core.commands.SetValueCommand;
import org.eclipse.gmf.runtime.emf.type.core.requests.IEditCommandRequest;
import org.eclipse.gmf.runtime.emf.type.core.requests.SetRequest;
import org.eclipse.gmf.runtime.notation.Bounds;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.Node;
import org.eclipse.gmf.runtime.notation.NotationPackage;
import org.eclipse.gmf.runtime.notation.Shape;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

import es.cv.gvcase.emf.common.part.EditingDomainRegistry;
import es.cv.gvcase.emf.common.provider.GeneralLabelProvider;
import es.cv.gvcase.emf.common.util.EMFUtil;
import es.cv.gvcase.emf.common.util.ResourceUtil;
import es.cv.gvcase.mdt.common.Activator;
import es.cv.gvcase.mdt.common.commands.diagram.EClassToDiagramRegistry;
import es.cv.gvcase.mdt.common.commands.wrappers.GMFtoEMFCommandWrapper;
import es.cv.gvcase.mdt.common.edit.policies.GroupSelectionEditPolicy;
import es.cv.gvcase.mdt.common.ids.MOSKittEditorIDs;
import es.cv.gvcase.mdt.common.model.ExtendedFeatureElement;
import es.cv.gvcase.mdt.common.model.ExtendedFeatureElementFactory;
import es.cv.gvcase.mdt.common.part.CachedResourcesDiagramEditor;
import es.cv.gvcase.mdt.common.part.CachedResourcesEditorInput;
import es.cv.gvcase.mdt.common.part.MOSKittMultiPageEditor;
import es.cv.gvcase.mdt.common.part.MultiPageEditorPart;
import es.cv.gvcase.mdt.common.provider.ILinkDescriptor;
import es.cv.gvcase.mdt.common.provider.MOSKittCommonLabelProvider;
import es.cv.gvcase.mdt.common.provider.ViewInfo;

/**
 * The Class MDTUtil.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * @author <a href="mailto:gmerin@prodevelop.es">Gabriel Merin Cubero</a>
 */
public class MDTUtil {

	// ID for the EAnnotation that describes the version of the first editor
	// that modified the diagram
	/** The editor version. */
	public static String EDITOR_VERSION = "es.cv.gvcase.mdt.common.Editor"; //$NON-NLS-1$

	/**
	 * This list of plugin identifiers details the plugins of which their
	 * versions must be stored when a version number is added do a
	 * {@link Diagram}.
	 */
	public static String[] BundlesToStoreVersionOfInDiagram = new String[] {
			"es.cv.gvcase.mdt.common", "es.cv.gvcase.emf.common" };

	public static final String DIAGRAM_PLUGIN_VERSION = "diagram_plugin_version-";

	public static String DiagramVersionAnnotationDetailKey = "version";

	/**
	 * Add to a {@link Diagram} the version of the first editor that will modify
	 * the diagram. If exists an old version, updates it to the new given. <br />
	 * When adding the version with which a diagram was created, we also store
	 * the version of MDT-common and EMF-common that with which they were
	 * created.
	 * 
	 * @param diagram
	 *            the diagram
	 * @param version
	 *            version of the editor that is going to modify the diagram
	 * 
	 * @author gmerin
	 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano
	 *         Muñoz</a>
	 */
	public static void addDiagramVersion(Diagram diagram, String version) {
		if (version != null) {
			// add the simple version as given
			EAnnotation eAnnotation = diagram.getEAnnotation(EDITOR_VERSION);
			if (eAnnotation == null) {
				eAnnotation = EcoreFactory.eINSTANCE.createEAnnotation();
				eAnnotation.setSource(EDITOR_VERSION);
				// Add a detail to store the version
				eAnnotation.getDetails().put(DiagramVersionAnnotationDetailKey,
						version);

				diagram.getEAnnotations().add(eAnnotation);
			} else {
				// Remove the old version
				eAnnotation.getDetails().remove(
						DiagramVersionAnnotationDetailKey);
				// Add a detail to store the version
				eAnnotation.getDetails().put(DiagramVersionAnnotationDetailKey,
						version);
			}
		}
		// Add the version of the plugins in the
		// BundlesToStoreVersionOfInDiagram list
		// This stores the versions of the plugins mdt-common and emf-common of
		// MOSKitt for future reference in the created diagrams
		addDiagramVersionAdditionalPlugins(diagram,
				BundlesToStoreVersionOfInDiagram);
	}

	/**
	 * Adds the version of the plugins in the list of given plugin identifiers
	 * to the given diagram.
	 * 
	 * @param diagram
	 */
	public static void addDiagramVersionAdditionalPlugins(Diagram diagram,
			String[] bundleIDs) {
		if (diagram != null && bundleIDs != null && bundleIDs.length > 0) {
			String bundleVersion = null;
			String annotationSource = null;
			EAnnotation versionEAnnotation = null;
			for (String bundleID : bundleIDs) {
				// for each bundle identifier
				// look for the bundle and get its version number
				bundleVersion = MDTUtil.getBundleVersion(bundleID);
				// get or create the EAnnotation
				if (bundleVersion != null) {
					annotationSource = DIAGRAM_PLUGIN_VERSION + bundleID;
					versionEAnnotation = getOrCreateEAnnotation(diagram,
							annotationSource);
					// set the value
					versionEAnnotation.getDetails().put(
							DiagramVersionAnnotationDetailKey, bundleVersion);
				}
			}
		}
	}

	/**
	 * Gets the {@link EAnnotation} with the given source from the given
	 * {@link EModelElement}. <br />
	 * If that {@link EAnnotation} does not exist, it is created and stored in
	 * the given element, then returned.
	 * 
	 * @param element
	 * @param source
	 * @return
	 */
	public static EAnnotation getOrCreateEAnnotation(EModelElement element,
			String source) {
		if (element == null || source == null) {
			return null;
		}
		EAnnotation eAnnotation = element.getEAnnotation(source);
		if (eAnnotation == null) {
			eAnnotation = EcoreFactory.eINSTANCE.createEAnnotation();
			eAnnotation.setSource(source);
			element.getEAnnotations().add(eAnnotation);
		}
		return eAnnotation;
	}

	public static final String HideFromContentProvidersEAnnotationSource = "es.cv.gvcase.mdt.common.util.HideFromContentProciders"; //$NON-NLS-1$

	/**
	 * Adds an {@link EAnnotation} to a {@link EModelElement} to indicate that
	 * that element must not be shown in content providers.
	 * 
	 * @param element
	 */
	public static void addHideElementFromContentProvidersEAnnotation(
			EModelElement element) {
		if (element == null) {
			return;
		}
		EAnnotation eAnnotation = element
				.getEAnnotation(HideFromContentProvidersEAnnotationSource);
		if (eAnnotation == null) {
			eAnnotation = EcoreFactory.eINSTANCE.createEAnnotation();
			eAnnotation.setSource(HideFromContentProvidersEAnnotationSource);
			element.getEAnnotations().add(eAnnotation);
		}
		return;
	}

	/**
	 * Removes the {@link EAnnotation} that indicates that that element must not
	 * be shown in content providers from a {@link EModelElement}.
	 * 
	 * @param element
	 */
	public static void removeHideElementFromContentProvidersEAnnotation(
			EModelElement element) {
		if (element == null) {
			return;
		}
		EAnnotation eAnnotation = element
				.getEAnnotation(HideFromContentProvidersEAnnotationSource);
		if (eAnnotation != null) {
			element.getEAnnotations().remove(eAnnotation);
		}
		return;
	}

	/**
	 * Removes the Hidable elements from the given {@link Collection}.
	 * 
	 * @param elements
	 */
	public static void filterHidableElements(Collection<EObject> elements) {
		if (elements == null || elements.size() <= 0) {
			return;
		}
		List<EObject> toRemove = new ArrayList<EObject>();
		// look for those elements that should be hidden
		for (EObject element : elements) {
			if (element instanceof EModelElement) {
				if (((EModelElement) element)
						.getEAnnotation(HideFromContentProvidersEAnnotationSource) != null) {
					toRemove.add(element);
				}
			}
		}
		// remove the hidable elements from the list
		elements.removeAll(toRemove);
	}

	/**
	 * Removes the Hidable elements from the given Array
	 * 
	 * @param elements
	 */
	public static Object[] filterHidableElements(Object[] elements) {
		if (elements == null || elements.length <= 0) {
			return elements;
		}
		List<EObject> toRemove = new ArrayList<EObject>();
		// look for those elements that should be hidden
		for (Object element : elements) {
			if (element instanceof EModelElement) {
				if (((EModelElement) element)
						.getEAnnotation(HideFromContentProvidersEAnnotationSource) != null) {
					toRemove.add((EObject) element);
				}
			}
		}
		//
		int length = elements.length - toRemove.size();
		Object[] filteresElements = new Object[length];
		//
		int i = 0;
		for (Object element : elements) {
			if (!toRemove.contains(element)) {
				filteresElements[i] = element;
				i++;
			}
		}
		// remove the hidable elements from the list
		return filteresElements;
	}

	/**
	 * Obtain the {@link Diagram} the version of first editor that modified the
	 * diagram.
	 * 
	 * @param diagram
	 *            the diagram
	 * 
	 * @return the diagram version
	 * 
	 * @author gmerin The version of the first editor that modified the diagram
	 */
	public static String getDiagramVersion(Diagram diagram) {
		EAnnotation eAnnotation = diagram.getEAnnotation(EDITOR_VERSION);
		// No version of the diagram
		if (eAnnotation == null)
			return null;
		return eAnnotation.getDetails().get(DiagramVersionAnnotationDetailKey); //$NON-NLS-1$
	}

	/**
	 * Gets the version of the given bundle ID with which this {@link Diagram}
	 * was created.
	 * 
	 * @param diagram
	 * @param bundleID
	 * @return
	 */
	public static String getDiagramProducedBundleVersion(Diagram diagram,
			String bundleID) {
		if (diagram == null || bundleID == null) {
			return null;
		}
		String annotationSource = DIAGRAM_PLUGIN_VERSION + bundleID;
		EAnnotation versionAnnotation = diagram
				.getEAnnotation(annotationSource);
		if (versionAnnotation != null) {
			return versionAnnotation.getDetails().get(
					DiagramVersionAnnotationDetailKey);
		}
		return null;
	}

	/**
	 * Obtain the {@link Plugin}'s version of the editor.
	 * 
	 * @param editor
	 *            Editor Part
	 * 
	 * @return the editor version
	 * 
	 * @author gmerin The version of the editor
	 */
	public static String getEditorVersion(IEditorPart editor) {
		return getBundleVersion(editor.getSite().getPluginId());
	}

	/**
	 * Obtain the {@link Plugin}'s version of the editor.
	 * 
	 * @param pluginId
	 *            Plugin ID
	 * 
	 * @return The version of the plugin
	 * 
	 * @author gmerin
	 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano
	 *         Muñoz</a>
	 */
	public static String getBundleVersion(String pluginId) {
		Bundle bundle = Platform.getBundle(pluginId);
		if (bundle == null) {
			return null;
		}
		String version = (String) bundle.getHeaders().get(
				Constants.BUNDLE_VERSION);
		return version;
	}

	/**
	 * Gets the workspace location.
	 * 
	 * @return the workspace location
	 */
	public static IPath getWorkspaceLocation() {
		try {
			return ResourcesPlugin.getWorkspace().getRoot().getLocation();
		} catch (NullPointerException ex) {
			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					"Error getting workspace", ex);
			Activator.getDefault().getLog().log(status);
			return null;
		}
	}

	/**
	 * Full file {@link Path} to {@link Resource}.
	 * 
	 * @param filePath
	 *            the file path
	 * 
	 * @return the string
	 */
	public static String fullFilePathToResource(String filePath) {
		String workspaceLocation = getWorkspaceLocation().toString();
		return filePath.replaceFirst(workspaceLocation, ""); //$NON-NLS-1$
	}

	/**
	 * Full file {@link Path} to {@link Resource} {@link URI}.
	 * 
	 * @param filePath
	 *            the file path
	 * 
	 * @return the uRI
	 */
	public static URI fullFilePathToResourceURI(String filePath) {
		String uri = fullFilePathToResource(filePath);
		return URI.createPlatformResourceURI(uri, true);
	}

	/**
	 * Gets the {@link Diagram} name.
	 * 
	 * @param diagram
	 *            the diagram
	 * 
	 * @return the diagram name
	 */
	public static String getDiagramName(Diagram diagram) {
		if (diagram != null) {
			String type = diagram.getType();
			String label = MOSKittEditorIDs.getExtensionsMapModelToLabel().get(
					type);
			label = label != null ? label : ""; //$NON-NLS-1$
			return label + " : " + diagram.getName(); //$NON-NLS-1$
		}
		return null;
	}

	/**
	 * Retieves the {@link Diagram} from the given {@link IEditorPart}.
	 * 
	 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano
	 *         Muñoz</a>
	 * @param editor
	 * @return
	 */
	public static Diagram getDiagramFomEditor(IEditorPart editor) {
		if (editor instanceof DiagramEditor) {
			return ((DiagramEditor) editor).getDiagram();
		} else {
			if (editor != null) {
				Object adapted = editor.getAdapter(Diagram.class);
				if (adapted instanceof Diagram) {
					return (Diagram) adapted;
				}
			}
		}
		return null;
	}

	/**
	 * Gets the active editor in the active workbench window.
	 * 
	 * @return
	 */
	public static IEditorPart getActiveEditor() {
		try {
			IWorkbenchWindow ww = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow();
			if (ww != null && ww.getActivePage() != null) {
				return ww.getActivePage().getActiveEditor();
			} else {
				return null;
			}
		} catch (NullPointerException ex) {
			return null;
		}
	}

	/**
	 * Gets the active editor in the active workbench window. If the active
	 * editor is a {@link MultiPageEditorPart} the nested active editor is
	 * returned.
	 * 
	 * @return
	 */
	public static IEditorPart getActiveNestedEditor() {
		IEditorPart editorPart = getActiveEditor();
		if (editorPart instanceof MultiPageEditorPart) {
			return ((MultiPageEditorPart) editorPart).getActiveEditor();
		}
		return editorPart;
	}

	/**
	 * Gets all editors getting the nested editors from a MOSKittMultiPageEditor
	 * 
	 * @return
	 */
	public static List<IEditorPart> getAllEditorsAndNestesEditors() {
		IEditorReference[] editorReferences = null;
		try {
			editorReferences = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage()
					.getEditorReferences();
		} catch (NullPointerException ex) {
			// if the workbench is not available for any reason, return an empty
			// collection
			return Collections.emptyList();
		}
		List<IEditorPart> editors = new ArrayList<IEditorPart>();
		if (editorReferences != null && editorReferences.length > 0) {
			// iterate through all the editors in the workbench
			for (IEditorReference editorReference : editorReferences) {
				if (editorReference.getEditor(false) != null) {
					IEditorPart editorPart = editorReference.getEditor(false);
					if (editorPart instanceof MOSKittMultiPageEditor) {
						// get all the editors from a multi page editor
						for (IEditorPart nestedEditor : ((MOSKittMultiPageEditor) editorPart)
								.getEditors()) {
							editors.add(nestedEditor);
						}
					} else {
						// store a normal editor
						editors.add(editorPart);
					}
				}
			}
		}
		return editors;
	}

	/**
	 * Gets a {@link List} of all the open editors identifiers.
	 * 
	 * @return
	 */
	public static List<String> getAllEditorAndNestedEditorsIDs() {
		List<IEditorPart> allEditors = getAllEditorsAndNestesEditors();
		if (allEditors == null || allEditors.isEmpty()) {
			return Collections.emptyList();
		}
		List<String> allEditorIDs = new ArrayList<String>();
		for (IEditorPart editor : allEditors) {
			if (editor instanceof CachedResourcesDiagramEditor) {
				String editorID = ((CachedResourcesDiagramEditor) editor)
						.getEditorID();
			}
		}
		return allEditorIDs;
	}

	/**
	 * Gets the preferences hint.
	 * 
	 * @param kind
	 *            the kind
	 * 
	 * @return the preferences hint
	 * 
	 * @throws ExecutionException
	 *             the execution exception
	 */
	public static PreferencesHint getPreferencesHint(String kind) {
		String editor = MOSKittEditorIDs.getAllExtensionModelToEditor().get(
				kind);
		return new PreferencesHint(editor);
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
	 * Gets the root element resource.
	 * 
	 * @param editorPart
	 *            the editor part
	 * 
	 * @return the root element resource
	 */
	public static Resource getRootElementResource(IEditorPart editorPart) {
		EObject rootElement = getEditorRootelement(editorPart);
		Resource resource = null;
		if (rootElement != null) {
			resource = rootElement.eResource();
		}
		return resource;
	}

	/**
	 * Gets the editor rootelement.
	 * 
	 * @param editorPart
	 *            the editor part
	 * 
	 * @return the editor rootelement
	 */
	public static EObject getEditorRootelement(IEditorPart editorPart) {
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

	/**
	 * Gets the host {@link Diagram}.
	 * 
	 * @param policy
	 *            the policy
	 * 
	 * @return the host diagram
	 */
	public static Diagram getHostDiagram(EditPolicy policy) {
		if (policy.getHost() instanceof IGraphicalEditPart) {
			View view = ((IGraphicalEditPart) policy.getHost())
					.getNotationView();
			if (view != null) {
				view = view.getDiagram();
			}
			if (view instanceof Diagram) {
				return (Diagram) view;
			}
		}
		return null;
	}

	/**
	 * Gets the host {@link IGraphicalEditPart}
	 * 
	 * @param policy
	 *            the policy
	 * 
	 * @return the host diagram
	 */
	public static IGraphicalEditPart getHostGraphicalEditPart(EditPolicy policy) {
		if (policy.getHost() instanceof IGraphicalEditPart) {
			return (IGraphicalEditPart) policy.getHost();
		}
		return null;
	}

	/**
	 * Gets a {@link Diagram} from the {@link Request} looking for it in the
	 * extended data with key <MultiDiagramUtil.BelongToDiagramSource>
	 * 
	 * @param request
	 * @return
	 */
	public static Diagram getDiagramFromRequest(IEditCommandRequest request) {
		Diagram diagram = null;
		if (request != null) {
			Object data = request.getParameters().get(
					MultiDiagramUtil.BelongToDiagramSource);
			if (data instanceof Diagram) {
				diagram = (Diagram) data;
			}
		}
		return diagram;
	}

	public static List<EObject> getRootElementsFromFile(IEditorInput input) {
		return getRootElementsFromFile(input, null);
	}

	public static List<EObject> getRootElementsFromFile(IEditorInput input,
			ResourceSet resourceSet) {
		URI uri = null;
		IURIEditorInput uriEditorInput = (IURIEditorInput) Platform
				.getAdapterManager().getAdapter(input, IURIEditorInput.class);
		if (uriEditorInput != null) {
			uri = URI.createURI(uriEditorInput.getURI().toString());
		} else {
			IFileEditorInput fileEditorInput = (IFileEditorInput) Platform
					.getAdapterManager().getAdapter(input,
							IFileEditorInput.class);
			if (fileEditorInput != null) {
				uri = URI.createURI(fileEditorInput.getFile().getLocationURI()
						.toString());
			} else {
				URIEditorInput uRIEditorInput = (URIEditorInput) Platform
						.getAdapterManager().getAdapter(input,
								URIEditorInput.class);
				if (uRIEditorInput != null) {
					uri = ((URIEditorInput) input).getURI();
				}
			}
		}
		if (uri != null) {
			if (resourceSet == null) {
				TransactionalEditingDomain domain = EditingDomainRegistry
						.getInstance().get("", input);
				resourceSet = domain.getResourceSet();
			}
			// resourceSet = resourceSet != null ? resourceSet
			// : new ResourceSetImpl();
			Resource resource = resourceSet.getResource(uri, true);
			if (resource != null) {
				List<EObject> rootEObjects = new ArrayList<EObject>();
				for (EObject eObject : resource.getContents()) {
					rootEObjects.add(eObject);
				}
				return rootEObjects;
			}
		}
		return Collections.emptyList();
	}

	/**
	 * Gets the object name or empty string.
	 * 
	 * @param object
	 *            the object
	 * 
	 * @return the object name or empty string
	 */
	public static String getObjectNameOrEmptyString(Object object) {
		String name = getObjectName(object);
		return name == null ? "" : name; //$NON-NLS-1$
	}

	/** The Constant getNameNames. */
	private static final String[] getNameNames = { "getName", "getname", //$NON-NLS-1$ //$NON-NLS-2$
			"getId", "getid", "getID" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	/**
	 * Gets the object name.
	 * 
	 * @param object
	 *            the object
	 * 
	 * @return the object name
	 */
	public static String getObjectName(Object object) {
		if (object == null) {
			return null;
		}
		Method method = null;
		Object o = null;
		for (String methodName : getNameNames) {
			try {
				method = object.getClass()
						.getMethod(methodName, (Class[]) null);
			} catch (NoSuchMethodException e) {
				method = null;
			}
			if (method != null) {
				break;
			}
		}
		if (method != null) {
			try {
				o = method.invoke(object, (Object[]) null);
			} catch (IllegalAccessException ex) {
				return null;
			} catch (InvocationTargetException ex) {
				return null;
			}
			if (o instanceof String) {
				return (String) o;
			}
		}
		return null;
	}

	/**
	 * Resolve semantic.
	 * 
	 * @param object
	 *            the object
	 * 
	 * @return the e object
	 */
	public static EObject resolveSemantic(Object object) {
		if (object instanceof EditPart) {
			object = ((EditPart) object).getModel();
		}
		if (object instanceof View) {
			object = ((View) object).getElement();
		}
		if (object instanceof EObject) {
			return (EObject) object;
		}
		return null;
	}

	private static final String LastOpenedDiagramPropertyBase = "lastOpenedDiagram";

	/** The Constant LastOpenedDiagramProperty. */
	public static final QualifiedName LastOpenedDiagramProperty = new QualifiedName(
			Activator.PLUGIN_ID, LastOpenedDiagramPropertyBase);

	/**
	 * A {@link QualifiedName} for a specific editor.
	 * 
	 * @param editorID
	 * @return
	 */
	public static QualifiedName getLastOpenedDiagramPropertyQualifiedNameForEditor(
			String editorID) {
		return new QualifiedName(Activator.PLUGIN_ID,
				LastOpenedDiagramPropertyBase
						+ (editorID != null ? editorID : ""));
	}

	public static CachedResourcesDiagramEditor getCachedResourcesDiagramEditorFromEditorRef(
			IEditorReference reference) {
		if (reference == null) {
			return null;
		}
		IWorkbenchPart part = reference.getEditor(false);

		if (part == null) {
			return null;
		}

		CachedResourcesDiagramEditor editor = (CachedResourcesDiagramEditor) Platform
				.getAdapterManager().getAdapter(part,
						CachedResourcesDiagramEditor.class);
		if (editor != null) {
			return editor;
		}
		return null;
	}

	/**
	 * Sets the last opened {@link Diagram} property.
	 * 
	 * @param diagram
	 *            the diagram
	 * 
	 * @return true, if successful
	 */
	public static boolean setLastOpenedDiagramProperty(Diagram diagram) {
		return setLastOpenedDiagramProperty(diagram, null);
	}

	/**
	 * Sets the last opened {@link Diagram} property.
	 * 
	 * @param diagram
	 *            the diagram
	 * 
	 * @return true, if successful
	 */
	public static boolean setLastOpenedDiagramProperty(Diagram diagram,
			String editorID) {
		return setLastOpenedDiagramProperty(diagram, editorID, true);
	}

	/**
	 * Sets the last opened {@link Diagram} property.
	 * 
	 * @param diagram
	 *            the diagram
	 * 
	 * @return true, if successful
	 */
	public static boolean setLastOpenedDiagramProperty(Diagram diagram,
			String editorID, boolean setEditorForFileProperty) {
		if (editorID == null) {
			editorID = MOSKittEditorIDs.getAllExtensionModelToEditor().get(
					diagram.getType());
		}
		// get Diagram fragment
		String fragment = diagram.eResource().getURIFragment(diagram);
		// get Diagram IFile
		URI uri = diagram.eResource().getURI();
		uri = uri != null ? uri.trimFragment() : null;
		String path = es.cv.gvcase.emf.common.util.PathsUtil
				.fromAbsoluteFileSystemToAbsoluteWorkspace(uri
						.toPlatformString(true));
		return setLastOpenedDiagramProperty(path, fragment, editorID,
				setEditorForFileProperty);
	}

	/**
	 * Sets the last opened {@link Diagram} property.
	 * 
	 * @param diagram
	 *            the diagram
	 * 
	 * @return true, if successful
	 */
	public static boolean setLastOpenedDiagramProperty(String path,
			String fragment, String editorID, boolean setEditorForFileProperty) {
		IPath filePath = new Path(path);
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(filePath);
		// set IFile property
		if (file != null) {
			try {
				QualifiedName propertyQualifiedName = getLastOpenedDiagramPropertyQualifiedNameForEditor(editorID);
				file.setPersistentProperty(propertyQualifiedName, fragment);
				return true;
			} catch (CoreException ex) {
				IStatus status = new Status(IStatus.WARNING,
						Activator.PLUGIN_ID, "Error setting file property");
				Activator.getDefault().getLog().log(status);
			}
		}

		return false;
	}

	/**
	 * Sets the last opened {@link Diagram} property.
	 * 
	 * @param diagram
	 *            the diagram
	 * 
	 * @return true, if successful
	 */
	public static boolean removeLastOpenedDiagramProperty(Diagram diagram) {
		if (diagram == null || diagram.eResource() == null) {
			return false;
		}

		// get Diagram fragment
		String fragment = diagram.eResource().getURIFragment(diagram);

		// get Diagram IFile
		URI uri = diagram.eResource().getURI();
		uri = uri != null ? uri.trimFragment() : null;
		String path = es.cv.gvcase.emf.common.util.PathsUtil
				.fromAbsoluteFileSystemToAbsoluteWorkspace(uri
						.toPlatformString(true));
		IPath filePath = new Path(path);
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(filePath);
		// set IFile property
		if (file != null) {
			try {
				file.setPersistentProperty(LastOpenedDiagramProperty, null);
				// set the correct editor to edit
				Diagram firstDiagram = null;
				for (EObject elem : diagram.eResource().getContents()) {
					if (elem instanceof Diagram) {
						firstDiagram = (Diagram) elem;
						break;
					}
				}
				String editorID = MOSKittEditorIDs
						.getAllExtensionModelToEditor().get(
								firstDiagram.getType());
				if (editorID != null) {
					setEditorForDiagramProperty(file, editorID);
				}
				return true;
			} catch (CoreException ex) {
				IStatus status = new Status(IStatus.WARNING,
						Activator.PLUGIN_ID, "Error setting file property");
				Activator.getDefault().getLog().log(status);
			}
		}

		return false;
	}

	/**
	 * Sets the editor for {@link Diagram} property.
	 * 
	 * @param uri
	 *            the uri
	 * @param editorID
	 *            the editor id
	 * 
	 * @throws CoreException
	 *             the core exception
	 */
	public static void setEditorForDiagramProperty(URI uri, String editorID) {
		String part1 = uri.path().replaceFirst("resource", "");
		IPath path = new Path(part1);
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		if (file != null) {
			setEditorForDiagramProperty(file, editorID);
		}
	}

	/**
	 * Sets the editor for {@link Diagram} property.
	 * 
	 * @param file
	 *            the file
	 * @param editorID
	 *            the editor id
	 * 
	 * @throws CoreException
	 *             the core exception
	 */
	public static void setEditorForDiagramProperty(IFile file, String editorID) {
		if (file != null) {
			try {
				file.setPersistentProperty(IDE.EDITOR_KEY, editorID);
			} catch (CoreException ex) {
				Activator.getDefault().logError(
						"Couldn't set file editorToOpen property", ex);
			}
			return;
		}
	}

	/**
	 * Removes the {@link IFile} property that associates a file with the editor
	 * that must open it.
	 * 
	 * @param file
	 */
	public static void removeEditorForFileProperty(IFile file) {
		if (file != null) {
			try {
				// passing a null value the persistent property is removed.
				file.setPersistentProperty(IDE.EDITOR_KEY, null);
			} catch (CoreException ex) {
				Activator.getDefault().logError(
						"Error removing editor property.", ex);
			}
		}
	}

	/**
	 * Removes the property that specifies the editor that opens an
	 * {@link IFile}.
	 * 
	 * @param uri
	 * @return
	 */
	public static boolean removeEditorForDiagramProperty(URI uri) {
		String part1 = uri.path().replaceFirst("resource", "");
		IPath path = new Path(part1);
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		if (file != null) {
			return removeEditorForDiagramProperty(file);
		}
		return false;
	}

	/**
	 * Removes the editor-to-open property associated to the given file
	 * represented by the given editor input.
	 * 
	 * @param input
	 */
	public static void removeEditorForDiagramProperty(IEditorInput input) {
		if (input != null) {
			URI inputURI = URIUtil.getPlatformResourceURI(input);
			if (inputURI != null) {
				IFile file = URIUtil.getFile(inputURI);
				if (file != null) {
					removeEditorForDiagramProperty(file);
				}
			}
		} else {
			return;
		}
	}

	/**
	 * Removes the property that specifies the editor that opens an
	 * {@link IFile}.
	 * 
	 * @param file
	 * @return
	 */
	public static boolean removeEditorForDiagramProperty(IFile file) {
		try {
			file.setPersistentProperty(IDE.EDITOR_KEY, null);
			return true;
		} catch (CoreException ex) {
			return false;
		}
	}

	public static boolean setEditorForDiagramForFirstDiagram(Resource resource) {
		Diagram diagram = MDTUtil.getFirstDiagramFromResource(resource);
		if (diagram != null) {
			String kind = diagram.getType();
			String editorID = MOSKittEditorIDs.getAllExtensionModelToEditor()
					.get(kind);
			if (editorID != null) {
				MDTUtil
						.setEditorForDiagramProperty(resource.getURI(),
								editorID);
			}
		}
		return false;
	}

	/**
	 * Gets the last opened {@link Diagram} property.
	 * 
	 * @param filePath
	 *            the file path
	 * 
	 * @return the last opened diagram property
	 */
	public static String getLastOpenedDiagramProperty(String filePath) {
		return getLastOpenedDiagramPropertyForEditor(filePath, null);
	}

	public static String getLastOpenedDiagramPropertyForEditor(String filePath,
			String editorID) {
		if (filePath == null) {
			return null;
		}
		IPath path = new Path(filePath);
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		if (file != null) {
			try {
				return file
						.getPersistentProperty(getLastOpenedDiagramPropertyQualifiedNameForEditor(editorID));
			} catch (CoreException ex) {
				IStatus status = new Status(IStatus.WARNING,
						Activator.PLUGIN_ID,
						"Error retieving editor property: ", ex);
				Activator.getDefault().getLog().log(status);
			}
		}
		return null;
	}

	/**
	 * Copy editor input but {@link URI}.
	 * 
	 * @param input
	 *            the input
	 * @param uri
	 *            the uri
	 * 
	 * @return the cached resources editor input
	 */
	public static CachedResourcesEditorInput copyEditorInputButUri(
			IEditorInput input, URI uri) {
		CachedResourcesEditorInput cachedInput = new CachedResourcesEditorInput(
				uri, input.getName());
		if (input instanceof CachedResourcesEditorInput) {
			cachedInput.setUnload(((CachedResourcesEditorInput) input)
					.isUnload());
		}
		return cachedInput;
	}

	/**
	 * Gets or builds a valid {@link CachedResourcesEditorInput} for a given
	 * {@link URI}. The given {@link URI} must point to a valid
	 * {@link GMFResource}. <br>
	 * This {@link URI} can have a fragment defined. If the fragment does not
	 * point to a valid {@link Diagram}, the first Diagram of the
	 * {@link Resource} is returned as input.
	 * 
	 * @param editorInput
	 * @param editorID
	 * @return
	 */
	public static CachedResourcesEditorInput getValidEditorInput(
			URI editorInput, String editorID) {
		if (editorID != null && editorInput != null) {
			// get the shared editing domain, that will have the resources
			// already loaded and with the latest changes.
			TransactionalEditingDomain domain = EditingDomainRegistry
					.getInstance().get(editorID,
							editorInput.trimFragment().toString());
			if (domain != null) {
				// search for the given diagram in all the resourceSet.
				URI uri = getProperDiagramURIToOpen(domain.getResourceSet(),
						editorInput);
				if (uri != null) {
					// if we found a matching diagram, return it as a
					// CachedResourcesEditorInput
					return new CachedResourcesEditorInput(uri, false);
				}
			}
		}
		return null;
	}

	/**
	 * Tries to get the given {@link Diagram} in {@link URI}. If not, it will
	 * get the first Diagram.
	 * 
	 * @param resourceSet
	 * @param editorInput
	 * @return
	 */
	public static URI getProperDiagramURIToOpen(ResourceSet resourceSet,
			URI editorInput) {
		// search the target diagram in the resourceset.
		EObject eObject = searchEObjectFromFragment(editorInput, resourceSet);
		if (eObject != null) {
			// if found, make sure it is a Diagram
			Diagram diagram = (Diagram) Platform.getAdapterManager()
					.getAdapter(eObject, Diagram.class);
			if (diagram != null) {
				// if it's a Diagram, return the proper URI
				return diagram.eResource().getURI().appendFragment(
						diagram.eResource().getURIFragment(diagram));
			}
		}
		// if target diagram is not found, look for the first Diagram
		Resource resource = resourceSet.getResource(editorInput.trimFragment(),
				true);
		if (resource != null) {
			Diagram diagram = getFirstDiagramFromResource(resource);
			if (diagram != null) {
				// if we find a diagram, return its URI.
				return resource.getURI().appendFragment(
						resource.getURIFragment(diagram));
			}
		}
		return null;
	}

	/**
	 * Searchs for an {@link EObject} in a {@link ResourceSet} by its fragment.
	 * 
	 * @param uriFragment
	 * @param resourceSet
	 * @return
	 */
	public static EObject searchEObjectFromFragment(URI uri,
			ResourceSet resourceSet) {
		if (uri == null) {
			return null;
		}
		String uriFragment = uri.fragment();
		if (uriFragment != null && uriFragment.length() > 0
				&& resourceSet != null) {
			resourceSet.getResource(uri.trimFragment(), true);
			for (Resource resource : resourceSet.getResources()) {
				// search for the EObject in each Resource
				EObject eObject = searchEObjectFromFragment(uriFragment,
						resource);
				if (eObject != null) {
					// if we find the target EObject in any of the Resources,
					// return it.
					return eObject;
				}
			}
		}
		return null;
	}

	/**
	 * Searchs for an {@link EObject} in a {@link Resource} by its fragment.
	 * 
	 * @param uriFragment
	 * @param resource
	 * @return
	 */
	public static EObject searchEObjectFromFragment(String uriFragment,
			Resource resource) {
		if (uriFragment != null && uriFragment.length() > 0 && resource != null) {
			// search for the EObject in the Resource.
			return resource.getEObject(uriFragment);
		}
		return null;
	}

	/**
	 * Returns the first {@link Diagram} in a {@link GMFResource}.
	 * 
	 * @param resource
	 * @return
	 */
	public static Diagram getFirstDiagramFromResource(Resource resource) {
		if (resource != null && resource.getContents().size() > 0) {
			// look for a Diagram in the whole Resource
			for (EObject eObject : resource.getContents()) {
				Diagram diagram = (Diagram) Platform.getAdapterManager()
						.getAdapter(eObject, Diagram.class);
				if (diagram != null) {
					// the first Diagram found is returned.
					return diagram;
				}
			}
		}
		return null;
	}

	/**
	 * Looks in the class hierarchy for a {@link Class} or Interface with
	 * qualified name className.
	 * 
	 * @param clazz
	 * @param className
	 * @return
	 */
	public static boolean isOfType(Class clazz, String className) {
		if (clazz == null || className == null) {
			return false;
		}
		// check class
		if (clazz.getName().equals(className)) {
			return true;
		}
		// look interfaces
		for (Class c : clazz.getInterfaces()) {
			if (isOfType(c, className)) {
				return true;
			}
		}
		// look superclass
		return isOfType(clazz.getSuperclass(), className);
	}

	/**
	 * Gets the edits the parts from selection.
	 * 
	 * @param selection
	 *            the selection
	 * 
	 * @return the edits the parts from selection
	 */
	public static List<EditPart> getEditPartsFromSelection(ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			List<EditPart> editParts = new ArrayList<EditPart>();
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			for (Object object : structuredSelection.toList()) {
				if (object instanceof EditPart) {
					editParts.add((EditPart) object);
				}
			}
			return editParts;
		}
		return Collections.emptyList();
	}

	public static List<IGraphicalEditPart> getGraphicalEditPartsFromSelection(
			ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			List<IGraphicalEditPart> editParts = new ArrayList<IGraphicalEditPart>();
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			for (Object object : structuredSelection.toList()) {
				if (object instanceof IGraphicalEditPart) {
					editParts.add((IGraphicalEditPart) object);
				}
			}
			return editParts;
		}
		return Collections.emptyList();
	}

	/**
	 * Gets the {@link EObject}s from selection.
	 * 
	 * @param selection
	 *            the selection
	 * 
	 * @return the e objects from selection
	 */
	public static List<EObject> getEObjectsFromSelection(ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			List<EObject> eObjects = new ArrayList<EObject>();
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			for (Object object : structuredSelection.toList()) {
				EObject eObject = resolveSemantic(object);
				if (eObject != null) {
					eObjects.add(eObject);
				}
			}
			return eObjects;
		}
		return Collections.EMPTY_LIST;
	}

	/**
	 * Looks for {@link Diagram}s associated to an {@link EObject} and all its
	 * children.
	 * 
	 * @param eObject
	 *            EObject to begin the search
	 * @param gmfResource
	 *            {@link GMFResource} to look for Diagrams. If null, a
	 *            GMFResource will be looked up in the EObject's
	 *            {@link ResourceSet}, if any.
	 * 
	 * @return the diagrams in hierarchy
	 */
	public static List<Diagram> getDiagramsInHierarchy(EObject eObject,
			Resource gmfResource) {
		// no eObject means nothing to search for
		if (eObject == null) {
			return Collections.EMPTY_LIST;
		}
		// no gmfResource given, we'll search one
		if (gmfResource instanceof GMFResource == false) {
			Resource resource = eObject.eResource();
			ResourceSet resourceSet = resource != null ? resource
					.getResourceSet() : null;
			if (resourceSet != null) {
				for (Resource resourceAux : resourceSet.getResources()) {
					if (resourceAux instanceof GMFResource) {
						// gmfResource found
						gmfResource = resourceAux;
						break;
					}
				}
			}
		}
		// no gmfResource given nor found. Nothing to do.
		if (gmfResource instanceof GMFResource == false) {
			return Collections.EMPTY_LIST;
		}
		// start of search
		// List to store all found <Diagram>s
		List<Diagram> diagrams = new ArrayList<Diagram>();
		// List of all <EObject>s to check
		List<EObject> allEObjects = new ArrayList<EObject>();
		allEObjects.add(eObject);
		for (Iterator<EObject> iterator = eObject.eAllContents(); iterator
				.hasNext();) {
			allEObjects.add(iterator.next());
		}
		// search for each <EObject>'s <Diagram>s
		for (EObject element : allEObjects) {
			for (EObject content : gmfResource.getContents()) {
				if (content instanceof Diagram
						&& element.equals(((Diagram) content).getElement())) {
					// a <Diagram> that references an affected <EObject> has
					// been found
					diagrams.add((Diagram) content);
				}
			}
		}
		if (diagrams.size() > 0) {
			// some <Diagram>s were found
			return diagrams;
		}
		return Collections.EMPTY_LIST;
	}

	// // Filter Views and Labels

	/** The Constant FilterViewAndLabelsSource. */
	public static final String FilterViewAndLabelsSource = "es.cv.gvcase.mdt.common.FilterViewsAndLabelsSource";

	/**
	 * Sets the elements to filter to {@link Diagram}.
	 * 
	 * @param diagram
	 *            the diagram
	 * @param infos
	 *            the infos
	 */
	public static void setElementsToFilterToDiagram(Diagram diagram,
			Collection<Integer> infos) {
		if (diagram == null || infos == null) {
			return;
		}
		EAnnotation eAnnotation = diagram
				.getEAnnotation(FilterViewAndLabelsSource);
		if (eAnnotation == null) {
			eAnnotation = EcoreFactory.eINSTANCE.createEAnnotation();
			eAnnotation.setSource(FilterViewAndLabelsSource);
			diagram.getEAnnotations().add(eAnnotation);
		}
		Collection<Integer> existing = getAllViewsToFilterFromDiagram(diagram);
		Collection<Integer> toAdd = new ArrayList<Integer>();
		Collection<Integer> toDelete = new ArrayList<Integer>();
		// build toAdd list
		for (Integer integer : infos) {
			if (existing.contains(integer) == false) {
				toAdd.add(integer);
			}
		}
		// build toDelete list
		for (Integer integer : existing) {
			if (infos.contains(integer) == false) {
				toDelete.add(integer);
			}
		}
		// add
		for (Integer integer : toAdd) {
			String key = String.valueOf(integer);
			if (false == eAnnotation.getDetails().containsKey(key)) {
				eAnnotation.getDetails().put(key, "");
			}
		}
		// delete
		for (Integer integer : toDelete) {
			String key = String.valueOf(integer);
			if (true == eAnnotation.getDetails().containsKey(key)) {
				eAnnotation.getDetails().remove(key);
			}
		}
	}

	/**
	 * Removes the element to filter from {@link Diagram}.
	 * 
	 * @param diagram
	 *            the diagram
	 * @param infos
	 *            the infos
	 */
	public static void removeElementToFilterFromDiagram(Diagram diagram,
			Collection<ViewInfo> infos) {
		if (diagram == null || infos == null || infos.size() <= 0) {
			return;
		}
		EAnnotation eAnnotation = diagram
				.getEAnnotation(FilterViewAndLabelsSource);
		if (eAnnotation == null) {
			return;
		}
		for (ViewInfo info : infos) {
			String key = String.valueOf(info.getVisualID());
			if (eAnnotation.getDetails().containsKey(key)) {
				eAnnotation.getDetails().removeKey(key);
			}
		}
	}

	/**
	 * Gets the all views to filter from {@link Diagram}.
	 * 
	 * @param diagram
	 *            the diagram
	 * 
	 * @return the all views to filter from diagram
	 */
	public static Collection<Integer> getAllViewsToFilterFromDiagram(
			Diagram diagram) {
		EAnnotation eAnnotation = diagram.getDiagram().getEAnnotation(
				FilterViewAndLabelsSource);
		if (eAnnotation == null) {
			return Collections.EMPTY_LIST;
		}
		Collection<Integer> keys = new ArrayList<Integer>();
		for (String key : eAnnotation.getDetails().keySet()) {
			keys.add(Integer.valueOf(key));
		}
		return keys;
	}

	/**
	 * Find element in {@link Diagram} filter.
	 * 
	 * @param diagram
	 *            the diagram
	 * @param visualID
	 *            the visual id
	 * 
	 * @return true, if successful
	 */
	public static boolean findElementInDiagramFilter(Diagram diagram,
			int visualID) {
		if (diagram != null) {
			EAnnotation eAnnotation = diagram
					.getEAnnotation(FilterViewAndLabelsSource);
			if (eAnnotation == null) {
				return false;
			}
			String key = String.valueOf(visualID);
			return eAnnotation.getDetails().containsKey(key);
		}
		return false;
	}

	public static final String HideViewID = "es.cv.gvcase.mdt.common.view.hideView";

	/**
	 * Filter {@link Diagram} {@link View}s.
	 * 
	 * @param diagram
	 *            the diagram
	 */
	public static void filterDiagramViews(Diagram diagram) {
		Collection<Integer> filters = getAllViewsToFilterFromDiagram(diagram);
		Iterator<EObject> it = diagram.eAllContents();
		while (it.hasNext()) {
			EObject eObject = it.next();
			if (eObject instanceof View) {
				View view = (View) eObject;
				if (getViewExtendedFeatureHideView(view) == false) {
					Integer integer = null;
					try {
						integer = Integer.valueOf(view.getType());
					} catch (NumberFormatException ex) {
						integer = null;
					}
					if (integer != null) {
						SetRequest request = null;
						if (filters.contains(integer)) {
							request = new SetRequest(
									view,
									NotationPackage.eINSTANCE.getView_Visible(),
									false);
						} else {
							request = new SetRequest(
									view,
									NotationPackage.eINSTANCE.getView_Visible(),
									true);
						}
						SetValueCommand command = new SetValueCommand(request);
						Object value = request.getValue();
						EObject elementToEdit = request.getElementToEdit();
						EStructuralFeature feature = request.getFeature();
						if (value != null && elementToEdit != null
								&& feature != null
								&& elementToEdit.eGet(feature) != null
								&& !value.equals(elementToEdit.eGet(feature))) {
							TransactionUtil
									.getEditingDomain(view)
									.getCommandStack()
									.execute(
											new GMFtoEMFCommandWrapper(command));
						}
					}
				} else {
					if (view.isVisible()) {
						SetRequest request = new SetRequest(view,
								NotationPackage.eINSTANCE.getView_Visible(),
								false);
						SetValueCommand command = new SetValueCommand(request);
						TransactionUtil.getEditingDomain(view)
								.getCommandStack().execute(
										new GMFtoEMFCommandWrapper(command));
					}
				}
			}
		}
	}

	/**
	 * Check if the current view has the annotation HideViewID
	 * (es.cv.gvcase.mdt.common.view.hideView) to set this view always to NOT
	 * Visible
	 * 
	 * @param view
	 * @return
	 */
	private static Boolean getViewExtendedFeatureHideView(View view) {
		// return false;
		EAnnotation eAnnotation = view.getEAnnotation(HideViewID);
		if (eAnnotation != null && eAnnotation.getDetails().size() > 0) {
			String bool = eAnnotation.getDetails().keySet().iterator().next();
			if ("true".equalsIgnoreCase(bool)) {
				return true;
			}
		}
		return false;
	}

	// //****////

	public static IStatusLineManager getStatusLineManager() {
		try {
			return PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage().getActiveEditor().getEditorSite()
					.getActionBars().getStatusLineManager();
		} catch (NullPointerException ex) {
			return null;
		}
	}

	/**
	 * This function returns the commands to remove references of the given
	 * eObject in the reached loaded models
	 * 
	 * @param editingDomain
	 *            : the editing domain
	 * @param eObject
	 *            : the eObject
	 * @return: the commands
	 */
	public static Command removeEObjectReferences(EditingDomain editingDomain,
			EObject eObject) {
		CompoundCommand cc = new CompoundCommand();
		for (Setting s : EcoreUtil.UsageCrossReferencer.find(eObject, eObject
				.eResource().getResourceSet())) {
			if (s.getEStructuralFeature().isMany()) {
				cc.append(RemoveCommand.create(editingDomain, s.getEObject(), s
						.getEStructuralFeature(), eObject));
			} else {
				cc.append(SetCommand.create(editingDomain, s.getEObject(), s
						.getEStructuralFeature(), null));
			}
		}

		if (!cc.isEmpty())
			return cc.unwrap();
		else
			return null;
	}

	protected static ILabelProvider instanceLabelProvider = null;

	public static ILabelProvider getLabelProvider() {
		if (instanceLabelProvider == null) {
			instanceLabelProvider = EMFUtil.getAdapterFactoryLabelProvider();
			instanceLabelProvider = new MOSKittCommonLabelProvider(
					instanceLabelProvider);
		}
		return instanceLabelProvider;
	}

	protected static ITreeContentProvider instanceContentProvider = null;

	public static ITreeContentProvider getContentProvider() {
		if (instanceContentProvider == null) {
			instanceContentProvider = EMFUtil
					.getAdapterFactoryContentProvider();
		}
		return instanceContentProvider;
	}

	// //
	// Handle notifications
	// //
	public static void handleViewInfoNotification(Notification event,
			IGraphicalEditPart editPart) {
		if (event.getNotifier() instanceof org.eclipse.emf.ecore.EAnnotation) {
			org.eclipse.emf.ecore.EAnnotation eAnnotation = (org.eclipse.emf.ecore.EAnnotation) event
					.getNotifier();
			if (eAnnotation.getSource() != null
					&& eAnnotation
							.getSource()
							.equals(
									es.cv.gvcase.mdt.common.util.MDTUtil.FilterViewAndLabelsSource)) {
				es.cv.gvcase.mdt.common.util.DiagramEditPartsUtil
						.updateDiagram(editPart);
			}
		}
	}

	public static void handleAddedRawView(Notification event,
			IGraphicalEditPart editPart) {
		if (editPart != null) {
			EObject model = editPart.getNotationView();
			EObject notifier = event.getNotifier() instanceof EObject ? (EObject) event
					.getNotifier()
					: null;
			Object oldValue = event.getOldValue();
			Object newValue = event.getNewValue();
			EStructuralFeature feature = event.getFeature() instanceof EStructuralFeature ? (EStructuralFeature) event
					.getFeature()
					: null;
			if (notifier != null
					&& notifier.equals(model)
					&& NotationPackage.eINSTANCE.getView_PersistedChildren()
							.equals(feature) && newValue != oldValue) {
				DiagramEditPartsUtil.updateDiagram(editPart);
			}
		}
	}

	public static boolean isEventAppearanceNotification(
			Notification notification) {
		Object feature = notification.getFeature();
		if (org.eclipse.gmf.runtime.notation.NotationPackage.eINSTANCE
				.getSize_Width().equals(feature)
				|| org.eclipse.gmf.runtime.notation.NotationPackage.eINSTANCE
						.getSize_Height().equals(feature)
				|| org.eclipse.gmf.runtime.notation.NotationPackage.eINSTANCE
						.getLocation_X().equals(feature)
				|| org.eclipse.gmf.runtime.notation.NotationPackage.eINSTANCE
						.getLocation_Y().equals(feature)) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if two {@link ILinkDescriptor}s are equal
	 * 
	 * @param link1
	 * @param link2
	 * @return
	 */
	public static boolean checkSameLinkDescriptor(ILinkDescriptor link1,
			ILinkDescriptor link2) {
		EObject model1 = link1.getModelElement();
		EObject model2 = link2.getModelElement();
		if (model1 == null && model2 != null) {
			return false;
		}
		if (model1 != null && model2 == null) {
			return false;
		}
		if (model1 != null && model1.equals(model2) == false) {
			return false;
		}

		EObject source1 = link1.getSource();
		EObject source2 = link2.getSource();
		if (source1 == null && source2 != null) {
			return false;
		}
		if (source1 != null && source2 == null) {
			return false;
		}
		if (source1 != null && source1.equals(source2) == false) {
			return false;
		}

		EObject destination1 = link1.getDestination();
		EObject destination2 = link2.getDestination();
		if (destination1 == null && destination2 != null) {
			return false;
		}
		if (destination1 != null && destination2 == null) {
			return false;
		}
		if (destination1 != null && destination1.equals(destination2) == false) {
			return false;
		}

		String type1 = link1.getType();
		String type2 = link2.getType();
		if (type1 == null && type2 != null) {
			return false;
		}
		if (type1 != null && type2 == null) {
			return false;
		}
		if (type1 != null && type1.equals(type2) == false) {
			return false;
		}

		int visualID1 = link1.getVisualID();
		int visualID2 = link2.getVisualID();
		if (visualID1 <= -1 || visualID2 <= -1) {
			return false;
		}
		if (visualID1 != visualID2) {
			return false;
		}

		return true;
	}

	/**
	 * Checks whether an editor input is the same than the editor input of a
	 * given editor.
	 * 
	 * @param editorRef
	 * @param newInput
	 * @return
	 */
	public static boolean checkSameEditorInput(IEditorReference editorRef,
			IEditorInput newInput) {
		IEditorInput oldInput;
		try {
			oldInput = editorRef.getEditorInput();
		} catch (PartInitException e) {
			return false;
		}

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
			if (CachedResourcesEditorInput.checkSameEditorInputWithFragment(
					oldInput, newCachedEditorInput)) {
				return true;
			}
			return false;
		}

		// get real URIs for both the old input ant the new input
		CachedResourcesDiagramEditor editor = MDTUtil
				.getCachedResourcesDiagramEditorFromEditorRef(editorRef);
		String editorID = editor != null ? editor.getEditorID() : null;
		URI oldUri = URIUtil.getUri(oldInput, editorID);
		URI newUri = URIUtil.getUri(newInput, editorID);

		if (oldUri != null && newUri != null) {
			return oldUri.equals(newUri);
		}

		return false;
	}

	/**
	 * Compares the given diagram versions. Returns 0 if ver1 == ver2; Returns
	 * -1 if ver1 > ver2; Returns 1 if ver1 < ver2
	 * 
	 * @param ver1
	 * @param ver2
	 * @return
	 */
	public static int compareDiagramVersions(String ver1, String ver2) {
		return new DiagramVersionComparator().compare(ver1, ver2);
	}

	/**
	 * Gets the Major Number of a version
	 * 
	 * @param version
	 * @return
	 */
	public static int getMajorNumberVersion(String version) {
		String[] parts = version.split("\\.");
		if (parts.length < 3 || parts.length > 4) {
			return -1;
		} else {
			return Integer.parseInt(parts[0]);
		}
	}

	/**
	 * Gets the Minor Number of a version
	 * 
	 * @param version
	 * @return
	 */
	public static int getMinorNumberVersion(String version) {
		String[] parts = version.split("\\.");
		if (parts.length < 3 || parts.length > 4) {
			return -1;
		} else {
			return Integer.parseInt(parts[1]);
		}

	}

	/**
	 * Gets the Revision Number of a version
	 * 
	 * @param version
	 * @return
	 */
	public static int getRevisionNumberVersion(String version) {
		String[] parts = version.split("\\.");
		if (parts.length < 3 || parts.length > 4) {
			return -1;
		} else {
			return Integer.parseInt(parts[2]);
		}

	}

	/**
	 * Gets the Qualifier Number of a version
	 * 
	 * @param version
	 * @return
	 */
	public static String getQualifierNumberVersion(String version) {
		String[] parts = version.split("\\.");
		if (parts.length < 4) {
			return "";
		} else {
			return parts[3];
		}
	}

	/**
	 * Gets the Diagram from an Editor Input. You can specify an editingDomain
	 * in case the diagram is yet loaded
	 * 
	 * @param version
	 * @return
	 */
	public static Diagram getDiagramFromEditorInput(IEditorInput input,
			TransactionalEditingDomain editingDomain) {
		URI uri = null;
		if (input instanceof URIEditorInput) {
			uri = ((URIEditorInput) input).getURI();
		}
		if (input instanceof FileEditorInput) {
			String uriPath = ((FileEditorInput) input).getPath().toString();
			uri = URI.createFileURI(uriPath);
		}

		if (uri == null) {
			return null;
		}

		String fragment = uri.fragment();
		Resource resource = null;
		EObject eObject = null;
		if (editingDomain != null && editingDomain.getResourceSet() != null) {
			resource = ResourceUtil.loadResourceFastOptions(uri.trimFragment(),
					editingDomain.getResourceSet());
			// resource = editingDomain.getResourceSet().getResource(
			// uri.trimFragment(), true);
			if (resource == null) {
				return null;
			}

			// if fragment is available, it points to the diagram to be
			// migrated. If not, get the first diagram founded in the resource
			if (fragment != null) {
				eObject = resource.getEObject(fragment);
			} else {
				for (EObject content : resource.getContents()) {
					if (content instanceof Diagram) {
						eObject = content;
						break;
					}
				}
			}

			if (eObject instanceof Diagram) {
				return (Diagram) eObject;
			} else {
				return null;
			}
		} else {
			ResourceSet resourceSet = new ResourceSetImpl();
			// resource = resourceSet.getResource(uri, true);
			resource = ResourceUtil.loadResourceFastOptions(uri.trimFragment(),
					resourceSet);
			// if fragment is available, it points to the diagram to be
			// migrated. If not, get the first diagram founded in the resource
			if (fragment != null) {
				eObject = resource.getEObject(fragment);
			} else {
				for (EObject content : resource.getContents()) {
					if (content instanceof Diagram) {
						eObject = content;
						break;
					}
				}
			}

			if (eObject instanceof Diagram) {
				return (Diagram) eObject;
			} else {
				return null;
			}
		}
	}

	/**
	 * Returns a String with the Diagram kind of the diagram to be created. <br>
	 * May ask the user to select one diagram among all available.
	 * 
	 * @return
	 */
	public static String getDiagramKindToCreate(
			IGraphicalEditPart grahicalHostEditPart) {
		return getDiagramKindToCreate(grahicalHostEditPart
				.resolveSemanticElement().getClass());
	}

	/**
	 * Returns a String with the Diagram kind of the diagram to be created. <br>
	 * May ask the user to select one diagram among all available.
	 * 
	 * @return
	 */
	public static String getDiagramKindToCreate(Class clazz) {
		List<String> diagramL = EClassToDiagramRegistry.getInstance()
				.getDiagramsForClass(clazz);
		if (diagramL.isEmpty()) {
			return null;
		} else if (diagramL.size() == 1) {
			return diagramL.get(0);
		}

		ElementListSelectionDialog dialog = new ElementListSelectionDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				new GeneralLabelProvider());
		dialog.setMessage("Select the diagram to be created");
		dialog.setTitle("Diagram selection");
		dialog.setElements(diagramL.toArray());
		if (dialog.open() == Dialog.OK) {
			return (String) dialog.getFirstResult();
		}

		return null;
	}

	// //
	// Selection
	// //

	public static ISelection getSelectionFromActiveEditor() {
		IEditorPart editor = getActiveEditor();
		if (editor instanceof IDiagramWorkbenchPart) {
			return ((IDiagramWorkbenchPart) editor).getDiagramGraphicalViewer()
					.getSelection();
		}
		return StructuredSelection.EMPTY;
	}

	/**
	 * Install the {@link GroupSelectionEditPolicy} on the given
	 * {@link IGraphicalEditPart}.
	 * 
	 * @param editPart
	 */
	public static void installGroupSelectionEditPolicy(
			IGraphicalEditPart editPart) {
		editPart.installEditPolicy(
				GroupSelectionEditPolicy.GROUP_SELECTION_FEEDBACK_ROLE,
				new GroupSelectionEditPolicy());
	}

	// //
	// apply effects on diagrams/editors
	// //

	/**
	 * Adds the SnapToGeometry property to a Diagram editor.
	 */
	public static void addSnapToGeometryToEditor(IDiagramWorkbenchPart part) {
		part.getDiagramGraphicalViewer().setProperty(
				SnapToGeometry.PROPERTY_SNAP_ENABLED, new Boolean(true));
	}

	// //
	// ResourceSet
	// //

	/**
	 * Loads a {@link Resource} from a {@link ResourceSet} normalizing the
	 * {@link URI} before searching for the resource. This can prevent loading a
	 * the same Resource under different URIs.
	 */
	public static Resource getResourceByURI(URI resourceURI,
			ResourceSet resourceSet, boolean loadOnDemand) {
		if (resourceURI != null && resourceSet != null) {
			return resourceSet.getResource(resourceURI, loadOnDemand);
		}
		return null;
	}

	/**
	 * Returns a SnapToHelper for the SnapToGeometry.
	 * 
	 * @param editPart
	 * @param clazz
	 * @return
	 */
	public static Object getSnapToHelperAdapter(IGraphicalEditPart editPart,
			Class clazz) {
		if (SnapToHelper.class.equals(clazz)) {
			return new SnapToGeometry(editPart);
		}
		return null;
	}

	// //
	// Orphaned Views
	// //

	public static boolean isOrphanView(View view) {
		if (view == null) {
			return false;
		}
		if (view.getType() == ViewType.NOTE || view.getType() == ViewType.TEXT) {
			return false;
		}
		if (view.getElement() == null || view.getElement().eIsProxy()) {
			return true;
		}
		return false;
	}

	// //
	// External elements
	// //

	public static boolean isExternalElement(EModelElement element) {
		if (element != null) {
			ExtendedFeatureElement extendedElement = ExtendedFeatureElementFactory
					.getInstance().asExtendedFeatureElement(element);
			Boolean external = extendedElement
					.getBoolean(ExtendedFeaturesIDs.ExternalExtendedFeatureID);
			if (external != null) {
				return external.booleanValue();
			}
		}
		return false;
	}

	// //
	// Mouse and figures location
	// //

	public static Point getMouseLocation(IGraphicalEditPart editPart) {
		org.eclipse.swt.graphics.Point mouseLocation = Display.getCurrent()
				.getCursorLocation();
		mouseLocation = editPart.getViewer().getControl().toControl(
				mouseLocation);
		Point location = new Point(mouseLocation.x, mouseLocation.y);
		return location;
	}

	/**
	 * Returns the mouse location relative to the figure on which the mouse is
	 * located. If the mouse is located on the canvas, the position returned is
	 * relative to the canvas.
	 * 
	 * @param editPart
	 *            an {@link EditPart} from which to get an
	 *            {@link EditPartViewer}.
	 * @return
	 */
	public static Point getMouseLocationUnderFigure(IGraphicalEditPart editPart) {
		if (editPart != null && editPart.getViewer() != null) {
			Point location = getMouseLocation(editPart);
			if (location != null) {
				EditPart foundEditpart = editPart.getViewer().findObjectAt(
						location);
				if (!editPart.equals(foundEditpart)) {
					return null;
				}
				if (foundEditpart instanceof IGraphicalEditPart) {
					Point figureAbsoluteLocation = getAbsoluteLocation(editPart);
					if (figureAbsoluteLocation != null) {
						location.x -= figureAbsoluteLocation.x;
						location.y -= figureAbsoluteLocation.y;
					}
				}
			}
		}
		return null;
	}

	public static Point getAbsoluteLocation(IGraphicalEditPart editPart) {
		if (editPart != null) {

			/**
			 * Get the DiagramEditPart. compare editPart to diagram editpart. if
			 * equals calculate offset from viewPort else calculate upward
			 * offsets until reaching the diagramEditPart calculate offset from
			 * viewPort
			 */

			DiagramEditPart diagramEditPart = DiagramEditPartsUtil
					.getDiagramEditPart(editPart);
			Point originalLocation = getNodeLocation(editPart);
			if (diagramEditPart != null && !diagramEditPart.equals(editPart)) {
				EditPart parentEditPart = editPart;
				parentEditPart = parentEditPart.getParent();
				while (!diagramEditPart.equals(parentEditPart)) {
					if (editPart != null) {
						addLocationFromPoint(originalLocation, parentEditPart);
					}
					parentEditPart = parentEditPart.getParent();
				}
			}
			// remove ViewPort offset.
			Point viewPortLocation = getViewPortLocation(editPart);
			if (viewPortLocation != null) {
				originalLocation.x -= viewPortLocation.x;
				originalLocation.y -= viewPortLocation.y;
			}
		}
		return null;
	}

	protected static void addLocationFromPoint(Point originalLocation,
			Object node) {
		Point nodeLocation = getNodeLocation(node);
		if (nodeLocation != null) {
			originalLocation.x += nodeLocation.x;
			originalLocation.y += nodeLocation.y;
		} else {
			// 
			return;
		}
	}

	public static Point getViewPortLocation(IGraphicalEditPart editPart) {
		if (editPart != null && editPart.getViewer() != null
				&& editPart.getViewer() != null) {
			Control viewerControl = editPart.getViewer().getControl();
			if (viewerControl instanceof FigureCanvas) {
				return ((FigureCanvas) viewerControl).getViewport()
						.getLocation();
			}
		}
		return null;
	}

	public static Bounds getNodeBounds(Object object) {
		IGraphicalEditPart editPart = null;
		if (object instanceof IGraphicalEditPart) {
			editPart = (IGraphicalEditPart) object;
			object = ((IGraphicalEditPart) object).getNotationView();
		}
		Bounds bounds = null;
		if (object instanceof Node) {
			Node node = (Node) object;
			bounds = node.getLayoutConstraint() instanceof Bounds ? (Bounds) node
					.getLayoutConstraint()
					: null;
		}
		if (object instanceof Shape) {
			Shape shape = (Shape) object;
			bounds = shape.getLayoutConstraint() instanceof Bounds ? (Bounds) shape
					.getLayoutConstraint()
					: null;
		}
		if (bounds != null) {
			return bounds;
		} else if (editPart != null) {
			return getNodeBounds(editPart.getParent());
		}
		return null;
	}

	public static Point getNodeLocation(Object object) {
		Bounds bounds = getNodeBounds(object);
		if (bounds != null) {
			Point point = new Point(bounds.getX(), bounds.getY());
			return point;
		}
		return null;
	}

	public static Point getRelativeOffset(Point fixed, Point relative) {
		if (fixed != null && relative != null) {
			return new Point(relative.x - fixed.x, relative.y - fixed.y);
		}
		return null;
	}

	public static Point getAdaptedNodePositionToBounds(Object object,
			Bounds constrainingBounds) {
		if (object == null || constrainingBounds == null) {
			return null;
		}
		Bounds elementBounds = getNodeBounds(object);
		Point elementPosition = getNodeLocation(object);
		if (elementBounds == null || elementPosition == null) {
			return null;
		}
		if (elementPosition.x + elementBounds.getWidth() > constrainingBounds
				.getWidth()) {
			int width = elementBounds.getWidth();
			if (width < 0) {
				width = 60;
			}
			elementPosition.x = constrainingBounds.getWidth() - width;
		}
		if (elementPosition.y + elementBounds.getHeight() > constrainingBounds
				.getHeight()) {
			int height = elementBounds.getHeight();
			if (height < 0) {
				height = 60;
			}
			elementPosition.y = constrainingBounds.getHeight() - height;
		}
		return elementPosition;
	}

	/**
	 * Selectes the editparts that represent the given elements in the selection
	 * in the given editor.
	 * 
	 * @param activeEditor
	 * @param selection
	 */
	public static void setSelectionInEditor(IEditorPart activeEditor,
			ISelection selection) {
		if (activeEditor instanceof IDiagramWorkbenchPart) {
			// set editor selection; select EditParts
			IDiagramGraphicalViewer viewer = ((IDiagramWorkbenchPart) activeEditor)
					.getDiagramGraphicalViewer();
			if (viewer == null) {
				return;
			}
			List<EditPart> editPartsToSelect = MDTUtil
					.getEditPartsFromSelection(selection, viewer);
			StructuredSelection selectedEditParts = new StructuredSelection(
					editPartsToSelect);
			viewer.setSelection(selectedEditParts);
			if (!selectedEditParts.isEmpty()) {
				EditPart editPart = (EditPart) selectedEditParts
						.getFirstElement();
				viewer.reveal(editPart);
			}
		}
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

}