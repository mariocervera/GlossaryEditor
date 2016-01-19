package es.cv.gvcase.mdt.common.provider;

import org.eclipse.emf.ecore.EObject;

public interface INodeDescriptor {

	EObject getModelElement();
	
	int getVisualID();
	
	String getType();
	
}
