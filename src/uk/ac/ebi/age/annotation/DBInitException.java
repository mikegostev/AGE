package uk.ac.ebi.age.annotation;


public class DBInitException extends Exception
{

 private static final long serialVersionUID = 1L;

 public DBInitException()
 {
  super("Annotation database initialization exception");
 }

 public DBInitException(Exception e)
 {
  super("Annotation database initialization exception", e);
 }
}
