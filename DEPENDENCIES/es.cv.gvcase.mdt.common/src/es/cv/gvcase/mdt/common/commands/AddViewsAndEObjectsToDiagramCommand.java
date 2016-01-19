package es.cv.gvcase.mdt.common.commands;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.notation.View;

public class AddViewsAndEObjectsToDiagramCommand extends
		AbstractCommonTransactionalCommmand {

	protected View parentView = null;

	protected View viewToAdd = null;

	public AddViewsAndEObjectsToDiagramCommand(
			TransactionalEditingDomain domain, View parentView, View viewToAdd) {
		super(domain, "Add views to diagram", null);
		this.parentView = parentView;
		this.viewToAdd = viewToAdd;
	}

	@Override
	protected CommandResult doExecuteWithResult(IProgressMonitor monitor,
			IAdaptable info) throws ExecutionException {
		// get and copy the EObject behind the view.
		
		// add the copied EObject to the parent element.
		
		// get and copy the view to add
		
		// set the view to add element to the copied element
		
		// add the copied view to the parent view
		
		return null;
	}

}

