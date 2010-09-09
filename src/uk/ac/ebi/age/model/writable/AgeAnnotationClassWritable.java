package uk.ac.ebi.age.model.writable;

import uk.ac.ebi.age.model.AgeAnnotationClass;

public interface AgeAnnotationClassWritable extends AgeAnnotationClass
{

 void setAbstract(boolean b);

 void addSuperClass(AgeAnnotationClass cl);

 void addSubClass(AgeAnnotationClass cl);

 void addAlias(String ali);

}
