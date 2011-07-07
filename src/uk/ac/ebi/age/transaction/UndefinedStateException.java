package uk.ac.ebi.age.transaction;

import java.io.IOException;

public class UndefinedStateException extends TransactionException
{

 public UndefinedStateException(String string, IOException e)
 {
  super(string, e);
 }

}
