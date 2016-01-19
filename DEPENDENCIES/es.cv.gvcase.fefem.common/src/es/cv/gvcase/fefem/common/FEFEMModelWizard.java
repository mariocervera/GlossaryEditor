package es.cv.gvcase.fefem.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.CommonPlugin;
import org.eclipse.emf.common.EMFPlugin;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.edit.ui.provider.ExtendedImageRegistry;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.ISetSelectionTarget;

public abstract class FEFEMModelWizard extends Wizard implements INewWizard {

	protected abstract EMFPlugin getEditorPlugin();

	protected abstract EMFPlugin getEditPlugin();

	protected abstract EPackage getEPackage();

	protected abstract String getModelName();

	protected abstract String getInitialObjectName();

	protected String getEncoding() {
		return "UTF-8";
	}

	private String modelName = getModelName();

	public List<String> FILE_EXTENSIONS = Collections.unmodifiableList(Arrays
			.asList(getEditorPlugin().getString(
					"_UI_" + modelName + "EditorFilenameExtensions").split(
					"\\s*,\\s*")));

	public final String FORMATTED_FILE_EXTENSIONS = getEditorPlugin()
			.getString("_UI_" + modelName + "EditorFilenameExtensions")
			.replaceAll("\\s*,\\s*", ", ");

	protected EPackage ePackage = getEPackage();

	protected EFactory eFactory = ePackage.getEFactoryInstance();

	protected FEFEMWizardNewFileCreationPage newFileCreationPage;

	protected IStructuredSelection selection;

	protected IWorkbench workbench;

	protected List<String> initialObjectNames;

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;
		this.selection = selection;
		setWindowTitle(getEditorPlugin().getString("_UI_Wizard_label"));
		setDefaultPageImageDescriptor(ExtendedImageRegistry.INSTANCE
				.getImageDescriptor(getEditorPlugin().getImage(
						"full/wizban/New" + modelName)));
	}

	protected Collection<String> getInitialObjectNames() {
		if (initialObjectNames == null) {
			initialObjectNames = new ArrayList<String>();
			for (EClassifier eClassifier : ePackage.getEClassifiers()) {
				if (eClassifier instanceof EClass) {
					EClass eClass = (EClass) eClassifier;
					if (!eClass.isAbstract()) {
						initialObjectNames.add(eClass.getName());
					}
				}
			}
			Collections.sort(initialObjectNames, CommonPlugin.INSTANCE
					.getComparator());
		}
		return initialObjectNames;
	}

	protected EObject createInitialModel() {
		EClass eClass = (EClass) ePackage
				.getEClassifier(getInitialObjectName());
		EObject rootObject = eFactory.create(eClass);
		return rootObject;
	}

	@Override
	public boolean performFinish() {
		try {
			// Remember the file.
			//
			final IFile modelFile = getModelFile();

			// Do the work within an operation.
			//
			WorkspaceModifyOperation operation = new WorkspaceModifyOperation() {
				@Override
				protected void execute(IProgressMonitor progressMonitor) {
					try {
						// Create a resource set
						//
						ResourceSet resourceSet = new ResourceSetImpl();

						// Get the URI of the model file.
						//
						URI fileURI = URI.createPlatformResourceURI(modelFile
								.getFullPath().toString(), true);

						// Create a resource for this file.
						//
						Resource resource = resourceSet.createResource(fileURI);

						// Add the initial model object to the contents.
						//
						EObject rootObject = createInitialModel();
						if (rootObject != null) {
							resource.getContents().add(rootObject);
						}

						// Save the contents of the resource to the file system.
						//
						Map<Object, Object> options = new HashMap<Object, Object>();
						options.put(XMLResource.OPTION_ENCODING, getEncoding());
						resource.save(options);
					} catch (Exception exception) {
						getEditorPlugin().log(exception);
					} finally {
						progressMonitor.done();
					}
				}

			};

			getContainer().run(false, false, operation);

			// Select the new file resource in the current view.
			//
			IWorkbenchWindow workbenchWindow = workbench
					.getActiveWorkbenchWindow();
			IWorkbenchPage page = workbenchWindow.getActivePage();
			final IWorkbenchPart activePart = page.getActivePart();
			if (activePart instanceof ISetSelectionTarget) {
				final ISelection targetSelection = new StructuredSelection(
						modelFile);
				getShell().getDisplay().asyncExec(new Runnable() {
					public void run() {
						((ISetSelectionTarget) activePart)
								.selectReveal(targetSelection);
					}
				});
			}

			// Open an editor on the new file.
			//
			try {
				page.openEditor(new FileEditorInput(modelFile), workbench
						.getEditorRegistry().getDefaultEditor(
								modelFile.getFullPath().toString()).getId());
			} catch (PartInitException exception) {
				MessageDialog.openError(workbenchWindow.getShell(),
						getEditorPlugin()
								.getString("_UI_OpenEditorError_label"),
						exception.getMessage());
				return false;
			}

			return true;
		} catch (Exception exception) {
			getEditorPlugin().log(exception);
			return false;
		}
	}

	public class FEFEMWizardNewFileCreationPage extends
			WizardNewFileCreationPage {
		public FEFEMWizardNewFileCreationPage(String pageId,
				IStructuredSelection selection) {
			super(pageId, selection);
		}

		@Override
		protected boolean validatePage() {
			if (super.validatePage()) {
				String extension = new Path(getFileName()).getFileExtension();
				if (extension == null || !FILE_EXTENSIONS.contains(extension)) {
					String key = FILE_EXTENSIONS.size() > 1 ? "_WARN_FilenameExtensions"
							: "_WARN_FilenameExtension";
					setErrorMessage(getEditorPlugin().getString(key,
							new Object[] { FORMATTED_FILE_EXTENSIONS }));
					return false;
				}
				return true;
			}
			return false;
		}

		public IFile getModelFile() {
			return ResourcesPlugin.getWorkspace().getRoot().getFile(
					getContainerFullPath().append(getFileName()));
		}
	}

	@Override
	public void addPages() {
		// Create a page, set the title, and the initial model file name.
		//
		newFileCreationPage = new FEFEMWizardNewFileCreationPage("Whatever",
				selection);
		newFileCreationPage.setTitle(getEditorPlugin().getString(
				"_UI_" + modelName + "ModelWizard_label"));
		newFileCreationPage.setDescription(getEditorPlugin().getString(
				"_UI_" + modelName + "ModelWizard_description"));
		newFileCreationPage.setFileName(getEditorPlugin().getString(
				"_UI_" + modelName + "EditorFilenameDefaultBase")
				+ "." + FILE_EXTENSIONS.get(0));
		addPage(newFileCreationPage);

		// Try and get the resource selection to determine a current directory
		// for the file dialog.
		//
		if (selection != null && !selection.isEmpty()) {
			// Get the resource...
			//
			Object selectedElement = selection.iterator().next();
			if (selectedElement instanceof IResource) {
				// Get the resource parent, if its a file.
				//
				IResource selectedResource = (IResource) selectedElement;
				if (selectedResource.getType() == IResource.FILE) {
					selectedResource = selectedResource.getParent();
				}

				// This gives us a directory...
				//
				if (selectedResource instanceof IFolder
						|| selectedResource instanceof IProject) {
					// Set this for the container.
					//
					newFileCreationPage.setContainerFullPath(selectedResource
							.getFullPath());

					// Make up a unique new name here.
					//
					String defaultModelBaseFilename = getEditorPlugin()
							.getString(
									"_UI_" + modelName
											+ "EditorFilenameDefaultBase");
					String defaultModelFilenameExtension = FILE_EXTENSIONS
							.get(0);
					String modelFilename = defaultModelBaseFilename + "."
							+ defaultModelFilenameExtension;
					for (int i = 1; ((IContainer) selectedResource)
							.findMember(modelFilename) != null; ++i) {
						modelFilename = defaultModelBaseFilename + i + "."
								+ defaultModelFilenameExtension;
					}
					newFileCreationPage.setFileName(modelFilename);
				}
			}
		}
	}

	public IFile getModelFile() {
		return newFileCreationPage.getModelFile();
	}

}
