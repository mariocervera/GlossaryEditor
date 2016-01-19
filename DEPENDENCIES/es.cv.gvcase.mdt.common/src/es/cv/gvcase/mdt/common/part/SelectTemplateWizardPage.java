/***************************************************************************
 * Copyright (c) 2010 Conselleria de Infraestructuras y Transporte,
 * Generalitat de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Javier Muñoz Ferrara (Prodevelop) – initial API and implementation.
 * 				  Francisco Javier Cano Muñoz (Prodevelop) - added abstract types.
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.part;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import es.cv.gvcase.mdt.common.model.ITypesFactory;
import es.cv.gvcase.mdt.common.model.TypesFactoryRegistry;
import es.cv.gvcase.mdt.common.model.TypesForDomainRegistry;
import es.cv.gvcase.mdt.common.model.TypesGroup;

public class SelectTemplateWizardPage extends WizardPage implements
		IBasicTypesSelectedProvider {

	private Button newModelButton;
	private Button useTemplateButton;
	private TableViewer templateTableViewer;
	// Array of checkboxes for new user type groups
	private Button basicTypesCheckBox[] = null;

	private String editorId;

	protected SelectTemplateWizardPage(String pageName) {
		super(pageName);
	}

	public String getEditorId() {
		return editorId;
	}

	public SelectTemplateWizardPage(String editorId, WizardPage nextPage,
			WizardPage templatePage) {
		super("Select creation approach");
		this.setTitle("Select creation approach");
		this
				.setDescription("Diagrams can be created from scratch or from a template");
		this.editorId = editorId;
	}

	public String getTemplatePath() {
		if (this.useTemplateButton.getSelection()) {
			if (this.templateTableViewer.getSelection() instanceof IStructuredSelection) {
				Object first = ((IStructuredSelection) this.templateTableViewer
						.getSelection()).getFirstElement();
				if (first instanceof ModelTemplateDescription) {
					return ((ModelTemplateDescription) first).getPath();
				}
			}
		}

		return null;
	}

	public String getTemplatePluginId() {
		if (this.useTemplateButton.getSelection()) {
			if (this.templateTableViewer.getSelection() instanceof IStructuredSelection) {
				Object first = ((IStructuredSelection) this.templateTableViewer
						.getSelection()).getFirstElement();
				if (first instanceof ModelTemplateDescription) {
					return ((ModelTemplateDescription) first).getPluginId();
				}
			}
		}

		return null;
	}

	public void createControl(Composite parent) {
		Composite root = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		root.setLayout(gridLayout);

		// creation of the template selection section
		Group composite = new Group(root, 0);
		composite.setText("Creation  approach");
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		gridLayout = new GridLayout(1, false);
		gridLayout.marginWidth = 20;
		gridLayout.marginTop = 10;
		gridLayout.verticalSpacing = 10;
		composite.setLayout(gridLayout);
		createDialogArea(composite);

		// creation of the basic types selection group
		Group typeComposite = new Group(root, 0);
		typeComposite.setText("Basic data types");
		typeComposite
				.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		gridLayout = new GridLayout(1, false);
		gridLayout.marginWidth = 20;
		gridLayout.marginTop = 10;
		gridLayout.verticalSpacing = 10;
		typeComposite.setLayout(gridLayout);
		createBasicTypesCheckBox(typeComposite);

		setControl(root);
	}

	/**
	 * Create the window with all the necessary check boxes to select the type
	 * groups to create.
	 * 
	 * @param window
	 *            control
	 */
	private void createBasicTypesCheckBox(Composite composite) {
		TypesForDomainRegistry registry = TypesForDomainRegistry.getInstance();
		List<TypesGroup> typesGroupList = registry
				.getTypesGroupForEditor(editorId);
		if (typesGroupList == null || typesGroupList.isEmpty()) {
			// no basic abstract types for this editor have been found, return
			return;
		}
		ITypesFactory typesFactory = TypesFactoryRegistry.getInstance()
				.getITypesFactoryForEditor(editorId);
		if (typesFactory == null) {
			// there is no factory to instantiate the basic abstract types,
			// return
			return;
		}
		basicTypesCheckBox = new Button[typesGroupList.size()];
		// This loop runs the TypesGroup over calling UMLFactory for every new
		// object.
		Iterator<TypesGroup> it = typesGroupList.iterator();
		int index = 0;
		// For each type of groups we will have to create a checbox.
		// Each checkbox will have store the typesgroup it can create in its
		// data field.
		while (it.hasNext()) {
			TypesGroup typesGroup = it.next();
			basicTypesCheckBox[index] = new Button(composite, SWT.CHECK);
			basicTypesCheckBox[index].setText("Add data types from "
					+ typesGroup.name);
			boolean selected = typesGroup.defaultSelected != null ? typesGroup.defaultSelected
					: false;
			basicTypesCheckBox[index].setSelection(selected);
			basicTypesCheckBox[index].setData(typesGroup);
			GridData data = new GridData(GridData.FILL_HORIZONTAL);
			data.horizontalSpan = 2;
			basicTypesCheckBox[index].setLayoutData(data);
			basicTypesCheckBox[index]
					.addSelectionListener(new SelectionListener() {
						public void widgetSelected(SelectionEvent e) {
							handlePrimitiveTypeSelected();
						}

						public void widgetDefaultSelected(SelectionEvent e) {
						}
					});
			index++;
		}
	}

	/**
	 * Sets the abstract types selected by the user in the parent wizard.
	 */
	protected void handlePrimitiveTypeSelected() {
		if (basicTypesCheckBox != null && basicTypesCheckBox.length > 0) {
			Collection<TypesGroup> typesGroupSelected = new ArrayList<TypesGroup>();
			for (int i = 0; i < basicTypesCheckBox.length; i++) {
				if (basicTypesCheckBox[i].getSelection()) {
					typesGroupSelected.add((TypesGroup) basicTypesCheckBox[i]
							.getData());
				}
			}
			if (getWizard() instanceof INewModelDiagramWizard) {
				((INewModelDiagramWizard) getWizard())
						.setSelectedBasicTypes(typesGroupSelected);
			}
		}
	}

	/**
	 * Returns the basic types selected.
	 * 
	 * @return
	 */
	public List<TypesGroup> getTypesGroupSelected() {
		List<TypesGroup> typesGroup = new ArrayList<TypesGroup>();
		for (Button button : basicTypesCheckBox) {
			if (button.getSelection()) {
				typesGroup.add((TypesGroup) button.getData());
			}
		}
		return typesGroup;
	}

	private void createDialogArea(Composite composite) {
		createApproachSelectionButtons(composite);
		createTemplatesViewer(composite);
	}

	private void createTemplatesViewer(Composite composite) {
		GridData data = new GridData(GridData.FILL_BOTH);
		templateTableViewer = new TableViewer(composite);
		templateTableViewer.getTable().setLayoutData(data);

		templateTableViewer
				.setContentProvider(new ModelTemplatesContentProvider());
		templateTableViewer.setLabelProvider(new ModelTemplatesLabelProvider());
		templateTableViewer.setInput(0);
		if (templateTableViewer.getTable().getItemCount() > 0) {
			IStructuredSelection ss = new StructuredSelection(
					templateTableViewer.getElementAt(0));
			templateTableViewer.setSelection(ss);
		} else {
			useTemplateButton.setEnabled(false);
		}
		templateTableViewer.getControl().setEnabled(false);
	}

	private void createApproachSelectionButtons(Composite composite) {
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		newModelButton = new Button(composite, SWT.RADIO);
		newModelButton.setText("Create new model");
		newModelButton.setLayoutData(data);
		newModelButton.setSelection(true);

		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		useTemplateButton = new Button(composite, SWT.RADIO);
		useTemplateButton.setText("Initialize from template");
		useTemplateButton.setLayoutData(data);
		useTemplateButton.setSelection(false);

		useTemplateButton.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}

			public void widgetSelected(SelectionEvent e) {
				if (useTemplateButton.getSelection()) {
					templateTableViewer.getControl().setEnabled(true);
				} else {
					templateTableViewer.getControl().setEnabled(false);
				}
			}

		});

	}

	private class ModelTemplatesContentProvider implements
			IStructuredContentProvider {

		private static final String extensionPointId = "es.cv.gvcase.mdt.common.templates";
		private static final String ATTRIBUTE_EDITORID = "editorId";
		private static final String ATTRIBUTE_NAME = "name";
		private static final String ATTRIBUTE_FILE = "file";

		public void dispose() {
			// TODO Auto-generated method stub

		}

		private Object[] getTemplatesDescription() {
			List<ModelTemplateDescription> templates = new ArrayList<ModelTemplateDescription>();

			IExtensionRegistry registry = Platform.getExtensionRegistry();
			IExtension[] extensions = registry.getExtensionPoint(
					extensionPointId).getExtensions();

			for (IExtension extension : extensions) {
				templates.addAll(processExtension(extension));
			}

			return templates.toArray();
		}

		private Collection<ModelTemplateDescription> processExtension(
				IExtension extension) {

			List<ModelTemplateDescription> templates = new ArrayList<ModelTemplateDescription>();
			for (IConfigurationElement configElement : extension
					.getConfigurationElements()) {
				String extensionEditorId = configElement
						.getAttribute(ATTRIBUTE_EDITORID);
				if (editorId.equals(extensionEditorId)) {
					templates.add(new ModelTemplateDescription(configElement
							.getAttribute(ATTRIBUTE_NAME), extension
							.getContributor().getName(), configElement
							.getAttribute(ATTRIBUTE_FILE)));
				}
			}

			return templates;
		}

		public Object[] getElements(Object inputElement) {
			return getTemplatesDescription();
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			if (viewer instanceof TableViewer) {
				((TableViewer) viewer).add(getElements(null));
			}
		}

	}

	private class ModelTemplateDescription {
		private String name;
		// private String metamodelURI;
		private String path;
		private String pluginId;

		public ModelTemplateDescription(String name, String pluginId,
				String path) {
			super();
			this.name = name;
			// this.e = metamodelURI;
			this.path = path;
			this.pluginId = pluginId;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		// /**
		// * @return the metamodelURI
		// */
		// public String getMetamodelURI() {
		// return metamodelURI;
		// }

		/**
		 * @return the path
		 */
		public String getPath() {
			return path;
		}

		public String getFileName() {
			String[] pathParts = path.split("/");
			return pathParts[pathParts.length - 1];
		}

		public String getPluginId() {
			return pluginId;
		}

	}

	private class ModelTemplatesLabelProvider implements ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof ModelTemplateDescription) {
				ModelTemplateDescription modelTemplate = (ModelTemplateDescription) element;
				return modelTemplate.getName() + " ("
						+ modelTemplate.getFileName() + ")";
			}
			return null;
		}

		public void addListener(ILabelProviderListener listener) {

		}

		public void dispose() {

		}

		public boolean isLabelProperty(Object element, String property) {

			return false;
		}

		public void removeListener(ILabelProviderListener listener) {

		}

	}

}
