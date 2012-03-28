package uk.ac.ebi.age.storage.impl.serswap.v3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;
import uk.ac.ebi.age.model.writable.AgeRelationWritable;
import uk.ac.ebi.age.storage.ModuleKey;
import uk.ac.ebi.age.storage.impl.serswap.StoragePlug;

import com.pri.util.collection.Collections;

public class AgeObjectLinkedProxy extends AgeObjectProxy
{
 private Collection< AgeRelationWritable > relations = null;
 
 public AgeObjectLinkedProxy(AgeObjectWritable obj, ModuleKey mk, StoragePlug sss)
 {
  super(obj, mk, sss);
 }

 @Override
 public Collection<? extends AgeRelationClass> getRelationClasses()
 {
  if( relations == null )
   return Collections.emptyList();
  
  Set<AgeRelationClass> set = new HashSet<AgeRelationClass>();
  
  for( AgeRelationWritable r : relations )
   set.add( r.getAgeElClass() );
    
  return set;
 }
 
 @Override
 public Collection< ? extends AgeRelationWritable> getRelations()
 {
  return relations;
 }

 @Override
 public Collection< ? extends AgeRelationWritable> getRelationsByClass(AgeRelationClass cls, boolean wSbCl)
 {
  if( relations == null )
   return Collections.emptyList();

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

  return rels!=null?rels:Collections.<AgeRelationWritable>emptyList();
 }

 @Override
 public void addRelation(AgeRelationWritable r)
 {
  if( relations == null )
   relations = new ArrayList<AgeRelationWritable>(7);
  
  relations.add(r);
 }

 @Override
 public void removeRelation(AgeRelationWritable rel)
 {
  if( relations != null )
   relations.remove(rel);
 }

 public void addRelations(Collection< ? extends AgeRelationWritable> relations2)
 {
  if( relations == null )
   relations = new ArrayList<AgeRelationWritable>(7);

  relations.addAll( relations2 );
 }

}
