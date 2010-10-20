package uk.ac.ebi.age.validator.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import uk.ac.ebi.age.log.LogNode;
import uk.ac.ebi.age.log.LogNode.Level;
import uk.ac.ebi.age.model.AgeAttribute;
import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.model.AgeRelation;
import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.AttributeAttachmentRule;
import uk.ac.ebi.age.model.Attributed;
import uk.ac.ebi.age.model.AttributedClass;
import uk.ac.ebi.age.model.QualifierRule;
import uk.ac.ebi.age.model.RelationRule;
import uk.ac.ebi.age.model.RestrictionType;
import uk.ac.ebi.age.model.Submission;
import uk.ac.ebi.age.validator.AgeSemanticValidator;

public class AgeSemanticValidatorImpl implements AgeSemanticValidator
{

 @Override
 public boolean validate(Submission subm, LogNode log)
 {
  boolean res = true;
  
  for( AgeObject obj : subm.getObjects() )
  {
   LogNode ln = log.branch("Validating object ID="+obj.getId()+" (OrigId="+obj.getOriginalId()+") Order: "+obj.getOrder());
   
   if( !validateObject( obj, obj.getAgeElClass(), ln ) )
   {
    ln.log(Level.ERROR, "Object validation failed");
   
    res = false;
   }
   else
    ln.log(Level.INFO, "Object validation successful");
  }
  
  return res;
 }

 private boolean validateObject(AgeObject obj, AgeClass cls, LogNode log)
 {
  boolean valid = true;

//  log.log(Level.INFO, "Validating object ID="+obj.getId()+" (OrigId="+obj.getId()+") with class '"+cls.getName()+"'");
  
//  if( cls.getSuperClasses() != null )
//  {
//   LogNode ln = log.branch("Validating against super classes");
//   
//   for( AgeClass supCls : cls.getSuperClasses() )
//    valid = validateObject(obj,supCls,ln) && valid;
//  }
  
  boolean res;
  
  LogNode ln = log.branch("Validating object's attributes");
  res = validateAttributed( obj, 0, ln );
  valid = res && valid;

  if( res )
   ln.log(Level.INFO, "Attributes validation successful");
  else
   ln.log(Level.ERROR, "Attributes validation failed");
  
  
  ln = log.branch("Validating object's relations");
  res = validateRelations( obj, ln );

  valid = res && valid;

  if( res )
   ln.log(Level.INFO, "Relations validation successful");
  else
   ln.log(Level.ERROR, "Relations validation failed");

  
  return valid;
 }

 private boolean validateRelations(AgeObject obj, LogNode log)
 {
  AgeClass cls = obj.getAgeElClass();

  Collection<RelationRule> rlRules = cls.getAllRelationRules();

  if( rlRules == null )
   rlRules = Collections.<RelationRule> emptyList();
  
  Collection< ? extends AgeRelationClass> rlClasses = obj.getRelationClasses();

  boolean objectOk = true;

  for(AgeRelationClass rlCls : rlClasses)
  {
   if(rlCls.isCustom() || rlCls.isImplicit() )
    continue;

   
   Collection< ? extends AgeRelation> rels = obj.getRelationsByClass(rlCls, true);

   LogNode ln = log.branch("Validation relations of class '"+rlCls.getName()+"' Relations number: "+rels.size());

   
   boolean res = checkTargetsUnique( rels, log ); 

   if( ! res )
    ln.log(Level.ERROR, "Relation targets are not unique");
   
   objectOk = res && objectOk; 

   res = isRelationAllowed(rlCls, rels, rlRules, ln);
   
   if( res )
    ln.log(Level.INFO, "Validation successful");
   else
    ln.log(Level.ERROR, "Validation failed");
   
   objectOk = res && objectOk; 
  }

  if(cls.getRelationRules() != null)
  {
   LogNode ln = log.branch("Validating relation rules");

   boolean rrulOk = true;
   for(RelationRule rlRl : cls.getRelationRules())
   {
    LogNode rlln = ln.branch("Validating rule: "+rlRl.getRuleId()+" of class: '"+cls.getName()+"'");

    boolean res = isRelationRuleSatisfied(rlRl, obj, rlln);
    
    if( res )
     rlln.log(Level.ERROR, "Rule failed");
    else
     rlln.log(Level.ERROR, "Rule satisfied");
    
    rrulOk = res && rrulOk;
   }
   
   if( rrulOk )
    ln.log(Level.INFO, "Validation successful" );
   else
    ln.log(Level.ERROR, "Validation failed" );
   
   objectOk = objectOk && rrulOk;
  }

  
  LogNode ln = log.branch("Validating relation qualifiers");
  boolean qres = true;
  for(AgeRelation rl : obj.getRelations())
  {
   LogNode atln = ln.branch("Validating relation's qualifiers. Relation class: '"+rl.getAgeElClass().getName()
     +"' Target object: "+rl.getTargetObject().getOriginalId()+" Order: "+rl.getOrder());

   boolean res = validateAttributed(rl,1,atln);
   
   if( res )
    atln.log(Level.INFO, "Validation successful" );
   else
    atln.log(Level.ERROR, "Validation failed" );

   qres = res && qres;
  }
  
  if( qres )
   ln.log(Level.INFO, "Validation successful" );
  else
   ln.log(Level.ERROR, "Validation failed" );

  objectOk = objectOk && qres;
  
  return objectOk;
 }
 
 
 private boolean validateAttributed( Attributed obj, int level, LogNode log )
 {
  boolean valid = true;
  
//  log.log(Level.INFO, "Validation")
  
  AttributedClass cls = obj.getAttributedClass();

  Collection<AttributeAttachmentRule> atRules = cls.getAttributeAttachmentRules() != null?
    cls.getAttributeAttachmentRules() : Collections.<AttributeAttachmentRule>emptyList();
  
  Collection<? extends AgeAttributeClass> atClasses = obj.getAttributeClasses();
  
  for( AgeAttributeClass atCls : atClasses )
  {
   if( atCls.isCustom() )
    continue;
   
   Collection<? extends AgeAttribute> attrs = obj.getAttributesByClass(atCls, true);
   
   LogNode ln = log.branch("Validating attributes of class '"+atCls.getName()+"' Attributes: "+attrs.size());

   boolean res = isAttributeAllowed(atCls, attrs, atRules, ln);
   
   if( res )
    ln.log(Level.INFO, "Validation successful");
   else
    ln.log(Level.ERROR, "Validation failed");
   
   valid = res && valid;
  }
  
  if( cls.getAttributeAttachmentRules() != null )
  {
   LogNode ln = log.branch("Validating attribute rules");

   boolean rlres = true;
   for( AttributeAttachmentRule atRl : cls.getAttributeAttachmentRules() )
   {
    LogNode sln = ln.branch("Validating rule "+atRl.getRuleId()+". Type: "+atRl.getType().name());

    boolean res = isAttributeRuleSatisfied( atRl, obj, sln );
    
    if( res )
     sln.log(Level.INFO, "Validation successful");
    else
     sln.log(Level.ERROR, "Validation failed");
    
    rlres = res && rlres;
   }
   
   if( rlres )
    ln.log(Level.INFO, "Validation successful");
   else
    ln.log(Level.ERROR, "Validation failed");
  
   valid = rlres && valid;
  }
  
  if( obj.getAttributes() != null )
  {
   LogNode ln = log.branch("Validating qualifiers");
   boolean qres = true;
   for( AgeAttribute attr : obj.getAttributes() )
   {
    LogNode sln = ln.branch("Validating qualifier. Class: "+attr.getAgeElClass().getName()+" Column: "+attr.getOrder() );
    
    boolean res = validateAttributed(attr,level+1,sln);

    if( res )
     sln.log(Level.INFO, "Qualifier validation successful");
    else
     sln.log(Level.INFO, "Qualifier validation failes");
    
    valid = res && valid;
    qres = res && qres;
   }
   
   if( qres )
    ln.log(Level.INFO, "Qualifiers validation successful");
   else
    ln.log(Level.INFO, "Qualifiers validation failes");
  }
  

  return valid;
 }
 
 private boolean isAttributeAllowed(AgeAttributeClass atCls, Collection<? extends AgeAttribute> attrs, Collection<AttributeAttachmentRule> atRules, LogNode log)
 {
  if( atRules == null )
   return false;
  
  for(AttributeAttachmentRule rul : atRules)
  {
   LogNode ln = log.branch("Validating rule "+rul.getRuleId());

   if(rul.getType() == RestrictionType.MUSTNOT)
   {
    ln.log(Level.DEBUG, "MUSTNOT rule. Skiping rule.");
    continue;
   }
   
   if(!((rul.isSubclassesIncluded() && atCls.isClassOrSubclass(rul.getAttributeClass())) || rul.getAttributeClass().equals(atCls)))
   {
    ln.log(Level.DEBUG, "Rule doesn't belong to attribute class. Skiping rule.");
    continue;
   }
   

   if( ! matchCardinality( rul, attrs.size() ) )
   {
    ln.log(Level.INFO,"Rule "+rul.getRuleId()+" cardinality requirement failed. Cardinality: "
      +rul.getCardinalityType().name()+":"+rul.getCardinality()+" Attributes: "+attrs.size()+". Skiping rule.");
    continue;
   }
   else
    ln.log(Level.DEBUG, "Cardinality validation successful");
   
   if( rul.isValueUnique() )
   {
    if( ! checkValuesUnique( rul, attrs ) )
    {
     ln.log(Level.INFO, "Value uniqueness check failed. Skiping rule");
     continue;
    }
    else
     ln.log(Level.DEBUG, "Value uniqueness check successful");
   }
   
   if( rul.getQualifiers() != null  )
   {
    if( ! matchQualifiers( rul.getQualifiers(), attrs, ln ) )
    {
     ln.log(Level.INFO, "Qualifiers don't match. Skiping rule");
     continue;
    }
    else
     ln.log(Level.DEBUG, "Qualifiers matched");
   }
  
  
   ln.log(Level.INFO, "Attribute(s) allowed by this rule");
   return true;
  }

  return false;
 }
 
 private boolean isRelationAllowed(AgeRelationClass rlCls, Collection<? extends AgeRelation> rels, Collection<RelationRule> relRules, LogNode log)
 {
  if( relRules == null )
   return false;
  
//  log.log(Level.INFO, "Checking whether relations are allowed by some rule");
  
  for(RelationRule rul : relRules)
  {
   LogNode ln = log.branch("Validating rule "+rul.getRuleId());

   if(!((rul.isSubclassesIncluded() && rlCls.isClassOrSubclass(rul.getRelationClass())) || rul.getRelationClass().equals(rlCls)))
   {
    ln.log(Level.DEBUG, "Rule "+rul.getRuleId()+" doesn't match class '"+rlCls.getName()+"'. Skiping.");
    continue;
   }
   
   if(rul.getType() == RestrictionType.MUSTNOT)
   {
    ln.log(Level.DEBUG, "MUSTNOT rule. Skiping rule.");
    continue;
   }

   if( ! matchCardinality( rul, rels.size() ) )
   {
    ln.log(Level.INFO,"Rule "+rul.getRuleId()+" cardinality requirement failed. Cardinality: "
      +rul.getCardinalityType().name()+":"+rul.getCardinality()+" Relations: "+rels.size()+". Skiping rule.");
    continue;
   }
   else
    ln.log(Level.DEBUG, "Cardinality validation successful");

  
   if( rul.getQualifiers() != null  )
   {
    if( ! matchQualifiers( rul.getQualifiers(), rels, ln ) )
    {
     ln.log(Level.INFO, "Qualifiers don't match. Skiping rule");
     continue;
    }
    else
     ln.log(Level.DEBUG, "Qualifiers matched");
   }

  
   ln.log(Level.INFO,"Relations of class '"+rlCls.getName()+"' are allowed by rule "+rul.getRuleId());

   
   return true;
  }

  return false;
 }

// private boolean checkUniq( Collection<? extends AgeAttribute> attrs )
// {
//  if( attrs.size() <= 1 )
//   return true;
//  
//  List<AgeAttribute> atList = new ArrayList<AgeAttribute>( attrs.size() );
//  atList.addAll(attrs);
//  
//  for( int i=0; i < attrs.size()-1; i++ )
//  {
//   for( int j=i+1; j < attrs.size(); j++ )
//   {
//    if( atList.get(i).equals( atList.get(j) ) )
//     return false;
//   }
//  }
//  
//  return true;
// }
 
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
 
 private boolean isAttributeRuleSatisfied(AttributeAttachmentRule atRl, Attributed obj, LogNode log)
 {
  if( atRl.getType() == RestrictionType.MAY )
  {
   log.log(Level.DEBUG, "MAY rule. Skiping rule.");
   return true;
  }
  
  Collection<? extends AgeAttribute> attrs = obj.getAttributesByClass(atRl.getAttributeClass(), true);

  if( attrs == null || attrs.size() == 0 )
  {
   log.log(Level.DEBUG, "No attributes of rule's class");

   return atRl.getType() == RestrictionType.MUSTNOT;
  }
  
  LogNode ln = log.branch("Validating cardinality");
  if( ! matchCardinality( atRl, attrs.size() ) )
  {
   log.log(Level.INFO,"Rule "+atRl.getRuleId()+" cardinality requirement failed. Cardinality: "+atRl.getCardinalityType().name()+":"+atRl.getCardinality()
     +" Attributes: "+attrs.size());
   
   return atRl.getType() == RestrictionType.MUSTNOT;
  }
  else
   ln.log(Level.INFO, "Validation successful");
  
  if( atRl.isValueUnique() )
  {
   ln = log.branch("Validating attribute values uniquness");
   if( ! checkValuesUnique(atRl, attrs) )
   {
    ln.log(Level.INFO, "Uniqueness validation failed");    
    return atRl.getType() == RestrictionType.MUSTNOT;
   }
   else
    ln.log(Level.INFO, "Uniqueness validation successful");
  }
  
  
  if( atRl.getQualifiers() != null )
  {
   ln = log.branch("Matching qualifiers");
   if( ! matchQualifiers(atRl.getQualifiers(), attrs, ln) )
   {
    ln.log(Level.INFO, "Qualifiers match failed");
    
    return atRl.getType() == RestrictionType.MUSTNOT;
   }
   else
    ln.log(Level.INFO, "Qualifiers matched");
  }
  
  return true;
 }

 private boolean isRelationRuleSatisfied(RelationRule rlRl, AgeObject obj, LogNode log)
 {
  if( rlRl.getType() == RestrictionType.MAY )
  {
   log.log(Level.DEBUG, "MAY rule. Skiping.");
   return true;
  }

  
  Collection<? extends AgeRelation> rels = obj.getRelationsByClass(rlRl.getRelationClass(), true);

  if( rels == null || rels.size() == 0 )
  {
   log.log(Level.DEBUG, "No relations of rule's class");

   return rlRl.getType() == RestrictionType.MUSTNOT;
  }
  
  LogNode ln = log.branch("Validating cardinality");
  if( ! matchCardinality( rlRl, rels.size() ) )
  {
   ln.log(Level.INFO,"Rule "+rlRl.getRuleId()+" cardinality requirement failed. Cardinality: "+rlRl.getCardinalityType().name()+":"+rlRl.getCardinality()
     +" Relations: "+rels.size());

   return rlRl.getType() == RestrictionType.MUSTNOT;
  }
  else
   ln.log(Level.INFO, "Validation successful");
  
//  if( ! checkTargetsUnique(rels) )
//   return rlRl.getType() == RestrictionType.MUSTNOT;
   
  
  if( rlRl.getQualifiers() != null )
  {
   ln = log.branch("Matching qualifiers");
   if( ! matchQualifiers(rlRl.getQualifiers(), rels, ln) )
   {
    ln.log(Level.INFO, "Qualifiers match failed");
    
    return rlRl.getType() == RestrictionType.MUSTNOT;
   }
   else
    ln.log(Level.INFO, "Qualifiers matched");
  }
  
  
  return true;
 }
 
 private boolean matchCardinality( AttributeAttachmentRule rul, int nAttr )
 {
  switch(rul.getCardinalityType())
  {
   case EXACT:
    if(rul.getCardinality() != nAttr)
     return false;

    break;

   case MAX:
    if(rul.getCardinality() < nAttr)
     return false;

    break;

   case MIN:
    if(rul.getCardinality() > nAttr)
     return false;

    break;
    
   default:
  }
 
  return true;
 }
 
 private boolean matchCardinality( RelationRule rul, int nRel)
 {
  boolean res=true;
  
  switch(rul.getCardinalityType())
  {
   case EXACT:
    if(rul.getCardinality() != nRel)
     res=false;

    break;

   case MAX:
    if(rul.getCardinality() < nRel)
     res=false;

    break;

   case MIN:
    if(rul.getCardinality() > nRel)
     res=false;

    break;

   default:
  }
 
  return res;
 }
 
 private boolean checkValuesUnique( AttributeAttachmentRule rul, Collection<? extends AgeAttribute> attrs )
 {
  if(rul.isValueUnique() && attrs.size() > 1)
  {
   ArrayList<AgeAttribute> atList = new ArrayList<AgeAttribute>(attrs.size());
   atList.addAll(attrs);

   for(int i = 0; i < attrs.size() - 1; i++)
   {
    for(int j = i + 1; j < attrs.size(); j++)
    {
     if(atList.get(i).equals(atList.get(j)))
      return false;
    }
   }
  }
  
  return true;
 }
 
 private boolean checkTargetsUnique( Collection<? extends AgeRelation> rels, LogNode log )
 {
  AgeObject dupObj=null;
  
  if(rels.size() > 1 )
  {
   ArrayList<AgeRelation> rlList = new ArrayList<AgeRelation>(rels.size());
   rlList.addAll(rels);

   cyc: for(int i = 0; i < rels.size() - 1; i++)
   {
    for(int j = i + 1; j < rels.size(); j++)
    {
     if(rlList.get(i).getTargetObject().equals(rlList.get(j).getTargetObject()))
     {
      dupObj = rlList.get(i).getTargetObject();
      break cyc;
     }
    }
   }
  }
  
  if( dupObj != null )
  {
   log.log(Level.ERROR, "All target objects must be unique. Object ID: '"+dupObj.getId()+"' OrigID: '"+dupObj.getOriginalId()+"' is duplicated");
   return false;
  }
  else
   return true;
 }
 
 private boolean matchQualifiers( Collection<QualifierRule> qRules, Collection<? extends Attributed> attrs, LogNode log )
 {
  boolean matched = true;

  if(qRules != null)
  {
   rules: for(QualifierRule qr : qRules)
   {
    LogNode ln = log.branch("Validating qualifier rule " + qr.getRuleId());

    for(Attributed attr : attrs)
    {
     boolean found = false;

     Collection< ? extends AgeAttributeClass> clss = attr.getAttributeClasses();

     if(clss != null)
     {
      for(AgeAttributeClass atc : clss)
      {
       if(atc.isClassOrSubclass(qr.getAttributeClass()))
       {
        found = true;
        break;
       }
      }
     }

     if(!found)
     {
      ln.log(Level.INFO, "Rule validation failed. No qualifiers with rule's class");

      matched = false;
      continue;
     }
    }

    if(qr.isUnique())
    {
     ln.log(Level.INFO, "Validating qualifiers' uniqueness");

     ArrayList<Attributed> atList = new ArrayList<Attributed>(attrs.size());
     atList.addAll(attrs);

     for(int i = 0; i < attrs.size() - 1; i++)
     {
      for(int j = i + 1; j < attrs.size(); j++)
      {
       if(isEqual(atList.get(i).getAttributesByClass(qr.getAttributeClass(), true), atList.get(j).getAttributesByClass(qr.getAttributeClass(), true)))
       {
        ln.log(Level.INFO, "Rule validation failed. Qualifiers are not unique");
        matched = false;
        continue rules;
       }
      }
     }
    }

    ln.log(Level.INFO, "Rule validation successful");
   }
  }

  return matched;
 }
 
// private boolean checkAllQualifiers( AgeAttributeClass atCls, Collection<? extends AgeAttribute> attrs )
// {
//  for( AgeAttribute attr : attrs )
//  {
//   Collection<? extends AgeAttributeClass> qClss = attr.getAttributeClasses();
//   
//   if( qClss != null )
//   {
//    for( AgeAttributeClass qCls : qClss )
//    {
//     if( ! isAttributeAllowed(qCls, attr.getAttributesByClass(qCls, true), atCls.getAttributeAttachmentRules() ) )
//      return false;
//    }
//   }
//  }
//  
//  return true;
// }

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
