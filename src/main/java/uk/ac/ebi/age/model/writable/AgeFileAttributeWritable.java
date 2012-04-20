package uk.ac.ebi.age.model.writable;

import uk.ac.ebi.age.model.AgeFileAttribute;
import uk.ac.ebi.age.model.ResolveScope;

public interface AgeFileAttributeWritable extends AgeAttributeWritable, AgeFileAttribute
{
 void setFileId( String fRef );

 void setResolvedScope(ResolveScope resolvedScope);
}
