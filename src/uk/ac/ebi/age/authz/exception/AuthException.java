package uk.ac.ebi.age.authz.exception;

public class AuthException extends Exception
{

 public AuthException()
 {
  super("Authorization exception");
 }

 public AuthException(String string)
 {
  super( string );
 }

 public AuthException(String string, Exception e)
 {
  super( string, e );
 }

}
