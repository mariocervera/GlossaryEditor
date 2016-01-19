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
 * Property class to store a type attribute.
 * 
 * @author <a href="mailto:csanchez@prodevelop.es">Carlos SÃ¡nchez PeriÃ±Ã¡n</a>
 * @NOT-generated
 */
public class Property {
	
	public final static String TYPE_STRING = "String";
	public final static String TYPE_BOOLEAN = "Boolean";
	public final static String TYPE_DOUBLE = "Double";
	public final static String TYPE_INTEGER = "Integer";
	public final static String TYPE_DATE = "Date";
	
	/**
	 * The property structure applies to.
	 * 
	 * @NOT-generated
	 */
	public String propertyID;

	/**
	 * The property name.
	 * 
	 * @NOT-generated
	 */
	public String name;
	
	/**
	 * List with children AvailableValue elements.
	 * 
	 * @NOT-generated
	 */
	public List<Object> AvailableValue;	
	
	/**
	 * Data type.
	 * 
	 * @NOT-generated
	 */
	public String type;
	
	/*
	 * 	(non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		String aux=""+name+"\n";
		if (AvailableValue!=null){
			Iterator<Object> it = AvailableValue.iterator();
			while(it.hasNext()){
				aux=aux+"\t\t\t"+it.next().toString();
			}
		}
		return aux;
	}
	
}
