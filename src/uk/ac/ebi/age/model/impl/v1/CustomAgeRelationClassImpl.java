package uk.ac.ebi.age.model.impl.v1;

import java.util.Collection;
import java.util.Collections;

import uk.ac.ebi.age.model.AgeAbstractClass;
import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.AgeClassPlug;
import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.service.IdGenerator;

class CustomAgeRelationClassImpl extends AgeSemanticElementImpl implements AgeRelationClass
{
 private static final long serialVersionUID = 1L;
 
 private String name;
 private AgeClassPlug ownerClass;
 private AgeClassPlug rangeClass;
 private CustomAgeRelationClassImpl inverse;
 private boolean implicit = false;
 private String id;
 
 


 public CustomAgeRelationClassImpl(String name, SemanticModel sm, AgeClass range, AgeClass owner)
 {
  super(sm);
  
  this.name=name;
  
  rangeClass = sm.getAgeClassPlug(range);

  ownerClass = sm.getAgeClassPlug(owner);
  
  inverse = new CustomAgeRelationClassImpl( "!"+name, sm, range, owner );
  inverse.setInverseClass(this);
  inverse.setImplicit(true);
  
  id="AgeRelationClass"+IdGenerator.getInstance().getStringId();
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
 
 public void setId(String id)
 {
  this.id = id;
 }


 public String getId()
 {
  return id;
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
 public Collection<AgeRelationClass> getSubClasses()
 {
  return null;
 }

 @Override
 public Collection<AgeRelationClass> getSuperClasses()
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

 public void resetModel()
 {
  ownerClass.unplug();
  rangeClass.unplug();
  inverse.resetModel();
 }
}
