package uk.ac.ebi.age.storage.index;

import uk.ac.ebi.age.model.AgeObject;

public interface AttachedSortedTextIndex<KeyT> extends TextIndex, AgeAttachedIndex
{
 AgeObject getAgeObject(KeyT key);

}
