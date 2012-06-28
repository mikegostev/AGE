package uk.ac.ebi.age.storage.impl.ser;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.storage.index.TextFieldExtractor;
import uk.ac.ebi.mg.executor.DefaultExecutorService;

public class DocCollection implements Iterable<Document>
{
 private List<AgeObject> objectList;
 private Collection<TextFieldExtractor> extractors;
 
 public DocCollection( List<AgeObject> objLst, Collection<TextFieldExtractor> exts)
 {
  objectList = objLst;
  extractors = exts;
 }
 
 
 @Override
 public Iterator<Document> iterator()
 {
  if( objectList.size() < 50 )
   return new SimpleIterator();
  
  return new MTIterator();
 }
 
 private class MTIterator implements Iterator<Document>
 {
  private BlockingQueue<Document> cQueue;
  private BlockingQueue<Document>[] queues;
  private int nxtQind = 1;
  
  private Document cDoc;
  
  @SuppressWarnings("unchecked")
  MTIterator()
  {
   int nq = Runtime.getRuntime().availableProcessors()+1;
   
   queues = new BlockingQueue[nq];
   
   int ql = objectList.size()/nq;
   
   for( int i=0; i < nq; i++ )
   {
    queues[i] = new ArrayBlockingQueue<Document>(ql);
    
    if( i == 0 )
     cQueue = queues[0];
   
    int beg = i*ql;
    int end = i==nq-1?objectList.size():beg+ql;
    
    DefaultExecutorService.getExecutorService().execute( new DocPreparator(queues[i], beg, end ) );
   }
  }
  
  @Override
  public boolean hasNext()
  {
   if(cDoc != null)
    return true;

   Document d;

   while(true)
   {

    while(true)
    {
     try
     {
      d = cQueue.take();
      break;
     }
     catch(InterruptedException e)
     {
     }
    }
    
    if( d.getFields() != null && d.getFields().size() > 0 )
    {
     cDoc = d;
     return true;
    }
    
    if( nxtQind >= queues.length )
     return false;
    
    cQueue = queues[ nxtQind++ ];
   }
  }

  @Override
  public Document next()
  {
   if( ! hasNext() )
    throw new NoSuchElementException();
   
   Document d = cDoc;
   cDoc = null;
   
   return d;
  }

  @Override
  public void remove()
  {
  }
  
 }

 private class DocPreparator implements Runnable
 {
  int begin, end;
  BlockingQueue<Document> queue;
  
  public DocPreparator(BlockingQueue<Document> blockingQueue, int beg, int end)
  {
   this.begin = beg;
   this.end = end;
   
   queue = blockingQueue;
  }

  @Override
  public void run()
  {
   for( int i=begin; i < end; i++ )
   {
    while( true )
    {
     try
     {
      queue.put( convert(objectList.get(i)) );
      break;
     }
     catch(InterruptedException e)
     {
     }
    }
   }
   
   while( true )
   {
    try
    {
     queue.put( new Document() );
     break;
    }
    catch(InterruptedException e)
    {
    }
   }
  }
  
 }

 private class SimpleIterator implements Iterator<Document>
 {
  private Iterator<AgeObject> objIter = objectList.iterator();

  @Override
  public boolean hasNext()
  {
   return objIter.hasNext();
  }

  @Override
  public Document next()
  {
   return convert( objIter.next() );
  }

  @Override
  public void remove()
  {
  }
  
 }
 
 private Document convert( AgeObject obj )
 {
  Document doc = new Document();
  
  for(TextFieldExtractor tfe : extractors )
  {
   String name = tfe.getName();
   String val = tfe.getExtractor().getValue(obj);
 
   doc.add(new Field(name, val, Field.Store.NO, Field.Index.ANALYZED));
  }
  
  return doc;
 }
}
