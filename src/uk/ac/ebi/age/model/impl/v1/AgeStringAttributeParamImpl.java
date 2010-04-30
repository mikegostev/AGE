package uk.ac.ebi.age.model.impl.v1;

import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.model.SemanticModel;

class AgeStringAttributeParamImpl extends AgeStringAttributeImpl
{
 private static final long serialVersionUID = 1L;
 
 private String param; 

 public AgeStringAttributeParamImpl(AgeObject obj, AgeAttributeClass attrClass, String prm, SemanticModel sm)
 {
  super(obj, attrClass, sm);
  
  param=prm;
 }

 @Override
 public String getParameter()
 {
  return param;
 }
}
