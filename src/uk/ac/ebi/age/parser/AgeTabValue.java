package uk.ac.ebi.age.parser;

public class AgeTabValue extends AgeTabElement
{
 private String value;
 private ColumnHeader colHeader;

 public AgeTabValue(int row, int col, String value, ColumnHeader prop)
 {
  super(row, col);
  this.value=value;
  colHeader=prop;
 }

 public String getValue()
 {
  return value;
 }

 public ColumnHeader getColumnHeader()
 {
  return colHeader;
 }
}
