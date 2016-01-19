/*******************************************************************************
 * Copyright (c) 2007 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – Initial API 
 * implementation.
 * 				Javier Muñoz - Added resource listener
 *
 ******************************************************************************/
package es.cv.gvcase.ide.navigator;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import es.cv.gvcase.ide.navigator.util.NavigatorUtil;
import es.cv.gvcase.ide.navigator.view.MOSKittCommonNavigator;

/**
 * Activates the MOSKitt explorers in a {@link Display} synched thread.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * @author <a href="mailto:jmunoz@prodevelop.es">Javier Muñoz</a>
 */
public class StartUp implements IStartup {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IStartup#earlyStartup()
	 */
	public void earlyStartup() {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				activateCommonNavigatorView(MOSKittCommonNavigator.ModelExplorerID);
				activateCommonNavigatorView(MOSKittCommonNavigator.ResourceExplorerID);
			}
		});
		addResourceChangeListener();
	}

	/**
	 * Adds a {@link CloseEditorsResouceRemovedListener} to the workspace.
	 */
	protected void addResourceChangeListener() {
		// Listener for closing open editors when a resource is removed
		// from the workspace
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		workspace.addResourceChangeListener(getResourceChangeListener());
	}

	/**
	 * Creates a new {@link CloseEditorsResouceRemovedListener}.
	 * 
	 * @return
	 */
	protected IResourceChangeListener getResourceChangeListener() {
		return new CloseEditorsResouceRemovedListener();
	}

	/**
	 * {@link IResourceChangeListener} that listens to resources being removed
	 * and closes editors that are editing that resource.
	 * 
	 * @author <a href="mailto:jmunoz@prodevelop.es">Javier Muñoz</a>
	 * 
	 */
	class CloseEditorsResouceRemovedListener implements IResourceChangeListener {
		/**
		 * Upon a resource change check if the change is the removal of the
		 * resource. If so, close all editors that are editing it.
		 */
		public void resourceChanged(IResourceChangeEvent event) {
			IResourceDelta delta = event.getDelta();
			try {
				// For each open editor
				for (final IEditorReference editor : PlatformUI.getWorkbench()
						.getWorkbenchWindows()[0].getActivePage()
						.getEditorReferences()) {
					IEditorInput input;
					try {
						input = editor.getEditorInput();
						IFileEditorInput fileInput = (IFileEditorInput) input
								.getAdapter(IFileEditorInput.class);
						// Finding the removed file in the changes
						// set
						if (fileInput != null) {
							IResourceDelta changedResourceDelta = delta
									.findMember(fileInput.getFile()
											.getFullPath());
							if (changedResourceDelta != null
									&& changedResourceDelta.getKind() == IResourceDelta.REMOVED) {
								Display.getDefault().syncExec(new Runnable() {

									public void run() {
										PlatformUI.getWorkbench()
												.getWorkbenchWindows()[0]
												.getActivePage()
												.closeEditor(
														editor.getEditor(false),
														false);
									}
								});
							}
						}
					} catch (PartInitException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} catch (NullPointerException e) {
				return;
			} catch (ArrayIndexOutOfBoundsException e) {
				return;
			}
		}
	}

	/**
	 * Activate common navigator view.
	 * 
	 * @param viewID
	 *            the view id
	 */
	private void activateCommonNavigatorView(String viewID) {
		IViewPart aux = NavigatorUtil.findViewPart(viewID);
		if (aux instanceof MOSKittCommonNavigator) {
			((MOSKittCommonNavigator) aux).activate();
		}
	}
}
