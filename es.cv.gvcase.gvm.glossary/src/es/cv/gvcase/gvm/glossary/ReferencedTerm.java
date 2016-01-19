/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package es.cv.gvcase.gvm.glossary;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Referenced Term</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link es.cv.gvcase.gvm.glossary.ReferencedTerm#getSource <em>Source</em>}</li>
 * </ul>
 * </p>
 *
 * @see es.cv.gvcase.gvm.glossary.GlossaryPackage#getReferencedTerm()
 * @model
 * @generated
 */
public interface ReferencedTerm extends Term {
	/**
	 * Returns the value of the '<em><b>Source</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Source</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Source</em>' reference.
	 * @see #setSource(Term)
	 * @see es.cv.gvcase.gvm.glossary.GlossaryPackage#getReferencedTerm_Source()
	 * @model required="true"
	 * @generated
	 */
	Term getSource();

	/**
	 * Sets the value of the '{@link es.cv.gvcase.gvm.glossary.ReferencedTerm#getSource <em>Source</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Source</em>' reference.
	 * @see #getSource()
	 * @generated
	 */
	void setSource(Term value);

} // ReferencedTerm
