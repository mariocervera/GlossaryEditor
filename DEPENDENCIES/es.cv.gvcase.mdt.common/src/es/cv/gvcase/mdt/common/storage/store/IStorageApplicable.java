package es.cv.gvcase.mdt.common.storage.store;

public interface IStorageApplicable {

	String getApplicableNsUri();
	
	boolean isApplicable(String nsUri);
}
