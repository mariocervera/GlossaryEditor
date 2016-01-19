/*******************************************************************************
 * Copyright (c) 2008, 2010 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – Initial implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.commands;

import java.util.Collections;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.notation.Diagram;

import es.cv.gvcase.mdt.common.Activator;
import es.cv.gvcase.mdt.common.commands.diagram.DiagramInitializerRegistry;
import es.cv.gvcase.mdt.common.ids.MOSKittModelIDs;
import es.cv.gvcase.mdt.common.model.CreateDiagramElementWrapper;
import es.cv.gvcase.mdt.common.provider.IDiagramInitializer;
import es.cv.gvcase.mdt.common.runnable.HookRunner;
import es.cv.gvcase.mdt.common.util.MDTUtil;
import es.cv.gvcase.mdt.common.util.MultiDiagramUtil;

/**
 * The Class OpenAsDiagramCommand.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 */
public class OpenAsDiagramCommand extends AbstractCommonTransactionalCommmand {

	// private View nodeView = null;
	/** The {@link Resource} where the new {@link Diagram} must be added. */
	protected Resource resource = null;

	/** The {@link EObject} that will be the canvas element. */
	private EObject element = null;

	/** The {@link Diagram} kind or type. */
	private String diagramKind = "";

	/** The new {@link Diagram}. */
	protected Diagram newDiagram = null;

	/** A {@link Map} of {@link IDiagramInitializer}s by type or kind. */
	private Map<String, IDiagramInitializer> initializers = null;

	/**
	 * Flag to know whether the name of the {@link Diagram} must be asked to the
	 * user.
	 */
	protected boolean askName = true;

	/** A {@link Diagram} name suggestion. */
	protected String possibleName = "";

	/**
	 * Flag to open the created diagram in an editor.
	 */
	private boolean openNewDiagram = true;

	/**
	 * The previous {@link Diagram}. If an undo is executed, this
	 * {@link Diagram} will be opened if this command closed it.
	 */
	protected Diagram previousDiagram = null;

	/**
	 * Flag to close the old {@link Diagram} we come from.
	 */
	private boolean openInNew = true;

	/**
	 * Flag that indicated this command created a new {@link Diagram}.
	 */
	protected boolean createdDiagram = false;

	/**
	 * Instantiates a new {@link OpenAsDiagramCommand}.
	 * 
	 * @param resource
	 *            the resource
	 * @param element
	 *            the element
	 * @param diagramKind
	 *            the diagram kind
	 */
	public OpenAsDiagramCommand(Resource resource, EObject element,
			String diagramKind) {
		this(resource, element, diagramKind, "", null, false);
	}

	/**
	 * 
	 * @param resource
	 * @param element
	 * @param diagramKind
	 * @param closeOld
	 */
	public OpenAsDiagramCommand(Resource resource, EObject element,
			String diagramKind, boolean openInNew) {
		this(resource, element, diagramKind, "", null, openInNew);
	}

	/**
	 * Instantiates a new {@link OpenAsDiagramCommand}.
	 * 
	 * @param resource
	 *            the resource
	 * @param element
	 *            the element
	 * @param diagramKind
	 *            the diagram kind
	 * @param name
	 *            the name
	 */
	public OpenAsDiagramCommand(Resource resource, EObject element,
			String diagramKind, String name) {
		this(resource, element, diagramKind, name, null, false);
	}

	/**
	 * 
	 * @param resource
	 * @param element
	 * @param diagramKind
	 * @param name
	 * @param closeOld
	 */
	public OpenAsDiagramCommand(Resource resource, EObject element,
			String diagramKind, String name, boolean openInNew) {
		this(resource, element, diagramKind, name, null, openInNew);
	}

	/**
	 * Instantiates a new {@link OpenAsDiagramCommand}.
	 * 
	 * @param resource
	 *            the resource
	 * @param element
	 *            the element
	 * @param diagramKind
	 *            the diagram kind
	 * @param initializer
	 *            the initializer
	 */
	public OpenAsDiagramCommand(Resource resource, EObject element,
			String diagramKind, IDiagramInitializer initializer) {
		this(resource, element, diagramKind, "", singleTonMap(diagramKind,
				initializer), false);
	}

	/**
	 * Instantiates a new {@link OpenAsDiagramCommand}.
	 * 
	 * @param resource
	 *            the resource
	 * @param element
	 *            the element
	 * @param diagramKind
	 *            the diagram kind
	 * @param initializers
	 *            the initializers
	 */
	public OpenAsDiagramCommand(Resource resource, EObject element,
			String diagramKind, Map<String, IDiagramInitializer> initializers) {
		this(resource, element, diagramKind, "", initializers, false);
	}

	/**
	 * Instantiates a new {@link OpenAsDiagramCommand}.
	 * 
	 * @param resource
	 *            the resource
	 * @param element
	 *            the element
	 * @param diagramKind
	 *            the diagram kind
	 * @param name
	 *            the diagram name
	 * @param initializer
	 *            the initializer
	 */
	public OpenAsDiagramCommand(Resource resource, EObject element,
			String diagramKind, String name, IDiagramInitializer initializer) {
		this(resource, element, diagramKind, name, singleTonMap(diagramKind,
				initializer), false);
	}

	/**
	 * Instantiates a new {@link OpenAsDiagramCommand}.
	 * 
	 * @param resource
	 *            the resource
	 * @param element
	 *            the element
	 * @param diagramKind
	 *            the diagram kind
	 * @param name
	 *            diagram's name
	 * @param initializers
	 *            the initializers
	 * @param closeOld
	 *            close old diagram
	 */
	public OpenAsDiagramCommand(Resource resource, EObject element,
			String diagramKind, String name,
			Map<String, IDiagramInitializer> initializers, boolean openInNew) {
		// editing domain is taken for original diagram,
		// if we open diagram from another file, we should use another editing
		// domain
		super(TransactionUtil.getEditingDomain(element), "Open Diagram", null);
		this.resource = resource;
		this.element = element;
		this.diagramKind = diagramKind;
		this.possibleName = name;
		this.initializers = initializers;
		this.openInNew = openInNew;
	}

	protected static Map<String, IDiagramInitializer> singleTonMap(String kind,
			IDiagramInitializer initializer) {
		if (kind != null) {
			return Collections.singletonMap(kind, initializer);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.commands.operations.AbstractOperation#canExecute()
	 */
	@Override
	public boolean canExecute() {
		return super.canExecute()
				&& (resource != null)
				&& (getDiagramDomainElement() != null)
				&& (MOSKittModelIDs.getAllExtensionModelIDs()
						.contains(getDiagramKind()));
	}

	public static final String hookPointCreateDiagram = "es.cv.gvcase.mdt.common.CreateDiagramRunnableHook";

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
		try {
			createdDiagram = false;
			// get the Diagram to open
			Diagram diagram = getDiagramToOpen();
			if (diagram == null) {
				// Try to create the diagram using an existing Runnable Hook
				CreateDiagramElementWrapper elementWrapper = new CreateDiagramElementWrapper(
						getDiagramDomainElement(), getDiagramKind(),
						possibleName, askName);
				HookRunner.getInstance().runRunnablesWithInfoForHook(
						hookPointCreateDiagram, elementWrapper);

				if (elementWrapper.getDiagram() != null) {
					diagram = elementWrapper.getDiagram();
					createdDiagram = true;
				} else if (!elementWrapper.proceedWithCreationAndOpening()) {
					return CommandResult.newCancelledCommandResult();
				} else {
					// if no Diagram to open, create a new one
					if (possibleName != "") {
						diagram = MultiDiagramUtil.intializeNewDiagram(
								getDiagramKind(), getDiagramDomainElement(),
								resource, getInitializers(), askName,
								possibleName);
					} else {
						diagram = MultiDiagramUtil.intializeNewDiagram(
								getDiagramKind(), getDiagramDomainElement(),
								resource, getInitializers(), askName);
					}
					if (diagram == null) {
						// the Diagram could not be created, something went
						// wrong.
						// Cancell.
						return CommandResult.newCancelledCommandResult();
					} else {
						createdDiagram = true;
					}
				}
			}
			// set the new Diagram
			newDiagram = diagram;
			// find the previous Diagram, to reopen it if this command in
			// undone.
			previousDiagram = MDTUtil.getDiagramFomEditor(MDTUtil
					.getActiveEditor());
			if (isOpenNewDiagram()) {
				// if the new Diagram has to be opened, open it :)
				MultiDiagramUtil.openDiagram(diagram, isOpenInNew()
						|| createdDiagram);
			}
			// no problems.
			return CommandResult.newOKCommandResult();
		} catch (Exception ex) {
			// something happened.
			throw new ExecutionException("Can't open diagram", ex);
		}
	}

	/**
	 * Gets the {@link Diagram} to open.
	 * 
	 * @return the diagram to open
	 */
	protected Diagram getDiagramToOpen() {
		return null;
	}

	/**
	 * Gets the {@link Diagram} domain element.
	 * 
	 * @return the diagram domain element
	 */
	protected EObject getDiagramDomainElement() {
		// use same element as associated with EP
		return element;
	}

	/**
	 * Gets the {@link Diagram} kind or type.
	 * 
	 * @return the diagram kind
	 * 
	 * @throws ExecutionException
	 *             the execution exception
	 */
	protected String getDiagramKind() {
		return diagramKind;
	}

	/**
	 * Will look for the initializers contributed to the
	 * {@link DiagramInitializerRegistry} via extension points or return the
	 * provided ones in the constructor.
	 * 
	 * @return
	 */
	protected Map<String, IDiagramInitializer> getInitializers() {
		Map<String, IDiagramInitializer> allInitializers = DiagramInitializerRegistry
				.getInstance().getAllDiagramInitializers();
		if (allInitializers != null) {
			return allInitializers;
		}
		return initializers;
	}

	/**
	 * 
	 * @return
	 */
	public Diagram getPreviousDiagram() {
		return previousDiagram;
	}

	/**
	 * True to open the created {@link Diagram}.
	 * 
	 * @return
	 */
	public boolean isOpenNewDiagram() {
		return openNewDiagram;
	}

	/**
	 * True to open the created {@link Diagram}. False to not open the new
	 * Diagram. True by default.
	 * 
	 * @param openNewDiagram
	 */
	public void setOpenNewDiagram(boolean openNewDiagram) {
		this.openNewDiagram = openNewDiagram;
	}

	/**
	 * Adapts to {@link Diagram}.class, returning the newly created Diagram
	 * after having been executed.
	 * 
	 * @param class_
	 *            the class_
	 * 
	 * @return the adapter
	 */
	public Object getAdapter(Class class_) {
		if (class_.equals(Diagram.class)) {
			return newDiagram;
		}
		return null;
	}

	/**
	 * Sets the ask name.
	 * 
	 * @param askName
	 *            the new ask name
	 */
	public void setAskName(boolean askName) {
		this.askName = askName;
	}

	public boolean isOpenInNew() {
		return openInNew;
	}

	@Override
	public boolean canUndo() {
		if (createdDiagram == true && previousDiagram == null) {
			return false;
		}
		return true;
	}

	/**
	 * Undoing removes the created {@link Diagram} from the {@link Resource} and
	 * opens the previously open {@link Diagram}.
	 */
	@Override
	protected IStatus doUndo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		if (previousDiagram != null) {
			// open the previously open Diagram
			MultiDiagramUtil.openDiagram(previousDiagram);
		}
		if (createdDiagram) {
			// remove the created Diagram from the Resource.
			newDiagram.eResource().getContents().remove(newDiagram);
		}
		return new Status(IStatus.OK, Activator.PLUGIN_ID,
				"OpenAsDiagram undone");
	}

}
