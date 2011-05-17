package uk.ac.ebi.age.annotation;

public interface AnnotationStorage
{
 void addAnnotation( AnnotationDomain domain, String subdomain, String objectID, String annotationClass, String annotationID, Object value );
 Object getAnnotation( AnnotationDomain domain, String subdomain, String objectID, String annotationClass, String annotationID );
}
