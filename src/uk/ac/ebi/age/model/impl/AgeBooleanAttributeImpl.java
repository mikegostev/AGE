package uk.ac.ebi.age.model.impl;

import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.model.FormatException;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.writable.AgeAttributeWritable;

public class AgeBooleanAttributeImpl extends AgeAttributeImpl implements AgeAttributeWritable
{
 private boolean value; 

 public AgeBooleanAttributeImpl(AgeObject obj, AgeAttributeClass attrClass, SemanticModel sm)
 {
  super(obj, attrClass, sm);
 }

 public Object getValue()
 {
  return value;
 }

 public void updateValue(String val) throws FormatException
 {  
  val=val.trim();
 
  if( val.length() == 0 )
   return;

  try
  { 
   value=Boolean.parseBoolean(val);
  }
  catch (NumberFormatException e) 
  {
   throw new FormatException("Invalid boolean format",e);
  }
 }



}
