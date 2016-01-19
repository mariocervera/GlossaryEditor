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
package es.cv.gvcase.mdt.common.parsers;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gmf.runtime.common.core.command.ICommand;
import org.eclipse.gmf.runtime.common.ui.services.parser.IParser;
import org.eclipse.gmf.runtime.common.ui.services.parser.IParserEditStatus;
import org.eclipse.gmf.runtime.common.ui.services.parser.ParserEditStatus;
import org.eclipse.gmf.runtime.emf.ui.services.parser.ISemanticParser;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;

import es.cv.gvcase.mdt.common.Activator;

/**
 * Wraps an IParser. Inteded to add or alter the PrintingString.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public abstract class SemanticWrapperParser implements ISemanticParser {

	/**
	 * The wrapped parser.
	 */
	IParser realParser = null;

	/**
	 * Constructor with the IParser to be wrapped.
	 * 
	 * @param parser
	 */
	public SemanticWrapperParser(IParser parser) {
		if (parser == null) {
			throw new IllegalArgumentException("parser must not be null");
		}
		realParser = parser;
	}

	/**
	 * Gets the wrapped parser.
	 * 
	 * @return
	 */
	public IParser getRealParser() {
		return realParser;
	}

	/**
	 * Gets the wrapped parser as an ISemanticParser.
	 * 
	 * @return
	 */
	protected ISemanticParser getSemanticParser() {
		return (ISemanticParser) Platform.getAdapterManager().getAdapter(
				getRealParser(), ISemanticParser.class);
	}

	public boolean areSemanticElementsAffected(EObject listener,
			Object notification) {
		if (getSemanticParser() != null) {
			return getSemanticParser().areSemanticElementsAffected(listener,
					notification);
		}
		return false;
	}

	public List getSemanticElementsBeingParsed(EObject element) {
		if (getSemanticParser() != null) {
			List semanticElementsBeingParsed = getSemanticParser()
					.getSemanticElementsBeingParsed(element);
			return semanticElementsBeingParsed != null ? semanticElementsBeingParsed
					: Collections.emptyList();
		}
		return Collections.emptyList();
	}

	public IContentAssistProcessor getCompletionProcessor(IAdaptable element) {
		return getRealParser().getCompletionProcessor(element);
	}

	public String getEditString(IAdaptable element, int flags) {
		return getRealParser().getEditString(element, flags);
	}

	public ICommand getParseCommand(IAdaptable element, String newString,
			int flags) {
		return getRealParser().getParseCommand(element, newString, flags);
	}

	protected String getRealPrintString(IAdaptable element, int flags) {
		return getRealParser().getPrintString(element, flags);
	}

	public abstract String getPrintString(IAdaptable element, int flags);

	public boolean isAffectingEvent(Object event, int flags) {
		if (getSemanticParser() != null) {
			return getSemanticParser().isAffectingEvent(event, flags);
		}
		return false;
	}

	public IParserEditStatus isValidEditString(IAdaptable element,
			String editString) {
		if (getSemanticParser() != null) {
			return getSemanticParser().isValidEditString(element, editString);
		}
		return new ParserEditStatus(Activator.PLUGIN_ID,
				IParserEditStatus.EDITABLE, "Ok");
	}

}
