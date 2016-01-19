/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package es.cv.gvcase.gvm.glossary;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see es.cv.gvcase.gvm.glossary.GlossaryPackage
 * @generated
 */
public interface GlossaryFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	GlossaryFactory eINSTANCE = es.cv.gvcase.gvm.glossary.impl.GlossaryFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Glossary</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Glossary</em>'.
	 * @generated
	 */
	Glossary createGlossary();

	/**
	 * Returns a new object of class '<em>Local Term</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Local Term</em>'.
	 * @generated
	 */
	LocalTerm createLocalTerm();

	/**
	 * Returns a new object of class '<em>Referenced Term</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Referenced Term</em>'.
	 * @generated
	 */
	ReferencedTerm createReferencedTerm();

	/**
	 * Returns a new object of class '<em>Redefined Term</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Redefined Term</em>'.
	 * @generated
	 */
	RedefinedTerm createRedefinedTerm();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	GlossaryPackage getGlossaryPackage();

} //GlossaryFactory
