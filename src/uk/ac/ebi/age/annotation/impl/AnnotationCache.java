package uk.ac.ebi.age.annotation.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import uk.ac.ebi.age.annotation.Topic;

public class AnnotationCache implements Serializable
{

 private static final long serialVersionUID = 1L;

 private long verison;

 private Map< Topic, SortedMap<String, Object> > annotMap;

 public AnnotationCache()
 {
  annotMap = new HashMap<Topic, SortedMap<String,Object>>();
 }
 
 public boolean addAnnotation(Topic tpc, String objId, Object value)
 {
  SortedMap<String, Object> tMap = annotMap.get(tpc);

  if(tMap == null)
  {
   tMap = new TreeMap<String, Object>();

   annotMap.put(tpc, tMap);
  }


  tMap.put(objId, value);

  return true;
 }

 public boolean removeAnnotation(Topic tpc, String id, boolean rec)
 {
  Collection<SortedMap<String, Object>> maps;

  if(tpc == null)
   maps = annotMap.values();
  else
  {
   SortedMap<String, Object> tMap = annotMap.get(tpc);

   if(tMap != null)
    maps = java.util.Collections.singleton(tMap);
   else
    maps = java.util.Collections.emptyList();
  }

  boolean removed = false;

  for(SortedMap<String, Object> tMap : maps)
  {

   if(!rec)
    return tMap.remove(id) != null;
   else
   {
    Map<String, Object> smp = tMap.tailMap(id);

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
  SortedMap<String, Object> tMap = annotMap.get(tpc);

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
