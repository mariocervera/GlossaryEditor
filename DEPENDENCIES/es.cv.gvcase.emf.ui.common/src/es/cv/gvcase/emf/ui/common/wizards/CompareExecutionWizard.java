/*******************************************************************************
 * Copyright (c) 2011 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana. All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *  Miguel Llacer (Prodevelop) [mllacer@prodevelop.es] - initial API implementation
 ******************************************************************************/
package es.cv.gvcase.emf.ui.common.wizards;

import java.util.Collections;
import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

import es.cv.gvcase.emf.ui.common.internal.Messages;
import es.cv.gvcase.emf.ui.common.pages.RequestCompareParametersPage;

public class CompareExecutionWizard extends Wizard {

	private URI leftModelURI;

	private RequestCompareParametersPage pageRequestParams;

	public CompareExecutionWizard(URI leftModelURI) {
		this.leftModelURI = leftModelURI;

		setWindowTitle(Messages.getString("CompareExecutionWizard.WindowTitle"));
	}

	@Override
	public void addPages() {
		int width = 640;
		int height = 640;
		getShell().setMinimumSize(width, height);

		pageRequestParams = new RequestCompareParametersPage(Messages
				.getString("RequestCompareParametersPage.PageName"),
				leftModelURI, null);
		addPage(pageRequestParams);

	}

	@Override
	public boolean canFinish() {
		return this.getPages()[this.getPages().length - 1].isPageComplete();
	}

	@Override
	public boolean performFinish() {

//		Resource left = pageRequestParams.getLeftResource();
//		Resource right = pageRequestParams.getRightResource();
//
//		if (left == null || right == null) {
//			MessageDialog
//					.openError(
//							getShell(),
//							"MOSKitt Compare",
//							"Differences model can't be created.\nPlease, verify that files selected are valid EMF models.");
//			return false;
//		}
//
//		DiffModel diff = null;
//
//		try {
//			diff = createDiffModel(left, right);
//		} catch (Exception e) {
//			MessageDialog.openError(getShell(), "MOSKitt Compare",
//					"Differences model can't be created.\n" + e.getMessage());
//			return false;
//		}
//
//		if (diff == null) {
//			MessageDialog
//					.openError(
//							getShell(),
//							"MOSKitt Compare",
//							"Differences model can't be created.\nPlease, verify that files selected are valid EMF models.");
//			return false;
//		}
//
//		String diffPath = pageRequestParams.getDiffResource();
//
//		if (!diffPath.endsWith(".emfdiff")) {
//			diffPath += ".emfdiff";
//		}
//
//		if (!saveModel(diff, diffPath)) {
//			MessageDialog.openError(getShell(), "MOSKitt Compare",
//					"Differences model can't be saved.");
//			return false;
//		}
//
//		if (diffPath.startsWith("platform:/resource")) {
//			// Open editor only if file is located in workspace
//			openDefaultEditor(diffPath.replace("platform:/resource", ""));
//		}

		return true;
	}

//	/**
//	 * Matches two models and creates the differences model.
//	 * 
//	 * @param left
//	 *            the new model
//	 * @param right
//	 *            the old model
//	 * 
//	 * @return the differences model
//	 */
//	private DiffModel createDiffModel(Resource left, Resource right)
//			throws InterruptedException {
//		final MatchModel match = MatchService.doResourceMatch(left, right,
//				new HashMap<String, Object>());
//		return DiffService.doDiff(match);
//	}

	/**
	 * Save differences model.
	 * 
	 * @param diff
	 *            the differences model
	 * @param path
	 *            the path to save
	 * 
	 * @return true, if successful
	 */
	private boolean saveModel(EObject diff, String path) {

		String project = null;

		if (path.startsWith("platform:/resource")) {
			// Obtains the workspace project
			project = path.replace("platform:/resource/", "");
			project = project.substring(0, project.indexOf("/"));
		}

		try {
			Resource r = new XMIResourceImpl();
			r.getContents().add(diff);
			diff.eResource().setURI(URI.createURI(path));

			// Save the differences model
			diff.eResource().save(Collections.EMPTY_MAP);

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		if (project != null) {
			try {
				// Refresh the workspace
				ResourcesPlugin.getWorkspace().getRoot().getProject(project)
						.refreshLocal(IResource.DEPTH_INFINITE, null);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}

		return true;
	}

	/**
	 * Open Differences model with Sample Reflective Ecore Model Editor.
	 * 
	 * @param diffPath
	 *            the differences path
	 */
	private void openDefaultEditor(String diffPath) {
		IFile diffFile = ResourcesPlugin.getWorkspace().getRoot().getFile(
				new Path(diffPath));

		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();

		try {
			page.openEditor(new FileEditorInput(diffFile),
					"org.eclipse.emf.ecore.presentation.ReflectiveEditorID");
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

}
