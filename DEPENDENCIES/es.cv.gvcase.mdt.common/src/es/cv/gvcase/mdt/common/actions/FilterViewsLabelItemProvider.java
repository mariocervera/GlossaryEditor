package es.cv.gvcase.mdt.common.actions;

import org.eclipse.gmf.runtime.common.ui.services.action.contributionitem.AbstractContributionItemProvider;
import org.eclipse.gmf.runtime.common.ui.util.IWorkbenchPartDescriptor;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IWorkbenchPage;

// TODO: Auto-generated Javadoc
/**
 * The Class FilterViewsLabelItemProvider.
 */
public class FilterViewsLabelItemProvider extends
		AbstractContributionItemProvider {

	/** The Constant FILTER_VIEWS_AND_LABELS. */
	private static final String FILTER_VIEWS_AND_LABELS = "filter_views_and_labels";

	/* (non-Javadoc)
	 * @see org.eclipse.gmf.runtime.common.ui.services.action.contributionitem.AbstractContributionItemProvider#createAction(java.lang.String, org.eclipse.gmf.runtime.common.ui.util.IWorkbenchPartDescriptor)
	 */
	@Override
	protected IAction createAction(String actionId,
			IWorkbenchPartDescriptor partDescriptor) {
		IWorkbenchPage page = partDescriptor.getPartPage();
		if (FILTER_VIEWS_AND_LABELS.equals(actionId)) {
			return new FilterViewsLabelsAction(page);
		}
		return super.createAction(actionId, partDescriptor);
	}

}
