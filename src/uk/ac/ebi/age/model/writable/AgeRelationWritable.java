package uk.ac.ebi.age.model.writable;

import uk.ac.ebi.age.model.AgeRelation;

public interface AgeRelationWritable extends AgeRelation, AttributedWritable, AgeObjectPropertyWritable
{
 void setOrder(int col);

 public void setInferred( boolean inf );
 AgeRelationWritable createClone();

}
