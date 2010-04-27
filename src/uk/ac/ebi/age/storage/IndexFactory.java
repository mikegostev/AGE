package uk.ac.ebi.age.storage;

import uk.ac.ebi.age.storage.impl.LuceneFullTextIndex;

public class IndexFactory
{
 private static IndexFactory instance = new IndexFactory() ;
 
 public static IndexFactory getInstance()
 {
  return instance;
 }

 public TextIndex createFullTextIndex()
 {
  return new LuceneFullTextIndex();
 }

}
