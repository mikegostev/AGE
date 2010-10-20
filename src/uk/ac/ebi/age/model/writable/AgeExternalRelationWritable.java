package uk.ac.ebi.age.model.writable;

import uk.ac.ebi.age.model.AgeExternalRelation;
import uk.ac.ebi.age.model.AgeObject;

public interface AgeExternalRelationWritable extends AgeExternalRelation, AgeRelationWritable
{
 void setTargetObject( AgeObject obj );
 
 AgeObject getSourceObject();
}
