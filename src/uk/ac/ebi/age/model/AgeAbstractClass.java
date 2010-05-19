package uk.ac.ebi.age.model;

import java.util.Collection;

public interface AgeAbstractClass
{
 Collection<? extends AgeAbstractClass> getSuperClasses();
 Collection<? extends AgeAbstractClass> getSubClasses();
 
 boolean isClassOrSubclass( AgeAbstractClass cl );
 
 String getName();
 String getId();

 Collection<AgeRestriction> getAttributeRestrictions();
 Collection<AgeRestriction> getAttributeAllRestrictions();

}
