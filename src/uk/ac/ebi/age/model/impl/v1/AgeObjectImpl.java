package uk.ac.ebi.age.model.impl.v1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import uk.ac.ebi.age.model.AgeAttribute;
import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.AgeClassPlug;
import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.AttributedClass;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.Submission;
import uk.ac.ebi.age.model.writable.AgeAttributeWritable;
import uk.ac.ebi.age.model.writable.AgeExternalRelationWritable;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;
import uk.ac.ebi.age.model.writable.AgeRelationWritable;

class AgeObjectImpl extends AttributedObject implements Serializable, AgeObjectWritable
{
 private static final long serialVersionUID = 1L;

 private List<AgeRelationWritable> relations;
 
 private transient Map<AgeRelationClass, List<AgeRelationWritable>> relationMap;
 
// private Map<String, List<AgeRelationWritable>> relations = new HashMap<String, List<AgeRelationWritable>>();

// private Collection<AgeAttributeWritable> attributes = new ArrayList<AgeAttributeWritable>( 10 );
// private Collection<Collection<AgeRelationWritable>> relations = new ArrayList<Collection<AgeRelationWritable>>(5);

 private AgeClassPlug ageClassPlug;

 private String id;
 private String origId;

 private Submission subm;
 
 private int order;
 
 public AgeObjectImpl(String id, AgeClass cls, SemanticModel sm)
 {
  super(sm);

  this.id=id;
  this.origId=id;

  ageClassPlug= sm.getAgeClassPlug(cls);
 }

 public String getId()
 {
  return id;
 }
 
 public String getOriginalId()
 {
  return origId;
 }

 public void setId(String nId)
 {
  id=nId;
 }
 
 @Override
 public void setOriginalId(String nId)
 {
  origId=nId;
 }

 
 public void addRelation(AgeRelationWritable rl)
 {
  
  if( relations == null )
   relations = new ArrayList<AgeRelationWritable>(5);
  
  relations.add(rl);
  
  if( relationMap != null )
   addRelToMap(rl);
 }


 private Map<AgeRelationClass,List<AgeRelationWritable>> getRelMap()
 {
  if( relationMap != null )
   return relationMap;
  
  relationMap = new HashMap<AgeRelationClass, List<AgeRelationWritable>>();
  
  if( relations != null )
  {
   for( AgeRelationWritable attr : relations )
    addRelToMap(attr);
  }
  
  return relationMap;
 }

 private void addRelToMap( AgeRelationWritable rl )
 {
  List<AgeRelationWritable> coll = relationMap.get(rl.getAgeElClass());
  
  if( coll == null )
   relationMap.put(rl.getAgeElClass(),Collections.singletonList(rl));
  else if( coll instanceof ArrayList<?> )
   coll.add(rl);
  else
  {
   ArrayList<AgeRelationWritable> nc = new ArrayList<AgeRelationWritable>(3);
   nc.addAll(coll);
   nc.add(rl);
   
   relationMap.put(rl.getAgeElClass(),nc);
  }
 }
 
 public AgeClass getAgeElClass()
 {
  return ageClassPlug.getAgeClass();
 }



// public Map<AgeAttributeClass, Collection<AgeAttribute> > getAttributeMap()
// {
//  Map< AgeAttributeClass, Collection<AgeAttribute> > map = new HashMap<AgeAttributeClass, Collection<AgeAttribute>>();
//  
//  for( List<? extends AgeAttribute> vals : attributes.values() )
//   map.put(vals.get(0).getAgeElClass(), vals);
//  
//  return map;
// }
 
 public Collection<AgeRelationWritable> getRelations()
 {
  return relations;
 }

 

 @Override
 public Collection< ? extends AgeRelationWritable> getRelationsByClass(AgeRelationClass cls, boolean wSubCls)
 {
  Map<AgeRelationClass,List<AgeRelationWritable>> map = getRelMap();
  
  if( ! wSubCls )
   return map.get(cls);
  
  List< AgeRelationWritable > lst = new ArrayList<AgeRelationWritable>();
  
  for( Map.Entry<AgeRelationClass,List<AgeRelationWritable>> me : map.entrySet() )
  {
   if( me.getKey().isClassOrSubclass(cls) )
    lst.addAll(me.getValue());
  }
  
  return lst;
 }

 
// @Override
// public Collection<String> getRelationClassesIds()
// {
//  return relations.keySet();
// }
//
// @Override
// public Collection< ? extends AgeRelationWritable> getRelationsByClassId(String cid)
// {
//  return relations.get(cid);
// }
 
 @Override
 public Collection< ? extends AgeRelationClass> getRelationClasses()
 {
  return getRelMap().keySet();
 }

 public AgeAttributeWritable createAgeAttribute(AgeAttributeClass attrClass)
 {
  AgeAttributeWritable attr = getSemanticModel().createAgeAttribute( attrClass );

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

 @Override
 public AttributedClass getAttributedClass()
 {
  return getAgeElClass();
 }

 @Override
 public void reset()
 {
  super.reset();
 
  relationMap=null;
 }
 
}
