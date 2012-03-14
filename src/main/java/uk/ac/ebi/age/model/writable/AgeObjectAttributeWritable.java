package uk.ac.ebi.age.model.writable;

import uk.ac.ebi.age.model.AgeObjectAttribute;

public interface AgeObjectAttributeWritable extends AgeObjectAttribute, AgeAttributeWritable
{
 AgeObjectWritable getValue();
 void setValue( AgeObjectWritable val );
}
