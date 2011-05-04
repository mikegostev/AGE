package uk.ac.ebi.age.ext.submission;


public interface AttachmentDiff
{

 String getId();

 void setId(String id);

 Status getStatus();

 void setStatus(Status status);

 boolean isMetaChanged();

 void setMetaChanged(boolean metaChanged);

 boolean isDataChanged();

 void setDataChanged(boolean dataChanged);

 boolean isVisibilityChanged();

 void setVisibilityChanged(boolean visibilityChanged);

 long getOldFileVersion();

 void setOldFileVersion(long oldFileVersion);

 long getNewFileVersion();

 void setNewFileVersion(long newFileVersion);

}