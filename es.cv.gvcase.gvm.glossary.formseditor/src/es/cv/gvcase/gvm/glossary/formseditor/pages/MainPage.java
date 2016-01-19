/***************************************************************************
* Copyright (c) 2008 Conselleria de Infraestructuras y Transporte,
* Generalitat de la Comunitat Valenciana . All rights reserved. This program
* and the accompanying materials are made available under the terms of the
* Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors: Mario Cervera Ubeda (Integranova) - Initial API and implementation
*
**************************************************************************/
package es.cv.gvcase.gvm.glossary.formseditor.pages;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import es.cv.gvcase.fefem.common.FEFEMPage;
import es.cv.gvcase.gvm.glossary.LocalTerm;
import es.cv.gvcase.gvm.glossary.RedefinedTerm;
import es.cv.gvcase.gvm.glossary.ReferencedTerm;
import es.cv.gvcase.gvm.glossary.formseditor.composites.LocalTermDetailsComposite;
import es.cv.gvcase.gvm.glossary.formseditor.composites.RedefinedTermDetailsComposite;
import es.cv.gvcase.gvm.glossary.formseditor.composites.ReferencedTermDetailsComposite;
import es.cv.gvcase.gvm.glossary.formseditor.composites.TermDetailsComposite;
import es.cv.gvcase.gvm.glossary.formseditor.composites.fefem.DetailsComposite;
import es.cv.gvcase.gvm.glossary.formseditor.composites.fefem.NameComposite;
import es.cv.gvcase.gvm.glossary.formseditor.composites.fefem.TableOfTermsComposite;
import es.cv.gvcase.gvm.glossary.formseditor.internal.Messages;

public class MainPage extends FEFEMPage {

	private Composite leftColumnComposite;
	private Composite rightColumnComposite;
	
	private TableOfTermsComposite tableOfTermsComposite;
	private DetailsComposite detailsGroup;
	
	/** This two composites have detailsGroup as parent */
	private Composite fixedPropertiesComposite;
	private TermDetailsComposite termDetailsComposite;
	
	private Section detailsSection;
	

	public MainPage(FormEditor editor) {
		super(editor, "es.cv.gvcase.glossary.configpage",
				Messages.MainPage_Glossary);
	}

	public MainPage(FormEditor editor, String id, String title) {
		super(editor, id, title);
	}
	
	public TableViewer getTermsTableViewer() {
		return tableOfTermsComposite.getViewer();
	}
	
	public Section getDetailsSection() {
		return detailsSection;
	}
	
	
	public TermDetailsComposite getTermDetailsComposite() {
		return termDetailsComposite;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
	 */
	@Override
	protected void createFormContent(IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		toolkit = managedForm.getToolkit();
		form.setText(this.getTitle()); //$NON-NLS-1$
		toolkit.decorateFormHeading(form.getForm());

		GridLayout layout = new GridLayout();
		layout.marginLeft = 10;
		layout.marginRight = 10;
		layout.marginTop = 5;
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = true;

		form.getBody().setLayout(layout);

		GridLayout columnLayout = new GridLayout();
		columnLayout.numColumns = 1;

		leftColumnComposite = toolkit.createComposite(form.getBody());

		GridData leftColumnLayoutData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING
				| GridData.VERTICAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_FILL
				| GridData.HORIZONTAL_ALIGN_FILL);

		leftColumnComposite.setLayout(columnLayout);
		leftColumnComposite.setLayoutData(leftColumnLayoutData);

		rightColumnComposite = toolkit.createComposite(form.getBody());
		
		GridData rightColumnLayoutData = new GridData(GridData.FILL_BOTH
				| GridData.VERTICAL_ALIGN_BEGINNING);
		
		rightColumnComposite.setLayout(columnLayout);
		rightColumnComposite.setLayoutData(rightColumnLayoutData);

		createTermsListSection(leftColumnComposite);
		createDetailsSection(rightColumnComposite);

		getTermsTableViewer().refresh();
	}
	
	private void createTermsListSection(Composite parent) {

		Section section = this.createSection(parent, toolkit, Messages.MainPage_Terms,
				null);

		createTableOfTerms(section);

	}
	
	private void createDetailsSection(Composite parent) {

		detailsSection = this.createSection(parent, toolkit, Messages.MainPage_Details,
				null);
		
		detailsGroup = new DetailsComposite(detailsSection, SWT.NONE, toolkit, getTermsTableViewer(), this);
		detailsGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		fixedPropertiesComposite = toolkit.createComposite(detailsGroup.getGroup());
		fixedPropertiesComposite.setLayout(new GridLayout(1, false));
		fixedPropertiesComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		NameComposite nc = new NameComposite(fixedPropertiesComposite, SWT.NONE,
				toolkit, getTermsTableViewer(), this);
		nc.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	private void createTableOfTerms(Section parent) {

		tableOfTermsComposite = new TableOfTermsComposite(parent, SWT.NONE, toolkit, getEditor().getModel(), this);
		
		GridData gd = new GridData(GridData.FILL_BOTH);
		tableOfTermsComposite.setLayoutData(gd);
		parent.setClient(tableOfTermsComposite);
		parent.layout();
	}
	
	
	public void createTermDetails(EObject term) {
		
		if(term instanceof RedefinedTerm) {
			termDetailsComposite = new RedefinedTermDetailsComposite(detailsGroup.getGroup(), SWT.NONE,
					toolkit, (RedefinedTerm) term, this);
		}
		else if(term instanceof LocalTerm) {
			termDetailsComposite = new LocalTermDetailsComposite(detailsGroup.getGroup(), SWT.NONE,
					toolkit, (LocalTerm) term, this);
		}
		else if(term instanceof ReferencedTerm) {
			termDetailsComposite = new ReferencedTermDetailsComposite(detailsGroup.getGroup(), SWT.NONE,
					toolkit, (ReferencedTerm) term, this);
		}
		
		if(termDetailsComposite != null) {
			termDetailsComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			detailsSection.setClient(detailsGroup);
			detailsGroup.pack();
			detailsSection.layout();
		}
	}
	
	public void refresh(){
		getTermsTableViewer().refresh();
	}

}
