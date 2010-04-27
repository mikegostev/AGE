package uk.ac.ebi.age.parser;

import uk.ac.ebi.age.model.ContextSemanticModel;
import uk.ac.ebi.age.model.RestrictionException;
import uk.ac.ebi.age.model.writable.SubmissionWritable;
import uk.ac.ebi.age.parser.impl.AgeTabSemanticValidatorImpl;


public abstract class AgeTabSemanticValidator
{
 public static final String rangeFlag="RANGE";
 public static final String typeFlag="TYPE";
 
 
 public static AgeTabSemanticValidator getInstance()
 {
  return new AgeTabSemanticValidatorImpl();
 }

 public abstract SubmissionWritable parse( AgeTabSubmission data, ContextSemanticModel sm ) throws SemanticException, ConvertionException, RestrictionException;

 

}
