package uk.ac.ebi.age.model;

import java.util.Collection;
import java.util.List;

public interface Attributed extends AgeContextSemanticElement
{
 AttributedClass getAttributedClass();

 List<? extends AgeAttribute> getAttributes();

 AgeAttribute getAttribute(AgeAttributeClass cls);

// Collection< ? extends AgeAttribute> getAttributes(AgeAttributeClass cls);
 List< ? extends AgeAttribute> getAttributesByClass(AgeAttributeClass cls, boolean wSubCls);

 // Collection<String> getAttributeClassesIds();

// Collection< ? extends AgeAttribute> getAttributesByClassId(String cid, boolean wSubCls);

 Collection< ? extends AgeAttributeClass> getAttributeClasses();
}