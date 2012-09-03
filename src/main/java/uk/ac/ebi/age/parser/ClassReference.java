package uk.ac.ebi.age.parser;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ClassReference extends AgeTabElement
{
 private boolean horizontal;
 private boolean custom;
 private String name;
 private Map<String,String> flags;
 private LinkedList<ClassReference> qualifiers;
 private String parentClass;
 
 private ClassReference embeddedClassRef;
 
 private ClassReference targetClassRef;
 private ClassReference rangeClassRef;
 private String rawRef;

 public ClassReference()
 {
  super(0,0);
 }
 
 public ClassReference(int row, int col)
 {
  super(row, col);
 }
 
 
 public int getOrder()
 {
  return horizontal?getCol():getRow();
 }
 
 public void setCustom(boolean t)
 {
  custom=t;
 }

 public boolean isCustom()
 {
  return custom;
 }

 public void setName(String nm)
 {
  name=nm;
 }

 public String getName()
 {
  return name;
 }

 public List<ClassReference> getQualifiers()
 {
  return qualifiers;
 }
 
 public void addQualifier(ClassReference s)
 {
  if( qualifiers == null )
   qualifiers = new LinkedList<ClassReference>();
  
  qualifiers.add(s);
 }

 public void insertQualifier(ClassReference s)
 {
  if( qualifiers == null )
   qualifiers = new LinkedList<ClassReference>();
  
  qualifiers.addFirst(s);
 }

 public void addFlag(String nm, String vl)
 {
  if( flags == null )
   flags = new TreeMap<String, String>();
  
  flags.put(nm, vl);
 }

 public boolean isFlagSet( String fl )
 {
  if( flags == null )
  return false;
  
  return flags.containsKey(fl);
 }
 
 public String getFlagValue( String fl )
 {
  if( flags == null )
  return null;
  
  return flags.get(fl);
 }
 
 public Map<String,String> getFlags()
 {
  return flags;
 }

 public String getParentClass()
 {
  return parentClass;
 }

 public void setParentClass(String parentClass)
 {
  this.parentClass = parentClass;
 }

 public boolean isQualifierFor(ClassReference hostPretender)
 {
//  if( embeddedClassRef != null )
//  {
//   if( cr.getEmbeddedClassRef() != null )
//    return equalsBasis(cr) && embeddedClassRef.isQualifierFor(cr.getEmbeddedClassRef());
//   else
//    return false;
//  }
//  else if( cr.getEmbeddedClassRef() != null)
//   return false;
  
  if( ! ( getName().equals( hostPretender.getName() ) && isCustom() == isCustom() ) ) // Different bases
   return false;

  if( getQualifiers() == null || getQualifiers().size() == 0 )  // This is not qualifier at all
    return false;
  
  if( hostPretender.getQualifiers() == null ) // case hostPretender.getQualifiers().size() == 0 will be checked later on
   {
    if( getQualifiers().size() != 1 ) // If cr is the main attribute then out qualifier chain must consist of one link
     return false;
    else
     return true;
   }

  if( getQualifiers().size() != ( hostPretender.getQualifiers().size()+1 ) ) // Our chain must be 1 link longer
   return false;
  
  Iterator<ClassReference> iter1 = getQualifiers().iterator();
  Iterator<ClassReference> iter2 = hostPretender.getQualifiers().iterator();
 
  while( iter2.hasNext() )
   if( ! iter1.next().equalsBasis(iter2.next()) )
    return false;
  
  return true;
 }


// @Override
// public boolean equals( Object obj )
// {
//  if( obj instanceof ClassReference )
//   return equals((ClassReference)obj);
//  
//  return false;
// }
 
 public boolean equalsTo( ClassReference cr )
 {
  if( ! equalsBasis(cr) )
    return false;
  
  if( embeddedClassRef != null )
  {
   if( cr.getEmbeddedClassRef() != null )
    return embeddedClassRef.equals(cr.getEmbeddedClassRef());
   else
    return false;
  }
  else
   return cr.getEmbeddedClassRef() == null;
  
 }

 public boolean equalsBasis( ClassReference cr )
 {
  if( ! ( getName().equals( cr.getName()) && isCustom() == isCustom() ) )
   return false;

  if( getQualifiers() == null || getQualifiers().size() == 0 )
  {
   if( cr.getQualifiers() == null || cr.getQualifiers().size() == 0 )
    return true;
   else
    return false;
  }
  
  if( cr.getQualifiers() == null || cr.getQualifiers().size() == 0 )
   return false;

  if( getQualifiers().size() != cr.getQualifiers().size() )
   return false;
  
  Iterator<ClassReference> iter1 = getQualifiers().iterator();
  Iterator<ClassReference> iter2 = cr.getQualifiers().iterator();
 
  while( iter1.hasNext() )
   if( ! iter1.next().equals(iter2.next()) )
    return false;
  
  return true;
 }

 
 public ClassReference getTargetClassRef()
 {
  return targetClassRef;
 }

 public void setTargetClassRef(ClassReference targetClassRef)
 {
  this.targetClassRef = targetClassRef;
 }

 public ClassReference getRangeClassRef()
 {
  return rangeClassRef;
 }

 public void setRangeClassRef(ClassReference rangeClassRef)
 {
  this.rangeClassRef = rangeClassRef;
 }

 public void setRawReference(String str)
 {
  rawRef = str;
 }
 
 public String getOriginalReference()
 {
  return rawRef;
 }

 public boolean isHorizontal()
 {
  return horizontal;
 }

 public void setHorizontal(boolean horizontal)
 {
  this.horizontal = horizontal;
 }

 public ClassReference getEmbeddedClassRef()
 {
  return embeddedClassRef;
 }

 public void setEmbeddedClassRef(ClassReference embeddedClassRef)
 {
  this.embeddedClassRef = embeddedClassRef;
 }

 @Override
 public String toString()
 {
  return rawRef;
 }
}
