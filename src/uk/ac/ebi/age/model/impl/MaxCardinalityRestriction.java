package uk.ac.ebi.age.model.impl;

import java.util.Collection;

import uk.ac.ebi.age.model.AgeAbstractObject;
import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.model.AgeRelation;
import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.AgeRestriction;
import uk.ac.ebi.age.model.RestrictionException;

public class MaxCardinalityRestriction implements AgeRestriction
{
 private AgeClass sourceClass;
 private AgeRelationClass relationClass;
 private AgeRestriction filler;
 private int cardinality;

 public MaxCardinalityRestriction(AgeClass scls, AgeRestriction fillerRestr, AgeRelationClass relcls, int card)
 {
  sourceClass=scls;
  relationClass=relcls;
  filler=fillerRestr;
  cardinality=card;
 }

 public void validate(AgeAbstractObject aobj) throws RestrictionException
 {
  if( ! ( aobj instanceof AgeObject ) )
   throw new RestrictionException("MaxCardinality restriction can only validate AgeObject-s");    
  
  AgeObject obj = (AgeObject)aobj;
  
  Collection<AgeRelation> rels = obj.getRelationsMap().get(relationClass);
  
  if( rels == null )
   return;
   
  int count=0;
  
  for( AgeRelation rel : rels )
  {
   try
   {
    filler.validate(rel.getTargetObject());
    count++;
   }
   catch(RestrictionException e)
   {
   }
  }
  
  if( count > cardinality )
   throw new RestrictionException("Object: '"+obj.getId()+"' of class: '"
     +obj.getAgeElClass().getName()+"' doesn't satisfy restriction (relations: "+count+", cardinality: "+cardinality+"): "+toString());

 }

}

