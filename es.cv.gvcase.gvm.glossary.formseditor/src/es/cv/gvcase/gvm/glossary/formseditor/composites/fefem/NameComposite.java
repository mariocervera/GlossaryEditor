package es.cv.gvcase.gvm.glossary.formseditor.composites.fefem;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import es.cv.gvcase.fefem.common.FEFEMPage;
import es.cv.gvcase.fefem.common.composites.EMFPropertyStringComposite;
import es.cv.gvcase.gvm.glossary.GlossaryPackage;
import es.cv.gvcase.gvm.glossary.formseditor.internal.Messages;

public class NameComposite extends EMFPropertyStringComposite {

	public NameComposite(Composite parent, int style, FormToolkit toolkit,
			EObject object, FEFEMPage page) {
		super(parent, style, toolkit, object, page);
	}
	
	public NameComposite(Composite parent, int style, FormToolkit toolkit,
			Viewer viewer, FEFEMPage page) {
		super(parent, style, toolkit, viewer, page);
	}

	@Override
	protected String getLabelText() {
		return Messages.NameComposite_Name + ':';
	}

	@Override
	protected EStructuralFeature getFeature() {
		return GlossaryPackage.eINSTANCE.getTerm_Name();
	}

}
