/*******************************************************************************
 * Copyright (c) 2012 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Marc Gil Sendra (Prodevelop) – Initial implementation
 ******************************************************************************/
package es.cv.gvcase.mdt.common.sections;

import java.util.regex.Pattern;

import org.eclipse.emf.ecoretools.tabbedproperties.internal.Messages;
import org.eclipse.emf.ecoretools.tabbedproperties.internal.utils.ColorRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;

@SuppressWarnings("restriction")
public abstract class AbstractFloatPropertySection extends
		AbstractTextPropertySection {

	/**
	 * Predefined string pattern value for decimal, absolute with '-' and exp
	 * notation : -25.36
	 */
	public static final String EXP_NUMERIC_PATTERN = "^[-\\d][\\d]*\\.?[\\d]*"; //$NON-NLS-1$

	/** The Pattern used to check a Float value */
	public static final Pattern FLOAT_PATTERN = Pattern
			.compile(EXP_NUMERIC_PATTERN);

	protected void verifyField(Event e) {
		String value = getText().getText();
		if (value == null || value.equals("") || isTextValid()) { //$NON-NLS-1$
			setErrorMessage(null);
			getText().setBackground(null);
			e.doit = true;
		} else {
			setErrorMessage(Messages.AbstractDoublePropertySection_NotValid);
			getText().setBackground(ColorRegistry.COLOR_ERROR);
			e.doit = false;
		}
	}

	protected String getFeatureAsString() {
		return getFeatureFloat().toString();
	}

	@Override
	protected Object getOldFeatureValue() {
		return getFeatureFloat();
	}

	protected Object getNewFeatureValue(String newText) {
		if (newText == null || newText.equals("")) { //$NON-NLS-1$
			return null;
		}
		return new Float(Float.parseFloat(newText));
	}

	/**
	 * Get the text value of the feature for the text field for the section.
	 * 
	 * @return the text value of the feature.
	 */
	protected abstract Float getFeatureFloat();

	@Override
	protected boolean isTextValid() {
		return FLOAT_PATTERN.matcher(getText().getText()).matches();
	}

	@Override
	protected int getStyle() {
		return SWT.SINGLE;
	}

	@Override
	public boolean shouldUseExtraSpace() {
		return false;
	}

}