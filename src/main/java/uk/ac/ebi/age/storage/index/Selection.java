package uk.ac.ebi.age.storage.index;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import uk.ac.ebi.age.model.AgeObject;

import com.pri.util.Counter;

public class Selection
{
 private int                  totalCount;
 private Map<String, Counter> aggregators;
 public List<AgeObject>       objects;

 public int getTotalCount()
 {
  return totalCount;
 }

 public void setTotalCount(int totalCount)
 {
  this.totalCount = totalCount;
 }

 public List<AgeObject> getObjects()
 {
  return objects;
 }

 public void setObjects(List<AgeObject> objects)
 {
  this.objects = objects;
 }

 public Map<String, Counter> getAggregators()
 {
  return aggregators;
 }
 
 public int getAggregator( String name )
 {
  if( aggregators == null )
   return 0;
  
  Counter ag = aggregators.get( name );
    
  
  return ag==null?0:ag.intValue();
 }

 
 public void addAggregator( String name, int val )
 {
  if( aggregators == null )
   aggregators = new TreeMap<String, Counter>();
  
  aggregators.put(name, new Counter(val) );
 }

 public void aggregate(String fld, int ival)
 {
  if( aggregators == null )
   aggregators = new TreeMap<String, Counter>();
  
  Counter c = aggregators.get(fld);
  
  if( c == null )
   aggregators.put( fld, new Counter(ival) );
  else
   c.add(ival);
 }
}
