package uk.ac.ebi.age.model;

public interface AgeAttributeClass extends AgeClassProperty, AgeSemanticElement, AgeAbstractClass
{
 DataType getDataType();

 String getName();

 void addSubClass(AgeAttributeClass sbcls);

 boolean isCustom();
 void setCustom(boolean b);

 AgeClass getOwningClass();
 void setOwningClass(AgeClass cls);

}
