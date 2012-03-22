package uk.ac.ebi.age.storage.impl.serswap.v3;

import java.lang.ref.SoftReference;
import java.util.Collection;

import uk.ac.ebi.age.model.AgeAttribute;
import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.AttributeClassRef;
import uk.ac.ebi.age.model.AttributedClass;
import uk.ac.ebi.age.model.ContextSemanticModel;
import uk.ac.ebi.age.model.writable.AgeAttributeWritable;
import uk.ac.ebi.age.model.writable.AgeExternalRelationWritable;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;
import uk.ac.ebi.age.model.writable.AgeRelationWritable;

import com.pri.util.collection.Collections;

abstract class SwapImplicitInvExtRelation implements AgeExternalRelationWritable
{
 private AgeObjectProxy sourceObject;
 private AgeObjectProxy targetObject;
 
 private SoftReference<AgeExternalRelationWritable> invRelRef;
 
 public SwapImplicitInvExtRelation( AgeObjectProxy src, AgeObjectProxy tgt)
 {
  sourceObject = src;
  targetObject = tgt;
 }
 
 @Override
 public String getTargetObjectId()
 {
  return targetObject.getId();
 }

 @Override
 public boolean isTargetGlobal()
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public AgeObjectWritable getSourceObject()
 {
  return sourceObject;
 }

 @Override
 public AgeObjectWritable getTargetObject()
 {
  return targetObject;
 }

 @Override
 abstract public AgeRelationClass getAgeElClass();


 @Override
 public boolean isInferred()
 {
  return true;
 }

 @Override
 public Collection< ? extends AgeAttributeWritable> getAttributes()
 {
  return Collections.emptyList();
 }

 @Override
 public int getOrder()
 {
  return 0;
 }

 @Override
 public AttributedClass getAttributedClass()
 {
  return getAgeElClass();
 }

 @Override
 public AgeAttribute getAttribute(AgeAttributeClass cls)
 {
  return null;
 }

 @Override
 public Collection< ? extends AgeAttributeWritable> getAttributesByClass(AgeAttributeClass cls, boolean wSubCls)
 {
  return null;
 }

 @Override
 public Collection< ? extends AgeAttributeClass> getAttributeClasses()
 {
  return Collections.emptyList();
 }

 @Override
 public ContextSemanticModel getSemanticModel()
 {
  return sourceObject.getSemanticModel();
 }

 @Override
 public String getId()
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public void setInferred(boolean inf)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public AgeRelationWritable createClone(AgeObjectWritable host)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public void setInverseRelation(AgeRelationWritable invRl)
 {
  invRelRef = new SoftReference<AgeExternalRelationWritable>( (AgeExternalRelationWritable)invRl );
 }

 @Override
 public AgeAttributeWritable createAgeAttribute(AttributeClassRef attrClass)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public AgeAttributeWritable createExternalObjectAttribute(AttributeClassRef attrClass, String val, boolean glb )
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public void addAttribute(AgeAttributeWritable attr)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public void removeAttribute(AgeAttributeWritable attr)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public void reset()
 {
 }

 @Override
 public void sortAttributes()
 {
 }

 @Override
 public void pack()
 {
 }

 @Override
 public void setSourceObject(AgeObjectWritable ageObject)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public void setTargetObject(AgeObjectWritable obj)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public AgeExternalRelationWritable getInverseRelation()
 {
  if( invRelRef != null && invRelRef.get() != null )
   return invRelRef.get();
  
  for( AgeRelationWritable rel : getTargetObject().getRelations() )
  {
   if( rel instanceof AgeExternalRelationWritable && rel.getTargetObject() == getSourceObject() && rel.getAgeElClass().equals( getAgeElClass().getInverseRelationClass() ) )
   {
    invRelRef = new SoftReference<AgeExternalRelationWritable>( (AgeExternalRelationWritable)rel );
    
    return (AgeExternalRelationWritable)rel;
   }
  }
  
  return null;
 }

 @Override
 public void setInverseRelation(AgeExternalRelationWritable inrv)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public void setTargetGlobal(boolean glb)
 {
  throw new UnsupportedOperationException();
 }

}
