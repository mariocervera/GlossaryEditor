/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package es.cv.gvcase.gvm.glossary.impl;

import es.cv.gvcase.gvm.glossary.*;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class GlossaryFactoryImpl extends EFactoryImpl implements GlossaryFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static GlossaryFactory init() {
		try {
			GlossaryFactory theGlossaryFactory = (GlossaryFactory)EPackage.Registry.INSTANCE.getEFactory("http://es/cv/gvcase/glossary"); 
			if (theGlossaryFactory != null) {
				return theGlossaryFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new GlossaryFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public GlossaryFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
			case GlossaryPackage.GLOSSARY: return createGlossary();
			case GlossaryPackage.LOCAL_TERM: return createLocalTerm();
			case GlossaryPackage.REFERENCED_TERM: return createReferencedTerm();
			case GlossaryPackage.REDEFINED_TERM: return createRedefinedTerm();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Glossary createGlossary() {
		GlossaryImpl glossary = new GlossaryImpl();
		return glossary;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public LocalTerm createLocalTerm() {
		LocalTermImpl localTerm = new LocalTermImpl();
		return localTerm;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ReferencedTerm createReferencedTerm() {
		ReferencedTermImpl referencedTerm = new ReferencedTermImpl();
		return referencedTerm;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RedefinedTerm createRedefinedTerm() {
		RedefinedTermImpl redefinedTerm = new RedefinedTermImpl();
		return redefinedTerm;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public GlossaryPackage getGlossaryPackage() {
		return (GlossaryPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static GlossaryPackage getPackage() {
		return GlossaryPackage.eINSTANCE;
	}

} //GlossaryFactoryImpl
