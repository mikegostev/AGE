package uk.ac.ebi.age.parser;

import uk.ac.ebi.age.log.LogNode;
import uk.ac.ebi.age.model.ContextSemanticModel;
import uk.ac.ebi.age.model.writable.DataModuleWritable;

public interface AgeTab2AgeConverter
{
 
 public DataModuleWritable convert( AgeTabModule data, ContextSemanticModel sm, LogNode logNode );
}
