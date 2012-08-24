package uk.ac.ebi.age.parser.impl;

import uk.ac.ebi.age.model.IdScope;
import uk.ac.ebi.age.model.ResolveScope;
import uk.ac.ebi.age.parser.SyntaxProfileDefinition;

public class ClassSpecificSyntaxProfileDefinitionImpl implements SyntaxProfileDefinition
{
 private final SyntaxProfileDefinition commonProfileDefinition;
 
 private String  customTokenBrackets;
 private String  flagsTokenBrackets;
 private String  qualifierTokenBrackets;
 private String  flagsSeparatorSign;
 private String  flagsEqualSign;
 private String  prototypeObjectId;
 private String  anonymousObjectId;

 private String  globalIdPrefix;
 private String  clusterIdPrefix;
 private String  moduleIdPrefix;
 private String  defaultScopeIdPrefix;

 private String  globalResolveScopePrefix;
 private String  clusterResolveScopePrefix;
 private String  moduleResolveScopePrefix; 
 private String  moduleCascadeResolveScopePrefix;
 private String  clusterCascadeResolveScopePrefix;
 private String  defaultResolveScopePrefix;

 private String  horizontalBlockPrefix;
 private String  verticalBlockPrefix;

 private IdScope defaultIdScope;
 private Boolean horizontalBlockDefault;
 private Boolean resetPrototype;
 private Boolean allowImplicitCustomClasses;
 
 private ResolveScope defaultObjAttrReslvScope;
 private ResolveScope defaultFileAttrReslvScope;
 private ResolveScope defaultRelReslvScope;

 private String defaultEmbeddedObjectAttributeSeparator;

 public ClassSpecificSyntaxProfileDefinitionImpl( SyntaxProfileDefinition comm )
 {
  commonProfileDefinition = comm;
 }
 
 @Override
 public String getCustomTokenBrackets()
 {
  if( customTokenBrackets != null )
   return customTokenBrackets;
  
  return commonProfileDefinition.getCustomTokenBrackets();
 }

 public void setCustomTokenBrackets(String customTokenBrackets)
 {
  this.customTokenBrackets = customTokenBrackets;
 }

 @Override
 public String getFlagsTokenBrackets()
 {
  if( flagsTokenBrackets != null )
  return flagsTokenBrackets;
  
  return commonProfileDefinition.getFlagsTokenBrackets();
 }

 public void setFlagsTokenBrackets(String flagsTokenBrackets)
 {
  this.flagsTokenBrackets = flagsTokenBrackets;
 }

 @Override
 public String getQualifierTokenBrackets()
 {
  if( qualifierTokenBrackets != null )
  return qualifierTokenBrackets;
  
  return commonProfileDefinition.getQualifierTokenBrackets();
 }

 public void setQualifierTokenBrackets(String qualifierTokenBrackets)
 {
  this.qualifierTokenBrackets = qualifierTokenBrackets;
 }

 @Override
 public String getFlagsSeparatorSign()
 {
  if( flagsSeparatorSign != null )
  return flagsSeparatorSign;
  
  return commonProfileDefinition.getFlagsSeparatorSign();
 }

 public void setFlagsSeparatorSign(String flagsSeparatorSign)
 {
  this.flagsSeparatorSign = flagsSeparatorSign;
 }

 @Override
 public String getFlagsEqualSign()
 {
  if( flagsEqualSign != null )
  return flagsEqualSign;
  
  return commonProfileDefinition.getFlagsEqualSign();
 }

 public void setFlagsEqualSign(String flagsEqualSign)
 {
  this.flagsEqualSign = flagsEqualSign;
 }

 @Override
 public String getPrototypeObjectId()
 {
  if( prototypeObjectId != null )
  return prototypeObjectId;
  
  return commonProfileDefinition.getPrototypeObjectId();
 }

 public void setPrototypeObjectId(String prototypeObjectId)
 {
  this.prototypeObjectId = prototypeObjectId;
 }

 @Override
 public String getAnonymousObjectId()
 {
  if( anonymousObjectId != null )
  return anonymousObjectId;
  
  return commonProfileDefinition.getAnonymousObjectId();
 }

 public void setAnonymousObjectId(String anonymousObjectId)
 {
  this.anonymousObjectId = anonymousObjectId;
 }

 @Override
 public String getGlobalIdPrefix()
 {
  if( globalIdPrefix != null )
  return globalIdPrefix;
  
  return commonProfileDefinition.getGlobalIdPrefix();
 }

 public void setGlobalIdPrefix(String globalIdPrefix)
 {
  this.globalIdPrefix = globalIdPrefix;
 }

 @Override
 public String getClusterIdPrefix()
 {
  if( clusterIdPrefix != null )
  return clusterIdPrefix;
  
  return commonProfileDefinition.getClusterIdPrefix();
 }

 public void setClusterIdPrefix(String clusterIdPrefix)
 {
  this.clusterIdPrefix = clusterIdPrefix;
 }

 @Override
 public String getModuleIdPrefix()
 {
  if( moduleIdPrefix != null )
  return moduleIdPrefix;
  
  return commonProfileDefinition.getModuleIdPrefix();
 }

 public void setModuleIdPrefix(String moduleIdPrefix)
 {
  this.moduleIdPrefix = moduleIdPrefix;
 }

 @Override
 public String getHorizontalBlockPrefix()
 {
  if( horizontalBlockPrefix != null )
  return horizontalBlockPrefix;
  
  return commonProfileDefinition.getHorizontalBlockPrefix();
 }

 public void setHorizontalBlockPrefix(String horizontalBlockPrefix)
 {
  this.horizontalBlockPrefix = horizontalBlockPrefix;
 }

 @Override
 public String getVerticalBlockPrefix()
 {
  if( verticalBlockPrefix != null )
  return verticalBlockPrefix;
  
  return commonProfileDefinition.getVerticalBlockPrefix();
 }

 public void setVerticalBlockPrefix(String verticalBlockPrefix)
 {
  this.verticalBlockPrefix = verticalBlockPrefix;
 }

 @Override
 public IdScope getDefaultIdScope()
 {
  if( defaultIdScope != null )
  return defaultIdScope;
  
  return commonProfileDefinition.getDefaultIdScope();
 }

 public void setDefaultIdScope(IdScope defaultScope)
 {
  this.defaultIdScope = defaultScope;
 }

 public boolean allowImplicitCustomClasses()
 {
  if( allowImplicitCustomClasses != null )
   return allowImplicitCustomClasses;
  
  return commonProfileDefinition.allowImplicitCustomClasses();
 }

 public void setAllowImplicitCustomClasses(boolean allowImplicitCustomClasses)
 {
  this.allowImplicitCustomClasses = allowImplicitCustomClasses;
 }
 
 @Override
 public boolean isHorizontalBlockDefault()
 {
  if( horizontalBlockDefault != null )
  return horizontalBlockDefault;
  
  return commonProfileDefinition.isHorizontalBlockDefault();
 }

 public void setHorizontalBlockDefault(boolean defaultHorizontal)
 {
  this.horizontalBlockDefault = defaultHorizontal;
 }

 @Override
 public boolean isResetPrototype()
 {
  if( resetPrototype != null )
  return resetPrototype;
  
  return commonProfileDefinition.isResetPrototype();
 }

 public void setResetPrototype(boolean resetPrototype)
 {
  this.resetPrototype = resetPrototype;
 }

 @Override
 public ResolveScope getDefaultObjectAttributeResolveScope()
 {  
  if( defaultObjAttrReslvScope != null )
   return defaultObjAttrReslvScope;
  
  return commonProfileDefinition.getDefaultObjectAttributeResolveScope();
 }
 
 public void setDefaultObjectAttributeResolveScope( ResolveScope sc )
 {
  defaultObjAttrReslvScope = sc;
 }
 
 @Override
 public ResolveScope getDefaultFileAttributeResolveScope()
 {  
  if( defaultFileAttrReslvScope != null )
   return defaultFileAttrReslvScope;
  
  return commonProfileDefinition.getDefaultFileAttributeResolveScope();
 }
 
 public void setDefaultFileAttributeResolveScope( ResolveScope sc )
 {
  defaultFileAttrReslvScope = sc;
 }


 @Override
 public ResolveScope getDefaultRelationResolveScope()
 {
  if( defaultRelReslvScope != null )
   return defaultRelReslvScope;
  
  return commonProfileDefinition.getDefaultRelationResolveScope();
 }

 @Override
 public String getEscapeSequence()
 {
  return commonProfileDefinition.getEscapeSequence();
 }

 @Override
 public String getDefaultScopeIdPrefix()
 {
  if( defaultScopeIdPrefix != null )
   return defaultScopeIdPrefix;
  
  return commonProfileDefinition.getDefaultScopeIdPrefix();
 }

 public void setDefaultScopeIdPrefix(String defaultScopeIdPrefix)
 {
  this.defaultScopeIdPrefix = defaultScopeIdPrefix;
 }

 @Override
 public String getGlobalResolveScopePrefix()
 {
  if( globalResolveScopePrefix != null )
   return globalResolveScopePrefix;
  
  return commonProfileDefinition.getGlobalResolveScopePrefix();
 }

 public void setGlobalResolveScopePrefix(String globalResolveScopePrefix)
 {
  this.globalResolveScopePrefix = globalResolveScopePrefix;
 }

 @Override
 public String getClusterResolveScopePrefix()
 {
  if( clusterResolveScopePrefix != null )
   return clusterResolveScopePrefix;
  
  return commonProfileDefinition.getClusterResolveScopePrefix();
 }

 public void setClusterResolveScopePrefix(String clusterResolveScopePrefix)
 {
  this.clusterResolveScopePrefix = clusterResolveScopePrefix;
 }

 @Override
 public String getModuleResolveScopePrefix()
 {
  if( moduleResolveScopePrefix != null )
   return moduleResolveScopePrefix;
  
  return commonProfileDefinition.getModuleResolveScopePrefix();
 }

 public void setModuleResolveScopePrefix(String moduleResolveScopePrefix)
 {
  this.moduleResolveScopePrefix = moduleResolveScopePrefix;
 }

 @Override
 public String getModuleCascadeResolveScopePrefix()
 {
  if( moduleCascadeResolveScopePrefix != null )
   return moduleCascadeResolveScopePrefix;
  
  return commonProfileDefinition.getModuleCascadeResolveScopePrefix();
 }

 public void setModuleCascadeResolveScopePrefix(String moduleCascadeResolveScopePrefix)
 {
  this.moduleCascadeResolveScopePrefix = moduleCascadeResolveScopePrefix;
 }

 @Override
 public String getClusterCascadeResolveScopePrefix()
 {
  if( clusterCascadeResolveScopePrefix != null )
   return clusterCascadeResolveScopePrefix;
  
  return commonProfileDefinition.getClusterCascadeResolveScopePrefix();
 }

 public void setClusterCascadeResolveScopePrefix(String clusterCascadeResolveScopePrefix)
 {
  this.clusterCascadeResolveScopePrefix = clusterCascadeResolveScopePrefix;
 }

 @Override
 public String getDefaultResolveScopePrefix()
 {
  if( defaultResolveScopePrefix != null )
   return defaultResolveScopePrefix;
  
  return commonProfileDefinition.getDefaultResolveScopePrefix();
 }

 public void setDefaultResolveScopePrefix(String defaultResolveScopePrefix)
 {
  this.defaultResolveScopePrefix = defaultResolveScopePrefix;
 }

 @Override
 public String getDefaultEmbeddedObjectAttributeSeparator()
 {
  if( defaultEmbeddedObjectAttributeSeparator != null )
   return defaultEmbeddedObjectAttributeSeparator;
  
  return commonProfileDefinition.getDefaultEmbeddedObjectAttributeSeparator();
 }

}