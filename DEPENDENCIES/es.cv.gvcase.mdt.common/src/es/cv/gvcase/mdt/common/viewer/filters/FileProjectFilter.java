/*******************************************************************************
 * Copyright (c) 2009 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Gabriel Merin Cubero (Prodevelop) - Initial implementation
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.viewer.filters;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.Viewer;

/**
 * A filter that filters the files contained in a given project.
 * 
 * @author gmerin
 * 
 */
public class FileProjectFilter extends FileFilter {

	private final IProject project;

	public FileProjectFilter(String[] extensions, IProject project) {
		super(extensions);
		this.project = project;
	}

	public FileProjectFilter(String[] extensions, boolean showHiddenFiles,
			IProject project) {
		super(extensions, showHiddenFiles);
		this.project = project;
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof IProject && element != getProject())
			return false;
		else
			return super.select(viewer, parentElement, element);
	}

	public IProject getProject() {
		return project;
	}
}
