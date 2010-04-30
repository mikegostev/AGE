package uk.ac.ebi.age.service.impl;

import uk.ac.ebi.age.service.IdGenerator;

public class IdGeneratorImpl extends IdGenerator
{
 private long init=(System.currentTimeMillis() - 1270000000L*1000L)*100;

 @Override
 public String getStringId()
 {
  return String.valueOf(init++);
 }

}
