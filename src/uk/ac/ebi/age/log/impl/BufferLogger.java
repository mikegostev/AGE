package uk.ac.ebi.age.log.impl;

import java.io.PrintWriter;

import uk.ac.ebi.age.ext.log.LogNode;
import uk.ac.ebi.age.ext.log.SimpleLogNode;

public class BufferLogger
{
 private SimpleLogNode rootNode;
 
 public BufferLogger()
 {
  rootNode = new SimpleLogNode( null, "" );
 }
 
 public SimpleLogNode getRootNode()
 {
  return rootNode;
 }
 
 public static void printBranch( LogNode node )
 {
  printBranch(node, new PrintWriter( System.out ), 0);
 }
 
 public static void printBranch( LogNode node, PrintWriter out  )
 {
  printBranch(node, out, 0);
 }

 
 private static void printBranch( LogNode node, PrintWriter out, int lvl )
 {
  for( int i=0; i < lvl; i++ )
   out.print("  ");
  
  if( node.getLevel() != null )
  {
   switch( node.getLevel() )
   {
    case DEBUG:
     out.print("DEBG: ");
     break;
    case INFO:
     out.print("INFO: ");
     break;
    case WARN:
     out.print("WARN: ");
     break;
    case ERROR:
     out.print("ERRR: ");
     break;
   }
   
   out.println(node.getMessage());
  }
  else
  {
   out.println(node.getMessage());
   
   if( node.getSubNodes() != null )
   {
    for(LogNode sbNode : node.getSubNodes() )
     printBranch(sbNode,out, lvl+1);
   }
  }
   
 }

 

}
