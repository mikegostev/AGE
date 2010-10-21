package uk.ac.ebi.age.service;

import uk.ac.ebi.age.service.impl.IdGeneratorImpl;

public abstract class IdGenerator
{
 private static IdGenerator instance = new IdGeneratorImpl();
 
 public static IdGenerator getInstance()
 {
  return instance;
 }
 
 public static void setInstance( IdGenerator gen )
 {
  instance=gen;
 }

 public abstract String getStringId();

 public abstract void shutdown();
 
 
}
