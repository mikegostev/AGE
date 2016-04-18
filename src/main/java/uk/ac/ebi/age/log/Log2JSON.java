package uk.ac.ebi.age.log;

import java.io.IOException;

import uk.ac.ebi.age.ext.log.LogNode;

import com.pri.util.StringUtils;

public class Log2JSON
{
 
 public static void convert( LogNode nd, Appendable out ) throws IOException
 {
  convertNode(nd, out, 0);
 }
 
 private static void convertNode( LogNode ln, Appendable sb, int lyr ) throws IOException
 {
  sb.append("{\n");
  sb.append(" \"level\": \"").append(ln.getLevel().name()).append("\",\n");
  
  boolean needComma=false;
  
  if(  ln.getMessage() != null )
  {
   sb.append(" \"message\": \"");
   StringUtils.appendAsJSONStr(sb, ln.getMessage() );
   sb.append('"');

   needComma = true;
  }
  
  if( ln.getSubNodes() != null )
  {
   if( needComma )
    sb.append(",\n");

   sb.append(" \"subnodes\": [");
   
   needComma=false;
   
   for( LogNode snd : ln.getSubNodes() )
   {
    if( needComma )
     sb.append(",\n");
    else
     needComma = true;
    
    convertNode(snd,sb,lyr+1);
   }
   
   sb.append("]\n");
  }

  sb.append("}");
 }
 

}