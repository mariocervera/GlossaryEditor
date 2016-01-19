/*******************************************************************************
 * Copyright (c) 2009 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – Initial implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.part;

import org.eclipse.gmf.runtime.common.ui.services.action.contributionitem.ContributionItemService;
import org.eclipse.gmf.runtime.common.ui.util.IWorkbenchPartDescriptor;
import org.eclipse.gmf.runtime.common.ui.util.WorkbenchPartDescriptor;
import org.eclipse.gmf.runtime.diagram.ui.parts.DiagramActionBarContributor;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.IEditorPart;

/**
 * An {@link IEditorActionBarContributor} for a {@link MOSKittMultiPageEditor}
 * that has un update() method available to the public. <br>
 * A single instance of this ActionBarContributor will be shared by all the
 * editors in a MOSKittMultiPageEditor. This ActionBarContributor will change
 * its editorClass and editorID to contribute to the actual nested active
 * editor.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public class MOSKittActionBarContributor extends DiagramActionBarContributor {

	// IWorkbenchPartDescriptor to perform action bars updates
	private WorkbenchPartDescriptor descriptor = null;

	/**
	 * Make the {@link IWorkbenchPartDescriptor} available to others.
	 * 
	 * @return
	 */
	protected WorkbenchPartDescriptor getDescriptor() {
		return descriptor;
	}

	@Override
	public void init(IActionBars bars) {
		super.init(bars);
		// store the IWorkbenchPartDescriptor for later use
		descriptor = new WorkbenchPartDescriptor(getEditorId(),
				getEditorClass(), getPage());
	}

	public void update() {
		// get the new contributions
		ContributionItemService.getInstance().updateActionBars(getActionBars(),
				descriptor);
		// refresh the UI
		getActionBars().updateActionBars();
	}

	// //
	// Editor Class
	// //

	/**
	 * The editor class of this ActionBarContributor.
	 */
	protected Class editorClass = null;

	/**
	 * Gets the editor class to which this ActionBarContributor is contributing
	 * currently.
	 */
	@Override
	protected Class getEditorClass() {
		return editorClass;
	}

	/**
	 * Sets the editor class to which this ActionBarContributor should
	 * contribute.
	 * 
	 * @param editorClass
	 */
	public void setEditorClass(Class editorClass) {
		this.editorClass = editorClass;
	}

	// //
	// Editor ID
	// //

	/**
	 * The editor identifier of the contributing action bar.
	 */
	protected String editorID = null;

	/**
	 * Gets the editor identifier this ActionBarContributor contributes to.
	 */
	@Override
	protected String getEditorId() {
		return editorID;
	}

	/**
	 * Sets the editor identifier.
	 * 
	 * @param editorID
	 */
	public void setEditorID(String editorID) {
		this.editorID = editorID;
	}
	
	// //
	// Active editor
	// //
	
	protected IEditorPart activeEditor = null;
	
	/**
	 * Store the activeEditor
	 */
	@Override
	public void setActiveEditor(IEditorPart editor) {
		this.activeEditor = editor;
		super.setActiveEditor(editor);
	}
	
	/**
	 * Retrieves the active editor.
	 * @return
	 */
	public IEditorPart getActiveEditor() {
		return activeEditor;
	}
}
