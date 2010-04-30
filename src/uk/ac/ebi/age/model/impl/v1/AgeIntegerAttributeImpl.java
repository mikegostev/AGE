package uk.ac.ebi.age.model.impl.v1;

import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.model.FormatException;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.writable.AgeAttributeWritable;


class AgeIntegerAttributeImpl extends AgeAttributeImpl implements AgeAttributeWritable
{
 private static final long serialVersionUID = 1L;

 private int value; 

 public AgeIntegerAttributeImpl(AgeObject obj, AgeAttributeClass attrClass, SemanticModel sm)
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
   value=Integer.parseInt(val);
  }
  catch (NumberFormatException e) 
  {
   throw new FormatException("Invalid integer format",e);
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
