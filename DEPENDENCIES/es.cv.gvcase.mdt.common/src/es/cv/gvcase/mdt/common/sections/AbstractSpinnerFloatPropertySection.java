/*******************************************************************************
 * Copyright (c) 2012 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Marc Gil Sendra (Prodevelop) – Initial implementation
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.sections;

import java.util.regex.Pattern;

import org.eclipse.swt.widgets.Composite;

/**
 * Provides an Spinner Section to be used in the Property Tabs
 * 
 * @author mgil
 */
public abstract class AbstractSpinnerFloatPropertySection extends
		AbstractSpinnerPropertySection {

	protected int mult = 1;

	/**
	 * Predefined string pattern value for decimal, absolute with '-' and exp
	 * notation : -25.36
	 */
	public static final String EXP_NUMERIC_PATTERN = "^[-\\d][\\d]*\\.?[\\d]*"; //$NON-NLS-1$

	/** The Pattern used to check a Float value */
	public static final Pattern FLOAT_PATTERN = Pattern
			.compile(EXP_NUMERIC_PATTERN);

	@Override
	protected void createWidgets(Composite composite) {
		super.createWidgets(composite);
		getSpinner().setDigits(getDigits());
		configureDigits();
		getSpinner().setMaximum(getMaximumValue() * mult);
		getSpinner().setIncrement(getSpecifiedIncrement());
		getSpinner().setPageIncrement(getPageIncrement() * mult);
	}

	/**
	 * Get the number of digits for the spinner. By default is 1.
	 */
	protected int getDigits() {
		return 1;
	}

	protected void configureDigits() {
		String mult = "1";
		for (int i = 0; i < getDigits(); i++) {
			mult += "0";
		}
		this.mult = Integer.parseInt(mult);
	}

	/**
	 * Get the increment for the spinner. By default, is 0.5
	 */
	protected int getSpecifiedIncrement() {
		return (int) mult / 2;
	}

	@Override
	protected Integer getFeatureAsInteger() {
		float value = getFeatureValue();
		int val = (int) (value * mult);
		return val;
	}

	@Override
	protected Object getOldFeatureValue() {
		return getFeatureValue();
	}

	@Override
	protected Object getNewFeatureValue(int value) {
		float val = value / (float) mult;
		return val;
	}

	@Override
	protected boolean isTextValid() {
		float val = getSpinner().getSelection() / (float) mult;
		return FLOAT_PATTERN.matcher(String.valueOf(val)).matches()
				&& val >= getMinimumValue() && val <= getMaximumValue();
	}

	protected Float getFeatureValue() {
		return (Float) getEObject().eGet(getFeature());
	}

	@Deprecated
	protected Integer getFeatureInteger() {
		return null;
	}

}
