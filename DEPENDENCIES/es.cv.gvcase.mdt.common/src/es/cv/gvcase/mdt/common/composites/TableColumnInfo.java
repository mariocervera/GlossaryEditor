/*******************************************************************************
 * Copyright (c) 2012 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana. All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Marc Gil Sendra (Integranova) - initial API implementation
 ******************************************************************************/
package es.cv.gvcase.mdt.common.composites;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.jface.viewers.CellEditor;

/**
 * A class that represents a Column in a Table.
 * 
 * @author mgil
 */
public class TableColumnInfo {
	public String text;
	public EStructuralFeature feature;
	public int size;
	public CellEditor cellEditor;

	public TableColumnInfo(String text, EStructuralFeature feature, int size,
			CellEditor cellEditor) {
		this.text = text;
		this.feature = feature;
		this.size = size;
		this.cellEditor = cellEditor;
	}
}
