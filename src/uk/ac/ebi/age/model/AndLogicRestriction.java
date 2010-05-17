package uk.ac.ebi.age.model;

import java.util.Collection;

public interface AndLogicRestriction extends AgeRestriction
{
 Collection<AgeRestriction> getOperands();
}
