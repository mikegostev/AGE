package uk.ac.ebi.age.model.writable;

import uk.ac.ebi.age.model.Attributed;


public interface AttributedWritable extends Attributed
{

 void addAttribute(AgeAttributeWritable attr);

 void removeAttribute(AgeAttributeWritable attr);

}