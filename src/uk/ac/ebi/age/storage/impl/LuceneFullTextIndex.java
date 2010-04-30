package uk.ac.ebi.age.storage.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.storage.TextIndex;
import uk.ac.ebi.age.storage.index.TextFieldExtractor;

public class LuceneFullTextIndex implements TextIndex
{
// private static final String AGEOBJECTFIELD="AgeObject";
 private String defaultFieldName;
 
 private Directory index = new RAMDirectory();
 private StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);
 
 private List<AgeObject> objectList;
 
 public LuceneFullTextIndex()
 {

 }

// public void index(List<AgeObject> aol, TextValueExtractor extr)
// {
//  try
//  {
//   IndexWriter iWriter = new IndexWriter(index, analyzer, true,
//     IndexWriter.MaxFieldLength.UNLIMITED);
//
//   objectList=aol;
//   for(AgeObject ao : objectList )
//   {
//    Document doc = new Document();
//    doc.add(new Field(TEXTFIELD, extr.getValue(ao), Field.Store.NO, Field.Index.ANALYZED));
//    
//    iWriter.addDocument(doc);
//   }
//
//   iWriter.close();
//  }
//  catch(CorruptIndexException e)
//  {
//   // TODO Auto-generated catch block
//   e.printStackTrace();
//  }
//  catch(IOException e)
//  {
//   // TODO Auto-generated catch block
//   e.printStackTrace();
//  }
// }
 
 public void index(List<AgeObject> aol, Collection<TextFieldExtractor> extf)
 {
  try
  {
   IndexWriter iWriter = new IndexWriter(index, analyzer, true,
     IndexWriter.MaxFieldLength.UNLIMITED);

   objectList=aol;
   for(AgeObject ao : objectList )
   {
    Document doc = new Document();
    
    for(TextFieldExtractor tfe : extf )
     doc.add(new Field(tfe.getName(), tfe.getExtractor().getValue(ao), Field.Store.NO, Field.Index.ANALYZED));
    
    iWriter.addDocument(doc);
   }

   iWriter.close();
   
   defaultFieldName = extf.iterator().next().getName();
  }
  catch(CorruptIndexException e)
  {
   // TODO Auto-generated catch block
   e.printStackTrace();
  }
  catch(IOException e)
  {
   // TODO Auto-generated catch block
   e.printStackTrace();
  }
 }
 
 public List<AgeObject> select(String query)
 {
  final List<AgeObject> res = new ArrayList<AgeObject>();
  
  Query q;
  try
  {
   q = new QueryParser( Version.LUCENE_30, defaultFieldName, analyzer).parse(query);

   final IndexSearcher searcher = new IndexSearcher(index, true);
   
   //TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
   searcher.search(q, new Collector()
   {
    
    @Override
    public void setScorer(Scorer arg0) throws IOException
    {
    }
    
    @Override
    public void setNextReader(IndexReader arg0, int arg1) throws IOException
    {
    }
    
    @Override
    public void collect(int docId) throws IOException
    {
     System.out.println("Found: "+docId );
     
     res.add( objectList.get(docId) );
    }
    
    @Override
    public boolean acceptsDocsOutOfOrder()
    {
     return true;
    }
   });
  }
  catch(ParseException e)
  {
   // TODO Auto-generated catch block
   e.printStackTrace();
  }
  catch(IOException e)
  {
   // TODO Auto-generated catch block
   e.printStackTrace();
  }

  
  //ScoreDoc[] hits = collector.topDocs().scoreDocs;
  
  return res;
 }


}
