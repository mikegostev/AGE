package uk.ac.ebi.age.storage.impl.serswap;

import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.IdScope;
import uk.ac.ebi.age.model.ResolveScope;
import uk.ac.ebi.age.model.writable.AgeExternalRelationWritable;

class ExtRelInfo
{
 private IdScope sourceObjectScope;
 private String targetId;
 private ResolveScope scope;
 private AgeRelationClass relationClass;
 private String customClassName; 

 public ExtRelInfo(AgeExternalRelationWritable rel)
 {
  targetId = rel.getTargetObjectId();
  scope = rel.getTargetResolveScope();
  
  if( ! rel.getAgeElClass().isCustom() )
   relationClass = rel.getAgeElClass();
  else
   customClassName = rel.getAgeElClass().getName();
  
  sourceObjectScope = rel.getSourceObject().getIdScope();
 }

 public String getTargetId()
 {
  return targetId;
 }

 public void setTargetId(String targetId)
 {
  this.targetId = targetId;
 }

 public ResolveScope getTargetResolveScope()
 {
  return scope;
 }

 public void setTargetResolveScope(ResolveScope sc)
 {
  this.scope = sc;
 }

 public AgeRelationClass getRelationClass()
 {
  return relationClass;
 }

 public String getCustomClassName()
 {
  return customClassName;
 }

 public IdScope getSourceObjectScope()
 {
  return sourceObjectScope;
 }

}
