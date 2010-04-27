package uk.ac.ebi.age.model.writable;

import uk.ac.ebi.age.model.AgeExternalRelation;

public interface AgeExternalRelationWritable extends AgeExternalRelation, AgeRelationWritable
{
 void setTargetObject( AgeObjectWritable obj );
 
 AgeObjectWritable getSourceObject();
}
