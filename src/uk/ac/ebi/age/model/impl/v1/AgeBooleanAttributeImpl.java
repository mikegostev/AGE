package uk.ac.ebi.age.model.impl.v1;

import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.model.FormatException;
import uk.ac.ebi.age.model.SemanticModel;

class AgeBooleanAttributeImpl extends AgeAttributeImpl 
{
 private static final long serialVersionUID = 1L;

 private boolean value; 

 protected AgeBooleanAttributeImpl()
 {}
 
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
