package es.cv.gvcase.ide.navigator.provider;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;

import es.cv.gvcase.ide.navigator.Activator;
import es.cv.gvcase.ide.navigator.treeviewers.filter.InNoDiagramViewerFilter;

public class NoViewLabelDecorator implements ILightweightLabelDecorator {

	private ImageDescriptor ovrDescriptor = Activator.getImagedescriptor("/icons/ovr/warning_co.gif");

	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	public void decorate(Object element, IDecoration decoration) {
		if (element instanceof EObject && 
				!(element instanceof View) &&
				InNoDiagramViewerFilter.elementWithoutView((EObject)element)){
		
			decoration.addOverlay(ovrDescriptor);
			
		}
	}

	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
		
	}

	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}
