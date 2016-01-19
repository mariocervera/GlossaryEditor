package es.cv.gvcase.mdt.common.provider;

import org.eclipse.jface.wizard.IWizard;

public interface IMOSKittEditorFactory2 extends IMOSKittEditorFactory {

	IWizard createWizardFor(String fileExtension);

}
