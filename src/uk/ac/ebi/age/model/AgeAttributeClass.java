package uk.ac.ebi.age.model;

public interface AgeAttributeClass extends AgeClassProperty, AgeSemanticElement, AgeAbstractClass
{
 DataType getDataType();

 String getName();

 boolean isCustom();

 AgeClass getOwningClass();

}
