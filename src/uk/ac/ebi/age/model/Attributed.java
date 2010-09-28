package uk.ac.ebi.age.model;

import java.util.Collection;

public interface Attributed
{

 Collection<? extends AgeAttribute> getAttributes();

 Collection< ? extends AgeAttribute> getAttributes(AgeAttributeClass cls);

 Collection<String> getAttributeClassesIds();

 Collection< ? extends AgeAttribute> getAttributesByClassId(String cid);

 Collection< ? extends AgeAttribute> getAttributesByClass(AgeAttributeClass cls);

 Collection< ? extends AgeAttributeClass> getAttributeClasses();

}