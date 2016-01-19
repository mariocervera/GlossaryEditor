package es.cv.gvcase.mdt.common.actions;

import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.common.ui.services.action.contributionitem.IPopupMenuContributionPolicy;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

import es.cv.gvcase.mdt.common.part.CachedResourcesDiagramEditor;
import es.cv.gvcase.mdt.common.part.MOSKittMultiPageEditor;
import es.cv.gvcase.mdt.common.util.MDTUtil;

public class RestoreViewPopupMenuContributionPolicy implements
		IPopupMenuContributionPolicy {

	public boolean appliesTo(ISelection selection,
			IConfigurationElement configuration) {
		IEditorPart activeEditor = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (activeEditor instanceof CachedResourcesDiagramEditor == false
				&& activeEditor instanceof MOSKittMultiPageEditor == false) {
			return false;
		}
		List<EditPart> editParts = MDTUtil.getEditPartsFromSelection(selection);
		if (editParts == null || editParts.size() != 1) {
			return false;
		}
		EditPart editPart = editParts.get(0);
		if (!(editPart instanceof IGraphicalEditPart)) {
			return false;
		}
		EObject eObject = ((IGraphicalEditPart) editPart)
				.resolveSemanticElement();
		if (eObject == null) {
			return false;
		}
		return true;
	}

}
