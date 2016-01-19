package es.cv.gvcase.ide.navigator.dnd;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.ui.navigator.CommonDropAdapter;
import org.eclipse.ui.navigator.CommonDropAdapterAssistant;
import org.eclipse.ui.views.navigator.LocalSelectionTransfer;

public class NavigatorDropAdapterAssistant extends CommonDropAdapterAssistant {

	@Override
	public IStatus handleDrop(CommonDropAdapter dropAdapter,
			DropTargetEvent dropTargetEvent, Object target) {
		
		for (TransferData data : dropTargetEvent.dataTypes){
			if (LocalSelectionTransfer.getInstance().isSupportedType(data)){
				
				if (! (target instanceof EObject)){
					continue;
				}
				
				
				List<EObject> objectsToMove = new ArrayList<EObject>();
				if (LocalSelectionTransfer.getInstance().nativeToJava(data) instanceof TreeSelection){
					TreeSelection selec = (TreeSelection) LocalSelectionTransfer.getInstance().nativeToJava(data);
					for (Iterator iter = selec.iterator(); iter.hasNext();){
						Object element = iter.next();
						if (element instanceof EObject){
								EObject sourceEObject = (EObject) element;
								if (canContain((EObject)target, sourceEObject)){
									objectsToMove.add(sourceEObject);									
								} else	if (sourceEObject.eClass().isSuperTypeOf(((EObject)target).eClass())){ // Compatible classes
									// do nothing
								}
							
						}
					}				
				}
				if (objectsToMove.size()>0){
					moveEObjectCollection((EObject) target,objectsToMove);
				}
			}	
		}
		
		return Status.CANCEL_STATUS;
	}

	private void moveEObjectCollection(EObject target, List<EObject> sourceEObjects) {
		
			if (sourceEObjects.size() == 0){
				return;
			}
		
			TransactionalEditingDomain editingDomain = TransactionUtil.getEditingDomain(target);
			editingDomain.getCommandStack().execute(
					AddCommand.create(editingDomain,
							target, 
							sourceEObjects.get(0).eContainmentFeature(), 
							sourceEObjects)
							);
				
	}

	@Override
	public IStatus validateDrop(Object target, int operation,
			TransferData transferType) {

		if (LocalSelectionTransfer.getInstance().isSupportedType(transferType)){
			
			if (! (target instanceof EObject)){
				return Status.CANCEL_STATUS;
			}
			
			if (LocalSelectionTransfer.getInstance().nativeToJava(transferType) instanceof TreeSelection){
				TreeSelection selec = (TreeSelection) LocalSelectionTransfer.getInstance().nativeToJava(transferType);
				for (Iterator iter = selec.iterator(); iter.hasNext();){
					Object element = iter.next();
					if (element instanceof EObject){
							EObject sourceEObject = (EObject) element;
							if (canContain((EObject)target, sourceEObject)){
								return Status.OK_STATUS;
							}
							if (sourceEObject.eClass().isSuperTypeOf(((EObject)target).eClass())){ // Compatible classes
								return Status.OK_STATUS;
							}
						
					}
				}				
			}			
		}
		return Status.CANCEL_STATUS;
	}
	
	private boolean canContain(EObject container, EObject candidate){
		 return container.eClass().eGet(candidate.eContainingFeature())!= null;
	}

}
