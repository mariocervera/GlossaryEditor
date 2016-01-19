/*******************************************************************************
 * Copyright (c) 2010 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana. All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Marc Gil Sendra (Prodevelop) [mgil@prodevelop.es] - initial API implementation
 ******************************************************************************/
package es.cv.gvcase.mdt.common.sections;

import org.eclipse.emf.ecoretools.tabbedproperties.sections.AbstractTabbedPropertySection;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;

import es.cv.gvcase.mdt.common.sections.descriptions.ControlDescriptionDecoratorProvider;

/**
 * A Property Section to select a Font from the System
 * 
 * @author mgil
 */
public abstract class AbstractFontChooserPropertySection extends
		AbstractTabbedPropertySection {
	protected CLabel fontLabel;
	protected Text fontText;
	protected Button fontButton;

	@Override
	protected EditingDomain getEditingDomain() {
		try {
			return super.getEditingDomain();
		} catch (IllegalArgumentException ex) {
			return null;
		}
	}
	
	protected void createWidgets(Composite composite) {
		fontLabel = getWidgetFactory().createCLabel(composite, getLabelText());

		fontText = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		fontText.setEditable(false);

		fontButton = getWidgetFactory().createButton(composite, "Change Font",
				SWT.PUSH);

		// add possible description hints
		ControlDescriptionDecoratorProvider.addDescriptionDecoration(fontText,
				getFeature());
	}

	protected void setSectionData(Composite composite) {
		FormData data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(fontText, 0);
		data.top = new FormAttachment(0, 0);
		data.bottom = new FormAttachment(100, 0);
		data.width = getStandardLabelWidth(composite,
				new String[] { getLabelText() });
		fontLabel.setLayoutData(data);

		data = new FormData();
		data.left = new FormAttachment(fontLabel,
				-ITabbedPropertyConstants.HMARGIN);
		data.right = new FormAttachment(fontButton, 0);
		data.top = new FormAttachment(0, 0);
		data.bottom = new FormAttachment(100, 0);
		fontText.setLayoutData(data);

		data = new FormData();
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, 0);
		data.bottom = new FormAttachment(100, 0);
		data.width = getStandardLabelWidth(composite, new String[] { fontButton
				.getText() });
		fontButton.setLayoutData(data);
	}

	protected void hookListeners() {
		fontButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				handleButtonPressed();
			}
		});
	}

	/**
	 * Called when the selected font changes
	 */
	protected void handleButtonPressed() {
		FontDialog ftDialog = new FontDialog(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell());
		ftDialog.setFontList(getFontValue().getFontData());
		FontData fData = ftDialog.open();

		if (fData != null) {
			createCommand(getFontValue(), new Font(null, fData));
		}
	}

	public void refresh() {
		fontText.setEnabled(!isReadOnly());
		fontButton.setEnabled(!isReadOnly());
		fontText
				.setText(StringConverter.asString(getFontValue().getFontData()));
	}

	/**
	 * Get the new Font value of the font feature for the section.
	 * 
	 * @return the Font value of the feature.
	 */
	protected abstract Font getFontValue();
}
