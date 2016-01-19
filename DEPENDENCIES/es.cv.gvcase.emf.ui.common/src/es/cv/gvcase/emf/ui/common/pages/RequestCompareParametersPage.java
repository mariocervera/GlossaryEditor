/*******************************************************************************
 * Copyright (c) 2011 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana. All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *  Miguel Llacer (Prodevelop) [mllacer@prodevelop.es] - initial API implementation
 ******************************************************************************/
package es.cv.gvcase.emf.ui.common.pages;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.team.core.history.IFileRevision;

import es.cv.gvcase.emf.common.util.ResourceUtil;
import es.cv.gvcase.emf.ui.common.composites.SelectModelComposite;
import es.cv.gvcase.emf.ui.common.composites.SelectNewModelComposite;
import es.cv.gvcase.emf.ui.common.composites.SelectResourceComposite;
import es.cv.gvcase.emf.ui.common.internal.Messages;

public class RequestCompareParametersPage extends WizardPage {

	private URI leftModelURI;
	private URI rightModelURI;

	private SelectResourceComposite leftResourceComposite;
	private SelectResourceComposite rightResourceComposite;

	private SelectResourceComposite diffResourceComposite;

	public RequestCompareParametersPage(String pageName) {
		this(pageName, null, null);
	}

	public RequestCompareParametersPage(String pageName, URI leftModelURI,
			URI rightModelURI) {
		super(pageName);
		setTitle(pageName);
		setDescription(Messages
				.getString("RequestCompareParametersPage.PageDescription"));

		this.leftModelURI = leftModelURI;
		this.rightModelURI = rightModelURI;
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridLayout compositeLayout = new GridLayout();
		composite.setLayout(compositeLayout);

		GridLayout groupLayout = new GridLayout();
		groupLayout.numColumns = 1;

		// input models group
		Group inputModelsGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
		inputModelsGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		inputModelsGroup.setLayout(groupLayout);
		inputModelsGroup.setText(Messages
				.getString("RequestCompareParametersPage.InputParameters"));

		leftResourceComposite = new SelectModelComposite(inputModelsGroup,
				SWT.NONE) {
			@Override
			public String getLabelText() {
				return Messages
						.getString("RequestCompareParametersPage.LeftModel");
			}
		};
		leftResourceComposite.setLayoutData(new GridData(
				GridData.FILL_HORIZONTAL));

		rightResourceComposite = new SelectModelComposite(inputModelsGroup,
				SWT.NONE, true) {
			@Override
			public String getLabelText() {
				return Messages
						.getString("RequestCompareParametersPage.RightModel");
			}
		};
		rightResourceComposite.setLayoutData(new GridData(
				GridData.FILL_HORIZONTAL));

		// output model group
		Group outputModelsGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
		outputModelsGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		outputModelsGroup.setLayout(groupLayout);
		outputModelsGroup.setText(Messages
				.getString("RequestCompareParametersPage.OutputParameters"));

		diffResourceComposite = new SelectNewModelComposite(outputModelsGroup,
				SWT.NONE);
		diffResourceComposite.setLayoutData(new GridData(
				GridData.FILL_HORIZONTAL));

		hookListeners();

		setInitialValues();

		setControl(composite);
	}

	private void hookListeners() {
		leftResourceComposite.addModifyListener(new TextFieldModifyListener(
				leftResourceComposite));

		rightResourceComposite.addModifyListener(new TextFieldModifyListener(
				rightResourceComposite));

		diffResourceComposite.addModifyListener(new TextFieldModifyListener(
				diffResourceComposite));
	}

	private void setInitialValues() {
		if (this.leftModelURI != null) {
			leftResourceComposite.setResourceSelected(this.leftModelURI
					.toString());
		}
		if (this.rightModelURI != null) {
			rightResourceComposite.setResourceSelected(this.rightModelURI
					.toString());
		}
	}

	private class TextFieldModifyListener implements ModifyListener {

		SelectResourceComposite resourceSelectionComposite;

		public TextFieldModifyListener(
				SelectResourceComposite resourceSelectionComposite) {
			this.resourceSelectionComposite = resourceSelectionComposite;
		}

		public void modifyText(ModifyEvent e) {
			updateEnablement();
			this.resourceSelectionComposite.updateFieldDecorators();
		}
	}

	private void updateEnablement() {
		if (this.isPageComplete()) {
			this.setPageComplete(true);
		} else {
			this.setPageComplete(false);
		}

	}

	public boolean isPageComplete() {
		if (leftResourceComposite == null
				|| !leftResourceComposite.isFileValid()) {
			return false;
		} else if (rightResourceComposite == null
				|| !rightResourceComposite.isFileValid()) {
			return false;
		} else if (diffResourceComposite == null
				|| !diffResourceComposite.isResourceSelected()) {
			return false;
		}

		return true;
	}

	private ResourceSet resourceSet = new ResourceSetImpl();

	public Resource getResource(String resourcePath) {
		return ResourceUtil.loadResourceFastOptions(
				URI.createURI(resourcePath), resourceSet);
	}

	public Resource getLeftResource() {
		try {
			return getResource(leftResourceComposite.getResourceSelected());
		} catch (Exception e) {
			// If page is complete, resource can't be null so this code is
			// unreachable
			return null;
		}
	}

	public Resource getRightResource() {
		try {
			Resource resource = null;
			if (rightResourceComposite.showFileHistory()) {
				IFileRevision revision = rightResourceComposite
						.getRevisionSelected();
				if (revision != null) {
					resource = rightResourceComposite.getResource(revision);
				}
			}
			return resource != null ? resource
					: getResource(rightResourceComposite.getResourceSelected());
		} catch (Exception e) {
			// If page is complete, resource can't be null so this code is
			// unreachable
			return null;
		}
	}

	public String getDiffResource() {
		return diffResourceComposite.getResourceSelected();
	}

}
