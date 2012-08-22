package uk.ac.ebi.age.model;

import java.util.Collection;

public interface AgeAttributeClass extends AgePropertyClass, AgeSemanticElement, AgeAbstractClass, AttributedClass, AgeAttributeClassPlug
{
 DataType getDataType();

 String getName();

 boolean isCustom();

 AgeClass getOwningClass();

 
 Collection<AgeAttributeClass> getSuperClasses();
 Collection<AgeAttributeClass> getSubClasses();

 AgeClass getTargetClass();
}
