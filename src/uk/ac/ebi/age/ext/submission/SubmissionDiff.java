package uk.ac.ebi.age.ext.submission;

public interface SubmissionDiff
{

 void setId(String id2);

 String getId();

 long getModificationTime();

 void setModificationTime(long modificationTime);

 String getModifier();

 void setModifier(String modifier);

 boolean isDescriptionChanged();

 void setDescriptionChanged(boolean descriptionChanged);

 void addDataModuleDiff(DataModuleDiff mdif);

 void addAttachmentDiff(AttachmentDiff adif);

}