package uk.ac.ebi.age.model.writable;

import java.util.Collection;

import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.Attributed;


public interface AttributedWritable extends Attributed
{
 Collection<? extends AgeAttributeWritable> getAttributes();

 Collection< ? extends AgeAttributeWritable> getAttributes(AgeAttributeClass cls);

 Collection< ? extends AgeAttributeWritable> getAttributesByClass(AgeAttributeClass cls, boolean wSubCls);


 void addAttribute(AgeAttributeWritable attr);

 void removeAttribute(AgeAttributeWritable attr);

 void reset();
}