package uk.ac.ebi.age.model;

import java.util.Collection;

public interface AgeObjectProperty
{
 Collection<? extends AgeAttribute> getQualifiers();
 
 public int getOrder();

}
