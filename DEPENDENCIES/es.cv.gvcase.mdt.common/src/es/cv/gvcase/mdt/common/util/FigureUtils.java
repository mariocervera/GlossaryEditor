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
package es.cv.gvcase.mdt.common.util;

import org.eclipse.draw2d.IFigure;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.draw2d.ui.figures.FigureUtilities;
import org.eclipse.gmf.runtime.notation.FillStyle;
import org.eclipse.gmf.runtime.notation.LineStyle;
import org.eclipse.gmf.runtime.notation.Node;
import org.eclipse.gmf.runtime.notation.NotationFactory;
import org.eclipse.gmf.runtime.notation.NotationPackage;
import org.eclipse.gmf.runtime.notation.Style;
import org.eclipse.gmf.runtime.notation.View;

import es.cv.gvcase.mdt.common.diagram.editparts.ISemanticFigure;

/**
 * Holder class for utility methods regarding figures in EditParts.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public class FigureUtils {

	/**
	 * Notifies the figures in the {@link IGraphicalEditPart} of the changes
	 * produced in {@link Notification}.
	 * 
	 * @param editPart
	 * @param notification
	 */
	public static void notifySemanticFigures(IGraphicalEditPart editPart,
			Notification notification) {
		IFigure figure = editPart.getFigure();
		notifyFigure(figure, notification);
	}

	/**
	 * Notifies an {@link ISemanticFigure} figure and its
	 * {@link ISemanticFigure} children of the changes produced by
	 * {@link Notification}.
	 * 
	 * @param figure
	 * @param notification
	 */
	public static void notifyFigure(IFigure figure, Notification notification) {
		if (figure instanceof ISemanticFigure) {
			((ISemanticFigure) figure).handleNotificationEvent(notification);
		}
		if (figure.getChildren() != null && figure.getChildren().size() > 0) {
			for (Object object : figure.getChildren()) {
				if (object instanceof IFigure) {
					notifyFigure((IFigure) object, notification);
				}
			}
		}
	}

	// //
	// FillStyle and LineStyle checking and completion.
	// //

	public static void reviewStylesOfEditPart(IGraphicalEditPart editPart) {
		if (editPart != null && editPart.getNotationView() instanceof Node) {
			if (!checkFillStyleExists(editPart.getNotationView())) {
				addFillStyleToNode(editPart);
			}
			if (!checkLineStyleExists(editPart.getNotationView())) {
				addLineStyleToNode(editPart);
			}
		}
	}

	public static boolean checkFillStyleExists(View view) {
		Node node = null;
		if (view instanceof Node) {
			node = (Node) view;
			Style style = node.getStyle(NotationPackage.eINSTANCE
					.getFillStyle());
			return style instanceof FillStyle;
		}
		return false;
	}

	public static boolean checkLineStyleExists(View view) {
		Node node = null;
		if (view instanceof Node) {
			node = (Node) view;
			Style style = node.getStyle(NotationPackage.eINSTANCE
					.getLineStyle());
			return style instanceof LineStyle;
		}
		return false;
	}

	public static void addFillStyleToNode(IGraphicalEditPart editPart) {
		Node node = null;
		IFigure figure = null;
		if (editPart != null && editPart.getNotationView() instanceof Node
				&& editPart.getFigure() != null) {
			figure = editPart.getFigure();
			int figureColor = FigureUtilities.colorToInteger(figure
					.getBackgroundColor());
			node = (Node) editPart.getNotationView();
			FillStyle fillStyle = NotationFactory.eINSTANCE.createFillStyle();
			fillStyle.setFillColor(figureColor);
			node.getStyles().add(fillStyle);
		}
	}

	public static void addLineStyleToNode(IGraphicalEditPart editPart) {
		Node node = null;
		IFigure figure = null;
		if (editPart != null && editPart.getNotationView() instanceof Node
				&& editPart.getFigure() != null) {
			figure = editPart.getFigure();
			int figureColor = FigureUtilities.colorToInteger(figure
					.getForegroundColor());
			node = (Node) editPart.getNotationView();
			LineStyle lineStyle = NotationFactory.eINSTANCE.createLineStyle();
			lineStyle.setLineColor(figureColor);
			node.getStyles().add(lineStyle);
		}
	}

}
