/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package es.cv.gvcase.gvm.glossary;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see es.cv.gvcase.gvm.glossary.GlossaryFactory
 * @model kind="package"
 * @generated
 */
public interface GlossaryPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "glossary";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://es/cv/gvcase/glossary";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "glossary";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	GlossaryPackage eINSTANCE = es.cv.gvcase.gvm.glossary.impl.GlossaryPackageImpl.init();

	/**
	 * The meta object id for the '{@link es.cv.gvcase.gvm.glossary.impl.GlossaryImpl <em>Glossary</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see es.cv.gvcase.gvm.glossary.impl.GlossaryImpl
	 * @see es.cv.gvcase.gvm.glossary.impl.GlossaryPackageImpl#getGlossary()
	 * @generated
	 */
	int GLOSSARY = 0;

	/**
	 * The feature id for the '<em><b>Contained Terms</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GLOSSARY__CONTAINED_TERMS = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GLOSSARY__NAME = 1;

	/**
	 * The number of structural features of the '<em>Glossary</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GLOSSARY_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link es.cv.gvcase.gvm.glossary.impl.TermImpl <em>Term</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see es.cv.gvcase.gvm.glossary.impl.TermImpl
	 * @see es.cv.gvcase.gvm.glossary.impl.GlossaryPackageImpl#getTerm()
	 * @generated
	 */
	int TERM = 1;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TERM__NAME = 0;

	/**
	 * The number of structural features of the '<em>Term</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TERM_FEATURE_COUNT = 1;

	/**
	 * The meta object id for the '{@link es.cv.gvcase.gvm.glossary.impl.LocalTermImpl <em>Local Term</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see es.cv.gvcase.gvm.glossary.impl.LocalTermImpl
	 * @see es.cv.gvcase.gvm.glossary.impl.GlossaryPackageImpl#getLocalTerm()
	 * @generated
	 */
	int LOCAL_TERM = 2;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_TERM__NAME = TERM__NAME;

	/**
	 * The feature id for the '<em><b>Definition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_TERM__DEFINITION = TERM_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Local Term</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_TERM_FEATURE_COUNT = TERM_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link es.cv.gvcase.gvm.glossary.impl.ReferencedTermImpl <em>Referenced Term</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see es.cv.gvcase.gvm.glossary.impl.ReferencedTermImpl
	 * @see es.cv.gvcase.gvm.glossary.impl.GlossaryPackageImpl#getReferencedTerm()
	 * @generated
	 */
	int REFERENCED_TERM = 3;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCED_TERM__NAME = TERM__NAME;

	/**
	 * The feature id for the '<em><b>Source</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCED_TERM__SOURCE = TERM_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Referenced Term</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCED_TERM_FEATURE_COUNT = TERM_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link es.cv.gvcase.gvm.glossary.impl.RedefinedTermImpl <em>Redefined Term</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see es.cv.gvcase.gvm.glossary.impl.RedefinedTermImpl
	 * @see es.cv.gvcase.gvm.glossary.impl.GlossaryPackageImpl#getRedefinedTerm()
	 * @generated
	 */
	int REDEFINED_TERM = 4;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REDEFINED_TERM__NAME = LOCAL_TERM__NAME;

	/**
	 * The feature id for the '<em><b>Definition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REDEFINED_TERM__DEFINITION = LOCAL_TERM__DEFINITION;

	/**
	 * The feature id for the '<em><b>Source</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REDEFINED_TERM__SOURCE = LOCAL_TERM_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Redefined Term</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REDEFINED_TERM_FEATURE_COUNT = LOCAL_TERM_FEATURE_COUNT + 1;


	/**
	 * Returns the meta object for class '{@link es.cv.gvcase.gvm.glossary.Glossary <em>Glossary</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Glossary</em>'.
	 * @see es.cv.gvcase.gvm.glossary.Glossary
	 * @generated
	 */
	EClass getGlossary();

	/**
	 * Returns the meta object for the containment reference list '{@link es.cv.gvcase.gvm.glossary.Glossary#getContainedTerms <em>Contained Terms</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Contained Terms</em>'.
	 * @see es.cv.gvcase.gvm.glossary.Glossary#getContainedTerms()
	 * @see #getGlossary()
	 * @generated
	 */
	EReference getGlossary_ContainedTerms();

	/**
	 * Returns the meta object for the attribute '{@link es.cv.gvcase.gvm.glossary.Glossary#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see es.cv.gvcase.gvm.glossary.Glossary#getName()
	 * @see #getGlossary()
	 * @generated
	 */
	EAttribute getGlossary_Name();

	/**
	 * Returns the meta object for class '{@link es.cv.gvcase.gvm.glossary.Term <em>Term</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Term</em>'.
	 * @see es.cv.gvcase.gvm.glossary.Term
	 * @generated
	 */
	EClass getTerm();

	/**
	 * Returns the meta object for the attribute '{@link es.cv.gvcase.gvm.glossary.Term#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see es.cv.gvcase.gvm.glossary.Term#getName()
	 * @see #getTerm()
	 * @generated
	 */
	EAttribute getTerm_Name();

	/**
	 * Returns the meta object for class '{@link es.cv.gvcase.gvm.glossary.LocalTerm <em>Local Term</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Local Term</em>'.
	 * @see es.cv.gvcase.gvm.glossary.LocalTerm
	 * @generated
	 */
	EClass getLocalTerm();

	/**
	 * Returns the meta object for the attribute '{@link es.cv.gvcase.gvm.glossary.LocalTerm#getDefinition <em>Definition</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Definition</em>'.
	 * @see es.cv.gvcase.gvm.glossary.LocalTerm#getDefinition()
	 * @see #getLocalTerm()
	 * @generated
	 */
	EAttribute getLocalTerm_Definition();

	/**
	 * Returns the meta object for class '{@link es.cv.gvcase.gvm.glossary.ReferencedTerm <em>Referenced Term</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Referenced Term</em>'.
	 * @see es.cv.gvcase.gvm.glossary.ReferencedTerm
	 * @generated
	 */
	EClass getReferencedTerm();

	/**
	 * Returns the meta object for the reference '{@link es.cv.gvcase.gvm.glossary.ReferencedTerm#getSource <em>Source</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Source</em>'.
	 * @see es.cv.gvcase.gvm.glossary.ReferencedTerm#getSource()
	 * @see #getReferencedTerm()
	 * @generated
	 */
	EReference getReferencedTerm_Source();

	/**
	 * Returns the meta object for class '{@link es.cv.gvcase.gvm.glossary.RedefinedTerm <em>Redefined Term</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Redefined Term</em>'.
	 * @see es.cv.gvcase.gvm.glossary.RedefinedTerm
	 * @generated
	 */
	EClass getRedefinedTerm();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	GlossaryFactory getGlossaryFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link es.cv.gvcase.gvm.glossary.impl.GlossaryImpl <em>Glossary</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see es.cv.gvcase.gvm.glossary.impl.GlossaryImpl
		 * @see es.cv.gvcase.gvm.glossary.impl.GlossaryPackageImpl#getGlossary()
		 * @generated
		 */
		EClass GLOSSARY = eINSTANCE.getGlossary();

		/**
		 * The meta object literal for the '<em><b>Contained Terms</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference GLOSSARY__CONTAINED_TERMS = eINSTANCE.getGlossary_ContainedTerms();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GLOSSARY__NAME = eINSTANCE.getGlossary_Name();

		/**
		 * The meta object literal for the '{@link es.cv.gvcase.gvm.glossary.impl.TermImpl <em>Term</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see es.cv.gvcase.gvm.glossary.impl.TermImpl
		 * @see es.cv.gvcase.gvm.glossary.impl.GlossaryPackageImpl#getTerm()
		 * @generated
		 */
		EClass TERM = eINSTANCE.getTerm();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TERM__NAME = eINSTANCE.getTerm_Name();

		/**
		 * The meta object literal for the '{@link es.cv.gvcase.gvm.glossary.impl.LocalTermImpl <em>Local Term</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see es.cv.gvcase.gvm.glossary.impl.LocalTermImpl
		 * @see es.cv.gvcase.gvm.glossary.impl.GlossaryPackageImpl#getLocalTerm()
		 * @generated
		 */
		EClass LOCAL_TERM = eINSTANCE.getLocalTerm();

		/**
		 * The meta object literal for the '<em><b>Definition</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LOCAL_TERM__DEFINITION = eINSTANCE.getLocalTerm_Definition();

		/**
		 * The meta object literal for the '{@link es.cv.gvcase.gvm.glossary.impl.ReferencedTermImpl <em>Referenced Term</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see es.cv.gvcase.gvm.glossary.impl.ReferencedTermImpl
		 * @see es.cv.gvcase.gvm.glossary.impl.GlossaryPackageImpl#getReferencedTerm()
		 * @generated
		 */
		EClass REFERENCED_TERM = eINSTANCE.getReferencedTerm();

		/**
		 * The meta object literal for the '<em><b>Source</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference REFERENCED_TERM__SOURCE = eINSTANCE.getReferencedTerm_Source();

		/**
		 * The meta object literal for the '{@link es.cv.gvcase.gvm.glossary.impl.RedefinedTermImpl <em>Redefined Term</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see es.cv.gvcase.gvm.glossary.impl.RedefinedTermImpl
		 * @see es.cv.gvcase.gvm.glossary.impl.GlossaryPackageImpl#getRedefinedTerm()
		 * @generated
		 */
		EClass REDEFINED_TERM = eINSTANCE.getRedefinedTerm();

	}

} //GlossaryPackage
