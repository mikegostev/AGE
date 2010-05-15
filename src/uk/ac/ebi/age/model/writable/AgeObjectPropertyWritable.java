package uk.ac.ebi.age.model.writable;

import java.util.Collection;

import uk.ac.ebi.age.model.AgeObjectProperty;

public interface AgeObjectPropertyWritable extends AgeObjectProperty
{
 Collection<AgeAttributeWritable> getQualifiers();
 
 void addQualifier( AgeAttributeWritable attr );
}
