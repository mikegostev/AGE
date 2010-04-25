package uk.ac.ebi.age.parser.impl;

import java.util.ArrayList;
import java.util.List;

import uk.ac.ebi.age.parser.BlockHeader;
import uk.ac.ebi.age.parser.ColumnHeader;

public class BlockHeaderImpl implements BlockHeader
{
 private ColumnHeader classCol;
 private List<ColumnHeader> props=new ArrayList<ColumnHeader>(30);
 
 /* (non-Javadoc)
  * @see uk.ac.ebi.age.parser.impl.BlockHeader#setClassColumnHeader(uk.ac.ebi.age.parser.ColumnHeader)
  */
 public void setClassColumnHeader(ColumnHeader cc)
 {
  classCol=cc;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.parser.impl.BlockHeader#addColumnHeader(uk.ac.ebi.age.parser.ColumnHeader)
  */
 public void addColumnHeader(ColumnHeader chd)
 {
  props.add(chd);
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.parser.impl.BlockHeader#getClassColumnHeader()
  */
 public ColumnHeader getClassColumnHeader()
 {
  return classCol;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.parser.impl.BlockHeader#getColumnHeaders()
  */
 public List<ColumnHeader> getColumnHeaders()
 {
  return props;
 }
}
