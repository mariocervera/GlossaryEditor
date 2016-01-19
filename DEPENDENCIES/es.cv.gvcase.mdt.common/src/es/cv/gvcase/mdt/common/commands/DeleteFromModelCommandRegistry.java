/*******************************************************************************
 * Copyright (c) 2008, 2009 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Marc Gil Sendra (Prodevelop) – Initial implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.commands;

import org.eclipse.core.runtime.Platform;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.viewers.IStructuredSelection;

import es.cv.gvcase.mdt.common.factories.DeleteFromModelCommandFactory;
import es.cv.gvcase.mdt.common.util.ExtensionPointParser;

public class DeleteFromModelCommandRegistry {
	/**
	 * The extension point identifier for Delete Commands
	 */
	private static final String extensionPointID = "es.cv.gvcase.mdt.common.customizedDeleteFromModelCommand";

	/**
	 * Find DeleteCommands from the extension points for the selected objects
	 * 
	 * @return
	 */
	public static Command findCommand(IStructuredSelection selection) {
		ExtensionPointParser extensionPointParser = new ExtensionPointParser(
				extensionPointID, new Class[] { DeleteCommandElement.class });

		for (Object o : extensionPointParser.parseExtensionPoint()) {
			if (o instanceof DeleteCommandElement) {
				DeleteCommandElement deleteCommandElement = (DeleteCommandElement) Platform
						.getAdapterManager().getAdapter(o,
								DeleteCommandElement.class);

				if (deleteCommandElement.deleteClassFactory == null
						|| !(deleteCommandElement.deleteClassFactory instanceof DeleteFromModelCommandFactory))
					continue;

				DeleteFromModelCommandFactory factory = (DeleteFromModelCommandFactory) deleteCommandElement.deleteClassFactory;

				if (factory.provides(selection))
					return factory.createNewDeleteFromModelCommand(selection);
			}
		}

		return null;
	}
}
