package es.cv.gvcase.mdt.common.actions;

import org.eclipse.gmf.runtime.common.ui.services.action.contributionitem.AbstractContributionItemProvider;
import org.eclipse.gmf.runtime.common.ui.util.IWorkbenchPartDescriptor;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.Node;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IWorkbenchPage;

public class RestoreViewContributionItemProvider extends
		AbstractContributionItemProvider {

	private static final String ActionID = "es.cv.gvcase.mdt.common.actions.RestoreViewAction";
	
	@Override
	protected IAction createAction(String actionId,
			IWorkbenchPartDescriptor partDescriptor) {
		IWorkbenchPage page = partDescriptor.getPartPage();
		if (ActionID.equals(actionId)) {
			View view = getSelectedView(partDescriptor);
			if (view instanceof Diagram || view instanceof Node) {
				return new RestoreViewAction(page, view);
			} else {
				return null;
			}
		}
		return super.createAction(actionId, partDescriptor);
	}

	/**
	 * Gets the selected view.
	 * 
	 * @param partDescriptor
	 *            the part descriptor
	 * 
	 * @return the selected view
	 */
	private View getSelectedView(IWorkbenchPartDescriptor partDescriptor) {
		Object object = getSelectedObject(partDescriptor);
		if (object instanceof IGraphicalEditPart) {
			return ((IGraphicalEditPart) object).getNotationView();
		}
		if (object instanceof View) {
			return ((View) object);
		}
		return null;
	}

}
