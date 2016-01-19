/***************************************************************************
 * Copyright (c) 2010 Conselleria de Infraestructuras y Transporte,
 * Generalitat de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – initial API and implementation
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.part;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.IWrapperItemProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.gmf.runtime.diagram.core.preferences.PreferencesHint;
import org.eclipse.gmf.runtime.diagram.core.services.ViewService;
import org.eclipse.gmf.runtime.diagram.core.services.view.CreateDiagramViewOperation;
import org.eclipse.gmf.runtime.emf.core.util.EObjectAdapter;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import es.cv.gvcase.mdt.common.internal.Messages;
import es.cv.gvcase.mdt.common.provider.IWizardModelElementProvider;
import es.cv.gvcase.mdt.common.util.MDTUtil;

/**
 * Wizard page that allows to select element from model.
 */
public class ModelElementSelectionPage extends WizardPage implements
		IWizardModelElementProvider {

	/** The selected model element. */
	protected EObject selectedModelElement;

	/** The model vewer. */
	private TreeViewer modelViewer;

	/**
	 * The previously selected diagram kind.
	 */
	public String selectedDiagramKind = "";

	/**
	 * The Constructor.
	 * 
	 * @param pageName
	 *            the page name
	 */
	public ModelElementSelectionPage(String pageName) {
		super(pageName);
	}

	/**
	 * Gets the model element.
	 * 
	 * @return the model element
	 */
	public EObject getModelElement() {
		IWizard parentWizard = getWizard();
		if (parentWizard instanceof INewDiagramFileWizard) {
			return ((INewDiagramFileWizard) parentWizard).getRootModelElement();
		}
		return selectedModelElement;
	}

	/**
	 * Get the selected diagram kind.
	 * 
	 * @return
	 */
	public String getSelectedDiagramKind() {
		if (selectedDiagramKind == null || selectedDiagramKind == "") {
			IWizard parentWizard = getWizard();
			if (parentWizard instanceof INewDiagramFileWizard) {
				return ((INewDiagramFileWizard) parentWizard).getDiagramKind();
			}
		}
		return selectedDiagramKind;
	}

	/**
	 * Set the selected diagram kind.
	 * 
	 * @param selectedDiagramKind
	 */
	public void setSelectedModelRootElement(EObject selectedElement) {
		this.selectedModelElement = selectedElement;
		// store the diagram kind in the parent wizard for later use by other
		// pages or the wizard logic.
		IWizard parentWizard = getWizard();
		if (parentWizard instanceof INewDiagramFileWizard) {
			((INewDiagramFileWizard) parentWizard)
					.setRootModelElement(selectedElement);
		}
	}

	@Override
	public void setWizard(IWizard newWizard) {
		super.setWizard(newWizard);
		if (newWizard instanceof INewDiagramFileWizard) {
			((INewDiagramFileWizard) newWizard)
					.setRootModelElement(selectedModelElement);
		}
	}

	/**
	 * Sets the model element.
	 * 
	 * @param modelElement
	 *            the model element
	 */
	public void setModelElement(EObject modelElement) {
		IWizard parentWizard = getWizard();
		if (parentWizard instanceof INewDiagramFileWizard) {
			((INewDiagramFileWizard) parentWizard)
					.setRootModelElement(modelElement);
		}
		selectedModelElement = modelElement;
		if (modelViewer != null) {
			if (selectedModelElement != null) {
				modelViewer.setInput(selectedModelElement.eResource());
				modelViewer.setSelection(new StructuredSelection(
						selectedModelElement));
			} else {
				modelViewer.setInput(null);
			}
			setPageComplete(validatePage());
		}
	}

	/**
	 * Creates the control.
	 * 
	 * @param parent
	 *            the parent
	 */
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);

		Composite plate = new Composite(parent, SWT.NONE);
		plate.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		plate.setLayout(layout);
		setControl(plate);

		Label label = new Label(plate, SWT.NONE);
		label.setText(getSelectionTitle());
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));

		modelViewer = new TreeViewer(plate, SWT.SINGLE | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.BORDER);
		GridData layoutData = new GridData(GridData.FILL_BOTH);
		layoutData.heightHint = 300;
		layoutData.widthHint = 300;
		modelViewer.getTree().setLayoutData(layoutData);
		IBaseLabelProvider labelProvider = MDTUtil.getLabelProvider();

		IContentProvider contentProvider = new AdapterFactoryContentProvider(
				new ComposedAdapterFactory(
						ComposedAdapterFactory.Descriptor.Registry.INSTANCE));

		modelViewer.setContentProvider(contentProvider);
		modelViewer.setLabelProvider(labelProvider);
		if (selectedModelElement != null) {
			modelViewer.setInput(selectedModelElement.eResource());
			modelViewer.setSelection(new StructuredSelection(
					selectedModelElement));
		}
		modelViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {

					public void selectionChanged(SelectionChangedEvent event) {
						ModelElementSelectionPage.this
								.updateSelection((IStructuredSelection) event
										.getSelection());
					}
				});

		setPageComplete(validatePage());
	}

	/**
	 * Gets the selection title.
	 * 
	 * @return the selection title
	 */
	protected String getSelectionTitle() {
		return Messages
				.getString("NewDiagramFileWizard_RootSelectionPageSelectionTitle")
				+ " for a " + selectedDiagramKind;
	}

	/**
	 * Update selection.
	 * 
	 * @param selection
	 *            the selection
	 */
	protected void updateSelection(IStructuredSelection selection) {
		selectedModelElement = null;
		if (selection.size() == 1) {
			Object selectedElement = selection.getFirstElement();
			if (selectedElement instanceof IWrapperItemProvider) {
				selectedElement = ((IWrapperItemProvider) selectedElement)
						.getValue();
			}
			if (selectedElement instanceof FeatureMap.Entry) {
				selectedElement = ((FeatureMap.Entry) selectedElement)
						.getValue();
			}
			if (selectedElement instanceof EObject) {
				selectedModelElement = (EObject) selectedElement;
			}
		}
		boolean validPage = validatePage();
		if (validPage) {
			setSelectedModelRootElement(selectedModelElement);
		} else {
			setSelectedModelRootElement(null);
		}
		setPageComplete(validPage);
	}

	/**
	 * Override to provide specific validation of the selected model element.
	 * 
	 * @return true, if validate page
	 */
	protected boolean validatePage() {
		if (selectedModelElement == null) {
			setErrorMessage(Messages
					.getString("NewDiagramFileWizard_RootSelectionPageNoSelectionMessage"));
			return false;
		}
		String diagramKind = getSelectedDiagramKind();
		if (diagramKind == null) {
			setErrorMessage(Messages
					.getString("NewDiagramFileWizard_RootSelectionPageNoSelectionMessage"));
			return false;
		}

		boolean result = ViewService.getInstance().provides(
				new CreateDiagramViewOperation(new EObjectAdapter(
						selectedModelElement), getDiagramKind(),
						getPreferencesHint()));
		setErrorMessage(result ? null
				: Messages
						.getString("NewDiagramFileWizard_RootSelectionPageInvalidSelectionMessage"));
		return result;
	}

	/**
	 * Gets the diagram kind from the wizard. The model kind must have been
	 * previously set by another page of the wizard.
	 * 
	 * @return
	 */
	protected String getDiagramKind() {
		IWizard parentWizard = getWizard();
		if (parentWizard instanceof INewDiagramFileWizard) {
			return ((INewDiagramFileWizard) parentWizard).getDiagramKind();
		}
		return "";
	}

	/**
	 * Gets the {@link PreferencesHint} from the parent wizard.
	 * 
	 * @return
	 */
	protected PreferencesHint getPreferencesHint() {
		IWizard parentWizard = getWizard();
		if (parentWizard instanceof INewDiagramFileWizard) {
			return ((INewDiagramFileWizard) parentWizard).getPreferencesHint();
		}
		return null;
	}
}
