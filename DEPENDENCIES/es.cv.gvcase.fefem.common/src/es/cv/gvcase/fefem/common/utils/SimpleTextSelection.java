/**
 * 
 */
package es.cv.gvcase.fefem.common.utils;

import org.eclipse.jface.text.ITextSelection;

/**
 * @author jm
 *
 */
public class SimpleTextSelection implements ITextSelection {

	protected String sel;
	
	
	
	public SimpleTextSelection(String sel) {
		super();
		this.sel = sel;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.ITextSelection#getEndLine()
	 */
	public int getEndLine() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.ITextSelection#getLength()
	 */
	public int getLength() {
		if(sel != null)
			return sel.length();
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.ITextSelection#getOffset()
	 */
	public int getOffset() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.ITextSelection#getStartLine()
	 */
	public int getStartLine() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.ITextSelection#getText()
	 */
	public String getText() {
		return sel;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelection#isEmpty()
	 */
	public boolean isEmpty() {
		return ((sel == null)||(sel.length() == 0));
	}

	@Override
	public String toString() {
		return sel;
	}

	
}
