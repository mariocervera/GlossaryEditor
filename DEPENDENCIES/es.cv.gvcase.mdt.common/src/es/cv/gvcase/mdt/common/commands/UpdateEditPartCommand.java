package es.cv.gvcase.mdt.common.commands;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;

import es.cv.gvcase.mdt.common.util.DiagramEditPartsUtil;

public class UpdateEditPartCommand extends AbstractCommonTransactionalCommmand {

	private final IGraphicalEditPart editPart;

	private EClass eClass = EcorePackage.eINSTANCE.getEObject();

	public UpdateEditPartCommand(IGraphicalEditPart editPart) {
		super(editPart.getEditingDomain(), "Update EditPart", null);
		this.editPart = editPart;
	}

	public UpdateEditPartCommand(IGraphicalEditPart editPart, EClass eClass) {
		super(editPart.getEditingDomain(), "Update EditPart", null);
		this.editPart = editPart;
		this.eClass = eClass;
	}

	@Override
	public boolean canExecute() {
		return editPart != null && eClass != null;
	}

	@Override
	protected CommandResult doExecuteWithResult(IProgressMonitor monitor,
			IAdaptable info) throws ExecutionException {
		DiagramEditPartsUtil.updateEditPartAndChildren(editPart, eClass);
		return CommandResult.newOKCommandResult();
	}
}
