package uk.ac.ebi.age.model;

import java.util.Collection;

/**
@model
*/

public interface AgeClass extends AgeSemanticElement, AgeAbstractClass, AttributedClass
{

 boolean isCustom();

 String getName();

 @Deprecated
 Collection<AgeRestriction> getObjectRestrictions();
 @Deprecated
 Collection<AgeRestriction> getAllObjectRestrictions();



 @Deprecated
 Collection<AgeRestriction> getRestrictions();
 @Deprecated
 Collection<AgeRestriction> getAllRestrictions();


 Collection<AgeClass> getSuperClasses();
 Collection<AgeClass> getSubClasses();

 String getIdPrefix();

 Collection<RelationRule> getRelationRules();
 Collection<RelationRule> getAllRelationRules();
 
}

