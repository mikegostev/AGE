package uk.ac.ebi.age.model.writable;

import uk.ac.ebi.age.model.AgeFileAttribute;

public interface AgeFileAttributeWritable extends AgeAttributeWritable, AgeFileAttribute
{
 void setFileReference( String fRef );
 void setFileId( String fId );
}
