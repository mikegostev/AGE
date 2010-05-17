package uk.ac.ebi.age.model;

import java.util.Collection;

public interface OrLogicRestriction extends AgeRestriction
{
 Collection<AgeRestriction> getOperands();
}
