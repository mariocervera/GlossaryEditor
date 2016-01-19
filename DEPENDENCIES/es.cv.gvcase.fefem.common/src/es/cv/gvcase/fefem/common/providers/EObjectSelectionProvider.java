package es.cv.gvcase.fefem.common.providers;


import java.util.Vector;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;

/**
 * This selection provider acts as an intermediate selection provider for each Viewer of each FEFEMComposite
 * in order to propagate several viewer selections (typically coming from StructureViewers and TextViewers)  to the Workbench
 * 
 * @author Jose Manuel García Valladolid
 */
public class EObjectSelectionProvider implements ISelectionProvider {

	private final ListenerList selectionListeners = new ListenerList();
		
	private final Vector<ISelectionProvider> registeredSelectionProviders = new Vector<ISelectionProvider>();
	private ISelectionProvider delegate;
	
	private ISelectionChangedListener selectionListener = new ISelectionChangedListener() {
		public void selectionChanged(SelectionChangedEvent event) {
			if (registeredSelectionProviders.contains(event.getSelectionProvider())) {
				delegate = event.getSelectionProvider();
				fireSelectionChanged(event.getSelection());
			}
		}


	};

		
	protected void fireSelectionChanged(ISelection selection) {
		fireSelectionChanged(selectionListeners, selection);
	}

	
	private void fireSelectionChanged(ListenerList list, ISelection selection) {
		SelectionChangedEvent event = new SelectionChangedEvent(delegate, selection);
		Object[] listeners = list.getListeners();
		for (int i = 0; i < listeners.length; i++) {
			ISelectionChangedListener listener = (ISelectionChangedListener) listeners[i];
			listener.selectionChanged(event);
		}
	}
	
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		selectionListeners.add(listener);

	}

	public ISelection getSelection() {
		return delegate == null ? null : delegate.getSelection();
	}

	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		selectionListeners.remove(listener);

	}

	public void setSelection(ISelection selection) {
		if (delegate != null) {
			delegate.setSelection(selection);
		}
	}

	public void registerDelegatedSelectionProvider(ISelectionProvider v){
		if(!registeredSelectionProviders.contains(v)){
			registeredSelectionProviders.add(v);
			v.addSelectionChangedListener(selectionListener);
		}
	}
	
	public void unregisterDelegatedSelectionProvider(ISelectionProvider v){
		if(registeredSelectionProviders.contains(v)){
			v.removeSelectionChangedListener(selectionListener);
			registeredSelectionProviders.remove(v);
		}
	}
	
	
}
