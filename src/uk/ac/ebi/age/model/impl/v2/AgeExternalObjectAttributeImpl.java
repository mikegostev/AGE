package uk.ac.ebi.age.model.impl.v2;

import java.io.Serializable;

import uk.ac.ebi.age.model.AgeAttribute;
import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.model.AgeObjectAttribute;
import uk.ac.ebi.age.model.AttributeClassRef;
import uk.ac.ebi.age.model.AttributedClass;
import uk.ac.ebi.age.model.ContextSemanticModel;
import uk.ac.ebi.age.model.FormatException;
import uk.ac.ebi.age.model.writable.AgeExternalObjectAttributeWritable;

class AgeExternalObjectAttributeImpl extends AgeAttributeImpl implements AgeExternalObjectAttributeWritable, Serializable
{
 private static final long serialVersionUID = 1L;

 private String objId;
 private transient AgeObject target;
 
 public AgeExternalObjectAttributeImpl(AttributeClassRef relClass, String id, ContextSemanticModel sm)
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
 public void setTargetObject(AgeObject obj)
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
  AgeExternalObjectAttributeImpl clone  = new AgeExternalObjectAttributeImpl(getClassRef(), objId, getSemanticModel());
  clone.target=this.target;
  
  cloneAttributes( clone );

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

