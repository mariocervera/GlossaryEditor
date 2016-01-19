/*******************************************************************************
 * Copyright (c) 2007 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Gabriel Merin Cubero (Prodevelop) – initial API and
 * implementation
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.sections;

import java.util.List;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.common.core.command.CompositeCommand;
import org.eclipse.gmf.runtime.diagram.ui.properties.sections.appearance.DiagramColorsAndFontsPropertySection;

import es.cv.gvcase.mdt.common.commands.wrappers.GMFtoEMFCommandWrapper;

public class DiagramColorsAndFontsPropertySectionEx extends
		DiagramColorsAndFontsPropertySection {

	@Override
	protected TransactionalEditingDomain getEditingDomain() {
		try {
			return super.getEditingDomain();
		} catch (IllegalArgumentException ex) {
			return null;
		}
	}

	@Override
	protected CommandResult executeAsCompositeCommand(String actionName,
			List commands) {

		// Build the composite command
		CompositeCommand compositeCommand = new CompositeCommand(actionName,
				commands);

		// Transform GMF command to EMF command to executed in the editing
		// domain
		Command command = new GMFtoEMFCommandWrapper(compositeCommand);

		// Execute the command within the diagram editor editing domain
		getEditingDomain().getCommandStack().execute(command);

		// Return the command execution result
		return compositeCommand.getCommandResult();
	}
}
