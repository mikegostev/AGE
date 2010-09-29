package uk.ac.ebi.age.model;

import java.util.Collection;

public interface Attributed
{
 AttributedClass getAttributedClass();

 Collection<? extends AgeAttribute> getAttributes();

 Collection< ? extends AgeAttribute> getAttributes(AgeAttributeClass cls);

 Collection<String> getAttributeClassesIds();

 Collection< ? extends AgeAttribute> getAttributesByClassId(String cid, boolean wSubCls);

 Collection< ? extends AgeAttribute> getAttributesByClass(AgeAttributeClass cls, boolean wSubCls);

 Collection< ? extends AgeAttributeClass> getAttributeClasses();

}