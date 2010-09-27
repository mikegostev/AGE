package uk.ac.ebi.age.validator.impl;

import java.util.Collection;
import java.util.Collections;

import uk.ac.ebi.age.model.AgeAttribute;
import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.model.AttributeAttachmentRule;
import uk.ac.ebi.age.model.RestrictionType;
import uk.ac.ebi.age.model.Submission;
import uk.ac.ebi.age.validator.AgeSemanticValidator;

public class AgeSemanticValidatorImpl implements AgeSemanticValidator
{

 @Override
 public void validate(Submission subm)
 {
  for( AgeObject obj : subm.getObjects() )
   validateObject( obj, obj.getAgeElClass() );
 }

 private boolean validateObject( AgeObject obj, AgeClass cls )
 {
  boolean valid = true;
  
  if( cls.getSuperClasses() != null )
  {
   for( AgeClass supCls : cls.getSuperClasses() )
    valid = valid?validateObject(obj,supCls):false;
  }
  
  Collection<AttributeAttachmentRule> atRules = cls.getAttributeAttachmentRules() != null? cls.getAttributeAttachmentRules() : Collections.<AttributeAttachmentRule>emptyList();
  
  Collection<? extends AgeAttributeClass> atClasses = obj.getAttributeClasses();
  
  for( AgeAttributeClass atCls : atClasses )
  {
   if( atCls.isCustom() )
    continue;
   
   Collection<? extends AgeAttribute> attrs = obj.getAttributesByClass(atCls);
   
   boolean allowed = false;
   
   for( AttributeAttachmentRule rul : atRules )
   {
    if( ( rul.isSubclassesIncluded() && atCls.isClassOrSubclass( rul.getAttributeClass() )  ) || rul.getAttributeClass().equals(atCls) )
    {
     if(rul.getType() != RestrictionType.MUSTNOT)
     {
      switch(rul.getCardinalityType())
      {
       case EXACT:
        if( rul.getCardinality() != attrs.size() )
         continue;
        
        break;
        
       case MAX:
        if( rul.getCardinality() < attrs.size() )
         continue;
        
        break;

       case MIN:
        if( rul.getCardinality() > attrs.size() )
         continue;
        
        break;
      }
      
      if( rul.isQualifiersUnique() )
      {
       
      }
     }
    }
   } 
  }
  
  if( cls.getAttributeAttachmentRules() != null )
  {
   for( AttributeAttachmentRule atRl : cls.getAttributeAttachmentRules() )
    validateObjectByAttributeRule(atRl,obj);
  }
  
  return valid;
 }

 private void validateObjectByAttributeRule(AttributeAttachmentRule atRl, AgeObject obj)
 {
  
  for( String atClsId : obj.getAttributeClassesIds() )
  {
   Collection<? extends AgeAttribute> atrs = obj.getAttributesByClassId(atClsId);
  }

  
 }

// private boolean validateAttributeByAttributeRule(AttributeAttachmentRule atRl, AgeAttribute at)
// {
//  if( !atRl.isSubclassesIncluded() )
//  {
//   if( ! at.getAgeElClass().equals(atRl.getAttributeClass()) )
//    return false;
//  }
//  else if( ! at.getAgeElClass().isClassOrSubclass(atRl.getAttributeClass()) )
//   return false;
//  
//  int nVals = at.
//  
//  if( atRl.getCardinalityType() != Cardinality.ANY )
//  {}
// }
 
}
