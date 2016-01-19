/*******************************************************************************
 * Copyright (c) 2005 AIRBUS FRANCE. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Copyright (c) of modifications Conselleria de Infraestructuras y Transporte, 
 * Generalitat de la Comunitat Valenciana. All rights reserved. Modifications 
 * are made available under the terms of the Eclipse Public License v1.0.
 * 
 * Contributors: Jacques Lescot (Anyware Technologies) - initial API and
 * implementation
 * 				 Javier Mu�oz (Integranova) - support to use with GMF editors
 * 				 Marc Gil Sendra (Prodevelop) - generalize this section
 ******************************************************************************/
package es.cv.gvcase.mdt.common.sections;

import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecoretools.tabbedproperties.sections.AbstractTabbedPropertySection;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.gmf.runtime.diagram.ui.properties.views.TextChangeHelper;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;

import es.cv.gvcase.mdt.common.sections.descriptions.ControlDescriptionDecoratorProvider;

/**
 * The section that manage the cardinality (lowerBound and UpperBound) of a
 * MultiplicityElement.
 * 
 * Creation 16 févr. 07
 * 
 * @author <a href="mailto:jacques.lescot@anyware-tech.com">Jacques LESCOT</a>
 */
public abstract class AbstractCardinalityPropertySection extends
		AbstractTabbedPropertySection {
	/** The text control for the lower bound. */
	private Text lowerBoundText;

	/** The text control for the upper bound. */
	private Text upperBoundText;

	/**
	 * A helper to listen for events that indicate that a text field has been
	 * changed.
	 */
	private TextChangeHelper lowerBoundListener;

	/**
	 * A helper to listen for events that indicate that a text field has been
	 * changed.
	 */
	private TextChangeHelper upperBoundListener;

	private Group cardinalityGroup;

	private CLabel lowerBoundLabel;

	private CLabel upperBoundLabel;

	@Override
	protected EditingDomain getEditingDomain() {
		try {
			return super.getEditingDomain();
		} catch (IllegalArgumentException ex) {
			return null;
		}
	}
	
	/**
	 * @see org.topcased.tabbedproperties.sections.AbstractTabbedPropertySection#createWidgets(org.eclipse.swt.widgets.Composite)
	 */
	protected void createWidgets(Composite composite) {
		cardinalityGroup = getWidgetFactory().createGroup(composite,
				getLabelText());

		lowerBoundLabel = getWidgetFactory().createCLabel(cardinalityGroup,
				getLowerBoundLabel());
		lowerBoundText = getWidgetFactory().createText(cardinalityGroup,
				getLowerBoundLabel(), SWT.BORDER | SWT.FLAT);
		getWidgetFactory().adapt(lowerBoundText, true, true);

		upperBoundLabel = getWidgetFactory().createCLabel(cardinalityGroup,
				getUpperBoundLabel());
		upperBoundText = getWidgetFactory().createText(cardinalityGroup,
				getUpperBoundLabel(), SWT.BORDER | SWT.FLAT);
		getWidgetFactory().adapt(upperBoundText, true, true);

		// add control decorators that show features descriptions
		ControlDescriptionDecoratorProvider.addDescriptionDecoration(
				lowerBoundText, getLowerFeature());
		// add control decorators that show features descriptions
		ControlDescriptionDecoratorProvider.addDescriptionDecoration(
				upperBoundText, getUpperFeature());
	}

	/**
	 * @see org.topcased.tabbedproperties.sections.AbstractTabbedPropertySection#setSectionData(org.eclipse.swt.widgets.Composite)
	 */
	protected void setSectionData(Composite composite) {
		FormData data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
		cardinalityGroup.setLayoutData(data);

		FormLayout layout = new FormLayout();
		layout.marginWidth = ITabbedPropertyConstants.HSPACE;
		layout.marginHeight = ITabbedPropertyConstants.VSPACE;
		layout.spacing = ITabbedPropertyConstants.VMARGIN;
		cardinalityGroup.setLayout(layout);

		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(lowerBoundText,
				-ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(lowerBoundText, 0, SWT.CENTER);
		lowerBoundLabel.setLayoutData(data);

		data = new FormData();
		data.left = new FormAttachment(0, getStandardLabelWidth(
				cardinalityGroup, new String[] { getLowerBoundLabel() }));
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(lowerBoundLabel, 0, SWT.CENTER);
		lowerBoundText.setLayoutData(data);

		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(upperBoundText,
				-ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(lowerBoundLabel,
				ITabbedPropertyConstants.VSPACE);
		upperBoundLabel.setLayoutData(data);

		data = new FormData();
		data.left = new FormAttachment(0, getStandardLabelWidth(
				cardinalityGroup, new String[] { getUpperBoundLabel() }));
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(upperBoundLabel, 0, SWT.CENTER);
		upperBoundText.setLayoutData(data);
	}

	/**
	 * @see org.topcased.tabbedproperties.sections.AbstractTabbedPropertySection#hookListeners()
	 */
	protected void hookListeners() {
		lowerBoundListener = new TextChangeHelper() {
			public void textChanged(Control control) {
				handleLowerBoundModified();
			}
		};
		lowerBoundListener.startListeningTo(lowerBoundText);
		lowerBoundListener.startListeningForEnter(lowerBoundText);

		upperBoundListener = new TextChangeHelper() {
			public void textChanged(Control control) {
				handleUpperBoundModified();
			}
		};
		upperBoundListener.startListeningTo(upperBoundText);
		upperBoundListener.startListeningForEnter(upperBoundText);
	}

	/**
	 * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#refresh()
	 */
	public void refresh() {
		if (!lowerBoundText.isDisposed() && !upperBoundText.isDisposed()) {
			lowerBoundText.setText(StringConverter.asString(getLowerValue()));
			int upper = getUpperValue();
			if (upper == -1) {
				upperBoundText.setText("*");
			} else {
				upperBoundText.setText(StringConverter.asString(upper));
			}
		}
	}

	/**
	 * Handle the lower bound text modified event.
	 */
	protected void handleLowerBoundModified() {
		if (getEObject() == null)
			return;

		String newText = lowerBoundText.getText();
		boolean equals = isEqual(newText, getLowerValue());
		if (!equals) {
			if (isValidMultiplicity()) {

				CompoundCommand compoundCommand = new CompoundCommand();
				Object value = new Integer(Integer.parseInt(newText));
				if (getEObjectList().size() == 1) {
					/* apply the property change to single selected object */
					compoundCommand.append(SetCommand.create(
							getEditingDomain(), getEObject(),
							getLowerFeature(), value));
				} else {
					/* apply the property change to all selected elements */
					for (EObject nextObject : getEObjectList()) {
						compoundCommand.append(SetCommand.create(
								getEditingDomain(), nextObject,
								getLowerFeature(), value));
					}
				}
				getEditingDomain().getCommandStack().execute(compoundCommand);
				refresh();
			}
		}
	}

	/**
	 * Handle the lower bound text modified event.
	 */
	protected void handleUpperBoundModified() {
		if (getEObject() == null)
			return;

		String newText = upperBoundText.getText();
		if (newText.equals("*")) {
			newText = "-1";
		}
		boolean equals = isEqual(newText, getUpperValue());
		if (!equals) {
			if (isValidMultiplicity()) {
				CompoundCommand compoundCommand = new CompoundCommand();
				Object value = new Integer(Integer.parseInt(newText));
				if (getEObjectList().size() == 1) {
					/* apply the property change to single selected object */
					compoundCommand.append(SetCommand.create(
							getEditingDomain(), getEObject(),
							getUpperFeature(), value));
				} else {
					/* apply the property change to all selected elements */
					for (EObject nextObject : getEObjectList()) {
						compoundCommand.append(SetCommand.create(
								getEditingDomain(), nextObject,
								getUpperFeature(), value));
					}
				}
				getEditingDomain().getCommandStack().execute(compoundCommand);
				refresh();
			}
		}
	}

	protected boolean isValidMultiplicity() {

		String lowerText = getLowerBoundText().getText();
		String upperText = getUpperBoundText().getText();

		if (lowerText.equals("*")) {
			setErrorMessage("Lower bound can not have many multiplicity.");
			return false;
		}

		if (upperText.equals("*")) {
			upperText = "-1";
		}

		int upperValue = new Integer(Integer.parseInt(upperText));
		int lowerValue = new Integer(Integer.parseInt(lowerText));

		if (lowerValue < 0) {
			setErrorMessage("Lower bound must be a positive integer or 0.");
			return false;
		}

		if ((lowerValue > upperValue) && (upperValue != -1)) {
			setErrorMessage("Lower bound can not be greater than upper bound.");
			return false;
		}

		setErrorMessage("");
		return true;
	}

	/**
	 * Determine if the provided string value is an equal representation of the
	 * current setting of the text property.
	 * 
	 * @param newText
	 *            the new string value.
	 * @param valueToCompare
	 *            the int value to compare with the text value
	 * @return <code>true</code> if the new string value is equal to the passed
	 *         int value.
	 */
	protected boolean isEqual(String newText, int valueToCompare) {
		try {
			Integer.parseInt(newText);
		} catch (NumberFormatException e) {
			refresh();
			return true;
		}
		Integer integer = new Integer(Integer.parseInt(newText));
		return new Integer(valueToCompare).equals(integer);
	}

	/**
	 * Get the EAttribute for the lower bound object
	 * 
	 * @return the EAttribute
	 */
	protected abstract EAttribute getLowerFeature();

	/**
	 * Get the EAttribute for the upper bound object
	 * 
	 * @return the EAttribute
	 */
	protected abstract EAttribute getUpperFeature();

	/**
	 * Get the label for the lower bound text field for the section.
	 * 
	 * @return the label for the lower bound text field.
	 */
	protected String getLowerBoundLabel() {
		return "Lower Bound:";
	}

	/**
	 * Get the label for the upper bound text field for the section.
	 * 
	 * @return the label for the upper bound text field.
	 */
	protected String getUpperBoundLabel() {
		return "Upper Bound:";
	}

	/**
	 * @see org.topcased.tabbedproperties.sections.AbstractTabbedPropertySection#getFeature()
	 */
	protected EStructuralFeature getFeature() {
		return null;
	}

	/**
	 * Return the Text used to edit the lowerBound value of the Multiplicity
	 * Element
	 * 
	 * @return Text
	 */
	protected Text getLowerBoundText() {
		return lowerBoundText;
	}

	/**
	 * Return the Text used to edit the upperBound value of the Multiplicity
	 * Element
	 * 
	 * @return Text
	 */
	protected Text getUpperBoundText() {
		return upperBoundText;
	}

	/**
	 * @see org.topcased.tabbedproperties.sections.AbstractTabbedPropertySection#getLabelText()
	 */
	protected abstract String getLabelText();

	/**
	 * gets the lower value as int
	 * 
	 * @return int
	 */
	protected abstract int getLowerValue();

	/**
	 * gets the upper value as int
	 * 
	 * @return int
	 */
	protected abstract int getUpperValue();
}