package es.cv.gvcase.fefem.common.validation;

import org.eclipse.emf.common.util.Diagnostic;

/**
 * Interface defining methods for a class that process given Ecore Validation Diagnostics
 * @author Jose Manuel García Valladolid
 */
public interface IEcoreDiagnosticDispatcher {

	/**
	 * This method is called before a validation diagnostic for a EObject calculation is happened 
	 */
	public void prepareDiagnostic();
	
	
	/**
	 * This method is called after a validation diagnostic for a EObject is calculated and is delegated
	 * @param d
	 */
	public void dispatchDiagnostic(Diagnostic d);
}
