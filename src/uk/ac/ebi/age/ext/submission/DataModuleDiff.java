package uk.ac.ebi.age.ext.submission;


public interface DataModuleDiff
{

 String getId();

 void setId(String id);

 Status getStatus();

 void setStatus(Status status);

 boolean isMetaChanged();

 void setMetaChanged(boolean metaChanged);

 boolean isDataChanged();

 void setDataChanged(boolean dataChanged);

 void setOldDocumentVersion(long oldDocumentVersion);

 long getOldDocumentVersion();

 long getNewDocumentVersion();

 void setNewDocumentVersion(long newDocumentVersion);

}