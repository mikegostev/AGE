package uk.ac.ebi.age.classif.exception;

public class ClassifierExistsException extends TagException
{
 public ClassifierExistsException()
 {
  super("Classifier exists");
 }
}
