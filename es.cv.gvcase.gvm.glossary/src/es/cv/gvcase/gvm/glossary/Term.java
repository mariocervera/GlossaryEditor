/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package es.cv.gvcase.gvm.glossary;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Term</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link es.cv.gvcase.gvm.glossary.Term#getName <em>Name</em>}</li>
 * </ul>
 * </p>
 *
 * @see es.cv.gvcase.gvm.glossary.GlossaryPackage#getTerm()
 * @model abstract="true"
 * @generated
 */
public interface Term extends EObject {

	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see es.cv.gvcase.gvm.glossary.GlossaryPackage#getTerm_Name()
	 * @model
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link es.cv.gvcase.gvm.glossary.Term#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);
} // Term
