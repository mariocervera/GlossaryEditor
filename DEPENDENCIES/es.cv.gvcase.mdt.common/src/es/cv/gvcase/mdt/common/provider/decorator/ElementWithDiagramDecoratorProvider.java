/*******************************************************************************
 * Copyright (c) 2010 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) - initial implementation
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.provider.decorator;

import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.common.core.service.AbstractProvider;
import org.eclipse.gmf.runtime.common.core.service.IOperation;
import org.eclipse.gmf.runtime.diagram.ui.editparts.GraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.services.decorator.CreateDecoratorsOperation;
import org.eclipse.gmf.runtime.diagram.ui.services.decorator.IDecoratorProvider;
import org.eclipse.gmf.runtime.diagram.ui.services.decorator.IDecoratorTarget;
import org.eclipse.gmf.runtime.notation.Diagram;

/**
 * Decorates all {@link EditPart} that represent an element that has an
 * associated {@link Diagram}.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public class ElementWithDiagramDecoratorProvider extends AbstractProvider
		implements IDecoratorProvider {

	public static final String EDITPART_WITH_DIAGRAMS = "EditPartWithDiagrams";

	/**
	 * Creates a {@link EditPartWithDiagramDecorator} on the target.
	 */
	public void createDecorators(IDecoratorTarget decoratorTarget) {
		GraphicalEditPart editPart = (GraphicalEditPart) decoratorTarget
				.getAdapter(GraphicalEditPart.class);
		if (editPart != null) {
			decoratorTarget.installDecorator(EDITPART_WITH_DIAGRAMS,
					new EditPartWithDiagramDecorator(decoratorTarget));
		}
	}

	/**
	 * Provides decoration to different edit parts.
	 */
	public boolean provides(IOperation operation) {
		if (!(operation instanceof CreateDecoratorsOperation)) {
			return false;
		}
		IDecoratorTarget decoratorTarget = ((CreateDecoratorsOperation) operation)
				.getDecoratorTarget();
		return decoratorTarget.getAdapter(EditPart.class) != null;
	}

}
