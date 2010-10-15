package uk.ac.ebi.age.model.writable;

import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.DataType;

public interface AgeAttributeClassWritable extends AgeAttributeClass, AgeAbstractClassWritable, AttributedClassWritable
{
 void addSubClass(AgeAttributeClass sbcls);
 void addSuperClass(AgeAttributeClass sbcls);

 void setDataType( DataType typ );
 void setAbstract(boolean b);

 void addAlias(String ali);
 
 void setTargetClass(AgeClass cls);
}
