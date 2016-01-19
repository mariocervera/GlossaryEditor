/*******************************************************************************
 * Copyright (c) 2007 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana. All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Emilio Sanchez (Integranova) � Initial implementation
 * [03/04/08] Francisco Javier Cano Muñoz (Prodevelop) - changed perspective layout
 *
 ******************************************************************************/
package es.cv.gvcase.ide.perspectives;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class GvCASEPerspective.
 */
public class GvCASEPerspective implements IPerspectiveFactory {

	/** The Constant ID. */
	public static final String ID = "es.cv.gvcase.ide.core.perspective";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui
	 * .IPageLayout)
	 */
	public void createInitialLayout(IPageLayout layout) {
		defineActions(layout);
		defineLayout(layout);
	}

	/**
	 * Define actions.
	 * 
	 * @param layout
	 *            the layout
	 */
	private void defineActions(IPageLayout layout) {

		// Add gvCASE Menu
		layout.addActionSet("es.cv.gvcase.ide.core.gvCASEActionSet");

		// Add File New Item
		layout.addNewWizardShortcut("es.cv.gvcase.ide.core.newprojectwizard");
		layout
				.addNewWizardShortcut("es.cv.gvcase.mdt.uml2.diagram.clazz.part.UMLCreationWizardID");
		layout
				.addNewWizardShortcut("es.cv.gvcase.mdt.uml2.diagram.profile.part.UMLCreationWizardID");
		layout
				.addNewWizardShortcut("es.cv.gvcase.mdt.db.diagram.part.SqlmodelCreationWizardID");
		layout
				.addNewWizardShortcut("es.cv.gvcase.mdt.bpmn.diagram.part.BpmnCreationWizardID");
		layout
				.addNewWizardShortcut("es.cv.gvcase.mdt.dashboard.diagram.part.DashboardCreationWizardID");
		layout
				.addNewWizardShortcut("es.cv.gvcase.mdt.sketcher.common.part.SketcherCreationWizard");
		layout
				.addNewWizardShortcut("es.cv.gvcase.mdt.uim.common.part.UimCreationWizard");
		layout
				.addNewWizardShortcut("es.cv.gvcase.mdt.wbs.diagram.part.WbsCreationWizardID");

		// Add "show views".
		layout
				.addShowViewShortcut("es.cv.gvcase.ide.navigator.view.modelViewer");
		layout
				.addShowViewShortcut("es.cv.gvcase.ide.navigator.view.resourceViewer");
		layout.addShowViewShortcut("org.eclipse.ui.navigator.ProjectExplorer");
		layout
				.addShowViewShortcut("es.cv.gvcase.trmanager.ui.views.TransformationsView");
		layout.addShowViewShortcut("org.eclipse.jdt.ui.ProjectsView");
		layout.addShowViewShortcut("org.eclipse.ui.views.PropertySheet");
		layout.addShowViewShortcut("org.eclipse.ui.console.ConsoleView");
		layout.addShowViewShortcut("org.eclipse.ui.views.ProblemView");
		layout.addShowViewShortcut("org.eclipse.help.ui.HelpView");
		layout
				.addShowViewShortcut("org.eclipse.ui.cheatsheets.views.CheatSheetView");

		// Add Perspectives
		layout.addPerspectiveShortcut("es.cv.gvcase.ide.core.perspective");
		layout.addPerspectiveShortcut("org.eclipse.jdt.ui.JavaPerspective");
	}

	/**
	 * Define layout.
	 * 
	 * @param layout
	 *            the layout
	 */
	private void defineLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();

		// Place navigator and outline to left of
		// editor area.
		IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT,
				0.2f, editorArea);
		left.addView("es.cv.gvcase.ide.navigator.resourceView");
		left.addPlaceholder("es.cv.gvcase.ide.navigator.resourceView");

		IFolderLayout bottom = layout.createFolder("bottom",
				IPageLayout.BOTTOM, 0.75f, editorArea);
		bottom.addView("es.cv.gvcase.mdt.dashboard.test.DashboardPart");
		bottom.addView(IPageLayout.ID_PROP_SHEET);
		bottom.addView(IPageLayout.ID_PROBLEM_VIEW);
		bottom.addPlaceholder("*");

		IFolderLayout leftBottom = layout.createFolder("leftBottom",
				IPageLayout.BOTTOM, 0.8f, "left");
		leftBottom.addView(IPageLayout.ID_OUTLINE);
		leftBottom.addPlaceholder(IPageLayout.ID_OUTLINE);

		IFolderLayout leftMiddle = layout.createFolder("leftMiddle",
				IPageLayout.BOTTOM, 0.5f, "left");
		leftMiddle.addView("es.cv.gvcase.ide.navigator.modelView");
		leftMiddle.addPlaceholder("es.cv.gvcase.ide.navigator.modelView");

	}
}
