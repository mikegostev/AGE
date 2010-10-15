package uk.ac.ebi.age.parser.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import uk.ac.ebi.age.parser.AgeTabObject;
import uk.ac.ebi.age.parser.AgeTabSubmission;
import uk.ac.ebi.age.parser.AgeTabSyntaxParser;
import uk.ac.ebi.age.parser.BlockHeader;

public class AgeTabSubmissionImpl implements AgeTabSubmission
{
 private AgeTabSyntaxParser parser;
 private Map<BlockHeader, Map<String,AgeTabObject>> blockObjectMap = new HashMap<BlockHeader, Map<String,AgeTabObject>>();
 
 
 public AgeTabSubmissionImpl( AgeTabSyntaxParser p )
 {
  parser = p;
 }
 
 public AgeTabObject getObject(String part, BlockHeader classColumnHeader)
 {
  Map<String,AgeTabObject> objMap = blockObjectMap.get(classColumnHeader);
  
  if( objMap == null )
   return null;
  
  return objMap.get(part);
 }


 public AgeTabObject getOrCreateObject(String objId, BlockHeader hdr, int ln)
 {
  Map<String,AgeTabObject> objMap = blockObjectMap.get(hdr);
  
  AgeTabObject obj = null;
  
  if( objMap == null )
  {
   objMap = new HashMap<String,AgeTabObject>();
   blockObjectMap.put(hdr, objMap);
  }
  else
   obj = objMap.get(objId);
  
  if( obj == null )
  {
   obj = new AgeTabObject(ln);
   obj.setId(objId);
   
   objMap.put(objId, obj);
  }
  
  return obj;
 }


 public Collection<BlockHeader> getBlocks()
 {
  return blockObjectMap.keySet();
 }


 public Collection<AgeTabObject> getObjects(BlockHeader hdr)
 {
  Map<String, AgeTabObject> mp = blockObjectMap.get(hdr);
  
  if( mp == null )
   return null;
  
  return mp.values();
 }


 public AgeTabObject createObject(String objId, BlockHeader hdr, int ln)
 {
  Map<String,AgeTabObject> objMap = blockObjectMap.get(hdr);
  
  AgeTabObject obj = null;
  
  if( objMap == null )
  {
   objMap = new HashMap<String,AgeTabObject>();
   blockObjectMap.put(hdr, objMap);
  }

  obj = new AgeTabObject( ln );
  obj.setId(objId);
  
  objMap.put(objId, obj);
  
  return obj;
 }

 @Override
 public AgeTabSyntaxParser getParser()
 {
  return parser;
 }

}
