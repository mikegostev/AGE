package uk.ac.ebi.age.annotation.impl;

import java.io.File;

import uk.ac.ebi.age.annotation.AnnotationDomain;
import uk.ac.ebi.age.annotation.AnnotationStorage;

public class InMemoryAnnotationStorage implements AnnotationStorage
{

 public InMemoryAnnotationStorage(File annotationDbDir)
 {
  // TODO Auto-generated constructor stub
 }

 @Override
 public void addAnnotation(AnnotationDomain domain, String subdomain, String objectID, String annotationClass, String annotationID, Object value)
 {
  // TODO Auto-generated method stub
  throw new dev.NotImplementedYetException();
  //
 }

 @Override
 public Object getAnnotation(AnnotationDomain domain, String subdomain, String objectID, String annotationClass, String annotationID)
 {
  // TODO Auto-generated method stub
  throw new dev.NotImplementedYetException();
  //return null;
 }

}
