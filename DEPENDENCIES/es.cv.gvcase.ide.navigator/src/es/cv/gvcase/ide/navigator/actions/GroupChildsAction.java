/*******************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Javier Muñoz (Integranova) - Initial implementation
 *
 ******************************************************************************/
package es.cv.gvcase.ide.navigator.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IPropertyListener;

import es.cv.gvcase.ide.navigator.view.MOSKittModelNavigator;

/**
 * 
 * @author <a href="mailto:jmunoz@prodevelop.es>Javier Muñoz</a>
 *
 */
public class GroupChildsAction extends Action implements IPropertyListener{
	

	private final MOSKittModelNavigator navigator;

	public GroupChildsAction(MOSKittModelNavigator commonNavigator){
		super("Group Childs");
		this.navigator = commonNavigator;
		this.setToolTipText("Group Childs");
		init();
	}

	private void init() {
	
		updateGroupingChildsProperty(navigator.isGroupingChildsEnabled());
		navigator.addPropertyListener(this);	
	}

	@Override
	public void run() {
		navigator.setGroupChildsEnabled(!navigator.isGroupingChildsEnabled());
	}

	public void propertyChanged(Object source, int propId) {
		switch (propId){
		case MOSKittModelNavigator.IS_GROUPINGCHILDS_ENABLED_PROPERTY:
			updateGroupingChildsProperty(((MOSKittModelNavigator)source).isGroupingChildsEnabled());
		}
		
	}

	private void updateGroupingChildsProperty(boolean groupingChildsEnabled) {
		setChecked(groupingChildsEnabled);
		
	}

	

}
