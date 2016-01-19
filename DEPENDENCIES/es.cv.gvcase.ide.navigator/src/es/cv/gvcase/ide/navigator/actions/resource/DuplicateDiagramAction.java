/*******************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – Initial API 
 * implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.ide.navigator.actions.resource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.FeatureMapUtil;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.emf.clipboard.core.ClipboardSupportUtil;
import org.eclipse.gmf.runtime.emf.commands.core.commands.DuplicateEObjectsCommand;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.SelectionListenerAction;

import es.cv.gvcase.mdt.common.commands.wrappers.GMFtoEMFCommandWrapper;

/**
 * Action to open a {@link Diagram} closing all other {@link IEditorPart}s that
 * are editing the same resource the Diagram to open uses.
 * 
 * @author <a href="maulto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 */
public class DuplicateDiagramAction extends SelectionListenerAction {

	/**
	 * Instantiates a new open diagram action.
	 */
	public DuplicateDiagramAction() {
		super("Duplicate diagram");
	}

	/**
	 * Gets the diagram.
	 * 
	 * @return the diagram
	 */
	protected Diagram getDiagram() {
		IStructuredSelection selection = getStructuredSelection();
		Object selected = selection.getFirstElement();
		if (selected instanceof Diagram) {
			return ((Diagram) selected);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		super.run();
		Diagram diagram = getDiagram();
		if (diagram != null) {
			duplicateDiagram(diagram);
		}
	}

	private void duplicateDiagram(Diagram diagram) {
		TransactionalEditingDomain editingDomain = TransactionUtil
				.getEditingDomain(diagram);
		List l = new ArrayList();
		l.add(diagram);
		DuplicateEObjectsCommand c = new DuplicateDiagramCommand(editingDomain,
				l);
		editingDomain.getCommandStack().execute(new GMFtoEMFCommandWrapper(c));
	}

	private class DuplicateDiagramCommand extends DuplicateEObjectsCommand {

		public DuplicateDiagramCommand(
				TransactionalEditingDomain editingDomain,
				List objectsToBeDuplicated) {
			super(editingDomain, "Duplicate EObjects", objectsToBeDuplicated);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @seeorg.eclipse.gmf.runtime.emf.commands.core.commands.
		 * DuplicateEObjectsCommand#canExecute()
		 */
		@Override
		public boolean canExecute() {
			return true;
		}

		@Override
		protected CommandResult doExecuteWithResult(
				IProgressMonitor progressMonitor, IAdaptable info)
				throws ExecutionException {

			// Remove elements whose container is getting copied.
			ClipboardSupportUtil.getCopyElements(getObjectsToBeDuplicated());

			// Perform the copy and update the references.
			EcoreUtil.Copier copier = new EcoreUtil.Copier();
			copier.copyAll(this.getObjectsToBeDuplicated());
			copier.copyReferences();

			// Update the map with all elements duplicated.
			getAllDuplicatedObjectsMap().putAll(copier);

			// Add the duplicates to the original's container.
			for (Iterator i = this.getObjectsToBeDuplicated().iterator(); i
					.hasNext();) {
				EObject original = (EObject) i.next();
				EObject duplicate = (EObject) copier.get(original);

				if (original instanceof Diagram) {
					((Diagram) original).eResource().getContents().add(
							duplicate);
				} else {
					EReference reference = original.eContainmentFeature();
					if (reference != null
							&& FeatureMapUtil.isMany(original.eContainer(),
									reference)
							&& ClipboardSupportUtil
									.isOkToAppendEObjectAt(original
											.eContainer(), reference, duplicate)) {

						ClipboardSupportUtil.appendEObjectAt(original
								.eContainer(), reference, duplicate);
					}
				}
			}
			return CommandResult
					.newOKCommandResult(getAllDuplicatedObjectsMap());
		}

	}

}
