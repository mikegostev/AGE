package uk.ac.ebi.age.ext.log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class SimpleLogNode implements LogNode,Serializable
{
 private static final long serialVersionUID = 1L;

 private String            nodeMessage;
 private Level             level;

 private List<LogNode>     subNodes;

 public SimpleLogNode()
 {
 }
 
 public SimpleLogNode(Level l, String msg)
 {
  nodeMessage = msg;
  level = l;
 }

 @Override
 public void log(Level lvl, String msg)
 {
  if(subNodes == null)
   subNodes = new ArrayList<LogNode>(10);

  subNodes.add(new SimpleLogNode(lvl, msg));
 }

 @Override
 public LogNode branch(String msg)
 {
  if(subNodes == null)
   subNodes = new ArrayList<LogNode>(10);

  LogNode nnd = new SimpleLogNode(null, msg);

  subNodes.add(nnd);

  return nnd;
 }

 @Override
 public void append(LogNode node)
 {
  if(subNodes == null)
   subNodes = new ArrayList<LogNode>(10);

  subNodes.add(node);
 }

 @Override
 public String getMessage()
 {
  return nodeMessage;
 }

 @Override
 public Level getLevel()
 {
  return level;
 }

 @Override
 public void setLevel(Level l)
 {
  level = l;
 }

 @Override
 public List<LogNode> getSubNodes()
 {
  return subNodes;
 }

 public static void setLevels( LogNode ln )
 {
  if( ln.getSubNodes() == null )
  {
   if( ln.getLevel() == null )
    ln.setLevel(Level.INFO);
   
   return;
  }
  
  LogNode.Level maxLevel = Level.getMinLevel();
  
  for( LogNode snd : ln.getSubNodes() )
  {
   setLevels(snd);
   
   if( snd.getLevel().getPriority() > maxLevel.getPriority() )
    maxLevel = snd.getLevel();
  }
  
  ln.setLevel(maxLevel);
 }
}
