package es.cv.gvcase.gvm.glossary.formseditor.composites.fefem;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import es.cv.gvcase.fefem.common.composites.EMFPropertyEReferenceComposite;
import es.cv.gvcase.gvm.glossary.GlossaryPackage;
import es.cv.gvcase.gvm.glossary.ReferencedTerm;
import es.cv.gvcase.gvm.glossary.formseditor.internal.Messages;
import es.cv.gvcase.gvm.glossary.formseditor.pages.MainPage;
import es.cv.gvcase.gvm.glossary.formseditor.providers.SourceTermsLabelProvider;

public class SourceComposite extends EMFPropertyEReferenceComposite {

	
	public SourceComposite(Composite parent, int style,
			FormToolkit toolkit, ReferencedTerm referencedTerm,
			MainPage page) {
		super(parent, style, toolkit, referencedTerm, page);

	}
	
	@Override
	protected Object[] getChoices() {

		if (getEObject() != null){
			List<Object> choices = new ArrayList<Object>();
			choices.add(null);
			choices.addAll(ItemPropertyDescriptor.getReachableObjectsOfType(getEObject(), GlossaryPackage.eINSTANCE.getTerm()));
			
			return choices.toArray();
		} else {
			return new Object[] {};
		}		
	}

	@Override
	protected String getLabelText() {
		return Messages.SourceComposite_Source + ':';
	}

	@Override
	protected ILabelProvider getLabelProvider() {
		return new SourceTermsLabelProvider();
	}

	@Override
	protected EStructuralFeature getFeature() {
		return GlossaryPackage.eINSTANCE.getReferencedTerm_Source();
	}
}
