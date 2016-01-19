/**
 * Copyright (c) 2006, 2007 Eclipse.org
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Dmitry Stadnik - initial API and implementation
 */
package es.cv.gvcase.mdt.common.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.ui.URIEditorInput;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.ui.IEditorInput;

/**
 * @author dstadnik
 */
public class URIUtil {

	private URIUtil() {
	}

	public static final String Platform_Resource = "platform:/resource"; //$NON-NLS-1$

	public static IFile getFile(URI uri) {
		//remove fragment, if any
		uri = uri.trimFragment();
		String fileName = uri.toFileString();
		if (fileName != null) {
			return ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(
					new Path(fileName));
		}
		if (uri.toString().startsWith(Platform_Resource)) { //$NON-NLS-1$
			String path = uri.toString().substring(Platform_Resource.length()); //$NON-NLS-1$
			IResource workspaceResource = ResourcesPlugin.getWorkspace()
					.getRoot().findMember(new Path(path));
			if (workspaceResource instanceof IFile) {
				return (IFile) workspaceResource;
			}
		}
		return null;
	}

	public static URI getPlatformResourceURI(IEditorInput input) {
		if (input != null) {
			URI uri = getUri(input);
			if (uri != null) {
				return getPlatformResourceURI(uri);
			}
		}
		return null;
	}

	public static URI getPlatformResourceURI(URI uri) {
		if (uri != null) {
			String uriString = uri.toString();
			if (uriString != null && uriString.startsWith(Platform_Resource)) {
				return uri;
			} else {
				URI platformResourceURI = URI
						.createPlatformResourceURI(uriString, false);
				return platformResourceURI;
			}
		} else {
			return null;
		}
	}

	/**
	 * 
	 * 
	 * @param input
	 * @return
	 */
	public static URI getUri(IEditorInput input) {
		return getUri(input, null);
	}

	/**
	 * 
	 * 
	 * @param input
	 * @return
	 */
	public static URI getUri(IEditorInput input, String editorID) {
		String uriString = null;
		if (input instanceof URIEditorInput) {
			uriString = es.cv.gvcase.emf.common.util.PathsUtil
					.getRelativeWorkspaceFromEditorInputWithFragment(input,
							false);
		} else {
			uriString = es.cv.gvcase.emf.common.util.PathsUtil
					.getRelativeWorkspaceFromEditorInput(input, false);
		}
		if (uriString != null) {
			return URI.createURI(uriString).trimFragment();
		}
		return null;
	}
}
