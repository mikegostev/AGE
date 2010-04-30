package uk.ac.ebi.age.model.impl.v1;

import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.model.SemanticModel;


class AgeIntegerAttributeParamImpl extends AgeIntegerAttributeImpl
{
 private static final long serialVersionUID = 1L;

 private String param; 

 public AgeIntegerAttributeParamImpl(AgeObject obj, AgeAttributeClass attrClass, String param, SemanticModel sm)
 {
  super(obj, attrClass, sm);
  this.param=param;
 }

 @Override
 public String getParameter()
 {
  return param;
 }

 @Override
 public String getId()
 {
  return null;
 }

}
