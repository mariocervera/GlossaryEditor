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
package es.cv.gvcase.fefem.common.utils;

import java.util.Vector;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.swt.custom.ExtendedStyledText;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

/**
 * This handler executes Cut, Copy and Paste commands for SWT StyledText widgets and 
 * OnPositive ExtendedStyledText widget
 * 
 * @author Jose Manuel García Valladolid
 *
 */
public class StyledTextCCPHandler implements IHandler {

	protected Vector<IHandlerListener> listeners = new Vector<IHandlerListener>();
	protected Control selectedControl;
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#addHandlerListener(org.eclipse.core.commands.IHandlerListener)
	 */
	public void addHandlerListener(IHandlerListener handlerListener) {
		if(!listeners.contains(handlerListener)){
			listeners.add(handlerListener);
		}


	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#dispose()
	 */
	public void dispose() {
		// TODO Auto-generated method stub

	}

	protected void doCopy(){
		if(this.selectedControl instanceof StyledText)
			((StyledText) selectedControl).copy();
		else
			if(this.selectedControl instanceof ExtendedStyledText)
				((ExtendedStyledText) selectedControl).copy();
	}
	
	protected void doCut(){
		if(this.selectedControl instanceof StyledText)
			((StyledText) selectedControl).cut();
		else
			if(this.selectedControl instanceof ExtendedStyledText)
				((ExtendedStyledText) selectedControl).cut();
	}
	
	protected void doPaste(){
		if(this.selectedControl instanceof StyledText)
			((StyledText) selectedControl).paste();
		else
			if(this.selectedControl instanceof ExtendedStyledText)
				((ExtendedStyledText) selectedControl).paste();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Object appcontext = event.getApplicationContext();
		if((appcontext != null)&&(appcontext instanceof EvaluationContext)){
			Object selection = ((EvaluationContext) appcontext).getVariable("selection");
			if(selection != null){
				if((selection instanceof ITextSelection)||(selection instanceof ControlSelection)){
					if(event.getCommand().getId().equals("org.eclipse.ui.edit.copy"))
						doCopy();
					if(event.getCommand().getId().equals("org.eclipse.ui.edit.cut"))
						doCut();
					if(event.getCommand().getId().equals("org.eclipse.ui.edit.paste"))
						doPaste();
				}
			}
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#isEnabled()
	 */
	public boolean isEnabled() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#isHandled()
	 */
	public boolean isHandled() {
		selectedControl = Display.getCurrent().getFocusControl();
		return ((selectedControl !=null) && ((selectedControl instanceof StyledText) || (selectedControl instanceof ExtendedStyledText)));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#removeHandlerListener(org.eclipse.core.commands.IHandlerListener)
	 */
	public void removeHandlerListener(IHandlerListener handlerListener) {
		if(listeners.contains(handlerListener)){
			listeners.remove(handlerListener);
		}


	}

}
