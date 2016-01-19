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
package es.cv.gvcase.emf.common.util;

import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.ui.URIEditorInput;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.part.FileEditorInput;

import es.cv.gvcase.emf.common.Activator;

/**
 * Several general utility methods to work with {@link URI}s, {@link IPath}s and
 * {@link IFile}s.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public class PathsUtil {

	/**
	 * Tries to convert the given path to a path in the workspace. If the
	 * conversion is not possible, a null String is returned.
	 * 
	 * @param uriPath
	 * @return
	 */
	public static String toWorkspaceURI(String uriPath) {
		URI uri = toWorkspaceURI(URI.createURI(uriPath));
		if (uri != null) {
			return uri.toString();
		}
		return null;
	}

	/**
	 * Tries to convert the given URI to an URI in the workspace. It the
	 * conversion is not possible, a null URI is returned.
	 * 
	 * @param uri
	 * @return
	 */
	public static URI toWorkspaceURI(URI uri) {
		if (uri == null) {
			return null;
		}
		if (uri.isPlatformPlugin()) {
			return uri;
		}
		if (uri.isPlatformResource()) {
			return uri;
		}
		if (uri.isFile()) {
			String path = "";
			for (String segment : uri.segments()) {
				path += "/" + segment;
			}
			if (!uri.hasAbsolutePath()) {
				path = path.substring(1);
			}
			String pathToWorkspace = fromAbsoluteFileSystemToAbsoluteWorkspace(
					path, false);
			uri = URI.createPlatformResourceURI(pathToWorkspace, true);
			return uri;
		}
		return null;
	}
	
	/**
	 * Gets the workspace location.
	 * 
	 * @return the workspace location
	 */
	public static IPath getWorkspaceLocation() {
		try {
			return ResourcesPlugin.getWorkspace().getRoot().getLocation();
		} catch (NullPointerException ex) {
			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					"Error getting workspace", ex);
			Activator.getDefault().getLog().log(status);
			return null;
		}
	}

	/**
	 * Full file {@link Path} to {@link Resource}.
	 * 
	 * @param filePath
	 *            the file path
	 * 
	 * @return the string
	 */
	public static String fullFilePathToResource(String filePath) {
		String workspaceLocation = getWorkspaceLocation().toString();
		return filePath.replaceFirst(workspaceLocation, "");
	}

	/**
	 * Full file {@link Path} to {@link Resource} {@link URI}.
	 * 
	 * @param filePath
	 *            the file path
	 * 
	 * @return the uRI
	 */
	public static URI fullFilePathToResourceURI(String filePath) {
		String uri = fullFilePathToResource(filePath);
		return URI.createPlatformResourceURI(uri, true);
	}

	/**
	 * From absolute file system to absolute workspace.
	 * 
	 * @param filesystemPath
	 *            the filesystem path
	 * 
	 * @return the string
	 */
	public static String fromAbsoluteFileSystemToAbsoluteWorkspace(
			String filesystemPath) {
		return fromAbsoluteFileSystemToAbsoluteWorkspace(filesystemPath, true);
	}

	/**
	 * From absolute file system to absolute workspace.
	 * 
	 * @param filesystemPath
	 *            the filesystem path
	 * 
	 * @return the string
	 */
	public static String fromAbsoluteFileSystemToAbsoluteWorkspace(
			String filesystemPath, boolean keepSchema) {
		String schemaUsed = getSchemeUsed(filesystemPath);
		String workspacePath = removeSchemas(filesystemPath);
		boolean isInWorkspace = isInWorkspace(workspacePath);
		if (isInWorkspace) {
			workspacePath = removeWorkspace(workspacePath);
		}
		workspacePath = removeProtocols(workspacePath);
		workspacePath = addRoot(workspacePath);
		if (keepSchema) {
			if (!isInWorkspace) {
				workspacePath = appendSchemeUsed(workspacePath, schemaUsed);
			}
		}
		return URI.decode(workspacePath);
	}

	protected static boolean isInWorkspace(String path) {
		if (path == null) {
			return false;
		} else if (path.startsWith(getWorkspaceLocation().toString())
				|| path.startsWith("/" + getWorkspaceLocation().toString())) {
			return true;
		}
		return false;
	}

	protected static String getSchemeUsed(String path) {
		if (path == null) {
			return null;
		}
		for (String scheme : archiveSchemes) {
			if (path.startsWith(scheme)) {
				return scheme;
			}
		}
		return null;
	}

	protected static String appendSchemeUsed(String path, String scheme) {
		if (path == null) {
			return null;
		}
		if (scheme == null) {
			return path;
		}
		return scheme + SCHEME_SEPARATOR + path;
	}

	public static String fromEditorInputToURIString(IEditorInput editorInput) {
		String uri = null;
		if (editorInput instanceof FileEditorInput) {
			uri = ((FileEditorInput) editorInput).getPath().toString();
			uri = fullFilePathToResourceURI(uri).toString();
		} else if (editorInput instanceof IURIEditorInput) {
			uri = ((IURIEditorInput) editorInput).getURI().toString();
		} else if (editorInput instanceof URIEditorInput) {
			uri = ((URIEditorInput) editorInput).getURI().trimFragment()
					.toString();
		}
		if (uri != null) {
			uri = fromAbsoluteFileSystemToAbsoluteWorkspace(uri);
			// uri = checkInWorkspaceOrFilesystem(uri);
		}
		return uri;
	}

	// The lower-cased schemes that will be used to identify archive URIs.
	/** The Constant archiveSchemes. */
	private static final Set<String> archiveSchemes;
	// Identifies a file-type absolute URI.
	/** The Constant SCHEME_FILE. */
	private static final String SCHEME_FILE = "file";

	/** The Constant SCHEME_JAR. */
	private static final String SCHEME_JAR = "jar";

	/** The Constant SCHEME_ZIP. */
	private static final String SCHEME_ZIP = "zip";

	/** The Constant SCHEME_ARCHIVE. */
	private static final String SCHEME_ARCHIVE = "archive";

	/** The Constant SCHEME_PLATFORM. */
	private static final String SCHEME_PLATFORM = "platform";
	// Static initializer for archiveSchemes.
	static {
		Set<String> set = new HashSet<String>();
		String propertyValue = System
				.getProperty("org.eclipse.emf.common.util.URI.archiveSchemes");

		if (propertyValue == null) {
			set.add(SCHEME_JAR);
			set.add(SCHEME_ZIP);
			set.add(SCHEME_ARCHIVE);
			set.add(SCHEME_FILE);
			set.add(SCHEME_PLATFORM);
		} else {
			for (StringTokenizer t = new StringTokenizer(propertyValue); t
					.hasMoreTokens();) {
				set.add(t.nextToken().toLowerCase());
			}
		}

		archiveSchemes = Collections.unmodifiableSet(set);
	}

	/** The Constant PROTOCOLS. */
	private static final String[] PROTOCOLS = new String[] { "resource",
			"plugin" };

	/** The Constant SCHEME_SEPARATOR. */
	private static final String SCHEME_SEPARATOR = ":";

	/** The Constant AUTHORITY_SEPARATOR. */
	private static final String AUTHORITY_SEPARATOR = "//";
	private static final String DASH_SEPARATOR = "/";

	/** The Constant replacement. */
	private static final String replacement = "";

	/**
	 * Removes the schemas.
	 * 
	 * @param path
	 *            the path
	 * 
	 * @return the string
	 */
	public static String removeSchemas(String path) {
		if (path == null) {
			return path;
		}
		String newPath = path;
		for (String archiveSchema : archiveSchemes) {
			if (newPath.startsWith(archiveSchema)) {
				newPath = newPath.replaceFirst(archiveSchema, replacement);
			}
		}
		if (newPath.startsWith(SCHEME_SEPARATOR)) {
			newPath = newPath.replaceFirst(SCHEME_SEPARATOR, replacement);
			if (newPath.startsWith(AUTHORITY_SEPARATOR)) {
				newPath = newPath
						.replaceFirst(AUTHORITY_SEPARATOR, replacement);
			}
		}
		if (Platform.OS_WIN32.equals(Platform.getOS())) {
			if (newPath.startsWith(DASH_SEPARATOR)) {
				newPath = newPath.replaceFirst(DASH_SEPARATOR, "");
			}
		}
		return newPath;
	}

	/**
	 * Removes the workspace.
	 * 
	 * @param path
	 *            the path
	 * 
	 * @return the string
	 */
	public static String removeWorkspace(String path) {
		if (path == null) {
			return path;
		}
		String newPath = path;
		if (newPath.startsWith(getWorkspaceLocation().toString())
				|| newPath.startsWith("/" + getWorkspaceLocation().toString())) {
			newPath = newPath.replaceFirst(getWorkspaceLocation().toString(),
					replacement);
		}
		return newPath;
	}

	/**
	 * Removes the protocols.
	 * 
	 * @param path
	 *            the path
	 * 
	 * @return the string
	 */
	public static String removeProtocols(String path) {
		if (path == null) {
			return null;
		}
		for (String protocol : PROTOCOLS) {
			if (path.startsWith(protocol) || path.startsWith("/" + protocol)) {
				path = path.replaceFirst(protocol, replacement);
			}
		}
		return path;
	}

	/**
	 * Adds the root.
	 * 
	 * @param path
	 *            the path
	 * 
	 * @return the string
	 */
	public static String addRoot(String path) {
		if (path == null) {
			return path;
		}
		String newPath = path;
		String rootPath = Path.ROOT.toString();
		while (newPath.startsWith(rootPath)) {
			newPath = newPath.replaceFirst(rootPath, replacement);
		}
		if (newPath.startsWith(Path.ROOT.toString()) == false) {
			newPath = Path.ROOT.toString() + newPath;
		}
		return newPath;
	}

	/**
	 * Gets the relative workspace from editor input. Removes <URI>'s fragments.
	 * 
	 * @param input
	 *            the input
	 * 
	 * @return the relative workspace from editor input
	 */
	public static String getRelativeWorkspaceFromEditorInput(IEditorInput input) {
		return getRelativeWorkspaceFromEditorInput(input, false);
	}

	/**
	 * Gets the relative workspace from editor input. Removes <URI>'s fragments.
	 * 
	 * @param input
	 *            the input
	 * 
	 * @return the relative workspace from editor input
	 */
	public static String getRelativeWorkspaceFromEditorInput(
			IEditorInput input, boolean keepSchema) {
		if (input == null) {
			return null;
		}
		String uriString = null;
		if (input instanceof FileEditorInput) {
			uriString = ((FileEditorInput) input).getFile().getFullPath()
					.toString();
		}
		if (input instanceof URIEditorInput) {
			uriString = ((URIEditorInput) input).getURI().trimFragment()
					.toString();
		}
		if (uriString != null) {
			uriString = fromAbsoluteFileSystemToAbsoluteWorkspace(uriString,
					keepSchema);
		}
		return uriString;
	}

	/**
	 * Gets the relative workspace from editor input. Does not remove <URI>'s
	 * fragments.
	 * 
	 * @param input
	 *            the input
	 * 
	 * @return the relative workspace from editor input
	 */
	public static String getRelativeWorkspaceFromEditorInputWithFragment(
			IEditorInput input) {
		if (input == null) {
			return null;
		}
		String uriString = null;
		if (input instanceof FileEditorInput) {
			uriString = ((FileEditorInput) input).getURI().toString();
		}
		if (input instanceof URIEditorInput) {
			uriString = ((URIEditorInput) input).getURI().toString();
		}
		if (uriString != null) {
			uriString = fromAbsoluteFileSystemToAbsoluteWorkspace(uriString);
		}
		return uriString;
	}

	/**
	 * Gets the relative workspace from editor input. Does not remove <URI>'s
	 * fragments.
	 * 
	 * @param input
	 *            the input
	 * 
	 * @return the relative workspace from editor input
	 */
	public static String getRelativeWorkspaceFromEditorInputWithFragment(
			IEditorInput input, boolean keepSchema) {
		if (input == null) {
			return null;
		}
		String uriString = null;
		if (input instanceof FileEditorInput) {
			uriString = ((FileEditorInput) input).getURI().toString();
		}
		if (input instanceof URIEditorInput) {
			uriString = ((URIEditorInput) input).getURI().toString();
		}
		if (uriString != null) {
			uriString = fromAbsoluteFileSystemToAbsoluteWorkspace(uriString,
					keepSchema);
		}
		return uriString;
	}

	/**
	 * Returns a string that represents the path of a url but relative to the
	 * given project. If the url is not in the project path, then a null value
	 * is returned.
	 * 
	 * @param url
	 * @param project
	 * @return a String with a relative project path
	 */
	public static String translateToProjectRelative(URL url, IProject project) {
		if (url != null && project != null) {
			String workspacePath = PathsUtil
					.fromAbsoluteFileSystemToAbsoluteWorkspace(url.toString(),
							false);
			String projectPath = project.getFullPath().toString();
			if (workspacePath.contains(projectPath)) {
				String relativePath = workspacePath.replaceFirst(projectPath,
						"");
				return relativePath;
			}
		}
		// If we get here, something went wrong
		return null;
	}

	public static URI getBaseWorkspacePathURI(String uri) {
		return getBaseWorkspacePathURI(URI.createURI(uri));
	}

	/**
	 * Get the absolute URI for the workspace from the given URI, without file
	 * name.
	 * 
	 * @param uri
	 * @return
	 */
	public static URI getBaseWorkspacePathURI(URI uri) {
		if (uri == null) {
			return null;
		}
		String workspaceURIString = fromAbsoluteFileSystemToAbsoluteWorkspace(uri
				.toString());
		uri = URI.createURI(workspaceURIString);
		if (uri.isFile()) {
			if (uri.fileExtension() != null) {
				uri = uri.trimFileExtension();
			}
			uri = uri.trimSegments(1);
		}
		return uri;
	}

	/**
	 * Removes file extension and file name from the given URI.
	 * 
	 * @param uri
	 * @return
	 */
	public static URI getURIWithoutFilename(URI uri) {
		if (uri == null) {
			return null;
		}
		if (uri.isFile()) {
			if (uri.fileExtension() != null) {
				uri = uri.trimFileExtension();
			}
		}
		uri = uri.trimSegments(1);
		return uri;
	}

	/**
	 * Gets the file name ("name" + "." + "extension") from the given URI.
	 * 
	 * @param uri
	 * @return
	 */
	public static String getFileNameFromURI(URI uri) {
		if (uri == null) {
			return null;
		}
		if (uri.isFile()) {
			String fileName = "";
			fileName += uri.segment(uri.segmentCount() - 1);
			if (uri.fileExtension() != null) {
				fileName += "." + uri.fileExtension();
			}
			return fileName;
		}
		return null;
	}

	private static final String UpperDirectory = "..";

	/**
	 * Adapts the relativeURI to an absolute URI taking absoluteURI as the
	 * absolute URI. <br>
	 * relativeURI is expected to have no file name or file extension.
	 * 
	 * @param absoluteUri
	 * @param relativeURI
	 * @return
	 */
	public static URI getAdaptedURI(URI absoluteUri, URI relativeURI) {
		if (absoluteUri == null || relativeURI == null) {
			return null;
		}
		URI finalURI = URI.createURI(absoluteUri.toString());
		for (String segment : relativeURI.segments()) {
			if (UpperDirectory.equals(UpperDirectory)) {
				finalURI = finalURI.trimSegments(1);
			} else {
				finalURI = finalURI.appendSegment(segment);
			}
		}
		return finalURI;
	}

	/**
	 * Adapts the relativeURI to an absolute URI taking absoluteURI as the
	 * absolute URI.<br>
	 * relativeURI is expected to have a valid file name and file extension.
	 * 
	 * @param absoluteUri
	 * @param relativeURI
	 * @return
	 */
	public static URI getAdaptedURIWithFilename(URI absoluteUri, URI relativeURI) {
		if (absoluteUri == null || relativeURI == null) {
			return null;
		}
		if (relativeURI.hasAbsolutePath()) {
			return relativeURI;
		}
		String fileName = relativeURI.lastSegment();
		relativeURI = getURIWithoutFilename(relativeURI);
		URI finalURI = URI.createURI(absoluteUri.toString());
		for (String segment : relativeURI.segments()) {
			if (UpperDirectory.equals(segment)) {
				finalURI = finalURI.trimSegments(1);
			} else {
				finalURI = finalURI.appendSegment(segment);
			}
		}
		finalURI = finalURI.appendSegment(fileName);
		return finalURI;
	}
}
