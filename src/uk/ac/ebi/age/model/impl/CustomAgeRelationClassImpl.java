package uk.ac.ebi.age.model.impl;

import java.util.Collection;
import java.util.Collections;

import uk.ac.ebi.age.model.AgeAbstractClass;
import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.AgeClassPlug;
import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.SemanticModel;

public class CustomAgeRelationClassImpl extends AgeSemanticElementImpl implements AgeRelationClass
{
 private String name;
 private AgeClassPlug ownerClass;
 private AgeClassPlug rangeClass;
 private CustomAgeRelationClassImpl inverse;
 private boolean implicit = false;
 
 
 public CustomAgeRelationClassImpl(String name, SemanticModel sm, AgeClass range, AgeClass owner)
 {
  super(sm);
  
  this.name=name;
  
  if( range.isCustom() )
   rangeClass = new AgeClassPlugFixed(range);
  else
   rangeClass = new AgeClassPlugPluggable(range, sm);

  if( owner.isCustom() )
   ownerClass = new AgeClassPlugFixed(owner);
  else
   ownerClass = new AgeClassPlugPluggable(owner, sm);

  inverse = new CustomAgeRelationClassImpl( "!"+name, sm, range, owner );
  inverse.setInverseClass(this);
  inverse.setImplicit(true);
 }

 protected void setInverseClass( CustomAgeRelationClassImpl inv )
 {
  inverse = inv;
 }
 
 @Override
 public AgeRelationClass getInverseClass()
 {
  return inverse;
 }
 
 @Override
 public String getName()
 {
  return name;
 }

 @Override
 public Collection<AgeClass> getDomain()
 {
  return Collections.singletonList(ownerClass.getAgeClass());
 }


 @Override
 public Collection<AgeClass> getRange()
 {
  return Collections.singletonList(rangeClass.getAgeClass());
 }

 @Override
 public boolean isCustom()
 {
  return true;
 }

 @Override
 public boolean isWithinDomain(AgeClass key)
 {
  return key.equals(ownerClass.getAgeClass());
 }

 @Override
 public boolean isWithinRange(AgeClass key)
 {
  return key.isClassOrSubclass(rangeClass.getAgeClass());
 }

 @Override
 public Collection< ? extends AgeAbstractClass> getSubClasses()
 {
  return null;
 }

 @Override
 public Collection< ? extends AgeAbstractClass> getSuperClasses()
 {
  return null;
 }

 @Override
 public boolean isClassOrSubclass(AgeAbstractClass cl)
 {
  return cl.equals(this);
 }

 @Override
 public boolean isImplicit()
 {
  return implicit;
 }
 
 private void setImplicit( boolean b )
 {
  implicit=b;
 }

}
