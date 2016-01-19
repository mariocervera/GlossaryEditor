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
package es.cv.gvcase.ide.core.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

// TODO: Auto-generated Javadoc
/**
 * The Class NewGvCASEProjectWizard.
 */
public class NewGvCASEProjectWizard extends BasicNewProjectResourceWizard implements INewWizard {

    /** The main page. */
    private WizardNewProjectCreationPage mainPage;
    
    /** The new project. */
    private IProject newProject;
    
    /** The workbench. */
    private IWorkbench workbench;
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.wizards.newresource.BasicNewResourceWizard#getWorkbench()
     */
    @Override
	public IWorkbench getWorkbench() {
    	return workbench;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard#addPages()
     */
    @Override
	public void addPages()
    {
    	
    	mainPage = new WizardNewProjectCreationPage("basicNewProjectPage");
    	mainPage.setTitle("Project");
    	mainPage.setDescription("Create a new project resource.");
        
    	addPage(mainPage);
    }
    
	/**
	 * Instantiates a new new gv case project wizard.
	 */
	public NewGvCASEProjectWizard() {
		super();
		setNeedsProgressMonitor(true);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
		this.workbench = workbench;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		String projectName = mainPage.getProjectName();
		
        // we don't want an empty project 
        if (projectName == null || projectName.equals(""))
        {
            return false;
        }

        IProject project = createProject(projectName);
        
        if (project != null)
        {
            this.newProject = project;
            
            try {
            	IProjectDescription description = project.getDescription();
                
            	String[] natures = description.getNatureIds();
                String[] newNatures = new String[natures.length + 1];
                System.arraycopy(natures, 0, newNatures, 0, natures.length);
                
                newNatures[natures.length] = "es.cv.gvcase.ide.core.gvCASEProjectNature";
                description.setNatureIds(newNatures);
                
                project.setDescription(description, null);

                selectAndReveal(project);
            }
            catch (CoreException e)
            {
            	e.printStackTrace(System.out);
            }
        }
        else
        {
            return false;
        }


        	updatePerspective();

        return true;
	}

    /**
     * Creates the project.
     * 
     * @param project the project
     * 
     * @return the i project
     */
    private IProject createProject(String project) {
    	if (newProject != null)
        {
            return newProject;
        }
        
    	IWorkspace workspace = ResourcesPlugin.getWorkspace();
        
        final IProject newProjectHandle = workspace.getRoot().getProject(project);
        
        try
        {
            // actually create the project 
            // and we don't want a progress monitor yet..
            newProjectHandle.create(null);
            newProjectHandle.open(null);
        }
        catch (CoreException ce)
        {
            ce.printStackTrace(System.out);
        }

        return newProjectHandle;
    }
}
