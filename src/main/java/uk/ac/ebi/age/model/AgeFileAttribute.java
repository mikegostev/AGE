package uk.ac.ebi.age.model;

import java.io.File;

public interface AgeFileAttribute extends AgeAttribute
{
 String getFileId();
 ResolveScope getTargetResolveScope();
 
 boolean isResolvedGlobal();

 File getFile();
 
// String getFileSysRef();
}
