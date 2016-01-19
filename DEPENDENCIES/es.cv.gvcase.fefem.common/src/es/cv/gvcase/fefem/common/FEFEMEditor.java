package es.cv.gvcase.fefem.common;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.emf.common.ui.MarkerHelper;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.Resource.Factory;
import org.eclipse.emf.ecore.util.Diagnostician;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.XMLResource.URIHandler;
import org.eclipse.emf.ecore.xmi.impl.URIHandlerImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.emf.edit.ui.action.ValidateAction;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.validation.service.ModelValidationService;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.editor.IFormPage;

import es.cv.gvcase.emf.common.part.EditingDomainRegistry;
import es.cv.gvcase.emf.common.part.IEditingDomainRegistrable;
import es.cv.gvcase.emf.common.util.PathsUtil;
import es.cv.gvcase.fefem.common.providers.EObjectSelectionProvider;
import es.cv.gvcase.fefem.common.utils.DefaultSelectionSynchronizerStrategy;
import es.cv.gvcase.fefem.common.utils.ISelectionSynchronizerStrategy;
import es.cv.gvcase.fefem.common.validation.EcoreValidationDecorator;
import es.cv.gvcase.fefem.common.validation.EcoreValidationListener;

/**
 *
 */
/**
 *
 */
public abstract class FEFEMEditor extends FormEditor implements IEditingDomainProvider, IEditingDomainRegistrable {


	protected EObject model;
	
	protected Resource resource;
	
	protected TransactionalEditingDomain domain;
	
	protected EcoreValidationDecorator ecoreValidationDecorator;
	
	protected EcoreValidationListener ecoreValidationListener;

	protected EObjectSelectionProvider selectionProvider;
	
	protected ISelectionListener pageSelectionListener;
	
	protected ISelectionSynchronizerStrategy selectionSynchStrategy;
	
	public FEFEMEditor() {
		super();
		
		// Create our custom Ecore validation listener. The mission of this listeners is propagate calculated diagnostics to each registered IEcoreDiagnosticDispatcher
		// By default the validation is not active
		ecoreValidationListener = new EcoreValidationListener(this, false);

		// Create our unique IEcoreDiagnosticDispatcher who create JFace decorators for all existing FEFEM composite controls showing validation results
		ecoreValidationDecorator = new EcoreValidationDecorator(this);

	}

		
	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		
		super.init(site, input);
		
		// Ecore validation setup
		if(enabledLiveEcoreValidation()){
			
			// Register our diagnostic dispatcher in the listener
			ecoreValidationListener.registerDiagnosticDispatcher(ecoreValidationDecorator);
			
			// our validation listener now is listening for changes made in the model needing to be validated
			ModelValidationService.getInstance().addValidationListener(ecoreValidationListener);
			// our validation listener now is listening for any structured selection done by the user on each StructuredViewer based composite
			getSelectionProvider().addSelectionChangedListener(ecoreValidationListener);
			// our validation listener now is listening for any editor page activated
			addPageChangedListener(ecoreValidationListener);
			
			// Activate the validation
			ecoreValidationListener.setActivated(true);
			
		}
		
		
		hookPageSelection();
		
	}

	private void hookPageSelection(){
		pageSelectionListener = new ISelectionListener(){

			public void selectionChanged(IWorkbenchPart part,
					ISelection selection) {
				pageSelectionChanged(part, selection);
				
			}
			
		};
		this.getSite().getPage().addPostSelectionListener(pageSelectionListener);
	}
	
	/**
	 * Enable or not the default selection synchronization strategy 
	 * May be overrided by subclasses if want to use this feature.
	 * @return
	 */
	protected boolean enabledSelectionSynch(){
		return false;
	}
	
	/**
	 * This feature brings the user a very useful dialog when the model has references to other externals resources
	 * and these resources can not be loaded for any reason
	 * @return
	 */
	protected boolean enabledUnresolvedProxiesDialog(){
		return false;
	}
	/**
	 * Manager for external page selections
	 * 
	 * @param part
	 * @param selection
	 */
	protected void pageSelectionChanged(IWorkbenchPart part, ISelection selection){
		if(part == this)
			return;
		
		if(!(selection instanceof IStructuredSelection))
			return;
		
		
		// Synchronize selection with existing Composites
		if(enabledSelectionSynch())
			synchronizeEObjectSelection(selection);
		
	}
	
	protected ISelectionSynchronizerStrategy getSelectionSynchronizerStrategy(){
		if(selectionSynchStrategy == null)
			selectionSynchStrategy = new DefaultSelectionSynchronizerStrategy();
		
		return selectionSynchStrategy;
	}
	
	/**
	 * Call current registered SelectionSynchronizerStrategy with the received selection
	 * @param selection
	 */
	protected void synchronizeEObjectSelection(ISelection selection){
		
		getSelectionSynchronizerStrategy().synchronizeEObjectSelection(selection, this, this.pages);
		
	}
	
	
	
	@Override
	public void dispose() {
		
		if(this.enabledLiveEcoreValidation()){
			// Remove previously registered listeners
			ModelValidationService.getInstance().removeValidationListener(ecoreValidationListener);
		}
		
		if(pageSelectionListener != null){
			this.getSite().getPage().removePostSelectionListener(pageSelectionListener);
		}
		super.dispose();
	}

	@Override
	protected void addPages() {
		
		loadModel();
		
		for (FormPage page : getEditorPagesList()){
			try {				
				addPage(page);
				page.addPropertyListener(new MyPropertyListener());
			} catch (PartInitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	protected void loadModel() {
		getToolkit();
		
		IEditorInput input = this.getEditorInput();
		// Load the model as an EMF Model
		IFile modelFile = ((IFileEditorInput) input).getFile(); 
		model = loadModel(modelFile.getFullPath());
	}
	
	protected EObject loadModel(IPath filePath) {
		
		org.eclipse.emf.common.util.URI uri = URI.createPlatformResourceURI(filePath.toString(),true);
		ResourceSet resourceSet = null;
		
		if (domain == null) {
			// Obtain an editing domain for the resource
			domain = EditingDomainRegistry.getInstance().get(getEditingDomainID(), filePath.toString());
			
			//ResourceSet resourceSet = new ResourceSetImpl();
			resourceSet = domain.getResourceSet();
	
			
			// Register the appropriate resource factory to handle all file extensions.
			
			// We firtly try to find a suitable resource factory from the provided resource URI
	        Factory resourceFactory = Resource.Factory.Registry.INSTANCE.getFactory(uri);
	        if (resourceFactory != null)
	            resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(filePath.getFileExtension() , resourceFactory);
	        else {
	        		// If not found we create a generic XMI resource factory to handle all file extensions.
	        		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(Resource.Factory.Registry.DEFAULT_EXTENSION,
						new XMIResourceFactoryImpl());
	        }
		

			// Register the package to ensure it is available during loading.
			resourceSet.getPackageRegistry().put(get_eNS_URI(),
					get_eINSTANCE());
		} else {
			resourceSet = domain.getResourceSet();
		}
		
		resource = resourceSet.getResource(uri, true);

		//domain = TransactionalEditingDomain.Factory.INSTANCE.createEditingDomain(resourceSet);
		
		if(resource instanceof XMLResource) {
			((XMLResource)this.resource).getDefaultSaveOptions().put(
					XMLResource.OPTION_URI_HANDLER, getURIHandler());
		}
		
		this.setPartName(getEditorInput().getName());
		return resource.getContents().get(0);
	}
	
	@Override
	public void doSave(IProgressMonitor monitor) {
		
		Activator plugin = Activator.getDefault();
		IPreferenceStore prefStore =  plugin.getPreferenceStore();
		
		boolean toggleState = prefStore
        .getString(Activator.PREFERENCES_VALIDATE_ON_SAVE).equals(
            MessageDialogWithToggle.ALWAYS)
        || prefStore.getString(Activator.PREFERENCES_VALIDATE_ON_SAVE).equals(
            MessageDialogWithToggle.NEVER);
		
		if (!toggleState){
			MessageDialogWithToggle dialog = MessageDialogWithToggle.openYesNoCancelQuestion(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					"On save validation",
					"Do you want to validate the model before saving?",
					"Remember my decision",
					false,
					prefStore,
					Activator.PREFERENCES_VALIDATE_ON_SAVE);
			
				
			if (dialog.getReturnCode() == IDialogConstants.YES_ID){
				validateModel();
			} else if (dialog.getReturnCode() == IDialogConstants.CANCEL_ID){
				return;
			}
		} else if (prefStore.getString(Activator.PREFERENCES_VALIDATE_ON_SAVE).equals(MessageDialogWithToggle.ALWAYS)){
			validateModel();
		}
		
		if (pages != null) {
			for (int i = 0; i < pages.size(); i++) {
				Object page = pages.get(i);
				if (page instanceof IFormPage) {
					IFormPage fpage = (IFormPage) page;
					fpage.doSave(monitor);
				}
			}
		}
		
		try {
			this.resource.save(null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void validateModel(){
		Diagnostician diagnostician = org.eclipse.emf.ecore.util.Diagnostician.INSTANCE;
		Diagnostic diagnostic = diagnostician.validate(this.getModel());
		MarkerHelper markerHelper = new ValidateAction.EclipseResourcesUtil();
		try {
			markerHelper.deleteMarkers(this.getModel());
			markerHelper.createMarkers(diagnostic);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void doSaveAs() {
		// TODO Allow isSave
		
	}
	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}
	
	public EObject getModel(){
		return this.model;
	}
	
	public CommandStack getCommandStack(){
		return this.domain.getCommandStack();
	}
	
	public TransactionalEditingDomain getEditingDomain(){
		return this.domain;
	}
	
	private class MyPropertyListener implements IPropertyListener {
		public void propertyChanged(Object source, int propId) {
			if (propId == PROP_DIRTY) {
				firePropertyChange(PROP_DIRTY);
			}
		}
	}
	
	protected URIHandler getURIHandler() {
		return new URIHandlerImpl() {
			@Override
			public URI deresolve(URI uri) {
				if (resolve && !uri.isRelative() && !uri.isPlatformPlugin()) {
					URI deresolvedURI = uri.deresolve(baseURI, true, true, false);
					if (deresolvedURI.hasRelativePath()) {
						uri = deresolvedURI;
					}
				}
				return uri;
			}
		};
	}

	/**
	 * Enable or not the automatic ecore live validation 
	 * May be overrided by subclasses if want to use automatic ecore live validation 
	 * @return
	 */
	protected boolean enabledLiveEcoreValidation(){
		return false;
	}
		
	public EcoreValidationDecorator getEcoreValidationDecorator() {
		return ecoreValidationDecorator;
	}

	/**
	 * Return the existing instance of EcoreValidatorListener
	 * @return
	 */
	public EcoreValidationListener getEcoreValidationListener() {
		return ecoreValidationListener;
	}

	public EObjectSelectionProvider getSelectionProvider() {
		if(selectionProvider == null){
			// Workbench selection provider
			selectionProvider = new EObjectSelectionProvider();
			this.getSite().setSelectionProvider(selectionProvider);
		}
		return selectionProvider;
	}
	
	public String getEditingDomainResourceURI() {
		IEditorInput input = getEditorInput();
		String uri = PathsUtil.fromEditorInputToURIString(input);
		return PathsUtil.removeSchemas(uri);
	}


	/* 
	 * Descendants should override this method in order to share editing domains between two or more FEFEM based editors
	 * @see es.cv.gvcase.emf.common.part.IEditingDomainRegistrable#getEditingDomainID()
	 */
	public String getEditingDomainID() {
		return Activator.PLUGIN_ID;
	}


	protected abstract String get_eNS_URI();
	
	protected abstract EPackage get_eINSTANCE();
		
	protected abstract List<FormPage> getEditorPagesList();
	
}
