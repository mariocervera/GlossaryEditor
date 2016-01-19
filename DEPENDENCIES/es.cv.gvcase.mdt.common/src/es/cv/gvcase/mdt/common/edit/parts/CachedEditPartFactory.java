/*******************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – Initial implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.edit.parts;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gmf.runtime.diagram.ui.editparts.GraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.parts.DiagramGraphicalViewer;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.FontStyle;
import org.eclipse.gmf.runtime.notation.NotationPackage;
import org.eclipse.gmf.runtime.notation.Style;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;

// TODO: Auto-generated Javadoc
/**
 * A factory for creating CachedEditPart objects.
 */
public abstract class CachedEditPartFactory implements EditPartFactory {

	/* (non-Javadoc)
	 * @see org.eclipse.gef.EditPartFactory#createEditPart(org.eclipse.gef.EditPart, java.lang.Object)
	 */
	public EditPart createEditPart(EditPart context, Object model) {

		if (model instanceof View && model instanceof Diagram == false) {
			View view = (View) model;
			EditPart editPart = CachedEditPartsRegistry.getEditPart(view);
			if (editPart != null) {
				editPart.getChildren().clear();
				if (editPart instanceof GraphicalEditPart) {
					Style style = view.getStyle(NotationPackage.eINSTANCE
							.getFontStyle());
					if (style instanceof FontStyle) {
						setEditPartFont((GraphicalEditPart) editPart,
								(FontStyle) style);
					}
				}
				return editPart;
			}
			editPart = createRealEditPart(context, model);
			if (editPart != null) {
				CachedEditPartsRegistry.putEditPart(view, editPart);
			}
			return editPart;
		}
		return null;
	}

	/**
	 * Sets the edit part font.
	 * 
	 * @param editPart the edit part
	 * @param style the style
	 */
	private static void setEditPartFont(GraphicalEditPart editPart,
			FontStyle style) {
		FontData fontData = new FontData(style.getFontName(), style
				.getFontHeight(), (style.isBold() ? SWT.BOLD : SWT.NORMAL)
				| (style.isItalic() ? SWT.ITALIC : SWT.NORMAL));
		Font newFont = getResourceManager(editPart).createFont(
				FontDescriptor.createFrom(fontData));
		editPart.getFigure().setFont(newFont);
	}

	/**
	 * Gets the resource manager.
	 * 
	 * @param editPart the edit part
	 * 
	 * @return the resource manager
	 */
	private static ResourceManager getResourceManager(EditPart editPart) {
		EditPartViewer viewer = editPart.getViewer();
		if (viewer instanceof DiagramGraphicalViewer) {
			return ((DiagramGraphicalViewer) viewer).getResourceManager();
		}
		return JFaceResources.getResources();
	}

	/**
	 * Creates a new CachedEditPart object.
	 * 
	 * @param editPart the edit part
	 * @param model the model
	 * 
	 * @return the edits the part
	 */
	protected abstract EditPart createRealEditPart(EditPart editPart,
			Object model);

}
