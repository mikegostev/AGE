package uk.ac.ebi.age.model.writable;

import java.util.Collection;
import java.util.Map;

import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.Submission;



/**
 @model
*/

public interface AgeObjectWritable extends AgeObject
{
 <T extends AgeRelationWritable> Map<AgeRelationClass, Collection<AgeRelationWritable>> getRelationsMap();
 Collection<? extends AgeAttributeWritable> getAttributes();
 Collection<? extends AgeRelationWritable> getRelations();
 
 
 void addAttribute(AgeAttributeWritable attr);

 void addRelation(AgeRelationWritable createExternalRelation);

 AgeAttributeWritable createAgeAttribute(AgeAttributeClass attrClass);

 AgeExternalRelationWritable createExternalRelation(String val, AgeRelationClass relClass);

 AgeRelationWritable createRelation(AgeObjectWritable targetObj, AgeRelationClass relClass);

 void setOrder(int row);

 void setSubmission( Submission s );
}
