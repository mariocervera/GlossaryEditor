package es.cv.gvcase.gvm.glossary.formseditor;

import org.eclipse.emf.common.EMFPlugin;
import org.eclipse.emf.ecore.EPackage;

import es.cv.gvcase.fefem.common.FEFEMModelWizard;
import es.cv.gvcase.gvm.glossary.GlossaryPackage;
import es.cv.gvcase.gvm.glossary.presentation.GlossaryEditorPlugin;
import es.cv.gvcase.gvm.glossary.provider.GlossaryEditPlugin;

public class GlossaryModelWizard extends FEFEMModelWizard {

	@Override
	protected EPackage getEPackage() {
		return GlossaryPackage.eINSTANCE;
	}

	@Override
	protected EMFPlugin getEditPlugin() {
		return GlossaryEditPlugin.INSTANCE;
	}

	@Override
	protected EMFPlugin getEditorPlugin() {
		return GlossaryEditorPlugin.INSTANCE;
	}

	@Override
	protected String getModelName() {		
		return "Glossary";
	}

	@Override
	protected String getInitialObjectName() {		
		return "Glossary";
	}

}
