/*******************************************************************************
 * Copyright (c) 2011 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana. All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *  Miguel Llacer (Prodevelop) [mllacer@prodevelop.es] - initial API implementation
 ******************************************************************************/
package es.cv.gvcase.emf.ui.common.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import es.cv.gvcase.emf.ui.common.wizards.CompareExecutionWizard;
import es.cv.gvcase.emf.ui.common.wizards.CompareExecutionWizardDialog;

public class CompareAction implements IObjectActionDelegate {

	private IWorkbenchPart targetPart;

	private URI leftModelURI = null;

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.targetPart = targetPart;
	}

	public void run(IAction action) {
		Wizard wizard = new CompareExecutionWizard(leftModelURI);

		CompareExecutionWizardDialog dialog = new CompareExecutionWizardDialog(
				getShell(), wizard);

		dialog.open();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		action.setEnabled(false);

		if (!(selection instanceof IStructuredSelection) || selection.isEmpty()) {
			return;
		}
		if (!(((IStructuredSelection) selection).getFirstElement() instanceof IFile)) {
			return;
		}

		leftModelURI = URI.createPlatformResourceURI(
				((IFile) ((IStructuredSelection) selection).getFirstElement())
						.getFullPath().toString(), true);

		action.setEnabled(true);
	}

	private Shell getShell() {
		return targetPart.getSite().getShell();
	}

}
