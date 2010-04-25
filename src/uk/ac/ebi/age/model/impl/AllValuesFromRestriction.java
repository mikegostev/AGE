package uk.ac.ebi.age.model.impl;

import java.util.Collection;

import uk.ac.ebi.age.model.AgeAbstractObject;
import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.model.AgeRelation;
import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.AgeRestriction;
import uk.ac.ebi.age.model.RestrictionException;

public class AllValuesFromRestriction implements AgeRestriction
{
 private AgeClass         sourceClass;
 private AgeRelationClass relationClass;
 private AgeRestriction   filler;

 public AllValuesFromRestriction(AgeClass scls, AgeRestriction fillerRestr, AgeRelationClass relcls)
 {
  sourceClass = scls;
  relationClass = relcls;
  filler = fillerRestr;
 }

 public String toString()
 {
  return "AllValuesFrom restriction. Source: '" + sourceClass.getName() + "' Relation: '" + relationClass.getName() + "' Filler: ( " + filler + " )";
 }

 public void validate(AgeAbstractObject aobj) throws RestrictionException
 {
  if(!(aobj instanceof AgeObject))
   throw new RestrictionException("AllValuesFrom restriction can only validate AgeObject-s");

  AgeObject obj = (AgeObject) aobj;

  Collection<AgeRelation> rels = obj.getRelationsMap().get(relationClass);

  if( rels == null )
   return;
  
  boolean other = false;

  for(AgeRelation rel : rels)
  {
   try
   {
    filler.validate(rel.getTargetObject());
   }
   catch(RestrictionException e)
   {
    other = true;
    break;
   }
  }

  if(other)
   throw new RestrictionException("Object: '" + obj.getId() + "' of class: '" + obj.getAgeElClass().getName()
     + "' doesn't satisfy restriction (has relations other than allowed): " + toString());

 }

}
