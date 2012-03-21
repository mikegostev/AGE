package uk.ac.ebi.age.storage.impl.serswap.v3;

import java.lang.ref.SoftReference;

import uk.ac.ebi.age.model.AgeRelationClass;

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

}
