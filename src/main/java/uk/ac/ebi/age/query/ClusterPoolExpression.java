package uk.ac.ebi.age.query;

import java.util.Set;

import uk.ac.ebi.age.model.AgeObject;

public class ClusterPoolExpression implements QueryExpression
{
 private final Set<String> clusterPool;

 public ClusterPoolExpression( Set<String> clstIDs )
 {
  clusterPool = clstIDs;
 }
 
 @Override
 public boolean test(AgeObject obj)
 {
  return clusterPool.contains(obj.getDataModule().getClusterId());
 }

 @Override
 public boolean isCrossingObjectConnections()
 {
  return false;
 }

}
