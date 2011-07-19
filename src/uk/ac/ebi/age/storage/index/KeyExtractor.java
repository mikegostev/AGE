package uk.ac.ebi.age.storage.index;

import uk.ac.ebi.age.model.AgeObject;

public interface KeyExtractor<KeyT>
{

 KeyT getKey(AgeObject o1);

}
