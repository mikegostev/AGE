package uk.ac.ebi.age.model.writable;

import uk.ac.ebi.age.model.AgeExternalObjectAttribute;

public interface AgeExternalObjectAttributeWritable extends AgeExternalObjectAttribute, AgeAttributeWritable
{
 void setTargetObject(AgeObjectWritable obj);

}
