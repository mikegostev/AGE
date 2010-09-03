package uk.ac.ebi.age.model.impl.v1;

import java.io.Serializable;
import java.util.Collection;

import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.AgeRestriction;
import uk.ac.ebi.age.model.AttributeAttachmentRule;
import uk.ac.ebi.age.model.RelationRule;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.service.IdGenerator;

class CustomAgeClassImpl extends AgeAbstractClassImpl implements AgeClass, Serializable 
{
 private static final long serialVersionUID = 1L;
 

 private String name;
 private String id;
 
 private String idPrefix;

 
 public CustomAgeClassImpl(String name, String pfx, SemanticModel sm)
 {
  super( sm );
  this.name=name;
  
  if( pfx == null )
   idPrefix = name.substring(0,1);
  else
   idPrefix=pfx;
  
  id = "AgeClass"+IdGenerator.getInstance().getStringId();
 }

 public String getId()
 {
  return id;
 }

 public void setId(String id)
 {
  this.id = id;
 }
 
  public Collection<AgeRestriction> getRestrictions()
 {
  return null;
 }
 

 public Collection<AgeRestriction> getAllRestrictions()
 {
  return null;
 }

 
 public Collection<AgeClass> getSuperClasses()
 {
  return null;
 }
 
 public Collection<AgeClass> getSubClasses()
 {
  return null;
 }

 public String getName()
 {
  return name;
 }

 public boolean isCustom()
 {
  return true;
 }

 public Collection<AgeRestriction> getObjectRestrictions()
 {
  return null;
 }


 public Collection<AgeRestriction> getAllObjectRestrictions()
 {
  return null;
 }

 
 public Collection<AgeRestriction> getAttributeRestrictions()
 {
  return null;
 }
 
 public Collection<AgeRestriction> getAttributeAllRestrictions()
 {
  return null;
 }

 public String getIdPrefix()
 {
  return idPrefix;
 }

 @Override
 public boolean isAbstract()
 {
  return false;
 }

 @Override
 public Collection<RelationRule> getRelationRules()
 {
  return null;
 }

 @Override
 public Collection<RelationRule> getAllRelationRules()
 {
  return null;
 }

 @Override
 public Collection<AttributeAttachmentRule> getAttributeAttachmentRules()
 {
  return null;
 }

 @Override
 public Collection<AttributeAttachmentRule> getAllAttributeAttachmentRules()
 {
  return null;
 }

}
