/***************************************************************************
 * Copyright (c) 2007, 2010 Conselleria de Infraestructuras y Transporte,
 * Generalitat de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Mario Cervera Ubeda (Integranova)
 * 				 Marc Gil Sendra (Prodevelop)
 * 				Francisco Javier Cano Muñoz (Prodevelop S.L.) - copy and paste with Views and Bounds
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.actions.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.command.CopyToClipboardCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.common.core.command.CompositeCommand;
import org.eclipse.gmf.runtime.common.core.command.ICommand;
import org.eclipse.gmf.runtime.common.ui.action.global.GlobalActionId;
import org.eclipse.gmf.runtime.common.ui.services.action.global.IGlobalActionContext;
import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.parts.IDiagramGraphicalViewer;
import org.eclipse.gmf.runtime.diagram.ui.parts.IDiagramWorkbenchPart;
import org.eclipse.gmf.runtime.diagram.ui.providers.DiagramGlobalActionHandler;
import org.eclipse.gmf.runtime.notation.Bounds;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.Edge;
import org.eclipse.gmf.runtime.notation.Node;
import org.eclipse.gmf.runtime.notation.Shape;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import es.cv.gvcase.mdt.common.commands.AbstractCommonTransactionalCommmand;
import es.cv.gvcase.mdt.common.commands.UpdateDiagramCommand;
import es.cv.gvcase.mdt.common.commands.wrappers.EMFtoGMFCommandWrapper;
import es.cv.gvcase.mdt.common.commands.wrappers.GEFtoEMFCommandWrapper;
import es.cv.gvcase.mdt.common.commands.wrappers.GEFtoGMFCommandWrapper;
import es.cv.gvcase.mdt.common.diagram.editparts.ListCompartmentEditPart;
import es.cv.gvcase.mdt.common.diagram.editparts.ShapeCompartmentEditPart;
import es.cv.gvcase.mdt.common.edit.policies.ViewAndFeatureResolver;
import es.cv.gvcase.mdt.common.edit.policies.ViewFeatureParentResolver;
import es.cv.gvcase.mdt.common.util.CopyUtils;
import es.cv.gvcase.mdt.common.util.DiagramEditPartsUtil;
import es.cv.gvcase.mdt.common.util.MDTUtil;
import es.cv.gvcase.mdt.common.util.MultiDiagramUtil;

/**
 * Adapted the copy handler to allow copy and paste of {@link View} elements and
 * their related diagrams. <br>
 * Now the default behavior of the copy&paste also copies the views of the
 * elements keeping their relative bounds and copying all the related
 * {@link Diagram}s to the elements bring copied. <br>
 * This copy/cut/paste handler makes heavy use of {@link ViewAndFeatureResolver}
 * and {@link ViewFeatureParentResolver} to find the container elements and the
 * container features for the elements being pasted.
 * 
 * 
 * @author mcervera
 * @author mgil
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 * @see CopyUtils
 * @see ViewAndFeatureResolver
 * @see ViewFeatureParentResolver
 */
public/* abstract */class ClipboardActionHandler extends
		DiagramGlobalActionHandler {

	/*
	 * Specifies whether the last executed action was a cut action or not. This
	 * is needed because the paste action will vary depending on it
	 */
	/** The is cut. */
	private static boolean isCut = false;

	/* Container of the objects in the clipboard */
	/** The container. */
	protected static EObject container = null;

	/* Objects in the clipboard */
	/** The clipboard. */
	protected static List<EObject> clipboard = new ArrayList<EObject>();

	/* Edit parts of the objects in the clipboard */
	/** The edit parts in clipboard. */
	protected static List<EditPart> editPartsInClipboard = new ArrayList<EditPart>();

	protected static List<Object> clipboardElements = new ArrayList<Object>();

	/** Map with the correspondence between the old and new eObject */
	protected Map<EObject, EObject> mapOld2New = new HashMap<EObject, EObject>();

	public void setContainer(EObject container) {
		this.container = container;
	}

	public EObject getContainer() {
		return this.container;
	}

	public boolean getIsCut() {
		return isCut;
	}

	public void setIsCut(boolean isCut) {
		this.isCut = isCut;
	}

	/**
	 * A copy operation can be performed when all the selected elements are of
	 * the same type and the element we are trying to copy is not the canvas
	 * element of a {@link Diagram}.
	 */
	@Override
	protected boolean canCopy(IGlobalActionContext cntxt) {

		if (cntxt.getSelection() instanceof StructuredSelection) {
			Object firstElement = ((StructuredSelection) cntxt.getSelection())
					.getFirstElement();
			if (firstElement instanceof IGraphicalEditPart) {
				// check that there is at least one element selected
				EObject eobject = ((IGraphicalEditPart) firstElement)
						.resolveSemanticElement();
				if (eobject != null) {
					List elements = ((StructuredSelection) cntxt.getSelection())
							.toList();
					List<EObject> eobjects = new ArrayList<EObject>();
					for (Object o : elements) {
						EObject eobj = ((IGraphicalEditPart) o)
								.resolveSemanticElement();
						eobjects.add(eobj);
					}
					IEditorPart editorPart = PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow().getActivePage()
							.getActiveEditor();
					// cehck that the selected element is not the diagram canvas
					if (editorPart instanceof IDiagramWorkbenchPart) {
						if (((IDiagramWorkbenchPart) editorPart).getDiagram()
								.getElement().equals(eobject)) {
							return false;
						}
					}
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * We can cut whenever we can copy.
	 */
	@Override
	protected boolean canCut(IGlobalActionContext cntxt) {
		return canCopy(cntxt);
	}

	/**
	 * We can paste if the target of the paste can hold the elements to paste.
	 */
	@Override
	protected boolean canPaste(IGlobalActionContext cntxt) {
		if (cntxt.getSelection() instanceof StructuredSelection) {
			Object firstElement = ((StructuredSelection) cntxt.getSelection())
					.getFirstElement();
			if (firstElement instanceof IGraphicalEditPart) {
				// fjcano :: find a proper container that can hold the item to
				// copy.
				IGraphicalEditPart pasteDestination = (IGraphicalEditPart) ((StructuredSelection) cntxt
						.getSelection()).getFirstElement();
				TransactionalEditingDomain domain = pasteDestination
						.getEditingDomain();
				if (pasteDestination != null
						&& pasteDestination.resolveSemanticElement() != null
						&& (!pasteDestination.resolveSemanticElement().equals(
								container) || !isCut)) {
					if (domain.getClipboard() != null
							&& domain.getClipboard().size() > 0) {
						Object[] objects = domain.getClipboard().toArray();
						EObject firstObjectToBePasted = (EObject) objects[0];
						if (firstObjectToBePasted instanceof View) {
							firstObjectToBePasted = ((View) firstObjectToBePasted)
									.getElement();
						}

						pasteDestination = getPasteDestinationFor(cntxt
								.getSelection(), firstObjectToBePasted);

						EStructuralFeature feature = getFeature(
								firstObjectToBePasted, pasteDestination);
						if (feature != null) {
							if (feature.getEType().getInstanceClass()
									.isInstance(firstObjectToBePasted)) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * Checks whether the clipboard content can be pasted in the given
	 * destination.
	 * 
	 * @param pasteDestination
	 * @return
	 */
	protected boolean canPasteInElement(IGraphicalEditPart pasteDestination,
			EObject toPaste) {
		if (pasteDestination != null
				&& pasteDestination.resolveSemanticElement() != null
				&& (!pasteDestination.resolveSemanticElement()
						.equals(container) || !isCut)) {
			TransactionalEditingDomain domain = pasteDestination
					.getEditingDomain();
			if (toPaste != null) {
				if (toPaste instanceof View) {
					toPaste = ((View) toPaste).getElement();
				}
				EStructuralFeature feature = getFeature(toPaste,
						pasteDestination);
				if (feature != null) {
					if (feature.getEType().getInstanceClass().isInstance(
							toPaste)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gmf.runtime.diagram.ui.providers.DiagramGlobalActionHandler
	 * #getCommand(org.eclipse.gmf.runtime.common.ui.services.action.global.
	 * IGlobalActionContext)
	 */
	@Override
	public ICommand getCommand(IGlobalActionContext cntxt) {

		IWorkbenchPart part = cntxt.getActivePart();
		if (!(part instanceof IDiagramWorkbenchPart)) {
			return null;
		}

		/* Get the model operation context */
		IDiagramWorkbenchPart diagramPart = (IDiagramWorkbenchPart) part;

		String actionId = cntxt.getActionId();

		if (actionId.equals(GlobalActionId.COPY)) {
			isCut = false;
			return getCopyCommand(cntxt, diagramPart, false);
		} else if (actionId.equals(GlobalActionId.CUT)) {
			isCut = true;
			return getCutCommand(cntxt, diagramPart);
		} else if (actionId.equals(GlobalActionId.PASTE)) {
			if (isCut) {
				isCut = false;
				return getExecutePasteAfterCutCommand(cntxt);
			} else {
				isCut = false;
				return getExecutePasteAfterCopyCommand(cntxt);
			}
		}

		return super.getCommand(cntxt);
	}

	// //
	//
	// //

	/**
	 * The copy command stores all the selected elements to be copied when a
	 * paste is executed.
	 * 
	 */
	@Override
	protected ICommand getCopyCommand(IGlobalActionContext cntxt,
			IDiagramWorkbenchPart diagramPart, boolean isUndoable) {

		if (cntxt.getSelection() instanceof StructuredSelection) {
			Object firstElement = ((StructuredSelection) cntxt.getSelection())
					.getFirstElement();
			EObject eobject = ((IGraphicalEditPart) firstElement)
					.resolveSemanticElement();
			TransactionalEditingDomain domain = TransactionUtil
					.getEditingDomain(eobject);
			List elements = ((StructuredSelection) cntxt.getSelection())
					.toList();
			final List<EObject> eobjects = new ArrayList<EObject>();
			List<IGraphicalEditPart> affectedEditParts = new ArrayList<IGraphicalEditPart>();
			editPartsInClipboard.clear();
			for (Object o : elements) {
				EObject eobj = ((IGraphicalEditPart) o).getNotationView();
				eobjects.add(eobj);
				container = eobj.eContainer();
				affectedEditParts.add((IGraphicalEditPart) o);
			}
			editPartsInClipboard.addAll(affectedEditParts);

			clipboard.clear();
			clipboard.addAll(eobjects);

			Command copyCommand = new CopyToClipboardCommand(domain, eobjects) {
				@Override
				public void doExecute() {
					// remove all duplicated elements
					CopyUtils.removeDuplicatedEObjects(clipboard);
					//
					ArrayList<Object> list = new ArrayList<Object>();
					for (EObject eobj : eobjects) {
						list.add(eobj);
					}
					oldClipboard = domain.getClipboard();
					domain.setClipboard(list);
				}
			};

			if (copyCommand != null) {
				return new EMFtoGMFCommandWrapper(copyCommand);
			}
		}
		return super.getCopyCommand(cntxt, diagramPart, isUndoable);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gmf.runtime.diagram.ui.providers.DiagramGlobalActionHandler
	 * #getCutCommand(org.eclipse.gmf.runtime.common.ui.services.action.global.
	 * IGlobalActionContext,
	 * org.eclipse.gmf.runtime.diagram.ui.parts.IDiagramWorkbenchPart)
	 */
	@Override
	protected ICommand getCutCommand(IGlobalActionContext cntxt,
			IDiagramWorkbenchPart diagramPart) {

		if (cntxt.getSelection() instanceof StructuredSelection) {
			Object firstElement = ((StructuredSelection) cntxt.getSelection())
					.getFirstElement();
			EObject eobject = ((IGraphicalEditPart) firstElement)
					.resolveSemanticElement();
			TransactionalEditingDomain domain = TransactionUtil
					.getEditingDomain(eobject);
			List<IGraphicalEditPart> selectedEditParts = MDTUtil
					.getGraphicalEditPartsFromSelection(cntxt.getSelection());
			final List<EObject> eobjects = new ArrayList<EObject>();
			List<IGraphicalEditPart> affectedEditParts = new ArrayList<IGraphicalEditPart>();
			editPartsInClipboard.clear();
			IGraphicalEditPart anEditPart = null;
			for (IGraphicalEditPart editPart : selectedEditParts) {
				EObject eobj = editPart.getNotationView();
				eobjects.add(eobj);
				container = eobj.eContainer();
				affectedEditParts.add(editPart);
				anEditPart = editPart;
			}
			editPartsInClipboard.addAll(affectedEditParts);

			clipboard.clear();
			clipboard.addAll(eobjects);

			final IGraphicalEditPart referenceEditPart = anEditPart;
			AbstractCommonTransactionalCommmand cutCommand = new AbstractCommonTransactionalCommmand(
					domain, "Cut objects", null) {

				List<EObject> eObjects = null;

				private Map<EObject, EStructuralFeature> mapEObject2ContainingFeature = null;

				private Map<EObject, EObject> mapEObject2Container = null;

				private Map<EObject, Point> mapEObject2Location = null;

				@Override
				protected CommandResult doExecuteWithResult(
						IProgressMonitor monitor, IAdaptable info)
						throws ExecutionException {
					//
					this.eObjects = eobjects;
					CopyUtils.removeDuplicatedEObjects(eObjects);
					//
					return statusToCommandResult(doRedo(monitor, info));
				}

				@Override
				protected IStatus doUndo(IProgressMonitor monitor,
						IAdaptable info) throws ExecutionException {
					// super.doUndo(monitor, info);
					// put the eobjects back in the container
					for (EObject eObject : eObjects) {
						putEObjectInParent(eObject);
					}
					//
					DiagramEditPartsUtil.updateDiagram(referenceEditPart);
					return OKStatus;
				}

				@Override
				protected IStatus doRedo(IProgressMonitor monitor,
						IAdaptable info) throws ExecutionException {
					ArrayList<Object> list = new ArrayList<Object>();
					for (EObject eobj : eobjects) {
						list.add(eobj);
						removeEObjectFromParent(eobj);
					}
					getEditingDomain().setClipboard(list);
					//
					DiagramEditPartsUtil.updateDiagram(referenceEditPart);
					return OKStatus;
				}

				protected void removeEObjectFromParent(EObject eObject) {
					if (eObject == null) {
						return;
					}
					if (mapEObject2Container == null) {
						mapEObject2Container = new HashMap<EObject, EObject>();
					}
					if (mapEObject2ContainingFeature == null) {
						mapEObject2ContainingFeature = new HashMap<EObject, EStructuralFeature>();
					}
					if (mapEObject2Location == null) {
						mapEObject2Location = new HashMap<EObject, Point>();
					}
					if (eObject instanceof View) {
						Point location = MDTUtil.getNodeLocation(eObject);
						if (location != null) {
							mapEObject2Location.put(eObject, location);
						}
						removeEObjectFromParent(((View) eObject).getElement());
						removeEdgesFromView(eObject);
					}
					EObject container = eObject.eContainer();
					EStructuralFeature feature = eObject.eContainingFeature();
					if (container != null) {
						mapEObject2Container.put(eObject, container);
						if (feature != null) {
							Object object = container.eGet(feature);
							if (object instanceof Collection) {
								((Collection) object).remove(eObject);
							} else {
								container.eSet(feature, null);
							}
							mapEObject2ContainingFeature.put(eObject, feature);
						}
					}
				}

				protected void removeEdgesFromView(Object object) {
					if (object instanceof View) {
						((View) object).getSourceEdges().clear();
						((View) object).getTargetEdges().clear();
					}
				}

				protected void putEObjectInParent(EObject eObject) {
					if (eObject instanceof View) {
						putEObjectInParent(((View) eObject).getElement());
					}
					if (eObject != null && mapEObject2Container != null
							&& mapEObject2ContainingFeature != null) {
						EObject container = mapEObject2Container.get(eObject);
						EStructuralFeature feature = mapEObject2ContainingFeature
								.get(eObject);
						if (eObject instanceof Node) {
							Point location = mapEObject2Location.get(eObject);
							((Bounds) ((Node) eObject).getLayoutConstraint())
									.setX(location.x);
							((Bounds) ((Node) eObject).getLayoutConstraint())
									.setY(location.y);
						}
						if (container != null && feature != null) {
							CopyUtils.putInContainer(eObject, feature,
									container);
						}
					}
				}

			};

			if (cutCommand != null) {
				return cutCommand;
			}
		}
		return super.getCutCommand(cntxt, diagramPart);
	}

	/**
	 * Do something with the copied EObject
	 * 
	 * @param eo
	 */
	protected void prepareEObject(EObject eObject) {
	}

	/**
	 * Helper method to prepare an EObject when it is going to be copied in the
	 * targetEditPart.
	 * 
	 * @param eObject
	 * @param targetEditPart
	 */
	protected void prepareEObject(EObject eObject,
			IGraphicalEditPart targetEditPart) {

	}

	/**
	 * Gets a proper paste destination.
	 * 
	 * @param selection
	 * @return
	 */
	protected IGraphicalEditPart getPasteDestination(ISelection selection) {
		if (selection == null || selection.isEmpty()) {
			return null;
		}
		if (selection instanceof StructuredSelection) {
			StructuredSelection ss = (StructuredSelection) selection;
			Object object = ((StructuredSelection) selection).getFirstElement();
			if (object instanceof IGraphicalEditPart) {
				IGraphicalEditPart iGraphicalEditPart = (IGraphicalEditPart) object;
				TransactionalEditingDomain domain = ((IGraphicalEditPart) object)
						.getEditingDomain();
				Collection<Object> objects = domain.getClipboard();
				if (objects == null || objects.size() <= 0) {
					return null;
				}
				if (canPasteInElement(iGraphicalEditPart, (EObject) objects
						.iterator().next())) {
					return iGraphicalEditPart;
				} else {
					return getParentSemanticEditPart(iGraphicalEditPart);
				}
			}
			return null;
		} else {
			return null;
		}
	}

	protected IGraphicalEditPart getPasteDestinationFor(ISelection selection,
			EObject toPaste) {
		if (selection == null || selection.isEmpty()) {
			return null;
		}
		if (selection instanceof StructuredSelection) {
			Object object = ((StructuredSelection) selection).getFirstElement();
			if (object instanceof IGraphicalEditPart) {
				IGraphicalEditPart iGraphicalEditPart = (IGraphicalEditPart) object;
				if (canPasteInElement(iGraphicalEditPart, toPaste)) {
					return iGraphicalEditPart;
				} else {
					IGraphicalEditPart pasteDestination = getChildCompartmentEditPartFor(
							iGraphicalEditPart, toPaste);
					if (pasteDestination != null) {
						return pasteDestination;
					}
					pasteDestination = getParentSemanticEditPart(iGraphicalEditPart);
					if (pasteDestination != null) {
						return pasteDestination;
					}
					return iGraphicalEditPart;
				}
			}
			return null;
		} else {
			return null;
		}
	}

	protected IGraphicalEditPart getChildCompartmentEditPartFor(
			IGraphicalEditPart parentEditPart, EObject toPaste) {
		if (parentEditPart != null && toPaste != null) {
			// look in children edit parts those that are containers and that
			// can handle the paste of the given element.
			IGraphicalEditPart editPart = null;
			for (Object child : parentEditPart.getChildren()) {
				if (child instanceof ShapeCompartmentEditPart
						|| child instanceof ListCompartmentEditPart) {
					editPart = (IGraphicalEditPart) child;
					if (canPasteInElement(editPart, toPaste)) {
						return editPart;
					}
				}
			}
		}
		return null;
	}

	protected IGraphicalEditPart getParentSemanticEditPart(
			IGraphicalEditPart editPart) {
		if (editPart == null) {
			return null;
		}
		IGraphicalEditPart parentEditPart = editPart.getParent() instanceof IGraphicalEditPart ? (IGraphicalEditPart) editPart
				.getParent()
				: null;
		TransactionalEditingDomain domain = (editPart).getEditingDomain();
		Collection<Object> objects = domain.getClipboard();
		if (objects == null || objects.size() <= 0) {
			return null;
		}
		while (parentEditPart != null) {
			if (canPasteInElement(parentEditPart, (EObject) objects.iterator()
					.next())) {
				return parentEditPart;
			}
			parentEditPart = parentEditPart.getParent() instanceof IGraphicalEditPart ? (IGraphicalEditPart) parentEditPart
					.getParent()
					: null;
		}
		return null;
	}

	/**
	 * Execute paste after copy.
	 * 
	 * @param cntxt
	 *            the cntxt
	 */
	protected ICommand getExecutePasteAfterCopyCommand(
			final IGlobalActionContext cntxt) {

		if (!(cntxt.getSelection() instanceof StructuredSelection)) {
			return null;
		}
		//

		if (clipboard == null || clipboard.size() <= 0) {
			return null;
		}

		// fjcano :: find a proper container that can hold the item to copy.
		final ISelection selectedInDiagram = cntxt.getSelection();
		Object firstElement = ((StructuredSelection) cntxt.getSelection())
				.getFirstElement();
		IGraphicalEditPart pasteDestination = null;
		IGraphicalEditPart selectedEditpart = null;
		EObject firstObjectToBePasted = null;
		TransactionalEditingDomain editingDomain = null;
		if (firstElement instanceof IGraphicalEditPart) {
			// fjcano :: find a proper container that can hold the item to
			// copy.
			selectedEditpart = (IGraphicalEditPart) ((StructuredSelection) cntxt
					.getSelection()).getFirstElement();
			editingDomain = selectedEditpart.getEditingDomain();
			Object[] objects = editingDomain.getClipboard().toArray();
			firstObjectToBePasted = (EObject) objects[0];
			if (firstObjectToBePasted instanceof View) {
				firstObjectToBePasted = ((View) firstObjectToBePasted)
						.getElement();
			}
		}
		pasteDestination = getPasteDestinationFor(selectedInDiagram,
				firstObjectToBePasted);
		final IGraphicalEditPart selectedEditPart = selectedEditpart;
		final IGraphicalEditPart editPart = pasteDestination;
		AbstractCommonTransactionalCommmand command = new AbstractCommonTransactionalCommmand(
				editingDomain, "Paste after copy", null) {

			private List<EObject> eObjects = null;

			@Override
			protected CommandResult doExecuteWithResult(
					IProgressMonitor monitor, IAdaptable info)
					throws ExecutionException {
				eObjects = findAllEObjectsToCopy(clipboard);
				// remove all duplicated elements
				CopyUtils.removeDuplicatedEObjects(eObjects);
				//
				Collection<EObject> copiedElements = null;
				IGraphicalEditPart targetEditPart = null;
				if (eObjects != null && eObjects.size() > 0) {
					copiedElements = CopyUtils.copyEObjects(eObjects, true,
							mapOld2New);

					Point firstPosition = MDTUtil
							.getMouseLocation(selectedEditPart);
					IFigure figure = editPart.getContentPane();
					Point where = firstPosition.getCopy();
					figure.translateFromParent(where);
					where.translate(figure.getClientArea().getLocation());

					Point lastPoint = null;
					for (EObject copied : copiedElements) {
						// find target destination
						targetEditPart = getPasteDestinationFor(
								selectedInDiagram, copied);
						// prepare EObject after copy
						prepareEObject(copied);
						prepareEObject(copied, targetEditPart);

						if (lastPoint != null) {
							Bounds currentBounds = MDTUtil
									.getNodeBounds(copied);
							where.x -= lastPoint.x - currentBounds.getX();
							where.y -= lastPoint.y - currentBounds.getY();
						}

						lastPoint = MDTUtil.getNodeLocation(copied);

						CopyUtils.storeEObjectInEditPart(copied,
								targetEditPart, where);
					}
				}
				// DiagramEditPartsUtil.updateEditPart(editPart);
				DiagramEditPartsUtil.updateDiagram(editPart);
				// set diagram selection to the new copied elements
				// setSelectionTo(copiedElements, targetEditPart);
				return CommandResult.newOKCommandResult();
			}
		};
		//
		if (command != null && command.canExecute()) {
			CompositeCommand cc = new CompositeCommand("Paste after copy");
			UpdateDiagramCommand updateCommand = new UpdateDiagramCommand(
					selectedEditPart);
			//
			// cc.add(new GEFtoGMFCommandWrapper(updateCommand));
			cc.add(command);
			cc.add(new GEFtoGMFCommandWrapper(updateCommand));
			return cc;
		}
		// 
		return command.canExecute() ? command : null;
	}

	protected List<EObject> findAllEObjectsToCopy(List<EObject> selected) {
		List<EObject> allEObjects = new ArrayList<EObject>();
		allEObjects.addAll(selected);
		// find all nodes in the selected elements
		List<View> nodeViews = new ArrayList<View>();
		for (EObject eObject : selected) {
			if (eObject instanceof Node || eObject instanceof Shape) {
				nodeViews.add((View) eObject);
			}
		}
		// find all edges related to the selected elements
		List<Edge> edgeViews = new ArrayList<Edge>();
		for (View view : nodeViews) {
			edgeViews.addAll(view.getSourceEdges());
			edgeViews.addAll(view.getTargetEdges());
		}
		// find edges related that have source and target in the selected nodes
		for (Edge edge : edgeViews) {
			View source = edge.getSource();
			View target = edge.getTarget();
			if (allEObjects.contains(source) && allEObjects.contains(target)
					&& !allEObjects.contains(edge)) {
				allEObjects.add(edge);
			}
		}
		return allEObjects;
	}

	protected Point getLocationForPaste(EObject eObject,
			IGraphicalEditPart selectedEditPart) {
		return getLocationForPaste(eObject, selectedEditPart, null);
	}

	protected Point getLocationForPaste(EObject eObject,
			IGraphicalEditPart selectedEditPart, Point offset) {
		//
		Point location = new Point(10, 10);
		//
		Point mouseLocation = MDTUtil.getMouseLocation(selectedEditPart);
		if (selectedEditPart instanceof DiagramEditPart) {
			if (mouseLocation.x > 0 && mouseLocation.y > 0) {
				location.x = mouseLocation.x;
				location.y = mouseLocation.y;
			}
		} else {
			Bounds bounds = MDTUtil.getNodeBounds(selectedEditPart);
			location = MDTUtil.getAdaptedNodePositionToBounds(eObject, bounds);
			if (location != null) {
				location.x += 10;
				location.y += 10;
			}
		}
		if (selectedEditPart instanceof DiagramEditPart) {
			if (offset != null) {
				location.x += offset.x;
				location.y += offset.y;
			}
		}
		return location;
	}

	/**
	 * Execute paste after cut.
	 * 
	 * @param cntxt
	 *            the cntxt
	 */
	protected ICommand getExecutePasteAfterCutCommand(
			final IGlobalActionContext cntxt) {
		//
		if (!(cntxt.getSelection() instanceof StructuredSelection)) {
			return null;
		}
		//
		if (clipboard == null || clipboard.size() <= 0) {
			return null;
		}
		// fjcano :: find a proper container that can hold the item to copy.
		final ISelection selectedInDiagram = cntxt.getSelection();
		Object firstElement = ((StructuredSelection) cntxt.getSelection())
				.getFirstElement();
		IGraphicalEditPart pasteDestination = null;
		IGraphicalEditPart selectedEditpart = null;
		EObject firstObjectToBePasted = null;
		TransactionalEditingDomain editingDomain = null;
		if (firstElement instanceof IGraphicalEditPart) {
			// fjcano :: find a proper container that can hold the item to
			// copy.
			selectedEditpart = (IGraphicalEditPart) ((StructuredSelection) cntxt
					.getSelection()).getFirstElement();
			editingDomain = selectedEditpart.getEditingDomain();
			Object[] objects = editingDomain.getClipboard().toArray();
			firstObjectToBePasted = (EObject) objects[0];
			if (firstObjectToBePasted instanceof View) {
				firstObjectToBePasted = ((View) firstObjectToBePasted)
						.getElement();
			}
		}
		pasteDestination = getPasteDestinationFor(selectedInDiagram,
				firstObjectToBePasted);
		final IGraphicalEditPart selectedEditPart = selectedEditpart;
		final IGraphicalEditPart editPart = pasteDestination;
		AbstractCommonTransactionalCommmand command = new AbstractCommonTransactionalCommmand(
				editingDomain, "Paste after cut", null) {

			private List<EObject> eObjects = null;

			private Map<EObject, EStructuralFeature> mapEObject2ContainingFeature = null;

			private Map<EObject, EObject> mapEObject2Container = null;

			@Override
			protected CommandResult doExecuteWithResult(
					IProgressMonitor monitor, IAdaptable info)
					throws ExecutionException {
				eObjects = findAllEObjectsToCopy(clipboard);
				// remove all duplicated elements
				CopyUtils.removeDuplicatedEObjects(eObjects);
				//
				if (eObjects != null && eObjects.size() > 0) {
					Point firstPosition = MDTUtil
							.getMouseLocation(selectedEditPart);
					IFigure figure = editPart.getContentPane();
					Point where = firstPosition.getCopy();
					figure.translateFromParent(where);
					where.translate(figure.getClientArea().getLocation());

					Point lastPoint = null;
					for (EObject eObject : eObjects) {
						if (lastPoint != null) {
							Bounds currentBounds = MDTUtil
									.getNodeBounds(eObject);
							where.x -= lastPoint.x - currentBounds.getX();
							where.y -= lastPoint.y - currentBounds.getY();
						}

						lastPoint = MDTUtil.getNodeLocation(eObject);

						CopyUtils.storeEObjectInEditPart(eObject, editPart,
								where);
					}
				}
				//
				DiagramEditPartsUtil.updateDiagram(selectedEditPart);
				//
				return statusToCommandResult(doRedo(monitor, info));
			}

			@Override
			protected IStatus doRedo(IProgressMonitor monitor, IAdaptable info)
					throws ExecutionException {
				// paste eObjects in the selected container
				for (EObject eObject : eObjects) {
					putEObjectInParent(eObject);
				}
				// add the eObjects references to the diagram
				addEObjectsToDiagram(eObjects, DiagramEditPartsUtil
						.getDiagramEditPart(selectedEditPart).getDiagramView());
				// refresh the diagram
				DiagramEditPartsUtil.updateDiagram(selectedEditPart);
				return OKStatus;
			}

			@Override
			protected IStatus doUndo(IProgressMonitor monitor, IAdaptable info)
					throws ExecutionException {
				// super.doUndo(monitor, info);
				for (EObject eObject : eObjects) {
					removeEObjectFromParent(eObject);
				}
				DiagramEditPartsUtil.updateDiagram(selectedEditPart);
				return OKStatus;
			}

			// //

			protected void addEObjectsToDiagram(Collection<EObject> eObjects,
					Diagram diagram) {
				if (eObjects != null && !eObjects.isEmpty()) {
					for (EObject eObject : eObjects) {
						if (eObject instanceof View) {
							eObject = ((View) eObject).getElement();
							if (eObject != null) {
								MultiDiagramUtil
										.AddEAnnotationReferenceToDiagram(
												diagram, eObject);
							}
						}
					}
				}
			}

			protected void removeEObjectFromParent(EObject eObject) {
				if (eObject == null) {
					return;
				}
				if (mapEObject2Container == null) {
					mapEObject2Container = new HashMap<EObject, EObject>();
				}
				if (mapEObject2ContainingFeature == null) {
					mapEObject2ContainingFeature = new HashMap<EObject, EStructuralFeature>();
				}
				if (eObject instanceof View) {
					removeEObjectFromParent(((View) eObject).getElement());
					removeEdgesFromView(eObject);
				}
				EObject container = eObject.eContainer();
				EStructuralFeature feature = eObject.eContainingFeature();
				if (container != null) {
					mapEObject2Container.put(eObject, container);
					if (feature != null) {
						Object object = container.eGet(feature);
						if (object instanceof Collection) {
							((Collection) object).remove(eObject);
						} else {
							container.eSet(feature, null);
						}
						mapEObject2ContainingFeature.put(eObject, feature);
					}
				}
			}

			protected void removeEdgesFromView(Object object) {
				if (object instanceof View) {
					((View) object).getSourceEdges().clear();
					((View) object).getTargetEdges().clear();
				}
			}

			protected void putEObjectInParent(EObject eObject) {
				if (eObject instanceof View) {
					putEObjectInParent(((View) eObject).getElement());
				}
				if (eObject != null && mapEObject2Container != null
						&& mapEObject2ContainingFeature != null) {
					EObject container = mapEObject2Container.get(eObject);
					EStructuralFeature feature = mapEObject2ContainingFeature
							.get(eObject);
					if (container != null && feature != null) {
						CopyUtils.putInContainer(eObject, feature, container);
					}
				}
			}
		};
		//
		if (command != null && command.canExecute()) {
			CompositeCommand cc = new CompositeCommand("Paste after cut");
			UpdateDiagramCommand updateCommand = new UpdateDiagramCommand(
					selectedEditPart);
			//
			// cc.add(new GEFtoGMFCommandWrapper(updateCommand));
			cc.add(command);
			cc.add(new GEFtoGMFCommandWrapper(updateCommand));
			return cc;
		}
		// 
		return command.canExecute() ? command : null;
	}

	/**
	 * Gets the delete view command.
	 * 
	 * @param editPart
	 *            the edit part
	 * 
	 * @return the delete view command
	 */
	protected Command getDeleteViewCommand(EditPart editPart) {

		if (editPart == null)
			return null;

		Request deleteViewRequest = new GroupRequest(
				RequestConstants.REQ_DELETE);
		org.eclipse.gef.commands.Command command = editPart
				.getCommand(deleteViewRequest);
		return new GEFtoEMFCommandWrapper(command);
	}

	/**
	 * Gets the edits the parts in clipboard.
	 * 
	 * @param clipboard
	 *            the clipboard
	 * 
	 * @return the edits the parts in clipboard
	 */
	protected Collection<EditPart> getEditPartsInClipboard(
			Collection<Object> clipboard) {
		if (clipboard != null && clipboard.size() > 0) {
			Collection<EditPart> editParts = new ArrayList<EditPart>();
			for (Object object : clipboard) {
				if (object instanceof EditPart) {
					editParts.add((EditPart) object);
				}
			}
			return editParts;
		}
		return Collections.emptyList();
	}

	/**
	 * Gets the feature.
	 * 
	 * @param objectToBePasted
	 *            the object to be pasted
	 * @param pasteDestination
	 *            the paste destination
	 * 
	 * @return the feature
	 */
	public EStructuralFeature getFeature(EObject objectToBePasted,
			EditPart pasteDestination) {
		if (objectToBePasted == null || pasteDestination == null) {
			return null;
		}
		EObject element = MDTUtil.resolveSemantic(pasteDestination);
		return getFeature(element, objectToBePasted, pasteDestination);
	}

	/**
	 * Gets the feature.
	 * 
	 * @param element
	 *            the element
	 * @param objectToBePasted
	 *            the object to be pasted
	 * @param editPart
	 *            the edit part
	 * 
	 * @return the feature
	 */
	protected EStructuralFeature getFeature(EObject element,
			EObject objectToBePasted, EditPart editPart) {

		if (objectToBePasted instanceof View) {
			objectToBePasted = ((View) objectToBePasted).getElement();
		}

		if (!(element == MDTUtil.resolveSemantic(editPart))) {
			return null;
		}

		EStructuralFeature feature = null;

		Object adapter = editPart.getAdapter(ViewAndFeatureResolver.class);
		ViewAndFeatureResolver resolver = null;
		if (adapter instanceof ViewAndFeatureResolver) {
			resolver = (ViewAndFeatureResolver) adapter;
		}
		if (resolver != null) {
			feature = resolver.getEStructuralFeatureForEClass(objectToBePasted
					.eClass());
		}
		return feature;
	}

	// //
	// 
	// //

	protected void setSelectionTo(Collection<EObject> eObjects,
			IGraphicalEditPart editPart) {
		eObjects = fromViewToEObject(eObjects);
		Collection<IGraphicalEditPart> editParts = getEditPartsFor(eObjects,
				editPart);
		if (editParts != null && !editParts.isEmpty()) {
			IDiagramGraphicalViewer viewer = ((IDiagramWorkbenchPart) MDTUtil
					.getActiveEditor()).getDiagramGraphicalViewer();
			if (viewer == null) {
				return;
			}
			StructuredSelection selectedEditParts = new StructuredSelection(
					editParts);
			viewer.setSelection(selectedEditParts);
			if (!selectedEditParts.isEmpty()) {
				EditPart firstEditPart = (EditPart) selectedEditParts
						.getFirstElement();
				viewer.reveal(firstEditPart);
			}
		}
	}

	protected Collection<EObject> fromViewToEObject(Collection<EObject> eObjects) {
		if (eObjects == null || eObjects.isEmpty()) {
			return Collections.emptyList();
		}
		List<EObject> realEObjects = new ArrayList<EObject>();
		for (EObject eObject : eObjects) {
			if (eObject instanceof View) {
				eObject = ((View) eObject).getElement();
			}
			realEObjects.add(eObject);
		}
		return realEObjects;
	}

	protected Collection<IGraphicalEditPart> getEditPartsFor(
			Collection<EObject> eObjects, IGraphicalEditPart editPart) {
		if (eObjects == null || eObjects.size() <= 0 || editPart == null) {
			return Collections.emptyList();
		}
		IGraphicalEditPart diagramEditPart = DiagramEditPartsUtil
				.getDiagramEditPart(editPart);
		if (diagramEditPart == null) {
			return Collections.emptyList();
		}
		List<IGraphicalEditPart> foundEditParts = new ArrayList<IGraphicalEditPart>();
		fillWithEditPartsFor(eObjects, diagramEditPart, foundEditParts);
		return foundEditParts;
	}

	protected void fillWithEditPartsFor(Collection<EObject> eObjects,
			IGraphicalEditPart editPart,
			Collection<IGraphicalEditPart> foundEditParts) {
		IGraphicalEditPart graphicalEditPart = null;
		EObject eObject = null;
		// search possible candidates in this edit part children
		for (Object child : editPart.getChildren()) {
			if (child instanceof IGraphicalEditPart) {
				graphicalEditPart = (IGraphicalEditPart) child;
				eObject = graphicalEditPart.resolveSemanticElement();
				if (eObjects.contains(eObject)) {
					foundEditParts.add(graphicalEditPart);
				}
			}
		}
		// search in children's children
		for (Object child : editPart.getChildren()) {
			if (child instanceof IGraphicalEditPart) {
				graphicalEditPart = (IGraphicalEditPart) child;
				fillWithEditPartsFor(eObjects, graphicalEditPart,
						foundEditParts);
			}
		}
	}

	protected IGraphicalEditPart getFirstXYEditPart(Collection<Object> editParts) {
		IGraphicalEditPart editPart = null;
		List<IGraphicalEditPart> editPartsList = new ArrayList<IGraphicalEditPart>();
		for (Object o : editParts) {
			if (o instanceof IGraphicalEditPart) {
				editPart = ((IGraphicalEditPart) o);
				editPartsList.add(editPart);
			}
		}
		IGraphicalEditPart[] editPartsArray = new IGraphicalEditPart[editParts
				.size()];
		editPartsList.toArray(editPartsArray);
		Arrays.sort(editPartsArray, new IGraphicalEditPartXYComparator());
		return editPartsArray[0];
	}

	protected class IGraphicalEditPartXComparator implements
			Comparator<IGraphicalEditPart> {
		public int compare(IGraphicalEditPart o1, IGraphicalEditPart o2) {
			if (o1 == null) {
				if (o2 == null) {
					return 0;
				} else {
					return -1;
				}
			}
			if (o2 == null) {
				if (o1 == null) {
					return 0;
				} else {
					return 1;
				}
			}
			Point location1 = MDTUtil.getNodeLocation(o1);
			Point location2 = MDTUtil.getNodeLocation(o2);
			if (location1 == null) {
				if (location2 == null) {
					return 0;
				} else {
					return -1;
				}
			}
			if (location2 == null) {
				if (location1 == null) {
					return 0;
				} else {
					return 1;
				}
			}
			if (location1.x < location2.x) {
				return 1;
			}
			return -1;
		}
	}

	protected class IGraphicalEditPartXYComparator implements
			Comparator<IGraphicalEditPart> {
		public int compare(IGraphicalEditPart o1, IGraphicalEditPart o2) {
			if (o1 == null) {
				if (o2 == null) {
					return 0;
				} else {
					return -1;
				}
			}
			if (o2 == null) {
				if (o1 == null) {
					return 0;
				} else {
					return 1;
				}
			}
			Point location1 = MDTUtil.getNodeLocation(o1);
			Point location2 = MDTUtil.getNodeLocation(o2);
			if (location1 == null) {
				if (location2 == null) {
					return 0;
				} else {
					return -1;
				}
			}
			if (location2 == null) {
				if (location1 == null) {
					return 0;
				} else {
					return 1;
				}
			}
			if (location1.x < location2.x) {
				return 1;
			}
			if (location1.x > location2.x) {
				return -1;
			}
			if (location1.y < location2.y) {
				return 1;
			}
			if (location1.y > location2.y) {
				return -1;
			}
			return 0;
		}
	}

	// //
	//
	// //

	protected View getFirstXYNode(Collection<Object> views) {
		Node node = null;
		List<View> nodeList = new ArrayList<View>();
		for (Object o : views) {
			if (o instanceof Node || o instanceof Shape) {
				node = ((Node) o);
				nodeList.add(node);
			}
		}
		if (nodeList.size() == 0) {
			return null;
		}
		View[] nodesArray = new View[nodeList.size()];
		nodeList.toArray(nodesArray);
		Arrays.sort(nodesArray, new NodeXYComparator());
		return nodesArray[0];
	}

	protected class NodeXComparator implements Comparator<Node> {
		public int compare(Node o1, Node o2) {
			if (o1 == null) {
				if (o2 == null) {
					return 0;
				} else {
					return -1;
				}
			}
			if (o2 == null) {
				if (o1 == null) {
					return 0;
				} else {
					return 1;
				}
			}
			Point location1 = MDTUtil.getNodeLocation(o1);
			Point location2 = MDTUtil.getNodeLocation(o2);
			if (location1 == null) {
				if (location2 == null) {
					return 0;
				} else {
					return -1;
				}
			}
			if (location2 == null) {
				if (location1 == null) {
					return 0;
				} else {
					return 1;
				}
			}
			if (location1.x > location2.x) {
				return 1;
			}
			return -1;
		}
	}

	protected class NodeXYComparator implements Comparator<View> {
		public int compare(View o1, View o2) {
			if (o1 == null) {
				if (o2 == null) {
					return 0;
				} else {
					return -1;
				}
			}
			if (o2 == null) {
				if (o1 == null) {
					return 0;
				} else {
					return 1;
				}
			}
			Point location1 = MDTUtil.getNodeLocation(o1);
			Point location2 = MDTUtil.getNodeLocation(o2);
			if (location1 == null) {
				if (location2 == null) {
					return 0;
				} else {
					return -1;
				}
			}
			if (location2 == null) {
				if (location1 == null) {
					return 0;
				} else {
					return 1;
				}
			}
			if (location1.x > location2.x) {
				return 1;
			}
			if (location1.x < location2.x) {
				return -1;
			}
			if (location1.y > location2.y) {
				return 1;
			}
			if (location1.y < location2.y) {
				return -1;
			}
			return 0;
		}
	}
}
