package uk.ac.ebi.age.storage.impl.ser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;

import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.query.AgeQuery;
import uk.ac.ebi.age.storage.index.AgeAttachedIndex;
import uk.ac.ebi.age.storage.index.DocCollection;
import uk.ac.ebi.age.storage.index.Selection;
import uk.ac.ebi.age.storage.index.TextFieldExtractor;
import uk.ac.ebi.age.storage.index.TextIndexWritable;

import com.pri.util.collection.Collections;

public class LuceneFullTextIndex implements TextIndexWritable, AgeAttachedIndex
{
// private static final String AGEOBJECTFIELD="AgeObject";
 private final String defaultFieldName;
 
 private Directory index;
 private final StandardAnalyzer analyzer = new StandardAnalyzer();
 private final QueryParser queryParser;
 private IndexSearcher searcher;
 
 private List<AgeObject> objectList = Collections.emptyList();
 
 private final AgeQuery query;
 private final Collection<TextFieldExtractor> extractors;
 
 private boolean dirty=false;

 public LuceneFullTextIndex(AgeQuery qury, Collection<TextFieldExtractor> exts) throws IOException
 {
  this(qury,exts,null);
 }

 public LuceneFullTextIndex(AgeQuery qury, Collection<TextFieldExtractor> exts, File path) throws IOException
 {
  query=qury;
  extractors=exts;
  
  defaultFieldName = extractors.iterator().next().getName();
  
  queryParser = new QueryParser( defaultFieldName, analyzer);

  
  if( path == null )
   index = new RAMDirectory();
  else
  {
   index = FSDirectory.open(path.toPath());

   
   if( index.listAll().length != 0  )
    searcher = new IndexSearcher(DirectoryReader.open(index));
  }
 }

 @Override
 public void close()
 {
  try
  {
   if( index != null )
    index.close();
  }
  catch(IOException e)
  {
   // TODO Auto-generated catch block
   e.printStackTrace();
  }
 }
 
// public void index(List<AgeObject> aol, Collection<TextFieldExtractor> extf)
// {
//  try
//  {
//   IndexWriter iWriter = new IndexWriter(index, analyzer, false,
//     IndexWriter.MaxFieldLength.UNLIMITED);
//
//   if( objectList == null )
//    objectList=aol;
//   else
//    objectList.addAll(aol);
//   
//   for(AgeObject ao : objectList )
//   {
//    Document doc = new Document();
//    
//    for(TextFieldExtractor tfe : extf )
//     doc.add(new Field(tfe.getName(), tfe.getExtractor().getValue(ao), Field.Store.NO, Field.Index.ANALYZED));
//    
//    iWriter.addDocument(doc);
//   }
//
//   iWriter.close();
//   
//   defaultFieldName = extf.iterator().next().getName();
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
 
 
 @Override
 public int count(String query)
 {
  if( searcher == null )
   return 0;
  
  Query q;
  try
  {
   q = queryParser.parse(query);

   return searcher.count(q);
  }
  catch(IOException e)
  {
   // TODO Auto-generated catch block
   e.printStackTrace();
  }
  catch(ParseException e)
  {
   // TODO Auto-generated catch block
   e.printStackTrace();
  }

  
  //ScoreDoc[] hits = collector.topDocs().scoreDocs;
  
  return -1;
 }

 @Override
 public List<AgeObject> select(String query)
 {
  return select(query,0,-1, null).getObjects();
 }

 @Override
 public Selection select(String query, final int offs, final int limit, final Collection<String> aggs )
 {
  final Selection selection = new Selection();
  
  if( searcher == null )
  {
   selection.setObjects(Collections.<AgeObject>emptyList());
   
   return selection;
  }
  
  final List<AgeObject> res = new ArrayList<AgeObject>();
  
  Query q;
  
  try
  {
   if( query == null || (query = query.trim()).length() == 0 )
    q= new MatchAllDocsQuery();
   else
    q = queryParser.parse(query);
   
   TopDocs docs = searcher.search(q, offs+limit);

   for( int i = offs; i < docs.scoreDocs.length; i++)
   {
    Document doc = searcher.doc(docs.scoreDocs[i].doc);
    int objInd = doc.getField(DocCollection.OBJ_NO_FIELD).numericValue().intValue();
    
    res.add( objectList.get( objInd ) );  
   }
   
   selection.setObjects(res);
   selection.setTotalCount(docs.totalHits);
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
  
  return selection;
 }

 @Override
 public AgeQuery getQuery()
 {
  return query;
 }

 @Override
 public void index(List<AgeObject> aol, boolean append)
 {
  List<AgeObject> naol = null;
  
  int ind = 0;
  
  if( append )
  {
   ind = objectList.size();
   
   naol = new ArrayList<AgeObject>( aol.size() + objectList.size() );
   
   naol.addAll(objectList);
   naol.addAll(aol);
  }
  else
  {
   naol = new ArrayList<AgeObject>( aol.size() );
   
   naol.addAll(aol);
  }
  
  objectList = naol;
  
  indexList( aol, append, ind );
 }
 
 protected void indexList(List<AgeObject> aol, boolean append, int startInd  )
 {
  try
  {
   if( searcher != null )
   {
    searcher.getIndexReader().close();
   }
   
   IndexWriterConfig idxCfg = new IndexWriterConfig(analyzer);
   
   idxCfg.setRAMBufferSizeMB(50);
   idxCfg.setOpenMode(append?OpenMode.APPEND:OpenMode.CREATE);
   
   IndexWriter iWriter = new IndexWriter(index, idxCfg );

   for( Document d : new DocCollection(aol, extractors, startInd))
    iWriter.addDocument(d);
   
   iWriter.close();
   
   
   searcher = new IndexSearcher( DirectoryReader.open(index) );
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


 @Override
 public List<AgeObject> getObjectList()
 {
  return objectList;
 }

 protected void setObjectList( List<AgeObject> lst )
 {
  objectList = lst;
  
  indexList(objectList, false, 0);
 }

 @Override
 public boolean isDirty()
 {
  return dirty;
 }

 @Override
 public void setDirty(boolean dirty)
 {
  this.dirty=dirty;
 }
}
