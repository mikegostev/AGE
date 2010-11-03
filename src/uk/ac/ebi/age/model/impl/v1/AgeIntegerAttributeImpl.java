package uk.ac.ebi.age.model.impl.v1;

import uk.ac.ebi.age.model.AgeAttribute;
import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.FormatException;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.writable.AgeAttributeWritable;


class AgeIntegerAttributeImpl extends AgeAttributeImpl implements AgeAttributeWritable
{
 private static final long serialVersionUID = 1L;

 private int value; 

 public AgeIntegerAttributeImpl(AgeAttributeClass attrClass, SemanticModel sm)
 {
  super(attrClass, sm);
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
 public String getId()
 {
  return null;
 }

 @Override
 public boolean getValueAsBoolean()
 {
  return value!=0;
 }

 @Override
 public double getValueAsDouble()
 {
  return value;
 }

 @Override
 public int getValueAsInteger()
 {
  return value;
 }

 @Override
 public void setBooleanValue(boolean boolValue)
 {
  value=boolValue?1:0;
 }

 @Override
 public void setDoubleValue(double doubleValue)
 {
  value=(int)Math.round(doubleValue);
 }

 @Override
 public void setIntValue(int intValue)
 {
  value=intValue;
 }

 @Override
 public void setValue(Object val)
 {
  if( val instanceof Number )
   value=((Number)val).intValue();
  else try
  {
   value = Integer.parseInt(val.toString());
  }
  catch (Exception e)
  {
  }
 }
 
 @Override
 public AgeAttributeWritable createClone()
 {
  AgeIntegerAttributeImpl clone  = new AgeIntegerAttributeImpl(getAgeAttributeClass(), getSemanticModel());
  clone.value=this.value;
  
  clone.setOrder( getOrder() );
  
  cloneAttributes( clone );

  return clone;
 }
 
 public boolean equals( Object ob )
 {
  if( ! (ob instanceof AgeAttribute) )
   return false;
  
   return value == ((AgeAttribute)ob).getValueAsInteger();
 }
 
 @Override
 public int compareTo(AgeAttribute o)
 {
  return value-o.getValueAsInteger();
 }
}
