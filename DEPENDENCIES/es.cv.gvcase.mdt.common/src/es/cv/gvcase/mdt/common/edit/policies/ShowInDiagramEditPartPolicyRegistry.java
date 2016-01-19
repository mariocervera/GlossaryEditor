/*******************************************************************************
 * Copyright (c) 2009 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – Initial implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.edit.policies;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.notation.Diagram;

import es.cv.gvcase.mdt.common.util.ExtensionPointParser;
import es.cv.gvcase.mdt.common.util.MDTUtil;

/**
 * Registry to store the behavior or policies to apply to edit parts to show qnd
 * filter elements in a {@link Diagram}.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public class ShowInDiagramEditPartPolicyRegistry {

	// // Singleton.

	private static final ShowInDiagramEditPartPolicyRegistry INSTANCE = new ShowInDiagramEditPartPolicyRegistry();

	public static ShowInDiagramEditPartPolicyRegistry getInstance() {
		return INSTANCE;
	}

	private ShowInDiagramEditPartPolicyRegistry() {
	}

	// // Maps of extension point data

	private static final String WhiteListString = "whitelist";
	private static final String BlackListString = "blacklist";

	private static final String ExtensionPointID = "es.cv.gvcase.mdt.common.belongToDiagramPolicy";

	private Map<String, String> mapEditPartClass2Policy = null;

	protected Map<String, String> getMapEditPartClass2Policy() {
		if (mapEditPartClass2Policy == null) {
			mapEditPartClass2Policy = new HashMap<String, String>();
		}
		return mapEditPartClass2Policy;
	}

	// // Extension Point reading

	protected void readExtensionPoint() {
		getMapEditPartClass2Policy().clear();
		ExtensionPointParser parser = new ExtensionPointParser(
				ExtensionPointID, new Class[] { EditPartPolicy.class });
		EditPartPolicy editPartPolicy = null;
		for (Object object : parser.parseExtensionPoint()) {
			if (object instanceof EditPartPolicy) {
				editPartPolicy = (EditPartPolicy) object;
			}
			getMapEditPartClass2Policy().put(editPartPolicy.editPartClass,
					editPartPolicy.policy);
		}
		return;
	}

	/**
	 * Returns the policy value specified in this registry for the given
	 * EditPart.
	 * 
	 * @param editPart
	 * @return
	 */
	public String getEditPartPolicy(EditPart editPart) {
		if (editPart != null) {
			readExtensionPoint();
			Class editPartClass = editPart.getClass();
			for (String classKey : getMapEditPartClass2Policy().keySet()) {
				if (MDTUtil.isOfType(editPartClass, classKey)) {
					return getMapEditPartClass2Policy().get(classKey);
				}
			}
		}
		return "";
	}

	/**
	 * False if the given EditPart is a blacklist.<br>
	 * True if the given EditPart is a whitelist. <br>
	 * True if the given EditPart is not in the registry. <br>
	 * (Whitelist is the default value for the showing policy of an EditPart).
	 * 
	 * @param editPart
	 * @return
	 */
	public boolean isEditPartWhiteListPolicy(EditPart editPart) {
		if (editPart == null) {
			return true;
		}
		readExtensionPoint();
		Class editPartClass = editPart.getClass();
		for (String editPartClassInMap : getMapEditPartClass2Policy().keySet()) {
			if (MDTUtil.isOfType(editPartClass, editPartClassInMap)) {
				return getMapEditPartClass2Policy().get(editPartClassInMap)
						.equals(WhiteListString);
			}
		}
		return true;
	}

}
