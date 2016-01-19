/*******************************************************************************
 * Copyright (c) 2010 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Marc Gil Sendra (Prodevelop) – Initial implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.migrations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import es.cv.gvcase.mdt.common.commands.AbstractCommonTransactionalCommmand;
import es.cv.gvcase.mdt.common.util.MDTUtil;

public abstract class MigratorService {

	protected Diagram diagram = null;
	protected TransactionalEditingDomain editingDomain = null;
	protected String label = "";
	protected List<String> MODEL_IDS = new ArrayList<String>();
	protected String sourceVersion = "";
	protected String targetVersion = "";

	public void setLabel(String label) {
		this.label = label;
	}

	public void addModelId(String modelID) {
		MODEL_IDS.add(modelID);
	}

	public void setSourceVersion(String srcVersion) {
		sourceVersion = srcVersion;
	}

	public void setTargetVersion(String tgtVersion) {
		targetVersion = tgtVersion;
	}

	public String getLabel() {
		return label;
	}

	public String getSourceVersion() {
		return sourceVersion;
	}

	public String getTargetVersion() {
		return targetVersion;
	}

	protected TransactionalEditingDomain getEditingDomain() {
		return editingDomain;
	}

	protected Diagram getDiagram() {
		return diagram;
	}

	/**
	 * Checks if this migrator could be executed for the given diagram. By
	 * default, checks if the diagram type is equals than the specified in the
	 * extension point, and if the diagram version is lower than the target
	 * version of the migrator (if not version is specified -old moskitt
	 * diagrams-, returns true too by default)
	 * 
	 * @param the
	 *            editor
	 * @return
	 */
	public boolean provides(Diagram diagram) {
		if (diagram == null) {
			return false;
		}

		if (!MODEL_IDS.contains(diagram.getType())) {
			return false;
		}

		String diagramVersion = MDTUtil.getDiagramVersion(diagram);
		if (diagramVersion == null) {
			this.diagram = diagram;
			return true;
		}

		if (MDTUtil.compareDiagramVersions(targetVersion, diagramVersion) < 0) {
			this.diagram = diagram;
			return true;
		}

		return false;
	}

	/**
	 * Execute the migration in a read/write transaction. The changes in the
	 * model could be done directly without commands, due to we're using a
	 * global AbstractCommonTransactionalCommand to execute the changes into
	 * this command
	 */
	public boolean executeMigrationWithTransaction(
			TransactionalEditingDomain editingDomain) {
		this.editingDomain = editingDomain;

		AbstractCommonTransactionalCommmand command = new AbstractCommonTransactionalCommmand(
				editingDomain, "migration", null) {
			@Override
			protected CommandResult doExecuteWithResult(
					IProgressMonitor monitor, IAdaptable info)
					throws ExecutionException {
				if (!executeMigrationWithoutTransaction()) {
					return CommandResult.newCancelledCommandResult();
				} else {
					return CommandResult.newOKCommandResult();
				}
			}
		};

		if (command.canExecute()) {
			try {
				IStatus status = command.execute(new NullProgressMonitor(),
						null);
				return status.isOK();
			} catch (ExecutionException e) {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * Execute the migration without a read/write transaction. Model changes
	 * could be done without read/write transactions.
	 */
	public boolean executeMigrationWithoutTransaction() {
		try {
			// execute the specified actions to migrate the diagramF
			specificActionsToExecute();
			// modify diagram version
			if (shouldModifyDiagramVersion()) {
				modifyDiagramVersion();
			}
			// save the diagram
			diagram.eResource().save(null);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * Specify if the diagram version should be updated. True by default.
	 * 
	 * @return
	 */
	protected boolean shouldModifyDiagramVersion() {
		return true;
	}

	/**
	 * Modify the diagram version. Needs an editing domain to execute
	 */
	private void modifyDiagramVersion() {
		MDTUtil.addDiagramVersion(getDiagram(), getTargetVersion());
	}

	/**
	 * Try to resolve the eObject proxy into the given resource
	 * 
	 * @param resource
	 * @param eObj
	 * @return
	 */
	protected EObject resolve(Resource resource, EObject eObj) {
		if (eObj.eIsProxy()) {
			URI proxyURI = ((InternalEObject) eObj).eProxyURI();
			return resource.getEObject(proxyURI.fragment());
		}
		return eObj;
	}

	/**
	 * Try to resolve the eObject proxy into all the resources loaded in the
	 * resourceSet where the diagram is contained. Try to execute before
	 * loadModelResource() method to load the model resource
	 * 
	 * @param eObj
	 * @return
	 */
	protected EObject resolve(EObject eObj) {
		if (diagram == null) {
			return null;
		}

		if (eObj == null) {
			return null;
		}

		if (!eObj.eIsProxy()) {
			return eObj;
		}

		// search in the resources placed in the resourceSet where the diagram
		// is contained
		URI proxyURI = ((InternalEObject) eObj).eProxyURI();
		for (Resource resource : diagram.eResource().getResourceSet()
				.getResources()) {
			try {
				EObject eObject = resource.getEObject(proxyURI.fragment());
				if (eObject != null) {
					return eObject;
				}
			} catch (Exception e) {
				continue;
			}
		}

		return null;
	}

	/**
	 * Tries to load the model resource where the diagram elements are
	 * contained.
	 */
	protected void loadModelResource() {
		if (diagram == null || diagram.getElement() == null
				|| !diagram.getElement().eIsProxy()) {
			return;
		}

		URI fullDiagramURI = diagram.eResource().getURI().trimFragment();
		URI pathURI = fullDiagramURI
				.trimSegments(fullDiagramURI.segmentCount() - 1);
		URI elementURI = ((InternalEObject) diagram.getElement()).eProxyURI();
		URI fullPathURI = pathURI.appendSegments(elementURI.segments());
		ResourceSet rs = diagram.eResource().getResourceSet();
		Resource r = rs.getResource(fullPathURI, true);
		try {
			r.load(null);
		} catch (IOException e) {
			MessageDialog.openError(new Shell(), "Resource error",
					"Could not find the model. Finding it into "
							+ fullPathURI.toString());
		}

		// // removes the "fake" resource
		// InternalEObject intEObj = ((InternalEObject) diagram.getElement());
		// URI uri = intEObj.eProxyURI().trimFragment();
		// rs.getResources().remove(
		// diagram.eResource().getResourceSet().getResource(uri, false));
	}

	/**
	 * Specify custom actions in the migration. Diagram will never be null,
	 * because provides method check it and always will be execute after this
	 * method
	 */
	protected abstract void specificActionsToExecute();
}
