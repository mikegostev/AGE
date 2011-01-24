package uk.ac.ebi.age.model;

import java.util.Collection;

import uk.ac.ebi.age.model.writable.AgeExternalRelationWritable;


public interface DataModule
{
 String getId();
 
 Collection<? extends AgeObject> getObjects();
 
 String getDescription();
 
 ContextSemanticModel getContextSemanticModel();
 
 Collection<AgeExternalRelationWritable> getExternalRelations();

// Collection<AgeExternalObjectAttributeWritable> getExternalObjectAttributes();
}