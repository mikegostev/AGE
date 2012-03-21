package uk.ac.ebi.age.storage.impl.serswap;

import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.IdScope;
import uk.ac.ebi.age.model.writable.AgeExternalRelationWritable;

class ExtRelInfo
{
 private IdScope sourceObjectScope;
 private String targetId;
 private boolean targetGlobal;
 private AgeRelationClass relationClass;
 private String customClassName; 

 public ExtRelInfo(AgeExternalRelationWritable rel)
 {
  targetId = rel.getTargetObjectId();
  targetGlobal = rel.isTargetGlobal();
  
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

 public boolean isTargetGlobal()
 {
  return targetGlobal;
 }

 public void setTargetGlobal(boolean targetGlobal)
 {
  this.targetGlobal = targetGlobal;
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
