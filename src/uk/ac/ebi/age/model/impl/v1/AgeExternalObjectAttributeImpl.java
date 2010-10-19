package uk.ac.ebi.age.model.impl.v1;

import java.io.Serializable;

import uk.ac.ebi.age.model.AgeAttribute;
import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.model.AgeObjectAttribute;
import uk.ac.ebi.age.model.AttributedClass;
import uk.ac.ebi.age.model.FormatException;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.writable.AgeExternalObjectAttributeWritable;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;

class AgeExternalObjectAttributeImpl extends AgeAttributeImpl implements AgeExternalObjectAttributeWritable, Serializable
{
 private static final long serialVersionUID = 1L;

 private String objId;
 private int order;
 private transient AgeObject target;
 
 public AgeExternalObjectAttributeImpl(AgeAttributeClass relClass, String id, SemanticModel sm)
 {
  super(relClass, sm);
  
  objId=id;
 }


 @Override
 public AgeObject getValue()
 {
  return target;
 }


 @Override
 public String getTargetObjectId()
 {
  return objId;
 }

 @Override
 public int getOrder()
 {
  return order;
 }

 @Override
 public void setOrder(int ord)
 {
  order=ord;
 }

 @Override
 public void setTargetObject(AgeObjectWritable obj)
 {
  target = obj;
  objId = target.getId();
 }

 @Override
 public String getId()
 {
  return null;
 }

 @Override
 public AttributedClass getAttributedClass()
 {
  return getAgeElClass();
 }

 @Override
 public boolean getValueAsBoolean()
 {
  return false;
 }

 @Override
 public int getValueAsInteger()
 {
  return 0;
 }

 @Override
 public double getValueAsDouble()
 {
  return 0;
 }

 @Override
 public void updateValue(String value) throws FormatException
 {
  objId=value;
 }

 @Override
 public void finalizeValue()
 {
 }

 @Override
 public void setValue(Object val)
 {
  if( val instanceof AgeObject )
  { 
   target=(AgeObject)val;
   objId=target.getId();
  }
 }

 @Override
 public void setBooleanValue(boolean boolValue)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public void setIntValue(int intValue)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public void setDoubleValue(double doubleValue)
 {
  throw new UnsupportedOperationException();
 }

 
 
 @Override
 public AgeExternalObjectAttributeWritable createClone()
 {
  AgeExternalObjectAttributeImpl clone  = new AgeExternalObjectAttributeImpl(getAgeAttributeClass(), objId, getSemanticModel());
  clone.target=this.target;
  
  return clone;
 }
 
 public boolean equals( Object ob )
 {
  if( ! (ob instanceof AgeObjectAttribute) )
   return false;
  
   return objId.equals( ((AgeObjectAttribute)ob).getValue().getId() );
 }

 @Override
 public int compareTo( AgeAttribute ob )
 {
  if( ! (ob instanceof AgeObjectAttribute) )
   return 1;

  
  return objId.compareTo( ((AgeObjectAttribute)ob).getValue().getId() );
 }

 
}

