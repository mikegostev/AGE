package uk.ac.ebi.age.storage.impl.serswap.v3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;
import uk.ac.ebi.age.model.writable.AgeRelationWritable;
import uk.ac.ebi.age.storage.ModuleKey;
import uk.ac.ebi.age.storage.impl.serswap.SerializedSwapStorage;

import com.pri.util.collection.CollectionsUnion;

public class AgeObjectLinkedProxy extends AgeObjectProxy
{
 private Collection< AgeRelationWritable > relations = new ArrayList<AgeRelationWritable>(5);
 
 public AgeObjectLinkedProxy(AgeObjectWritable obj, ModuleKey mk, SerializedSwapStorage sss)
 {
  super(obj, mk, sss);
 }

 @Override
 public Collection<? extends AgeRelationClass> getRelationClasses()
 {
  if( relations.size() == 0 )
   super.getRelationClasses();
  
  Set<AgeRelationClass> set = new HashSet<AgeRelationClass>();
  
  set.addAll( super.getRelationClasses() );
  
  for( AgeRelationWritable r : relations )
   set.add( r.getAgeElClass() );
    
  return set;
 }
 
 @SuppressWarnings("unchecked")
 @Override
 public Collection< ? extends AgeRelationWritable> getRelations()
 {
  return new CollectionsUnion<AgeRelationWritable>( super.getRelations(), relations );
 }

 @Override
 public Collection< ? extends AgeRelationWritable> getRelationsByClass(AgeRelationClass cls, boolean wSbCl)
 {
  ArrayList<AgeRelationWritable> rels = null;
  
  for( AgeRelationWritable r : relations )
  {
   if( cls.equals(r.getAgeElClass()) )
   {
    if( rels == null )
     rels = new ArrayList<AgeRelationWritable>();
    
    rels.add(r);
   }
   else if( wSbCl && r.getAgeElClass().isClassOrSubclass(cls) )
   {
    if( rels == null )
     rels = new ArrayList<AgeRelationWritable>();
    
    rels.add(r);
   }
  }
  
  if( rels != null )
  {
   rels.addAll(super.getRelationsByClass(cls,wSbCl));
   return rels;
  }
  
  return super.getRelationsByClass(cls,wSbCl);
 }

 @Override
 public void addRelation(AgeRelationWritable r)
 {
  relations.add(r);
 }

 @Override
 public void removeRelation(AgeRelationWritable rel)
 {
  if( !relations.remove(rel) )
   super.removeRelation(rel);
 }

}
