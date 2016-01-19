package es.cv.gvcase.gvm.glossary.formseditor.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "es.cv.gvcase.gvm.glossary.formseditor.internal.messages"; //$NON-NLS-1$
	public static String DefinitionComposite_Definition;
	public static String MainPage_Details;
	public static String MainPage_Glossary;
	public static String MainPage_Terms;
	public static String NameComposite_Name;
	public static String SourceComposite_Source;
	public static String TableOfTermsComposite_AddLocalTerm;
	public static String TableOfTermsComposite_AddRedTerm;
	public static String TableOfTermsComposite_AddRefTerm;
	public static String TableOfTermsComposite_Name;
	public static String TableOfTermsComposite_NewLocalTerm;
	public static String TableOfTermsComposite_NewRedefinedTerm;
	public static String TableOfTermsComposite_NewReferencedTerm;
	public static String TableOfTermsComposite_RemoveTerm;
	public static String TableOfTermsComposite_Type;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
