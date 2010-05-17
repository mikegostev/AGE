package uk.ac.ebi.age.model.impl.v1;

import java.io.Serializable;

import uk.ac.ebi.age.model.AgeAbstractClass;
import uk.ac.ebi.age.model.AgeAbstractObject;
import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.AgeRestriction;
import uk.ac.ebi.age.model.RestrictionException;
import uk.ac.ebi.age.model.impl.IsInstanceOfRestriction;

class IsInstanceOfRestrictionImpl extends IsInstanceOfRestriction implements AgeRestriction, Serializable
{
 private static final long serialVersionUID = 1L;
 
 private AgeClass sourceClass;
 private AgeAbstractClass targerClass;

 public IsInstanceOfRestrictionImpl(AgeClass srcClas, AgeAbstractClass tgtClass)
 {
  sourceClass=srcClas;
  targerClass=tgtClass;
 }

 public String toString()
 {
  return "IsInstanceOf restriction. Target class: '"+targerClass.getName()+"'";
 }
 
 public void validate(AgeAbstractObject obj) throws RestrictionException
 {
  isInstanceOf(obj, targerClass);
 }

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

 @Override
 public AgeAbstractClass getTargetClass()
 {
  return targerClass;
 }

}
