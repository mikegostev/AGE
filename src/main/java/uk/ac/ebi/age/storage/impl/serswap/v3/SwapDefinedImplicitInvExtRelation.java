package uk.ac.ebi.age.storage.impl.serswap.v3;

import uk.ac.ebi.age.model.AgeRelationClass;

public class SwapDefinedImplicitInvExtRelation extends SwapImplicitInvExtRelation
{
 private transient AgeRelationClass relClass;
 private String className;

 public SwapDefinedImplicitInvExtRelation(AgeObjectProxy src, AgeObjectProxy tgt, AgeRelationClass invClass )
 {
  super(src, tgt);
  
  relClass = invClass.getInverseRelationClass();
  className = invClass .getName();
 }

 @Override
 public AgeRelationClass getAgeElClass()
 {
  if( relClass == null )
   relClass = ((AgeObjectProxy)getSourceObject()).getStorage().getSemanticModel()
    .getDefinedAgeRelationClass(className).getInverseRelationClass();
 
  return relClass;
 }

}
