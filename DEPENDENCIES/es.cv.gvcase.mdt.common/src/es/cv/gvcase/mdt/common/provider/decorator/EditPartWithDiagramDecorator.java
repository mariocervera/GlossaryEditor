/*******************************************************************************
 * Copyright (c) 2010 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) - initial api implementation
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.provider.decorator;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.transaction.NotificationFilter;
import org.eclipse.emf.transaction.ResourceSetChangeEvent;
import org.eclipse.emf.transaction.ResourceSetListener;
import org.eclipse.emf.transaction.RollbackException;
import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.GraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ShapeEditPart;
import org.eclipse.gmf.runtime.diagram.ui.services.decorator.AbstractDecorator;
import org.eclipse.gmf.runtime.diagram.ui.services.decorator.IDecoratorTarget;
import org.eclipse.gmf.runtime.emf.core.resources.GMFResource;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.swt.graphics.Image;

import es.cv.gvcase.mdt.common.Activator;
import es.cv.gvcase.mdt.common.diagram.editparts.MOSKittGraphicalEditPart;
import es.cv.gvcase.mdt.common.util.MultiDiagramUtil;

/**
 * Decorated an {@link EditPart} if it represents an element with one or more
 * {@link Diagram}s associated.
 * 
 * @author <a href="mailto:fjcano@prodevleop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public class EditPartWithDiagramDecorator extends AbstractDecorator {

	public EditPartWithDiagramDecorator(IDecoratorTarget decoratorTarget) {
		super(decoratorTarget);
	}

	private static final String icon = "icons/Diagram.png";

	public void refresh() {
		removeDecoration();
		GraphicalEditPart editPart = (GraphicalEditPart) getDecoratorTarget()
				.getAdapter(GraphicalEditPart.class);
		if (editPart instanceof MOSKittGraphicalEditPart) {
			MOSKittGraphicalEditPart mgep = (MOSKittGraphicalEditPart) editPart;
			if (!mgep.acceptDiagramDecoration()) {
				return;
			}
		}

		if (!(editPart instanceof ShapeEditPart)) {
			return;
		}

		if (!MultiDiagramUtil.getDiagramsAssociatedToEditPart(editPart)
				.isEmpty()) {
			Image image = Activator.getDefault().getBundledImage(icon);
			setDecoration(getDecoratorTarget().addShapeDecoration(image,
					IDecoratorTarget.Direction.NORTH_WEST, 0, true));
		}
	}

	private final ResourceSetListener resourceSetListener = new ResourceSetListener() {
		/**
		 * Filter of the ResourceSetListener. We will only listen to changes on
		 * specific features.
		 */
		public NotificationFilter getFilter() {
			return null;
		}

		/**
		 * By default, assume that we want individual transaction pre-commit.
		 */
		public boolean isAggregatePrecommitListener() {
			return false;
		}

		/**
		 * By default, assume that we do not only want post-commit events but
		 * also pre-commit events.
		 */
		public boolean isPostcommitOnly() {
			return false;
		}

		/**
		 * By default, assume that we do not only want pre-commit events but
		 * also post-commit events.
		 */
		public boolean isPrecommitOnly() {
			return false;
		}

		/**
		 * When a change if made to the listened feature, then refresh the
		 * decorator.
		 */
		public void resourceSetChanged(ResourceSetChangeEvent event) {
			for (Notification notification : event.getNotifications()) {
				if (notification.getNotifier() instanceof GMFResource
						&& (notification.getNewValue() instanceof Diagram || notification
								.getOldValue() instanceof Diagram)) {
					refresh();
				}
			}
		}

		/**
		 * The default implementation of this method does nothing, returning no
		 * trigger command.
		 */
		public Command transactionAboutToCommit(ResourceSetChangeEvent event)
				throws RollbackException {

			return null;
		}

	};

	/**
	 * Adds decoration if applicable.
	 */
	public void activate() {
		IGraphicalEditPart gep = (IGraphicalEditPart) getDecoratorTarget()
				.getAdapter(IGraphicalEditPart.class);
		if (gep != null) {
			gep.getEditingDomain().addResourceSetListener(resourceSetListener);
			refresh();
		}
	}

	/**
	 * Removes the decoration.
	 */
	@Override
	public void deactivate() {
		removeDecoration();

		IGraphicalEditPart gep = (IGraphicalEditPart) getDecoratorTarget()
				.getAdapter(IGraphicalEditPart.class);
		if (gep != null) {
			gep.getEditingDomain().removeResourceSetListener(
					resourceSetListener);
		}
	}

}
