/*******************************************************************************
 * Copyright (c) 2010 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana. All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Marc Gil Sendra (Prodevelop) - initial API implementation
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.sections;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.views.properties.PropertySheet;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import es.cv.gvcase.mdt.common.part.MOSKittMultiPageEditor;

/**
 * A Class with useful methods to work with the Property Sheets
 * 
 * @author mgil
 */
public class PropertySheetUtils {

	/**
	 * This method provides a simple way to refresh all the selected Tab,
	 * refreshing all the sections contained in this Tab.
	 * 
	 * As the sections couldn't refresh the parent Tab directly or refresh the
	 * sibling Sections, the only way to do that is by simulating a deselect and
	 * select the current element in the current working editor
	 * 
	 * @param section
	 * @param eObject
	 */
	public static void refreshTab(AbstractPropertySection section,
			EObject eObject) {
		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();

		if (window == null) {
			return;
		}
		IWorkbenchPage page = window.getActivePage();

		if (page == null) {
			return;
		}
		IViewPart view = page.findView("org.eclipse.ui.views.PropertySheet");

		if (view == null) {
			return;
		}

		if (!(view instanceof PropertySheet)) {
			return;
		}
		IPage ipage = ((PropertySheet) view).getCurrentPage();

		if (!(ipage instanceof TabbedPropertySheetPage)) {
			return;
		}
		IEditorPart editor = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActiveEditor();

		// if there are any view maximized, get the editor by comparing the
		// current eObject with the contained elements of the diagram element
		if (editor == null) {
			IEditorReference[] editors = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage()
					.getEditorReferences();
			if (editors.length > 0) {
				first: for (int i = 0; i < editors.length; i++) {
					if (!(editors[i].getEditor(true) instanceof MOSKittMultiPageEditor)) {
						continue;
					}

					MOSKittMultiPageEditor moskittEditor = (MOSKittMultiPageEditor) editors[i]
							.getEditor(true);
					EObject eo = moskittEditor.getDiagram().getElement();
					if (eo.equals(eObject)) {
						editor = moskittEditor;
						break first;
					}

					for (TreeIterator<EObject> it = eo.eAllContents(); it
							.hasNext();) {
						eo = it.next();
						if (eo.equals(eObject)) {
							editor = moskittEditor;
							break first;
						}
					}
				}
			}
		}

		if (editor == null) {
			return;
		}
		((TabbedPropertySheetPage) ipage).selectionChanged(editor,
				StructuredSelection.EMPTY);
		((TabbedPropertySheetPage) ipage).selectionChanged(editor, section
				.getSelection());
	}
}
