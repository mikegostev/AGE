package uk.ac.ebi.age.parser;


public class AgeTabValue extends AgeTabElement
{
 private CellValue value;
 private ClassReference colHeader;

 public AgeTabValue(int row, int col, ClassReference prop, CellValue value)
 {
  super(row, col);
  this.value=value;
  colHeader=prop;
 }

 public String getValue()
 {
  return value.getValue();
 }

 public CellValue getCellValue()
 {
  return value;
 }

 public ClassReference getColumnHeader()
 {
  return colHeader;
 }

 public boolean matchPrefix( String pfx )
 {
  return value.matchSubstring(pfx, 0);
 }

 public void trim()
 {
  value.trim();
 }
 
}
