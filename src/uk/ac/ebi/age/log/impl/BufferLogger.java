package uk.ac.ebi.age.log.impl;

import java.util.ArrayList;
import java.util.List;

import uk.ac.ebi.age.log.LogNode;

public class BufferLogger
{
 private LogNode rootNode;
 
 public BufferLogger()
 {
  rootNode = new LBNode( null, "" );
 }
 
 public LogNode getRootNode()
 {
  return rootNode;
 }
 
 class LBNode implements LogNode
 {
  private String nodeMessage;
  private Level level;
  
  private List<LogNode> subNodes;

  LBNode( Level l, String msg )
  {
   nodeMessage = msg;
   level = l;
  }
  
  @Override
  public void log(Level lvl, String msg)
  {
   if( subNodes == null )
    subNodes = new ArrayList<LogNode>(10);
   
   subNodes.add(new LBNode(lvl, msg));
  }

  @Override
  public LogNode branch(String msg)
  {
   if( subNodes == null )
    subNodes = new ArrayList<LogNode>(10);
   
   LogNode nnd = new LBNode(null, msg);
   
   subNodes.add(nnd);
   
   return nnd;
  }

  @Override
  public void append(LogNode node)
  {
   if( subNodes == null )
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
  public List<LogNode> getSubNodes()
  {
   return subNodes;
  }


  
 }
}
