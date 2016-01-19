package es.cv.gvcase.mdt.common.actions;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.gef.commands.Command;
import org.eclipse.gmf.runtime.common.ui.services.action.contributionitem.IPopupMenuContributionPolicy;
import org.eclipse.gmf.runtime.diagram.ui.editparts.GraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.requests.EditCommandRequestWrapper;
import org.eclipse.gmf.runtime.emf.type.core.requests.DestroyElementRequest;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;

import es.cv.gvcase.mdt.common.diagram.editparts.AbstractBorderedShapeEditPart;
import es.cv.gvcase.mdt.common.diagram.editparts.BorderedBorderItemEditPart;
import es.cv.gvcase.mdt.common.diagram.editparts.CompartmentEditPart;
import es.cv.gvcase.mdt.common.diagram.editparts.ConnectionNodeEditPart;
import es.cv.gvcase.mdt.common.diagram.editparts.DiagramEditPart;
import es.cv.gvcase.mdt.common.diagram.editparts.LabelEditPart;
import es.cv.gvcase.mdt.common.diagram.editparts.ListCompartmentEditPart;
import es.cv.gvcase.mdt.common.diagram.editparts.ShapeCompartmentEditPart;
import es.cv.gvcase.mdt.common.diagram.editparts.ShapeNodeEditPart;

public class DeleteActionMenuContributionPolicy implements
		IPopupMenuContributionPolicy {

	public boolean appliesTo(ISelection selection,
			IConfigurationElement configuration) {

		if (!(selection instanceof StructuredSelection)) {
			return true;
		}

		for (Object o : ((StructuredSelection) selection).toList()) {
			if (!(o instanceof GraphicalEditPart)) {
				return true;
			}
			GraphicalEditPart gep = (GraphicalEditPart) o;

			if (gep.resolveSemanticElement() == null) {
				return false;
			}

			DestroyElementRequest req = new DestroyElementRequest(gep
					.resolveSemanticElement(), false);
			Command c = gep.getCommand(new EditCommandRequestWrapper(req));
			if (c == null || c.canExecute() == false) {
				return false;
			}
		}

		// all the selected edit parts should be of our types
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
