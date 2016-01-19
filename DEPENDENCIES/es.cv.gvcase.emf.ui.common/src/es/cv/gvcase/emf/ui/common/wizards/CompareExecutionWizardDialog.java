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
package es.cv.gvcase.emf.ui.common.wizards;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

public class CompareExecutionWizardDialog extends WizardDialog {

	public CompareExecutionWizardDialog(Shell parentShell, IWizard newWizard) {
		super(parentShell, newWizard);
	}

}
