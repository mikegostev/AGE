package uk.ac.ebi.age.validator;

import java.util.Collection;

import uk.ac.ebi.age.ext.log.LogNode;
import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.AgeRelation;
import uk.ac.ebi.age.model.DataModule;
import uk.ac.ebi.age.model.SemanticModel;

public interface AgeSemanticValidator
{
 boolean validate(DataModule s, LogNode log);
 boolean validate(DataModule subm, SemanticModel mod, LogNode log);

 boolean validateRelations(AgeClass objClass, Collection<? extends AgeRelation> rels, LogNode log);

}
