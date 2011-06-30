package uk.ac.ebi.age.classif.exception;

public class ClassifierNotFoundException extends TagException
{
 public ClassifierNotFoundException()
 {
  super("Classifier not found");
 }
}
