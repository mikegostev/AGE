package uk.ac.ebi.age.validator;

import uk.ac.ebi.age.log.LogNode;
import uk.ac.ebi.age.model.Submission;

public interface AgeSemanticValidator
{
 boolean validate(Submission s, LogNode log);
}