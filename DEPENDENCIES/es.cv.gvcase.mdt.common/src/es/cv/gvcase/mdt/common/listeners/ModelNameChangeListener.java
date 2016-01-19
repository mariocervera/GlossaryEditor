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
package es.cv.gvcase.mdt.common.listeners;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.gmf.runtime.common.ui.util.FileUtil;
import org.eclipse.gmf.runtime.emf.core.resources.GMFResource;
import org.eclipse.gmf.runtime.emf.core.resources.GMFResourceFactory;
import org.eclipse.gmf.runtime.notation.Diagram;

import es.cv.gvcase.mdt.common.Activator;
import es.cv.gvcase.mdt.common.part.MOSKittDiagramsFileExtensionRegistry;

// TODO: Auto-generated Javadoc
/**
 * A resource change listener that listens to model file that are moved. Updates
 * references in diagram files so that they reference the new file.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 */
public class ModelNameChangeListener implements IResourceChangeListener {

	/** The active workspace. */
	private IWorkspace workspace = null;

	/**
	 * Constructor that sets the active workspace.
	 */
	public ModelNameChangeListener() {
		workspace = ResourcesPlugin.getWorkspace();
	}

	/**
	 * Returns the active workspace.
	 * 
	 * @return the workspace
	 */
	protected IWorkspace getWorkspace() {
		return workspace;
	}

	/**
	 * Changes references in diagram files to the new model file.
	 * 
	 * @param event
	 *            the event
	 */
	public void resourceChanged(IResourceChangeEvent event) {
		// check that the event is a move event.
		// movedFrom: new resource that was moved from another
		for (IResourceDelta movedFrom : getDeltaByFlag(event.getDelta(),
				IResourceDelta.MOVED_FROM)) {

			// check that the moved file is a model file
			if (isModelFile(movedFrom) == false) {
				return;
			}

			// important paths
			// new path
			String newPath = movedFrom.getFullPath().toString();
			// original path
			String originalPath = movedFrom.getMovedFromPath().toString();

			// find all diagrams in the workspace
			List<IPath> paths = Collections.EMPTY_LIST;
			if (event.getSource() instanceof IWorkspace) {
				IWorkspace workspace = (IWorkspace) event.getSource();
				if (workspace != getWorkspace()) {
					return;
				}
				paths = visitWorkspace(getWorkspace(), newPath);
			}

			// check if there are any references in the diagram to the moved
			// model
			// and update those references to the new model file.
			replaceModelReferences(originalPath, newPath, paths);

			// refresh workspace to show changes
			refreshWorkspace();
		}
	}

	/**
	 * Refresh workspace.
	 */
	protected void refreshWorkspace() {
		// refresh workspace
		try {
			getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE,
					new NullProgressMonitor());
		} catch (CoreException ex) {
			IStatus status = new Status(IStatus.WARNING, Activator.PLUGIN_ID,
					"Workspace couldn't be refreshed.");
			Activator.getDefault().getLog().log(status);
		}
	}

	/**
	 * Finds the delta that matches the given flag.
	 * 
	 * @param delta
	 *            the delta
	 * @param flag
	 *            the flag
	 * 
	 * @return the delta by flag
	 */
	protected List<IResourceDelta> getDeltaByFlag(IResourceDelta delta, int flag) {
		if (delta == null) {
			return null;
		}

		if ((delta.getFlags() & flag) == flag) {
			return Collections.singletonList(delta);
		}

		List<IResourceDelta> res = new ArrayList<IResourceDelta>();
		for (IResourceDelta child : delta.getAffectedChildren()) {
			res.addAll(getDeltaByFlag(child, flag));
		}

		return res;
	}

	/**
	 * Check if the file is a model file.
	 * 
	 * @param delta
	 *            the delta
	 * 
	 * @return true, if checks if is model file
	 */
	protected boolean isModelFile(IResourceDelta delta) {
		URI uri = URI.createURI(delta.getFullPath().toString());
		ResourceSet resourceSet = getEMFResourceSet();
		Resource resource = null;
		try {
			resource = resourceSet.getResource(uri, true);
		} catch (Exception ex) {
			return false;
		}
		return resource instanceof Resource;
	}

	/** The EMFResourceSet;. */
	ResourceSet EMFResourceSet = null;

	/**
	 * Initializes the EMFResourceSet.
	 * 
	 * @return the EMF resource set
	 */
	private ResourceSet getEMFResourceSet() {
		if (EMFResourceSet == null) {
			EMFResourceSet = new ResourceSetImpl();
			XMIResourceFactoryImpl resourceFactory = new XMIResourceFactoryImpl();
			EMFResourceSet.getResourceFactoryRegistry()
					.getExtensionToFactoryMap().put(
							Resource.Factory.Registry.DEFAULT_EXTENSION,
							resourceFactory);
		}
		return EMFResourceSet;
	}

	/** The GMFResourceSet;. */
	ResourceSet GMFResourceSet = null;

	/**
	 * Initializes the GMFResourceSet.
	 * 
	 * @return the GMF resource set
	 */
	private ResourceSet getGMFResourceSet() {
		if (GMFResourceSet == null) {
			GMFResourceSet = new ResourceSetImpl();
			GMFResourceFactory resourceFactory = new GMFResourceFactory();
			GMFResourceSet.getResourceFactoryRegistry()
					.getExtensionToFactoryMap().put(
							Resource.Factory.Registry.DEFAULT_EXTENSION,
							resourceFactory);
		}
		return GMFResourceSet;
	}

	/**
	 * Checks whether the given path is a GMF resource.
	 * 
	 * @param path
	 *            the path
	 * 
	 * @return true, if checks if is diagram file
	 */
	protected boolean isDiagramFile(IPath path) {
		URI uri = URI.createURI(path.toString());
		ResourceSet resourceSet = getGMFResourceSet();
		Resource resource = null;
		if (MOSKittDiagramsFileExtensionRegistry.getInstance()
				.isMOSKittDiagramFileExtension(path.getFileExtension())) {
			try {
				resource = resourceSet.getResource(uri, true);
			} catch (Exception ex) {
				return false;
			}
			if (resource instanceof GMFResource) {
				if (resource.getContents().size() > 0) {
					for (EObject content : resource.getContents()) {
						if (content instanceof Diagram) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * Stores the <IPath>s to be checked for model references.
	 * 
	 * @param workspace
	 *            the workspace
	 * @param path
	 *            the path
	 * 
	 * @return the list< i path>
	 */
	protected List<IPath> visitWorkspace(IWorkspace workspace, String path) {
		final List<IPath> paths = new ArrayList<IPath>();
		IResourceVisitor visitor = new IResourceVisitor() {
			public boolean visit(IResource resource) throws CoreException {
				if (resource instanceof IFile) {
					if (isDiagramFile(((IFile) resource).getFullPath())) {
						paths.add(((IFile) resource).getFullPath());
					}
					return false;
				}
				return true;
			}
		};
		try {
			workspace.getRoot().accept(visitor);
		} catch (CoreException ex) {
			IStatus status = new Status(IStatus.WARNING, Activator.PLUGIN_ID,
					"Error visiting workspace.");
			Activator.getDefault().getLog().log(status);
			return Collections.EMPTY_LIST;
		}
		return paths;
	}

	/**
	 * For each resource in paths, references to originalPath are replaced by
	 * references to newPath.
	 * 
	 * @param originalPath
	 *            the original path
	 * @param newPath
	 *            the new path
	 * @param paths
	 *            the paths
	 */
	protected void replaceModelReferences(String originalPath, String newPath,
			List<IPath> paths) {
		for (IPath path : paths) {
			// open diagram file to be read String by String
			BufferedReader reader = getReaderFor(path);
			// open temporal writing file
			IPath temporalPath = getTemporalPath(path);
			BufferedWriter writer = getWriterFor(temporalPath);
			// relative path to original
			String relativeToOriginal = getRelative(originalPath, path
					.toString());
			// relative path to new
			String newToOriginal = getRelative(newPath, path.toString());
			// replace (read, replace, save)
			replaceReferences(reader, writer, relativeToOriginal, newToOriginal);
			boolean toMove = true;
			try {
				// close reader
				reader.close();
			} catch (IOException ex) {
				IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
						"Error closing " + path.toString() + " reader");
				Activator.getDefault().getLog().log(status);
			}
			try {
				// close writer.
				writer.close();
			} catch (IOException ex) {
				IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
						"Error closing " + temporalPath.toString() + " writer");
				Activator.getDefault().getLog().log(status);
				toMove = false;
			}
			// move temporal file to real file
			if (toMove) {
				IFile real = getWorkspace().getRoot().getFile(path);
				// refresh workspace to "see" the temporal file
				refreshWorkspace();
				IFile temporal = getWorkspace().getRoot().getFile(temporalPath);
				if (temporal.exists() && real.exists()) {
					try {
						// delete real file, otherwise temporal file cannot be
						// moved
						real.delete(true, true, new NullProgressMonitor());
						refreshWorkspace();
						// move temporal file to real file
						temporal.move(path, true, new NullProgressMonitor());
					} catch (CoreException ex) {
						IStatus status = new Status(IStatus.ERROR,
								Activator.PLUGIN_ID,
								"Error updating model references: " + ex);
						Activator.getDefault().getLog().log(status);
					}
				}
			}
		}
	}

	/**
	 * Gets a <BufferedReader> for an <IPath>.
	 * 
	 * @param path
	 *            the path
	 * 
	 * @return the reader for
	 */
	protected BufferedReader getReaderFor(IPath path) {
		if (path == null) {
			return null;
		}
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(getAbsoluteFullPath(path).toFile())));
			return reader;
		} catch (FileNotFoundException ex) {
			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					"Error opening " + path.toString() + " for reading");
			Activator.getDefault().getLog().log(status);
			return null;
		}
	}

	/**
	 * Gets a <BufferedWritter> for an <IPath>.
	 * 
	 * @param path
	 *            the path
	 * 
	 * @return the writer for
	 */
	protected BufferedWriter getWriterFor(IPath path) {
		if (path == null) {
			return null;
		}
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(getAbsoluteFullPath(path).toFile())));
			return writer;
		} catch (FileNotFoundException ex) {
			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					"Error opening " + path.toString() + "for writing");
			Activator.getDefault().getLog().log(status);
			return null;
		}
	}

	/**
	 * Finds a temporary <IPath> that doesn't overwrite any existing file.
	 * 
	 * @param path
	 *            the path
	 * 
	 * @return the temporal path
	 */
	protected IPath getTemporalPath(IPath path) {
		String temporalPathString = null;
		int n = 1;
		boolean exists = false;
		do {
			temporalPathString = path.toString() + n;
			exists = checkPathExists(temporalPathString);
			n++;
		} while (exists);
		return new Path(temporalPathString);
	}

	/**
	 * Checks if a given <IPath> exists in the workspace.
	 * 
	 * @param pathString
	 *            the path string
	 * 
	 * @return true, if check path exists
	 */
	protected boolean checkPathExists(String pathString) {
		IPath path = new Path(pathString);
		IFile file = getWorkspace().getRoot().getFile(path);
		return file != null ? file.exists() : false;
	}

	/**
	 * Returns the relative String path path1 to path2.
	 * 
	 * @param path1
	 *            the path1
	 * @param path2
	 *            the path2
	 * 
	 * @return the relative
	 */
	protected String getRelative(String path1, String path2) {
		String relative = FileUtil.getRelativePath(path1, path2);
		relative = relative.replaceAll("\\\\", "/");
		return relative;
	}

	/**
	 * Replaces all instanceos of originalReference with newReference.
	 * 
	 * @param reader
	 *            the reader
	 * @param originalReference
	 *            the original reference
	 * @param newReference
	 *            the new reference
	 * @param writer
	 *            the writer
	 */
	protected void replaceReferences(BufferedReader reader,
			BufferedWriter writer, String originalReference, String newReference) {
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				line = line.replaceAll(originalReference, newReference);
				writer.append(line);
				writer.newLine();
			}
		} catch (IOException ex) {
			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					"Error reading file");
			Activator.getDefault().getLog().log(status);
			return;
		}
	}

	/**
	 * Adds the <IWorkspace> location to the given <IPath>.
	 * 
	 * @param path
	 *            the path
	 * 
	 * @return the absolute full path
	 */
	protected IPath getAbsoluteFullPath(IPath path) {
		return getWorkspace() == null ? null : new Path(getWorkspace()
				.getRoot().getLocation().toString()
				+ path.toString());
	}
}
