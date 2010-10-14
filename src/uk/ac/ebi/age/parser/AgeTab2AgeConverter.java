package uk.ac.ebi.age.parser;

import uk.ac.ebi.age.log.LogNode;
import uk.ac.ebi.age.model.ContextSemanticModel;
import uk.ac.ebi.age.model.writable.SubmissionWritable;

public interface AgeTab2AgeConverter
{
 public static final String rangeFlag="RANGE";
 public static final String typeFlag="TYPE";

 
 public SubmissionWritable convert( AgeTabSubmission data, ContextSemanticModel sm, LogNode logNode );
}
