package uk.ac.ebi.age.model;

import java.util.Collection;


public interface Submission
{
 String getId();
 
 Collection<? extends AgeObject> getObjects();
 
 String getDescription();
 
 ContextSemanticModel getContextSemanticModel();
 
 Collection<AgeExternalRelation> getExternalRelations();
}