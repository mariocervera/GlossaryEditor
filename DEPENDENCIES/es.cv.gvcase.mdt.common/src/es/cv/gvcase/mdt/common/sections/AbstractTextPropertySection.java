package es.cv.gvcase.mdt.common.sections;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Composite;

import es.cv.gvcase.mdt.common.sections.descriptions.ControlDescriptionDecoratorProvider;

public abstract class AbstractTextPropertySection
		extends
		org.eclipse.emf.ecoretools.tabbedproperties.sections.AbstractTextPropertySection {

	@Override
	protected EditingDomain getEditingDomain() {
		try {
			return super.getEditingDomain();
		} catch (IllegalArgumentException ex) {
			return null;
		}
	}

	/**
	 * @see org.eclipse.emf.ecoretools.tabbedproperties.sections.AbstractTabbedPropertySection#hookListeners()
	 */
	protected void hookListeners() {
		super.hookListeners();

		getText().addFocusListener(new FocusListener() {

			public void focusGained(FocusEvent e) {
				getText().setSelection(0, getText().getText().length());
			}

			public void focusLost(FocusEvent e) {
			}

		});

	}

	@Override
	protected int getStyle() {
		int style = SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL;
		return style;
	}

	@Override
	public boolean shouldUseExtraSpace() {
		return true;
	}

	@Override
	protected void createWidgets(Composite composite) {
		// create widgets normally
		super.createWidgets(composite);
		// fjcano :: add a control decorator if any
		ControlDescriptionDecoratorProvider.addDescriptionDecoration(getText(),
				getFeature());
	}

	@Override
	protected Object getOldFeatureValue() {
		EObject eObject = getEObject();
		if (eObject != null) {
			EStructuralFeature feature = getFeature();
			if (feature != null) {
				return eObject.eGet(feature);
			}
		}
		return null;
	}

}
