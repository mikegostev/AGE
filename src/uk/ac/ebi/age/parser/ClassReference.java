package uk.ac.ebi.age.parser;

import java.util.Map;
import java.util.TreeMap;

public class ClassReference extends AgeTabElement
{

 private boolean custom;
 private String name;
 private Map<String,String> flags;
 private ClassReference qualifier;

 public ClassReference()
 {
  super(0,0);
 }
 
 public ClassReference(int row, int col)
 {
  super(row, col);
 }
 
 
 public void setCustom(boolean t)
 {
  custom=t;
 }

 public boolean isCustom()
 {
  return custom;
 }

 public void setName(String nm)
 {
  name=nm;
 }

 public String getName()
 {
  return name;
 }

 public ClassReference getQualifier()
 {
  return qualifier;
 }
 
 public void setQualifier(ClassReference s)
 {
  qualifier = s;
 }

 public void addFlag(String nm, String vl)
 {
  if( flags == null )
   flags = new TreeMap<String, String>();
  
  flags.put(nm, vl);
 }

 public boolean isFlagSet( String fl )
 {
  if( flags == null )
  return false;
  
  return flags.containsKey(fl);
 }
 
 public String getFlagValue( String fl )
 {
  if( flags == null )
  return null;
  
  return flags.get(fl);
 }
 
 public Map<String,String> getFlags()
 {
  return flags;
 }




}
