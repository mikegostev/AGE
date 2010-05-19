package uk.ac.ebi.age.model.impl.v1;

import java.io.Serializable;
import java.util.Collection;

import uk.ac.ebi.age.model.AgeAbstractObject;
import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.model.AgeRelation;
import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.AgeRestriction;
import uk.ac.ebi.age.model.AgeExactCardinalityRestriction;
import uk.ac.ebi.age.model.RestrictionException;

class ExactCardinalityRestrictionImpl implements AgeExactCardinalityRestriction, Serializable
{
 private static final long serialVersionUID = 1L;
 
 private AgeClass sourceClass;
 private AgeRelationClass relationClass;
 private AgeRestriction filler;
 private int cardinality;

 public ExactCardinalityRestrictionImpl(AgeClass scls, AgeRestriction fillerRestr, AgeRelationClass relcls, int card)
 {
  sourceClass=scls;
  relationClass=relcls;
  filler=fillerRestr;
  cardinality=card;
 }

 public void validate(AgeAbstractObject aobj) throws RestrictionException
 {
  if( ! ( aobj instanceof AgeObject ) )
   throw new RestrictionException("ExactCardinality restriction can only validate AgeObject-s");    
  
  AgeObject obj = (AgeObject)aobj;
  
  Collection<? extends AgeRelation> rels = obj.getRelations(relationClass);
  
  if( rels == null )
  {
   if(cardinality != 0)
    throw new RestrictionException("Object: '" + obj.getId() + "' of class: '" + obj.getAgeElClass().getName()
      + "' doesn't satisfy restriction (has no relations, cardinality=" + cardinality + "): " + toString());
   else
    return;
  }
  
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
  
  if( count != cardinality )
   throw new RestrictionException("Object: '"+obj.getId()+"' of class: '"
     +obj.getAgeElClass().getName()+"' doesn't satisfy restriction (relations: "+count+", cardinality: "+cardinality+"): "+toString());

 }

 public AgeClass getSourceClass()
 {
  return sourceClass;
 }

 @Override
 public AgeRelationClass getAgeRelationClass()
 {
  return relationClass;
 }

 @Override
 public int getCardinality()
 {
  return cardinality;
 }

 @Override
 public AgeRestriction getFiller()
 {
  return filler;
 }
}

