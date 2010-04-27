package uk.ac.ebi.age.model.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.AgeRestriction;
import uk.ac.ebi.age.model.SemanticModel;

import com.pri.util.collection.CollectionsUnion;

class AgeClassImpl extends AgeAbstractClassImpl implements AgeClass 
{
 private interface Collector<T>
 {
  T get( AgeClass cls );
 }
 
 private String name;
 private boolean custom=false;
 private Collection<AgeClass> subClasses = new LinkedList<AgeClass>();
 private Collection<AgeClass> superClasses = new LinkedList<AgeClass>();
 private Collection<AgeRestriction> restrictions = new LinkedList<AgeRestriction>();
 private Collection<AgeRestriction> attributeRestrictions = new LinkedList<AgeRestriction>();
 private String idPrefix;

 private Collection<AgeRestriction> unionRestrictions;
 
 public AgeClassImpl(String name, String pfx, SemanticModel sm)
 {
  super( sm );
  this.name=name;
  
  if( pfx == null )
   idPrefix = name.substring(0,1);
  else
   idPrefix=pfx;
  
  Collection< Collection<AgeRestriction> > un = new ArrayList<Collection<AgeRestriction>>(2);
  un.add(restrictions);
  un.add(attributeRestrictions);
  
  unionRestrictions = new CollectionsUnion<AgeRestriction>( un );
 }

 public void addSubClass(AgeClass sbCls)
 {
  subClasses.add(sbCls);
 }

 public void addSuperClass(AgeClass sbCls)
 {
  superClasses.add(sbCls);
 }
 
 public Collection<AgeRestriction> getRestrictions()
 {
  return unionRestrictions;
 }
 

 public Collection<AgeRestriction> getAllRestrictions()
 {
  Collection<Collection<AgeRestriction>> allRest = new ArrayList<Collection<AgeRestriction>>(10);
  
  collectFromHierarchy(this,allRest, new Collector<Collection<AgeRestriction>>(){

   public Collection<AgeRestriction> get(AgeClass cls)
   {
    Collection<AgeRestriction> restr = cls.getRestrictions();
    return restr==null||restr.size()==0?null:restr;
   }} );
  
  return new CollectionsUnion<AgeRestriction>(allRest);
 }

 private static <T> void collectFromHierarchy( AgeClass cls, Collection<T> allRest, Collector<T> src )
 {
  T clct = src.get(cls);
  
  if( clct != null )
   allRest.add( clct );
  
  for( AgeClass supcls : cls.getSuperClasses() )
  {
   collectFromHierarchy(supcls,allRest,src);
  }
 }
 
 public Collection<AgeClass> getSuperClasses()
 {
  return superClasses;
 }
 
 public Collection<AgeClass> getSubClasses()
 {
  return subClasses;
 }

 public String getName()
 {
  return name;
 }

 public boolean isCustom()
 {
  return custom;
 }

 public Collection<AgeRestriction> getObjectRestrictions()
 {
  return restrictions;
 }

 public void addObjectRestriction(AgeRestriction rest)
 {
  restrictions.add(rest);
 }

 public Collection<AgeRestriction> getAllObjectRestrictions()
 {
  Collection<Collection<AgeRestriction>> allRest = new ArrayList<Collection<AgeRestriction>>(10);
  
  collectFromHierarchy(this, allRest, new Collector<Collection<AgeRestriction>>()
  {
   public Collection<AgeRestriction> get(AgeClass cls)
   {
    Collection<AgeRestriction> restr = cls.getObjectRestrictions();
    return restr==null||restr.size()==0?null:restr;
   }
  });
  
  return new CollectionsUnion<AgeRestriction>(allRest);
 }

 
 public void addAttributeRestriction(AgeRestriction rest)
 {
  attributeRestrictions.add(rest);
 }

 public Collection<AgeRestriction> getAttributeRestrictions()
 {
  return attributeRestrictions;
 }
 
 public Collection<AgeRestriction> getAttributeAllRestrictions()
 {
  Collection<Collection<AgeRestriction>> allRest = new ArrayList<Collection<AgeRestriction>>(10);
  
  collectFromHierarchy(this, allRest, new Collector<Collection<AgeRestriction>>()
  {
   public Collection<AgeRestriction> get(AgeClass cls)
   {
    Collection<AgeRestriction> restr = cls.getAttributeRestrictions();
    return restr==null||restr.size()==0?null:restr;
   }
  });
  
  return new CollectionsUnion<AgeRestriction>(allRest);
 }

 public String getIdPrefix()
 {
  return idPrefix;
 }

}
