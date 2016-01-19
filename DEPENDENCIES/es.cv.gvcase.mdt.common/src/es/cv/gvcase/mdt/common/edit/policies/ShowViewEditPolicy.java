/*******************************************************************************
 * Copyright (c) 2007, 2010 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Miguel Llacer San Fernando (Prodevelop) - initial API and implementation
 *
 ******************************************************************************/

package es.cv.gvcase.mdt.common.edit.policies;

import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.OpenEditPolicy;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import es.cv.gvcase.emf.common.provider.GeneralLabelProvider;
import es.cv.gvcase.mdt.common.commands.AbstractCommonTransactionalCommmand;
import es.cv.gvcase.mdt.common.commands.OpenAsDiagramCommand;
import es.cv.gvcase.mdt.common.commands.OpenDiagramCommand;
import es.cv.gvcase.mdt.common.commands.diagram.EClassToDiagramRegistry;
import es.cv.gvcase.mdt.common.util.MDTUtil;
import es.cv.gvcase.mdt.common.util.MultiDiagramUtil;

/**
 * An EditPolicy that opens a
 */
public class ShowViewEditPolicy {

	/**
	 * Identifier of the view to open
	 */
	private String viewID = "org.eclipse.ui.views.PropertySheet";

	public ShowViewEditPolicy() {
	}

	public ShowViewEditPolicy(String viewID) {
		this.viewID = viewID;
	}

	public String getViewID() {
		return viewID;
	}

	/**
	 * Listens to double-click event over some element of the diagram and shows
	 * properties tab.
	 * 
	 * @return the open edit policy
	 */
	public static OpenEditPolicy createOpenEditPolicy() {
		return (new ShowViewEditPolicy())
				.createOpenEditPolicy("org.eclipse.ui.views.PropertySheet");
	}

	/**
	 * Listens to double-click event over some element of the diagram and shows
	 * properties tab.
	 * 
	 * @return the open edit policy
	 */
	public OpenEditPolicy createOpenEditPolicy(final String viewID) {
		return new OpenEditPolicy() {
			@Override
			protected Command getOpenCommand(Request request) {
				// fjcano :: if the hosting figure has any diagram
				// associated, open one of them
				Diagram diagramToOpen = getDiagramToOpen();
				if (diagramToOpen != null) {
					return new OpenDiagramCommand(diagramToOpen).toGEFCommand();
				}
				// fjcano :: if no diagram is available to open, check if a
				// new one can be created
				String diagramToCreate = getDiagramKindToCreate();
				if (diagramToCreate != null) {
					IGraphicalEditPart host = MDTUtil
							.getHostGraphicalEditPart(this);
					return new OpenAsDiagramCommand(getGMFREsource(host), host
							.resolveSemanticElement(), diagramToCreate)
							.toGEFCommand();
				}
				// fjcano :: by default, open the properties view
				TransactionalEditingDomain domain = getEditingDomain();
				if (domain != null) {
					return (new OpenViewCommand(domain, viewID)).toGEFCommand();
				}
				return null;
			}

			protected Resource getGMFREsource(IGraphicalEditPart editPart) {
				if (editPart != null) {
					return editPart.getNotationView().eResource();
				}
				return null;
			}

			/**
			 * Return true if an associated diagram was opened.
			 * 
			 * @return
			 */
			protected Diagram getDiagramToOpen() {
				IGraphicalEditPart grahicalHostEditPart = (IGraphicalEditPart) getHost();
				EObject element = grahicalHostEditPart.resolveSemanticElement();
				List<Diagram> diagramL = MultiDiagramUtil
						.getDiagramsAssociatedToElement(element);

				if (diagramL.isEmpty()) {
					return null;
				} else if (diagramL.size() == 1) {
					return diagramL.get(0);
				}

				ElementListSelectionDialog dialog = new ElementListSelectionDialog(
						PlatformUI.getWorkbench().getActiveWorkbenchWindow()
								.getShell(), new GeneralLabelProvider());
				dialog.setMessage("Select the diagram to be opened");
				dialog.setTitle("Diagram selection");
				dialog.setElements(diagramL.toArray());
				if (dialog.open() == Dialog.OK) {
					return (Diagram) dialog.getFirstResult();
				}

				return null;
			}

			protected String getDiagramKindToCreate() {
				IGraphicalEditPart grahicalHostEditPart = (IGraphicalEditPart) getHost();
				List<String> diagramL = EClassToDiagramRegistry.getInstance()
						.getDiagramsForEditPart(grahicalHostEditPart);

				if (diagramL.isEmpty()) {
					return null;
				} else if (diagramL.size() == 1) {
					return diagramL.get(0);
				}

				ElementListSelectionDialog dialog = new ElementListSelectionDialog(
						PlatformUI.getWorkbench().getActiveWorkbenchWindow()
								.getShell(), new GeneralLabelProvider());
				dialog.setMessage("Select the diagram to be created");
				dialog.setTitle("Diagram selection");
				dialog.setElements(diagramL.toArray());
				if (dialog.open() == Dialog.OK) {
					return (String) dialog.getFirstResult();
				}

				return null;
			}

			protected TransactionalEditingDomain getEditingDomain() {
				if (getHost() instanceof IGraphicalEditPart) {
					return ((IGraphicalEditPart) getHost()).getEditingDomain();
				}
				return null;
			}
		};

	}

	protected static ShowViewEditPolicy ShowPropertiesViewEditPolicy = null;

	public static ShowViewEditPolicy getShowPropertiesViewEditPolicy() {
		if (ShowPropertiesViewEditPolicy == null) {
			ShowPropertiesViewEditPolicy = new ShowViewEditPolicy(
					"org.eclipse.ui.views.PropertySheet");
		}
		return ShowPropertiesViewEditPolicy;
	}

	/**
	 * Simple command to encapsulate the operation of opening a view in Eclipse
	 * 
	 * @author fjcano
	 * 
	 */
	protected class OpenViewCommand extends AbstractCommonTransactionalCommmand {

		protected String viewID = null;

		public OpenViewCommand(TransactionalEditingDomain domain, String viewID) {
			super(domain, "Open view", null);
			this.viewID = viewID;
		}

		@Override
		protected CommandResult doExecuteWithResult(IProgressMonitor monitor,
				IAdaptable info) throws ExecutionException {
			try {
				// fjcano :: by default, open the properties view
				PlatformUI.getWorkbench().getActiveWorkbenchWindow()
						.getActivePage().showView(this.viewID);
			} catch (PartInitException ex) {
				return CommandResult.newErrorCommandResult(ex);
			}
			return CommandResult.newOKCommandResult();
		}
	}

}
