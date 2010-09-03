package uk.ac.ebi.age.model.impl.v1;

import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.RestrictionType;


public class QualifierAttachmentRule
{
 private AgeAttributeClass attr;
 private RestrictionType type;

 public AgeAttributeClass getAttributeClass()
 {
  return attr;
 }

 public RestrictionType getType()
 {
  return type;
 }

 public void setAttributeClass(AgeAttributeClass attr)
 {
  this.attr = attr;
 }

 public void setType(RestrictionType type)
 {
  this.type = type;
 }

}
