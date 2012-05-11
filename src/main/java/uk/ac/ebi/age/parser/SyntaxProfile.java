package uk.ac.ebi.age.parser;

import java.util.HashMap;
import java.util.Map;

import uk.ac.ebi.age.model.IdScope;


public class SyntaxProfile
{
 private SyntaxProfileDefinition commonSyntaxProfileDefinition = AgeDefaultSyntaxProfileDefinition.getInstance();
 private Map<String,SyntaxProfileDefinition> classDefMap = new HashMap<String, SyntaxProfileDefinition>();
 
 public SyntaxProfileDefinition getCommonSyntaxProfile()
 {
  return commonSyntaxProfileDefinition;
 }
 
 public void setCommonSyntaxProfile( SyntaxProfileDefinition def )
 {
  commonSyntaxProfileDefinition = def;
 }

 public SyntaxProfileDefinition getClassSpecificSyntaxProfile( String className )
 {
  SyntaxProfileDefinition clDef = classDefMap.get(className);
  
  if( clDef != null )
   return clDef;
  
  return commonSyntaxProfileDefinition;
 }
 
 public void addClassSpecificSyntaxProfile( String className, SyntaxProfileDefinition profile )
 {
  classDefMap.put(className, profile);
 }

 public String getCustomTokenBrackets( String className )
 {
  SyntaxProfileDefinition clDef = classDefMap.get(className);
  
  if( clDef == null )
   return commonSyntaxProfileDefinition.getCustomTokenBrackets();
  
  String def = clDef.getCustomTokenBrackets();

  if( def == null )
   return commonSyntaxProfileDefinition.getCustomTokenBrackets();
   
  return def;
 }

 public String getFlagsTokenBrackets( String className )
 {
  SyntaxProfileDefinition clDef = classDefMap.get(className);
  
  if( clDef == null )
   return commonSyntaxProfileDefinition.getFlagsTokenBrackets();
  
  String def = clDef.getFlagsTokenBrackets();

  if( def == null )
   return commonSyntaxProfileDefinition.getFlagsTokenBrackets();
   
  return def;
 }

 public String getQualifierTokenBrackets( String className )
 {
  SyntaxProfileDefinition clDef = classDefMap.get(className);
  
  if( clDef == null )
   return commonSyntaxProfileDefinition.getQualifierTokenBrackets();
  
  String def = clDef.getQualifierTokenBrackets();

  if( def == null )
   return commonSyntaxProfileDefinition.getQualifierTokenBrackets();
   
  return def;
 }

 public String getFlagsSeparatorSign( String className )
 {
  SyntaxProfileDefinition clDef = classDefMap.get(className);
  
  if( clDef == null )
   return commonSyntaxProfileDefinition.getFlagsSeparatorSign();
  
  String def = clDef.getFlagsSeparatorSign();

  if( def == null )
   return commonSyntaxProfileDefinition.getFlagsSeparatorSign();
   
  return def;
 }

 public String getFlagsEqualSign( String className )
 {
  SyntaxProfileDefinition clDef = classDefMap.get(className);
  
  if( clDef == null )
   return commonSyntaxProfileDefinition.getFlagsEqualSign();
  
  String def = clDef.getFlagsEqualSign();

  if( def == null )
   return commonSyntaxProfileDefinition.getFlagsEqualSign();
   
  return def;
 }

 public String getPrototypeObjectId( String className )
 {
  SyntaxProfileDefinition clDef = classDefMap.get(className);
  
  if( clDef == null )
   return commonSyntaxProfileDefinition.getPrototypeObjectId();
  
  String def = clDef.getPrototypeObjectId();

  if( def == null )
   return commonSyntaxProfileDefinition.getPrototypeObjectId();
   
  return def;
 }

 public String getAnonymousObjectId( String className )
 {
  SyntaxProfileDefinition clDef = classDefMap.get(className);
  
  if( clDef == null )
   return commonSyntaxProfileDefinition.getAnonymousObjectId();
  
  String def = clDef.getAnonymousObjectId();

  if( def == null )
   return commonSyntaxProfileDefinition.getAnonymousObjectId();
   
  return def;
 }

 public String getGlobalIdPrefix( String className )
 {
  SyntaxProfileDefinition clDef = classDefMap.get(className);
  
  if( clDef == null )
   return commonSyntaxProfileDefinition.getGlobalIdPrefix();
  
  String def = clDef.getGlobalIdPrefix();

  if( def == null )
   return commonSyntaxProfileDefinition.getGlobalIdPrefix();
   
  return def;
 }

 public String getClusterIdPrefix( String className )
 {
  SyntaxProfileDefinition clDef = classDefMap.get(className);
  
  if( clDef == null )
   return commonSyntaxProfileDefinition.getClusterIdPrefix();
  
  String def = clDef.getClusterIdPrefix();

  if( def == null )
   return commonSyntaxProfileDefinition.getClusterIdPrefix();
   
  return def;
 }

 public String getModuleIdPrefix( String className )
 {
  SyntaxProfileDefinition clDef = classDefMap.get(className);
  
  if( clDef == null )
   return commonSyntaxProfileDefinition.getModuleIdPrefix();
  
  String def = clDef.getModuleIdPrefix();

  if( def == null )
   return commonSyntaxProfileDefinition.getModuleIdPrefix();
   
  return def;
 }

 public IdScope getDefaultIdScope( String className )
 {
  SyntaxProfileDefinition clDef = classDefMap.get(className);
  
  if( clDef == null )
   return commonSyntaxProfileDefinition.getDefaultIdScope();
  
  IdScope def = clDef.getDefaultIdScope();

  if( def == null )
   return commonSyntaxProfileDefinition.getDefaultIdScope();
   
  return def;
 }

 public boolean isHorizontalBlockDefault( String className )
 {
  SyntaxProfileDefinition clDef = classDefMap.get(className);
  
  if( clDef == null )
   return commonSyntaxProfileDefinition.isHorizontalBlockDefault();
  
  Boolean def = clDef.isHorizontalBlockDefault();

  if( def == null )
   return commonSyntaxProfileDefinition.isHorizontalBlockDefault();
   
  return def;
 }

 public boolean isResetPrototype( String className )
 {
  SyntaxProfileDefinition clDef = classDefMap.get(className);
  
  if( clDef == null )
   return commonSyntaxProfileDefinition.isResetPrototype();
  
  Boolean def = clDef.isResetPrototype();

  if( def == null )
   return commonSyntaxProfileDefinition.isResetPrototype();
   
  return def;
 }

 public String getEscapeSequence()
 {
  return commonSyntaxProfileDefinition.getEscapeSequence();
 }
}
