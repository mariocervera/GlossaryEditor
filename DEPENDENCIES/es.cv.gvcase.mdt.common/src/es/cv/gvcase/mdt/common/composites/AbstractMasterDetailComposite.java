/*******************************************************************************
 * Copyright (c) 2012 Conselleria de Infraestructuras y Transporte, Generalitat
 * de la Comunitat Valenciana. All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Marc Gil Sendra (Integranova) - initial API implementation
 *******************************************************************************/
package es.cv.gvcase.mdt.common.composites;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.command.AbstractCommand;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.emf.transaction.ResourceSetChangeEvent;
import org.eclipse.emf.transaction.ResourceSetListenerImpl;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

import es.cv.gvcase.mdt.common.util.MDTUtil;

/**
 * A Composite to manage Master and Detail info in an horizontal way: in the
 * left we have a tree that represents the master, where we can create elements;
 * in the right we have the details of the selected element in the tree
 * 
 * @author mgil
 */
public abstract class AbstractMasterDetailComposite extends Composite {

	protected TabbedPropertySheetWidgetFactory widgetFactory;
	protected EditingDomain domain;
	protected EObject eObject;
	protected IContentProvider contentProvider;
	protected ILabelProvider labelProvider;

	// MASTER WIDGETS
	protected Composite masterComposite;
	protected Composite buttonsComposite;
	protected TreeViewer treeViewer;
	protected List<? extends CreateButtonInfo> createButtonInfos;
	protected List<Button> createButtons;
	protected DeleteButtonInfo deleteButtonInfo;
	protected Button deleteButton;

	// DETAILS WIDGETS
	protected Group groupComposite;
	protected List<RefresheableComposite> refreshableComposites;

	/**
	 * Creates a Master Detail Composite
	 */
	public AbstractMasterDetailComposite(Composite parent) {
		super(parent, SWT.NONE);
		this.widgetFactory = new TabbedPropertySheetWidgetFactory();
		this.contentProvider = getContentProvider();
		this.labelProvider = getLabelProvider();

		createButtons = new ArrayList<Button>();
		widgetFactory.adapt(this);

		createContents();
		setSectionData();
		hookListeners();
	}

	/**
	 * Create the contents of the Composite
	 */
	protected void createContents() {
		createMasterComposite();
		createDetailsGroup();
	}

	/**
	 * Create the Master Composite
	 */
	protected void createMasterComposite() {
		masterComposite = widgetFactory.createComposite(this, SWT.NONE);
		treeViewer = new TreeViewer(masterComposite, SWT.BORDER | SWT.H_SCROLL
				| SWT.V_SCROLL);
		treeViewer.setContentProvider(contentProvider);
		treeViewer.setLabelProvider(labelProvider);
		ViewerFilter filter = getTreeViewerFilter();
		if (filter != null) {
			treeViewer.setFilters(new ViewerFilter[] { filter });
		}

		buttonsComposite = widgetFactory.createComposite(masterComposite,
				SWT.NONE);
		createButtonInfos = getCreateButtonInfos();
		deleteButtonInfo = getDeleteButtonInfo();
		createButtons(buttonsComposite);
	}

	/**
	 * Indicate if a Menu should be created in the Tree
	 */
	protected boolean shouldCreateMenu() {
		return true;
	}

	/**
	 * Creates the Menu for the Tree. By default, there will be created the same
	 * actions as buttons exists.
	 */
	protected void createMenu() {
		if (treeViewer.getControl().getMenu() != null) {
			treeViewer.getControl().getMenu().dispose();
		}

		Menu menu = new Menu(treeViewer.getControl());

		// Create Menu Items
		for (CreateButtonInfo cbi : createButtonInfos) {
			if (cbi.isEnabledFor(treeViewer.getSelection())) {
				MenuItem mi = new MenuItem(menu, SWT.PUSH);
				mi.setData(cbi);
				mi.setText(cbi.text);
				mi.setImage(getLabelProvider().getImage(
						cbi.newObjectEClass.getEPackage().getEFactoryInstance()
								.create(cbi.newObjectEClass)));
				mi.addSelectionListener(getCreateButtonListener());
			}
		}

		if (deleteButtonInfo.isEnabledFor(treeViewer.getSelection())) {
			// Separator Menu Item, only if exists any creation menu items
			MenuItem mi;
			if (menu.getItems().length > 0) {
				mi = new MenuItem(menu, SWT.SEPARATOR);
				mi.setData(deleteButtonInfo);
			}

			// Delete Menu Item
			mi = new MenuItem(menu, SWT.PUSH);
			mi.setData(deleteButtonInfo);
			mi.setText(deleteButtonInfo.text);
			mi.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(
					ISharedImages.IMG_ETOOL_DELETE));
			mi.addSelectionListener(getDeleteButtonListener());
		}

		treeViewer.getControl().setMenu(menu);
	}

	/**
	 * Get the list of Create Button Info to create every button
	 */
	protected List<? extends CreateButtonInfo> getCreateButtonInfos() {
		return Collections.emptyList();
	}

	/**
	 * Get the list of Delete Buttons Info to create every button
	 */
	protected DeleteButtonInfo getDeleteButtonInfo() {
		return null;
	}

	/**
	 * Create the buttons for Master Composite
	 */
	protected void createButtons(Composite buttonsComposite) {
		for (CreateButtonInfo cbi : createButtonInfos) {
			Button createButton = widgetFactory.createButton(buttonsComposite,
					cbi.text, SWT.PUSH);
			createButton.setVisible(false);
			createButton.setData(cbi);
			createButton.setImage(getLabelProvider().getImage(
					cbi.newObjectEClass.getEPackage().getEFactoryInstance()
							.create(cbi.newObjectEClass)));
			createButton.setAlignment(SWT.LEFT);
			createButtons.add(createButton);
		}

		if (deleteButtonInfo != null) {
			deleteButton = widgetFactory.createButton(buttonsComposite,
					deleteButtonInfo.text, SWT.PUSH);
			deleteButton.setVisible(false);
			deleteButton.setData(deleteButtonInfo);
			deleteButton.setImage(PlatformUI.getWorkbench().getSharedImages()
					.getImage(ISharedImages.IMG_ETOOL_DELETE));
			deleteButton.setAlignment(SWT.LEFT);
		}
	}

	/**
	 * Create the Details Group
	 */
	protected void createDetailsGroup() {
		groupComposite = widgetFactory.createGroup(this, "Details");
		groupComposite.setVisible(false);
		refreshableComposites = getDetailGroupContents(groupComposite);
		for (RefresheableComposite c : refreshableComposites) {
			c.setVisible(false);
		}
	}

	/**
	 * Get the list of Composites of the Detail Group
	 */
	protected abstract List<RefresheableComposite> getDetailGroupContents(
			Group group);

	/**
	 * Set the section data of the main composite
	 */
	protected void setSectionData() {
		GridLayout layout = new GridLayout(2, false);
		this.setLayout(layout);

		FormData data = new FormData();
		this.setLayoutData(data);

		setSectionDataMasterComposite();
		setSectionDataDetailsGroup();
	}

	/**
	 * Set the section data for the widgets placed in the master composite
	 */
	protected void setSectionDataMasterComposite() {
		// Master Composite
		GridLayout layout = new GridLayout(2, false);
		masterComposite.setLayout(layout);

		GridData data = new GridData(GridData.FILL_VERTICAL);
		masterComposite.setLayoutData(data);

		// Tree Viewer
		data = new GridData(GridData.FILL_VERTICAL);
		data.widthHint = 300;
		data.heightHint = 200;
		treeViewer.getControl().setLayoutData(data);

		// Buttons Composite
		CustomFormLayout fl = new CustomFormLayout();
		buttonsComposite.setLayout(fl);

		data = new GridData(GridData.FILL_BOTH);
		data.grabExcessHorizontalSpace = true;
		buttonsComposite.setLayoutData(data);

		setSectionDataButtons();
	}

	/**
	 * Set the section data for the widgets placed in the buttons composite
	 */
	protected void setSectionDataButtons() {
		Button lastButton = null;
		for (Button button : createButtons) {
			if (button.getVisible()) {
				CustomFormData data = new CustomFormData();
				data.left = new CustomFormAttachment(0, 0);
				data.right = new CustomFormAttachment(100, 0);
				if (lastButton != null) {
					data.top = new CustomFormAttachment(lastButton, 0);
				} else {
					data.top = new CustomFormAttachment(0, 0);
				}
				button.setLayoutData(data);
				lastButton = button;
			}
		}

		if (deleteButton != null && deleteButton.getVisible()) {
			CustomFormData data = new CustomFormData();
			data.left = new CustomFormAttachment(0, 0);
			data.right = new CustomFormAttachment(100, 0);
			if (lastButton != null) {
				data.top = new CustomFormAttachment(lastButton, 0);
			} else {
				data.top = new CustomFormAttachment(0, 0);
			}
			deleteButton.setLayoutData(data);
		}
	}

	/**
	 * Set the section data for the detail group
	 */
	protected void setSectionDataDetailsGroup() {
		FormLayout layout = new FormLayout();
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		groupComposite.setLayout(layout);

		GridData data = new GridData(GridData.FILL_BOTH);
		data.minimumWidth = 300;
		data.grabExcessHorizontalSpace = true;
		groupComposite.setLayoutData(data);

		setSectionDataDetailsContents();
	}

	/**
	 * Set the section data for the widgets placed in the detail group
	 */
	protected void setSectionDataDetailsContents() {
		for (Composite c : refreshableComposites) {
			FormData data = new FormData();
			data.left = new FormAttachment(0, 0);
			data.right = new FormAttachment(100, 0);
			data.top = new FormAttachment(0, 0);
			data.bottom = new FormAttachment(100, 0);
			c.setLayoutData(data);
		}
	}

	/**
	 * Set the listers for the widgets
	 */
	protected void hookListeners() {
		treeViewer.getControl().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// only if pressed left button (1)
				if (e.button == 1) {
					TreeItem ti = treeViewer.getTree().getSelection()[0];
					if (ti.getExpanded()) {
						treeViewer.collapseToLevel(ti.getData(), 1);
					} else {
						treeViewer.expandToLevel(ti.getData(), 1);
					}
				}
			}
		});

		treeViewer
				.addSelectionChangedListener(getTreeSelectionChangeListener());

		treeViewer.getControl().addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.DEL) {
					if (deleteButtonInfo
							.isEnabledFor(treeViewer.getSelection())) {
						Command command = getDeleteCommand();
						executeCommand(command);
					}
				}
			}
		});

		for (Button button : createButtons) {
			button.addSelectionListener(getCreateButtonListener());
		}

		if (deleteButton != null) {
			deleteButton.addSelectionListener(getDeleteButtonListener());
		}
	}

	/**
	 * Get the selection listener when click in any element of the Tree
	 */
	protected ISelectionChangedListener getTreeSelectionChangeListener() {
		return new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				refresh();
			}
		};
	}

	/**
	 * Refreshed the whole composite (Contextual Menu, Buttons and Detail text)
	 */
	public void refresh() {
		// refresh the menuItems
		refreshMenuItems();

		// refresh the available buttons
		refreshButtons();

		// refresh the section of the details
		refreshDetails();
	}

	/**
	 * Refreshes the available MenuItems for the selected element in the Tree.
	 */
	public void refreshMenuItems() {
		if (shouldCreateMenu()) {
			createMenu();
		}
	}

	/**
	 * Refreshes the available buttons for the selected element in the Tree.
	 */
	public void refreshButtons() {
		for (Button button : createButtons) {
			CreateButtonInfo cbi = (CreateButtonInfo) button.getData();
			boolean enabled = cbi.isEnabledFor(treeViewer.getSelection());
			button.setVisible(enabled);
		}

		if (deleteButton != null) {
			DeleteButtonInfo dbi = (DeleteButtonInfo) deleteButton.getData();
			boolean enabled = dbi.isEnabledFor(treeViewer.getSelection());
			deleteButton.setVisible(enabled);
		}

		// refresh the section of the buttons
		setSectionDataButtons();
		layout(true, true);
		masterComposite.layout(true, true);
		buttonsComposite.layout(true, true);
	}

	/**
	 * Refreshes the Details group when change the selection in the tree.
	 * Depending on the EClass of the selected element, the corresponding
	 * composite with their details will be shown
	 */
	public void refreshDetails() {
		refreshDetailsText();

		boolean any = false;
		for (RefresheableComposite rc : refreshableComposites) {
			if (rc.isEnabledFor(treeViewer.getSelection())) {
				rc.setVisible(true);
				any = true;
			} else {
				rc.setVisible(false);
			}
		}

		if (any) {
			groupComposite.setVisible(true);
		} else {
			groupComposite.setVisible(false);
		}
	}

	/**
	 * Refreshes the Text of the Details Group
	 */
	public void refreshDetailsText() {
		if (getSelectedElement() != null) {
			groupComposite.setText("Details for: "
					+ getLabelProvider().getText(getSelectedElement()));
		} else {
			groupComposite.setText("Details");
		}
	}

	/**
	 * Get the selection listener when click in the Create Button
	 */
	protected SelectionListener getCreateButtonListener() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CreateButtonInfo cbi = (CreateButtonInfo) e.widget.getData();
				Command command = getCreateCommand(cbi);
				executeCommand(command);
			}
		};
	}

	/**
	 * Get the selection listener when click in the Delete Button
	 */
	protected SelectionListener getDeleteButtonListener() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Command command = getDeleteCommand();
				executeCommand(command);
			}
		};
	}

	/**
	 * Get the Command to be executed when press Create Button
	 */
	protected Command getCreateCommand(CreateButtonInfo cbi) {
		Command command = new CreateCommand(cbi);
		return command;
	}

	/**
	 * Get the Command to be executed when press Delete Button
	 */
	protected Command getDeleteCommand() {
		Command command = new DeleteCommand();
		return command;
	}

	/**
	 * Executes the given command
	 */
	protected void executeCommand(Command command) {
		if (command.canExecute()) {
			domain.getCommandStack().execute(command);
		}
	}

	/**
	 * Returns the selected EObject in the tree
	 */
	public EObject getSelectedElement() {
		return (EObject) ((StructuredSelection) treeViewer.getSelection())
				.getFirstElement();
	}

	/**
	 * Set the eObject of the composite and force the update of the Tree and
	 * Details
	 */
	public void setEObject(EObject eObject) {
		this.eObject = eObject;
		domain = TransactionUtil.getEditingDomain(eObject);
		if (domain == null) {
			domain = ((IEditingDomainProvider) eObject.eResource()
					.getResourceSet()).getEditingDomain();
		}
		treeViewer.setInput(getInputForTree());
		if (domain instanceof TransactionalEditingDomain) {
			((TransactionalEditingDomain) domain)
					.addResourceSetListener(listener);
		}
	}

	@Override
	public void dispose() {
		if (domain instanceof TransactionalEditingDomain) {
			((TransactionalEditingDomain) domain)
					.removeResourceSetListener(listener);
		}
		super.dispose();
	}

	private ResourceSetListenerImpl listener = new ResourceSetListenerImpl() {
		@Override
		public void resourceSetChanged(ResourceSetChangeEvent event) {
			if (!groupComposite.isDisposed()) {
				refreshDetailsText();
			}
			if (!treeViewer.getControl().isDisposed()) {
				treeViewer.refresh();
			}
		}
	};

	/**
	 * Get the Label Provider for the Tree
	 */
	public ILabelProvider getLabelProvider() {
		return MDTUtil.getLabelProvider();
	}

	/**
	 * Get the Content Provider for the Tree
	 */
	protected IContentProvider getContentProvider() {
		return getDefaultContentProvider();
	}

	/**
	 * Get the Default Content Provider for the Tree
	 */
	private IContentProvider getDefaultContentProvider() {
		return MDTUtil.getContentProvider();
	}

	/**
	 * Get the Filter for the Tree
	 */
	protected ViewerFilter getTreeViewerFilter() {
		return null;
	}

	/**
	 * Override to set a custom input. By default, the input it returns an array
	 * with the main eObject of the composite, so a TreeArrayContentProvider
	 * should be used in the Tree Viewer
	 */
	protected Object getInputForTree() {
		if (contentProvider.equals(getDefaultContentProvider())) {
			return eObject;
		} else {
			return new Object[] { eObject };
		}
	}

	/**
	 * A Class to execute a create command with the capability of refresh the
	 * viewer
	 * 
	 * @author mgil
	 */
	protected class CreateCommand extends AbstractCommand {
		private Command command;
		private CreateButtonInfo cbi;

		public CreateCommand(CreateButtonInfo cbi) {
			this.cbi = cbi;
		}

		public void execute() {
			EObject newEObject = EcoreUtil.create(cbi.newObjectEClass);
			EObject container = getSelectedElement();
			if (cbi.containingFeature.isMany()) {
				command = AddCommand.create(domain, container,
						cbi.containingFeature, newEObject);
			} else {
				command = SetCommand.create(domain, container,
						cbi.containingFeature, newEObject);
			}

			if (command.canExecute()) {
				command.execute();

				// refresh the treeViewer
				treeViewer.expandToLevel(getSelectedElement(), 1);
				treeViewer.refresh();
				treeViewer.setSelection(new StructuredSelection(newEObject));
			}
		}

		public void redo() {
			if (command != null) {
				command.redo();

				if (!treeViewer.getControl().isDisposed()) {
					treeViewer.refresh();
				}
			}
		}

		@Override
		public void undo() {
			if (command != null) {
				command.undo();

				if (!treeViewer.getControl().isDisposed()) {
					treeViewer.refresh();
				}
			}
		}

		@Override
		protected boolean prepare() {
			return true;
		}
	};

	/**
	 * A Class to execute a delete command with the capability of refresh the
	 * viewer
	 * 
	 * @author mgil
	 */
	protected class DeleteCommand extends AbstractCommand {
		private Command command;

		public void execute() {
			EObject selectedElement = getSelectedElement();
			command = org.eclipse.emf.edit.command.DeleteCommand.create(domain,
					selectedElement);
			if (command.canExecute()) {
				command.execute();

				// refresh the treeViewer
				TreeItem selected = treeViewer.getTree().getSelection()[0];
				Object toSelect = null;
				for (TreeItem ti : selected.getParentItem().getItems()) {
					if (selected.equals(ti)) {
						break;
					} else {
						toSelect = ti.getData();
					}
				}
				if (toSelect == null) {
					toSelect = selected.getParentItem().getData();
				}
				if (toSelect != null) {
					treeViewer.refresh();
					treeViewer.setSelection(new StructuredSelection(toSelect));
				}
			}
		}

		public void redo() {
			if (command != null) {
				command.redo();

				if (!treeViewer.getControl().isDisposed()) {
					treeViewer.refresh();
				}
			}
		}

		@Override
		public void undo() {
			if (command != null) {
				command.undo();

				if (!treeViewer.getControl().isDisposed()) {
					treeViewer.refresh();
				}
			}
		}

		@Override
		protected boolean prepare() {
			return true;
		}
	};

}
