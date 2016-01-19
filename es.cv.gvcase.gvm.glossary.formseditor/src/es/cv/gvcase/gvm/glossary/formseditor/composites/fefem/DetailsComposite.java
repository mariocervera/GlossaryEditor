package es.cv.gvcase.gvm.glossary.formseditor.composites.fefem;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import es.cv.gvcase.fefem.common.FEFEMPage;
import es.cv.gvcase.fefem.common.composites.EMFEObjectComposite;
import es.cv.gvcase.gvm.glossary.GlossaryPackage;

public class DetailsComposite extends EMFEObjectComposite {

	public DetailsComposite(Composite parent, int style, FormToolkit toolkit, EObject eObject, FEFEMPage page) {
		super(parent, style, toolkit, eObject, page);
	}
	
	public DetailsComposite(Composite parent, int style, FormToolkit toolkit, Viewer viewer, FEFEMPage page) {
		super(parent, style, toolkit, viewer, page);
	}

	@Override
	protected EStructuralFeature getFeature() {
		return GlossaryPackage.eINSTANCE.getTerm_Name();
	}

	

}
