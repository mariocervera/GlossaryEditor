/*******************************************************************************
 * Copyright (c) 2010 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana. All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Integranova) - initial API implementation
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.composites;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.swt.widgets.Composite;

/**
 * A provider that can provide {@link DetailComposite}s for given elements.
 * 
 * @author <a href="mailto:fjcano@prodeelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public interface IDetailsCompositeProvider {

	DetailComposite provideFor(Object object, Composite parent, int style,
			EObject eObject, EditingDomain domain);

	boolean providesFor(Object object);

}
