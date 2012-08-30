package uk.ac.ebi.age.model.writable;

import uk.ac.ebi.age.model.AgeObjectProperty;

public interface AgeObjectPropertyWritable extends AgeObjectProperty, AttributedWritable
{
 @Override
 void addAttribute(AgeAttributeWritable attr);
}
