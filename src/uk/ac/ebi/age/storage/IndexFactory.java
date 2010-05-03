package uk.ac.ebi.age.storage;

import java.util.Collection;

import uk.ac.ebi.age.query.AgeQuery;
import uk.ac.ebi.age.storage.impl.LuceneFullTextIndex;
import uk.ac.ebi.age.storage.index.TextFieldExtractor;

public class IndexFactory
{
 private static IndexFactory instance = new IndexFactory() ;
 
 public static IndexFactory getInstance()
 {
  return instance;
 }

 public TextIndex createFullTextIndex(AgeQuery qury, Collection<TextFieldExtractor> exts)
 {
  return new LuceneFullTextIndex( qury, exts );
 }

}
