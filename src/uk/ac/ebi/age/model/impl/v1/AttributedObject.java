package uk.ac.ebi.age.model.impl.v1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.writable.AgeAttributeWritable;
import uk.ac.ebi.age.model.writable.AttributedWritable;

public abstract class AttributedObject extends AgeSemanticElementImpl implements AttributedWritable
{

 private static final long serialVersionUID = 2L;

 private List<AgeAttributeWritable> attributes;
 
 private transient Map<AgeAttributeClass,List<AgeAttributeWritable>> attribMap; // = new HashMap<String,List<AgeAttributeWritable>>();

// private transient List<AgeAttributeClass> atClasses = null;

 
 public AttributedObject(SemanticModel m)
 {
  super(m);
 }

 
 @Override
 public AgeAttributeWritable createAgeAttribute(AgeAttributeClass attrClass)
 {
  AgeAttributeWritable attr = getSemanticModel().createAgeAttribute(attrClass);
  
  addAttribute(attr);
  
  return attr;
 }
 
 @Override
 public AgeAttributeWritable createExternalObjectAttribute(String val, AgeAttributeClass attrClass)
 {
  AgeAttributeWritable attr = getSemanticModel().createExternalObjectAttribute(attrClass, val);
  
  addAttribute(attr);
  
  return attr;
 }

 
 @Override
 public Collection<AgeAttributeWritable> getAttributes()
 {
  return attributes;
 }
 
 @Override
 public void addAttribute(AgeAttributeWritable attr)
 {
  if( attributes == null )
   attributes = new ArrayList<AgeAttributeWritable>(15);
  
  attributes.add(attr);
  
  if( attribMap != null )
   addAttribToMap(attr);
 }

 @Override
 public void removeAttribute(AgeAttributeWritable attr)
 {
  if( attributes == null )
   return;
  
  if( ! attributes.remove(attr) )
   return;
  
  if( attribMap == null )
   return;
  
  Collection<AgeAttributeWritable> coll = attribMap.get(attr.getAgeElClass());
  
  if( coll == null )
   return;
  
  coll.remove(attr);
  
  if( coll.size() == 0 )
   attribMap.remove(attr.getAgeElClass());
 }

 @Override
 public Collection< ? extends AgeAttributeWritable> getAttributes(AgeAttributeClass cls)
 {
  return getAttribMap().get(cls.getId());
 }

 
 private Map<AgeAttributeClass,List<AgeAttributeWritable>> getAttribMap()
 {
  if( attribMap != null )
   return attribMap;
  
  attribMap = new HashMap<AgeAttributeClass, List<AgeAttributeWritable>>();
  
  for( AgeAttributeWritable attr : attributes )
   addAttribToMap(attr);
  
  return attribMap;
 }

 private void addAttribToMap( AgeAttributeWritable attr )
 {
  List<AgeAttributeWritable> coll = attribMap.get(attr.getAgeElClass());
  
  if( coll == null )
   attribMap.put(attr.getAgeElClass(),Collections.singletonList(attr));
  else if( coll instanceof ArrayList<?> )
   coll.add(attr);
  else
  {
   ArrayList<AgeAttributeWritable> nc = new ArrayList<AgeAttributeWritable>(3);
   nc.addAll(coll);
   nc.add(attr);
   
   attribMap.put(attr.getAgeElClass(),nc);
  }
 }

// @Override
// public Collection<String> getAttributeClassesIds()
// {
//  return attributes.keySet();
// }

// @Override
// public Collection<? extends AgeAttributeWritable> getAttributesByClassId(String cid, boolean wSubCls)
// {
//  if( ! wSubCls )
//   return getAttribMap().get(cid);
// }

 @Override
 public Collection< ? extends AgeAttributeWritable> getAttributesByClass(AgeAttributeClass cls, boolean wSubCls)
 {
  Map<AgeAttributeClass,List<AgeAttributeWritable>> map = getAttribMap();
  
  if( ! wSubCls )
   return map.get(cls);
  
  List< AgeAttributeWritable > lst = new ArrayList<AgeAttributeWritable>();
  
  for( Map.Entry<AgeAttributeClass,List<AgeAttributeWritable>> me : map.entrySet() )
  {
   if( me.getKey().isClassOrSubclass(cls) )
    lst.addAll(me.getValue());
  }
  
  return lst;
 }

 @Override
 public Collection<? extends AgeAttributeClass> getAttributeClasses()
 {
  return getAttribMap().keySet();
 }

 @Override
 public void reset()
 {
  attribMap=null;
 }
}
