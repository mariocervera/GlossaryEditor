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

import java.util.Collection;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;

import es.cv.gvcase.mdt.common.Activator;
import es.cv.gvcase.mdt.common.model.ExtendedFeatureElementFactory;
import es.cv.gvcase.mdt.common.model.Feature;

/**
 * Removes a value from an extended {@link Feature}. Value can be a {@link Collection}.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public class RemoveExtendedFeatureValueCommand extends
		AbstractCommonTransactionalCommmand {

	/**
	 * The {@link EModelElement} that has the extended feature to be edited.
	 */
	private EModelElement element = null;

	public EModelElement getElement() {
		return element;
	}

	/**
	 * The iddentifier of the extended feature to be edited.
	 */
	private String featureID = null;

	public String getFeatureID() {
		return featureID;
	}

	/**
	 * The value to remove from the extended feature.
	 */
	private Object value = null;

	public Object getValue() {
		return value;
	}

	/**
	 * Instantiates a new {@link RemoveExtendedFeatureValueCommand}.
	 * 
	 * @param domain
	 *            a {@link TransactionalEditingDomain} for this command.
	 * @param element
	 *            an {@link EModelElement} that has the extended feature.
	 * @param featureID
	 *            the extended {@link Feature} identifier.
	 * @param value
	 *            the value to remove from the extended {@link Feature}.
	 */
	public RemoveExtendedFeatureValueCommand(TransactionalEditingDomain domain,
			EModelElement element, String featureID, Object value) {
		super(domain, "Add to " + featureID + " a value", null);
		this.element = element;
		this.featureID = featureID;
		this.value = value;
	}

	@Override
	public boolean canExecute() {
		// we can execute as long as we have an element to edit, we know which
		// feature to edit and the value to be removed is defined. Of course,
		// the extended feature must be applicable to the given element.
		return getElement() != null
				&& getFeatureID() != null
				&& ExtendedFeatureElementFactory.getInstance().isForType(
						getElement(), getFeatureID());
	}

	@Override
	protected CommandResult doExecuteWithResult(IProgressMonitor monitor,
			IAdaptable info) throws ExecutionException {
		try {
			// delegate to doRedo()
			doRedo(monitor, info);
		} catch (ExecutionException ex) {
			return CommandResult.newErrorCommandResult(ex);
		}
		return CommandResult.newOKCommandResult(getElement());
	}

	@Override
	protected IStatus doRedo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		// get the feature
		Feature feature = ExtendedFeatureElementFactory.getInstance()
				.getMapFeatureIDToFeature().get(getFeatureID());
		if (feature != null) {
			// remove the value from the feature in the element
			feature.removeValue(getElement(), getValue());
		}
		// all went well
		return new Status(IStatus.OK, Activator.PLUGIN_ID, "Value set.");
	}

	@Override
	protected IStatus doUndo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		// get the feature
		Feature feature = ExtendedFeatureElementFactory.getInstance()
				.getMapFeatureIDToFeature().get(getFeatureID());
		if (feature != null) {
			// restore the value the feature had before.
			feature.addValue(getElement(), getValue());
		}
		return new Status(IStatus.OK, Activator.PLUGIN_ID, "Value set.");
	}

}
