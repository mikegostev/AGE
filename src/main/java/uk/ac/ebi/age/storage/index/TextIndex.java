package uk.ac.ebi.age.storage.index;

import java.util.Collection;
import java.util.List;

import uk.ac.ebi.age.model.AgeObject;

public interface TextIndex extends AgeIndex
{
 List<AgeObject> select(String query);
 Selection select(String lucQuery, int offset, int count, Collection<String> aggrs );

 int count(String query);

}
