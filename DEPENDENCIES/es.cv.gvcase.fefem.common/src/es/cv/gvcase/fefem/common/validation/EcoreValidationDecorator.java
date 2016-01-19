package es.cv.gvcase.fefem.common.validation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.editor.IFormPage;

import es.cv.gvcase.fefem.common.FEFEMEditor;
import es.cv.gvcase.fefem.common.composites.EMFPropertyComposite;

/**
 * This class creates is a IEcoreDiagnosticDispatcher with the mission of creating decorators to FEFEM composite controls in order to show Ecore validation diagnostic results
 * by inspecting EMFPropertyComposite features, its values and the Diagnostic results of validation 
 *  
 * @author Jose Manuel García Valladolid
 * 
 */
public class EcoreValidationDecorator implements IEcoreDiagnosticDispatcher{

	protected FEFEMEditor editor;
	protected Map<Control, ControlDecoration> decorators = new HashMap<Control, ControlDecoration>();
	
	protected Vector<EMFPropertyComposite> ecoreValidationComposites = new Vector<EMFPropertyComposite>();
	
	public EcoreValidationDecorator(FEFEMEditor editor) {
		super();
		this.editor = editor;
	}
	
	
	public void hideDecorators(){
		
		Iterator<Control> decoKeys = decorators.keySet().iterator();
		while(decoKeys.hasNext()){
			ControlDecoration cd = decorators.get(decoKeys.next());
			cd.hide();
		}
		
	}
	
	public void showDecorators(){
		
		Iterator<Control> decoKeys = decorators.keySet().iterator();
		while(decoKeys.hasNext()){
			ControlDecoration cd = decorators.get(decoKeys.next());
			cd.show();
		}
		
	}
	
	public void dispatchDiagnostic(Diagnostic d){
		
		EObject eobject = null;
		EStructuralFeature feature = null;
		IFormPage activePage = this.editor.getActivePageInstance();
		
		// 1. Obtain validated feature from diagnostic
		List<?> diagnosticData = d.getData();
		// Accoding to Diagnostic.getData() Javadoc description, the first element is typically the EObject containing the problem and the second element the feature
		if(diagnosticData.size()>=2){
			if(diagnosticData.get(0) instanceof EObject)
				eobject = (EObject) diagnosticData.get(0);
			if(diagnosticData.get(1) instanceof EStructuralFeature)
				feature = (EStructuralFeature) diagnosticData.get(1);
		}	
		
		if(feature != null){
			
			// 2. Search for a registered EMFPropertyComposite that represents the feature
			Iterator<EMFPropertyComposite> iter = getRegisteredComposites().iterator();
			while(iter.hasNext()){
				EMFPropertyComposite cmp = iter.next();
				if((cmp.getEStructuralFeature() != null)&&
				    (cmp.getEObject() != null)&&
				   (cmp.getEStructuralFeature().equals(feature))&&
				   (cmp.getPage().equals(activePage))&&
				   (cmp.getEObject().equals(eobject))){
					
					Control c = cmp.getRepresentativeControl();
					
					ControlDecoration controlDecoration = null;
					if(decorators.containsKey(c))
						controlDecoration = decorators.get(c);
					else{
						controlDecoration = new ControlDecoration(c, SWT.LEFT | SWT.TOP);
						decorators.put(c, controlDecoration);
						c.addDisposeListener(new DisposeListener(){

							public void widgetDisposed(DisposeEvent e) {
								ControlDecoration cd = decorators.get(e.getSource());
								if(cd!=null){
									decorators.remove(cd);
								}
								
							}
							
						});
					}
						
						controlDecoration.setDescriptionText(adaptDiagnosticMessage(d.getMessage()));
						FieldDecoration fieldDecoration = null;
						controlDecoration.hide();
						
						switch (d.getSeverity())
				        {
				          case Diagnostic.ERROR: fieldDecoration = FieldDecorationRegistry.getDefault().getFieldDecoration( FieldDecorationRegistry.DEC_ERROR);
				          break;
				          case Diagnostic.WARNING: fieldDecoration = FieldDecorationRegistry.getDefault().getFieldDecoration( FieldDecorationRegistry.DEC_WARNING);
				          break;
				        }
						
						controlDecoration.setImage(fieldDecoration.getImage());
						if(d.getSeverity() > Diagnostic.OK)
							controlDecoration.show();
									
						cmp.redraw();
		
					
				}
				
			}
		}
		
		
	}
	
	protected String adaptDiagnosticMessage(String msg){
		String[] parts = msg.split("'");
		if(parts.length >=4) {
			String msgToReduce = parts[3];
			
			// Get the EClass part, not the full uri
			String[] parts2 = msgToReduce.split("@");
			if(parts2.length>=1){
				
				// Remove package part
				String[] parts3 = parts2[0].split("\\.");
				String eclassName = parts3[parts3.length-1];
				
				String r = "";
				for(int i=0;i<parts.length;i++){
					if(i == 3)
						r = r + eclassName+"'";
					else
						r = r + parts[i]+"'";
				}
				return r;
			}else
				return msg;
		}else
			return msg;
		
	}

	public void registerComposite(EMFPropertyComposite cmp){
		if(!ecoreValidationComposites.contains(cmp)){
			ecoreValidationComposites.add(cmp);
			
			// Automatic deregistering when dispose composite
			cmp.addDisposeListener(new DisposeListener(){

				public void widgetDisposed(DisposeEvent e) {
					unregisterComposite((EMFPropertyComposite) e.getSource());
					
				}
				
			});
		}	
			
	}
	
	public void unregisterComposite(EMFPropertyComposite cmp){
		if(ecoreValidationComposites.contains(cmp)){
			ecoreValidationComposites.remove(cmp);
		}
	}
	
	
	public Vector<EMFPropertyComposite> getRegisteredComposites() {
		return ecoreValidationComposites;
	}


	public void prepareDiagnostic() {
		
		// reset validation status by hidding any existings decorators
		hideDecorators();

	}

	
}
