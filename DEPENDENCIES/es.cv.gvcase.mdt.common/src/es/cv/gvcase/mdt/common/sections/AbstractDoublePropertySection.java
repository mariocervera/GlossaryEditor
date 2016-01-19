/*******************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Javier Muñoz (Prodevelop) – Initial implementation
 *
 ******************************************************************************/


package es.cv.gvcase.mdt.common.sections;

import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Composite;

import es.cv.gvcase.mdt.common.sections.descriptions.ControlDescriptionDecoratorProvider;


public abstract class AbstractDoublePropertySection extends org.eclipse.emf.ecoretools.tabbedproperties.sections.AbstractDoublePropertySection{

	@Override
	protected EditingDomain getEditingDomain() {
		try {
			return super.getEditingDomain();
		} catch (IllegalArgumentException ex) {
			return null;
		}
	}
	
	@Override
	protected void createWidgets(Composite composite) {
		super.createWidgets(composite);
		// add control decorators that show the description of features
		ControlDescriptionDecoratorProvider.addDescriptionDecoration(getText(),
				getFeature());
	}

	/**
	 * @see org.eclipse.emf.ecoretools.tabbedproperties.sections.AbstractTabbedPropertySection#hookListeners()
	 */
	protected void hookListeners() {
		super.hookListeners();
		
		getText().addFocusListener(new FocusListener(){

			public void focusGained(FocusEvent e) {
				getText().setSelection(0, getText().getText().length());				
			}

			public void focusLost(FocusEvent e) {				
			}
			
		});

	}

}