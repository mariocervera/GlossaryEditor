/*******************************************************************************
 * Copyright (c) 2012 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Marc Gil Sendra (Prodevelop) – Initial implementation.
 ******************************************************************************/
package es.cv.gvcase.ide.navigator.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.common.ui.URIEditorInput;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil.UnresolvedProxyCrossReferencer;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.gmf.runtime.emf.core.resources.GMFResource;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.part.FileEditorInput;

import es.cv.gvcase.emf.common.part.EditingDomainRegistry;
import es.cv.gvcase.emf.common.util.PathsUtil;
import es.cv.gvcase.emf.common.util.ResourceUtil;
import es.cv.gvcase.ide.navigator.Activator;
import es.cv.gvcase.ide.navigator.preferences.PreferenceConstants;
import es.cv.gvcase.mdt.common.util.MDTUtil;

/**
 * This class checks the broken references only in the being opened editor, not
 * in the referenced ones that can point this one.
 * 
 * @author mgil
 */
public class BrokenReferencesChecker {

	private IEditorInput input;

	private boolean hasBrokenReferences = false;
	private boolean continueOpeningEditor = true;
	private boolean openBrokenReferencesEditor = false;

	private File errorLogFile = null;

	public BrokenReferencesChecker(IEditorInput input) {
		this.input = input;
		checkBrokenReferences();
	}

	/**
	 * Manage the Broken References. The returned value indicates if should
	 * proceed with opening the editor (true) or not (false)
	 */
	public void checkBrokenReferences() {
		// Firstly verifies if this dialog is activated by the user by using
		// preferences
		if (!isActivated()) {
			return;
		}

		List<Resource> resources = getResourcesToOpen();
		if (resources.isEmpty()) {
			return;
		}

		Map<EObject, Collection<Setting>> brokenReferences = getBrokenReferences(resources);
		if (!brokenReferences.isEmpty()) {
			hasBrokenReferences = true;
			String title = "Broken References Checker";
			String message = "Some broken references has been found in the model. Would you delete them before proceed?"
					+ "\nA report will be created.";
			boolean result = MessageDialog.openQuestion(Display.getCurrent()
					.getActiveShell(), title, message);
			if (result) {
				// delete broken references
				deleteBrokenReferences(resources, brokenReferences);
			}
		}
	}

	private List<Resource> getResourcesToOpen() {
		List<Resource> checkeableResources = new ArrayList<Resource>();

		// create a new domain or get it from the editor being opened, to
		// allocate all the Resources into a ResourceSet
		TransactionalEditingDomain domain = EditingDomainRegistry.getInstance()
				.get("", input);

		// get the uri of the input
		String uri = null;
		if (input instanceof FileEditorInput) {
			uri = ((FileEditorInput) input).getURI().toString();
		} else if (input instanceof URIEditorInput) {
			uri = ((URIEditorInput) input).getURI().toString();
		}
		if (uri == null) {
			return checkeableResources;
		}

		// load the resource pointed by the uri of the editor
		ResourceUtil.loadResourceFastOptions(URI.createURI(uri), domain
				.getResourceSet());

		// check the resources loaded. If no resources (strange case...) doesn't
		// continue
		List<Resource> resources = domain.getResourceSet().getResources();
		if (resources.isEmpty()) {
			return checkeableResources;
		}

		// get the first resource being opened. If it's a GMF Resource, we'll
		// add this resource and the resource that contains all the elements of
		// the diagram. So we try to get the first element and check if it's a
		// diagram. In that case, we get the element of the diagram and its
		// resource
		Resource resource = resources.get(0);
		checkeableResources.add(resource);

		if (resource instanceof GMFResource) {
			if (resource.getContents().isEmpty()) {
				return checkeableResources;
			}

			EObject first = resource.getContents().get(0);
			if (first instanceof Diagram) {
				Diagram diagram = (Diagram) first;
				if (diagram.getElement() == null
						|| diagram.getElement().eResource() == null) {
					return null;
				}
				resource = diagram.getElement().eResource();
				checkeableResources.add(resource);
			}
		}

		return checkeableResources;
	}

	private Map<EObject, Collection<Setting>> getBrokenReferences(
			List<Resource> resources) {
		Map<EObject, Collection<Setting>> brokenReferences = new HashMap<EObject, Collection<Setting>>();

		List<Resource> toRemove = new ArrayList<Resource>();
		for (Resource resource : resources) {
			Map<EObject, Collection<Setting>> current = UnresolvedProxyCrossReferencer
					.find(resource);
			if (!current.isEmpty()) {
				List<EObject> toRemove2 = new ArrayList<EObject>();
				for (Entry<EObject, Collection<Setting>> entry : current
						.entrySet()) {
					List<Setting> toRemove3 = new ArrayList<Setting>();
					for (Setting setting : entry.getValue()) {
						if (resources.size() > 1
								&& setting.getEObject().eResource() instanceof GMFResource
								&& !setting.getEStructuralFeature().equals(
										EcorePackage.eINSTANCE
												.getEAnnotation_References())) {
							toRemove3.add(setting);
						}
					}
					entry.getValue().removeAll(toRemove3);
					if (entry.getValue().isEmpty()) {
						toRemove2.add(entry.getKey());
					}
				}
				for (EObject eo : toRemove2) {
					current.remove(eo);
				}
				if (!current.isEmpty()) {
					brokenReferences.putAll(current);
				} else {
					toRemove.add(resource);
				}
			} else {
				toRemove.add(resource);
			}
		}

		resources.removeAll(toRemove);

		return brokenReferences;
	}

	private void deleteBrokenReferences(List<Resource> resources,
			Map<EObject, Collection<Setting>> brokenReferences) {
		TransactionalEditingDomain domain = TransactionUtil
				.getEditingDomain(resources.get(0));
		CompoundCommand cc = new CompoundCommand();

		List<BrokenElement> brokenElements = new ArrayList<BrokenElement>();
		for (Entry<EObject, Collection<Setting>> entry : brokenReferences
				.entrySet()) {
			InternalEObject ieo = (InternalEObject) entry.getKey();

			BrokenElement be = new BrokenElement();
			be.brokenElement = ieo;
			be.brokenElementRefs = new ArrayList<BrokenElementRef>();

			for (Setting setting : entry.getValue()) {
				EObject eo = setting.getEObject();
				EStructuralFeature feature = setting.getEStructuralFeature();
				if (resources.size() > 1
						&& eo.eResource() instanceof GMFResource
						&& !feature.equals(EcorePackage.eINSTANCE
								.getEAnnotation_References())) {
					continue;
				}

				BrokenElementRef ber = new BrokenElementRef();
				ber.brokenElementRef = eo;
				ber.feature = feature;

				if (feature.isMany()) {
					cc.append(RemoveCommand.create(domain, eo, feature, ieo));
				} else {
					cc.append(SetCommand.create(domain, eo, feature, null));
				}

				be.brokenElementRefs.add(ber);
			}

			brokenElements.add(be);
		}

		boolean result = doReport(resources, brokenElements);
		if (result && cc.canExecute()) {
			domain.getCommandStack().execute(cc);
			try {
				for (Resource resource : resources) {
					resource.save(null);
				}
			} catch (IOException e) {
			}
		}
	}

	private boolean doReport(List<Resource> resources,
			List<BrokenElement> brokenElements) {
		errorLogFile = getExportingFile(resources.get(0));
		continueOpeningEditor = errorLogFile != null;
		if (continueOpeningEditor) {
			try {
				PrintStream printStream = new PrintStream(errorLogFile);
				exportBrokenReferencesReport(printStream, resources,
						brokenElements);
				try {
					ResourcesPlugin.getWorkspace().getRoot().refreshLocal(
							IResource.DEPTH_INFINITE, null);
				} catch (CoreException ex) {
					return false;
				}
			} catch (FileNotFoundException ex) {
				Activator.getDefault().getLog().log(
						new Status(IStatus.ERROR, Activator.PLUGIN_ID,
								"File not found: " + errorLogFile)); //$NON-NLS-1$
				return false;
			}
		}

		openBrokenReferencesEditor = continueOpeningEditor;
		return continueOpeningEditor;
	}

	private File getExportingFile(Resource resource) {
		File brLogFile = null;
		SaveAsDialog fileSelectDialog = new SaveAsDialog(Display.getDefault()
				.getActiveShell());
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IPath location = Path.fromOSString(PathsUtil
				.fromAbsoluteFileSystemToAbsoluteWorkspace(resource.getURI()
						.toString())
				+ "_brokenReferences.txt");

		IFile ifile = workspace.getRoot().getFile(location);
		fileSelectDialog.setOriginalFile(ifile);
		if (fileSelectDialog.open() == SaveAsDialog.OK) {
			// a file was selected, find its true path
			IPath filePath = fileSelectDialog.getResult();
			filePath = ResourcesPlugin.getWorkspace().getRoot().getLocation()
					.append(filePath);
			brLogFile = filePath.toFile();
		} else {
			brLogFile = null;
		}

		// return the selected export file
		return brLogFile;
	}

	private void exportBrokenReferencesReport(PrintStream printStream,
			List<Resource> resources, List<BrokenElement> brokenElements) {
		printStream.println("Broken References Checker"); //$NON-NLS-1$
		printStream.println("-------------------------"); //$NON-NLS-1$
		printStream
				.println("Here is shown a list of Broken References that the model couldn't resolve. The next information includes:"); //$NON-NLS-1$
		printStream
				.println("\t1. List of Non Resolved elements: type of the element and containing model."); //$NON-NLS-1$
		printStream
				.println("\t2. For each Non Resolved element, a list of elements that uses it: name of this element, path from the root element and affected property."); //$NON-NLS-1$
		printStream.println();

		int br = 0;
		for (Resource resource : resources) {
			printStream.println("Model: " //$NON-NLS-1$
					+ PathsUtil
							.fromAbsoluteFileSystemToAbsoluteWorkspace(resource
									.getURI().toString()));
			printStream.println();

			Collections.sort(brokenElements, new BrokenElementComparator());
			for (BrokenElement be : brokenElements) {
				if (be.brokenElementRefs.get(0).brokenElementRef.eResource()
						.equals(resource)) {
					printStream
							.println("* \"" + be.getType() + "\" not found in model [" + be.getResource() + "]"); //$NON-NLS-1$
					Collections.sort(be.brokenElementRefs,
							new BrokenElementRefComparator());
					for (BrokenElementRef ber : be.brokenElementRefs) {
						printStream
								.println("\t* Referenced by: " + ber.getElementName()); //$NON-NLS-1$
						printStream.println("\t* Located in: " + ber.getPath()); //$NON-NLS-1$
						printStream
								.println("\t* In property: " + ber.getFeatureName()); //$NON-NLS-1$
						printStream.println();
						br++;
					}
				}
			}
			printStream.println();
		}

		printStream.println("Total: " + br + " broken references."); //$NON-NLS-1$
	}

	/**
	 * Verifies if the search for broken references is activated throw
	 * preferences
	 */
	protected boolean isActivated() {
		IPreferenceStore ps = Activator.getDefault().getPreferenceStore();
		if (ps == null) {
			return false;
		} else {
			return ps.getBoolean(PreferenceConstants.P_MODEL_BROKENREFERENCES);
		}
	}

	public File getErrorLogFile() {
		return errorLogFile;
	}

	public boolean hasBrokenReferences() {
		return hasBrokenReferences;
	}

	public boolean continueOpeningEditor() {
		return continueOpeningEditor;
	}

	public boolean shouldOpenBrokenReferencesEditor() {
		return openBrokenReferencesEditor;
	}

	private class BrokenElement {
		public InternalEObject brokenElement;
		public List<BrokenElementRef> brokenElementRefs;

		public String getType() {
			return brokenElement.eClass().getName();
		}

		public String getResource() {
			return brokenElement.eProxyURI().toString();
		}
	}

	private class BrokenElementRef {
		public EObject brokenElementRef;
		public EStructuralFeature feature;

		public String getElementName() {
			return MDTUtil.getLabelProvider().getText(brokenElementRef);
		}

		public String getPath() {
			String path = "";
			EObject eo = brokenElementRef;
			while (eo != null) {
				if (path != "") {
					path = MDTUtil.getLabelProvider().getText(eo) + " :: "
							+ path;
				} else {
					path = MDTUtil.getLabelProvider().getText(eo);
				}
				eo = eo.eContainer();
			}
			return path;
		}

		public String getFeatureName() {
			return feature.getName();
		}
	}

	private class BrokenElementComparator implements Comparator<BrokenElement> {
		public int compare(BrokenElement o1, BrokenElement o2) {
			return o1.getType().compareTo(o2.getType());
		}
	}

	private class BrokenElementRefComparator implements
			Comparator<BrokenElementRef> {
		public int compare(BrokenElementRef o1, BrokenElementRef o2) {
			return (o1.getElementName() + o1.getFeatureName()).compareTo(o2
					.getElementName()
					+ o2.getFeatureName());
		}
	}
}
