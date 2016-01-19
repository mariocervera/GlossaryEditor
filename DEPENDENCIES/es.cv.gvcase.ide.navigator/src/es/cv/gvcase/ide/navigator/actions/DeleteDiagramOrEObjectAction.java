/*******************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) - initial API implementation
 * [03/04/08] Francisco Javier Cano Muñoz (Prodevelop) - adaptation to Common Navigator Framework
 *
 ******************************************************************************/
package es.cv.gvcase.ide.navigator.actions;

import java.util.Collections;

import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.command.DeleteCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.gmf.runtime.emf.core.resources.GMFResource;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.actions.SelectionListenerAction;

import es.cv.gvcase.mdt.common.commands.DeleteDiagramCommand;

/**
 * A standar DeleteAction that allows deleting {@link Diagram} elements from a
 * {@link GMFResource}.
 * 
 * @author <a href="maulto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 */
public class DeleteDiagramOrEObjectAction extends SelectionListenerAction {

	/**
	 * Instantiates a new delete diagram or e object action.
	 * 
	 * @param removeAllReferences
	 *            the remove all references
	 */
	public DeleteDiagramOrEObjectAction(boolean removeAllReferences) {
		super("Delete");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		super.run();
		// //
		IStructuredSelection ss = getStructuredSelection();
		CompoundCommand cc = new CompoundCommand("Delete elements");
		TransactionalEditingDomain domain = null;
		for (Object selected : ss.toList()) {
			if (selected instanceof EObject) {
				domain = domain != null ? domain : TransactionUtil
						.getEditingDomain((EObject) selected);
				if (selected instanceof Diagram) {
					Diagram diagram = (Diagram) selected;
					cc.append(new DeleteDiagramCommand(diagram));
				} else {
					cc.append(new DeleteCommand(domain, Collections
							.singleton(selected)));
				}
			}
		}
		if (cc.canExecute() && domain != null) {
			domain.getCommandStack().execute(cc);
		}
	}

}
