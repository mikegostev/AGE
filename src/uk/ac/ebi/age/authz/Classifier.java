package uk.ac.ebi.age.authz;

import java.util.Collection;

public interface Classifier
{

 String getId();

 String getDescription();

 Tag getTag(String tagId);

 Collection<? extends Tag> getTags();
}
