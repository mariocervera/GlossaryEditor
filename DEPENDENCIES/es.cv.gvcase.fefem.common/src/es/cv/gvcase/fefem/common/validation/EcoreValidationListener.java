/***************************************************************************
* Copyright (c) 2008 Conselleria de Infraestructuras y Transporte,
* Generalitat de la Comunitat Valenciana . All rights reserved. This program
* and the accompanying materials are made available under the terms of the
* Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors: 
* 	Jose Manuel García Valladolid (CIT/Indra SL) - Initial API and implementation
* 	Adolfo Sanchez-Barbudo Herrera (Open Canarias S.L.)- Bug #4188 Refactoring
*
**************************************************************************/

package es.cv.gvcase.fefem.common.validation;

import java.util.Vector;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.Diagnostician;
import org.eclipse.emf.validation.model.EvaluationMode;
import org.eclipse.emf.validation.service.IValidationListener;
import org.eclipse.emf.validation.service.ValidationEvent;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;

import es.cv.gvcase.fefem.common.FEFEMEditor;
import es.cv.gvcase.fefem.common.FEFEMPage;


/**
 * This class is listening for changes made in:
 *   a) validation status of the model 
 *   b) selection happened on a ISelectionProvider
 *   c) selection of a page in a editor
 *   
 * As a response to this changes, this class obtain a diagnostic for the EObject and this one is
 * passed to any registered IEcoreDiagnosticDispatcher
 * 
 * @author Jose Manuel García Valladolid
 */
public class EcoreValidationListener implements IValidationListener, ISelectionChangedListener, IPageChangedListener {

	protected Vector<IEcoreDiagnosticDispatcher> dispatchers = new Vector<IEcoreDiagnosticDispatcher>();
	protected boolean activated = true;
	protected FEFEMEditor editor;
	
	public EcoreValidationListener(FEFEMEditor editor,boolean activated) {
		super();
		this.activated = activated;
		this.editor = editor;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.validation.service.IValidationListener#validationOccurred(org.eclipse.emf.validation.service.ValidationEvent)
	 */
	public void validationOccurred(ValidationEvent event) {
		
		if(event.getEvaluationMode().equals(EvaluationMode.LIVE))
		{
			for (Object target : event.getValidationTargets())
			{
				if (target instanceof Notification) {
					Notification notification = (Notification) target;
					Object obj = notification.getNotifier();
					if (isAnEditorObject(obj)) {
						obtainDiagnostics((EObject)obj);
					}
				}
			}
		}
	}

	private boolean isAnEditorObject(Object obj) {
		
		if(obj == null)
			return false;
		
		if(obj instanceof EObject){
			EObject eObj = (EObject) obj;
			// Only validates EObjects contained in the same Resource as the FEFEMEditor edits...
			if(eObj.eResource() != null)
				return eObj.eResource().equals(this.editor.getModel().eResource());
			else
				return false;
		}
		return false;
	}

	public void obtainDiagnostics(EObject o){
		
		for(IEcoreDiagnosticDispatcher dispatcher :  dispatchers) {
			dispatcher.prepareDiagnostic();
		}
		
		if(!isActivated())
			return;
		
		Diagnostician diagnostician = org.eclipse.emf.ecore.util.Diagnostician.INSTANCE;
		Diagnostic d = diagnostician.validate(o);

		for (Diagnostic  childDiagnostic : d.getChildren())
		{
			for(IEcoreDiagnosticDispatcher dispatcher :  dispatchers)
			{
				dispatcher.dispatchDiagnostic(childDiagnostic);
			}
		}
	}
	
	public void selectionChanged(SelectionChangedEvent event) {
		// Selection filter
		// Only admit Structured selections, avoid text selections...
		if(event.getSelection() instanceof StructuredSelection){
			Object first = ((StructuredSelection) event.getSelection()).getFirstElement();
			if(isAnEditorObject(first)) {
				obtainDiagnostics((EObject) first);
			}
		}
	}

	public void registerDiagnosticDispatcher(IEcoreDiagnosticDispatcher i){
		if(!dispatchers.contains(i))
			dispatchers.add(i);
	}

	public void unregisterDiagnosticDispatcher(IEcoreDiagnosticDispatcher i){
		if(dispatchers.contains(i))
			dispatchers.remove(i);
	}

	public void pageChanged(PageChangedEvent event) {
		Object page = event.getSelectedPage();
		if (page instanceof FEFEMPage) {
			obtainDiagnostics(((FEFEMPage) page).getEditor().getModel());
		}
	}

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}
}
