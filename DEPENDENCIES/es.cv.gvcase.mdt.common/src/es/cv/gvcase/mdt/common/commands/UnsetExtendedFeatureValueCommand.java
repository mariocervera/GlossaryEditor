/*******************************************************************************
 * Copyright (c) 2008, 2009 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – Initial implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.commands;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;

import es.cv.gvcase.mdt.common.Activator;
import es.cv.gvcase.mdt.common.model.ExtendedFeatureElementFactory;
import es.cv.gvcase.mdt.common.model.Feature;

/**
 * Sets an extended {@link Feature} to null.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public class UnsetExtendedFeatureValueCommand extends
		AbstractCommonTransactionalCommmand {

	/**
	 * {@link EModelElement} to edit.
	 */
	private EModelElement element = null;

	public EModelElement getElement() {
		return element;
	}

	/**
	 * {@link Feature} identifier.
	 */
	private String featureID = null;

	public String getFeatureID() {
		return featureID;
	}

	private EAnnotation eAnnotation = null;

	protected EAnnotation getEAnnotation() {
		return eAnnotation;
	}

	/**
	 * Instantiates a new {@link UnsetExtendedFeatureValueCommand}.
	 * 
	 * @param domain
	 *            a {@link TransactionalEditingDomain} for this command to be
	 *            executed in.
	 * @param element
	 *            {@link EModelElement} to be edited.
	 * @param featureID
	 *            {@link Feature} identifier.
	 */
	public UnsetExtendedFeatureValueCommand(TransactionalEditingDomain domain,
			EModelElement element, String featureID) {
		super(domain, "Add to " + featureID + " a value", null);
		this.element = element;
		this.featureID = featureID;
	}

	@Override
	public boolean canExecute() {
		// this command is executable as long as there is an element to edit,
		// the feature to unset is known and the feature is applicable to the
		// given element.
		return getElement() != null
				&& getFeatureID() != null
				&& ExtendedFeatureElementFactory.getInstance().isForType(
						getElement(), getFeatureID());
	}

	@Override
	protected CommandResult doExecuteWithResult(IProgressMonitor monitor,
			IAdaptable info) throws ExecutionException {
		try {
			// delegate to doRedo();
			doRedo(monitor, info);
		} catch (ExecutionException ex) {
			return CommandResult.newErrorCommandResult(ex);
		}
		return CommandResult.newOKCommandResult(getElement());
	}

	@Override
	protected IStatus doRedo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		// get the EAnnotation representing the extended feature.
		eAnnotation = getElement().getEAnnotation(getFeatureID());
		if (eAnnotation != null) {
			// remove it from the element.
			getElement().getEAnnotations().remove(eAnnotation);
		}
		// all went well.
		return new Status(IStatus.OK, Activator.PLUGIN_ID, "Value set.");
	}

	@Override
	protected IStatus doUndo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		// add the previously removed EAnnotation to the element.
		if (getElement().getEAnnotation(getFeatureID()) == null) {
			getElement().getEAnnotations().add(getEAnnotation());
		}
		return new Status(IStatus.OK, Activator.PLUGIN_ID, "Value set.");
	}

}
