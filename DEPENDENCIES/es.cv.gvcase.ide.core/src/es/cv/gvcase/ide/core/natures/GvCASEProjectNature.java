/*******************************************************************************
 * Copyright (c) 2007 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana. All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Emilio Sanchez (Integranova) � Initial implementation
 *
 ******************************************************************************/
package es.cv.gvcase.ide.core.natures;

import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IProject;

// TODO: Auto-generated Javadoc
/**
 * The Class GvCASEProjectNature.
 */
public class GvCASEProjectNature implements IProjectNature{
    
    /** The project. */
    private IProject project;
    
	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IProjectNature#deconfigure()
	 */
	public void deconfigure() {
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IProjectNature#configure()
	 */
	public void configure() {
		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IProjectNature#getProject()
	 */
	public IProject getProject() {
		return this.project;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IProjectNature#setProject(org.eclipse.core.resources.IProject)
	 */
	public void setProject(IProject p) {
		this.project = p;
	}
}
