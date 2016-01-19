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
 * Type class to store a new type with several properties.
 * 
 * @author <a href="mailto:csanchez@prodevelop.es">Carlos SÃ¡nchez PeriÃ±Ã¡n</a>
 * @NOT-generated
 */
public class Type {
	/**
	 * The type identifier.
	 * 
	 * @NOT-generated
	 */
	public String typeID;

	/**
	 * The type name.
	 * 
	 * @NOT-generated
	 */
	public String name;

	/**
	 * List with children property.
	 * 
	 * @NOT-generated
	 */
	public List<Object> Property;	
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		String aux=""+name+"\n";
		if (Property!=null){
			Iterator<Object> it = Property.iterator();
			while(it.hasNext()){
				aux=aux+"\t\t"+it.next().toString();
			}
		}
		return aux;
	}
}
