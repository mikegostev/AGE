package uk.ac.ebi.age.model;

import java.util.Collection;

public interface AgeObjectProperty extends Attributed
{
 @Override
 Collection<? extends AgeAttribute> getAttributes();
 
 public int getOrder();

 AgePropertyClass getAgeElClass();
}
