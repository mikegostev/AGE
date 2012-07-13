package uk.ac.ebi.age.ext.user.exception;

import java.io.Serializable;

public class NotAuthorizedException extends Exception implements Serializable
{

 private static final long serialVersionUID = 1L;

 public NotAuthorizedException()
 {
 }
 
 public NotAuthorizedException( String msg )
 {
  super( msg );
 }

}
