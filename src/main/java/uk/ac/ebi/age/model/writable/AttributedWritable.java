package uk.ac.ebi.age.model.writable;

import java.util.Collection;
import java.util.List;

import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AttributeClassRef;
import uk.ac.ebi.age.model.Attributed;
import uk.ac.ebi.age.model.ResolveScope;


public interface AttributedWritable extends Attributed
{
 AgeAttributeWritable createAgeAttribute(AttributeClassRef attrClass);
 AgeAttributeWritable createExternalObjectAttribute(AttributeClassRef attrClass, String val, ResolveScope scope );

 
 Collection<? extends AgeAttributeWritable> getAttributes();


 Collection< ? extends AgeAttributeWritable> getAttributesByClass(AgeAttributeClass cls, boolean wSubCls);


 void addAttribute(AgeAttributeWritable attr);

 void setAttributes(List<AgeAttributeWritable> attrs);

 void removeAttribute(AgeAttributeWritable attr);

 void reset();
 void sortAttributes();
 
 void pack();
}