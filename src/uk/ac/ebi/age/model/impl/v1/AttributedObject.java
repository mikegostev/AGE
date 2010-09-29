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

import com.pri.util.collection.CollectionsUnion;

public abstract class AttributedObject extends AgeSemanticElementImpl implements AttributedWritable
{

 private static final long serialVersionUID = 1L;

 private Map<String,List<AgeAttributeWritable>> attributes = new HashMap<String,List<AgeAttributeWritable>>();

 private transient List<AgeAttributeClass> atClasses = null;

 
 public AttributedObject(SemanticModel m)
 {
  super(m);
 }

 
 @Override
 public Collection<AgeAttributeWritable> getAttributes()
 {
  return new CollectionsUnion<AgeAttributeWritable>(attributes.values());
 }
 
 @Override
 public void addAttribute(AgeAttributeWritable attr)
 {
  List<AgeAttributeWritable> coll = attributes.get(attr.getAgeElClass().getId());
  
  if( coll == null )
  {
   attributes.put(attr.getAgeElClass().getId(),Collections.singletonList(attr));
  
   if( atClasses != null )
    atClasses.add(e);
  }
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

 @Override
 public void removeAttribute(AgeAttributeWritable attr)
 {
  Collection<AgeAttributeWritable> coll = attributes.get(attr.getAgeElClass().getId());
  
  if( coll != null )
   coll.remove(attr);
 }

 @Override
 public Collection< ? extends AgeAttributeWritable> getAttributes(AgeAttributeClass cls)
 {
  return attributes.get(cls.getId());
 }

 
 @Override
 public Collection<String> getAttributeClassesIds()
 {
  return attributes.keySet();
 }

 @Override
 public Collection<? extends AgeAttributeWritable> getAttributesByClassId(String cid, boolean wSubCls)
 {
  if( ! wSubCls )
   return attributes.get(cid);
 }

 @Override
 public Collection< ? extends AgeAttributeWritable> getAttributesByClass(AgeAttributeClass cls, boolean wSubCls)
 {
  return attributes.get(cls.getId());
 }

 @Override
 public Collection<? extends AgeAttributeClass> getAttributeClasses()
 {
  Collection<AgeAttributeClass> clsz = new ArrayList<AgeAttributeClass>( attributes.size() );
  
  for( List<AgeAttributeWritable> alLst : attributes.values() )
   if( alLst.size() > 0 )
    clsz.add(alLst.get(0).getAgeElClass());
  
  return clsz;
 }
}
