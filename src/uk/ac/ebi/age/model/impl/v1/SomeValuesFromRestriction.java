package uk.ac.ebi.age.model.impl.v1;

import java.io.Serializable;
import java.util.Collection;

import uk.ac.ebi.age.model.AgeAbstractObject;
import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.model.AgeRelation;
import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.AgeRestriction;
import uk.ac.ebi.age.model.RestrictionException;

class SomeValuesFromRestriction implements AgeRestriction, Serializable
{
 private static final long serialVersionUID = 1L;
 
 private AgeClass sourceClass;
 private AgeRelationClass relationClass;
 private AgeRestriction filler;
 
 public SomeValuesFromRestriction(AgeClass scls, AgeRestriction fillerRestr, AgeRelationClass relcls)
 {
  sourceClass=scls;
  relationClass=relcls;
  filler=fillerRestr;
 }

 public void validate(AgeAbstractObject aobj) throws RestrictionException
 {
  if( ! ( aobj instanceof AgeObject ) )
   throw new RestrictionException("SomeValuesFrom restriction can only validate AgeObject-s");    
  
  AgeObject obj = (AgeObject)aobj;
  
  Collection<? extends AgeRelation> rels = obj.getRelations(relationClass);
  
  if( rels == null )
   throw new RestrictionException("Object: '"+obj.getId()+"' of class: '"
     +obj.getAgeElClass().getName()+"' doesn't satisfy restriction (has no relations): "+toString());
  
  boolean has=false;
  
  for( AgeRelation rel : rels )
  {
   try
   {
    filler.validate(rel.getTargetObject());
    has = true;
    break;
   }
   catch(RestrictionException e)
   {
   }
  }
  
  if( ! has )
   throw new RestrictionException("Object: '"+obj.getId()+"' of class: '"
     +obj.getAgeElClass().getName()+"' doesn't satisfy restriction (has no necessary relations): "+toString());

 }

 public String toString()
 {
  return "SomeValuesFrom restriction. Source: '"+sourceClass.getName()+"' Relation: '"+relationClass.getName()+"' Filler: ( "+filler+" )";
 }

// public RestrictionValidator getValidator()
// {
//  return new SomeValuesFromValidator();
// }
//
// 
// private class SomeValuesFromValidator implements RestrictionValidator
// {
//  boolean satisfied=false;
//
//  public String getErrorMessage()
//  {
//   // TODO Auto-generated method stub
//   throw new dev.NotImplementedYetException();
//  }
//
//  public boolean isSatisfied()
//  {
//   return satisfied;
//  }
//
//  public void validate(AgeObject obj)
//  {
////    
////   
////   for( AgeRelation rel : obj.getRelations())
////   {
////   
////   AgeRelationClass relClass = rel.getRelationClass();
////   
////   if( !relClass.isClassOrSubclass(relationClass) )
////    return;
////   
////   RestrictionValidator flrValdr = filler.getValidator();
////   
////   flrValdr.validate(rel);
////   
////   throw new dev.NotImplementedYetException();
//  }
//  
// }


}
