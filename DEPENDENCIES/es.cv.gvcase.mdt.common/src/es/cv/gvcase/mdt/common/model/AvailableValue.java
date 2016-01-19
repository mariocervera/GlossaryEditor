/*******************************************************************************
 * Copyright (c) 2009 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Carlos SÃ¡nchez PeriÃ±Ã¡n (Prodevelop)
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.model;

/**
 * Available value stores one enumeration value.
 * 
 * @author <a href="mailto:csanchez@prodevelop.es">Carlos SÃ¡nchez PeriÃ±Ã¡n</a>
 * @NOT-generated
 */
public class AvailableValue {

	public String value;

	public boolean isValueForType(String type) {
		if (Feature.BooleanType.equals(type)) {
			return isBoolean();
		} else if (Feature.DoubleType.equals(type)) {
			return isDouble();
		} else if (Feature.IntegerType.equals(type)) {
			return isInteger();
		} else if (Feature.StringType.equals(type)) {
			return isString();
		} else {
			return false;
		}
	}

	protected boolean isBoolean() {
		return Boolean.valueOf(value) != null;
	}

	protected boolean isInteger() {
		try {
			return Integer.valueOf(value) != null;
		} catch (NumberFormatException ex) {
			return false;
		}
	}

	protected boolean isDouble() {
		try {
			return Double.valueOf(value) != null;
		} catch (NumberFormatException ex) {
			return false;
		}
	}

	protected boolean isString() {
		return value != null;
	}

	public Object getValue(String type) {
		if (Feature.BooleanType.equals(type)) {
			return getBoolean(value);
		} else if (Feature.DoubleType.equals(type)) {
			return getDouble(value);
		} else if (Feature.IntegerType.equals(type)) {
			return getInteger(value);
		} else if (Feature.StringType.equals(type)) {
			return getString(value);
		} else {
			return null;
		}
	}

	protected Boolean getBoolean(String s) {
		return Boolean.valueOf(s);
	}

	protected Double getDouble(String s) {
		try {
			return Double.valueOf(s);
		} catch (NumberFormatException ex) {
			return null;
		}
	}

	protected Integer getInteger(String s) {
		try {
			return Integer.valueOf(s);
		} catch (NumberFormatException ex) {
			return null;
		}
	}

	protected String getString(String s) {
		return s;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		String aux=""+value+"\n";
		return aux;
	}
}
