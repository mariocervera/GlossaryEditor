/*******************************************************************************
 * Copyright (c) 2011 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana. All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *  Miguel Llacer (Prodevelop) [mllacer@prodevelop.es] - initial API implementation
 ******************************************************************************/
package es.cv.gvcase.emf.ui.common.dialogs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.emf.common.ui.dialogs.WorkspaceResourceDialog;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.ui.action.LoadResourceAction.LoadResourceDialog;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;

public class LoadEMFModelDialog extends LoadResourceDialog {

	private String nsURI;
	private IResource resource;

	public LoadEMFModelDialog(Shell parent) {
		this(parent, "", null);
	}

	public LoadEMFModelDialog(Shell parent, String requiredURI) {
		this(parent, requiredURI, null);
	}

	public LoadEMFModelDialog(Shell parent, String requiredURI,
			IResource resource) {
		super(parent);
		this.nsURI = requiredURI;
		this.resource = resource;
	}

	public LoadEMFModelDialog(Shell parent, EditingDomain domain) {
		super(parent, domain);
	}

	@Override
	protected void prepareBrowseWorkspaceButton(Button browseWorkspaceButton) {
		{
			browseWorkspaceButton.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent event) {

					IFile file = null;

					ArrayList<ViewerFilter> filters = new ArrayList<ViewerFilter>();
					filters.add(getEMFResourceViewFilter());

					Object[] initialSelection = null;

					if (resource instanceof IProject) {
						initialSelection = new Object[] { resource };
					} else if (resource instanceof IFile) {
						IContainer container = resource.getParent();
						while (container != null) {
							if (container instanceof IProject) {
								break;
							}
							container = container.getParent();
						}
						if (container instanceof IProject) {
							initialSelection = new Object[] { container };
						}
					}

					IFile[] files = WorkspaceResourceDialog.openFileSelection(
							getShell(), null, null, false, initialSelection,
							filters);
					if (files.length != 0) {
						file = files[0];
					}

					if (file != null) {
						uriField
								.setText(URI.createPlatformResourceURI(
										file.getFullPath().toString(), true)
										.toString());
					}

				}
			});
		}
	}

	private EMFResourceViewFilter viewFilter = null;

	private ViewerFilter getEMFResourceViewFilter() {
		if (viewFilter == null) {
			viewFilter = new EMFResourceViewFilter();
		}
		return viewFilter;
	}

	private class EMFResourceViewFilter extends ViewerFilter {

		@Override
		public boolean select(Viewer viewer, Object parentElement,
				Object element) {
			if (element instanceof IContainer) {

				if (((IContainer) element).getName().charAt(0) == '.') {
					// Do not show hidden folders
					return false;
				}

				return true;
			} else if (element instanceof IFile) {
				IFile file = (IFile) element;

				if (file.getName().charAt(0) == '.') {
					// Do not show hidden files
					return false;
				}

				if (nsURI.equals("")) { // Any model is required
					return true;
				}

				for (String fileExtension : getFileExtensions(nsURI)) {
					if (fileExtension.equals(file.getFileExtension())) {
						return true;
					}
				}

			}
			return false;
		}

	}

	@Override
	protected void prepareBrowseFileSystemButton(Button browseFileSystemButton) {
		browseFileSystemButton.setEnabled(false);
	}

	/**
	 * Gets the file extensions related with the metamodel nsURI.
	 * 
	 * @param nsURI
	 *            the nsURI
	 * 
	 * @return the file extensions
	 */
	protected List<String> getFileExtensions(String nsURI) {
		if (nsURIfileExtensions != null) {
			List<String> list = nsURIfileExtensions.get(nsURI);
			if (list != null) {
				return list;
			}
		}
		return Collections.emptyList();
	}

	static Map<String, List<String>> nsURIfileExtensions = null;

	/**
	 * Registry to validate file extensions by metamodel nsURI.
	 */
	static {
		nsURIfileExtensions = new HashMap<String, List<String>>();
		List<String> list = new ArrayList<String>();

		list.add("uml_diagram");
		list.add("uim_diagram");
		list.add("sqlschema_diagram");
		list.add("wbsdiagram");
		list.add("bpmn_di");
		nsURIfileExtensions.put(
				"http://www.eclipse.org/gmf/runtime/1.0.2/notation", list);

		nsURIfileExtensions.put("http://www.eclipse.org/uml2/3.0.0/UML",
				Collections.singletonList("uml"));

		nsURIfileExtensions.put("http://stp.eclipse.org/bpmn", Collections
				.singletonList("bpmn"));

		nsURIfileExtensions.put(
				"http:///org/eclipse/datatools/modelbase/sql/schema.ecore",
				Collections.singletonList("sqlschema"));

		nsURIfileExtensions.put("http:///es.cv.gvcase.mdt.uim/1.3.0",
				Collections.singletonList("uim"));

		nsURIfileExtensions.put("http:///es.cv.gvcase.mdt.sketcher/0.1.0",
				Collections.singletonList("sketcher_diagram"));
		nsURIfileExtensions.put("http:///es.cv.gvcase.mdt.sketcher/0.3.0",
				Collections.singletonList("sketcher_diagram"));

		nsURIfileExtensions.put("http:///es/cv/gvcase/mdt/wbs.ecore",
				Collections.singletonList("wbs"));

		nsURIfileExtensions.put(
				"http:///es.cv.gvcase.transformationConfiguration.ecore",
				Collections.singletonList("transformationconfiguration"));

		nsURIfileExtensions.put("mw_traceability", Collections
				.singletonList("gvtrace"));

		nsURIfileExtensions.put("http://datadictionary/1.0", Collections
				.singletonList("datadictionary"));

		nsURIfileExtensions.put("http://mdc", Collections.singletonList("mdc"));

		nsURIfileExtensions.put("http://es/cv/gvcase/glossary", Collections
				.singletonList("glossary"));

		nsURIfileExtensions.put("http://es/cv/gvcase/gvm/hr/org", Collections
				.singletonList("hr"));

		nsURIfileExtensions.put("http://prjcat/1.0", Collections
				.singletonList("prjcat"));

		nsURIfileExtensions.put("http://mip1.3", Collections
				.singletonList("mip"));

		nsURIfileExtensions.put("http://role/1.0", Collections
				.singletonList("role"));

		nsURIfileExtensions.put("http://es.cv.gvcase.gvm.common", Collections
				.singletonList("common"));

		nsURIfileExtensions.put("http:///es.cv.gvcase.codgen.gvnix.jpa/1.0",
				Collections.singletonList("jpadefinitions"));
	}

}
