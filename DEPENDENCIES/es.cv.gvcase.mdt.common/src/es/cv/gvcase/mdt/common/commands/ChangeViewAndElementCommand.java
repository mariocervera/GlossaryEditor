/*******************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Marc Gil Sendra (Prodevelop) - Initial implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gmf.runtime.common.core.command.AbstractCommand;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.common.core.command.CompositeCommand;
import org.eclipse.gmf.runtime.diagram.core.commands.DeleteCommand;
import org.eclipse.gmf.runtime.diagram.core.preferences.PreferencesHint;
import org.eclipse.gmf.runtime.diagram.ui.editparts.GraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateViewRequest;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateViewRequest.ViewDescriptor;
import org.eclipse.gmf.runtime.emf.commands.core.command.AbstractTransactionalCommand;
import org.eclipse.gmf.runtime.emf.core.util.EObjectAdapter;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;
import org.eclipse.gmf.runtime.emf.type.core.commands.CreateElementCommand;
import org.eclipse.gmf.runtime.emf.type.core.commands.DestroyElementCommand;
import org.eclipse.gmf.runtime.emf.type.core.commands.SetValueCommand;
import org.eclipse.gmf.runtime.emf.type.core.requests.CreateElementRequest;
import org.eclipse.gmf.runtime.emf.type.core.requests.DestroyElementRequest;
import org.eclipse.gmf.runtime.emf.type.core.requests.SetRequest;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.Node;
import org.eclipse.gmf.runtime.notation.NotationPackage;
import org.eclipse.gmf.runtime.notation.View;

import es.cv.gvcase.mdt.common.commands.wrappers.GEFtoEMFCommandWrapper;
import es.cv.gvcase.mdt.common.commands.wrappers.GMFtoEMFCommandWrapper;
import es.cv.gvcase.mdt.common.util.DiagramEditPartsUtil;
import es.cv.gvcase.mdt.common.util.MultiDiagramUtil;

/**
 * Complex <Command> that takes an existing <EObject and View> and turns it into
 * another <EObject and View>.
 * 
 * @author mgil
 */
public abstract class ChangeViewAndElementCommand extends
		AbstractCommonTransactionalCommmand implements IAdaptable {

	/** The new e object. */
	private EObject newEObject = null;

	/** The new e object. */
	private View newView = null;

	/** The new e container. */
	private EObject container = null;

	/** The old e object. */
	private EObject oldEObject = null;

	/** The old view. */
	private EditPart containerEditPart = null;

	/** The new i element type. */
	private IElementType newIElementType = null;

	/** The new visual id. */
	private int newVisualID = -1;

	/** Copy the diagram? */
	private boolean copyDiagram = false;

	/** The cached create element request. */
	protected CreateElementRequest cachedCreateElementRequest = null;

	protected CreateViewRequest cachedCreateViewRequest = null;

	/** Should delete the old element */
	private boolean shouldDelete = true;

	/** Should create the new view */
	private boolean shouldCreateNewView = true;

	/**
	 * Instantiates a new change view and element command.
	 * 
	 * @param oldEditPart
	 *            the old edit part
	 * @param newIElementType
	 *            the new i element type
	 * @param newVisualID
	 *            the new visual id
	 */
	public ChangeViewAndElementCommand(EditPart containerEditPart,
			EObject eObject, IElementType newIElementType, int newVisualID,
			boolean copyDiagram) {
		super(
				TransactionUtil.getEditingDomain(eObject) != null ? TransactionUtil
						.getEditingDomain(eObject)
						: TransactionUtil
								.getEditingDomain(((View) containerEditPart
										.getModel()).getElement()),
				"Mutate an old EObject and View to a new EObject and View",
				Collections.EMPTY_LIST);

		this.newIElementType = newIElementType;
		this.newVisualID = newVisualID;
		this.containerEditPart = containerEditPart;
		this.oldEObject = eObject;
		this.copyDiagram = copyDiagram;
	}

	protected void setCopyDiagram(boolean copy) {
		this.copyDiagram = copy;
	}

	public void setContainer(EObject container) {
		this.container = container;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.gmf.runtime.emf.commands.core.command.
	 * AbstractTransactionalCommand
	 * #doExecuteWithResult(org.eclipse.core.runtime.IProgressMonitor,
	 * org.eclipse.core.runtime.IAdaptable)
	 */
	@Override
	protected CommandResult doExecuteWithResult(IProgressMonitor monitor,
			IAdaptable info) throws ExecutionException {
		// create new EObject
		AbstractTransactionalCommand createNewEObjectCommand = createCreateNewEObjectCommand();
		if (createNewEObjectCommand == null) {
			return CommandResult
					.newErrorCommandResult("Error when creating new EObject");
		}
		getEditingDomain().getCommandStack().execute(
				new GMFtoEMFCommandWrapper(createNewEObjectCommand));

		// extract the newly created EObject
		getNewlyCreatedEObject();

		// change the diagram element if it has one
		if (copyDiagram) {
			AbstractCommand changeDiagramElement = createChangeDiagramElement();
			if (changeDiagramElement != null) {
				changeDiagramElement.execute(monitor, info);
			}
		}

		// "clone" NewEObject
		AbstractCommand cloneNewEObjectCommand = createCloneNewEObjectCommand(getNewEObject());
		if (cloneNewEObjectCommand == null) {
			return CommandResult
					.newErrorCommandResult("Error when cloning new EObject");
		}
		getEditingDomain().getCommandStack().execute(
				new GMFtoEMFCommandWrapper(cloneNewEObjectCommand));

		if (shouldCreateNewView) {
			// create the new View
			Command createViewCommand = getCreateViewCommand(newEObject);
			if (createViewCommand == null) {
				return CommandResult
						.newErrorCommandResult("Error when creating new EObject View");
			}
			getEditingDomain().getCommandStack().execute(
					new GEFtoEMFCommandWrapper(createViewCommand));

			// extract the newly created View
			getNewlyCreatedView();

			// add the EObject to the Diagram's "to show" references
			AbstractTransactionalCommand addReferenceToDiagramCommand = createAddNewEObjectToDiagramReferencesCommand();
			if (addReferenceToDiagramCommand == null) {
				return CommandResult
						.newErrorCommandResult("Error when adding new EObject reference");
			}
			getEditingDomain().getCommandStack().execute(
					new GMFtoEMFCommandWrapper(addReferenceToDiagramCommand));
		}
		if (shouldDelete) {
			// if the oldEObject has parent, execute a delete command for all
			// the elements (eobject, view)
			if (oldEObject.eContainer() != null) {
				es.cv.gvcase.mdt.common.commands.DeleteCommand dc = new es.cv.gvcase.mdt.common.commands.DeleteCommand(
						getEditingDomain(), "Remove old Element",
						Collections.EMPTY_LIST, Collections
								.singletonList((Object) oldEObject));
				dc.setDeleteWithConfirmation(false);
				getEditingDomain().getCommandStack().execute(dc.toEMFCommand());
			}
			// if not, only delete the view from the diagram
			else {
				for (Object o : DiagramEditPartsUtil
						.getEObjectViews(oldEObject)) {
					if (o instanceof View) {
						View v = (View) o;
						DeleteCommand delete = new DeleteCommand(v);
						getEditingDomain().getCommandStack().execute(
								new GMFtoEMFCommandWrapper(delete));
					}
				}
			}
		}

		return CommandResult.newOKCommandResult();
	}

	// // Create requests methods

	/**
	 * Creates the create new e object request.
	 * 
	 * @return the creates the element request
	 */
	protected CreateElementRequest createCreateNewEObjectRequest() {
		EObject container = getContainer();
		IElementType type = newIElementType;
		EReference feature = getContainingFeature();
		if (container == null || type == null || feature == null) {
			return null;
		}
		return new CreateElementRequest(container, type, feature);
	}

	/**
	 * Creates the destroy old EObject request.
	 * 
	 * @return the destroy element request
	 */
	protected DestroyElementRequest createDestroyOldEObjectRequest() {
		EObject oldEObject = getOldEObject();
		return oldEObject != null ? new DestroyElementRequest(oldEObject, false)
				: null;
	}

	/**
	 * Gets the creates the view request.
	 * 
	 * @param oldEditPart
	 *            the old edit part
	 * @param newEObject
	 *            the new e object
	 * 
	 * @return the creates the view request
	 */
	private CreateViewRequest getCreateViewRequest(EObject newEObject) {

		String semanticHint = String.valueOf(newVisualID);
		ViewDescriptor viewDescriptor = new ViewDescriptor(new EObjectAdapter(
				newEObject), Node.class, semanticHint,
				PreferencesHint.USE_DEFAULTS);

		CreateViewRequest createViewRequest = new CreateViewRequest(
				viewDescriptor);
		createViewRequest.setLocation(getNewLocation());
		return createViewRequest;
	}

	// // Create commands methods

	/**
	 * Creates the clone new e object command.
	 * 
	 * @param newEObject
	 *            the new e object
	 * 
	 * @return the abstract command
	 */
	protected abstract AbstractCommand createCloneNewEObjectCommand(
			EObject newEObject);

	/**
	 * Creates the set value command from feature.
	 * 
	 * @param elementToEdit
	 *            the element to edit
	 * @param feature
	 *            the feature
	 * @param elementToCopy
	 *            the element to copy
	 * 
	 * @return the sets the value command
	 */
	public static SetValueCommand createSetValueCommandFromFeature(
			EObject elementToEdit, EStructuralFeature feature,
			EObject elementToCopy) {
		Object value = elementToCopy != null && feature != null ? elementToCopy
				.eGet(feature, true) : null;
		if (elementToEdit == null || feature == null || elementToCopy == null) {
			return null;
		}
		SetRequest request = new SetRequest(elementToEdit, feature, value);
		return new SetValueCommand(request);
	}

	/**
	 * Creates the destroy element command.
	 * 
	 * @param eObject
	 *            the e object
	 * 
	 * @return the destroy element command
	 */
	public static DestroyElementCommand createDestroyElementCommand(
			EObject eObject) {
		DestroyElementRequest destroyRequest = new DestroyElementRequest(
				eObject, false);
		return new DestroyElementCommand(destroyRequest);
	}

	/**
	 * Creates the create new e object command.
	 * 
	 * @return the abstract transactional command
	 */
	protected AbstractTransactionalCommand createCreateNewEObjectCommand() {
		CreateElementRequest createRequest = createCreateNewEObjectRequest();
		cachedCreateElementRequest = createRequest;
		return createRequest != null ? new CreateElementCommand(createRequest)
				: null;
	}

	/**
	 * Creates the change diagram element.
	 * 
	 * @return the abstract command
	 */
	protected AbstractCommand createChangeDiagramElement() {
		CompositeCommand cc = new CompositeCommand("Change Diagram Element");

		List<Diagram> diagrams = MultiDiagramUtil
				.getDiagramsAssociatedToElement(getOldEObject());
		if (diagrams.isEmpty()) {
			return null;
		}

		SetRequest request = new SetRequest(diagrams.get(0),
				NotationPackage.eINSTANCE.getView_Element(), getNewEObject());

		cc.add(new SetValueCommand(request));

		return cc.canExecute() ? cc : null;
	}

	/**
	 * Creates the delete old e object command.
	 * 
	 * @return the abstract transactional command
	 */
	protected AbstractTransactionalCommand createDeleteOldEObjectCommand() {
		DestroyElementRequest destroyRequest = createDestroyOldEObjectRequest();
		return destroyRequest != null ? new DestroyElementCommand(
				destroyRequest) : null;
	}

	/**
	 * Creates the add new e object to diagram references command.
	 * 
	 * @return the abstract transactional command
	 */
	protected AbstractTransactionalCommand createAddNewEObjectToDiagramReferencesCommand() {
		TransactionalEditingDomain domain = getEditingDomain();
		Diagram diagram = getDiagram() instanceof Diagram ? (Diagram) getDiagram()
				: null;
		EObject newEObject = getNewEObject();
		if (domain == null || newEObject == null) {
			return null;
		}
		List<EObject> list = new ArrayList<EObject>();
		list.add(newEObject);
		return new AddEObjectReferencesToDiagram(domain, diagram, list);
	}

	/**
	 * Gets the delete view command.
	 * 
	 * @param oldEditPart
	 *            the old edit part
	 * 
	 * @return the delete view command
	 */
	private AbstractTransactionalCommand getDeleteViewCommand(
			GraphicalEditPart oldEditPart) {
		View view = (View) oldEditPart.getModel();
		return new DeleteCommand(view);
	}

	/**
	 * Gets the creates the view command.
	 * 
	 * @param oldEditPart
	 *            the old edit part
	 * @param newEObject
	 *            the new e object
	 * 
	 * @return the creates the view command
	 */
	private Command getCreateViewCommand(EObject newEObject) {
		CreateViewRequest createViewRequest = getCreateViewRequest(newEObject);
		cachedCreateViewRequest = createViewRequest;
		if (createViewRequest == null) {
			return null;
		}

		return containerEditPart.getCommand(createViewRequest);
	}

	// // Getter methods

	/**
	 * Gets the new e object.
	 * 
	 * @return the new e object
	 */
	protected EObject getNewEObject() {
		return newEObject;
	}

	/**
	 * Gets the container.
	 * 
	 * @return the container
	 */
	protected EObject getContainer() {
		if (container != null)
			return container;

		EObject element = containerEditPart != null ? ((View) containerEditPart
				.getModel()).getElement() : null;
		return element;
	}

	/**
	 * Gets the containing feature.
	 * 
	 * @return the containing feature
	 */
	protected abstract EReference getContainingFeature();

	/**
	 * Gets the diagram.
	 * 
	 * @return the diagram
	 */
	protected Diagram getDiagram() {
		return containerEditPart != null ? DiagramEditPartsUtil
				.getDiagramEditPart(containerEditPart).getDiagramView() : null;
	}

	/**
	 * Gets the old e object.
	 * 
	 * @return the old e object
	 */
	protected EObject getOldEObject() {
		return oldEObject;
	}

	/**
	 * Gets the new e object view.
	 * 
	 * @return the new e object view
	 */
	protected View getNewEObjectView() {
		return newView;
	}

	// // extract results

	/**
	 * Gets the newly created e object.
	 * 
	 * @return the newly created e object
	 */
	protected void getNewlyCreatedEObject() {
		EObject eObject = cachedCreateElementRequest != null ? cachedCreateElementRequest
				.getNewElement()
				: null;
		newEObject = eObject;
	}

	/**
	 * Gets the newly created e object.
	 * 
	 * @return the newly created e object
	 */
	protected void getNewlyCreatedView() {
		ViewDescriptor vd = (ViewDescriptor) ((List) cachedCreateViewRequest
				.getNewObject()).get(0);
		newView = cachedCreateViewRequest != null ? (View) vd
				.getAdapter(View.class) : null;
	}

	/**
	 * Gets the location.
	 * 
	 * @param editPart
	 *            the edit part
	 * 
	 * @return the location
	 */
	private Point getNewLocation() {
		return getOldEditPart() != null ? new Point(getOldEditPart()
				.getFigure().getBounds().getLocation()) : new Point();
	}

	private GraphicalEditPart getOldEditPart() {
		for (Object o : containerEditPart.getChildren()) {
			if (o instanceof GraphicalEditPart) {
				GraphicalEditPart gep = (GraphicalEditPart) o;
				if (gep.resolveSemanticElement().equals(oldEObject))
					return gep;
			}
		}
		return null;
	}

	public Object getAdapter(Class adapter) {
		if (adapter != null && adapter.equals(View.class))
			return newView;
		else if (adapter != null && adapter.equals(EObject.class))
			return newEObject;
		else
			return null;
	}

	public void setShouldDeleteOldElement(boolean delete) {
		shouldDelete = delete;
	}

	public void setShouldCreateNewView(boolean createView) {
		shouldCreateNewView = createView;
	}

}
