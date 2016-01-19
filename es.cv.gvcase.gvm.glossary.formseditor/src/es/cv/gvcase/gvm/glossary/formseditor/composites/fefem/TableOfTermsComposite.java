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
package es.cv.gvcase.gvm.glossary.formseditor.composites.fefem;

import java.util.ArrayList;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.widgets.FormToolkit;

import es.cv.gvcase.fefem.common.FEFEMPage;
import es.cv.gvcase.fefem.common.composites.EMFContainedCollectionEditionComposite;
import es.cv.gvcase.gvm.glossary.GlossaryFactory;
import es.cv.gvcase.gvm.glossary.GlossaryPackage;
import es.cv.gvcase.gvm.glossary.Term;
import es.cv.gvcase.gvm.glossary.formseditor.cellmodifiers.GlossaryCellModifier;
import es.cv.gvcase.gvm.glossary.formseditor.filters.GlossaryFilter;
import es.cv.gvcase.gvm.glossary.formseditor.internal.Messages;
import es.cv.gvcase.gvm.glossary.formseditor.pages.MainPage;
import es.cv.gvcase.gvm.glossary.formseditor.providers.GlossaryLabelProvider;
import es.cv.gvcase.gvm.glossary.formseditor.sorters.NameSorter;
import es.cv.gvcase.gvm.glossary.formseditor.sorters.TypeSorter;

public class TableOfTermsComposite extends EMFContainedCollectionEditionComposite {
	
	private static final String NAME_COLUMN = Messages.TableOfTermsComposite_Name;
	private static final String TYPE_COLUMN = Messages.TableOfTermsComposite_Type;
	
	public TableOfTermsComposite(Composite parent, int style, FormToolkit toolkit,EObject eobject,
			FEFEMPage page) {
		super(parent, style, toolkit, eobject, page);
		
		getViewer().addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				handleSelectionChanged();
			}
		});
	}


	@Override
	protected ICellModifier getCellModifier(FEFEMPage page) {
		return new GlossaryCellModifier(page);
	}
	
	protected CellEditor[] getCellEditors(Table table){
		CellEditor[] cellEditors = new CellEditor[1];
		cellEditors[0] = new TextCellEditor(table);
		return cellEditors;
	}



	@Override
	protected String[] getColumNames() {
		return new String [] {NAME_COLUMN, TYPE_COLUMN};
	}


	@Override
	protected EStructuralFeature getFeature() {
		return GlossaryPackage.eINSTANCE.getGlossary_ContainedTerms();
	}


	@Override
	protected CellLabelProvider getLabelProvider() {
		return new GlossaryLabelProvider();
	}
	
	protected void handleSelectionChanged() {
		MainPage page = ((MainPage) this.getPage());
		
		StructuredSelection selection = (StructuredSelection)getViewer().getSelection();
		
		if(page != null && page.getTermDetailsComposite() != null &&
				!page.getTermDetailsComposite().isDisposed()) {
			
			page.getTermDetailsComposite().dispose();
		}
		
		if(selection.getFirstElement() instanceof EObject) {
				EObject selectedTerm = (EObject) selection.getFirstElement();
				page.createTermDetails(selectedTerm);
				
				page.getEditor().getEcoreValidationListener().obtainDiagnostics(selectedTerm);
		}

	}	
	
	protected ViewerSorter getSorter(String columName) {
		if (columName.equals(NAME_COLUMN)){
			return new NameSorter();
		} else if (columName.equals(TYPE_COLUMN)){
			return new TypeSorter();
		}
		return null;
	}
	
	protected void createAddAndRemoveButtons(Composite container,
			FormToolkit toolkit) {
		
		if(this.addButtons == null) {
			this.addButtons = new ArrayList<Button>();
		}
		
		//Add Local Term button
		Button addLocalTermButton = toolkit.createButton(container, Messages.TableOfTermsComposite_AddLocalTerm, SWT.NONE);
		addLocalTermButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.addButtons.add(addLocalTermButton);
		addLocalTermButton.addSelectionListener(new SelectionAdapter()  {
			public void widgetSelected(SelectionEvent event) {
				Term t = GlossaryFactory.eINSTANCE.createLocalTerm();
				t.setName(Messages.TableOfTermsComposite_NewLocalTerm);
				addNewTerm(t);
			}
		});
		
		
		//Add Referenced Term button
		Button addReferencedTermButton = toolkit.createButton(container, Messages.TableOfTermsComposite_AddRefTerm, SWT.NONE);
		addReferencedTermButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.addButtons.add(addReferencedTermButton);
		addReferencedTermButton.addSelectionListener(new SelectionAdapter()  {
			public void widgetSelected(SelectionEvent event) {;				
				Term t = GlossaryFactory.eINSTANCE.createReferencedTerm();
				t.setName(Messages.TableOfTermsComposite_NewReferencedTerm);
				addNewTerm(t);
			}
		});

		
		//Add Redefined Term button
		Button addRedefinedTermButton = toolkit.createButton(container, Messages.TableOfTermsComposite_AddRedTerm, SWT.NONE);
		addRedefinedTermButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.addButtons.add(addRedefinedTermButton);
		addRedefinedTermButton.addSelectionListener(new SelectionAdapter()  {
			public void widgetSelected(SelectionEvent event) {;
				Term t = GlossaryFactory.eINSTANCE.createRedefinedTerm();
				t.setName(Messages.TableOfTermsComposite_NewRedefinedTerm);
				addNewTerm(t);
			}
		});
		
		if(this.removeButtons == null) {
			this.removeButtons = new ArrayList<Button>();
		}
		
		//Remove button
		Button removeButton = toolkit.createButton(container, Messages.TableOfTermsComposite_RemoveTerm, SWT.NONE);
		removeButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		SelectionListener removeButtonListener = getRemoveButtonSelectionListener();
		if (removeButtonListener != null){
			removeButton.addSelectionListener(removeButtonListener);
		}
		this.removeButtons.add(removeButton);

	}
	
	private void addNewTerm(EObject newElement) {
		
		if(modelObservable != null) {					
			modelObservable.add(newElement);
			getPage().setDirty(true);
			getViewer().setSelection(new StructuredSelection(newElement));
		}
	}
	
	@Override
	public boolean isDetailComposite() {
		return false;
	}
	
	@Override
	protected boolean canFilter() {
		return true;
	}
	
	@Override
	protected ViewerFilter getSearchFilter() {
		return new GlossaryFilter(getSearchText());
	}

}
