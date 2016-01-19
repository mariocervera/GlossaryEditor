/*******************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 * Francisco Javier Cano Muñoz (Prodevelop) – Initial implementation.
 * Mario Cervera Ubeda (Prodevelop) - Added method getFeatureAsString
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.sections.extended;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecoretools.tabbedproperties.sections.AbstractTabbedPropertySection;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;

import es.cv.gvcase.mdt.common.commands.AbstractCommonTransactionalCommmand;
import es.cv.gvcase.mdt.common.commands.SetExtendedFeatureValueCommand;
import es.cv.gvcase.mdt.common.commands.UnsetExtendedFeatureValueCommand;
import es.cv.gvcase.mdt.common.dialogs.LoadFilteredResourceDialog;
import es.cv.gvcase.mdt.common.model.ExtendedFeatureElement;
import es.cv.gvcase.mdt.common.model.ExtendedFeatureElementFactory;
import es.cv.gvcase.mdt.common.sections.descriptions.ControlDescriptionDecoratorProvider;

/**
 * Abstract property section to select a {@link IResource} from the workspace
 * and store its path in an extended feature. <br>
 * It features a read-only textbox, a select button and a clear button.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public abstract class AbstractExtendedResourceAsStringPropertySection extends
		AbstractTabbedPropertySection {

	// extended feature identifier.
	private String extendedFeatureID = null;

	@Override
	protected EditingDomain getEditingDomain() {
		try {
			return super.getEditingDomain();
		} catch (IllegalArgumentException ex) {
			return null;
		}
	}
	
	public AbstractExtendedResourceAsStringPropertySection(String featureID) {
		this.extendedFeatureID = featureID;
	}

	/**
	 * This is not used, as this property section uses extended features.
	 */
	@Override
	protected EStructuralFeature getFeature() {
		// XXX : not used due to extended features mechanism
		return null;
	}

	/**
	 * Returns the default label text. <br>
	 * Recommended to override.
	 */
	@Override
	protected String getLabelText() {
		return "Select file";
	}

	/**
	 * Get the EObject being edited as an {@link EModelElement}.
	 * 
	 * @return
	 */
	protected EModelElement getEModelElement() {
		EObject eObject = getEObject();
		if (eObject != null) {
			return (EModelElement) Platform.getAdapterManager().getAdapter(
					eObject, EModelElement.class);
		}
		return null;
	}

	/**
	 * Get the extended feature ID.
	 * 
	 * @return
	 */
	protected String getFeatureID() {
		return extendedFeatureID;
	}

	/**
	 * Get the extended feature value as a String.
	 * 
	 * @return
	 */
	protected String getFeatureAsString() {
		ExtendedFeatureElement element = ExtendedFeatureElementFactory
				.getInstance().asExtendedFeatureElement(getEModelElement());
		if (element != null) {
			return element.getString(getFeatureID());
		}
		return null;
	}

	/**
	 * Get the {@link TransactionalEditingDomain} the EObject being edited uses.
	 * 
	 * @return
	 */
	protected TransactionalEditingDomain getTransactionalEditingDomain() {
		EObject eObject = getEObject();
		if (eObject != null) {
			return TransactionUtil.getEditingDomain(eObject);
		} else {
			return null;
		}
	}

	/**
	 * Set the extended feature value via commands.
	 * 
	 * @param value
	 */
	protected void setFeatureAsString(String value) {
		EModelElement element = getEModelElement();
		TransactionalEditingDomain domain = getTransactionalEditingDomain();
		if (element != null && domain != null) {
			AbstractCommonTransactionalCommmand command = null;
			if (value != null) {
				command = new SetExtendedFeatureValueCommand(domain, element,
						getFeatureID(), value);
			} else {
				command = new UnsetExtendedFeatureValueCommand(domain, element,
						getFeatureID());
			}
			command.executeInTransaction();
		}
	}

	// label + read-only text + button
	private Label textLabel = null;
	private Text resourcePathText = null;
	private Button selectButton = null;
	private Button clearButton = null;

	/**
	 * A Label, a Select button, a Clear button and a read-only Text are
	 * created.
	 */
	@Override
	protected void createWidgets(Composite composite) {
		super.createWidgets(composite);
		// label
		textLabel = getWidgetFactory().createLabel(composite, getLabelText());
		// read-only resource path string
		resourcePathText = getWidgetFactory().createText(composite, "",
				SWT.BORDER | SWT.READ_ONLY);
		// add control decorators that show the description of features
		ControlDescriptionDecoratorProvider.addDescriptionDecoration(resourcePathText,
				getFeatureID());
		// button to open the file selection dialog
		selectButton = getWidgetFactory().createButton(composite, "Select...",
				SWT.PUSH);
		selectButton.addSelectionListener(getSelectButtonHandler());
		// button to clear the resource
		clearButton = getWidgetFactory().createButton(composite, "Clear",
				SWT.PUSH);
		clearButton.addSelectionListener(getClearButtonHandler());
	}

	/**
	 * Standard layout for the wodgets.
	 */
	@Override
	protected void setSectionData(Composite composite) {
		super.setSectionData(composite);
		FormData data;
		// button
		data = new FormData();
		data.top = new FormAttachment(0, 0);
		data.bottom = new FormAttachment(100, 0);
		data.left = new FormAttachment(90, 0);
		data.right = new FormAttachment(95, 0);
		if (selectButton != null) {
			selectButton.setLayoutData(data);
		}
		// clear button
		data = new FormData();
		data.top = new FormAttachment(0, 0);
		data.bottom = new FormAttachment(100, 0);
		data.left = new FormAttachment(95, 0);
		data.right = new FormAttachment(100, 0);
		if (clearButton != null) {
			clearButton.setLayoutData(data);
		}
		// text
		data = new FormData();
		data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
		data.bottom = new FormAttachment(100, 0);
		data.left = new FormAttachment(0, getStandardLabelWidth(composite,
				new String[] { getLabelText() }));
		data.right = new FormAttachment(selectButton);
		if (resourcePathText != null) {
			resourcePathText.setLayoutData(data);
		}
		// label
		data = new FormData();
		data.top = new FormAttachment(resourcePathText, 0, SWT.TOP);
		data.bottom = new FormAttachment(100, 0);
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(resourcePathText,
				-ITabbedPropertyConstants.HSPACE);
		if (textLabel != null) {
			textLabel.setLayoutData(data);
		}
	}

	/**
	 * When refreshing check the extended feature value and set the text to that
	 * value.
	 */
	@Override
	public void refresh() {
		super.refresh();
		// set the text with the correct value
		String resourcePath = getFeatureAsString();
		if (resourcePathText != null && resourcePath != null) {
			resourcePathText.setText(resourcePath);
		}
	}

	/**
	 * Sets both the extended feature vaule and the text string.
	 * 
	 * @param resourcePath
	 */
	protected void setResourcePath(String resourcePath) {
		// set the feature
		setFeatureAsString(resourcePath);
		// set the read-only text
		if (resourcePathText != null) {
			resourcePathText.setText(resourcePath != null ? resourcePath : "");
		} else {
			resourcePathText.setText("");
		}
	}

	/**
	 * Handler for the "Select" button.
	 * 
	 * @return
	 */
	protected SelectionListener getSelectButtonHandler() {
		return new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				// XXX never used
				return;
			}

			public void widgetSelected(SelectionEvent e) {
				// shows a LoadResource dialog. If 'Ok' the selected resource
				// will be stored in the extended feature.
				LoadFilteredResourceDialog resourceDialog = new LoadFilteredResourceDialog(
						PlatformUI.getWorkbench().getDisplay().getActiveShell(),
						getShowBrowseFileSystem(), getViewFilters());
				if (resourceDialog.open() == resourceDialog.OK) {
					List<URI> uris = resourceDialog.getURIs();
					if (uris != null && uris.size() > 0) {
						setResourcePath(uris.get(0).toString());
					}
				}
			}
		};
	}

	/**
	 * By default, the Browse Filesystem will be disabled.
	 * 
	 * @return
	 */
	protected boolean getShowBrowseFileSystem() {
		return false;
	}

	/**
	 * Handler for the "Clear" button.
	 * 
	 * @return
	 */
	protected SelectionListener getClearButtonHandler() {
		return new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				// XXX: never used;
				return;
			}

			public void widgetSelected(SelectionEvent e) {
				// clear the resource in the extended feature and the text
				// string.
				setResourcePath(null);
			}
		};
	}

	/**
	 * Stub to get the {@link ViewerFilter}s to use in the LoadResource dialog. <br>
	 * Override to use ViewFilters.
	 * 
	 * @return
	 */
	protected List<ViewerFilter> getViewFilters() {
		return Collections.emptyList();
	}

}
