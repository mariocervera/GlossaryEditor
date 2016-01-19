package es.cv.gvcase.mdt.common.sections;

import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.swt.widgets.Composite;

import es.cv.gvcase.mdt.common.sections.descriptions.ControlDescriptionDecoratorProvider;

public abstract class AbstractEnumerationPropertySection
		extends
		org.eclipse.emf.ecoretools.tabbedproperties.sections.AbstractEnumerationPropertySection {

	@Override
	protected EditingDomain getEditingDomain() {
		try {
			return super.getEditingDomain();
		} catch (IllegalArgumentException ex) {
			return null;
		}
	}
	
	@Override
	protected void createWidgets(Composite composite) {
		// create widgets normally
		super.createWidgets(composite);
		// add possible decorations
		ControlDescriptionDecoratorProvider.addDescriptionDecoration(
				getCombo(), getFeature());
	}

}
