/*******************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Francisco Javier Cano Muñoz (Prodevelop) – Initial API and implementation.
 * Mario Cervera Ubeda (Prodevelop) - Adapted to use CommandProviderRegistry
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.actions;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.UnexecutableCommand;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.gmf.runtime.common.core.command.CompositeCommand;
import org.eclipse.gmf.runtime.diagram.ui.commands.CommandProxy;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.jface.viewers.IStructuredSelection;

import es.cv.gvcase.mdt.common.actions.registry.CommandProviderRegistry;
import es.cv.gvcase.mdt.common.actions.registry.DeleteFromModelAndDiagramBlackListRegistry;
import es.cv.gvcase.mdt.common.actions.registry.ICommandProvider;
import es.cv.gvcase.mdt.common.commands.DeleteCommand;
import es.cv.gvcase.mdt.common.commands.DeleteDiagramCommand;
import es.cv.gvcase.mdt.common.commands.DeleteFromModelCommandRegistry;
import es.cv.gvcase.mdt.common.commands.wrappers.EMFtoGMFCommandWrapper;
import es.cv.gvcase.mdt.common.commands.wrappers.GMFtoEMFCommandWrapper;

/**
 * An specialization of DeleteAction that allows to delete Diagrams in the
 * MOSKitt model navigator.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public class DeleteAction extends org.eclipse.emf.edit.ui.action.DeleteAction {

	public DeleteAction(EditingDomain domain, boolean removeAllReferences) {
		super(domain, removeAllReferences);
	}

	public DeleteAction(EditingDomain domain) {
		this(domain, false);
	}

	public DeleteAction(boolean removeAllReferences) {
		this(null, removeAllReferences);
	}

	public DeleteAction() {
		this(null);
	}

	@Override
	public Command createCommand(Collection<?> selection) {
		// //
		CompositeCommand cc = new CompositeCommand("Delete elements");
		TransactionalEditingDomain domain = null;
		for (Object selected : selection) {
			if (selected instanceof EObject) {
				EObject selectedEObj = (EObject) selected;
				domain = domain != null ? domain : TransactionUtil
						.getEditingDomain(selectedEObj);

				if (CommandProviderRegistry.providesFor(selectedEObj.eClass())) {
					ICommandProvider provider = CommandProviderRegistry
							.getCommandProvider(selectedEObj.eClass());
					Command c = provider.getCommand(selectedEObj,
							ICommandProvider.DELETE_ACTION);
					if (c != null) {
						cc.add(new EMFtoGMFCommandWrapper(c));
					}
				} else {
					if (selected instanceof Diagram) {
						Diagram diagram = (Diagram) selected;
						cc.add(new EMFtoGMFCommandWrapper(
								new DeleteDiagramCommand(diagram)));
					} else {
						org.eclipse.gef.commands.Command aux = DeleteFromModelCommandRegistry
								.findCommand(getStructuredSelection());
						if (aux != null && aux.canExecute()) {
							cc.add(new CommandProxy(aux));
						} else {
							cc.add(new DeleteCommand(domain, "Delete elements",
									null, Collections.singleton(selected)));
						}
					}
				}
			}
		}
		if (domain != null && cc.canExecute()) {
			return new GMFtoEMFCommandWrapper(cc);
		}
		return UnexecutableCommand.INSTANCE;
	}

	@Override
	public boolean updateSelection(IStructuredSelection selection) {
		return super.updateSelection(selection)
				&& !DeleteFromModelAndDiagramBlackListRegistry.getInstance()
						.removeDeleteFromModelAction(
								getStructuredSelection().toList());
	}

}
