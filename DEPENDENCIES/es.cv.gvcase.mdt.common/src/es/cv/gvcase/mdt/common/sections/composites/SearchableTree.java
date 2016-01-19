/***********************************************************************
 * Copyright (c) 2007 Anyware Technologies
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Anyware Technologies - initial API and implementation
 **********************************************************************/

package es.cv.gvcase.mdt.common.sections.composites;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;

import es.cv.gvcase.mdt.common.provider.EObjectsResourcesContentProvider;
import es.cv.gvcase.mdt.common.provider.GroupableTreeArrayContentProvider;
import es.cv.gvcase.mdt.common.provider.PackagedLabelProvider;
import es.cv.gvcase.mdt.common.provider.ResourceWrappedLabelProvider;

/**
 * This widget displays a tree (or a list) with a text field to allow to filter
 * the list content using the field content.
 * 
 * @author <a href="david.sciamma@anyware-tech.com">David Sciamma</a>
 */
public class SearchableTree extends Composite {

	private static boolean caseSensitive = true;

	private static boolean grouped = false;

	private static boolean showingResources = false;

	private Text searchField;

	private Button sensitiveBt;

	private Button groupedBt;

	private Button showResourcesBt;

	private TreeViewer treeViewer;

	private IStructuredSelection initialSelection;

	private ITreeContentProvider flatProvider;

	private ITreeContentProvider groupedProvider;

	/**
	 * Private filter to search into the object tree
	 * 
	 * @author <a href="david.sciamma@anyware-tech.com">David Sciamma</a>
	 */
	private class SearchFilter extends ViewerFilter {

		/**
		 * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer,
		 *      java.lang.Object, java.lang.Object)
		 */
		public boolean select(Viewer viewer, Object parentElement,
				Object element) {
			String searchedText = SearchableTree.this.searchField.getText();
			boolean isCaseSensitive = SearchableTree.this.sensitiveBt
					.getSelection();
			if (searchedText != null & !"".equals(searchedText)) {
				IBaseLabelProvider provider = treeViewer.getLabelProvider();
				if (provider instanceof ILabelProvider) {
					String text = ((ILabelProvider) provider).getText(element);

					if (!isCaseSensitive) {
						text = text.toLowerCase();
						searchedText = searchedText.toLowerCase();
					}

					if (text.indexOf(searchedText) != -1) {
						return true;
					}
					return false;
				}
			}

			return true;
		}

	}

	/**
	 * Constructor
	 * 
	 * @param parent
	 *            the parent composite
	 * @param style
	 *            the Tree Style
	 */
	public SearchableTree(Composite parent, int style) {
		super(parent, SWT.NONE);

		createContents(this, style);
		hookListeners();
	}

	/**
	 * Creates the UI
	 * 
	 * @param parent
	 *            this widget
	 * @param style
	 *            the tree style
	 */
	protected void createContents(Composite parent, int style) {
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		parent.setLayout(layout);

		createSearchComp(parent);
		createTree(parent, style);
	}

	/**
	 * Creates the UI for the serach field
	 * 
	 * @param parent
	 *            the parent composite
	 */
	private void createSearchComp(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(4, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		main.setLayout(layout);

		main.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// Create search zone
		Label searchLbl = new Label(main, SWT.NONE);
		searchLbl.setText("Search : ");

		this.searchField = new Text(main, SWT.BORDER);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 3;
		this.searchField.setLayoutData(gd);

		// Create case sensitive checkbox
		this.sensitiveBt = new Button(main, SWT.CHECK);
		this.sensitiveBt.setText("Case sensitive");
		this.sensitiveBt.setSelection(caseSensitive);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		this.sensitiveBt.setLayoutData(gd);

		// Create case grouped checkbox
		this.groupedBt = new Button(main, SWT.CHECK);
		this.groupedBt.setText("Grouped");
		this.groupedBt.setSelection(isGrouped());
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		this.groupedBt.setLayoutData(gd);

		// Create show resources checkbox
		this.showResourcesBt = new Button(main, SWT.CHECK);
		this.showResourcesBt.setText("Show Resources");
		this.showResourcesBt.setSelection(isShowingResources());
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		this.showResourcesBt.setLayoutData(gd);
	}

	/**
	 * Creates the tree displaying the objects
	 * 
	 * @param parent
	 *            the parent composite
	 * @param style
	 *            the tree style
	 */
	private void createTree(Composite parent, int style) {
		treeViewer = new TreeViewer(parent, SWT.BORDER | SWT.H_SCROLL
				| SWT.V_SCROLL | style);
		treeViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		treeViewer.addFilter(new SearchFilter());
	}

	/**
	 * Adds the listeners on the widgets
	 */
	protected void hookListeners() {
		this.searchField.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				refresh();
			}
		});

		groupedBt.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				grouped = SearchableTree.this.groupedBt.getSelection();
				updateProviders();
				refresh();
			}

		});

		showResourcesBt.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				showingResources = SearchableTree.this.showResourcesBt
						.getSelection();
				updateProviders();
				refresh();
			}

		});
	}

	public void setGroupedContentProvider(
			ITreeContentProvider groupedContentProvider) {
		groupedProvider = groupedContentProvider;
	}

	protected void updateProviders() {

		ITreeSelection selection = (ITreeSelection) getTreeViewer()
				.getSelection();

		ITreeContentProvider contentProvider = this.flatProvider;
		IBaseLabelProvider labelProvider = getTreeViewer().getLabelProvider();

		if (isGrouped()) {
			contentProvider = groupedProvider;
		}

		if (isShowingResources()) {
			contentProvider = new EObjectsResourcesContentProvider(
					contentProvider);
			if (!(labelProvider instanceof ResourceWrappedLabelProvider)) {
				labelProvider = new ResourceWrappedLabelProvider(
						(ILabelProvider) labelProvider);
			}
		}

		getTreeViewer().setLabelProvider(labelProvider);
		getTreeViewer().setContentProvider(contentProvider);

		getTreeViewer().setSelection(selection, true);
	}

	/**
	 * Refresh the tree and the selection
	 */
	protected void refresh() {
		treeViewer.refresh();
		// Try to restore initial selection
		treeViewer.setSelection(initialSelection);
		// Else select the first object
		if (((IStructuredSelection) treeViewer.getSelection()).size() == 0) {
			if (treeViewer.getTree().getItems().length > 0) {
				TreeItem item = treeViewer.getTree().getItem(0);
				treeViewer.getTree().setSelection(new TreeItem[] { item });
			}
		}
	}

	/**
	 * Returns the tree used to display the objects
	 * 
	 * @return the tree viewer
	 */
	public TreeViewer getTreeViewer() {
		return treeViewer;
	}

	/**
	 * Set the content provider for the tree
	 * 
	 * @param provider
	 *            the tree content provider
	 */
	public void setContentProvider(ITreeContentProvider provider) {
		flatProvider = provider;
		// flatProvider = new EObjectsResourcesContentProvider(provider);
		// groupedProvider = new GroupableTreeArrayContentProvider(provider);
		getTreeViewer().setContentProvider(flatProvider);
		setGroupedContentProvider(new GroupableTreeArrayContentProvider(
				flatProvider));
	}

	/**
	 * Set the label provider for the tree
	 * 
	 * @param provider
	 *            the tree label provider
	 */
	public void setLabelProvider(ILabelProvider provider) {
		treeViewer.setLabelProvider(new PackagedLabelProvider(provider));
	}

	/**
	 * Set the input model
	 * 
	 * @param input
	 *            the input object
	 */
	public void setInput(Object input) {
		treeViewer.setInput(input);
	}

	/**
	 * Set the initial selection of the tree
	 * 
	 * @param selection
	 *            the intial selection
	 */
	public void setInitialSelection(IStructuredSelection selection) {
		this.initialSelection = selection;
		this.updateProviders();
		this.refresh();
	}

	public boolean isGrouped() {
		return grouped;
	}

	public boolean isShowingResources() {
		return showingResources;
	}
}
