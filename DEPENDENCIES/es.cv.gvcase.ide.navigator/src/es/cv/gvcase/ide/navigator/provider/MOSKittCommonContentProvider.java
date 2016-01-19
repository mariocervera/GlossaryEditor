/*******************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – Initial API implementation.
 * 				 Javier Muñoz (Integranova) - Support for grouping by type
 * 				 Marc Gil Sendra (Prodevelop) - Improve to reduce the time to refresh the tree
 *
 ******************************************************************************/
package es.cv.gvcase.ide.navigator.provider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.emf.edit.ui.provider.ExtendedImageRegistry;
import org.eclipse.emf.transaction.ResourceSetChangeEvent;
import org.eclipse.gmf.runtime.diagram.ui.parts.DiagramEditor;
import org.eclipse.gmf.runtime.emf.core.resources.GMFResource;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.ui.navigator.ICommonContentProvider;
import org.eclipse.ui.navigator.ICommonLabelProvider;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import es.cv.gvcase.ide.navigator.search.factories.GroupableAdapterFactory;
import es.cv.gvcase.ide.navigator.util.NavigatorUtil;
import es.cv.gvcase.ide.navigator.view.MOSKittCommonNavigator;
import es.cv.gvcase.ide.navigator.view.MOSKittModelNavigator;
import es.cv.gvcase.mdt.common.util.MultiDiagramUtil;

/**
 * An aggregation of {@link ICommonContentProvider} and
 * {@link ICommonLabelProvider} to be used in the
 * 'org.eclipse.ui.navigatorContent' extension point.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 */
public abstract class MOSKittCommonContentProvider extends LabelProvider
		implements ICommonContentProvider, ICommonLabelProvider,
		IMOSKittNavigatorPropertySheetContributor {

	/** {@link Viewer} to provide content for. */
	protected Viewer myViewer = null;

	/**
	 * {@link List} of {@link AdapterFactory}s to be used as content and label
	 * providers.
	 */
	protected List<AdapterFactory> factories = new ArrayList<AdapterFactory>();

	/** A {@link ComposedAdapterFactory} that groups all factories to use. */
	protected ComposedAdapterFactory factory = null;

	protected GroupableAdapterFactory groupableFactory = null;

	protected LabelProvider decoratedLabelProvider = null;

	/** {@link ICommonContentExtensionSite} as given in initialization. */
	protected ICommonContentExtensionSite contentExtensionSite = null;

	/** {@link ILabelDecorator} that removes the type name */
	protected ILabelDecorator removeTypeNameDecorator = null;

	protected ILabelDecorator getRemoveTypeNameDecorator() {
		if (removeTypeNameDecorator == null) {
			removeTypeNameDecorator = new NoTypePrefixLabelDecorator();
		}
		return removeTypeNameDecorator;
	}

	/** The resource factories. */
	protected static Map<String, Object> resourceFactories = null;

	/**
	 * Dummy class indicating that no model is available to be shown.
	 * 
	 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano
	 *         Muñoz</a>
	 */
	public class NoModelProvider {
		// Empty class
	}

	/**
	 * Gets the viewer ID as defined in the 'org.eclipse.ui.view' extension
	 * point. To be implemented by subclasses.
	 * 
	 * @return the viewer id
	 */
	protected String getViewerID() {
		return contentExtensionSite.getExtensionStateModel().getViewerId();
	}

	protected LabelProvider getDecoratedLabelProvider() {
		return decoratedLabelProvider;
	}

	protected ILabelDecorator getLabelDecorator() {
		return null;
	}

	public MOSKittCommonContentProvider() {
		addFactory(new DiagramItemProviderAdapterFactory());
		addFactory(new ComposedAdapterFactory(
				ComposedAdapterFactory.Descriptor.Registry.INSTANCE), true);
	}

	/**
	 * Gets the {@link MOSKittCommonNavigator} this content provider is
	 * associated to, via the viewer ID.
	 * 
	 * @return the common navigator
	 */
	protected MOSKittCommonNavigator getCommonNavigator() {
		IViewPart part = NavigatorUtil.findViewPart(getViewerID());
		if (part instanceof MOSKittCommonNavigator) {
			return ((MOSKittCommonNavigator) part);
		}
		return null;
	}

	protected MOSKittModelNavigator getModelNavigator() {
		MOSKittCommonNavigator nav = getCommonNavigator();
		return nav instanceof MOSKittModelNavigator ? (MOSKittModelNavigator) nav
				: null;
	}

	/**
	 * Gets the contributor ID to create the {@link TabbedPropertySheetPage}s.
	 * To be implemented by subclasses.
	 * 
	 * @return the contributor id
	 */
	public abstract String getContributorID();

	/**
	 * Adapts to {@link TabbedPropertySheetPage}.
	 * 
	 * @param adapter
	 *            the adapter
	 * 
	 * @return the adapter
	 */
	public Object getAdapter(Class adapter) {
		if (adapter != null && adapter == TabbedPropertySheetPage.class) {
			final String contributorID = getContributorID();
			if (contributorID != null && contributorID.length() > 0) {
				return new TabbedPropertySheetPage(
						new ITabbedPropertySheetPageContributor() {
							public String getContributorId() {
								return contributorID;
							}
						});
			}
		}
		return null;
	}

	/**
	 * Adds an {@link AdapterFactory} to this
	 * {@link MOSKittCommonContentProvider} list of factories.
	 * 
	 * @param factory
	 *            the factory
	 */
	protected void addFactory(AdapterFactory factory) {
		factories.add(factory);
	}

	/**
	 * Adds an {@link AdapterFactory} to this
	 * {@link MOSKittCommonContentProvider} list of factories.
	 * 
	 * @param factory
	 *            the factory
	 * @param build
	 *            If true, a new {@link ComposedAdapterFactory} will be built
	 *            with the current list of factories.
	 */
	protected void addFactory(AdapterFactory factory, boolean build) {
		factories.add(factory);
		if (build) {
			buildFactory();
		}
	}

	/**
	 * Builds a {@link ComposedAdapterFactory} using the current list of
	 * factories.
	 */
	protected void buildFactory() {
		factory = new ComposedAdapterFactory(factories);
		ILabelDecorator labelDecorator = getLabelDecorator();
		if (labelDecorator != null) {
			decoratedLabelProvider = new DecoratingLabelProvider(
					new AdapterFactoryLabelProvider(factory), labelDecorator);
		} else {
			decoratedLabelProvider = null;
		}

		groupableFactory = new GroupableAdapterFactory(factory);
	}

	/**
	 * Override this method if you want to execute additional actions when the
	 * resource is modified
	 * 
	 * @param event
	 */
	public void resourceSetChanged(ResourceSetChangeEvent event) {
		for (Object object : event.getNotifications()) {
			if (object instanceof Notification) {
				Notification notification = (Notification) object;
				if (shouldHandleNotification(notification)) {
					handleNotification(notification);
				}
			}

		}
	}

	/**
	 * Returns true if the {@link Notification} from the
	 * {@link ResourceSetChangeEvent} should be handled. <br>
	 * Subclasses may override to decide whether to handle a notification or
	 * not.
	 * 
	 * @param notification
	 * @return
	 */
	protected boolean shouldHandleNotification(Notification notification) {
		if (notification == null) {
			return false;
		}
		// check if the changes come from the editor this provider knows how to
		// handle
		if (!canHandleOpenEditor()) {
			return false;
		}
		// the default behavior handles notifications of elements added that
		// are not Views or EAnnotations.
		if (notification.getEventType() == Notification.ADD) {
			Object value = notification.getNewValue();
			if (value instanceof View || value instanceof EAnnotation) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}

	/**
	 * Handles a {@link Notification} from a {@link ResourceSetChangeEvent}. <br>
	 * Subclasses may override to handle notifications.
	 * 
	 * @param notification
	 */
	protected void handleNotification(Notification notification) {
		Object newValue = notification.getNewValue();
		// the default handling is setting the selection to the new element
		// added.
		if (getCommonNavigator() != null
				&& getCommonNavigator().isLinkingEnabled()
				&& getCommonNavigator().getCommonViewer() != null
				&& isNewlyCreatedEObject(notification)) {
			ISelection newSelection = new StructuredSelection(newValue);
			// add only when the notification is from a newly created
			// EObject.
			getCommonNavigator().getCommonViewer().setSelection(newSelection);
		}
	}

	/**
	 * Find out whether the Given Notification is the addition of a new element
	 * that has to be handled.
	 * 
	 * @param notification
	 * @return
	 */
	protected boolean isNewlyCreatedEObject(Notification notification) {
		// a newly created element notification is one that has the type as ADD,
		// has a new value that is an EObject and that EObject's container is
		// the notifier.
		if (notification == null) {
			return false;
		} else {
			if (notification.getEventType() == Notification.ADD
					&& notification.getNewValue() instanceof EObject) {
				// An Add event can be treated
				EObject eObject = (EObject) notification.getNewValue();
				if (eObject instanceof EAnnotation) {
					// EAnnotations are not treated
					return false;
				}
				if (notification.getNotifier() instanceof EAnnotation) {
					// Changes in an Eannotation are not treated
					return false;
				}
				if (eObject.eContainer() != null
						&& eObject.eContainer().equals(
								notification.getNotifier())) {
					// if the addition is because of an EObject
					return true;
				}
			}
		}
		return false;
	}

	Map<String, Object> ecoreResourceFactories = new HashMap<String, Object>();

	/**
	 * Gets a mapping of file extensions and resource factory types, to be used
	 * in a {@link ResourceSetChangeEvent}. To be implemented by subclasses.
	 * 
	 * @return the resource factories
	 */
	protected Map<String, Object> getResourceFactories() {
		if (ecoreResourceFactories.isEmpty()) {
			ecoreResourceFactories.put("*", ResourceFactoryImpl.class);
		}
		return ecoreResourceFactories;
	}

	/**
	 * Initialization. Stores the given {@link ICommonContentExtensionSite}.
	 * 
	 * @param config
	 *            the config
	 */
	public void init(ICommonContentExtensionSite config) {
		contentExtensionSite = config;
		MOSKittCommonNavigator.addPropertySheetContributor(this);
	}

	/**
	 * Gets the children of the given element, using the
	 * {@link ComposedAdapterFactory}.
	 * 
	 * @param parentElement
	 *            the parent element
	 * 
	 * @return the children
	 */
	public Object[] getChildren(Object parentElement) {

		// if no content provider knows how to handle this element, do nothing
		if (canHandleOpenEditor() == false) {
			return new Object[0];
		}

		Object[] children = null;

		// if grouping is enabled, return the children in a folder organization
		if (getModelNavigator().isGroupingChildsEnabled()
				&& groupableFactory
						.isFactoryForType(ITreeItemContentProvider.class)) {
			ITreeItemContentProvider provider = (ITreeItemContentProvider) groupableFactory
					.adapt(parentElement, ITreeItemContentProvider.class);
			return provider == null ? null : provider
					.getChildren(parentElement).toArray();
		}

		// get the normal children from one of the available providers
		if (factory.isFactoryForType(ITreeItemContentProvider.class)) {
			ITreeItemContentProvider provider = (ITreeItemContentProvider) factory
					.adapt(parentElement, ITreeItemContentProvider.class);
			children = provider == null ? null : provider.getChildren(
					parentElement).toArray();
		} else if (parentElement instanceof ITreeContentProvider) {
			children = ((ITreeContentProvider) parentElement)
					.getChildren(parentElement);
		} else if (parentElement instanceof ICommonContentProvider) {
			children = ((ICommonContentProvider) parentElement)
					.getChildren(parentElement);
		} else if (parentElement instanceof ITreeItemContentProvider) {
			children = ((ITreeItemContentProvider) parentElement).getChildren(
					parentElement).toArray();
		}

		// add any extra children as children
		// the default method adds diagrams related to this element.
		children = addExtraChildren(parentElement, children);

		return children;
	}

	/**
	 * Hook method to add extra children to the given element. This default
	 * method adds the related diagrams to the element. <br>
	 * Subclasses may override this method to add (or remove) children for the
	 * parentElement.
	 * 
	 * @param parentElement
	 * @param children
	 * @return
	 */
	protected Object[] addExtraChildren(Object parentElement, Object[] children) {
		EObject eObject = (EObject) Platform.getAdapterManager().getAdapter(
				parentElement, EObject.class);
		if (eObject == null) {
			return children;
		}

		List<Diagram> diagrams = getDiagramsAssociatedToElement(eObject);
		if (diagrams == null) {
			return children;
		}

		List allChildren = new ArrayList();
		allChildren.addAll(Arrays.asList(children));
		allChildren.addAll(diagrams);

		return allChildren.toArray();
	}

	/**
	 * Gets the parent of the given element, using the
	 * {@link ComposedAdapterFactory}.
	 * 
	 * @param element
	 *            the element
	 * 
	 * @return the parent
	 */
	public Object getParent(Object element) {
		if (canHandleOpenEditor() == false) {
			// return new Object[0];
			return null;
		}

		if (getModelNavigator().isGroupingChildsEnabled()
				&& groupableFactory
						.isFactoryForType(ITreeItemContentProvider.class)) {
			ITreeItemContentProvider provider = (ITreeItemContentProvider) groupableFactory
					.adapt(element, ITreeItemContentProvider.class);
			return provider == null ? new Object[0] : provider
					.getParent(element);
		}

		if (factory.isFactoryForType(ITreeContentProvider.class)) {
			ITreeContentProvider provider = (ITreeContentProvider) factory
					.adapt(element, ITreeContentProvider.class);
			return provider == null ? new Object[0] : provider
					.getParent(element);
		} else if (factory.isFactoryForType(ITreeItemContentProvider.class)) {
			ITreeItemContentProvider provider = (ITreeItemContentProvider) factory
					.adapt(element, ITreeItemContentProvider.class);
			return provider == null ? new Object[0] : provider
					.getParent(element);
		}
		return new Object[0];
	}

	/**
	 * Gets whether the given element has children, using the
	 * {@link ComposedAdapterFactory}.
	 * 
	 * @param element
	 *            the element
	 * 
	 * @return true, if checks for children
	 */
	public boolean hasChildren(Object element) {
		// if the grouping is active, check whether there's any child groupable
		// element.
		if (getModelNavigator() != null
				&& getModelNavigator().isGroupingChildsEnabled()
				&& groupableFactory
						.isFactoryForType(ITreeItemContentProvider.class)) {
			ITreeItemContentProvider provider = (ITreeItemContentProvider) groupableFactory
					.adapt(element, ITreeItemContentProvider.class);
			return provider == null ? false : provider.hasChildren(element);
		}

		// check in the normal providers
		boolean hasChildren = false;
		if (factory.isFactoryForType(ITreeItemContentProvider.class)) {
			ITreeItemContentProvider provider = (ITreeItemContentProvider) factory
					.adapt(element, ITreeItemContentProvider.class);
			hasChildren = provider == null ? false : provider
					.hasChildren(element);
		} else if (element instanceof ITreeContentProvider) {
			hasChildren = ((ITreeContentProvider) element).hasChildren(element);
		} else if (element instanceof ITreeItemContentProvider) {
			hasChildren = ((ITreeItemContentProvider) element)
					.hasChildren(element);
		} else if (element instanceof ICommonContentProvider) {
			hasChildren = ((ICommonContentProvider) element)
					.hasChildren(element);
		}
		// check if the element can have diagrams
		if (!(element instanceof Diagram)) {
			hasChildren = hasChildren || hasExtraChildren(element);
		}

		return hasChildren;
	}

	/**
	 * Hook method to check if an elements has extra children besides those
	 * provided by the item providers. This default method checks that the
	 * element has one or more diagrams associated <br>
	 * Subclasses may override to implement their own logic.
	 * 
	 * 
	 * @param element
	 * @return
	 */
	protected boolean hasExtraChildren(Object element) {
		EObject eObject = (EObject) Platform.getAdapterManager().getAdapter(
				element, EObject.class);
		if (eObject == null) {
			return false;
		}
		return getDiagramsAssociatedToElement(eObject) != null;
	}

	/**
	 * The list of resources processed
	 */
	private List<Resource> processedResources = null;

	/**
	 * The map containing every eObject with diagrams
	 */
	private HashMap<EObject, List<Diagram>> eObjectToDiagrams = null;

	/**
	 * Calculate all the elements that contains diagrams
	 */
	private List<Diagram> getDiagramsAssociatedToElement(EObject eObject) {
		if ((processedResources == null && eObjectToDiagrams == null)
				|| !processedResources.contains(eObject.eResource())) {
			processedResources = new ArrayList<Resource>();
			eObjectToDiagrams = new HashMap<EObject, List<Diagram>>();
			if (eObject.eResource() != null) {
				fillMap(eObject.eResource().getResourceSet());
			}
		}
		return eObjectToDiagrams.get(eObject);
	}

	/**
	 * Fill the map
	 */
	private void fillMap(ResourceSet resourceSet) {
		for (Resource resource : resourceSet.getResources()) {
			if (!(resource instanceof GMFResource)) {
				continue;
			}

			// it's a GMF Resource, so store the eObjects of this Diagram in the
			// list
			for (EObject eObject : resource.getContents()) {
				if (!(eObject instanceof Diagram)) {
					continue;
				}

				Diagram diagram = (Diagram) eObject;

				// add the diagram Element
				EObject diagramElement = diagram.getElement();
				addElementToMap(diagramElement, diagram);

				EAnnotation eAnnotation = diagram
						.getEAnnotation(MultiDiagramUtil.AllDiagramsRelatedToElement);
				if (eAnnotation != null) {
					for (EObject diagramElement2 : eAnnotation.getReferences()) {
						addElementToMap(diagramElement2, diagram);
					}
				}
			}
		}
	}

	/**
	 * Associate the diagram to the element in the map
	 */
	private void addElementToMap(EObject element, Diagram diagram) {
		if (element == null) {
			return;
		}

		if (eObjectToDiagrams.get(element) == null) {
			List<Diagram> diagrams = new ArrayList<Diagram>();
			diagrams.add(diagram);
			eObjectToDiagrams.put(element, diagrams);
		} else {
			eObjectToDiagrams.get(element).add(diagram);
		}

		if (!processedResources.contains(element.eResource())) {
			processedResources.add(element.eResource());
		}
	}

	/**
	 * Gets the children of the given root element, using the
	 * {@link ComposedAdapterFactory}.
	 * 
	 * @param inputElement
	 *            the input element
	 * 
	 * @return the elements
	 */
	public Object[] getElements(Object inputElement) {
		if (canHandleOpenEditor() == false) {
			return new Object[0];
		}
		Resource resource = getResourceFromEditor();
		if (resource == null) {
			return new Object[] { new NoModelProvider() };
		} else if (canHandleResource(resource)) {
			NavigatorRootItem rootItem = new NavigatorRootItem(resource,
					getCommonNavigator());
			return rootItem.getElements(rootItem).toArray();
		} else {
			return new Object[0];
		}
	}

	/**
	 * Returns true if this content provider can handle the given Resource.
	 * 
	 * @param resource
	 *            the resource
	 * 
	 * @return true if the given {@link ResourceSetChangeEvent} can be handled
	 *         by this {@link MOSKittCommonContentProvider}.
	 */
	protected abstract boolean canHandleResource(Resource resource);

	protected boolean canHandleOpenEditor() {
		Resource resource = getResourceFromEditor();
		if (resource != null) {
			return canHandleResource(resource);
		}
		return false;
	}

	/**
	 * Gets the active {@link IEditorPart}'s underlying model resource.
	 * 
	 * @return the resource from editor
	 */
	protected Resource getResourceFromEditor() {
		MOSKittCommonNavigator navigator = getCommonNavigator();
		if (navigator == null) {
			return null;
		}
		IEditorPart editor = navigator.getEditorPart();
		if (editor == null) {
			return null;
		}
		
		if (editor instanceof AbstractDecoratedTextEditor) {
			return null;
		}

		Resource resource = NavigatorUtil.getModelResourceFromEditor(editor,
				getResourceFactories());
		if (resource == null) {
			return null;
		}
		return resource;
	}

	/**
	 * Checks if is resource from active editor.
	 * 
	 * @param gmfResource
	 *            the gmf resource
	 * 
	 * @return true, if is resource from active editor
	 */
	protected boolean isResourceFromActiveEditor(GMFResource gmfResource) {

		IEditorPart editor = NavigatorUtil.getActiveEditor();

		if (editor instanceof DiagramEditor) {
			DiagramEditor diagramEditor = (DiagramEditor) editor;
			Resource diagramResource = diagramEditor.getDiagram().eResource();
			if (diagramResource == gmfResource)
				return true;
		}

		return false;
	}

	/**
	 * Disposal of this {@link MOSKittCommonContentProvider}. For internal use
	 * only. Not to be called by clients.
	 */
	@Override
	public void dispose() {
		myViewer = null;
		factories = null;
		factory = null;
		eObjectToDiagrams = null;
		processedResources = null;
		MOSKittCommonNavigator.removePropertySheetContributor(this);
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		myViewer = viewer;
	}

	public void restoreState(IMemento memento) {
	}

	public void saveState(IMemento memento) {
	}

	/**
	 * Gets the image of the given element, using the
	 * {@link ComposedAdapterFactory}.
	 * 
	 * @param element
	 *            the element
	 * 
	 * @return the image
	 */
	@Override
	public Image getImage(Object element) {
		if (canHandleOpenEditor() == false) {
			return null;
		}

		if (element instanceof PackagingNode) {
			return new GroupableLabelProvider().getImage(element);
		}

		if ((element instanceof Resource && !(element instanceof GMFResource))) {
			if (!this.hasChildren(element)) {
				return PlatformUI.getWorkbench().getSharedImages().getImage(
						ISharedImages.IMG_OBJS_ERROR_TSK);
			}
		}

		if (getDecoratedLabelProvider() != null) {
			return getDecoratedLabelProvider().getImage(element);
		}

		Object preImage = null;
		if (factory.isFactoryForType(ILabelProvider.class)) {
			ILabelProvider provider = (ILabelProvider) factory.adapt(element,
					ILabelProvider.class);
			preImage = provider == null ? null : provider.getImage(element);
		} else if (factory.isFactoryForType(IItemLabelProvider.class)) {
			IItemLabelProvider provider = (IItemLabelProvider) factory.adapt(
					element, IItemLabelProvider.class);
			preImage = provider == null ? null : provider.getImage(element);
		} else if (element instanceof ILabelProvider) {
			preImage = ((ILabelProvider) element).getImage(element);
		}
		return ExtendedImageRegistry.INSTANCE.getImage(preImage);
	}

	/**
	 * Gets the text of the given element, using the
	 * {@link ComposedAdapterFactory}.
	 * 
	 * @param element
	 *            the element
	 * 
	 * @return the text
	 */
	@Override
	public String getText(Object element) {
		String labelText = null;
		if (canHandleOpenEditor() == false) {
			labelText = null;
		} else if (element instanceof NoModelProvider) {
			labelText = "No model";
		} else if (element instanceof PackagingNode) {
			labelText = new GroupableLabelProvider().getText(element);
		} else if (getDecoratedLabelProvider() != null) {
			labelText = getDecoratedLabelProvider().getText(element);
		} else if (factory.isFactoryForType(ILabelProvider.class)) {
			ILabelProvider provider = (ILabelProvider) factory.adapt(element,
					ILabelProvider.class);
			labelText = provider == null ? null : provider.getText(element);
		} else if (factory.isFactoryForType(IItemLabelProvider.class)) {
			IItemLabelProvider provider = (IItemLabelProvider) factory.adapt(
					element, IItemLabelProvider.class);
			labelText = provider == null ? null : provider.getText(element);
		} else if (element instanceof ILabelProvider) {
			labelText = ((ILabelProvider) element).getText(element);
		}
		if (getModelNavigator().isRemovePrefixTypeEnabled()
				&& getRemoveTypeNameDecorator() != null) {
			return getRemoveTypeNameDecorator()
					.decorateText(labelText, element);
		}
		return labelText;
	}

	/**
	 * Gets the description of the given element, using the
	 * {@link ComposedAdapterFactory}.
	 * 
	 * @param anElement
	 *            the an element
	 * 
	 * @return the description
	 */
	public String getDescription(Object anElement) {
		if (canHandleOpenEditor() == false) {
			return null;
		}
		if (factory.isFactoryForType(ICommonLabelProvider.class)) {
			ICommonLabelProvider provider = (ICommonLabelProvider) factory
					.adapt(anElement, ICommonLabelProvider.class);
			return provider == null ? null : provider.getDescription(anElement);
		}
		return null;
	}

	/**
	 * Gets whether this {@link MOSKittCommonContentProvider} can provide a
	 * {@link TabbedPropertySheetPage} for the current context.
	 * 
	 * @return true, if checks for property sheet page
	 */
	public boolean hasPropertySheetPage() {
		Resource resource = getResourceFromEditor();
		return canHandleResource(resource);
	}

	/**
	 * Gets the {@link TabbedPropertySheetPage} specified by this
	 * {@link MOSKittCommonContentProvider}'s contributorID.
	 * 
	 * @return the property sheet page
	 */
	public TabbedPropertySheetPage getPropertySheetPage() {
		if (hasPropertySheetPage()) {
			return new TabbedPropertySheetPage(
					new ITabbedPropertySheetPageContributor() {
						public String getContributorId() {
							return MOSKittCommonContentProvider.this
									.getContributorID();
						}
					});
		}
		return null;
	}
}
