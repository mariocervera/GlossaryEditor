/*******************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Francisco Javier Cano Muñoz (Prodevelop) – Initial API and implementation.
 * Mario Cervera Ubeda (Prodevelop) - Create action also for editparts without semantic element
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.actions;

import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.gmf.runtime.common.ui.services.action.contributionitem.AbstractContributionItemProvider;
import org.eclipse.gmf.runtime.common.ui.util.IWorkbenchPartDescriptor;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPage;

import es.cv.gvcase.mdt.common.util.MDTUtil;

public class DeleteActionItemProvider extends AbstractContributionItemProvider {

	/** The Constant ACTION_OPEN_UPPER_DIAGRAM. */
	private static final String DELETE= "delete_from_model";

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gmf.runtime.common.ui.services.action.contributionitem.AbstractContributionItemProvider#createAction(java.lang.String,
	 *      org.eclipse.gmf.runtime.common.ui.util.IWorkbenchPartDescriptor)
	 */
	@Override
	protected IAction createAction(String actionId,
			IWorkbenchPartDescriptor partDescriptor) {
		if (DELETE.equals(actionId) == false) {
			return super.createAction(actionId, partDescriptor);
		}
		IWorkbenchPage page = partDescriptor.getPartPage();
		ISelection selection = page.getSelection();
		EditingDomain domain = null;
		if (selection!= null && selection.isEmpty() == false) {
			List<EObject> eObjects = MDTUtil.getEObjectsFromSelection(selection);
			if (eObjects != null && eObjects.size() > 0) {
				domain = TransactionUtil.getEditingDomain(eObjects.get(0));
			}
			if(domain == null && selection instanceof StructuredSelection) {
				Iterator it = ((StructuredSelection)selection).iterator();
				while(it.hasNext() && domain == null) {
					Object ob = it.next();
					if(ob instanceof IGraphicalEditPart) {
						domain = ((IGraphicalEditPart)ob).getEditingDomain();
					}
				}
			}
		}
		if (domain != null) {
			return new DeleteFromModelAction(page);
		}
		return super.createAction(actionId, partDescriptor);
	}
	
}
