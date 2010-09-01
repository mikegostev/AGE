package uk.ac.ebi.age.model.writable;

import uk.ac.ebi.age.model.AgeAnnotation;
import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.AgeRestriction;

/**
@model
*/

public interface AgeClassWritable extends AgeClass
{
 void addObjectRestriction(AgeRestriction rest);

 void addAttributeRestriction(AgeRestriction rest);

 void addSubClass(AgeClass cls);
 void addSuperClass(AgeClass cls);

 void setId(String string);
 
 void addAnnotation( AgeAnnotation annt );
}

