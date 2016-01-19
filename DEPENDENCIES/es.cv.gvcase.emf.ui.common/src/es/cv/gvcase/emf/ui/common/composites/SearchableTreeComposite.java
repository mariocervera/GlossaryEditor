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
package es.cv.gvcase.emf.ui.common.composites;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;

import es.cv.gvcase.emf.ui.common.providers.EObjectsResourcesContentProvider;
import es.cv.gvcase.emf.ui.common.providers.GroupableTreeArrayContentProvider;
import es.cv.gvcase.emf.ui.common.providers.PackagedLabelProvider;
import es.cv.gvcase.emf.ui.common.providers.ResourceWrappedLabelProvider;
import es.cv.gvcase.emf.ui.common.utils.ImageUtils;
import es.cv.gvcase.emf.ui.common.utils.PackagingNode;

/**
 * This widget displays a tree (or a list) with a text field to allow to filter
 * the list content using the field content.
 * 
 * @author <a href="david.sciamma@anyware-tech.com">David Sciamma</a>
 */
public class SearchableTreeComposite extends Composite {

	private static boolean caseSensitive = true;

	private static boolean grouped = false;

	private static boolean showingResources = false;

	private Text searchField;

	private Button searchBt;

	private Button sensitiveBt;

	private Button groupedBt;

	private Button showResourcesBt;

	private TreeViewer treeViewer;

	private IStructuredSelection initialSelection;

	private ITreeContentProvider flatProvider;

	private ITreeContentProvider groupedProvider;

	private ITreeContentProvider resourceProvider;

	private boolean selectAfterRefreshTree = false;

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
			String searchedText = SearchableTreeComposite.this.searchField
					.getText();
			boolean isCaseSensitive = SearchableTreeComposite.this.sensitiveBt
					.getSelection();
			if (searchedText != null & !"".equals(searchedText)) {
				IBaseLabelProvider labelProvider = treeViewer
						.getLabelProvider();
				IContentProvider contentProvider = treeViewer
						.getContentProvider();
				if (labelProvider instanceof ILabelProvider) {
					String text = ((ILabelProvider) labelProvider)
							.getText(element);

					if (!isCaseSensitive) {
						text = text.toLowerCase();
						searchedText = searchedText.toLowerCase();
					}

					if (text.indexOf(searchedText) != -1) {
						return true;
					} else if (contentProvider != null) {
						for (Object child : ((ITreeContentProvider) contentProvider)
								.getChildren(element)) {
							if (select(viewer, element, child)) {
								return true;
							}
						}
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
	public SearchableTreeComposite(Composite parent, int style) {
		this(parent, style, true);
	}

	public SearchableTreeComposite(Composite parent, int style,
			boolean selectAfterRefreshTree) {
		super(parent, SWT.NONE);

		createContents(this, style);
		hookListeners();

		this.selectAfterRefreshTree = selectAfterRefreshTree;
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
		Composite searchComp = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(3, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		searchComp.setLayout(layout);

		searchComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// Create search zone
		Label searchLbl = new Label(searchComp, SWT.NONE);
		searchLbl.setText("Search : ");

		// Create search text
		this.searchField = new Text(searchComp, SWT.BORDER);
		this.searchField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// Create search button
		this.searchBt = new Button(searchComp, SWT.PUSH);
		Shell shell = parent.getShell();
		if (shell != null) {
			shell.setDefaultButton(this.searchBt);
		}
		this.searchBt
				.setImage(ImageUtils.getSharedImage(ImageUtils.IMG_SEARCH));

		// Create buttons zone
		Composite filtersComp = new Composite(parent, SWT.NONE);
		GridLayout layout2 = new GridLayout(2, false);
		layout2.marginHeight = 0;
		layout2.marginWidth = 0;
		filtersComp.setLayout(layout2);

		filtersComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// Create case sensitive checkbox
		this.sensitiveBt = new Button(filtersComp, SWT.CHECK);
		this.sensitiveBt.setText("Case sensitive");
		this.sensitiveBt.setSelection(isCaseSensitive());
		this.sensitiveBt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// Create case grouped checkbox
		this.groupedBt = new Button(filtersComp, SWT.CHECK);
		this.groupedBt.setText("Grouped");
		this.groupedBt.setSelection(isGrouped());
		this.groupedBt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// Create show resources checkbox
		this.showResourcesBt = new Button(filtersComp, SWT.CHECK);
		this.showResourcesBt.setText("Show Resources");
		this.showResourcesBt.setSelection(isShowingResources());
		this.showResourcesBt.setLayoutData(new GridData(
				GridData.FILL_HORIZONTAL));
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

		this.searchField.addKeyListener(new KeyListener() {

			public void keyReleased(KeyEvent e) {
				// Noting to do
			}

			public void keyPressed(KeyEvent e) {
				switch (e.keyCode) {
				case SWT.TRAVERSE_RETURN:
					refresh();
				case SWT.ARROW_DOWN:
					treeViewer.getControl().setFocus();
				}
			}
		});

		this.searchBt.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				refresh();
			}

		});

		sensitiveBt.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				caseSensitive = SearchableTreeComposite.this.sensitiveBt
						.getSelection();
			}

		});

		groupedBt.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				grouped = SearchableTreeComposite.this.groupedBt.getSelection();
				updateProviders();
				refresh();
			}

		});

		showResourcesBt.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				showingResources = SearchableTreeComposite.this.showResourcesBt
						.getSelection();
				updateProviders();
				refresh();
			}

		});

		treeViewer.getControl().addKeyListener(new KeyListener() {

			public void keyReleased(KeyEvent e) {
				// Noting to do
			}

			public void keyPressed(KeyEvent e) {
				Object selected = ((IStructuredSelection) treeViewer
						.getSelection()).getFirstElement();

				if (selected == null) {
					return;
				}

				switch (e.keyCode) {
				case SWT.ARROW_RIGHT:
					treeViewer.expandToLevel(selected, 1);
					break;
				case SWT.ARROW_LEFT:
					treeViewer.collapseToLevel(selected, 1);
					break;
				case SWT.ARROW_UP:
					if (treeViewer.getTree().getItems().length > 0) {
						TreeItem item = treeViewer.getTree().getItem(0);
						if (item != null && item.getData().equals(selected)) {
							searchField.setFocus();
						}
					}
				}
			}
		});
	}

	public void setGroupedContentProvider(
			ITreeContentProvider groupedContentProvider) {
		groupedProvider = groupedContentProvider;
	}

	public void setResourceContentProvider(
			ITreeContentProvider resourceContentProvider) {
		resourceProvider = resourceContentProvider;
	}

	protected void updateProviders() {
		// Doesn't select the element

		// ITreeSelection selection = (ITreeSelection) getTreeViewer()
		// .getSelection();

		ITreeContentProvider contentProvider = this.flatProvider;
		IBaseLabelProvider labelProvider = getTreeViewer().getLabelProvider();

		if (isGrouped()) {
			contentProvider = groupedProvider;
		}

		if (isShowingResources()) {
			if (resourceProvider == null) {
				contentProvider = new EObjectsResourcesContentProvider(
						contentProvider);
			} else {
				contentProvider = resourceProvider;
			}
			if (!(labelProvider instanceof ResourceWrappedLabelProvider)) {
				labelProvider = new ResourceWrappedLabelProvider(
						(ILabelProvider) labelProvider);
			}
		}

		getTreeViewer().setLabelProvider(labelProvider);
		getTreeViewer().setContentProvider(contentProvider);

		// getTreeViewer().setSelection(selection, true);
	}

	public void setSelectAfterRefreshTree(boolean value) {
		selectAfterRefreshTree = value;
	}

	/**
	 * Refresh the tree and the selection
	 */
	protected void refresh() {
		treeViewer.refresh();
		if (selectAfterRefreshTree) {
			// select the first object
			if (treeViewer.getTree().getItems().length > 0) {
				treeViewer.expandAll();

				TreeItem[] items = treeViewer.getTree().getItems();

				for (int i = 0; i < items.length; i++) {
					if (selectTreeItem(items[i])) {
						break;
					}
				}

				if (searchField.getText() == null
						|| searchField.getText().equals("")) {
					treeViewer.collapseAll();
				}
			}
		}
	}

	private boolean selectTreeItem(TreeItem item) {
		boolean selected = false;

		if (item.getData() instanceof Resource
				|| item.getData() instanceof PackagingNode
				|| !isFiltered(item.getData())) {
			for (int i = 0; i < item.getItems().length; i++) {
				selected = selectTreeItem(item.getItems()[i]);
				if (selected) {
					break;
				}
			}
		} else {
			treeViewer.getTree().setSelection(new TreeItem[] { item });
			selected = true;
		}
		return selected;
	}

	private boolean isFiltered(Object element) {
		if (element == null) {
			return false;
		}

		String searchedText = searchField.getText();
		boolean isCaseSensitive = sensitiveBt.getSelection();

		if (searchedText != null & !"".equals(searchedText)) {
			IBaseLabelProvider labelProvider = treeViewer.getLabelProvider();
			if (labelProvider instanceof ILabelProvider) {
				String text = ((ILabelProvider) labelProvider).getText(element);

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
	public void setContentProvider(IStructuredContentProvider provider) {
		if (!(provider instanceof ITreeContentProvider)) {
			return;
		}
		flatProvider = (ITreeContentProvider) provider;
		// flatProvider = new EObjectsResourcesContentProvider(provider);
		// groupedProvider = new GroupableTreeArrayContentProvider(provider);
		getTreeViewer().setContentProvider(flatProvider);
		setGroupedContentProvider(new GroupableTreeArrayContentProvider(
				flatProvider));
		updateProviders();
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

	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	public boolean isGrouped() {
		return grouped;
	}

	public boolean isShowingResources() {
		return showingResources;
	}

}
