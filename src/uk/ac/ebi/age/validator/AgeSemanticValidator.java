package uk.ac.ebi.age.validator;

import java.util.Collection;

import uk.ac.ebi.age.log.LogNode;
import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.model.AgeRelation;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.Submission;

public interface AgeSemanticValidator
{
 boolean validate(Submission s, LogNode log);
 boolean validate(Submission subm, SemanticModel mod, LogNode log);

 boolean validateRelations(AgeObject key, Collection<? extends AgeRelation> value, LogNode log);

}
