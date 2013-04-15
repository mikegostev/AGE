package uk.ac.ebi.age.ontology;

import uk.ac.ebi.age.authz.Subject;
import uk.ac.ebi.mg.collection.Named;

public interface Ontology  extends Subject, Named<String>
{

 public String getName();

 public String getDescription();

 public String getURL();

}
