package uk.ac.ebi.age.model.writable;

import java.util.Collection;

import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.Submission;



/**
 @model
*/

public interface AgeObjectWritable extends AgeObject
{
 Collection<? extends AgeAttributeWritable> getAttributes();
 Collection<? extends AgeRelationWritable> getRelations();
 
 Collection<? extends AgeRelationWritable> getRelations( AgeRelationClass cls );
 Collection<? extends AgeAttributeWritable> getAttributes( AgeAttributeClass cls );

 
 void addAttribute(AgeAttributeWritable attr);
 void removeAttribute(AgeAttributeWritable attr);

 void addRelation(AgeRelationWritable createExternalRelation);

 AgeAttributeWritable createAgeAttribute(AgeAttributeClass attrClass);

 AgeExternalRelationWritable createExternalRelation(String val, AgeRelationClass relClass);

 AgeRelationWritable createRelation(AgeObjectWritable targetObj, AgeRelationClass relClass);

 void setOrder(int row);

 void setSubmission( Submission s );
 
 void setId( String id );
 
// void resetModel();
}
