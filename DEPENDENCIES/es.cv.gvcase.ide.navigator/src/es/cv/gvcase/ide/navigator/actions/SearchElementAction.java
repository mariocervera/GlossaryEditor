/*******************************************************************************
 * Copyright (c) 2009 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Javier Muñoz (Prodevelop) – Initial implementation
 *
 ******************************************************************************/
package es.cv.gvcase.ide.navigator.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import es.cv.gvcase.ide.navigator.dialogs.NavigatorSearchDialog;
import es.cv.gvcase.ide.navigator.view.MOSKittModelNavigator;

/**
 * 
 * @author <a href="mailto:jmunoz@prodevelop.es>Javier Muñoz</a>
 *
 */
public class SearchElementAction extends Action{
	

	private final MOSKittModelNavigator navigator;

	public SearchElementAction(MOSKittModelNavigator commonNavigator){
		super("Search elements");
		this.navigator = commonNavigator;
		this.setToolTipText("Search elements");
		init();
	}

	private void init() {
	}

	@Override
	public void run() {		
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		NavigatorSearchDialog dialog =
			new NavigatorSearchDialog(shell,
					navigator);
		dialog.open();
		
	}
	
}
