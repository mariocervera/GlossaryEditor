package es.cv.gvcase.mdt.common.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class EObjectsResourcesContentProvider  extends ArrayContentProvider implements ITreeContentProvider{

	ITreeContentProvider wrappedProvider;
	
	Object [] input = null;
	
	public EObjectsResourcesContentProvider(ITreeContentProvider provider){
		this.wrappedProvider = provider;
	}

	public Object[] getElements(Object inputElement) {
		
		HashMap<Resource,Resource> resourcesList = new HashMap<Resource,Resource>();
		
		Object[] inputArray = null;
		
		if (inputElement instanceof Object[]){
			inputArray = (Object[])inputElement;
		} else if (inputElement instanceof Object){
			inputArray = new Object[] {inputElement};
		} else {
			return null;
		}
		
		for (Object o : inputArray){
			if (o instanceof EObject){
				Resource r = ((EObject)o).eResource();
				if (! resourcesList.containsKey(r)){
					resourcesList.put(r, r);
				}
				
			}
		}
		
		
		return resourcesList.keySet().toArray();
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput instanceof Object[]){
			this.input = (Object[])newInput;
		} else if (newInput instanceof Object){
			this.input = new Object[] {newInput};
		} 
		this.wrappedProvider.inputChanged(viewer, oldInput, newInput);
	}

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof Resource){
			List<Object> children = new ArrayList<Object>();
			for (Object o : this.input){
				if (o instanceof EObject){
					if ( ((EObject)o).eResource().equals(parentElement) ){
						children.add(o);
					}
				}
			}
			return this.wrappedProvider.getElements(children.toArray());
//			return children.toArray();
		}
		return this.wrappedProvider.getChildren(parentElement);
	}

	public Object getParent(Object element) {
		if (element instanceof Resource){
			return null;
		}
		return this.wrappedProvider.getParent(element);
	}

	public boolean hasChildren(Object element) {
		if (element instanceof Resource){
			return true;
		}
		return this.wrappedProvider.hasChildren(element);
	}

	public void dispose() {
		// TODO Auto-generated method stub
		
	}
	
	
}
