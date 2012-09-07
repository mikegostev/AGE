package uk.ac.ebi.age.storage.index;

import java.util.List;

import uk.ac.ebi.age.model.AgeObject;

public interface AgeAttachedIndex
{
 List<? extends AgeObject> getObjectList();
}
