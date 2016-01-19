package es.cv.gvcase.mdt.common.storage.store;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gmf.runtime.diagram.ui.parts.IDiagramWorkbenchPart;
import org.eclipse.ui.IEditorPart;

public class EditorStorageApplicable implements IStorageApplicable {

	protected IEditorPart editor = null;

	protected List<String> appicableNsUris = new ArrayList<String>();

	public EditorStorageApplicable(IEditorPart editor) {
		this.editor = editor;
	}

	public EditorStorageApplicable(List<String> applicableNsUris) {
		this.appicableNsUris = applicableNsUris;
	}

	public String getApplicableNsUri() {
		if (editor instanceof IDiagramWorkbenchPart) {
			EObject rootElement = ((IDiagramWorkbenchPart) editor).getDiagram()
					.getElement();
			return rootElement.eClass().getEPackage().getNsURI();
		}
		return null;
	}

	public boolean isApplicable(String nsUri) {
		String applicableNsUri = getApplicableNsUri();
		if (applicableNsUri != null && applicableNsUri.equals(nsUri)) {
			return true;
		}
		if (appicableNsUris != null) {
			for (String s : appicableNsUris) {
				if (s != null && s.equals(nsUri)) {
					return true;
				}
			}
		}
		return false;
	}

}
