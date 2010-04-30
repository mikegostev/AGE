package uk.ac.ebi.age.parser;

import java.util.List;

public interface BlockHeader
{

 public abstract void setClassColumnHeader(ColumnHeader cc);

 public abstract void addColumnHeader(ColumnHeader chd);

 public abstract ColumnHeader getClassColumnHeader();

 public abstract List<ColumnHeader> getColumnHeaders();

}
