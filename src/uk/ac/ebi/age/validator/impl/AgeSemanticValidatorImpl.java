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
 public void validate(Submission subm, LogNode log)
 {
  for( AgeObject obj : subm.getObjects() )
  {
   LogNode ln = log.branch("Validating object ID="+obj.getId()+" (OrigId="+obj.getOriginalId()+")");
   if( !validateObject( obj, obj.getAgeElClass(), ln ) )
    ln.log(Level.ERROR, "Object validation failed");
   else
    ln.log(Level.INFO, "Object validation success");
  }
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
  
  valid = validateAttributed( obj, log ) && valid;

  valid = validateRelations( obj, log ) && valid;
  
  return valid;
 }

 private boolean validateRelations(AgeObject obj, LogNode log)
 {
  log = log.branch("Validating object's relations");
  
  AgeClass cls = obj.getAgeElClass();

  Collection<RelationRule> rlRules = cls.getAllRelationRules();

  if( rlRules == null )
   rlRules = Collections.<RelationRule> emptyList();
  
  Collection< ? extends AgeRelationClass> rlClasses = obj.getRelationClasses();

  boolean objectOk = true;

  for(AgeRelationClass rlCls : rlClasses)
  {
   if(rlCls.isCustom())
    continue;

   
   Collection< ? extends AgeRelation> rels = obj.getRelationsByClass(rlCls, true);

   LogNode ln = log.branch("Checking relations of class '"+rlCls.getName()+"' Relations: "+rels.size());

   
   objectOk = checkTargetsUnique( rels, log ) && objectOk; 

   boolean res = isRelationAllowed(rlCls, rels, rlRules, ln);
   
   if( res )
    ln.log(Level.INFO, "Validation successful");
   else
    ln.log(Level.ERROR, "Validation failed");
   
   objectOk = res && objectOk; 
  }

  if(cls.getRelationRules() != null)
  {
   LogNode ln = log.branch("Checking relation rules");

   for(RelationRule rlRl : cls.getRelationRules())
   {
    LogNode rlln = log.branch("Checking rule: "+rlRl.getId()+" of class: '"+cls.getName()+"'");

    boolean res = isRelationRuleSatisfied(rlRl, obj, rlln);
    
    if( res )
     rlln.log(Level.ERROR, "Rule failed");
    else
     rlln.log(Level.ERROR, "Rule satisfied");
    
    objectOk = res && objectOk;
   }
  }

  
  LogNode ln = log.branch("Checking relation attributes");

  for(AgeRelation rl : obj.getRelations())
  {
   LogNode atln = ln.branch("Validating relation's attributes. Relation class: '"+rl.getAgeElClass().getName()
     +"' Target object: "+rl.getTargetObject().getOriginalId());

   objectOk = validateAttributed(rl,0,atln) && objectOk;
  }
  
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
   
   LogNode ln = log.branch("Checking attributes of class '"+atCls.getName()+"' Attributes: "+attrs.size());

   valid = isAttributeAllowed(atCls, attrs, atRules) && valid;
  }
  
  if( cls.getAttributeAttachmentRules() != null )
  {
   LogNode ln = log.branch("Checking attribute rules");

   for( AttributeAttachmentRule atRl : cls.getAttributeAttachmentRules() )
    isAttributeRuleSatisfied(atRl,obj);
  }
  
  
  for( AgeAttribute attr : obj.getAttributes() )
  {
   if( ! validateAttributed(attr,level+1,log) )
   {
    objectOk=false;
    break;
   }
  }
  

  return valid;
 }
 
 private boolean isAttributeAllowed(AgeAttributeClass atCls, Collection<? extends AgeAttribute> attrs, Collection<AttributeAttachmentRule> atRules)
 {
  if( atRules == null )
   return false;
  
  boolean satisf = false;

  for(AttributeAttachmentRule rul : atRules)
  {
   if(!((rul.isSubclassesIncluded() && atCls.isClassOrSubclass(rul.getAttributeClass())) || rul.getAttributeClass().equals(atCls)))
    continue;

   if(rul.getType() == RestrictionType.MUSTNOT)
    continue;

   if( ! matchCardinality( rul, attrs.size() ) )
   {
    log.log(Level.ERROR,"Rule "+rul.getId()+" cardinality requirement failed. Cardinality: "+rul.getCardinalityType().name()+":"+rul.getCardinality()+" Relations: "+nRel);
    continue;
   }
   
   if( ! checkValuesUnique( rul, attrs ) )
    continue;
  
   if( ! matchQualifiers(rul.getQualifiers(), attrs))
    continue;
   
  
   satisf = true;
   break;
   
  }

  return satisf;
 }
 
 private boolean isRelationAllowed(AgeRelationClass atCls, Collection<? extends AgeRelation> rels, Collection<RelationRule> relRules, LogNode log)
 {
  if( relRules == null )
   return false;
  
  log.log(Level.INFO, "Checking whether relations are allowed by some rule");
  
  boolean satisf = false;

  for(RelationRule rul : relRules)
  {
   if(!((rul.isSubclassesIncluded() && atCls.isClassOrSubclass(rul.getRelationClass())) || rul.getRelationClass().equals(atCls)))
   {
    log.log(Level.DEBUG, "Rule "+rul.getId()+" doesn't match class '"+atCls.getName()+"'. Skiping.");
    continue;
   }
   
   if(rul.getType() == RestrictionType.MUSTNOT)
    continue;

   if( ! matchCardinality( rul, rels.size(), log ) )
   {
    log.log(Level.INFO,"Rule "+rul.getId()+" cardinality requirement failed. Cardinality: "+rul.getCardinalityType().name()+":"+rul.getCardinality()
      +" Relations: "+rels.size()+". Skiping rule.");
    continue;
   }
   
  
   if( ! matchQualifiers(rul.getQualifiers(), rels, log))
   {
    log.log(Level.INFO,"Rule's "+rul.getId()+" qualifier rules failed. Skiping rule");

    continue;
   }
  
   log.log(Level.INFO,"Relations of class '"+atCls.getName()+"' are allowed by rule "+rul.getId());

   
   satisf = true;
   break;
   
  }

  return satisf;
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
 
 private boolean isAttributeRuleSatisfied(AttributeAttachmentRule atRl, Attributed obj)
 {
  if( atRl.getType() == RestrictionType.MAY )
   return true;
  
  Collection<? extends AgeAttribute> attrs = obj.getAttributesByClass(atRl.getAttributeClass(), true);

  if( attrs == null || attrs.size() == 0 )
   return atRl.getType() == RestrictionType.MUSTNOT;

  
  if( ! matchCardinality( atRl, attrs.size() ) )
   return atRl.getType() == RestrictionType.MUSTNOT;

  if( atRl.isValueUnique() && ! checkValuesUnique(atRl, attrs) )
   return atRl.getType() == RestrictionType.MUSTNOT;
   
  if( ! matchQualifiers(atRl.getQualifiers(), attrs) )
   return atRl.getType() == RestrictionType.MUSTNOT;
  
  
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
  
  LogNode ln = log.branch("Checking cardinality");
  if( ! matchCardinality( rlRl, rels.size(), ln ) )
  {
   ln.log(Level.INFO,"Rule "+rlRl.getId()+" cardinality requirement failed. Cardinality: "+rlRl.getCardinalityType().name()+":"+rlRl.getCardinality()
     +" Relations: "+rels.size());

   return rlRl.getType() == RestrictionType.MUSTNOT;
  }
  
//  if( ! checkTargetsUnique(rels) )
//   return rlRl.getType() == RestrictionType.MUSTNOT;
   
  ln = log.branch("Matching qualifiers");
  if( ! matchQualifiers(rlRl.getQualifiers(), rels, ln) )
  {
   ln.log(Level.INFO, "Qualifiers match failed");
   
   return rlRl.getType() == RestrictionType.MUSTNOT;
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
  }
 
  return true;
 }
 
 private boolean matchCardinality( RelationRule rul, int nRel, LogNode log )
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
  if(qRules != null)
  {
   for(QualifierRule qr : qRules)
   {
    LogNode ln = log.branch("Checking qualifier rule "+qr.getID());
    
    for(Attributed attr : attrs)
    {
     boolean found = false;

     Collection<? extends AgeAttributeClass> clss = attr.getAttributeClasses();
     
     if( clss != null )
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
      ln.log(Level.INFO, "Rule check failed. No qualifiers");
      
      return false;
     }
    }

    if(qr.isUnique())
    {
     ln.log(Level.INFO, "Checking qualifiers' uniqueness");
     
     ArrayList<Attributed> atList = new ArrayList<Attributed>(attrs.size());
     atList.addAll(attrs);

     for(int i = 0; i < attrs.size() - 1; i++)
     {
      for(int j = i + 1; j < attrs.size(); j++)
      {
       if( isEqual(atList.get(i).getAttributesByClass(qr.getAttributeClass(), true), atList.get(j).getAttributesByClass(qr.getAttributeClass(),true)) )
       {
        ln.log(Level.INFO, "Rule check failed. Qualifiers are not unique");
        return false;
       }
      }
     }
    }
    
    ln.log(Level.INFO, "Rule check successful");
   }
  }
  
  return true;
 }
 
 private boolean checkAllQualifiers( AgeAttributeClass atCls, Collection<? extends AgeAttribute> attrs )
 {
  for( AgeAttribute attr : attrs )
  {
   Collection<? extends AgeAttributeClass> qClss = attr.getAttributeClasses();
   
   if( qClss != null )
   {
    for( AgeAttributeClass qCls : qClss )
    {
     if( ! isAttributeAllowed(qCls, attr.getAttributesByClass(qCls, true), atCls.getAttributeAttachmentRules() ) )
      return false;
    }
   }
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
