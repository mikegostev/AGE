package uk.ac.ebi.age.model.impl.v1;

import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.model.FormatException;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.writable.AgeAttributeWritable;

class AgeStringAttributeImpl extends AgeAttributeImpl implements AgeAttributeWritable
{
 private static final long serialVersionUID = 1L;
 
 private String value; 

 public AgeStringAttributeImpl(AgeObject obj, AgeAttributeClass attrClass, SemanticModel sm)
 {
  super(obj, attrClass, sm);
 }

 public Object getValue()
 {
  return value;
 }

 public void updateValue(String val) throws FormatException
 {
  if( value == null )
   value=val;
  else if( val.length() == 0 )
   value +="\n";
  else
   value += "\n"+val;
 }

 public void finalizeValue()
 {
  if( value != null )
   value = value.trim();
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
