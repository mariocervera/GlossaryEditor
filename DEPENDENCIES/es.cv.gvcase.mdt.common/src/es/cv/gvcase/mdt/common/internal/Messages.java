package es.cv.gvcase.mdt.common.internal;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	
	// public string messages
	
	public static String MOSKittMultiPageEditor_1;
	//
	public static String MOSKittMultiPageEditor_10;
	//
	public static String MOSKittMultiPageEditor_15;
	//
	public static String MOSKittMultiPageEditor_16;
	//
	public static String MOSKittMultiPageEditor_19;
	//
	public static String MOSKittMultiPageEditor_2;
	//
	public static String MOSKittMultiPageEditor_20;
	//
	public static String MOSKittMultiPageEditor_23;
	//
	public static String MOSKittMultiPageEditor_26;
	//
	public static String MOSKittMultiPageEditor_27;
	//
	public static String MOSKittMultiPageEditor_3;
	//
	public static String MOSKittMultiPageEditor_30;
	//
	public static String MOSKittMultiPageEditor_31;
	//
	public static String MOSKittMultiPageEditor_4;
	//
	public static String MOSKittMultiPageEditor_5;
	//
	public static String MOSKittMultiPageEditor_7;
	//
	public static String MOSKittMultiPageEditor_8;
	//
	public static String MOSKittMultiPageEditor_9;
	//
	public static String NewDiagramFileWizard_RootSelectionPageNoSelectionMessage;
	//
	public static String NewDiagramFileWizard_RootSelectionPageSelectionTitle;
	//
	public static String NewDiagramFileWizard_RootSelectionPageInvalidSelectionMessage;
	
	// //
	// --
	// //
	
	private static final String BUNDLE_NAME = "es.cv.gvcase.mdt.common.internal.messages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
