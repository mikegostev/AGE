package uk.ac.ebi.age.model.impl.v3;

import java.io.Serializable;

import uk.ac.ebi.age.model.AgeAttribute;
import uk.ac.ebi.age.model.AgeObjectAttribute;
import uk.ac.ebi.age.model.AttributeClassRef;
import uk.ac.ebi.age.model.FormatException;
import uk.ac.ebi.age.model.writable.AgeAttributeWritable;
import uk.ac.ebi.age.model.writable.AgeObjectAttributeWritable;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;
import uk.ac.ebi.age.model.writable.AttributedWritable;

public class AgeObjectAttributeImpl extends AgeAttributeImpl implements AgeObjectAttributeWritable, Serializable
{
 private static final long serialVersionUID = 3L;
 
 private AgeObjectWritable value; 

 protected AgeObjectAttributeImpl(AttributeClassRef attrClass, AttributedWritable host)
 {
  super(attrClass, host);
 }

 public AgeObjectWritable getValue()
 {
  return value;
 }

 public void updateValue(String val) throws FormatException
 {
  throw new UnsupportedOperationException();
 }

 public void finalizeValue()
 {
 }
 

 @Override
 public String getId()
 {
  return null;
 }

 @Override
 public boolean getValueAsBoolean()
 {
  return false;
 }

 @Override
 public double getValueAsDouble()
 {
  return 0;
 }

 @Override
 public int getValueAsInteger()
 {
  return 0;
 }

 @Override
 public void setBooleanValue(boolean boolValue)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public void setDoubleValue(double doubleValue)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public void setIntValue(int intValue)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public void setValue(Object val)
 {
  if( val instanceof AgeObjectWritable )
   value=(AgeObjectWritable)val;
 }
 
 @Override
 public void setValue( AgeObjectWritable val)
 {
  value=val;
 }
 
 @Override
 public AgeAttributeWritable createClone( AttributedWritable host )
 {
  AgeObjectAttributeImpl clone  = new AgeObjectAttributeImpl(getClassReference(), host);
  clone.value=this.value;
  
  cloneAttributes( clone );

  return clone;
 }
 
 public boolean equals( Object ob )
 {
  if( ! (ob instanceof AgeAttribute) )
   return false;
  
   return value.equals( ((AgeAttribute)ob).getValue() );
 }

 @Override
 public int compareTo( AgeAttribute ob )
 {
  if( ! (ob instanceof AgeObjectAttribute) )
   return 1;

  
  return value.getId().compareTo( ((AgeObjectAttribute)ob).getValue().getId() );
 }

 @Override
 public String getTargetObjectId()
 {
  return value.getId();
 }


}
