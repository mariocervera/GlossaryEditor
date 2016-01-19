/***************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte,
 * Generalitat de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Jose Manuel García Valladolid (CIT) - Initial API and implementation
 *
 **************************************************************************/
package es.cv.gvcase.fefem.common.providers;

import java.util.Iterator;
import java.util.Vector;

import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import es.cv.gvcase.fefem.common.utils.ControlSelection;
import es.cv.gvcase.fefem.common.utils.SimpleTextSelection;

/**
 * @author Jose Manuel García Valladolid
 *
 */
public class StyledTextSelectionProvider implements ISelectionProvider, Listener, DisposeListener, FocusListener {

	protected StyledText widget;
	
	protected Vector<ISelectionChangedListener> listeners = new Vector<ISelectionChangedListener>();
	
	protected ISelection currentSelection;
	
	/**
	 * 
	 */
	public StyledTextSelectionProvider(StyledText widget) {
		super();
		
		this.widget = widget;
		widget.addListener(SWT.Selection, this);
		widget.addDisposeListener(this);
		widget.addFocusListener(this);
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		if(!listeners.contains(listener)){
			listeners.add(listener);
		}

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
	 */
	public ISelection getSelection() {
		return currentSelection;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		
		if(listeners.contains(listener)){
			listeners.remove(listener);
		}

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
	 */
	public void setSelection(ISelection selection) {
		if((widget != null)&&(selection instanceof ITextSelection)){
			String s = widget.getText();
			s.replace(widget.getSelectionText(), ((ITextSelection) selection).getText());
			widget.setText(s);
		}
			

	}

	public void handleEvent(Event event) {
		
		Iterator<ISelectionChangedListener> i = listeners.iterator();
		while(i.hasNext()){
			ISelectionChangedListener l = i.next();
			currentSelection = new SimpleTextSelection(widget.getSelectionText());
			SelectionChangedEvent e = new SelectionChangedEvent(this, currentSelection);
			l.selectionChanged(e);
		}
	}

	public void widgetDisposed(DisposeEvent e) {
		if(!widget.isDisposed()){
			widget.removeListener(SWT.Selection, this);
			widget.removeFocusListener(this);
		}
		
	}

	public void focusGained(FocusEvent e) {
		Iterator<ISelectionChangedListener> i = listeners.iterator();
		while(i.hasNext()){
			ISelectionChangedListener l = i.next();
			currentSelection = new ControlSelection(widget);
			SelectionChangedEvent ev = new SelectionChangedEvent(this, currentSelection);
			l.selectionChanged(ev);
		}
		
	}

	public void focusLost(FocusEvent e) {
		// TODO Auto-generated method stub
		
	}

	


}
