package uk.ac.ebi.age.query;

import uk.ac.ebi.age.model.AgeObject;

public class TrueExpression implements QueryExpression
{

 public boolean check(AgeObject obj)
 {
  return true;
 }

}
