/*******************************************************************************
 * Copyright (c) 2010 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Miguel Llacer San Fernando (Prodevelop) - Initial implementation
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.viewer.filters;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.Viewer;

/**
 * A filter that filters the folders and files contained in a given project.
 * 
 * @author mllacer
 * 
 */
public class FolderProjectFilter extends FileFilter {

	private final IProject project;

	public FolderProjectFilter(String[] extensions, IProject project) {
		super(extensions);
		this.project = project;
	}

	public FolderProjectFilter(String[] extensions, boolean showHiddenFiles,
			IProject project) {
		super(extensions, showHiddenFiles);
		this.project = project;
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof IProject && element != getProject()) {
			return false;
		}
		else if (element instanceof IFolder) {
			return true;
		}
		else {
			return super.select(viewer, parentElement, element);
		}
	}

	public IProject getProject() {
		return project;
	}
}
