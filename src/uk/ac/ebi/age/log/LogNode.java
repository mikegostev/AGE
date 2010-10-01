package uk.ac.ebi.age.log;

import java.util.List;

public interface LogNode
{
 static enum Level
 {
  ERROR,
  WARN,
  INFO,
  DEBUG
 }
 
 void log(Level lvl, String msg);
 LogNode branch(String msg);
 
 String getMessage();
 Level getLevel();
 List<LogNode> getSubNodes();
}
