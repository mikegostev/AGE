package uk.ac.ebi.age.storage.impl;

import java.util.List;

import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.query.AgeQuery;

public interface AgeStorageIndex
{

 AgeQuery getQuery();

 void index(List<AgeObject> res);

 void reset();

}
