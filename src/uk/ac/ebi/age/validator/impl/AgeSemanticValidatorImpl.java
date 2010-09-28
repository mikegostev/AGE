package uk.ac.ebi.age.validator.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import uk.ac.ebi.age.model.AgeAttribute;
import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.model.AttributeAttachmentRule;
import uk.ac.ebi.age.model.Attributed;
import uk.ac.ebi.age.model.AttributedClass;
import uk.ac.ebi.age.model.QualifierRule;
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

 private boolean validateObject(AgeObject obj, AgeClass cls)
 {
  boolean valid = true;
  
  if( cls.getSuperClasses() != null )
  {
   for( AgeClass supCls : cls.getSuperClasses() )
    valid = valid?validateObject(obj,supCls):false;
  }
  
  validateAttributed( obj, cls );
  
  return valid;
 }

 private boolean isAttributeAllowed(AgeAttributeClass atCls, Collection<? extends AgeAttribute> attrs, Collection<AttributeAttachmentRule> atRules)
 {
  if( atRules == null )
   return false;
  
  boolean satisf = false;

  ruleCyc: for(AttributeAttachmentRule rul : atRules)
  {
   if(!((rul.isSubclassesIncluded() && atCls.isClassOrSubclass(rul.getAttributeClass())) || rul.getAttributeClass().equals(atCls)))
    continue;

   if(rul.getType() == RestrictionType.MUSTNOT)
    continue;

   switch(rul.getCardinalityType())
   {
    case EXACT:
     if(rul.getCardinality() != attrs.size())
      continue;

     break;

    case MAX:
     if(rul.getCardinality() < attrs.size())
      continue;

     break;

    case MIN:
     if(rul.getCardinality() > attrs.size())
      continue;

     break;
   }

   List<AgeAttribute> atList = null;

   if(rul.isValueUnique() && attrs.size() > 1)
   {
    atList = new ArrayList<AgeAttribute>(attrs.size());
    atList.addAll(attrs);

    for(int i = 0; i < attrs.size() - 1; i++)
    {
     for(int j = i + 1; j < attrs.size(); j++)
     {
      if(atList.get(i).equals(atList.get(j)))
       continue ruleCyc;
     }
    }
   }

   if(rul.getQualifiers() != null)
   {
    for(QualifierRule qr : rul.getQualifiers())
    {
     for(AgeAttribute attr : attrs)
     {
      boolean found = false;

      for(String atcID : attr.getAttributeClassesIds())
      {
       if(atcID.equals(qr.getAttributeClass().getId()))
       {
        found = true;
        break;
       }
      }

      if(!found)
       continue ruleCyc;
     }

     if(qr.isUnique())
     {
      if(atList == null)
      {
       atList = new ArrayList<AgeAttribute>(attrs.size());
       atList.addAll(attrs);
      }

      for(int i = 0; i < attrs.size() - 1; i++)
      {
       for(int j = i + 1; j < attrs.size(); j++)
       {
        if(!isEqual(atList.get(i).getAttributesByClass(qr.getAttributeClass()), atList.get(j).getAttributesByClass(qr.getAttributeClass())))
         continue ruleCyc;
       }
      }

     }
    }
   }

   for( AgeAttribute attr : attrs )
   {
    Collection<? extends AgeAttributeClass> qClss = attr.getAttributeClasses();
    
    if( qClss != null )
    {
     for( AgeAttributeClass qCls : qClss )
     {
      if( ! isAttributeAllowed(qCls, attr.getAttributesByClass(qCls), atCls.getAttributeAttachmentRules() ) )
       return false;
     }
    }
   }
   
   
   satisf = true;
   break;
   
  }

  return satisf;
 }
 
 private boolean validateAttributed( Attributed obj, AttributedClass cls )
 {
  boolean valid = true;
  

  Collection<AttributeAttachmentRule> atRules = cls.getAttributeAttachmentRules() != null? cls.getAttributeAttachmentRules() : Collections.<AttributeAttachmentRule>emptyList();
  
  Collection<? extends AgeAttributeClass> atClasses = obj.getAttributeClasses();
  
  boolean objectOk=true;
  
  for( AgeAttributeClass atCls : atClasses )
  {
   if( atCls.isCustom() )
    continue;
   
   Collection<? extends AgeAttribute> attrs = obj.getAttributesByClass(atCls);
   
   if( ! isAttributeAllowed(atCls, attrs, atRules) )
   {
    objectOk=false;
    break;
   }
 
  }
  
  if( cls.getAttributeAttachmentRules() != null )
  {
   for( AttributeAttachmentRule atRl : cls.getAttributeAttachmentRules() )
    isRuleSatisfied(atRl,obj);
  }
  
  return valid;
 }

 private boolean checkUniq( Collection<? extends AgeAttribute> attrs )
 {
  if( attrs.size() <= 1 )
   return true;
  
  List<AgeAttribute> atList = new ArrayList<AgeAttribute>( attrs.size() );
  atList.addAll(attrs);
  
  for( int i=0; i < attrs.size()-1; i++ )
  {
   for( int j=i+1; j < attrs.size(); j++ )
   {
    if( atList.get(i).equals( atList.get(j) ) )
     return false;
   }
  }
  
  return true;
 }
 
 private boolean isEqual(Collection<? extends AgeAttribute> set1, Collection<? extends AgeAttribute> set2 )
 {
  if( set1 == null )
   return set2 == null || set2.size() == 0 ;

  if( set2 == null )
   return set1.size() == 0 ;
  
  if( set1.size() != set2.size() )
   return false;
  
  if( set1.size() == 1 )
   return set1.iterator().next().equals(set2.iterator().next());
  
  List<AgeAttribute> lst1 = new ArrayList<AgeAttribute>( set1.size() );
  List<AgeAttribute> lst2 = new ArrayList<AgeAttribute>( set2.size() );
  
  lst1.addAll(set1);
  lst2.addAll(set2);
  
  Collections.sort(lst1);
  Collections.sort(lst2);
  
  for( int i=0; i < set1.size(); i++ )
  {
   if( ! lst1.get(i).equals(lst2.get(i)) )
    return false;
  }
  
  return true;
 }
 
 private boolean isRuleSatisfied(AttributeAttachmentRule atRl, Attributed obj)
 {
  if( atRl.getType() == RestrictionType.MAY )
   return true;
  
  Collection<? extends AgeAttribute> atrs = obj.getAttributesByClassId(atRl.getAttributeClass().getId());

  if( atrs == null || atrs.size() == 0 )
   return atRl.getType() == RestrictionType.MUSTNOT;
  
  switch( atRl.getCardinalityType() )
  {
   case EXACT:
    if( atrs.size() != atRl.getCardinality() )
     return false;
    
    break;
    
   case MAX:
    if( atrs.size() > atRl.getCardinality() )
     return false;
    
    break;
    
   case MIN:
    if( atrs.size() < atRl.getCardinality() )
     return false;

  }
  
  return true;
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
