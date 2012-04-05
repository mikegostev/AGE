package uk.ac.ebi.age.storage.impl.serswap.v3;

import java.lang.ref.SoftReference;

import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.AttributeClassRef;
import uk.ac.ebi.age.model.ResolveScope;
import uk.ac.ebi.age.model.writable.AgeAttributeWritable;

public class SwapCustomImplicitInvExtRelation extends SwapImplicitInvExtRelation
{
 private transient SoftReference<AgeRelationClass> relClassRef;
 private String clsName;

 public SwapCustomImplicitInvExtRelation(AgeObjectProxy src, AgeObjectProxy tgt, String clsNm)
 {
  super(src, tgt);

  clsName = clsNm;
 }

 public SwapCustomImplicitInvExtRelation(AgeObjectProxy src, AgeObjectProxy tgt, AgeRelationClass cls)
 {
  super(src, tgt);

  clsName = cls.getName();
  
  relClassRef = new SoftReference<AgeRelationClass>(cls.getInverseRelationClass());
 }

 @Override
 public AgeRelationClass getAgeElClass()
 {
  AgeRelationClass cls = null;
  
  if( relClassRef != null )
   cls = relClassRef.get();
  
  if( cls != null )
   return cls;
  
  cls = getTargetObject().getDataModule().getContextSemanticModel().getCustomAgeRelationClass(clsName).getInverseRelationClass();

  relClassRef = new SoftReference<AgeRelationClass>(cls);
  return cls;
 }

 @Override
 public void setTargetResolveScope(ResolveScope scp)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public ResolveScope getTargetResolveScope()
 {
  return ResolveScope.CASCADE_CLUSTER;
 }

 @Override
 public AgeAttributeWritable createExternalObjectAttribute(AttributeClassRef attrClass, String val, ResolveScope scope)
 {
  // TODO Auto-generated method stub
  throw new dev.NotImplementedYetException();
  //return null;
 }

}
