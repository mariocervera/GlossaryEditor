/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package es.cv.gvcase.gvm.glossary;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Local Term</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link es.cv.gvcase.gvm.glossary.LocalTerm#getDefinition <em>Definition</em>}</li>
 * </ul>
 * </p>
 *
 * @see es.cv.gvcase.gvm.glossary.GlossaryPackage#getLocalTerm()
 * @model
 * @generated
 */
public interface LocalTerm extends Term {
	/**
	 * Returns the value of the '<em><b>Definition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Definition</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Definition</em>' attribute.
	 * @see #setDefinition(String)
	 * @see es.cv.gvcase.gvm.glossary.GlossaryPackage#getLocalTerm_Definition()
	 * @model
	 * @generated
	 */
	String getDefinition();

	/**
	 * Sets the value of the '{@link es.cv.gvcase.gvm.glossary.LocalTerm#getDefinition <em>Definition</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Definition</em>' attribute.
	 * @see #getDefinition()
	 * @generated
	 */
	void setDefinition(String value);

} // LocalTerm
