package uk.ac.ebi.age.query;

import uk.ac.ebi.age.model.AgeObject;

public interface QueryExpression
{

 boolean check(AgeObject obj);

}
