package uk.ac.ebi.age.model;

import java.util.Collection;

/**
@model
*/

public interface AgeClass extends AgeSemanticElement, AgeAbstractClass
{

 boolean isCustom();

 String getName();

 void addObjectRestriction(AgeRestriction rest);
 Collection<AgeRestriction> getObjectRestrictions();
 Collection<AgeRestriction> getAllObjectRestrictions();


 void addAttributeRestriction(AgeRestriction rest);
 Collection<AgeRestriction> getAttributeRestrictions();
 Collection<AgeRestriction> getAttributeAllRestrictions();

 Collection<AgeRestriction> getRestrictions();
 Collection<AgeRestriction> getAllRestrictions();

 void addSubClass(AgeClass cls);
 void addSuperClass(AgeClass cls);


 Collection<AgeClass> getSuperClasses();
 Collection<AgeClass> getSubClasses();

 String getIdPrefix();

}

