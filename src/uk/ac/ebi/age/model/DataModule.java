package uk.ac.ebi.age.model;

import java.util.Collection;


public interface DataModule
{
 public static interface AttributedSelector
 {
  boolean select( Attributed at );
 }
 
 String getId();
 long getVersion();
 String getClusterId();

 Collection<? extends AgeObject> getObjects();
 
 String getDescription();
 
 ContextSemanticModel getContextSemanticModel();
 
 Collection<? extends AgeExternalRelation> getExternalRelations();
 Collection<? extends AgeExternalObjectAttribute> getExternalObjectAttributes();

 Collection<? extends Attributed> getAttributed( AttributedSelector sel );
}