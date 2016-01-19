/***************************************************************************
* Copyright (c) 2008 Conselleria de Infraestructuras y Transporte,
* Generalitat de la Comunitat Valenciana . All rights reserved. This program
* and the accompanying materials are made available under the terms of the
* Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors: Mario Cervera Ubeda (Prodevelop) - Initial API and implementation
* 			    Jose Manuel García Valladolid (CIT) - Maintenance support
*
**************************************************************************/
package es.cv.gvcase.fefem.common.widgets;

import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;

import com.onpositive.richtexteditor.viewer.RichTextViewer;
import com.onpositive.richtexteditor.viewer.RichTextViewerControlConfiguration;

import es.cv.gvcase.fefem.common.utils.ControlSelection;

public class StyledTextViewer extends RichTextViewer implements DisposeListener, FocusListener {
	
	public StyledTextViewer(Composite parent, int style) {
		
		super(parent, style);
	}
	
	@Override
	protected void createControl(Composite parent, int styles) {
		
		super.createControl(parent, styles);
		
		//getControl().setBackground(new Color(Display.getCurrent(), 220, 220, 220));
		this.getTextWidget().addFocusListener(this);
		this.getTextWidget().addDisposeListener(this);
	}
	
	@Override
	protected IContributionManager createToolbarManager() {
		ToolBar toolBar = new ToolBar((Composite)getControl(), SWT.HORIZONTAL | SWT.WRAP);
		toolBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return new ToolBarManager(toolBar);
	}

	@Override
	/**
	 * This method allows the user to change the RichTextViewerControlConfiguration of the
	 * StyledTextViewer which is used to configure its layer manager
	 * 
	 * @return the RichTextViewerControlConfiguration
	 */
	public RichTextViewerControlConfiguration getConfiguration() {
		return new RichTextViewerControlConfiguration();
	}

	public void widgetDisposed(DisposeEvent e) {
		if(!getControl().isDisposed()){
			getControl().removeFocusListener(this);
		}
		
	}

	public void focusGained(FocusEvent e) {
		//this.setSelection(new ControlSelection(this.getTextWidget()));
		
		// Force to create a text selection for enabling pasting text
		this.setSelection(new ITextSelection(){

			public int getEndLine() {
				return 0;
			}

			public int getLength() {
				return 0;
			}

			public int getOffset() {
				return 0;
			}

			public int getStartLine() {
				return 0;
			}

			public String getText() {
				return "";
			}

			public boolean isEmpty() {
				return false;
			}
			
		});
	}

	public void focusLost(FocusEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	
}
