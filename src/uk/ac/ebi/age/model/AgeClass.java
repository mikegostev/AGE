package uk.ac.ebi.age.model;

import java.util.Collection;

/**
@model
*/

public interface AgeClass extends AgeSemanticElement, AgeAbstractClass
{

 boolean isCustom();

 String getName();

 Collection<AgeRestriction> getObjectRestrictions();
 Collection<AgeRestriction> getAllObjectRestrictions();


 Collection<AgeRestriction> getAttributeRestrictions();
 Collection<AgeRestriction> getAttributeAllRestrictions();

 Collection<AgeRestriction> getRestrictions();
 Collection<AgeRestriction> getAllRestrictions();


 Collection<AgeClass> getSuperClasses();
 Collection<AgeClass> getSubClasses();

 String getIdPrefix();

}

