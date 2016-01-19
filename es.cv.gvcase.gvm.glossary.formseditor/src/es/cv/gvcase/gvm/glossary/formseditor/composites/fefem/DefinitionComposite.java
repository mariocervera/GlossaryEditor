package es.cv.gvcase.gvm.glossary.formseditor.composites.fefem;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import es.cv.gvcase.fefem.common.FEFEMPage;
import es.cv.gvcase.fefem.common.composites.EMFPropertyStringTextComposite;
import es.cv.gvcase.gvm.glossary.GlossaryPackage;
import es.cv.gvcase.gvm.glossary.formseditor.internal.Messages;

public class DefinitionComposite extends EMFPropertyStringTextComposite {
	
	public DefinitionComposite(Composite parent, int style,
			FormToolkit toolkit, EObject object, FEFEMPage page) {
		super(parent, style, toolkit, object, page);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String getLabelText() {
		return Messages.DefinitionComposite_Definition + ':';
	}

	@Override
	protected EStructuralFeature getFeature() {
		return GlossaryPackage.eINSTANCE.getLocalTerm_Definition();
	}

}
