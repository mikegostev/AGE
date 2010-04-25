package uk.ac.ebi.age.model.writable;

import uk.ac.ebi.age.model.AgeAttribute;
import uk.ac.ebi.age.model.FormatException;

public interface AgeAttributeWritable extends AgeAttribute
{

 void updateValue(String value) throws FormatException;

 void finalizeValue();

 void setOrder(int col);
}
