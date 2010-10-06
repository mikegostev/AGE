package uk.ac.ebi.age.model.impl.v1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.AgeSemanticElement;
import uk.ac.ebi.age.model.Cardinality;
import uk.ac.ebi.age.model.QualifierRule;
import uk.ac.ebi.age.model.RestrictionType;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.writable.RelationRuleWritable;


public class RelationRuleImpl implements Serializable, RelationRuleWritable, AgeSemanticElement
{
 private static final long serialVersionUID = 1L;

 private RestrictionType type = RestrictionType.MAY;
 private Cardinality cardType = Cardinality.ANY;
 private AgeRelationClass relationClass;
 private AgeClass targetClass;
 private int cardinality=1;
 private Collection<QualifierRule> qualifiers;
 private boolean subClassesIncludedRel=true;
 private boolean subclassesIncludedTarg=true;

 private SemanticModel model;

 private int id;

 public RelationRuleImpl( RestrictionType typ, SemanticModel mod )
 {
  type=typ;
  model = mod;
 }
 

 public RestrictionType getType()
 {
  return type;
 }

 public void setType(RestrictionType type)
 {
  this.type = type;
 }

 public Cardinality getCardinalityType()
 {
  return cardType;
 }

 public void setCardinalityType(Cardinality cardType)
 {
  this.cardType = cardType;
 }

 public int getCardinality()
 {
  return cardinality;
 }

 public void setCardinality(int cardinality)
 {
  this.cardinality = cardinality;
 }

 public Collection<QualifierRule> getQualifiers()
 {
  return qualifiers;
 }

 public void addQualifier( QualifierRule qr )
 {
  if( qualifiers == null )
   qualifiers=new ArrayList<QualifierRule>();

  qualifiers.add(qr);
 
 }
 


 public void clearQualifiers()
 {
  if( qualifiers != null )
   qualifiers.clear();
 }


 public boolean isSubclassesIncluded()
 {
  return subclassesIncluded;
 }

 public void setSubclassesIncluded(boolean subclassesIncluded)
 {
  this.subclassesIncluded = subclassesIncluded;
 }

 @Override
 public RestrictionType getRestrictionType()
 {
  return type;
 }

 @Override
 public SemanticModel getSemanticModel()
 {
  return model;
 }

 @Override
 public int getRuleId()
 {
  return id;
 }
 
 @Override
 public void setRuleId( int id )
 {
  this.id=id;
 }

 @Override
 public String getId()
 {
  return null;
 }



 @Override
 public boolean isRelationSubclassesIncluded()
 {
  // TODO Auto-generated method stub
  return false;
 }


 @Override
 public AgeRelationClass getRelationClass()
 {
  // TODO Auto-generated method stub
  return null;
 }


 @Override
 public AgeClass getTargetClass()
 {
  // TODO Auto-generated method stub
  return null;
 }


 @Override
 public void setRelationSubclassesIncluded(boolean relationSubclassesIncluded)
 {
  // TODO Auto-generated method stub
  
 }


 @Override
 public void setRelationClass(AgeRelationClass ageRelationClass)
 {
  // TODO Auto-generated method stub
  
 }


 @Override
 public void setTargetClass(AgeClass ageClass)
 {
  // TODO Auto-generated method stub
  
 }



}
