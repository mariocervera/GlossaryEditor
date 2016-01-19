/*******************************************************************************
 * Copyright (c) 2009 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – Initial implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.util;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gmf.runtime.common.core.command.ICommand;
import org.eclipse.gmf.runtime.diagram.ui.commands.ICommandProxy;

/**
 * Storage for debug utils.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 *
 */
public class DebugUtil {

	
	/**
	 * Command printing on standard output.
	 */

	// //
	public static void printCommandOnStdOut(ICommand c) {
		printCommand(new ICommandProxy(c));
	}
	
	public static void printCommandOnStdOut(Command c) {
		printCommand(c);
	}

	protected static void printCommand(Command c) {
		if (c instanceof CompoundCommand) {
			printCompoundCommand((CompoundCommand) c);
		} else if (c instanceof ICommandProxy) {
			System.out.println(((ICommandProxy) c).getICommand().toString() + " canExecute: " + c.canExecute());
		} else {
			System.out.println(c.toString() + " canExecute: " + c.canExecute());
		}
	}

	protected static void printCompoundCommand(CompoundCommand cc) {
		for (Object c : cc.getChildren()) {
			if (c instanceof CompoundCommand) {
				printCompoundCommand((CompoundCommand) c);
			} else {
				if (c instanceof Command) {
					printCommand((Command) c);
				}
			}
		}
	}

}
