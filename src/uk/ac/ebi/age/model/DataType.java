package uk.ac.ebi.age.model;

public enum DataType
{
 INTEGER(false,true),
 REAL   (false,true),
 STRING (true,false),
 BOOLEAN(false,false);
 
 
 boolean isTextual;
 boolean isNumeric;
 
 private DataType(boolean txt, boolean num)
 {
  isTextual = txt;
  isNumeric = num;
 }
 
 public boolean isTextual()
 {
  return isTextual;
 }
 
 public boolean isNumeric()
 {
  return isNumeric;
 }
}
