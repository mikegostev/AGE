package uk.ac.ebi.age.storage.exeption;


public class SubmissionStoreException extends Exception
{

 private static final long serialVersionUID = 1L;

 public SubmissionStoreException(String string)
 {
  super(string);
 }

 
 public SubmissionStoreException(String string, Exception e)
 {
  super(string,e);
 }

}
