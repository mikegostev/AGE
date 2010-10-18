package uk.ac.ebi.age.model.writable;

import java.util.Collection;

import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.Submission;



/**
 @model
*/

public interface AgeObjectWritable extends AgeObject, AttributedWritable
{
 Collection<? extends AgeRelationWritable> getRelations();
 
// Collection< ? extends AgeRelationWritable> getRelationsByClassId(String cid);
 Collection< ? extends AgeRelationWritable> getRelationsByClass(AgeRelationClass cls, boolean wSbCl);
 
 
 void addAttribute(AgeAttributeWritable attr);
 void removeAttribute(AgeAttributeWritable attr);

 void addRelation(AgeRelationWritable createExternalRelation);


 AgeExternalRelationWritable createExternalRelation(String val, AgeRelationClass relClass);

 AgeRelationWritable createRelation(AgeObjectWritable targetObj, AgeRelationClass relClass);

 void setOrder(int row);

 void setSubmission( Submission s );
 
 void setId( String id );

 
// void resetModel();
}
