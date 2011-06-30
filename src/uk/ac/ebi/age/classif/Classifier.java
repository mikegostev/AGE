package uk.ac.ebi.age.classif;

public interface Classifier
{

 String getId();

 String getDescription();

 Tag getTag(String tagId);

}
