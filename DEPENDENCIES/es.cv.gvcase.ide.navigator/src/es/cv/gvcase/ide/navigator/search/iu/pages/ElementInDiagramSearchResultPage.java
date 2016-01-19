/***************************************************************************
 * Copyright (c) 2008, 2009 Conselleria de Infraestructuras y Transporte,
 * Generalitat de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – initial api implementation
 *
 ******************************************************************************/
package es.cv.gvcase.ide.navigator.search.iu.pages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.ui.URIEditorInput;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;
import org.eclipse.emf.search.core.engine.IModelSearchQuery;
import org.eclipse.emf.search.core.results.AbstractModelSearchResultEntry;
import org.eclipse.emf.search.core.results.IModelResultEntry;
import org.eclipse.emf.search.core.results.IModelSearchResult;
import org.eclipse.emf.search.core.results.ModelSearchResultEvent;
import org.eclipse.emf.search.ui.Activator;
import org.eclipse.emf.search.ui.actions.AbstractModelSearchPageActionGroup;
import org.eclipse.emf.search.ui.actions.OpenInDiagramEditorAction;
import org.eclipse.emf.search.ui.actions.OpenInLegacyEditorAction;
import org.eclipse.emf.search.ui.actions.ToggleOpenButtonAction;
import org.eclipse.emf.search.ui.handlers.IModelElementEditorSelectionHandler;
import org.eclipse.emf.search.ui.pages.ModelEditorOpenEnum;
import org.eclipse.emf.search.ui.providers.DecoratingModelSearchResultLabelProvider;
import org.eclipse.emf.search.ui.services.ActionContributionExtensionManager;
import org.eclipse.emf.search.ui.services.MenuContributionExtensionManager;
import org.eclipse.emf.search.ui.services.ModelSearchParticipantTabExtensionManager;
import org.eclipse.emf.search.ui.services.ParticipantTabDescriptor;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.ISearchResultListener;
import org.eclipse.search.ui.ISearchResultPage;
import org.eclipse.search.ui.ISearchResultViewPart;
import org.eclipse.search.ui.SearchResultEvent;
import org.eclipse.search.ui.text.AbstractTextSearchViewPage;
import org.eclipse.search.ui.text.Match;
import org.eclipse.search2.internal.ui.InternalSearchUI;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.progress.UIJob;

import es.cv.gvcase.ide.navigator.search.results.ElementInDiagramSearchResult;
import es.cv.gvcase.ide.navigator.search.ui.utils.ElementInDiagramSearchResultItemProvider;
import es.cv.gvcase.mdt.common.util.MultiDiagramUtil;

/**
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public class ElementInDiagramSearchResultPage extends
		AbstractTextSearchViewPage implements ISearchResultPage,
		ISearchResultListener {
	private ModelEditorOpenEnum openEditorMode;
	private String fId;
	private AbstractModelSearchPageActionGroup modelSearchActionGroup;

	// Search selection wrappers
	private IModelSearchResult modelSearchResult;

	// Adapter factories based content/label providers
	private ComposedAdapterFactory adapterFactory;
	private ElementInDiagramSearchResultItemProvider searchResultItemProvider;
	private DecoratingModelSearchResultLabelProvider searchResultLabelProvider;
	private List<AdapterFactory> composeableAdapterFactories;
	private List<IModelElementEditorSelectionHandler> modelElementEditorSelectionHandlerList;

	// Menu/Action contribution enabling/visibility support helpers
	private Map<String, String> menuManagerTargetMetaModelIDMap;
	private Map<String, MenuManager> menuManagerMap;
	private Map<String, MenuManager> contributedActionIDToMenuMapping;

	// Model Result Page Actions
	private ToggleOpenButtonAction toggleOpenEditorButtonAction;
	private OpenInDiagramEditorAction openInDiagramEditorAction;
	private OpenInLegacyEditorAction openInLegacyEditorAction;

	private ActionContributionExtensionManager actionContributionExtensionManager;
	private MenuContributionExtensionManager menuContributionExtensionManager;

	// Dialog Settings
	private final static String MODEL_RESULT_PAGE_ID = "modelResultPageID"; //$NON-NLS-1$
	private final static String OPEN_DIAGRAM_TOGGLE_SELECTION_ID = "openDiagramToggleSelectionID"; //$NON-NLS-1$
	private IDialogSettings settings;

	private static final int DEFAULT_ELEMENT_LIMIT = 1000;

	public static Object MODEL_SEARCH_FAMILY = new Object();

	/**
	 * Creates a Model Search Page with result tree
	 */
	public ElementInDiagramSearchResultPage() {
		super(FLAG_LAYOUT_TREE);

		settings = Activator.getDefault().getDialogSettings();

		ModelSearchParticipantTabExtensionManager modelExtensibleSearchParticipantTabExtensionManager = ModelSearchParticipantTabExtensionManager
				.getInstance();

		ParticipantTabDescriptor[] participantTabDescriptors = modelExtensibleSearchParticipantTabExtensionManager
				.getModelSearchParticipantDescriptors();

		composeableAdapterFactories = new ArrayList<AdapterFactory>();
		modelElementEditorSelectionHandlerList = new ArrayList<IModelElementEditorSelectionHandler>();

		// Add AdapterFactoryLabelAdapters
		for (ParticipantTabDescriptor participantTabDescriptor : participantTabDescriptors) {
			composeableAdapterFactories.add(participantTabDescriptor
					.getElementComposeableAdapterFactory());
			modelElementEditorSelectionHandlerList.add(participantTabDescriptor
					.getModelElementEditorSelectionHandler());
		}
		composeableAdapterFactories
				.add(new ResourceItemProviderAdapterFactory());

		adapterFactory = new ComposedAdapterFactory(composeableAdapterFactories);

		searchResultItemProvider = new ElementInDiagramSearchResultItemProvider();
		searchResultLabelProvider = new DecoratingModelSearchResultLabelProvider(
				adapterFactory, modelSearchResult);

		actionContributionExtensionManager = ActionContributionExtensionManager
				.getInstance();
		menuContributionExtensionManager = MenuContributionExtensionManager
				.getInstance();

		menuManagerTargetMetaModelIDMap = new HashMap<String, String>();
		menuManagerMap = new HashMap<String, MenuManager>();
		contributedActionIDToMenuMapping = new HashMap<String, MenuManager>();

		setElementLimit(new Integer(DEFAULT_ELEMENT_LIMIT));

		initResultPageActions();

		loadDialogSettings();
	}

	public void setViewPart(ISearchResultViewPart part) {
		super.setViewPart(part);
		modelSearchActionGroup = new AbstractModelSearchPageActionGroup(part);
	}

	private void initResultPageActions() {
	}

	private boolean hasAValidTargetMetaModeID(String nsURI) {
		if (modelSearchResult.getQuery() instanceof IModelSearchQuery) {
			return (((IModelSearchQuery) modelSearchResult.getQuery())
					.isValidTargetMetaModel(nsURI));
		}
		return false;
	}

	private void addResultPageActions(IMenuManager mgr) {
	}

	protected void fillContextMenu(IMenuManager mgr) {
	}

	protected void fillToolbar(IToolBarManager tbm) {
	}

	private void addGroupActions(IToolBarManager mgr) {
	}

	private void updateGroupingActions() {
	}

	public void init(IPageSite site) {
		super.init(site);
	}

	public void dispose() {
		super.dispose();
	}

	public StructuredViewer getViewer() {
		return super.getViewer();
	}

	@Override
	protected void handleOpen(OpenEvent event) {
		ISelection s = event.getSelection();
		if (s instanceof IStructuredSelection) {
			openEditor(((StructuredSelection) s).getFirstElement());
		}
	}

	/**
	 * Obtains the workspace file corresponding to the specified resource, if it
	 * has a platform-resource URI. Note that the resulting file, if not
	 * <code>null</code>, may nonetheless not actually exist (as the file is
	 * just a handle).
	 * 
	 * @param resource
	 *            an EMF resource
	 * 
	 * @return the corresponding workspace file, or <code>null</code> if the
	 *         resource's URI is not a platform-resource URI
	 */
	public static IFile getFile(Resource resource) {
		if (resource instanceof Resource) {
			ResourceSet rset = resource.getResourceSet();

			return getFile(resource.getURI(), (rset != null) ? rset
					.getURIConverter() : null);
		}
		return null;
	}

	/**
	 * Finds the file corresponding to the specified URI, using a URI converter
	 * if necessary (and provided) to normalize it.
	 * 
	 * @param uri
	 *            a URI
	 * @param converter
	 *            an optional URI converter (may be <code>null</code>)
	 * 
	 * @return the file, if available in the workspace
	 */
	private static IFile getFile(URI uri, URIConverter converter) {
		if ("platform".equals(uri.scheme()) && (uri.segmentCount() > 2)) { //$NON-NLS-1$
			if ("resource".equals(uri.segment(0))) { //$NON-NLS-1$
				IPath path = new Path(URI.decode(uri.path()))
						.removeFirstSegments(1);
				return ResourcesPlugin.getWorkspace().getRoot().getFile(path);
			}
		} else if (uri.isFile() && !uri.isRelative()) {
			return ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(
					new Path(uri.toFileString()));
		}
		// normalize, to see whether may we can resolve it this time
		if (converter instanceof URIConverter) {
			URI normalized = converter.normalize(uri);
			if (!uri.equals(normalized)) {
				// recurse on the new URI
				return getFile(normalized, converter);
			}
		}
		return null;
	}

	/**
	 * Open editor with given EObject selected if any.
	 * 
	 * @param eObject
	 *            EObject to be selected
	 * @return the open editor part, null otherwise
	 * @throws PartInitException
	 */
	public IEditorPart openEditor(EObject eObject) throws PartInitException {
		if (eObject instanceof Diagram) {
			try {
				return MultiDiagramUtil.openDiagram((Diagram) eObject);
			} catch (ExecutionException ex) {
				return null;
			}
		}
		if (eObject != null) {
			Resource resource = eObject.eResource();
			if (resource != null) {
				URI uri = resource.getURI();
				if (uri != null) {
					IEditorInput editorInput = null;
					if (uri.isPlatformResource()) {
						String path = uri.toPlatformString(true);
						IResource workspaceResource = ResourcesPlugin
								.getWorkspace().getRoot().findMember(
										new Path(path));
						if (workspaceResource instanceof IFile) {
							editorInput = new FileEditorInput(
									(IFile) workspaceResource);
						}
					} else {
						editorInput = new URIEditorInput(uri);
					}
					if (editorInput != null) {
						IWorkbench workbench = PlatformUI.getWorkbench();
						IWorkbenchPage page = workbench
								.getActiveWorkbenchWindow().getActivePage();
						IEditorDescriptor desc = workbench.getEditorRegistry()
								.getDefaultEditor(uri.lastSegment());
						if (desc != null) {
							IEditorPart editorPart = page.openEditor(
									editorInput, desc.getId());
							return editorPart;
						}
						return null;
					}
				}
			}
		}
		return null;
	}

	@Override
	protected void showMatch(Match match, int currentOffset, int currentLength)
			throws PartInitException {
		showMatch(match, currentOffset, currentLength, true);
	}

	@Override
	protected void showMatch(Match match, int currentOffset, int currentLength,
			boolean activate) throws PartInitException {

		getViewer().setSelection(new StructuredSelection(match));
		getViewer().reveal(match);
		if (activate) {
			if (match instanceof IModelResultEntry)
				openEditor(((IModelResultEntry) match).getSource());
		}
	}

	/**
	 * Open editor associated to given target (mostly EMF Resources).
	 * 
	 * @param destination
	 *            an EMF Resource
	 */
	public void openEditor(final Object destination) {
		new UIJob("Open EMF Editor") {
			public IStatus runInUIThread(IProgressMonitor monitor) {
				IWorkbenchWindow dw = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow();
				if (dw != null) {
					IWorkbenchPage page = dw.getActivePage();
					if (page != null) {
						if (destination instanceof AbstractModelSearchResultEntry) {
							handleEObjectToOpenEditorFrom((EObject) ((AbstractModelSearchResultEntry) destination)
									.getSource());
						} else if (destination instanceof Resource) {
							if (!((Resource) destination).getContents()
									.isEmpty()) {
								handleEObjectToOpenEditorFrom(((Resource) destination)
										.getContents().get(0));
							}
						}
					}
				}
				return Status.OK_STATUS;
			}
		}.schedule();
	}

	/**
	 * Walk all
	 * 
	 * @{IModelElementEditorSelectionHandler until finding an EObject selection
	 *                                       compatible one.
	 * @param eobject
	 *            given object which open editor operation has to be handled
	 */
	public void handleEObjectToOpenEditorFrom(EObject eobject) {
		try {
			IEditorPart editorPart = openEditor(eobject);
			// check whether the selected EditorPart implements the
			// IEditingDomainProvider interface
			if (editorPart instanceof IEditingDomainProvider) {
				EditingDomain editingDomain = ((IEditingDomainProvider) editorPart)
						.getEditingDomain();
				Object object = editingDomain.getResourceSet().getEObject(
						EcoreUtil.getURI(eobject), true);
				for (IModelElementEditorSelectionHandler modelElementEditorSelectionHandler : modelElementEditorSelectionHandlerList) {
					if (modelElementEditorSelectionHandler
							.isCompatibleModelElementEditorSelectionHandler(editorPart)) {
						modelElementEditorSelectionHandler
								.handleModelSearchElementSelection(editorPart,
										object, getOpenEditorMode());
					}
				}
			}
		} catch (PartInitException e) {
			Activator.getDefault().getLog().log(
					new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0,
							"Editor model element Selection failed !", e));
		}
	}

	/**
	 * 
	 */
	public void setInput(ISearchResult search, Object viewState) {
		super.setInput(search, viewState);
		if (search instanceof ElementInDiagramSearchResult) {
			search.addListener(this);
			refreshResultPage(modelSearchResult = (ElementInDiagramSearchResult) search);
		}
	}

	public void setID(String id) {
		fId = id;
	}

	public String getID() {
		return fId;
	}

	public String getLabel() {
		return modelSearchResult == null ? "" : modelSearchResult.getLabel(); //$NON-NLS-1$
	}

	public void searchResultChanged(final SearchResultEvent e) {
		if (e instanceof ModelSearchResultEvent) {
			final ModelSearchResultEvent ssre = (ModelSearchResultEvent) e;
			// Update view must be done inside the UI thread.
			refreshResultPage(modelSearchResult = (IModelSearchResult) ssre
					.getSearchResult());
		}
	}

	private class RefreshResultsUIJob extends UIJob {
		Object input;

		public RefreshResultsUIJob(Object modelSearchResult) {
			super("Refresh Model Search Results");
			setPriority(Job.INTERACTIVE);
			input = modelSearchResult;
		}

		@Override
		public IStatus runInUIThread(IProgressMonitor monitor) {
			try {
				Job.getJobManager().sleep(InternalSearchUI.FAMILY_SEARCH);
				getDisplay().asyncExec(new Runnable() {
					public void run() {
						if (getViewer().getControl().isDisposed())
							return;// Status.CANCEL_STATUS;
						if (getViewer().getContentProvider() == null)
							return;// Status.CANCEL_STATUS;
						DecoratingModelSearchResultLabelProvider labelProvider = (DecoratingModelSearchResultLabelProvider) getViewer()
								.getLabelProvider();
						labelProvider.setModelSearchResult(modelSearchResult);
						getViewer().refresh(true);
					}
				});
				Job.getJobManager().wakeUp(InternalSearchUI.FAMILY_SEARCH);
			} catch (Exception e) {
				return Status.CANCEL_STATUS;
			}

			return Status.OK_STATUS;
		}

		@Override
		public boolean belongsTo(Object family) {
			return MODEL_SEARCH_FAMILY.equals(family);
		}
	}

	private void refreshResultPage(Object modelSearchResult) {
		if (modelSearchResult instanceof ElementInDiagramSearchResult) {
			new RefreshResultsUIJob(modelSearchResult).schedule();
		}
	}

	@Override
	protected void configureTreeViewer(TreeViewer viewer) {
		viewer.setLabelProvider(searchResultLabelProvider);
		viewer.setContentProvider(searchResultItemProvider);
	}

	@Override
	protected void elementsChanged(Object[] objects) {/* nothing change */
	}

	public ModelEditorOpenEnum getOpenEditorMode() {
		return (openEditorMode == null) ? ModelEditorOpenEnum.TREE
				: openEditorMode;
	}

	public void setOpenEditorMode(ModelEditorOpenEnum openEditorMode) {
		this.openEditorMode = openEditorMode;
		storeDialogSettings();
	}

	private void storeDialogSettings() {
	}

	private void loadDialogSettings() {
	}

	@Override
	protected void clear() {
		getViewer().setInput(null);
	}

	@Override
	protected void configureTableViewer(TableViewer viewer) {
	}

	public ToggleOpenButtonAction getToggleOpenEditorButtonAction() {
		return toggleOpenEditorButtonAction;
	}

	public OpenInDiagramEditorAction getOpenInDiagramEditorAction() {
		return openInDiagramEditorAction;
	}

	public OpenInLegacyEditorAction getOpenInLegacyEditorAction() {
		return openInLegacyEditorAction;
	}
}
