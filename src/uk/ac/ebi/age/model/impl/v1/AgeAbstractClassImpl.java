package uk.ac.ebi.age.model.impl.v1;

import uk.ac.ebi.age.model.AgeAbstractClass;
import uk.ac.ebi.age.model.SemanticModel;

abstract class AgeAbstractClassImpl extends AgeSemanticElementImpl implements  AgeAbstractClass
{
 private static final long serialVersionUID = 1L;

 public AgeAbstractClassImpl(SemanticModel model)
 {
  super(model);
 }



 public boolean isClassOrSubclass( AgeAbstractClass cl )
 {
  if( cl.equals(this) )
   return true;
  
  if( cl.getSubClasses() == null )
   return false;
  
  for( AgeAbstractClass supcls : cl.getSubClasses() )
   if( isClassOrSubclass(supcls) )
    return true;
  
  return false;
 }
 
 public String toString()
 {
  return getName();
 }

}
