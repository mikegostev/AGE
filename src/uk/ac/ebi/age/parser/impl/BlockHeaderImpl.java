package uk.ac.ebi.age.parser.impl;

import java.util.ArrayList;
import java.util.List;

import uk.ac.ebi.age.parser.BlockHeader;
import uk.ac.ebi.age.parser.ClassReference;

public class BlockHeaderImpl implements BlockHeader
{
 private ClassReference classCol;
 private List<ClassReference> props=new ArrayList<ClassReference>(30);
 
 /* (non-Javadoc)
  * @see uk.ac.ebi.age.parser.impl.BlockHeader#setClassColumnHeader(uk.ac.ebi.age.parser.ColumnHeader)
  */
 public void setClassColumnHeader(ClassReference cc)
 {
  classCol=cc;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.parser.impl.BlockHeader#addColumnHeader(uk.ac.ebi.age.parser.ColumnHeader)
  */
 public void addColumnHeader(ClassReference chd)
 {
  props.add(chd);
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.parser.impl.BlockHeader#getClassColumnHeader()
  */
 public ClassReference getClassColumnHeader()
 {
  return classCol;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.parser.impl.BlockHeader#getColumnHeaders()
  */
 public List<ClassReference> getColumnHeaders()
 {
  return props;
 }
}
