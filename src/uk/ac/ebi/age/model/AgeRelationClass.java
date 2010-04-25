package uk.ac.ebi.age.model;

import java.util.Collection;

public interface AgeRelationClass extends AgeClassProperty, AgeAbstractClass
{

 boolean isWithinRange(AgeClass key);
 boolean isWithinDomain(AgeClass key);

 void addSubClass(AgeRelationClass makeRelationsBranch);
 void addSuperClass(AgeRelationClass ageRelCls);

 void addDomainClass(AgeClass dmCls);

 void addRangeClass(AgeClass dmCls);

 String getName();

 Collection<AgeClass> getRange();

 Collection<AgeClass> getDomain();

 void setCustom(boolean b);
 boolean isCustom();

 AgeRelationClass getInverseClass();
 void setInverseClass(AgeRelationClass ageEl);
 
 void setDefined(boolean b);
 boolean isDefined();
}
