/*******************************************************************************
 * Copyright (c) 2010 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Marc Gil Sendra (Prodevelop) – Initial implementation
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.sections;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecoretools.tabbedproperties.sections.AbstractTabbedPropertySection;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;

import es.cv.gvcase.mdt.common.Activator;
import es.cv.gvcase.mdt.common.sections.composites.FileChooser;
import es.cv.gvcase.mdt.common.sections.descriptions.ControlDescriptionDecoratorProvider;

public abstract class AbstractFileChooserPropertySection extends
		AbstractTabbedPropertySection {

	/**
	 * A boolean that store if refreshing is happening and no model
	 * modifications should be performed
	 */
	private boolean isRefreshing = false;

	/**
	 * The text + filechooser button
	 */
	private FileChooser fileChooser;

	/**
	 * The label for this section
	 */
	private CLabel labelText;
	
	@Override
	protected EditingDomain getEditingDomain() {
		try {
			return super.getEditingDomain();
		} catch (IllegalArgumentException ex) {
			return null;
		}
	}

	protected void createWidgets(Composite composite) {
		labelText = getWidgetFactory().createCLabel(composite, getLabelText());

		fileChooser = new FileChooser(composite, getWidgetFactory(),
				getFilterExtensions(), SWT.NONE);
		fileChooser.setEditable(false);

		if (getFeature() != null) {
			fileChooser.setChangeable(getFeature().isChangeable());
		}

		// add possible description hints
		ControlDescriptionDecoratorProvider.addDescriptionDecoration(
				fileChooser, getFeature());
	}

	protected String[] getFilterExtensions() {
		return new String[] { "*.*" };
	}

	protected void setSectionData(Composite composite) {
		FormData data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(fileChooser,
				-ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
		labelText.setLayoutData(data);

		data = new FormData();
		data.left = new FormAttachment(0, getStandardLabelWidth(composite,
				new String[] { getLabelText() }));
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(labelText, 0, SWT.CENTER);
		fileChooser.setLayoutData(data);

	}

	/**
	 * Adds the listeners on the widgets
	 */
	protected void hookListeners() {
		fileChooser.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				handleTextModified();
			}

		});
	}

	/**
	 * Handle the combo modified event.
	 */
	protected void handleTextModified() {
		if (!isRefreshing && getFeatureValue() != fileChooser.getSelection()) {
			List<IStatus> status = verifyFile();
			fileChooser.setStatus(status);
			if (status.isEmpty()) {
				EditingDomain editingDomain = getEditingDomain();
				if (getEObjectList().size() == 1) {
					/* apply the property change to single selected object */
					editingDomain.getCommandStack().execute(
							SetCommand.create(editingDomain, getEObject(),
									getFeature(), fileChooser.getSelection()));
				}
			}
		}
	}

	public void refresh() {
		isRefreshing = true;
		fileChooser.setChangeable(!isReadOnly());
		fileChooser.setSelection(getFeatureValue());
		isRefreshing = false;
	}

	/**
	 * @return the FileChooser
	 */
	protected FileChooser getFileChooser() {
		return fileChooser;
	}

	/**
	 * Handler called to verify the file path on user text modification. By
	 * default it checks the file existence is the property checkFileExistence
	 * is set to true
	 * 
	 * @return true is the file matches
	 */
	protected List<IStatus> verifyFile() {
		List<IStatus> statusList = new ArrayList<IStatus>();
		if (isCheckFileExistence()) {
			String selection = fileChooser.getSelection();
			if (selection != null && !"".equals(selection)) { //$NON-NLS-1$
				File file = new File(selection);
				if (!file.exists()) {
					statusList.add(new Status(IStatus.ERROR,
							Activator.PLUGIN_ID, getLabelText()
									+ "The file doesn't exists"));
				}
				if (!file.isFile()) {
					statusList.add(new Status(IStatus.ERROR,
							Activator.PLUGIN_ID, getLabelText()
									+ "The selected file is not a file"));
				}
			} else if (cannotBeBlank()) {
				statusList.add(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
						getLabelText() + "The value cannot be Blank"));
			}
		}
		List<IStatus> emptyList = Collections.emptyList();
		return statusList.isEmpty() ? emptyList : statusList;
	}

	/**
	 * 
	 * @return true if the file must exist in the file system
	 */
	public boolean isCheckFileExistence() {
		return true;
	}

	/**
	 * @return true if the field is compulsory
	 */
	public boolean cannotBeBlank() {
		return false;
	}

	/**
	 * Get the current feature value of the selected model object.
	 * 
	 * @return the feature value to select in the ccombo.
	 */
	protected abstract String getFeatureValue();

}
