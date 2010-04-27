package uk.ac.ebi.age.query;

import java.util.ArrayList;
import java.util.Collection;

import uk.ac.ebi.age.model.AgeObject;

public class AndExpression implements QueryExpression
{
 private Collection<QueryExpression> exprs = new ArrayList<QueryExpression>(5);
 
 public void addExpression(QueryExpression expr)
 {
  exprs.add(expr);
 }

 public boolean check(AgeObject obj)
 {
  for( QueryExpression expr : exprs )
  {
   if( ! expr.check(obj) )
    return false;
  }
  
  return true;
 }
}
