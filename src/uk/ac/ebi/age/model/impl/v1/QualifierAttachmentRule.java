package uk.ac.ebi.age.model.impl.v1;

import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.writable.QualifierRuleWritable;


public class QualifierAttachmentRule implements QualifierRuleWritable
{
 private AgeAttributeClass attr;
 private boolean unique;

 @Override
 public AgeAttributeClass getAttributeClass()
 {
  return attr;
 }

 @Override
 public void setAttributeClass(AgeAttributeClass attr)
 {
  this.attr = attr;
 }

 @Override
 public boolean isUnique()
 {
  return unique;
 }

 @Override
 public void setUnique(boolean unique)
 {
  this.unique = unique;
 }


}
