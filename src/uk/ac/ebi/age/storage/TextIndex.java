package uk.ac.ebi.age.storage;

import java.util.List;

import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.storage.impl.AgeStorageIndex;

public interface TextIndex extends AgeStorageIndex
{
 List<AgeObject> select(String query);
}
