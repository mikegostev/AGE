package uk.ac.ebi.age.validator.impl;

import uk.ac.ebi.age.model.AgeAttribute;
import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.model.AttributeAttachmentRule;
import uk.ac.ebi.age.model.Cardinality;
import uk.ac.ebi.age.model.Submission;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;
import uk.ac.ebi.age.model.writable.SubmissionWritable;
import uk.ac.ebi.age.validator.AgeSemanticValidator;

public class AgeSemanticValidatorImpl implements AgeSemanticValidator
{

 @Override
 public void validate(Submission subm)
 {
  for( AgeObjectWritable obj : subm.getObjects() )
   validateObject( obj, obj.getAgeElClass() );
 }

 private void validateObject( AgeObject obj, AgeClass cls )
 {
  
  if( cls.getSuperClasses() != null )
  {
   for( AgeClass supCls : cls.getSuperClasses() )
    validateObject(obj,supCls);
  }
  
  if( cls.getAttributeAttachmentRules() != null )
  {
   for( AttributeAttachmentRule atRl : cls.getAttributeAttachmentRules() )
    validateObjectByAttributeRule(atRl,obj);
  }
  
 }

 private void validateObjectByAttributeRule(AttributeAttachmentRule atRl, AgeObjectWritable obj)
 {
  
  if( obj.getAttribute != null )
  {
   for( AgeAttribute at : obj.getAttributes() )
   {
    validateAttributeByAttributeRule(atRl,at);
   }
  }
  
 }

 private boolean validateAttributeByAttributeRule(AttributeAttachmentRule atRl, AgeAttribute at)
 {
  if( !atRl.isSubclassesIncluded() )
  {
   if( ! at.getAgeElClass().equals(atRl.getAttributeClass()) )
    return false;
  }
  else if( ! at.getAgeElClass().isClassOrSubclass(atRl.getAttributeClass()) )
   return false;
  
  int nVals = at.
  
  if( atRl.getCardinalityType() != Cardinality.ANY )
  
 }
 
}
