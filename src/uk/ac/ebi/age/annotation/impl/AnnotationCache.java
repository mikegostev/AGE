package uk.ac.ebi.age.annotation.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import uk.ac.ebi.age.annotation.Topic;

public class AnnotationCache
{
 private long verison;

 private Map< Topic, SortedMap<String, Serializable> > annotMap;

 
 public boolean addAnnotation(Topic tpc, String objId, Serializable value)
 {
  SortedMap<String, Serializable> tMap = annotMap.get(tpc);

  if(tMap == null)
  {
   tMap = new TreeMap<String, Serializable>();

   annotMap.put(tpc, tMap);
  }


  tMap.put(objId, value);

  return true;
 }

 public boolean removeAnnotation(Topic tpc, String id, boolean rec)
 {
  Collection<SortedMap<String, Serializable>> maps;

  if(tpc == null)
   maps = annotMap.values();
  else
  {
   SortedMap<String, Serializable> tMap = annotMap.get(tpc);

   if(tMap != null)
    maps = java.util.Collections.singleton(tMap);
   else
    maps = java.util.Collections.emptyList();
  }

  boolean removed = false;

  for(SortedMap<String, Serializable> tMap : maps)
  {

   if(!rec)
    return tMap.remove(id) != null;
   else
   {
    Map<String, Serializable> smp = tMap.tailMap(id);

    Iterator<String> keys = smp.keySet().iterator();

    while(keys.hasNext())
    {
     String key = keys.next();

     if(key.startsWith(id))
     {
      keys.remove();

      removed = true;
     }
     else
      break;
    }

   }

  }

  return removed;
 }

 public Object getAnnotation(Topic tpc, String id)
 {
  SortedMap<String, Serializable> tMap = annotMap.get(tpc);

  if(tMap == null)
   return null;

  return tMap.get(id);
 }

 public long getVerison()
 {
  return verison;
 }

 public void setVerison(long verison)
 {
  this.verison = verison;
 }

}
