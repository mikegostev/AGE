package uk.ac.ebi.age.storage.index;

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
import uk.ac.ebi.age.storage.AgeStorage;

import com.pri.util.collection.Collections;

public class LuceneDetachedFullTextIndex implements TextIndexWritable
{
 private String objectIdField = "_###ObjID";
 private String moduleIdField = "_###ModID";
 private String clusterIdField = "_###ClstID";
 
// private static final String AGEOBJECTFIELD="AgeObject";
 private final String defaultFieldName;
 
 private Directory index;
 private final StandardAnalyzer analyzer = new StandardAnalyzer();
 private final QueryParser queryParser;
 private IndexSearcher searcher;
 
 private final AgeStorage storage;
 
 private final AgeQuery query;
 private final List<TextFieldExtractor> extractors;
 
 private boolean dirty=false;

 public LuceneDetachedFullTextIndex(AgeQuery qury, Collection<TextFieldExtractor> exts, AgeStorage st) throws IOException
 {
  this(qury,exts,st,null);
 }

 public LuceneDetachedFullTextIndex(AgeQuery qury, Collection<TextFieldExtractor> exts, AgeStorage st, File path) throws IOException
 {
  query=qury;
  extractors=new ArrayList<TextFieldExtractor>(exts.size()+3);
  storage = st;
  
  extractors.addAll(exts);
  
  defaultFieldName = extractors.get(0).getName();
  
  for( TextFieldExtractor ext : extractors )
  {
   if(objectIdField.equals( ext.getName() ) )
    objectIdField += "#";
   else if(moduleIdField.equals( ext.getName() ) )
    moduleIdField += "#";
   else if(clusterIdField.equals( ext.getName() ) )
    clusterIdField += "#";
  }
  
  extractors.add( new TextFieldExtractor(objectIdField, new TextValueExtractor()
  {
   @Override
   public String getValue(AgeObject ao)
   {
    return ao.getId();
   }
  }, true) );

  extractors.add( new TextFieldExtractor(moduleIdField, new TextValueExtractor()
  {
   @Override
   public String getValue(AgeObject ao)
   {
    return ao.getModuleKey().getModuleId();
   }
  }, true) );
  
  extractors.add( new TextFieldExtractor(clusterIdField, new TextValueExtractor()
  {
   @Override
   public String getValue(AgeObject ao)
   {
    return ao.getModuleKey().getClusterId();
   }
  }, true) );
  
  queryParser = new QueryParser( defaultFieldName, analyzer);

  
  if( path == null )
   index = new RAMDirectory();
  else
  {
   index = FSDirectory.open( path.toPath() );

   
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
    if( query == null || (query=query.trim()).length() == 0 )
     q = new MatchAllDocsQuery();
    else
     q = queryParser.parse(query);
    
    TopDocs docs = searcher.search(q, offs+limit);

    for( int i = offs; i < docs.scoreDocs.length; i++)
    {
     Document doc = searcher.doc(docs.scoreDocs[i].doc);
     
     res.add( storage.getObject(doc.get(clusterIdField), doc.get(moduleIdField), doc.get(objectIdField)) );
     
     if( aggs != null )
     {
      
      for(String fld : aggs)
      {
       String val = doc.get(fld);
       
       int ival = 0;
       
       try
       {
        ival = Integer.parseInt(val);
       }
       catch (Throwable e)
       {
       }
       
       selection.aggregate(fld,ival);
      }
     }
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
  indexList( aol, append );
 }
 
 protected void indexList(List<AgeObject> aol, boolean append )
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

   for( Document d : new DocCollection(aol, extractors,0))
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
