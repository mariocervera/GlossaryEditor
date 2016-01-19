/*******************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Jose Manuel García Valladolid (Indra SL- CIT) – Initial implementation
 *
 ******************************************************************************/
package es.cv.gvcase.fefem.common.utils;

import java.util.Vector;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.part.Page;

import es.cv.gvcase.fefem.common.FEFEMEditor;
import es.cv.gvcase.fefem.common.composites.EMFContainedCollectionEditionComposite;
import es.cv.gvcase.fefem.common.composites.EMFContainedHierarchicalCollectionEditionComposite;
import es.cv.gvcase.fefem.common.composites.EMFPropertyComposite;

/**
 * Default implementation of a strategy for synchronnize external EObject selections with the current editor
 * 
 * This implementations searchs for existing Structured Viewers having the given EObject, makes
 * the Page containing them active  and gives the focus to the viewer control
 * 
 * @author Jose Manuel García Valladolid (garcia_josval@gva.es)
 *
 */
public class DefaultSelectionSynchronizerStrategy implements
		ISelectionSynchronizerStrategy {

	/* (non-Javadoc)
	 * @see es.cv.gvcase.fefem.common.utils.IEObjectSelectionSynchronizerStrategy#synchronizeEObjectSelection(org.eclipse.emf.ecore.EObject, es.cv.gvcase.fefem.common.FEFEMEditor)
	 */
	public void synchronizeEObjectSelection(ISelection selection, FEFEMEditor editor, Vector<Page> pages) {
		
		// Search for Structured FEFEM composites being able to represent this EObject
		Object s = ((IStructuredSelection) selection).getFirstElement();
		if(!(s instanceof EObject))
			return;
		
		EObject o = (EObject) s;
		EStructuralFeature f = o.eContainingFeature(); 
		for(int p=0;p<pages.size()	;p++){
			Object currentp = pages.get(p);
			if(currentp instanceof FormPage){
				EMFPropertyComposite pc = searchStructuredFEFEMComposite(((FormPage) currentp).getPartControl(), f);
				if(pc!=null){
					if(pc instanceof EMFContainedCollectionEditionComposite){
						editor.setActivePage(((IFormPage) currentp).getId());
						((EMFContainedCollectionEditionComposite) pc).getViewer().setSelection(new StructuredSelection(o));
					}else if(pc instanceof EMFContainedHierarchicalCollectionEditionComposite){
						editor.setActivePage(((IFormPage) currentp).getId());
						((EMFContainedHierarchicalCollectionEditionComposite) pc).getViewer().expandAll();
						if(selection instanceof TreeSelection){
							((EMFContainedHierarchicalCollectionEditionComposite) pc).getViewer().setSelection(new TreeSelection(((TreeSelection) selection).getPaths()));
						}else
							((EMFContainedHierarchicalCollectionEditionComposite) pc).getViewer().setSelection(new StructuredSelection(o));
						
					}
				}
			}
		}

	}
	
	private EMFPropertyComposite searchStructuredFEFEMComposite(Control c, EStructuralFeature f){
		if(c == null)
			return null;
		
		if (c instanceof EMFPropertyComposite){
			if(((EMFPropertyComposite) c).getEStructuralFeature() != null)
				if (((EMFPropertyComposite) c).getEStructuralFeature().equals(f))
					return (EMFPropertyComposite) c;
			else if(c instanceof EMFContainedHierarchicalCollectionEditionComposite){
				if(((EMFContainedHierarchicalCollectionEditionComposite) c).getReflexiveEStructuralFeature()!=null)
					if(((EMFContainedHierarchicalCollectionEditionComposite) c).getReflexiveEStructuralFeature().equals(f))
						return (EMFPropertyComposite) c;
				
			}
		}
		
		if(c instanceof Composite){
			Control[] childs = ((Composite) c).getChildren();
			if(childs != null)
				for (Control child : childs) {
					EMFPropertyComposite cmp = searchStructuredFEFEMComposite(child, f);
					if (cmp != null)
						return cmp;
				}
		}
		
		return null;
	}

}
