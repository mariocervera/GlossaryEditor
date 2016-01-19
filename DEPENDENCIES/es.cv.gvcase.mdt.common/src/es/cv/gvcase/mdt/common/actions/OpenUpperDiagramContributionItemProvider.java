/***************************************************************************
 * Copyright (c) 2008, 2010 Conselleria de Infraestructuras y Transporte,
 * Generalitat de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – initial API and implementation
 * 				 Francisco Javier Cano Muñoz (Prodevelop) - prepared the contributor to contribute the Open Upper action to the toolbar.
 *
 ******************************************************************************/

package es.cv.gvcase.mdt.common.actions;

import org.eclipse.gmf.runtime.common.ui.services.action.contributionitem.AbstractContributionItemProvider;
import org.eclipse.gmf.runtime.common.ui.util.IWorkbenchPartDescriptor;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IWorkbenchPage;

/**
 * Contributes the "Open upper" action to the diagram's context menu.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 */
public class OpenUpperDiagramContributionItemProvider extends
		AbstractContributionItemProvider {

	/** The Constant ACTION_OPEN_UPPER_DIAGRAM. */
	public static final String ACTION_OPEN_UPPER_DIAGRAM = "common_action_open_upper_diagram";

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.gmf.runtime.common.ui.services.action.contributionitem.
	 * AbstractContributionItemProvider#createAction(java.lang.String,
	 * org.eclipse.gmf.runtime.common.ui.util.IWorkbenchPartDescriptor)
	 */
	@Override
	protected IAction createAction(String actionId,
			IWorkbenchPartDescriptor partDescriptor) {
		IWorkbenchPage page = partDescriptor.getPartPage();
		if (ACTION_OPEN_UPPER_DIAGRAM.equals(actionId)) {
			OpenUpperDiagramAction ouda = new OpenUpperDiagramAction(page);
			return ouda;
		}
		return super.createAction(actionId, partDescriptor);
	}

}
