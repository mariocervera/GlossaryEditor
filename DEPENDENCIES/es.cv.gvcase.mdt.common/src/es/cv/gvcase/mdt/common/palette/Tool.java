/*******************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – Initial implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.palette;

import java.util.List;

/**
 * A tool entry for a palette.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public class Tool {
	/**
	 * Tool entry identifier.
	 */
	public String toolID;
	/**
	 * Tool entry container identifier
	 */
	public String parentID;
	/**
	 * Tool entry label for new tools.
	 */
	public String label;
	/**
	 * Tool entry description for new tools.
	 */
	public String description;
	/**
	 * Tool entry icon for new tools.
	 */
	public String icon;
	/**
	 * If true, this tool entry will be removed.
	 */
	public Boolean remove;
	/**
	 * Element type for this tool entry.
	 */
	public List<ElementType> ElementType;
	/**
	 * Runnable class for a custom tool.
	 */
	public List<Runnable> Runnable;

	public Tool() {
	}
}
