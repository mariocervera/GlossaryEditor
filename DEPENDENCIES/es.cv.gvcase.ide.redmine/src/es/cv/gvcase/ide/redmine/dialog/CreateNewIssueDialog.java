package es.cv.gvcase.ide.redmine.dialog;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.Section;
import org.redmine.ta.AuthenticationException;
import org.redmine.ta.NotFoundException;
import org.redmine.ta.RedmineException;
import org.redmine.ta.RedmineManager;
import org.redmine.ta.beans.Issue;
import org.redmine.ta.beans.Project;
import org.redmine.ta.beans.Tracker;

import es.cv.gvcase.ide.redmine.Activator;
import es.cv.gvcase.ide.redmine.preferences.MOSKittRedminePreferenceConstants;

public class CreateNewIssueDialog extends Dialog {

	private FormToolkit toolkit = new FormToolkit(Display.getCurrent());
	private final int minWidth = 600;
	private final int minHeight = 400;

	private Button buttonAnonymous;
	private Button buttonUser;

	private Text textKey;
	private Combo comboType;
	private Combo comboProject;
	private Text textSubject;
	private Text textDescription;

	private IPreferenceStore store;

	private final String redmineHost = "https://moskitt.gva.es/redmine";
	private final String defaultAPIAccessKey = "71f4676ccfe1604878bd3d22890f6cdf7550d337";

	private String selectedAPIKey = "";

	public CreateNewIssueDialog() {
		super(Display.getCurrent().getActiveShell());
		store = Activator.getDefault().getPreferenceStore();
	}

	/**
	 * Sets a minimum size to the Shell
	 */
	@Override
	public void create() {
		super.create();
		// getShell().setMinimumSize(getShell().getSize());
		getShell().setMinimumSize(minWidth, minHeight);

		// center the shell on the screen
		centerShell(getShell());
	}

	protected void createButtonsForButtonBar(Composite parent) {
		// create OK and Cancel buttons by default
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				false);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);

		toolkit.adapt(parent);
		toolkit.adapt(parent.getParent());
	}

	/**
	 * Make the Shell resizable
	 */
	@Override
	protected int getShellStyle() {
		return super.getShellStyle() | SWT.RESIZE;
	}

	/**
	 * The main creation control. Creates the content of the dialog
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		getShell().setText("Report a new Issue");

		// External Composite
		Composite composite = (Composite) super.createDialogArea(parent);
		createMainSection(composite);

		selectAutentication();

		return composite;
	}

	private void createMainSection(Composite parent) {
		// main composite
		Composite mainComposite = toolkit.createComposite(parent);
		mainComposite.setLayout(new GridLayout(2, false));
		GridData gd = new GridData(GridData.FILL_BOTH);
		mainComposite.setLayoutData(gd);

		// Section for autentication
		Section section = toolkit.createSection(mainComposite, Section.TWISTIE
				| Section.TITLE_BAR | Section.EXPANDED);
		section.setText("Autetication Information for Redmine");
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING));

		Composite sectionContent = toolkit.createComposite(section);
		sectionContent.setLayout(new GridLayout(2, false));
		gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		sectionContent.setLayoutData(gd);
		section.setClient(sectionContent);

		buttonAnonymous = toolkit.createButton(sectionContent, "Anonymous",
				SWT.RADIO);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		buttonAnonymous.setLayoutData(gd);
		buttonAnonymous.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (buttonAnonymous.getSelection()) {
					setAnonymous();
				}
			}
		});

		buttonUser = toolkit.createButton(sectionContent,
				"Personal API Access Key", SWT.RADIO);
		gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		buttonUser.setLayoutData(gd);
		buttonUser.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (buttonUser.getSelection()) {
					setUser();
				}
			}
		});

		textKey = toolkit.createText(sectionContent, "", SWT.BORDER);
		textKey
				.setToolTipText("You can find this Key in your account page on MOSKitt Redmine");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		textKey.setLayoutData(gd);
		textKey.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent event) {
				switch (event.keyCode) {
				case SWT.CR:
					comboProject.setFocus();
				}
			}
		});
		textKey.setTextLimit(40);
		textKey.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				store.setValue(
						MOSKittRedminePreferenceConstants.APIAccessKeyUser,
						textKey.getText());
				if (!selectedAPIKey.equals(textKey.getText())) {
					selectedAPIKey = textKey.getText();
					updateComboProjects();
				}
			}
		});

		Image image = null;
		try {
			String imagePath = FileLocator.toFileURL(
					Platform.getBundle("es.cv.gvcase.ide.navigator") //$NON-NLS-1$
							.getResource("/icons/")).getPath(); //$NON-NLS-1$
			imagePath += "moskitt_logo.png"; //$NON-NLS-1$
			image = new Image(Display.getCurrent(), imagePath);
			ImageData id = image.getImageData();
			Rectangle bounds = image.getBounds();
			id = id.scaledTo(bounds.width / 2, bounds.height / 2);
			image = new Image(image.getDevice(), id);
		} catch (Exception e) {
		}
		ImageHyperlink imageHyperlink = toolkit.createImageHyperlink(
				mainComposite, SWT.CENTER);
		imageHyperlink.setImage(image);
		imageHyperlink.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				try {
					PlatformUI.getWorkbench().getBrowserSupport()
							.getExternalBrowser().openURL(new URL(redmineHost));
				} catch (PartInitException e1) {
				} catch (MalformedURLException e1) {
				}
			}
		});
		gd = new GridData(GridData.CENTER);
		imageHyperlink.setLayoutData(gd);

		// Composite for Issue fields
		Composite fieldsComposite = toolkit.createComposite(mainComposite);
		fieldsComposite.setLayout(new GridLayout(2, false));
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		fieldsComposite.setLayoutData(gd);

		Label labelProject = toolkit.createLabel(fieldsComposite, "Project:");
		gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		labelProject.setLayoutData(gd);

		comboProject = new Combo(fieldsComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
		gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		comboProject.setLayoutData(gd);
		comboProject.setVisibleItemCount(15);
		comboProject.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent event) {
				switch (event.keyCode) {
				case SWT.CR:
					comboType.setFocus();
				}
			}
		});

		Label labelIssueKind = toolkit.createLabel(fieldsComposite, "Type:");
		gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		labelIssueKind.setLayoutData(gd);

		comboType = new Combo(fieldsComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
		gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		comboType.setLayoutData(gd);
		comboType.setItems(getTypesLabels());
		comboType.select(0);
		comboType.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent event) {
				switch (event.keyCode) {
				case SWT.CR:
					textSubject.setFocus();
				}
			}
		});

		Label labelSummary = toolkit.createLabel(fieldsComposite, "Subject:");
		gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		labelSummary.setLayoutData(gd);

		textSubject = toolkit.createText(fieldsComposite, "", SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		textSubject.setLayoutData(gd);
		textSubject.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent event) {
				switch (event.keyCode) {
				case SWT.CR:
					textDescription.setFocus();
				}
			}
		});

		Label labelDescription = toolkit.createLabel(fieldsComposite,
				"Description:");
		gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING
				| GridData.VERTICAL_ALIGN_BEGINNING);
		labelDescription.setLayoutData(gd);

		textDescription = toolkit.createText(fieldsComposite, "", SWT.MULTI
				| SWT.BORDER);
		gd = new GridData(GridData.FILL_BOTH);
		textDescription.setLayoutData(gd);

		toolkit.adapt(parent);
	}

	private void selectAutentication() {
		String autentication = store
				.getString(MOSKittRedminePreferenceConstants.RedmineAutentication);

		if (autentication
				.equals(MOSKittRedminePreferenceConstants.RedmineAutenticationAnonymous)) {
			setAnonymous();
		} else if (autentication
				.equals(MOSKittRedminePreferenceConstants.RedmineAutenticationUser)) {
			setUser();
		}

		String key = store
				.getString(MOSKittRedminePreferenceConstants.APIAccessKeyUser);
		if (!key.equals("")) {
			textKey.setText(key);
		}
	}

	private void setAnonymous() {
		buttonUser.setSelection(false);
		textKey.setEnabled(false);
		buttonAnonymous.setSelection(true);

		store
				.setValue(
						MOSKittRedminePreferenceConstants.RedmineAutentication,
						MOSKittRedminePreferenceConstants.RedmineAutenticationAnonymous);

		selectedAPIKey = defaultAPIAccessKey;
		updateComboProjects();
	}

	private void setUser() {
		buttonUser.setSelection(true);
		textKey.setEnabled(true);
		buttonAnonymous.setSelection(false);

		store.setValue(MOSKittRedminePreferenceConstants.RedmineAutentication,
				MOSKittRedminePreferenceConstants.RedmineAutenticationUser);

		selectedAPIKey = store
				.getString(MOSKittRedminePreferenceConstants.APIAccessKeyUser);
		updateComboProjects();
	}

	private void updateComboProjects() {
		projects = null;
		comboProject.setItems(getProjectNames());
		int index = 0;
		for (String it : comboProject.getItems()) {
			if (it.equals(defaultProject)) {
				break;
			}
			index++;
		}
		comboProject.select(index);
	}

	@Override
	protected void okPressed() {
		String subject = textSubject.getText();
		if (subject.trim().equals("")) {
			showError("A subject should be specified.");
			return;
		}

		String description = textDescription.getText();
		if (description.trim().equals("")) {
			showError("A description should be specified.");
			return;
		}

		String apiAccessKey = "";
		if (buttonAnonymous.getSelection()) {
			apiAccessKey = defaultAPIAccessKey;
		} else {
			apiAccessKey = textKey.getText();
			if (apiAccessKey.equals("")) {
				showError("It's necessary to specify the User API access key.\n"
						+ "You can find it in your account page on MOSKitt Redmine");
				return;
			}
		}

		boolean res = MessageDialog.openConfirm(getShell(), "Create new Issue",
				"Are you sure you want to send this new Issue?");
		if (!res) {
			return;
		}

		// The manager for Redmine
		RedmineManager mgr = new RedmineManager(redmineHost, apiAccessKey);

		// Create the Tracker for the Issue (Bug or Feature Request)
		Integer idTracker = getTypeValue(comboType.getText());
		String nameTracker = getTypeLabel(idTracker);
		Tracker tracker = new Tracker(idTracker, nameTracker);

		// Create the Issue
		Issue issue = new Issue();
		issue.setTracker(tracker);
		issue.setSubject(textSubject.getText());
		issue.setDescription(textDescription.getText());
		String project = getProjectIdentifier(comboProject.getText());
		try {
			issue = mgr.createIssue(project, issue);
		} catch (IOException e) {
			showError(e.toString());
			return;
		} catch (AuthenticationException e) {
			showError(e.toString());
			return;
		} catch (NotFoundException e) {
			showError(e.toString());
			return;
		} catch (RedmineException e) {
			showError(e.toString());
			return;
		}

		if (issue != null) {
			showInformation("The new Issue has been created.");
		}
		super.okPressed();
	}

	private void showError(String error) {
		showError(getShell(), error);
	}

	private void showError(Shell shell, String error) {
		MessageDialog.openError(shell,
				"Error creating new Issue on MOSKitt Redmine", error);
	}

	private void showInformation(String info) {
		MessageDialog.openInformation(getShell(),
				"Creating new Issue on MOSKitt Redmine", info);
	}

	private void centerShell(Shell shell) {
		Monitor primary = shell.getDisplay().getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = shell.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		shell.setLocation(x, y);
	}

	private class IssueType {
		private String label;
		private Integer value;

		public IssueType(String label, int value) {
			this.label = label;
			this.value = new Integer(value);
		}

	}

	private List<IssueType> types;

	private List<IssueType> getTypes() {
		if (types == null) {
			types = new ArrayList<IssueType>();
			types.add(new IssueType("Bug", 1));
			types.add(new IssueType("Feature Request", 4));
		}
		return types;
	}

	public String[] getTypesLabels() {
		List<String> labels = new ArrayList<String>();

		for (IssueType it : getTypes()) {
			labels.add(it.label);
		}

		return labels.toArray(new String[0]);
	}

	private Integer getTypeValue(String label) {
		for (IssueType it : getTypes()) {
			if (it.label.equals(label)) {
				return it.value;
			}
		}

		return null;
	}

	private String getTypeLabel(Integer value) {
		for (IssueType it : getTypes()) {
			if (it.value.equals(value)) {
				return it.label;
			}
		}

		return null;
	}

	private class IssueProject {
		private String identifier;
		private String name;

		public IssueProject(String identifier, String name) {
			this.identifier = identifier;
			this.name = name;
		}
	}

	private List<IssueProject> projects;
	private String defaultProject = null;

	private List<IssueProject> getProjects() {
		if (projects == null) {
			final RedmineManager redmineConnection = new RedmineManager(
					redmineHost, selectedAPIKey);
			projects = new ArrayList<IssueProject>();

			IRunnableWithProgress run = new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					monitor
							.beginTask(
									"Getting available Projects on MOSKitt Redmine for selected user",
									50);
					Display display = new Display();
					Shell shell = new Shell(display);
					centerShell(shell);
					try {
						for (Project p : redmineConnection.getProjects()) {
							if (defaultProject == null
									&& p.getName().toLowerCase().equals(
											"moskitt")) {
								defaultProject = p.getName();
							}
							projects.add(new IssueProject(p.getIdentifier(), p
									.getName()));
						}
					} catch (IOException e) {
						showError(shell, e.toString());
					} catch (AuthenticationException e) {
						showError(shell, e.toString());
					} catch (RedmineException e) {
						showError(shell, e.toString());
					}
				}
			};
			try {
				ProgressMonitorDialog pmd = new ProgressMonitorDialog(
						getShell());
				pmd.run(true, true, run);
			} catch (InvocationTargetException e) {
				showError(getShell(), e.toString());
			} catch (InterruptedException e) {
				showError(getShell(), e.toString());
			}
		}

		Collections.sort(projects, new ProjectSorter());
		return projects;
	}

	private String[] getProjectNames() {
		List<String> labels = new ArrayList<String>();

		for (IssueProject ip : getProjects()) {
			labels.add(ip.name);
		}

		return labels.toArray(new String[0]);
	}

	private String getProjectIdentifier(String name) {
		for (IssueProject ip : getProjects()) {
			if (ip.name.equals(name)) {
				return ip.identifier;
			}
		}

		return null;
	}

	private class ProjectSorter implements Comparator<IssueProject> {

		public int compare(IssueProject o1, IssueProject o2) {
			return o1.name.toLowerCase().compareTo(o2.name.toLowerCase());
		}

	}

}
