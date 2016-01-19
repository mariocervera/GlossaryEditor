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
package es.cv.gvcase.mdt.common.viewer.filters;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class FileFilter extends ViewerFilter {

	private final String[] extensions;
	private final boolean showHiddenFiles;

	/**
	 * A file filter depending on the passed extensions. If the array is empty
	 * or null, then all files are valid. Hidden files won't be shown anyway.
	 * 
	 * @param extensions
	 */
	public FileFilter(String[] extensions) {
		this(extensions, false);
	}

	/**
	 * A file filter depending on the passed extensions. If the array is empty
	 * or null, then all files are valid. Hidden files will be shown if
	 * showHiddenFiles is true, hidden if false.
	 * 
	 * @param extensions
	 * @param showHiddenFiles
	 */
	public FileFilter(String[] extensions, boolean showHiddenFiles) {
		this.extensions = extensions;
		this.showHiddenFiles = showHiddenFiles;
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof IProject) {
			return true;
		}

		if (element instanceof IFile) {
			IFile file = (IFile) element;

			if (!showHiddenFiles && file.getName().charAt(0) == '.') {
				// Do not show hidden files
				return false;
			}

			if (isValid(file)) {
				return true;
			} else {
				return false;
			}
		}
		return true;
	}

	private boolean isValid(IFile file) {
		// If no extensions, then every file is valid
		if (extensions == null || extensions.length == 0)
			return true;

		// If extensions, then filter
		String ext = file.getFileExtension();
		if (ext == null)
			return false;

		boolean valid = false;
		for (int i = 0; i < extensions.length; i++) {
			if (ext.compareToIgnoreCase(extensions[i]) == 0) {
				valid = true;
				break;
			}
		}
		return valid;
	}
}
