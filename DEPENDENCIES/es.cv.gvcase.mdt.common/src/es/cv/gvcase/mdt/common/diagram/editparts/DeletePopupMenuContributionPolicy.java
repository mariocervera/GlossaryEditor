package es.cv.gvcase.mdt.common.diagram.editparts;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.gmf.runtime.common.ui.services.action.contributionitem.IPopupMenuContributionPolicy;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;

public class DeletePopupMenuContributionPolicy implements
		IPopupMenuContributionPolicy {

	public boolean appliesTo(ISelection selection,
			IConfigurationElement configuration) {
		if (!(selection instanceof StructuredSelection)) {
			return false;
		}

		for (Object o : ((StructuredSelection) selection).toList()) {
			if (!(o instanceof AbstractBorderedShapeEditPart)
					&& !(o instanceof CompartmentEditPart)
					&& !(o instanceof ConnectionNodeEditPart)
					&& !(o instanceof DiagramEditPart)
					&& !(o instanceof LabelEditPart)
					&& !(o instanceof ListCompartmentEditPart)
					&& !(o instanceof ShapeCompartmentEditPart)
					&& !(o instanceof ShapeNodeEditPart)
					&& !(o instanceof BorderedBorderItemEditPart)) {
				return false;
			}
		}

		return true;
	}

}
