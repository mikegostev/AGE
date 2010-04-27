package uk.ac.ebi.age.service;

import uk.ac.ebi.age.service.impl.IdGeneratorImpl;

public abstract class IdGenerator
{
 private static IdGenerator instance = new IdGeneratorImpl();
 
 public static IdGenerator getInstance()
 {
  return instance;
 }

 public abstract String getStringId();
 
 
}
