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
package es.cv.gvcase.emf.ui.common.composites;

import java.io.File;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFileState;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.team.core.history.IFileRevision;

public abstract class SelectResourceComposite extends Composite {

	private final static String EMPTY_STRING = "";

	private ModifyListener mListener = null;

	private static ResourceSet resourceSet;

	private boolean showHistory;

	public SelectResourceComposite(Composite parent, int style) {
		this(parent, style, false);
	}

	public SelectResourceComposite(Composite parent, int style,
			boolean showHistory) {
		super(parent, style);

		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.verticalAlignment = SWT.TOP;
		data.grabExcessHorizontalSpace = true;
		this.setLayoutData(data);
		this.showHistory = showHistory;

		createControls(this);
	}

	protected Text createTextField(Composite parent) {
		Text text = new Text(parent, SWT.SINGLE | SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.verticalAlignment = SWT.CENTER;
		data.grabExcessVerticalSpace = false;
		data.widthHint = IDialogConstants.ENTRY_FIELD_WIDTH;
		text.setLayoutData(data);
		return text;
	}

	protected Button createButton(Composite parent) {
		Button button = new Button(parent, SWT.PUSH);
		GridData data = new GridData();
		data.horizontalAlignment = SWT.RIGHT;
		button.setLayoutData(data);

		return button;
	}

	public boolean isResourceSelected() {
		return !getResourceSelected().equals(EMPTY_STRING);
	}

	public abstract void setResourceSelected(String resourcePath);

	public abstract String getResourceSelected();

	public void setInitialResourcePath(String resourcePath) {
		setResourceSelected(resourcePath);
	}

	public void addModifyListener(ModifyListener listener) {
		this.mListener = listener;
	}

	public ModifyListener getModifyListener() {
		return this.mListener;
	}

	protected abstract void createControls(Composite parent);

	public abstract void updateFieldDecorators();

	public boolean isFileValid() {
		String filePath = getResourceSelected();
		if (filePath == null) {
			return false;
		}
		if (filePath.startsWith("platform:/resource")) {
			filePath = filePath.replaceAll("platform:/resource", "");
			IPath path = new Path(filePath);
			IFile file = null;

			try {
				file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
				if (file != null && file.exists()) {
					return true;
				}
			} catch (Exception e) {
				return false;
			}
		} else if (filePath.startsWith("file:")) {
			File file = new File(filePath.replaceAll("file:/", ""));

			if (!Platform.getOS().equals(Platform.OS_WIN32)) {
				file = new File(filePath.replaceAll("file:", ""));
			}
			if (file.exists()) {
				return true;
			}
		} else {
			File file = new File(filePath);
			if (file.exists()) {
				return true;
			}
		}

		return false;
	}

	public Resource getResource(String resourcePath) throws Exception {
		// Trying to load the file as an EMF/XMI file
		ResourceSet rset = getResourceSet();
		return rset.getResource(URI.createURI(resourcePath), true);
	}

	public Resource getResource(IFileRevision revision) throws Exception {
		// Trying to load the file revision as an EMF/XMI file
		IStorage storage = revision.getStorage(null);
		if (!(storage instanceof IFileState)) {
			return null;
		}

		Resource resource = new XMIResourceImpl();
		resource.load(storage.getContents(), null);
		resource.setURI(URI.createURI(getRevisionFilePath(revision)));

		// Save the revision model
		resource.save(Collections.EMPTY_MAP);
		return resource;
	}

	public ResourceSet getResourceSet() {
		if (resourceSet == null) {
			resourceSet = new ResourceSetImpl();
			Resource.Factory resourceFactory = new XMIResourceFactoryImpl();
			resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
					.put("*", resourceFactory);
		}
		return resourceSet;
	}

	/**
	 * Shows composite with file history.
	 * 
	 * @return true, if successful
	 */
	public boolean showFileHistory() {
		return showHistory;
	}

	/**
	 * Gets the revision selected.
	 * 
	 * @return the selected file revision
	 */
	public IFileRevision getRevisionSelected() {
		return null;
	}

	/**
	 * Gets a valid file path for the file revision.
	 * 
	 * @param revision
	 *            the revision
	 * 
	 * @return the file path for the revision
	 * 
	 * @throws CoreException
	 *             the core exception
	 */
	private String getRevisionFilePath(IFileRevision revision)
			throws CoreException {
		IStorage storage = revision.getStorage(null);
		if (!(storage instanceof IFileState)) {
			return null;
		}

		IFileState state = (IFileState) storage;

		String nameWithFileExtension = state.getName();
		String fullPath = state.getFullPath().toString().replace(
				nameWithFileExtension, "");
		String date = getDate(new Date(state.getModificationTime()));
		String name = null, extension = null;

		if (nameWithFileExtension != null
				&& nameWithFileExtension.lastIndexOf(".") != -1) {
			name = nameWithFileExtension.substring(0, nameWithFileExtension
					.lastIndexOf("."));
			extension = nameWithFileExtension.substring(nameWithFileExtension
					.lastIndexOf(".") + 1, nameWithFileExtension.length());
		}

		if (name != null && extension != null) {
			fullPath = fullPath + name + date + "." + extension;
		} else {
			fullPath = fullPath + nameWithFileExtension + date;
		}

		return "platform:/resource" + fullPath;
	}

	/**
	 * Gets a valid string for the date.
	 * 
	 * @param date
	 *            the date of the revision
	 * 
	 * @return the string date
	 */
	private String getDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		String year = Integer.toString(calendar.get(Calendar.YEAR));
		String month = Integer.toString(calendar.get(Calendar.MONTH) + 1);
		if (month.length() < 2) {
			month = "0" + month;
		}
		String day = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
		if (day.length() < 2) {
			day = "0" + day;
		}
		String hour = Integer.toString(calendar.get(Calendar.HOUR));
		if (hour.length() < 2) {
			hour = "0" + hour;
		}
		String minute = Integer.toString(calendar.get(Calendar.MINUTE));
		if (minute.length() < 2) {
			minute = "0" + minute;
		}

		return year + month + day + hour + minute;
	}

}
