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

/**
 * Stub representation of the Element element in the TemplateTool extension. <br>
 * Represents an element with additional information about how it should be
 * created and configured.
 * 
 * @author <a href="mailto:fjcano@prodeelop.es">Francisco Javier Cano Muñoz</a>
 * 
 * @see TemplateTool
 * @see TemplateToolRegistry
 */
public class Element {

	public String name;

	public String packageUri;

	public String createdElementId;

	public String parent;

	public String containment;

	public Boolean addToDiagram;

	public List<Element> Element;

}
