package uk.ac.ebi.age.model.impl.v1;

import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.model.FormatException;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.writable.AgeAttributeWritable;

class AgeRealAttributeImpl extends AgeAttributeImpl implements AgeAttributeWritable
{

 private static final long serialVersionUID = 1L;

 private double value; 

 public AgeRealAttributeImpl(AgeObject obj, AgeAttributeClass attrClass, SemanticModel sm)
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
   value=Double.parseDouble(val);
  }
  catch (NumberFormatException e) 
  {
   throw new FormatException("Invalid real format",e);
  }
 }

 @Override
 public String getParameter()
 {
  return null;
 }

 @Override
 public String getId()
 {
  return null;
 }


}
