package uk.ac.ebi.age.model;

import java.util.Collection;

public interface AgeAttributeClass extends AgeClassProperty, AgeSemanticElement, AgeAbstractClass
{
 DataType getDataType();

 String getName();

 boolean isCustom();

 AgeClass getOwningClass();

 
 Collection<AgeAttributeClass> getSuperClasses();
 Collection<AgeAttributeClass> getSubClasses();
}
