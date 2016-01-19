package es.cv.gvcase.mdt.common.provider;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.emf.ecore.EObject;

public interface ILinkDescriptor extends INodeDescriptor {

	EObject getSource();

	EObject getDestination();

	IAdaptable getSemanticAdapter();

}
