/*******************************************************************************
 * Copyright (c) 2008, 2009 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – Initial implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.util;

/**
 * Utility methods for generation templates.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 */
import org.eclipse.m2m.qvt.oml.blackbox.java.Operation;
import org.eclipse.m2m.qvt.oml.blackbox.java.Operation.Kind;

public class GenModelUtils {

	@Operation(contextual = false, kind = Kind.HELPER)
	public static String unQualify(String fqn) {
		// can't implement in qvto because String type lacks split(regex) method
		if (fqn == null || fqn.length() == 0) {
			return "";
		}
		String[] splits = fqn.split(".");
		return splits[splits.length - 1];
	}

	@Operation(contextual = false, kind = Kind.HELPER)
	public static String replaceAll(String str) {
		// can't implement in qvto because replace does not work correctly
		if (str == null || str.length() == 0) {
			return "";
		}
		return str.replaceAll(" ", "_");
	}
	
	@Operation(contextual = false, kind = Kind.HELPER)
	public static String getSimpleClassName(String str) {
		// can't implement in qvto because String type lacks split(regex) method
		if (str == null || str.length() == 0) {
			return "";
		}
		String[] splits = str.split(".");
		return splits[splits.length - 1];
	}
	
	@Operation(contextual = false, kind = Kind.HELPER)
	public static String getPackageName(String str) {
		// can't implement in qvto because String type lacks split(regex) method
		if (str == null || str.length() == 0) {
			return "";
		}
		String[] splits = str.split(".");
		String packageName = "";
		// build a new string without the last segment
		for (int i = 0; i < splits.length - 2; i++) {
			packageName += splits[i];
			if (i < splits.length - 2) {
				packageName += ".";
			}
		}
		return packageName;
	}

	@Operation(contextual = false, kind = Kind.HELPER)
	public static boolean isViewFiltersPage(String id) {
		return "viewfilter".equals(getSimpleClassName(id));
	}
	
}
