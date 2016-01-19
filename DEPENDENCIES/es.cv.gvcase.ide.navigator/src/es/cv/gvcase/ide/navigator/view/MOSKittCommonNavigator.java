/*******************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – Initial API implementation.
 * 				 Marc Gil Sendra (Prodevelop) - Improve for avoid stupid refreshes
 *
 ******************************************************************************/
package es.cv.gvcase.ide.navigator.view;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.emf.transaction.ResourceSetChangeEvent;
import org.eclipse.emf.transaction.ResourceSetListener;
import org.eclipse.emf.transaction.ResourceSetListenerImpl;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.internal.navigator.extensions.NavigatorContentExtension;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import es.cv.gvcase.ide.navigator.provider.IMOSKittNavigatorPropertySheetContributor;
import es.cv.gvcase.ide.navigator.provider.MOSKittCommonContentProvider;
import es.cv.gvcase.ide.navigator.util.NavigatorUtil;

/**
 * A specialization of {@link CommonNavigator} to be used as MOSKitt
 * navigator(s).
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 */
public class MOSKittCommonNavigator extends CommonNavigator implements
		IEditingDomainProvider, IPropertyListener {

	/** Cached {@link IPropertySheetPage}. */
	private IPropertySheetPage propertySheet = null;

	/** Model explorer navigator view ID. */
	public static final String ModelExplorerID = "es.cv.gvcase.ide.navigator.view.modelViewer";

	/** Resource explorer navigator view ID. */
	public static final String ResourceExplorerID = "es.cv.gvcase.ide.navigator.view.resourceViewer";

	/** {@link EditingDomain} used to perform actions and commands. */
	EditingDomain editingDomain = null;

	/** Active {@link IEditorPart}. */
	IEditorPart editorPart = null;

	/** {@link IWorkbenchPage} this {@link MOSKittCommonNavigator} belong to. */
	IWorkbenchPage myPage = null;

	/** Contributor for {@link TabbedPropertySheetPage}. */
	private static List<IMOSKittNavigatorPropertySheetContributor> propertySheetContributors;

	/**
	 * Adds a {@link TabbedPropertySheetPage}s contributor. Has no effect if
	 * contributor is already added.
	 * 
	 * @param contributor
	 *            the contributor
	 */
	public static void addPropertySheetContributor(
			IMOSKittNavigatorPropertySheetContributor contributor) {
		if (propertySheetContributors == null) {
			propertySheetContributors = new ArrayList<IMOSKittNavigatorPropertySheetContributor>();
		}
		propertySheetContributors.add(contributor);
	}

	/**
	 * Removes a {@link TabbedPropertySheetPage}s contributor.
	 * 
	 * @param contributor
	 *            the contributor
	 */
	public static void removePropertySheetContributor(
			IMOSKittNavigatorPropertySheetContributor contributor) {
		if (propertySheetContributors != null
				&& propertySheetContributors.contains(contributor)) {
			propertySheetContributors.remove(contributor);
		}
	}

	public static List<IMOSKittNavigatorPropertySheetContributor> getPropertySheetContributors() {
		if (propertySheetContributors == null) {
			propertySheetContributors = new ArrayList<IMOSKittNavigatorPropertySheetContributor>();
		}
		return propertySheetContributors;
	}

	/** {@link IPartListener2} to react to editor activations and deactivations. */
	IPartListener2 partListener = new IPartListener2() {

		public void partActivated(IWorkbenchPartReference partRef) {
			handlePartActivated(partRef);
		}

		public void partBroughtToTop(IWorkbenchPartReference partRef) {
		}

		public void partClosed(IWorkbenchPartReference partRef) {
			handlePartDeactivated(partRef);
		}

		public void partDeactivated(IWorkbenchPartReference partRef) {
		}

		public void partHidden(IWorkbenchPartReference partRef) {
		}

		public void partInputChanged(IWorkbenchPartReference partRef) {
			handlePartActivated(partRef);
		}

		public void partOpened(IWorkbenchPartReference partRef) {
			handlePartActivated(partRef);
		}

		public void partVisible(IWorkbenchPartReference partRef) {
			handlePartActivated(partRef);
		}

	};

	/**
	 * {@link ResourceSetListener} to listen and react to changes in the
	 * resource set.
	 */
	ResourceSetListener resourceSetListener = new NotifyContentExtensionsListener();

	/**
	 * {@link ResourceSetListener} that handles changes in the resources and
	 * notifies the content providers.
	 * 
	 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano
	 *         Muñoz</a>
	 * 
	 */
	class NotifyContentExtensionsListener extends ResourceSetListenerImpl {
		@Override
		public void resourceSetChanged(ResourceSetChangeEvent event) {
			// super currently does nothing
			super.resourceSetChanged(event);
			// handle changes in the resource
			handleResourceSetChanged(event);
			// notify content providers of the resource change
			notifyAllContentProviders(event);
		}

		protected void notifyAllContentProviders(ResourceSetChangeEvent event) {
			List notifications = event.getNotifications();
			int i = 0;
			boolean finish = false;
			while (!finish && i < notifications.size()) {
				Object n = notifications.get(i);
				if (n instanceof Notification) {
					Notification notification = (Notification) n;
					if (notification.getNotifier() instanceof EObject) {
						EObject notifier = (EObject) notification.getNotifier();
						Iterator it = getNavigatorContentService()
								.findRootContentExtensions(notifier).iterator();
						while (it.hasNext()) {
							Object obj = it.next();
							finish = true;
							if (obj instanceof NavigatorContentExtension) {
								NavigatorContentExtension nce = (NavigatorContentExtension) obj;
								if (nce.getContentProvider() instanceof MOSKittCommonContentProvider) {
									MOSKittCommonContentProvider provider = (MOSKittCommonContentProvider) nce
											.getContentProvider();
									provider.resourceSetChanged(event);
								}
							}
						}
					}
				}
				i++;
			}
		}
	}

	/**
	 * Gets the listener for the View
	 * 
	 * @return
	 */
	protected IPartListener2 getListener() {
		return partListener;
	}

	/**
	 * Initializes this {@link MOSKittCommonNavigator}. For internal use only.
	 * Not to be called by clients.
	 * 
	 * @param site
	 *            the site
	 * 
	 * @throws PartInitException
	 *             the part init exception
	 */
	@Override
	public void init(IViewSite site) throws PartInitException {
		super.init(site);
		try {
			myPage = site.getPage();
			myPage.addPartListener(getListener());
		} catch (NullPointerException ex) {
		}
		activate();
	}

	/**
	 * Disposal of this {@link MOSKittCommonNavigator}. For internal use only.
	 * Not to be called by clients.
	 */
	@Override
	public void dispose() {
		super.dispose();
		try {
			myPage.removePartListener(getListener());
			deactivate();
		} catch (NullPointerException ex) {
		}
	}

	/**
	 * Activates this {@link MOSKittCommonNavigator} if an {@link IEditorPart}
	 * is activated.
	 * 
	 * @param partRef
	 *            the part ref
	 */
	protected void handlePartActivated(IWorkbenchPartReference partRef) {
		IWorkbenchPart part = partRef.getPart(false);
		if (part instanceof IEditorPart) {
			activate();
			part.addPropertyListener(this);
		} else if (editingDomain != null) {
			refreshViewer();
		}
	}

	/**
	 * Deactivates this {@link MOSKittCommonNavigator} if the active
	 * {@link IEditorPart} is deactivated.
	 * 
	 * @param partRef
	 *            the part ref
	 */
	protected void handlePartDeactivated(IWorkbenchPartReference partRef) {
		IWorkbenchPart part = partRef.getPart(false);
		if (editorPart != null && editorPart.equals(part)) {
			deactivate();
			part.removePropertyListener(this);
		}
	}

	/**
	 * Handle resource set changed. Only refresh the viewer if there are Add,
	 * Remove or Set Notifications, and not for models from GMF (Notation
	 * Package)
	 * 
	 * @param event
	 */
	protected void handleResourceSetChanged(ResourceSetChangeEvent event) {
		boolean needsRefresh = false;
		for (Notification n : event.getNotifications()) {
			// check if a notification that affects an EObject has been affected
			// by an event that the navigator in interested in.
			if (n.getEventType() == Notification.ADD
					|| n.getEventType() == Notification.REMOVE
					|| n.getEventType() == Notification.SET) {
				if (n.getFeature() != null
						&& n.getFeature() instanceof EStructuralFeature) {
					EStructuralFeature feature = (EStructuralFeature) n
							.getFeature();
					EPackage ePackage = feature.getEContainingClass()
							.getEPackage();
					needsRefresh = true;
					break;
				}
			}
			// check whether an element has been added or removed from a
			// resource
			if (n.getNotifier() instanceof Resource
					&& (n.getNewValue() instanceof EObject || n.getOldValue() instanceof EObject)) {
				needsRefresh = true;
				break;
			}
			// check whether a resource has been added or removed from the
			// resourceset
			if (n.getNotifier() instanceof ResourceSet) {
				if (n.getNewValue() instanceof Resource
						|| n.getOldValue() instanceof Resource) {
					needsRefresh = true;
					break;
				}
			}
		}
		//
		if (needsRefresh) {
			refreshViewer();
		}
	}

	/**
	 * Gets the active {@link IEditorPart}, get the {@link EditingDomain} to use
	 * and install a {@link ResourceSetListener} in that {@link EditingDomain}.
	 * Forces the viewer to be refreshed.
	 */
	public void activate() {
		IEditorPart activeEditorPart = NavigatorUtil.getActiveEditor();
		// check if there has been a change in editors.
		boolean changedEditors = activeEditorPart != null
				&& !activeEditorPart.equals(editorPart);
		// if the editing domain is no longer valid or there has been a change
		// in editors, update everything
		if (editingDomain == null || changedEditors) {
			editorPart = NavigatorUtil.getActiveEditor();
			editingDomain = NavigatorUtil.getEditingDomainFromActiveEditor();
			if (editingDomain instanceof TransactionalEditingDomain) {
				((TransactionalEditingDomain) editingDomain)
						.addResourceSetListener(resourceSetListener);
			}
		}
		refreshViewer();
		doUpdate();
	}

	/**
	 * Releases the active {@link IEditorPart}, uninstalls the
	 * {@link ResourceSetListener} from the {@link EditingDomain} and releases
	 * the {@link EditingDomain}. Forces the viewer to be refreshed.
	 */
	public void deactivate() {
		editorPart = null;
		if (editingDomain instanceof TransactionalEditingDomain) {
			((TransactionalEditingDomain) editingDomain)
					.removeResourceSetListener(resourceSetListener);
		}
		editingDomain = null;
		refreshViewer();
		doUpdate();
	}

	/**
	 * An update for subclasses to implement. Is called after activate() and
	 * deactivate().
	 * 
	 */
	public void doUpdate() {
		// nothing
		// for subclasses
	}

	/**
	 * Forces the viewer to be refreshed (If has elements).
	 */
	public void refreshViewer() {
		CommonViewer viewer = getCommonViewer();
		if (viewer != null && viewer.getTree().isDisposed() == false) {
			viewer.refresh();
		}
	}

	/**
	 * Gets the active {@link IEditorPart}.
	 * 
	 * @return the editor part
	 */
	public IEditorPart getEditorPart() {
		return editorPart;
	}

	/**
	 * Gets the {@link EditingDomain} this {@link MOSKittCommonNavigator} is
	 * using.
	 * 
	 * @return the editing domain
	 */
	public EditingDomain getEditingDomain() {
		return editingDomain;
	}

	public TransactionalEditingDomain getTransactionalEditingDomain() {
		EditingDomain domain = getEditingDomain();
		if (domain instanceof TransactionalEditingDomain) {
			return (TransactionalEditingDomain) domain;
		}
		return null;
	}

	// IPropertyListener
	/**
	 * Subclases may react under property changes.
	 * 
	 * @author fjcano
	 */
	public void propertyChanged(Object source, int propId) {
		// nothing to do
		// for subclasses to implement
	}

}
