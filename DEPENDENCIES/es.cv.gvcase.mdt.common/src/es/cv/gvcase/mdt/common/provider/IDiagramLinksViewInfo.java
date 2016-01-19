package es.cv.gvcase.mdt.common.provider;

import java.util.List;

import org.eclipse.gmf.runtime.notation.View;

public interface IDiagramLinksViewInfo {

	List<ILinkDescriptor> getIncomingLinks(View view);

	List<ILinkDescriptor> getOutgoingLinks(View view);

	List<ILinkDescriptor> getContainedLinks(View view);

	List<ILinkDescriptor> getAllLinks(View view);

}
