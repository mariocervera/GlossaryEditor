/*******************************************************************************
 * Copyright (c) 2010 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – Initial implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.actions;

import org.eclipse.gmf.runtime.common.ui.services.action.contributionitem.AbstractContributionItemProvider;
import org.eclipse.gmf.runtime.common.ui.util.IWorkbenchPartDescriptor;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;

/**
 * Contributor of the groupin/ungrouping action for GMF diagrams.
 * 
 * @author <a href="mailto:fjcano@prodeelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public class GroupViewsContributionItemProvider extends
		AbstractContributionItemProvider {

	public static final String ACTION_GROUP_VIEWS = "common_action_group_views";

	@Override
	protected IAction createAction(String actionId,
			IWorkbenchPartDescriptor partDescriptor) {
		if (ACTION_GROUP_VIEWS.equals(actionId)) {
			GroupViewsAction action = new GroupViewsAction(partDescriptor
					.getPartPage());
			action.setAccelerator(SWT.CTRL | SWT.ALT | 'Q');
			return action;
		}
		return super.createAction(actionId, partDescriptor);
	}

}
