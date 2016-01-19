/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package es.cv.gvcase.gvm.glossary;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Glossary</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link es.cv.gvcase.gvm.glossary.Glossary#getContainedTerms <em>Contained Terms</em>}</li>
 *   <li>{@link es.cv.gvcase.gvm.glossary.Glossary#getName <em>Name</em>}</li>
 * </ul>
 * </p>
 *
 * @see es.cv.gvcase.gvm.glossary.GlossaryPackage#getGlossary()
 * @model
 * @generated
 */
public interface Glossary extends EObject {
	/**
	 * Returns the value of the '<em><b>Contained Terms</b></em>' containment reference list.
	 * The list contents are of type {@link es.cv.gvcase.gvm.glossary.Term}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Contained Terms</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Contained Terms</em>' containment reference list.
	 * @see es.cv.gvcase.gvm.glossary.GlossaryPackage#getGlossary_ContainedTerms()
	 * @model containment="true"
	 * @generated
	 */
	EList<Term> getContainedTerms();

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
	 * @see es.cv.gvcase.gvm.glossary.GlossaryPackage#getGlossary_Name()
	 * @model
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link es.cv.gvcase.gvm.glossary.Glossary#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

} // Glossary
