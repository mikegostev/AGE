package uk.ac.ebi.age.model.impl;

import java.io.Serializable;

import uk.ac.ebi.age.model.AgeAbstractClass;
import uk.ac.ebi.age.model.AgeAbstractObject;
import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.AgeRestriction;
import uk.ac.ebi.age.model.RestrictionException;

abstract public class IsInstanceOfRestriction implements uk.ac.ebi.age.model.IsInstanceOfRestriction, Serializable
{
 private static final long serialVersionUID = 1L;

 public static void isInstanceOf( AgeAbstractObject obj, AgeAbstractClass cls ) throws RestrictionException
 {
  for( AgeAbstractClass supcls : cls.getSuperClasses() )
   isInstanceOf(obj, supcls);
  
  if( cls instanceof AgeClass )
  {
   for(AgeRestriction rest : ((AgeClass)cls).getRestrictions() )
    rest.validate(obj);
  }
 }

}
