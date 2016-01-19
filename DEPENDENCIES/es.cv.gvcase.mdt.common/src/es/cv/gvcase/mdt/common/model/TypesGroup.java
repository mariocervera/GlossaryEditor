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

import java.util.Iterator;
import java.util.List;

/**
 * Types Group container with several types described into it
 * 
 * @author <a href="mailto:csanchez@prodevelop.es">Carlos SÃ¡nchez PeriÃ±Ã¡n</a>
 * @NOT-generated
 */
public class TypesGroup {
	/**
	 * The types group that this Domain structure applies to.
	 * 
	 * @NOT-generated
	 */
	public String typesGroupID;

	/**
	 * The types group name.
	 * 
	 * @NOT-generated
	 */
	public String name;

	/**
	 * The defaultSelected boolean.
	 * 
	 * @NOT-generated
	 */
	public Boolean defaultSelected;

	/**
	 * List with children Types elements.
	 * 
	 * @NOT-generated
	 */
	public List<Object> Type;

	/**
	 * List of editor that this TypeGroup applies to.
	 * 
	 * @NOT-generated
	 */
	public List<Object> Editor;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String aux = "" + name + "\n";
		if (Editor != null) {
			Iterator<Object> it = Editor.iterator();
			while (it.hasNext()) {
				aux = aux + "\tEditorID Associated : " + it.next().toString()
						+ "\n";
			}
		}
		if (Type != null) {
			Iterator<Object> it = Type.iterator();
			while (it.hasNext()) {
				aux = aux + "\t" + it.next().toString();
			}
		}
		return aux;
	}

}
