package uk.ac.ebi.age.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import uk.ac.ebi.age.model.AgeObject;

public class AndExpression implements QueryExpression
{
 private final Collection<QueryExpression> exprs;
 
 public AndExpression()
 {
  exprs = new ArrayList<QueryExpression>(5);
 }
 
 public AndExpression( QueryExpression... exps )
 {
  exprs = Arrays.asList(exps);
 }

 public void addExpression(QueryExpression expr)
 {
  exprs.add(expr);
 }

 @Override
 public boolean test(AgeObject obj)
 {
  for( QueryExpression expr : exprs )
  {
   if( ! expr.test(obj) )
    return false;
  }
  
  return true;
 }
 

 @Override
 public boolean isCrossingObjectConnections()
 {
  for( QueryExpression expr : exprs )
  {
   if( expr.isCrossingObjectConnections() )
    return true;
  }
  
  return false;
 }
}
