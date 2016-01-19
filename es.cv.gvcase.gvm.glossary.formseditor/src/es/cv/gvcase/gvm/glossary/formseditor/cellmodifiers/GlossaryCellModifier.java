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
package es.cv.gvcase.gvm.glossary.formseditor.cellmodifiers;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.TableItem;

import es.cv.gvcase.fefem.common.FEFEMPage;
import es.cv.gvcase.gvm.glossary.GlossaryPackage;
import es.cv.gvcase.gvm.glossary.Term;

public class GlossaryCellModifier implements ICellModifier{

	private FEFEMPage page;
	
	private static final String NAME_COLUMN = "Name";

	
	public GlossaryCellModifier(FEFEMPage page) {
		this.page = page;
	}
	
	public boolean canModify(Object element, String property) {
		
		if(property.equals(NAME_COLUMN)) {
			return true;
		}
		
		return false;
	}

	public Object getValue(Object element, String property) {

		if(element instanceof Term) {
			Term term = (Term) element;
			if(property.equals(NAME_COLUMN)) return term.getName();
		}
		
		return null;
	}

	public void modify(Object element, String property, Object value) {
		
		TransactionalEditingDomain domain = page.getEditor().getEditingDomain();
		
		if(domain == null) return;
		
		if(element instanceof TableItem) {
			Object data = ((TableItem) element).getData();
			if(data instanceof Term) {
				Term term = (Term) data;
				if(property.equals(NAME_COLUMN)) {
					
					if(value != null && term.getName() != null &&
							value.toString().equals(term.getName())) return;
					
					Command c = SetCommand.create(domain, term,
							GlossaryPackage.eINSTANCE.getTerm_Name(), value);
					domain.getCommandStack().execute(c);
					
					page.setDirty(true);
					
					page.refresh();
				}
			}
		}
	}

}
