package uk.ac.ebi.age.model.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import uk.ac.ebi.age.model.AgeAttribute;
import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.Submission;
import uk.ac.ebi.age.model.writable.AgeAttributeWritable;
import uk.ac.ebi.age.model.writable.AgeExternalRelationWritable;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;
import uk.ac.ebi.age.model.writable.AgeRelationWritable;

import com.pri.util.collection.CollectionsUnion;

class AgeObjectImpl extends AgeSemanticElementImpl implements Serializable, AgeObjectWritable
{
 private Map<AgeAttributeClass,AgeAttributeWritable> attributes = new HashMap<AgeAttributeClass,AgeAttributeWritable>();
 private Map<AgeRelationClass, Collection<AgeRelationWritable>> relations = new HashMap<AgeRelationClass, Collection<AgeRelationWritable>>();

 private AgeClassPlugPluggable ageClassPlug;
 private String id;

 private Submission subm;
 
 private int order;
 
 public AgeObjectImpl(String id, AgeClass cls, SemanticModel sm)
 {
  super(sm);
  this.id=id;
  ageClassPlug= new AgeClassPlugPluggable(cls, sm);
 }

 public String getId()
 {
  return id;
 }
 
 public void addAttribute(AgeAttributeWritable attr)
 {
  attributes.put(attr.getAgeElClass(), attr);
 }

 public void addRelation(AgeRelationWritable rl)
 {
  Collection<AgeRelationWritable> rels = relations.get(rl.getRelationClass());
  
  if( rels == null )
  {
   rels = new LinkedList<AgeRelationWritable>();
   relations.put(rl.getRelationClass(),rels);
  }
  
  rels.add(rl);
 }

 public AgeClass getAgeElClass()
 {
  return ageClassPlug.getAgeClass();
 }

 public AgeAttribute getAttribute(AgeAttributeClass attrCls)
 {
  return attributes.get(attrCls);
 }

 public Collection<AgeAttributeWritable> getAttributes()
 {
  return attributes.values();
 }

 public Collection<AgeRelationWritable> getRelations()
 {
  return new CollectionsUnion<AgeRelationWritable>(relations.values());
 }

 public Map<AgeRelationClass, Collection<AgeRelationWritable>> getRelationsMap()
 {
  return relations;
 }

 public AgeAttributeWritable createAgeAttribute(AgeAttributeClass attrClass)
 {
  AgeAttributeWritable attr = getSemanticModel().createAgeAttribute(this, attrClass);

  addAttribute(attr);
  
  return attr;
 }

 public AgeExternalRelationWritable createExternalRelation(String val, AgeRelationClass relClass)
 {
  AgeExternalRelationWritable rel = getSemanticModel().createExternalRelation(this, val, relClass);
  
  addRelation(rel);
  
  return rel;
 }

 public AgeRelationWritable createRelation(AgeObjectWritable targetObj, AgeRelationClass relClass)
 {
  AgeRelationWritable rel = getSemanticModel().createAgeRelation(targetObj, relClass);
  
  addRelation(rel);
  
  return rel;
 }

 public int getOrder()
 {
  return order;
 }

 public void setOrder(int order)
 {
  this.order = order;
 }

 public Submission getSubmission()
 {
  return subm;
 }

 public void setSubmission(Submission subm)
 {
  this.subm = subm;
 }

 public void changeModel( SemanticModel newModel )
 {
  
 }

 @Override
 public void resetModel()
 {
  ageClassPlug.unplug();

  for( AgeAttributeWritable attr : attributes.values() )
  {
   attr.resetModel();
  }
  
  for( Collection<AgeRelationWritable> relColl : relations.values() )
  {
   for( AgeRelationWritable rel : relColl )
    rel.resetModel();
  }

 }

}
