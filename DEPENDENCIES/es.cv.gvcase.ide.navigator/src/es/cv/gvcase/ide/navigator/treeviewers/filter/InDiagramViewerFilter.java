/*******************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) - initial api implementation
 * [02/04/08] Francisco Javier Cano Muñoz (Prodevelop) - adaptation to Common Navigator Framework
 *
 ******************************************************************************/
package es.cv.gvcase.ide.navigator.treeviewers.filter;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.gmf.runtime.diagram.ui.parts.DiagramEditor;
import org.eclipse.gmf.runtime.emf.core.util.EMFCoreUtil;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.NotationPackage;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import es.cv.gvcase.ide.navigator.provider.NavigatorLoadedResourcesItem;
import es.cv.gvcase.ide.navigator.provider.NavigatorRootItem;

/**
 * Filters elements that are not shown in the active editor's {@link Diagram}.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 */
public class InDiagramViewerFilter extends ViewerFilter {

	/**
	 * For an {@link EObject} will return if it is shown in the active diagram.
	 * 
	 * @param viewer
	 *            the viewer
	 * @param parentElement
	 *            the parent element
	 * @param element
	 *            the element
	 * 
	 * @return true, if select
	 */
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof Resource || element instanceof NavigatorRootItem
				|| element instanceof NavigatorLoadedResourcesItem) {
			return true;
		}

		Diagram diagram = getActiveDiagram();
		if (diagram == null) {
			return true;
		}
		EObject diagramElement = getDiagramElement(diagram);
		if (diagramElement == null) {
			return true;
		}

		EObject eObject = unwrapEObject(element);
		if (eObject == null) {
			return false;
		}
		return isEObjectInDiagram(eObject, diagram)
				|| isEObjectParent(eObject, diagramElement);
	}

	/**
	 * Checks if is e object in diagram.
	 * 
	 * @param element
	 *            the element
	 * @param diagram
	 *            the diagram
	 * 
	 * @return true, if is e object in diagram
	 */
	protected boolean isEObjectInDiagram(EObject element, Diagram diagram) {
		List<View> views = getEObjectViews(element);
		for (View view : views) {
			if (diagram.equals(view.getDiagram())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if is e object parent.
	 * 
	 * @param parent
	 *            the parent
	 * @param element
	 *            the element
	 * 
	 * @return true, if is e object parent
	 */
	protected boolean isEObjectParent(EObject parent, EObject element) {
		if (element == parent) {
			return true;
		}
		EObject realParent = element.eContainer();
		if (realParent == null) {
			return false;
		}
		if (isEObjectParent(parent, realParent)) {
			return true;
		}
		return false;
	}

	/**
	 * Gets the active diagram.
	 * 
	 * @return the active diagram
	 */
	protected Diagram getActiveDiagram() {
		try {
			IWorkbench wb = PlatformUI.getWorkbench();
			if (wb != null) {
				IWorkbenchWindow ww = wb.getActiveWorkbenchWindow();
				if (ww != null) {
					IWorkbenchPage wp = ww.getActivePage();
					if (wp != null) {
						IEditorPart editorPart = wp.getActiveEditor();
						if (editorPart instanceof DiagramEditor) {
							return ((DiagramEditor) editorPart).getDiagram();
						} else {
							Diagram diagram = (Diagram) editorPart
									.getAdapter(Diagram.class);
							if (diagram != null) {
								return diagram;
							}
						}
					}
				}
			}
		} catch (NullPointerException ex) {
			return null;
		}
		return null;
	}

	/**
	 * Gets the diagram element.
	 * 
	 * @param diagram
	 *            the diagram
	 * 
	 * @return the diagram element
	 */
	protected EObject getDiagramElement(Diagram diagram) {
		return diagram.getElement();
	}

	/**
	 * Gets the e object views.
	 * 
	 * @param element
	 *            the element
	 * 
	 * @return the e object views
	 */
	protected static List<View> getEObjectViews(EObject element) {
		List<View> views = new ArrayList<View>();
		if (element != null) {
			EReference[] features = { NotationPackage.eINSTANCE
					.getView_Element() };
			views.addAll(EMFCoreUtil.getReferencers(element, features));
		}
		return views;
	}

	/**
	 * Unwrap e object.
	 * 
	 * @param element
	 *            the element
	 * 
	 * @return the e object
	 */
	protected EObject unwrapEObject(Object element) {
		if (element instanceof EObject) {
			return (EObject) element;
		}
		return null;
	}

}
