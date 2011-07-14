package uk.ac.ebi.age.annotation;

import java.io.Serializable;

import uk.ac.ebi.age.entity.ID;

public interface AnnotationManager
{
 Object getAnnotation(Topic tpc, ID objId);

 boolean addAnnotation(Topic tpc, ID objId, Serializable value);
}
