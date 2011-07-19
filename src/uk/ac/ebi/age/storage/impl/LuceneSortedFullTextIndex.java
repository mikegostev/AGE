package uk.ac.ebi.age.storage.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.query.AgeQuery;
import uk.ac.ebi.age.storage.index.KeyExtractor;
import uk.ac.ebi.age.storage.index.SortedTextIndexWritable;
import uk.ac.ebi.age.storage.index.TextFieldExtractor;

public class LuceneSortedFullTextIndex<KeyT> extends LuceneFullTextIndex implements SortedTextIndexWritable<KeyT>
{
 private class ObjComparator implements Comparator<AgeObject>
 {
  @Override
  public int compare(AgeObject o1, AgeObject o2)
  {
   return keyComparator.compare(keyExtractor.getKey(o1), keyExtractor.getKey(o2) );
  }
 }
 
 public ObjComparator objectComparator = new ObjComparator();
 
 public KeyExtractor<KeyT> keyExtractor;
 public Comparator<KeyT> keyComparator;

 public LuceneSortedFullTextIndex(AgeQuery qury, Collection<TextFieldExtractor> exts, KeyExtractor<KeyT> kext, Comparator<KeyT> keyComp)
 {
  super(qury,exts);
 }

 
 public int indexOfKey( KeyT key )
 {
  List<AgeObject> list = getObjectList();
  
  int low = 0;
  int high = list.size() - 1;

  while(low <= high)
  {
   int mid = (low + high) >>> 1;
   AgeObject midVal = list.get(mid);
   int cmp = keyComparator.compare( keyExtractor.getKey(midVal), key);

   if(cmp < 0)
    low = mid + 1;
   else if(cmp > 0)
    high = mid - 1;
   else
    return mid; // key found
  }
  return -(low + 1); // key not found
 }

 
 @Override
 public AgeObject getAgeObject(KeyT key)
 {
  int ind = indexOfKey(key);
  
  if( ind >= 0 )
   return getObjectList().get(ind);
  
  return null; // key not found
 }

 public void index(List<AgeObject> aol)
 {
  ArrayList<AgeObject> naol = null;


  if( getObjectList()!= null )
  {
   naol = new ArrayList<AgeObject>( aol.size() + getObjectList().size() );
   naol.addAll(getObjectList());
  }
  else 
   naol = new ArrayList<AgeObject>( aol.size());
  
  naol.addAll(aol);
  
  Collections.sort(aol, objectComparator );
  
  reset();
  
  super.indexList(naol);
 }
}
