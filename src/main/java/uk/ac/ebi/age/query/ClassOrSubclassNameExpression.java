package uk.ac.ebi.age.query;

import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.AgeObject;

public class ClassOrSubclassNameExpression implements QueryExpression
{

 private String className;
 
 public String getClassName()
 {
  return className;
 }
 
 public void setClassName(String className)
 {
  this.className = className;
 }
 

 @Override
 public boolean test(AgeObject obj)
 {
  AgeClass cls = obj.getAgeElClass();
  
  return isSuperClass(cls, className);
 }

 private static boolean isSuperClass( AgeClass cls, String cName )
 {
  if( cls.isCustom() )
   return false;

  if( cls.getName().equals(cName) )
   return true;

  if( cls.getSuperClasses() == null )
   return false;
  
  for( AgeClass spcls : cls.getSuperClasses() )
  {
   if( isSuperClass(spcls,cName) )
    return true;
  }

  return false;
 }
 
 @Override
 public boolean isCrossingObjectConnections()
 {
  return false;
 }
 
 

}
