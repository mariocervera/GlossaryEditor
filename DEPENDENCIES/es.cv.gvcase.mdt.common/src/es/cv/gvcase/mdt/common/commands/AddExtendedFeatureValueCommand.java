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
 * Adds a value to an extended feature.
 * Value can be a {@link Collection}.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 *
 */
public class AddExtendedFeatureValueCommand extends
		AbstractCommonTransactionalCommmand {

	private EModelElement element = null;

	public EModelElement getElement() {
		return element;
	}

	private String featureID = null;

	public String getFeatureID() {
		return featureID;
	}

	private Object value = null;

	public Object getValue() {
		return value;
	}

	public AddExtendedFeatureValueCommand(TransactionalEditingDomain domain,
			EModelElement element, String featureID, Object value) {
		super(domain, "Add to " + featureID + " a value", null);
		this.element = element;
		this.featureID = featureID;
		this.value = value;
	}

	@Override
	public boolean canExecute() {
		return getElement() != null
				&& getFeatureID() != null
				&& ExtendedFeatureElementFactory.getInstance().isForType(
						getElement(), getFeatureID());
	}

	@Override
	protected CommandResult doExecuteWithResult(IProgressMonitor monitor,
			IAdaptable info) throws ExecutionException {
		try {
			doRedo(monitor, info);
		} catch (ExecutionException ex) {
			return CommandResult.newErrorCommandResult(ex);
		}
		return CommandResult.newOKCommandResult(getElement());
	}

	@Override
	protected IStatus doRedo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		Feature feature = ExtendedFeatureElementFactory.getInstance()
				.getMapFeatureIDToFeature().get(getFeatureID());
		if (feature != null) {
			feature.addValue(getElement(), getValue());
		}
		return new Status(IStatus.OK, Activator.PLUGIN_ID, "Value setted.");
	}

	@Override
	protected IStatus doUndo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		Feature feature = ExtendedFeatureElementFactory.getInstance()
				.getMapFeatureIDToFeature().get(getFeatureID());
		if (feature != null) {
			feature.removeValue(getElement(), getValue());
		}
		return new Status(IStatus.OK, Activator.PLUGIN_ID, "Value setted.");
	}

}
