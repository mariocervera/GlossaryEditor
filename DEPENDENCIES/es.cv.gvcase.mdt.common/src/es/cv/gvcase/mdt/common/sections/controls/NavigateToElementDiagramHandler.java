/*******************************************************************************
 * Copyright (c) 2011 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana. All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 * 			Francisco Javier Cano Muñoz (Prodevelop) - initial API implementation
 ******************************************************************************/
package es.cv.gvcase.mdt.common.sections.controls;

import java.util.List;

import org.eclipse.emf.common.ui.URIEditorInput;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.gmf.runtime.diagram.ui.parts.IDiagramWorkbenchPart;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import es.cv.gvcase.emf.ui.common.composites.EObjectChooserComposite;
import es.cv.gvcase.emf.ui.common.composites.EObjectChooserComposite.INavigationHandler;
import es.cv.gvcase.mdt.common.Activator;
import es.cv.gvcase.mdt.common.part.MOSKittMultiPageEditor;
import es.cv.gvcase.mdt.common.util.MDTUtil;
import es.cv.gvcase.mdt.common.util.MultiDiagramUtil;

/**
 * An {@link INavigationHandler} for the {@link EObjectChooserComposite} that
 * knows how to navigate to the {@link Diagram} that shows a given element.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public class NavigateToElementDiagramHandler implements INavigationHandler {

	// //
	//
	// //

	protected class SelectDiagramInputDialog extends MessageDialog {

		protected ListViewer listViewer = null;

		public ListViewer getListViewer() {
			return listViewer;
		}

		protected ISelection selected = null;

		public ISelection getSelection() {
			return selected;
		}

		protected List<IEditorInput> editorInputsAvailable = null;

		// //

		public SelectDiagramInputDialog(Shell shell,
				List<IEditorInput> availableEditorInputs) {
			super(shell, "Select diagram to open", null,
					"Select the diagram to open or cancel",
					MessageDialog.QUESTION_WITH_CANCEL, new String[] { "Open",
							"Cancel" }, 1);
			this.editorInputsAvailable = availableEditorInputs;
		}

		@Override
		protected Control createCustomArea(Composite parent) {
			/**
			 * Create a dialog area with a list view where the available editor
			 * inputs are shown with their name for the user to select one and
			 * only one.
			 */
			// create the list viewer
			this.listViewer = new ListViewer(parent, SWT.SINGLE | SWT.BORDER);
			GridData gridData = new GridData(GridData.FILL_BOTH);
			listViewer.getControl().setLayoutData(gridData);
			listViewer.getControl().addMouseListener(new MouseListener() {
				public void mouseUp(MouseEvent e) {
					// nothing on mouse up
				}

				public void mouseDown(MouseEvent e) {
					// nothing on mouse down
				}

				public void mouseDoubleClick(MouseEvent e) {
					buttonPressed(0);
				}
			});
			// create the content provider
			listViewer.setContentProvider(new IStructuredContentProvider() {
				public void inputChanged(Viewer viewer, Object oldInput,
						Object newInput) {
					// nothing to do
				}

				public void dispose() {
					// nothing to do
				}

				public Object[] getElements(Object inputElement) {
					if (editorInputsAvailable != null) {
						Object[] elements = new Object[editorInputsAvailable
								.size()];
						editorInputsAvailable.toArray(elements);
						return elements;
					} else {
						return new Object[0];
					}
				}
			});
			// create the label provider
			listViewer.setLabelProvider(new ILabelProvider() {
				public void removeListener(ILabelProviderListener listener) {
					// nothing to do
				}

				public boolean isLabelProperty(Object element, String property) {
					return false;
				}

				public void dispose() {
					// nothing to do
				}

				public void addListener(ILabelProviderListener listener) {
					// nothing to do
				}

				public String getText(Object element) {
					if (element instanceof URIEditorInput) {
						return ((URIEditorInput) element).getName();
					}
					return "Editor input: " + element.toString();
				}

				public Image getImage(Object element) {
					return null;
				}
			});
			// set the input for the list
			listViewer.setInput(editorInputsAvailable);
			//
			return listViewer.getControl();
		}

		@Override
		protected void buttonPressed(int buttonId) {
			// store the selection before disposal of the widget
			this.selected = getListViewer().getSelection();
			// normal operation
			super.buttonPressed(buttonId);
		}
	}

	// //
	// Interface to get the element whose diagram we want to show
	// //

	/**
	 * Provides the {@link EObject} to be shown in a {@link Diagram} when
	 * clicking this button.
	 */
	public interface IElementToShowProvider {
		/**
		 * Provides the {@link EObject} to be shown in a {@link Diagram}.
		 * 
		 * @return
		 */
		EObject getElementToShow();
	}

	//

	protected IElementToShowProvider elementProvider = null;

	public IElementToShowProvider getElementProvider() {
		return elementProvider;
	}

	public void setElementProvider(IElementToShowProvider elementProvider) {
		this.elementProvider = elementProvider;
	}

	// //
	// Constructor
	// //

	public NavigateToElementDiagramHandler(
			IElementToShowProvider elementProvider) {
		this.elementProvider = elementProvider;
	}

	// //
	// Handler of the navigation
	// //

	public void handleNavigate() {
		// Handling the navigation means the user wants to open a
		// diagram where the element that is given by the
		// elementProvider is shown. To open that diagram these
		// steps need to be taken:
		/**
		 * 1) Get the EObject to be shown from the element provider.
		 */
		EObject elementToShow = getElementToShow();
		if (elementToShow == null) {
			return;
		}
		/**
		 * 2) Get the diagrams that have a View of the given element
		 */
		List<IEditorInput> diagramInputsThatShowElement = getDiagramsThatShowElement(elementToShow);
		/**
		 * 3.1) If no diagram is found representing that EObject, do nothing
		 */
		if (diagramInputsThatShowElement == null
				|| diagramInputsThatShowElement.size() <= 0) {
			return;
		}
		IEditorInput inputToOpen = null;
		/**
		 * 3.2) If one diagram is found representing that EObject, open that
		 * diagram
		 */
		if (diagramInputsThatShowElement.size() == 1) {
			inputToOpen = diagramInputsThatShowElement.get(0);
		}
		/**
		 * 3.3) If more than one diagram is found representing that EObject, ask
		 * the user to choose one of them and open that one
		 */
		if (diagramInputsThatShowElement.size() > 1) {
			Shell activeShell = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell();
			SelectDiagramInputDialog dialog = new SelectDiagramInputDialog(
					activeShell, diagramInputsThatShowElement);
			int result = dialog.open();
			if (result != 0) {
				// the user did not choose the open button
				return;
			}
			// get the selected editor input and open
			ISelection selection = dialog.getSelection();
			if (selection instanceof IStructuredSelection) {
				Object selected = ((IStructuredSelection) selection)
						.getFirstElement();
				if (selected instanceof IEditorInput) {
					inputToOpen = (IEditorInput) selected;
				}
			}
		}
		/**
		 * 4) open the selected diagram and select and show the element in the
		 * opened diagram
		 */
		IEditorPart openedEditor = openTargetEditor(inputToOpen);
		setSelectionInEditor(openedEditor);
	}

	/**
	 * Opens the editor that can show the given {@link IEditorInput}. </br> It
	 * will open one of the already open editors if it matches the given
	 * IEditorInput.
	 * 
	 * @param inputToOpen
	 * @return
	 */
	protected IEditorPart openTargetEditor(IEditorInput inputToOpen) {
		if (inputToOpen == null) {
			return null;
		}
		// try to open the given IEditorInput in one of the already open
		// editors.
		IEditorPart editorReused = MultiDiagramUtil
				.openEditorInputInOpenDiagram(inputToOpen);
		if (editorReused != null) {
			return editorReused;
		}
		// open the given IEditorInput in a new editor.
		try {
			IWorkbenchPage activePage = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage();
			return IDE.openEditor(activePage, inputToOpen,
					MOSKittMultiPageEditor.MOSKittMultiPageEditorID);
		} catch (PartInitException ex) {
			Activator.getDefault().logError(
					"A problem ocurred while opening a diagram", ex);
			return null;
		}
	}

	/**
	 * Gets a list of {@link Diagram}s that have at least one view of the given
	 * element.
	 * 
	 * @param element
	 * @return
	 */
	protected List<IEditorInput> getDiagramsThatShowElement(EObject element) {
		List<IEditorInput> diagramInputsThatShowElement = MultiDiagramUtil
				.getDiagramsWhereElementIsShown(getElementToShow());
		return diagramInputsThatShowElement;
	}

	/**
	 * Selects and shows in the given editor the edit part that represents the
	 * involved elemento to show.
	 * 
	 * @param openedEditor
	 */
	protected void setSelectionInEditor(IEditorPart openedEditor) {
		if (openedEditor == null) {
			return;
		}
		IStructuredSelection selection = new StructuredSelection(
				getElementInNewResourceSet(openedEditor));
		MDTUtil.setSelectionInEditor(openedEditor, selection);
	}

	protected EObject getElementInNewResourceSet(IEditorPart editorPart) {
		if (editorPart instanceof IDiagramWorkbenchPart) {
			EObject originalEObject = getElementToShow();
			IDiagramWorkbenchPart diagramWorkbenchPart = (IDiagramWorkbenchPart) editorPart;
			Diagram diagram = diagramWorkbenchPart.getDiagram();
			ResourceSet resourceSet = diagram.eResource().getResourceSet();
			Resource newEditorResource = resourceSet.getResource(
					originalEObject.eResource().getURI(), false);
			if (newEditorResource != null) {
				String elementFragment = originalEObject.eResource()
						.getURIFragment(originalEObject);
				return newEditorResource.getEObject(elementFragment);
			}
		}
		return null;
	}

	/**
	 * Gets the target element to show in the {@link Diagram} to open. The
	 * target element to show is provided by an {@link IElementToShowProvider}.
	 * 
	 * @return
	 */
	protected EObject getElementToShow() {
		if (getElementProvider() != null) {
			return getElementProvider().getElementToShow();
		}
		return null;
	}

}
