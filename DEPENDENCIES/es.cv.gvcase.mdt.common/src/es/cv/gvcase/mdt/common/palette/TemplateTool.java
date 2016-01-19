/*******************************************************************************
 * Copyright (c) 2010 Conselleria de Infraestructuras y Transporte, Generalitat 
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

import es.cv.gvcase.emf.common.part.IObjectWithContributorId;

/**
 * Stub representation of a TemplateTool element in the TemplateTool extension. <br>
 * Represents a tool in a palette that has information about creating template
 * elements, that is, elements composed of other elements in a structured order.
 * 
 * @author <a href="mailto:fjcano@prodeelop.es">Francisco Javier Cano Muñoz</a>
 * 
 * @see TemplateToolRegistry
 */
public class TemplateTool implements IObjectWithContributorId {

	public String id;

	public String nsURI;

	public String name;

	public String imageLarge;

	public String imageSmall;

	public String description;

	public Object elementCreator;

	public Object elementConfigurator;

	public List<Element> Element;

	public List<EditorGroup> EditorGroup;

	protected String contributorID = null;

	/**
	 * Return the Plugin's ID that made the contribution of this object
	 * 
	 * @return The Plugin's ID String
	 */
	public String getContributorId() {
		return contributorID;
	}

	/**
	 * Set the Plugin's ID that made the contribution of this object
	 */
	public void setContributorId(String contributorId) {
		this.contributorID = contributorId;
	}

}
