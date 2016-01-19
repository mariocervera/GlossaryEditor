/*******************************************************************************
 * Copyright (c) 2012 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: MArc Gil Sendra (Prodevelop) – Initial implementation
 *
 ******************************************************************************/
package es.cv.gvcase.ide.navigator.part;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JTextPane;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.SharedScrolledComposite;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;

public class MOSKittBrokenReferencesEditor extends EditorPart {

	private FormToolkit toolkit = new FormToolkit(Display.getCurrent());

	private ImageHyperlink imageHyperlink;
	private Label labelInfo;
	private Button buttonPrint;
	private Section sectionBR;
	private Text textBR;

	private File file;

	@Override
	public void createPartControl(Composite parent) {
		final SharedScrolledComposite scrollableComposite = toolkit
				.createPageBook(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		scrollableComposite.setExpandHorizontal(true);
		scrollableComposite.setExpandVertical(true);
		scrollableComposite.setMinWidth(0);
		scrollableComposite.setShowFocusedControl(true);

		final Composite contents = toolkit.createComposite(scrollableComposite);
		scrollableComposite.setContent(contents);

		GridLayout gl = new GridLayout();
		contents.setLayout(gl);

		Image image = null;
		try {
			String imagePath = FileLocator.toFileURL(
					Platform.getBundle("es.cv.gvcase.ide.navigator") //$NON-NLS-1$
							.getResource("/icons/")).getPath(); //$NON-NLS-1$
			imagePath += "moskitt_logo.png"; //$NON-NLS-1$
			image = new Image(Display.getCurrent(), imagePath);
		} catch (Exception e) {
		}

		imageHyperlink = toolkit.createImageHyperlink(contents, SWT.CENTER
				| SWT.WRAP);
		imageHyperlink.setImage(image);
		imageHyperlink.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				try {
					PlatformUI.getWorkbench().getBrowserSupport()
							.getExternalBrowser().openURL(
									new URL("http://www.moskitt.org"));
				} catch (PartInitException e1) {
				} catch (MalformedURLException e1) {
				}
			}
		});
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		imageHyperlink.setLayoutData(gd);

		IEditorInput editorInput = getEditorInput();
		String modelName = "";
		if (editorInput instanceof FileEditorInput) {
			FileEditorInput fei = (FileEditorInput) editorInput;
			modelName = fei.getFile().getFullPath().toString();
		} else {
			modelName = editorInput.getName();
		}

		String text = "Selected file \"" + modelName
				+ "\" has some broken references that have been removed.";
		labelInfo = toolkit.createLabel(contents, text, SWT.WRAP);
		gd = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		labelInfo.setLayoutData(gd);

		text = "Print Broken References information";
		buttonPrint = toolkit.createButton(contents, text, SWT.PUSH);
		gd = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		buttonPrint.setLayoutData(gd);
		buttonPrint.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				JTextPane jtp = new JTextPane();
				jtp.setBackground(Color.white);
				jtp.setText(textBR.getText());
				try {
					jtp.print(null, null, true, null, null, true);
				} catch (java.awt.print.PrinterException ex) {
					ex.printStackTrace();
				}
			}
		});

		sectionBR = toolkit.createSection(contents, Section.TWISTIE
				| Section.TITLE_BAR | Section.EXPANDED);
		sectionBR.setText("Show the Broken References");
		sectionBR.setLayoutData(new GridData(GridData.FILL_BOTH));
		gd = new GridData(GridData.FILL_HORIZONTAL);

		Composite sectionContent = toolkit.createComposite(sectionBR);
		sectionContent.setLayout(new GridLayout());
		gd = new GridData(GridData.FILL_HORIZONTAL);
		sectionContent.setLayoutData(gd);

		textBR = toolkit.createText(sectionContent, "", SWT.READ_ONLY
				| SWT.V_SCROLL | SWT.WRAP);
		textBR.setText("");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 200;
		textBR.setLayoutData(gd);

		sectionBR.setClient(sectionContent);

		toolkit.adapt(parent);
	}

	public void setBrokenReferencesFile(File file) {
		this.file = file;
		parseFile();
	}

	private void parseFile() {
		try {
			FileInputStream fis = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));

			String text = "";
			String line = "";
			while ((line = br.readLine()) != null) {
				text += line + "\n";
			}

			textBR.setText(text);

			fis.close();
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
	}

	@Override
	public void doSaveAs() {
	}

	@Override
	public void setFocus() {
		if (imageHyperlink != null) {
			imageHyperlink.setFocus();
		}
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

}
