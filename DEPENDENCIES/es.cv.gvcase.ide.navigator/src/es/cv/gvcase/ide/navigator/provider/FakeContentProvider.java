package es.cv.gvcase.ide.navigator.provider;

import java.util.Collections;
import java.util.Map;

import org.eclipse.emf.ecore.provider.EcoreItemProviderAdapterFactory;
import org.eclipse.emf.ecore.resource.Resource;

public class FakeContentProvider extends MOSKittCommonContentProvider {

	public FakeContentProvider() {
		// Common factories
		addFactory(new EcoreItemProviderAdapterFactory(),true);
		
	}
	
	@Override
	protected boolean canHandleResource(Resource resource) {
		return false;
	}

	@Override
	public String getContributorID() {
		return "es.cv.gvcase.fakeContentProvider";
	}

	@Override
	protected Map<String, Object> getResourceFactories() {

		return Collections.emptyMap();
	}

	@Override
	protected String getViewerID() {
		return "es.cv.gvcase.ide.navigator.modelView";
	}

	@Override
	public boolean hasPropertySheetPage() {
		return false;
	}

}
