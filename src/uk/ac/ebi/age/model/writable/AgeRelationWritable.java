package uk.ac.ebi.age.model.writable;

import uk.ac.ebi.age.model.AgeRelation;

public interface AgeRelationWritable extends AgeRelation
{
 AgeObjectWritable getTargetObject();
 
 void setOrder(int col);

 void resetModel();

}
