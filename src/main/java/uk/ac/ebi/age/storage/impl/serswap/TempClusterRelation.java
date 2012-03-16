package uk.ac.ebi.age.storage.impl.serswap;

import java.util.Collection;

import uk.ac.ebi.age.model.AgeAttribute;
import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.AttributeClassRef;
import uk.ac.ebi.age.model.AttributedClass;
import uk.ac.ebi.age.model.ContextSemanticModel;
import uk.ac.ebi.age.model.writable.AgeAttributeWritable;
import uk.ac.ebi.age.model.writable.AgeExternalRelationWritable;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;
import uk.ac.ebi.age.model.writable.AgeRelationWritable;

public class TempClusterRelation implements AgeExternalRelationWritable
{
 private AgeExternalRelationWritable origRel;
 private String clusterId;

 public TempClusterRelation(AgeExternalRelationWritable rel, String cid )
 {
  origRel = rel;
  clusterId = cid;
 }

 public Collection< ? extends AgeAttributeWritable> getAttributes()
 {
  return origRel.getAttributes();
 }

 public String getTargetObjectId()
 {
  return origRel.getTargetObjectId();
 }

 public AgeObjectWritable getSourceObject()
 {
  return origRel.getSourceObject();
 }

 public ContextSemanticModel getSemanticModel()
 {
  return origRel.getSemanticModel();
 }

 public String getId()
 {
  return origRel.getId();
 }

 public AttributedClass getAttributedClass()
 {
  return origRel.getAttributedClass();
 }

 public AgeObjectWritable getTargetObject()
 {
  return origRel.getTargetObject();
 }

 public int getOrder()
 {
  return origRel.getOrder();
 }

 public AgeRelationClass getAgeElClass()
 {
  return origRel.getAgeElClass();
 }

 public boolean isTargetGlobal()
 {
  return origRel.isTargetGlobal();
 }

 public void setSourceObject(AgeObjectWritable ageObject)
 {
  origRel.setSourceObject(ageObject);
 }

 public void setInferred(boolean inf)
 {
  origRel.setInferred(inf);
 }

 public AgeAttribute getAttribute(AgeAttributeClass cls)
 {
  return origRel.getAttribute(cls);
 }

 public void setTargetObject(AgeObjectWritable obj)
 {
  origRel.setTargetObject(obj);
 }

 public boolean isInferred()
 {
  return origRel.isInferred();
 }

 public AgeAttributeWritable createAgeAttribute(AttributeClassRef attrClass)
 {
  return origRel.createAgeAttribute(attrClass);
 }

 public AgeRelationWritable createClone(AgeObjectWritable host)
 {
  return origRel.createClone(host);
 }

 public Collection< ? extends AgeAttributeWritable> getAttributesByClass(AgeAttributeClass cls, boolean wSubCls)
 {
  return origRel.getAttributesByClass(cls, wSubCls);
 }

 public AgeExternalRelationWritable getInverseRelation()
 {
  return origRel.getInverseRelation();
 }

 public void setInverseRelation(AgeExternalRelationWritable inrv)
 {
  origRel.setInverseRelation(inrv);
 }

 public AgeAttributeWritable createExternalObjectAttribute(AttributeClassRef attrClass, String val)
 {
  return origRel.createExternalObjectAttribute(attrClass, val);
 }

 public void setTargetGlobal(boolean glb)
 {
  origRel.setTargetGlobal(glb);
 }

 public Collection< ? extends AgeAttributeClass> getAttributeClasses()
 {
  return origRel.getAttributeClasses();
 }

 public void setInverseRelation(AgeRelationWritable invRl)
 {
  origRel.setInverseRelation(invRl);
 }

 public void addAttribute(AgeAttributeWritable attr)
 {
  origRel.addAttribute(attr);
 }

 public void removeAttribute(AgeAttributeWritable attr)
 {
  origRel.removeAttribute(attr);
 }

 public void reset()
 {
  origRel.reset();
 }

 public void sortAttributes()
 {
  origRel.sortAttributes();
 }

 public void pack()
 {
  origRel.pack();
 }

 public String getClusterId()
 {
  return clusterId;
 }

 public void setClusterId(String clusterId)
 {
  this.clusterId = clusterId;
 }
}
