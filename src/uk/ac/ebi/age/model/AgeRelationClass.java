package uk.ac.ebi.age.model;

import java.util.Collection;

public interface AgeRelationClass extends AgeClassProperty, AgeAbstractClass
{

 boolean isWithinRange(AgeClass key);
 boolean isWithinDomain(AgeClass key);

 String getId();
 String getName();

 Collection<AgeClass> getRange();

 Collection<AgeClass> getDomain();

 boolean isCustom();

 AgeRelationClass getInverseClass();
 
 boolean isImplicit();
 
 public void resetModel();

}
