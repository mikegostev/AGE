package uk.ac.ebi.age.model;

public interface AgeAttribute extends AgeObjectProperty
{
 AgeAttributeClass getAgeElClass();
 
 Object getValue();
 
 boolean getValueAsBoolean();
 int getValueAsInteger();
 double getValueAsDouble();
 
 public int getOrder();
}
