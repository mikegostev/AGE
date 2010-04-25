package uk.ac.ebi.age.storage;

import java.util.Collection;
import java.util.List;

import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.storage.impl.AgeStorageIndex;
import uk.ac.ebi.age.storage.index.TextFieldExtractor;
import uk.ac.ebi.age.storage.index.TextValueExtractor;

public interface TextIndex extends AgeStorageIndex
{
 void index(List<AgeObject> aol, TextValueExtractor extr );
 void index(List<AgeObject> executeQuery, Collection<TextFieldExtractor> exts);

 
// void index(String txt, AgeObject ao);
// void close();

 List<AgeObject> select(String query);




}
