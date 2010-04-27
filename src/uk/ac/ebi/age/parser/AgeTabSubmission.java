package uk.ac.ebi.age.parser;

import java.util.Collection;

public interface AgeTabSubmission
{
 AgeTabObject getObject(String part, BlockHeader classColumnHeader);

 AgeTabObject getOrCreateObject(String part, BlockHeader classColumnHeader, int ln);

 Collection<BlockHeader> getBlocks();

 Collection<AgeTabObject> getObjects(BlockHeader hdr);

 AgeTabObject createObject(String part, BlockHeader header, int ln);

}
