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
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gmf.runtime.common.core.command.ICommand;
import org.eclipse.gmf.runtime.common.core.command.UnexecutableCommand;
import org.eclipse.gmf.runtime.common.ui.services.parser.IParser;
import org.eclipse.gmf.runtime.common.ui.services.parser.IParserEditStatus;
import org.eclipse.gmf.runtime.emf.ui.services.parser.ISemanticParser;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;

/**
 * A wrapper parser that adds a new line with the name of the container of the
 * parsed element if the container {@link View} is not the container element of
 * the model. Must be created over another {@link IParser}. Will forward most
 * operations to that IParser.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 */
public class ContainerNameParserWrapper implements ISemanticParser {

	/** The Constant NoParserString. */
	private static final String NoParserString = "NO_PARSER";

	/** The Constant NoContainerName. */
	private static final String NoContainerName = "NO_CONTAINER";

	/** The real parser. */
	IParser realParser = null;

	/** The view. */
	View view = null;

	/**
	 * The strategy to find the container's name.
	 */
	ContainerNameParserStrategy containerNameStrategy = null;

	/**
	 * Retrieves the strategy to use to find the container's name.
	 * 
	 * @return
	 */
	public ContainerNameParserStrategy getContainerNameStrategy() {
		if (containerNameStrategy == null) {
			// in case a Strategy was not given at creation time, a basic one
			// will be used.
			containerNameStrategy = new BasicContainerNameParserStrategy(view);
		}
		return containerNameStrategy;
	}

	/**
	 * Instantiates a new container name parser wrapper.
	 * 
	 * @param parser
	 *            the parser
	 * @param view
	 *            the view
	 */
	public ContainerNameParserWrapper(IParser parser, View view) {
		realParser = parser;
		this.view = view;
	}

	/**
	 * Instantiates a new container name parser wrapper with a given strategy.
	 */
	public ContainerNameParserWrapper(IParser parser, View view,
			ContainerNameParserStrategy strategy) {
		realParser = parser;
		this.view = view;
		this.containerNameStrategy = strategy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.gmf.runtime.emf.ui.services.parser.ISemanticParser#
	 * areSemanticElementsAffected(org.eclipse.emf.ecore.EObject,
	 * java.lang.Object)
	 */
	public boolean areSemanticElementsAffected(EObject listener,
			Object notification) {
		if (realParser instanceof ISemanticParser) {
			return ((ISemanticParser) realParser).areSemanticElementsAffected(
					listener, notification);
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.gmf.runtime.emf.ui.services.parser.ISemanticParser#
	 * getSemanticElementsBeingParsed(org.eclipse.emf.ecore.EObject)
	 */
	public List getSemanticElementsBeingParsed(EObject element) {
		if (realParser instanceof ISemanticParser) {
			return ((ISemanticParser) realParser)
					.getSemanticElementsBeingParsed(element);
		}
		return Collections.singletonList(element);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.gmf.runtime.common.ui.services.parser.IParser#
	 * getCompletionProcessor(org.eclipse.core.runtime.IAdaptable)
	 */
	public IContentAssistProcessor getCompletionProcessor(IAdaptable element) {
		if (realParser instanceof ISemanticParser) {
			return ((ISemanticParser) realParser)
					.getCompletionProcessor(element);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gmf.runtime.common.ui.services.parser.IParser#getEditString
	 * (org.eclipse.core.runtime.IAdaptable, int)
	 */
	public String getEditString(IAdaptable element, int flags) {
		return realParser != null ? realParser.getEditString(element, flags)
				: NoParserString;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gmf.runtime.common.ui.services.parser.IParser#getParseCommand
	 * (org.eclipse.core.runtime.IAdaptable, java.lang.String, int)
	 */
	public ICommand getParseCommand(IAdaptable element, String newString,
			int flags) {
		return realParser != null ? realParser.getParseCommand(element,
				newString, flags) : UnexecutableCommand.INSTANCE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gmf.runtime.common.ui.services.parser.IParser#getPrintString
	 * (org.eclipse.core.runtime.IAdaptable, int)
	 */
	public String getPrintString(IAdaptable element, int flags) {
		String realString = realParser != null ? realParser.getPrintString(
				element, flags) : NoParserString;
		String containerNameString = getContainerNameStrategy()
				.getContainerName(element);
		return realString
				+ (containerNameString != null ? "\nfrom: <"
						+ containerNameString + ">" : "");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gmf.runtime.common.ui.services.parser.IParser#isAffectingEvent
	 * (java.lang.Object, int)
	 */
	public boolean isAffectingEvent(Object event, int flags) {
		return realParser instanceof IParser ? (realParser).isAffectingEvent(
				event, flags) : true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gmf.runtime.common.ui.services.parser.IParser#isValidEditString
	 * (org.eclipse.core.runtime.IAdaptable, java.lang.String)
	 */
	public IParserEditStatus isValidEditString(IAdaptable element,
			String editString) {
		return realParser instanceof IParser ? (realParser).isValidEditString(
				element, editString) : null;
	}

	/**
	 * Interface to define strategies to get a container's name.
	 * 
	 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
	 *
	 */
	public interface ContainerNameParserStrategy {
		String getContainerName(IAdaptable eObject);
	}

}
