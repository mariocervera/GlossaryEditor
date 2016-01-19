/*******************************************************************************
 * Copyright (c) 2009, 2010 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – Initial implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.part;

import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.IEditorPart;

/**
 * An extension of {@link MultiPageEditorSite} to include an
 * {@link IMOSKittMultiPageEditorContext}. This editor uses a contributor that
 * delegates in the nested editor.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public class MOSKittMultiPageEditorSite extends MultiPageEditorSite {

	/**
	 * The context that has the document provider and the editing domain.
	 */
	IMOSKittMultiPageEditorContext editorContext = null;

	/**
	 * Constructor.
	 * 
	 * @param multiPageEditor
	 * @param editorPart
	 */
	public MOSKittMultiPageEditorSite(MOSKittMultiPageEditor multiPageEditor,
			IEditorPart editorPart) {
		this(multiPageEditor, editorPart, (IMOSKittMultiPageEditorContext) null);
	}

	/**
	 * Constructor.
	 * 
	 * @param multiPageEditor
	 * @param editorPart
	 * @param context
	 */
	public MOSKittMultiPageEditorSite(MOSKittMultiPageEditor multiPageEditor,
			IEditorPart editorPart, IMOSKittMultiPageEditorContext context) {
		super(multiPageEditor, editorPart);
		this.editorContext = context;
	}

	/**
	 * Constructor.
	 * 
	 * @param multiPageEditor
	 * @param editorPart
	 * @param context
	 */
	public MOSKittMultiPageEditorSite(MOSKittMultiPageEditor multiPageEditor,
			IEditorPart editorPart, IMOSKittMultiPageEditorContext context,
			IEditorActionBarContributor actionBarContributor) {
		super(multiPageEditor, editorPart);
		this.editorContext = context;
		this.actionBarcontributor = actionBarContributor instanceof MOSKittMultiPageEditorActionBarContributor ? (MOSKittMultiPageEditorActionBarContributor) actionBarContributor
				: null;
	}

	/**
	 * Gets this editor context with the editing domain and the document
	 * provider.
	 * 
	 * @return
	 */
	public IMOSKittMultiPageEditorContext getEditorContext() {
		return editorContext;
	}

	/**
	 * Return the id of the active editor in the {@link MOSKittMultiPageEditor}.
	 */
	@Override
	public String getId() {
		IEditorPart editorPart = getEditor();
		if (editorPart instanceof CachedResourcesDiagramEditor) {
			return ((CachedResourcesDiagramEditor) editorPart).getEditorID();
		} else {
			return null;
		}
	}

	/**
	 * The action bar contributor that can delegate in the active editor.
	 */
	private MOSKittMultiPageEditorActionBarContributor actionBarcontributor = null;

	/**
	 * Returns an implementation of IEditorActionBarContributor that can provide
	 * the ActionBar for the active editor.
	 */
	@Override
	public MOSKittMultiPageEditorActionBarContributor getActionBarContributor() {
		return actionBarcontributor;
	}

	public void setActionBarcontributor(
			MOSKittMultiPageEditorActionBarContributor actionBarcontributor) {
		this.actionBarcontributor = actionBarcontributor;
	}

}
