package uk.ac.ebi.age.authz;

public interface Classifier
{

 String getId();

 String getDescription();

 Tag getTag(String tagId);

}
