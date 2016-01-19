package es.cv.gvcase.mdt.common.actions.registry;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import es.cv.gvcase.emf.common.part.ExtensionPointParser;
import es.cv.gvcase.mdt.common.actions.handlers.ClipboardActionHandler;

public class ClipboardActionHandlerRegistry {

	// //
	// Singleton
	// //

	protected static ClipboardActionHandlerRegistry INSTANCE = null;

	public static ClipboardActionHandlerRegistry getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ClipboardActionHandlerRegistry();
		}
		return INSTANCE;
	}

	private ClipboardActionHandlerRegistry() {
	}

	// //
	// Helper stub classes
	// //

	public class ClipboardHandler {
		public String modelID;
		public Object clipboardhandlerClass;
	}

	// //
	// Handlers information
	// //

	protected Map<String, ClipboardActionHandler> mapmodelID2ClipboardHandler = null;

	protected Map<String, ClipboardActionHandler> getAllHandlers() {
		if (mapmodelID2ClipboardHandler == null) {
			readExtensionPoint();
		}
		if (mapmodelID2ClipboardHandler == null) {
			return Collections.emptyMap();
		}
		return mapmodelID2ClipboardHandler;
	}

	public ClipboardActionHandler getClipboardHandlerFor(String modelID) {
		Map<String, ClipboardActionHandler> mapEditorID2Handler = getAllHandlers();
		if (mapEditorID2Handler != null) {
			return mapEditorID2Handler.get(modelID);
		}
		return null;
	}

	// //
	// Extension point reader
	// //

	public static final String ExtensionPointID = "es.cv.gvcase.mdt.common.clipboardhandlers";

	protected void readExtensionPoint() {
		if (mapmodelID2ClipboardHandler == null) {
			mapmodelID2ClipboardHandler = new HashMap<String, ClipboardActionHandler>();
		}
		mapmodelID2ClipboardHandler.clear();
		//
		ExtensionPointParser parser = new ExtensionPointParser(
				ExtensionPointID, new Class[] { ClipboardHandler.class },
				this);
		ClipboardHandler handler = null;
		for (Object o : parser.parseExtensionPoint()) {
			if (o instanceof ClipboardHandler) {
				handler = (ClipboardHandler) o;
				if (handler.modelID != null
						&& handler.clipboardhandlerClass instanceof ClipboardActionHandler) {
					mapmodelID2ClipboardHandler
							.put(
									handler.modelID,
									(ClipboardActionHandler) handler.clipboardhandlerClass);
				}
			}
		}
	}

}
