/*******************************************************************************
 * Copyright (c) 2007 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Gabriel Merin Cubero (Prodevelop) – initial API and
 * implementation
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.sections;

import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.common.core.command.CompositeCommand;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.properties.sections.appearance.ShapeColorsAndFontsPropertySection;
import org.eclipse.gmf.runtime.notation.NotationPackage;
import org.eclipse.swt.events.SelectionEvent;

import es.cv.gvcase.mdt.common.commands.AnnotateNodeStyleCommand;
import es.cv.gvcase.mdt.common.commands.wrappers.GMFtoEMFCommandWrapper;

/**
 * Extended ShapeColorsAndFontsPropertySection that modifies the default
 * behavior of the command's execution in order to add to the EditPart's Node
 * the styles changes.
 * 
 * @author gmerin
 * 
 */
public class ShapeColorsAndFontsPropertySectionEx extends
		ShapeColorsAndFontsPropertySection {

	// The EAttribute that represents the changed style
	private EAttribute changedStyle;

	@Override
	protected TransactionalEditingDomain getEditingDomain() {
		try {
			return super.getEditingDomain();
		} catch (IllegalArgumentException ex) {
			return null;
		}
	}

	@Override
	protected CommandResult executeAsCompositeCommand(String actionName,
			List commands) {

		// Iterate through all the selected editParts
		Iterator it = getInputIterator();
		while (it.hasNext()) {
			IGraphicalEditPart ep = (IGraphicalEditPart) it.next();
			commands.add(new AnnotateNodeStyleCommand(ep, getChangedStyle(),
					getEditingDomain(), null));
		}

		// Build the composite command
		CompositeCommand compositeCommand = new CompositeCommand(actionName,
				commands);

		// Transform GMF command to EMF command to executed in the editing
		// domain
		Command command = new GMFtoEMFCommandWrapper(compositeCommand);

		// Execute the command within the diagram editor editing domain
		getEditingDomain().getCommandStack().execute(command);

		// Return the command execution result
		return compositeCommand.getCommandResult();
	}

	@Override
	protected void changeFillColor(SelectionEvent event) {
		setChangedStyle(NotationPackage.eINSTANCE.getFillStyle_FillColor());
		super.changeFillColor(event);
	}

	@Override
	protected void changeLineColor(SelectionEvent event) {
		setChangedStyle(NotationPackage.eINSTANCE.getLineStyle_LineColor());
		super.changeLineColor(event);
	}

	@Override
	protected void changeFontColor(SelectionEvent event) {
		setChangedStyle(NotationPackage.eINSTANCE.getFontStyle_FontColor());
		super.changeFontColor(event);
	}

	@Override
	protected void updateFontBold() {
		setChangedStyle(NotationPackage.eINSTANCE.getFontStyle_Bold());
		super.updateFontBold();
	}

	@Override
	protected void updateFontFamily() {
		setChangedStyle(NotationPackage.eINSTANCE.getFontStyle_FontName());
		super.updateFontFamily();
	}

	@Override
	protected void updateFontItalic() {
		setChangedStyle(NotationPackage.eINSTANCE.getFontStyle_FontName());
		super.updateFontItalic();
	}

	@Override
	protected void updateFontSize() {
		setChangedStyle(NotationPackage.eINSTANCE.getFontStyle_FontHeight());
		super.updateFontSize();
	}

	/**
	 * Set the new style
	 * 
	 * @param changedStyle
	 */
	public void setChangedStyle(EAttribute changedStyle) {
		this.changedStyle = changedStyle;
	}

	/**
	 * Get changed style
	 * 
	 * @return changedStyle
	 */
	public EAttribute getChangedStyle() {
		return changedStyle;
	}

}
