package uk.ac.ebi.age.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.ebi.age.model.IdScope;

public class AgeTabObject
{
 private int row;
 private String id;
 private Map<ClassReference, List<AgeTabValue>> values = new HashMap<ClassReference, List<AgeTabValue>>();
 private boolean idIsDefined; // Whether some ID was defined in submission file
 private boolean isPrototype;
 private IdScope scope;

 public AgeTabObject( int rw )
 {
  row=rw;
 }
 
 public void addValue(AgeTabValue val)
 {
  List<AgeTabValue> vl = values.get(val.getColumnHeader());
  
  if( vl == null )
   values.put(val.getColumnHeader(), vl=new ArrayList<AgeTabValue>(20) );
  
  vl.add(val);
 }
 

 public void setId(String objId)
 {
  id=objId;
 }
 
 public String getId()
 {
  return id;
 }

 public List<AgeTabValue> getValues( ClassReference col )
 {
  return values.get(col);
 }
 
 public int getRow()
 {
  return row;
 }

 public void setIdDefined(boolean b)
 {
  idIsDefined=b;
 }
 
 public boolean isIdDefined()
 {
  return idIsDefined;
 }

 public void setPrototype(boolean equals)
 {
  isPrototype = equals;
 }

 public boolean isPrototype()
 {
  return isPrototype;
 }

 public IdScope getIdScope()
 {
  return scope;
 }

 public void setIdScope(IdScope s)
 {
  scope=s;
 }
}
