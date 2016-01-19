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
package es.cv.gvcase.mdt.common.part;

import org.eclipse.emf.common.ui.URIEditorInput;
import org.eclipse.emf.common.util.URI;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;

import es.cv.gvcase.emf.common.util.PathsUtil;
import es.cv.gvcase.mdt.common.util.MDTUtil;

// TODO: Auto-generated Javadoc
/**
 * The Class CachedResourcesEditorInput.
 */
public class CachedResourcesEditorInput extends URIEditorInput {

	/** The unload. */
	private boolean unload = true;

	/**
	 * Flag to open in newEditor.
	 */
	private boolean openInNewEditor = false;

	/**
	 * Flag to check if diagram is migrated
	 */
	private boolean migrated = false;

	/**
	 * Instantiates a new cached resources editor input.
	 * 
	 * @param uri
	 *            the uri
	 */
	public CachedResourcesEditorInput(URI uri) {
		super(uri);
	}

	/**
	 * Instantiates a new cached resources editor input.
	 * 
	 * @param uri
	 *            the uri
	 * @param name
	 *            the name
	 */
	public CachedResourcesEditorInput(URI uri, String name) {
		super(uri, name);
	}

	/**
	 * Instantiates a new cached resources editor input.
	 * 
	 * @param uri
	 *            the uri
	 * @param name
	 *            the name
	 * @param unload
	 *            the unload
	 */
	public CachedResourcesEditorInput(URI uri, String name, boolean unload) {
		this(uri, name, unload, false);
	}

	/**
	 * Instantiates a new cached resources editor input.
	 * 
	 * @param uri
	 *            the uri
	 * @param unload
	 *            the unload
	 */
	public CachedResourcesEditorInput(URI uri, boolean unload) {
		this(uri, null, unload, false);
	}

	public CachedResourcesEditorInput(URI uri, String name, boolean unload,
			boolean openInNewEditor) {
		super(uri, name);
		this.unload = unload;
		this.openInNewEditor = openInNewEditor;
	}

	/**
	 * Sets the unload.
	 * 
	 * @param unload
	 *            the new unload
	 */
	public void setUnload(boolean unload) {
		this.unload = unload;
	}

	/**
	 * Checks if is unload.
	 * 
	 * @return true, if is unload
	 */
	public boolean isUnload() {
		return unload;
	}

	/**
	 * Checks if the editor has been migrated
	 * 
	 * @return
	 */
	public boolean isMigrated() {
		return migrated;
	}
	
	public void setMigrated(boolean migrated) {
		this.migrated = migrated;
	}

	public void setOpenInNewEditor(boolean openInNewEditor) {
		this.openInNewEditor = openInNewEditor;
	}

	public boolean isOpenInNewEditor() {
		return openInNewEditor;
	}

	/**
	 * Check same editor input.
	 * 
	 * @param input1
	 *            the input1
	 * @param input2
	 *            the input2
	 * 
	 * @return true, if successful
	 */
	public static boolean checkSameEditorInput(IEditorInput input1,
			IEditorInput input2) {
		String uri1 = "", uri2 = "";
		uri1 = PathsUtil.getRelativeWorkspaceFromEditorInput(input1, false);
		uri2 = PathsUtil.getRelativeWorkspaceFromEditorInput(input2, false);
		if (uri1.equals(uri2)) {
			return true;
		}
		return false;
	}

	public static boolean checkSameEditorInputWithFragment(IEditorInput input1,
			IEditorInput input2) {
		String uri1 = "", uri2 = "";
		uri1 = PathsUtil.getRelativeWorkspaceFromEditorInputWithFragment(
				input1, false);
		uri2 = PathsUtil.getRelativeWorkspaceFromEditorInputWithFragment(
				input2, false);
		if (uri1.equals(uri2)) {
			return true;
		}
		return false;
	}

	/**
	 * To platform resource. Removes <URI>'s fragments.
	 * 
	 * @param input
	 *            the input
	 * 
	 * @return the string
	 */
	public static String toPlatformResource(IEditorInput input) {
		String uri = "";
		if (input instanceof FileEditorInput) {
			uri = ((FileEditorInput) input).getURI().toString();
		}
		if (input instanceof URIEditorInput) {
			uri = ((URIEditorInput) input).getURI().trimFragment().toString();
		}

		if (uri.startsWith("platform:/resource")) {
			return uri;
		}

		if (uri.startsWith("file:")) {
			uri = uri.replaceFirst("file:", "");
		}
		if (uri.startsWith(MDTUtil.getWorkspaceLocation().toString())) {
			uri = uri.replaceFirst(MDTUtil.getWorkspaceLocation().toString(),
					"");
			return "platform:/resource" + uri;
		}

		return uri;
	}

	/**
	 * To platform resource. Does not remove <URI>'s fragments.
	 * 
	 * @param input
	 *            the input
	 * 
	 * @return the string
	 */
	public static String toPlatformResourceWithFragment(IEditorInput input) {
		String uri = "";
		if (input instanceof FileEditorInput) {
			uri = ((FileEditorInput) input).getURI().toString();
		}
		if (input instanceof URIEditorInput) {
			uri = ((URIEditorInput) input).getURI().toString();
		}

		if (uri.startsWith("platform:/resource")) {
			return uri;
		}

		if (uri.startsWith("file:")) {
			uri = uri.replaceFirst("file:", "");
		}
		if (uri.startsWith(MDTUtil.getWorkspaceLocation().toString())) {
			uri = uri.replaceFirst(MDTUtil.getWorkspaceLocation().toString(),
					"");
			return "platform:/resource" + uri;
		}

		return uri;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.common.ui.URIEditorInput#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof IEditorInput) {
			return checkSameEditorInput(this, (IEditorInput) o);
		}
		return false;
	}

}
