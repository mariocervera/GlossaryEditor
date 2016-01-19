/**
 * 
 */
package es.cv.gvcase.fefem.common.utils;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Control;

/**
 * @author Jose Manuel García Valladolid
 *
 */
public class ControlSelection implements ISelection{

	protected Control control;
	
	
	public ControlSelection(Control control) {
		super();
		this.control = control;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelection#isEmpty()
	 */
	public boolean isEmpty() {
		return (control == null);
	}

	public Control getControl() {
		return control;
	}

	@Override
	public String toString() {
		return "ControlSelection [control=" + control + "]";
	}

	

	

}
