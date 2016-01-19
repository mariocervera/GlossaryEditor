package es.cv.gvcase.ide.redmine.action;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import es.cv.gvcase.ide.redmine.dialog.CreateNewIssueDialog;

public class CreateNewIssueAction implements IWorkbenchWindowActionDelegate {

	public void init(IWorkbenchWindow window) {
	}

	public void run(IAction action) {
		CreateNewIssueDialog dialog = new CreateNewIssueDialog();
		dialog.open();
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

	public void dispose() {
	}

}
