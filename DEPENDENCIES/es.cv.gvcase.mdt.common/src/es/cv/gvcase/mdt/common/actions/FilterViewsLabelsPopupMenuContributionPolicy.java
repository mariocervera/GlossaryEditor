package es.cv.gvcase.mdt.common.actions;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.gmf.runtime.common.ui.services.action.contributionitem.IPopupMenuContributionPolicy;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

// TODO: Auto-generated Javadoc
/**
 * The Class FilterViewsLabelsPopupMenuContributionPolicy.
 */
public class FilterViewsLabelsPopupMenuContributionPolicy implements
		IPopupMenuContributionPolicy {

	/* (non-Javadoc)
	 * @see org.eclipse.gmf.runtime.common.ui.services.action.contributionitem.IPopupMenuContributionPolicy#appliesTo(org.eclipse.jface.viewers.ISelection, org.eclipse.core.runtime.IConfigurationElement)
	 */
	public boolean appliesTo(ISelection selection,
			IConfigurationElement configuration) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) selection;
			for (Object o : ss.toList()) {
				if (o instanceof IGraphicalEditPart) {
					if (((IGraphicalEditPart) o).getNotationView() instanceof Diagram) {
						
						return true;
					}
				}
			}
		}
		return false;
	}

}
