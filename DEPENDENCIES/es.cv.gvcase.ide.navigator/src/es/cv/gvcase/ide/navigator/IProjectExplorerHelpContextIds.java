/*******************************************************************************
 * Copyright (c) 2007 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Emilio Sánchez (Integranova) – Initial API 
 * implementation.
 *
 ******************************************************************************/

package es.cv.gvcase.ide.navigator;

import org.eclipse.ui.PlatformUI;

// TODO: Auto-generated Javadoc
/**
 * The Interface IProjectExplorerHelpContextIds.
 */
public interface IProjectExplorerHelpContextIds {
	    
    	/** The Constant PREFIX. */
    	public static final String PREFIX = PlatformUI.PLUGIN_ID + "."; 

	    // Actions
	    /** The Constant FILTER_SELECTION_ACTION. */
    	public static final String FILTER_SELECTION_ACTION = PREFIX
	            + "filter_selection_action_context"; 

	    /** The Constant GOTO_RESOURCE_ACTION. */
    	public static final String GOTO_RESOURCE_ACTION = PREFIX
	            + "goto_resource_action_context"; 

	    /** The Constant RESOURCE_NAVIGATOR_MOVE_ACTION. */
    	public static final String RESOURCE_NAVIGATOR_MOVE_ACTION = PREFIX
	            + "resource_navigator_move_action_context";

	    /** The Constant RESOURCE_NAVIGATOR_RENAME_ACTION. */
    	public static final String RESOURCE_NAVIGATOR_RENAME_ACTION = PREFIX
	            + "resource_navigator_rename_action_context";

	    /** The Constant SHOW_IN_NAVIGATOR_ACTION. */
    	public static final String SHOW_IN_NAVIGATOR_ACTION = PREFIX
	            + "show_in_navigator_action_context"; 

	    /** The Constant SORT_VIEW_ACTION. */
    	public static final String SORT_VIEW_ACTION = PREFIX
	            + "sort_view_action_context"; 

	    /** The Constant COPY_ACTION. */
    	public static final String COPY_ACTION = PREFIX
	            + "resource_navigator_copy_action_context";

	    /** The Constant PASTE_ACTION. */
    	public static final String PASTE_ACTION = PREFIX
	            + "resource_navigator_paste_action_context";

	    /** The Constant COLLAPSE_ALL_ACTION. */
    	public static final String COLLAPSE_ALL_ACTION = PREFIX
	            + "collapse_all_action_context";

	    // Dialogs
	    /** The Constant GOTO_RESOURCE_DIALOG. */
    	public static final String GOTO_RESOURCE_DIALOG = PREFIX
	            + "goto_resource_dialog_context";

	    // Views
	    /** The Constant RESOURCE_VIEW. */
    	public static final String RESOURCE_VIEW = PREFIX + "resource_view_context"; 
	}
