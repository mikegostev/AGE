package uk.ac.ebi.age.model.impl.v1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import uk.ac.ebi.age.model.AgeAttribute;
import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.AgeClassPlug;
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
 private static final long serialVersionUID = 1L;

 private Map<String,Collection<AgeAttributeWritable>> attributes = new HashMap<String,Collection<AgeAttributeWritable>>();
 private Map<String, Collection<AgeRelationWritable>> relations = new HashMap<String, Collection<AgeRelationWritable>>();

// private Collection<AgeAttributeWritable> attributes = new ArrayList<AgeAttributeWritable>( 10 );
// private Collection<Collection<AgeRelationWritable>> relations = new ArrayList<Collection<AgeRelationWritable>>(5);

 private AgeClassPlug ageClassPlug;
 private String id;

 private Submission subm;
 
 private int order;
 
 public AgeObjectImpl(String id, AgeClass cls, SemanticModel sm)
 {
  super(sm);
  this.id=id;
  ageClassPlug= sm.getAgeClassPlug(cls);
 }

 public String getId()
 {
  return id;
 }
 
 public void addAttribute(AgeAttributeWritable attr)
 {
  Collection<AgeAttributeWritable> coll = attributes.get(attr.getAgeElClass().getId());
  
  if( coll == null )
   attributes.put(attr.getAgeElClass().getId(),Collections.singleton(attr));
  else if( coll instanceof ArrayList<?> )
   coll.add(attr);
  else
  {
   ArrayList<AgeAttributeWritable> nc = new ArrayList<AgeAttributeWritable>(3);
   nc.addAll(coll);
   nc.add(attr);

   attributes.put(attr.getAgeElClass().getId(),nc);
  }
 }

 public void addRelation(AgeRelationWritable rl)
 {
  Collection<AgeRelationWritable> coll = relations.get(rl.getAgeElClass().getId());
  
  if( coll == null )
   relations.put(rl.getAgeElClass().getId(),Collections.singleton(rl));
  else if( coll instanceof ArrayList<?> )
   coll.add(rl);
  else
  {
   ArrayList<AgeRelationWritable> nc = new ArrayList<AgeRelationWritable>(3);
   nc.addAll(coll);
   nc.add(rl);

   relations.put(rl.getAgeElClass().getId(),nc);
  }

 }

 public AgeClass getAgeElClass()
 {
  return ageClassPlug.getAgeClass();
 }


 public Collection<AgeAttributeWritable> getAttributes()
 {
  return new CollectionsUnion<AgeAttributeWritable>(attributes.values());
 }

 public Collection<AgeRelationWritable> getRelations()
 {
  return new CollectionsUnion<AgeRelationWritable>(relations.values());
 }

 public AgeAttributeWritable createAgeAttribute(AgeAttributeClass attrClass)
 {
  AgeAttributeWritable attr = getSemanticModel().createAgeAttribute(this, attrClass);

  addAttribute(attr);
  
  return attr;
 }
 
 public AgeAttributeWritable createAgeAttribute(AgeAttributeClass attrClass, String param)
 {
  AgeAttributeWritable attr = getSemanticModel().createAgeAttribute(this, attrClass, param);

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



// @Override
// public void resetModel()
// {
//  ageClassPlug.unplug();
//
//  for( Collection<AgeAttributeWritable> attrc : attributes.values() )
//  {
//   for( AgeAttributeWritable at : attrc )
//    at.resetModel();
//  }
//  
//  for( Collection<AgeRelationWritable> relColl : relations.values() )
//  {
//   for( AgeRelationWritable rel : relColl )
//    rel.resetModel();
//  }
//
// }

 @Override
 public Collection< ? extends AgeAttributeWritable> getAttributes(AgeAttributeClass cls)
 {
  return attributes.get(cls.getId());
 }

 @Override
 public Collection< ? extends AgeRelationWritable> getRelations(AgeRelationClass cls)
 {
  return relations.get(cls.getId());
 }

 @Override
 public Object getAttributeValue(AgeAttributeClass cls)
 {
  Collection<? extends AgeAttribute> atVals = getAttributes(cls);
  
  if( atVals == null || atVals.size() == 0 )
   return null;
  
   Iterator<? extends AgeAttribute> it = atVals.iterator();
   if( it.hasNext() )
    return it.next().getValue();

  return null;
 }

}
