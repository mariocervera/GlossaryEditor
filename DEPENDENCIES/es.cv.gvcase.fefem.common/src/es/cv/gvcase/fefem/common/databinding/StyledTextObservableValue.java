/***************************************************************************
* Copyright (c) 2008 Conselleria de Infraestructuras y Transporte,
* Generalitat de la Comunitat Valenciana . All rights reserved. This program
* and the accompanying materials are made available under the terms of the
* Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors: Mario Cervera Ubeda (Prodevelop) - Initial API and implementation
*
**************************************************************************/
package es.cv.gvcase.fefem.common.databinding;

import org.eclipse.core.databinding.observable.value.AbstractVetoableValue;
import org.eclipse.core.databinding.observable.value.ValueDiff;
import org.eclipse.jface.text.DocumentEvent;

import com.onpositive.richtexteditor.viewer.IRichDocumentListener;
import com.onpositive.richtexteditor.viewer.undo.RichDocumentChange;

import es.cv.gvcase.fefem.common.widgets.StyledTextViewer;

public class StyledTextObservableValue extends AbstractVetoableValue  {

	private StyledTextViewer styledTextViewer;
	
	private String oldValue;
	
	public StyledTextObservableValue(StyledTextViewer textViewer) {
		
		this.styledTextViewer = textViewer;
		if(textViewer != null) {
			this.oldValue = this.styledTextViewer.getLayerManager().getSerializedString();
			this.styledTextViewer.addRichDocumentListener(new IRichDocumentListener(){
	
				public void documentAboutToBeChanged(DocumentEvent event) {
					
				}
	
				public void documentChanged(DocumentEvent event,
						RichDocumentChange change) {
					
					fireValueChange(new ValueDiff() {
						public Object getOldValue() {
							return oldValue;
						}
	
						public Object getNewValue() {
							return getValue();
						}
					});
					if(styledTextViewer != null && 
							!styledTextViewer.getControl().isDisposed() &&
							!styledTextViewer.getLayerManager().getSerializedString().equals(oldValue)) {
						oldValue = styledTextViewer.getLayerManager().getSerializedString();
					}
					
				}
				
			});
		}

	}
	
	
	@Override
	protected void doSetApprovedValue(Object value) {
		
		if(this.styledTextViewer != null &&
				!styledTextViewer.getControl().isDisposed()) {
			this.styledTextViewer.getTextWidget().setText("");
			this.styledTextViewer.getLayerManager().pasteHTML(value.toString(), 0);
			oldValue = this.styledTextViewer.getLayerManager().getSerializedString();
		}
	}

	@Override
	protected Object doGetValue() {
		if(this.styledTextViewer != null &&
				!styledTextViewer.getControl().isDisposed()) {
			return styledTextViewer.getLayerManager().getSerializedString();
		}
		return "";
	}

	public Object getValueType() {
		return String.class;
	}
	
}
