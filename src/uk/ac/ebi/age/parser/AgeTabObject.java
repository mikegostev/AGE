package uk.ac.ebi.age.parser;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AgeTabObject
{
 private int row;
 private String id;
 private Map<ColumnHeader, List<AgeTabValue>> values = new HashMap<ColumnHeader, List<AgeTabValue>>();

 public AgeTabObject( int rw )
 {
  row=rw;
 }
 
 public void addValue(int row, int col, String val, ColumnHeader prop)
 {
  List<AgeTabValue> vl = values.get(prop);
  
  if( vl == null )
   values.put(prop, vl=new LinkedList<AgeTabValue>() );
  
  vl.add(new AgeTabValue(row, col, val, prop));
 }

 public void setId(String objId)
 {
  id=objId;
 }
 
 public String getId()
 {
  return id;
 }

 public List<AgeTabValue> getValues( ColumnHeader col )
 {
  return values.get(col);
 }
 
 public int getRow()
 {
  return row;
 }
}
