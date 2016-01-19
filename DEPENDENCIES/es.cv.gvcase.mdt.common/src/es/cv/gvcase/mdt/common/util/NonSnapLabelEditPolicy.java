package es.cv.gvcase.mdt.common.util;

import org.eclipse.gmf.runtime.diagram.ui.editpolicies.NonResizableLabelEditPolicy;

/**
 * A non resize policy for labels that eliminates the snapping of labels.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 *
 */
public class NonSnapLabelEditPolicy extends NonResizableLabelEditPolicy {

	protected org.eclipse.gef.commands.Command getMoveCommand(org.eclipse.gef.requests.ChangeBoundsRequest request) {
		org.eclipse.gmf.runtime.diagram.ui.editparts.LabelEditPart editPart = (org.eclipse.gmf.runtime.diagram.ui.editparts.LabelEditPart) getHost();
		org.eclipse.draw2d.geometry.Point refPoint = editPart.getReferencePoint();

		// translate the feedback figure
		org.eclipse.draw2d.geometry.PrecisionRectangle rect = new org.eclipse.draw2d.geometry.PrecisionRectangle(
				getInitialFeedbackBounds().getCopy());
		getHostFigure().translateToAbsolute(rect);
		rect.translate(request.getMoveDelta());
		rect.resize(request.getSizeDelta());
		getHostFigure().translateToRelative(rect);

		org.eclipse.draw2d.geometry.Point normalPoint = new org.eclipse.draw2d.geometry.Point(rect.x - refPoint.x,
				rect.y - refPoint.y);

		org.eclipse.gmf.runtime.common.core.command.ICommand moveCommand = new org.eclipse.gmf.runtime.diagram.ui.commands.SetBoundsCommand(
				editPart.getEditingDomain(),
				org.eclipse.gmf.runtime.diagram.ui.l10n.DiagramUIMessages.MoveLabelCommand_Label_Location,
				new org.eclipse.gmf.runtime.emf.core.util.EObjectAdapter((org.eclipse.gmf.runtime.notation.View) editPart.getModel()),
				normalPoint);
		return new org.eclipse.gmf.runtime.diagram.ui.commands.ICommandProxy(moveCommand);
	}
	
}
