package uk.ac.ebi.age.parser.impl;

import uk.ac.ebi.age.model.IdScope;
import uk.ac.ebi.age.model.ResolveScope;
import uk.ac.ebi.age.parser.AgeDefaultSyntaxProfileDefinition;
import uk.ac.ebi.age.parser.SyntaxProfileDefinition;

//This in mutable SyntaxProfileDefinition initialized by AgeDefaultSyntaxProfileDefinition
public class SyntaxProfileDefinitionImpl implements SyntaxProfileDefinition
{
 private final String escapeSequence = AgeDefaultSyntaxProfileDefinition.escapeSequence;
 
 private String  customTokenBrackets    = AgeDefaultSyntaxProfileDefinition.customTokenBrackets;
 private String  flagsTokenBrackets     = AgeDefaultSyntaxProfileDefinition.flagsTokenBrackets;
 private String  qualifierTokenBrackets = AgeDefaultSyntaxProfileDefinition.qualifierTokenBrackets;
 private String  flagsSeparatorSign     = AgeDefaultSyntaxProfileDefinition.flagsSeparatorSign;
 private String  flagsEqualSign         = AgeDefaultSyntaxProfileDefinition.flagsEqualSign;
 private String  prototypeObjectId      = AgeDefaultSyntaxProfileDefinition.prototypeObjectId;
 private String  anonymousObjectId      = AgeDefaultSyntaxProfileDefinition.anonymousObjectId;

 private String  globalIdPrefix         = AgeDefaultSyntaxProfileDefinition.globalIdPrefix;
 private String  clusterIdPrefix        = AgeDefaultSyntaxProfileDefinition.clusterIdPrefix;
 private String  moduleIdPrefix         = AgeDefaultSyntaxProfileDefinition.moduleIdPrefix;
 private String  defaultScopeIdPrefix   = AgeDefaultSyntaxProfileDefinition.defaultScopeIdPrefix;

 private String       globalResolveScopePrefix         = AgeDefaultSyntaxProfileDefinition.globalResolveScopePrefix;
 private String       clusterResolveScopePrefix        = AgeDefaultSyntaxProfileDefinition.clusterResolveScopePrefix;
 private String       moduleResolveScopePrefix         = AgeDefaultSyntaxProfileDefinition.moduleResolveScopePrefix;
 private String       moduleCascadeResolveScopePrefix  = AgeDefaultSyntaxProfileDefinition.moduleCascadeResolveScopePrefix;
 private String       clusterCascadeResolveScopePrefix = AgeDefaultSyntaxProfileDefinition.clusterCascadeResolveScopePrefix;
 private String       defaultResolveScopePrefix        = AgeDefaultSyntaxProfileDefinition.defaultResolveScopePrefix;
 
 private String defaultEmbeddedObjectAttributeSeparator= AgeDefaultSyntaxProfileDefinition.defaultEmbeddedObjectAttributeSeparator;
 
 private String  horizontalBlockPrefix  = AgeDefaultSyntaxProfileDefinition.horizontalBlockPrefix;
 private String  verticalBlockPrefix    = AgeDefaultSyntaxProfileDefinition.verticalBlockPrefix;

 private IdScope defaultIdScope           = AgeDefaultSyntaxProfileDefinition.defaultIdScope;
 private Boolean horizontalBlockDefault = AgeDefaultSyntaxProfileDefinition.horizontalBlockDefault;
 private Boolean resetPrototype         = AgeDefaultSyntaxProfileDefinition.resetPrototype;
 
 private final ResolveScope defaultObjAttrReslvScope = AgeDefaultSyntaxProfileDefinition.defaultObjectAttributeResolveScope;
 private ResolveScope defaultFileAttrReslvScope = AgeDefaultSyntaxProfileDefinition.defaultFileAttributeResolveScope;
 private final ResolveScope defaultRelReslvScope     = AgeDefaultSyntaxProfileDefinition.defaultRelationResolveScope;

 @Override
 public String getCustomTokenBrackets()
 {
  return customTokenBrackets;
 }

 public void setCustomTokenBrackets(String customTokenBrackets)
 {
  this.customTokenBrackets = customTokenBrackets;
 }

 @Override
 public String getFlagsTokenBrackets()
 {
  return flagsTokenBrackets;
 }

 public void setFlagsTokenBrackets(String flagsTokenBrackets)
 {
  this.flagsTokenBrackets = flagsTokenBrackets;
 }

 @Override
 public String getQualifierTokenBrackets()
 {
  return qualifierTokenBrackets;
 }

 public void setQualifierTokenBrackets(String qualifierTokenBrackets)
 {
  this.qualifierTokenBrackets = qualifierTokenBrackets;
 }

 @Override
 public String getFlagsSeparatorSign()
 {
  return flagsSeparatorSign;
 }

 public void setFlagsSeparatorSign(String flagsSeparatorSign)
 {
  this.flagsSeparatorSign = flagsSeparatorSign;
 }

 @Override
 public String getFlagsEqualSign()
 {
  return flagsEqualSign;
 }

 public void setFlagsEqualSign(String flagsEqualSign)
 {
  this.flagsEqualSign = flagsEqualSign;
 }

 @Override
 public String getPrototypeObjectId()
 {
  return prototypeObjectId;
 }

 public void setPrototypeObjectId(String prototypeObjectId)
 {
  this.prototypeObjectId = prototypeObjectId;
 }

 @Override
 public String getAnonymousObjectId()
 {
  return anonymousObjectId;
 }

 public void setAnonymousObjectId(String anonymousObjectId)
 {
  this.anonymousObjectId = anonymousObjectId;
 }

 @Override
 public String getGlobalIdPrefix()
 {
  return globalIdPrefix;
 }

 public void setGlobalIdPrefix(String globalIdPrefix)
 {
  this.globalIdPrefix = globalIdPrefix;
 }

 @Override
 public String getClusterIdPrefix()
 {
  return clusterIdPrefix;
 }

 public void setClusterIdPrefix(String clusterIdPrefix)
 {
  this.clusterIdPrefix = clusterIdPrefix;
 }

 @Override
 public String getModuleIdPrefix()
 {
  return moduleIdPrefix;
 }

 public void setModuleIdPrefix(String moduleIdPrefix)
 {
  this.moduleIdPrefix = moduleIdPrefix;
 }

 @Override
 public String getHorizontalBlockPrefix()
 {
  return horizontalBlockPrefix;
 }

 public void setHorizontalBlockPrefix(String horizontalBlockPrefix)
 {
  this.horizontalBlockPrefix = horizontalBlockPrefix;
 }

 @Override
 public String getVerticalBlockPrefix()
 {
  return verticalBlockPrefix;
 }

 public void setVerticalBlockPrefix(String verticalBlockPrefix)
 {
  this.verticalBlockPrefix = verticalBlockPrefix;
 }

 @Override
 public IdScope getDefaultIdScope()
 {
  return defaultIdScope;
 }

 public void setDefaultIdScope(IdScope defaultScope)
 {
  this.defaultIdScope = defaultScope;
 }

 @Override
 public Boolean isHorizontalBlockDefault()
 {
  return horizontalBlockDefault;
 }

 public void setHorizontalBlockDefault(boolean defaultHorizontal)
 {
  this.horizontalBlockDefault = defaultHorizontal;
 }

 @Override
 public Boolean isResetPrototype()
 {
  return resetPrototype;
 }

 public void setResetPrototype(boolean resetPrototype)
 {
  this.resetPrototype = resetPrototype;
 }

 @Override
 public ResolveScope getDefaultObjectAttributeResolveScope()
 {
  return defaultObjAttrReslvScope;
 }

 @Override
 public ResolveScope getDefaultRelationResolveScope()
 {
  return defaultRelReslvScope;
 }

 @Override
 public String getEscapeSequence()
 {
  return escapeSequence;
 }

 @Override
 public String getDefaultScopeIdPrefix()
 {
  return defaultScopeIdPrefix;
 }

 public void setDefaultScopeIdPrefix(String defaultScopeIdPrefix)
 {
  this.defaultScopeIdPrefix = defaultScopeIdPrefix;
 }

 @Override
 public String getGlobalResolveScopePrefix()
 {
  return globalResolveScopePrefix;
 }

 public void setGlobalResolveScopePrefix(String globalResolveScopePrefix)
 {
  this.globalResolveScopePrefix = globalResolveScopePrefix;
 }

 @Override
 public String getClusterResolveScopePrefix()
 {
  return clusterResolveScopePrefix;
 }

 public void setClusterResolveScopePrefix(String clusterResolveScopePrefix)
 {
  this.clusterResolveScopePrefix = clusterResolveScopePrefix;
 }

 @Override
 public String getModuleResolveScopePrefix()
 {
  return moduleResolveScopePrefix;
 }

 public void setModuleResolveScopePrefix(String moduleResolveScopePrefix)
 {
  this.moduleResolveScopePrefix = moduleResolveScopePrefix;
 }

 @Override
 public String getModuleCascadeResolveScopePrefix()
 {
  return moduleCascadeResolveScopePrefix;
 }

 public void setModuleCascadeResolveScopePrefix(String moduleCascadeResolveScopePrefix)
 {
  this.moduleCascadeResolveScopePrefix = moduleCascadeResolveScopePrefix;
 }

 @Override
 public String getClusterCascadeResolveScopePrefix()
 {
  return clusterCascadeResolveScopePrefix;
 }

 public void setClusterCascadeResolveScopePrefix(String clusterCascadeResolveScopePrefix)
 {
  this.clusterCascadeResolveScopePrefix = clusterCascadeResolveScopePrefix;
 }

 @Override
 public String getDefaultResolveScopePrefix()
 {
  return defaultResolveScopePrefix;
 }

 public void setDefaultResolveScopePrefix(String defaultResolveScopePrefix)
 {
  this.defaultResolveScopePrefix = defaultResolveScopePrefix;
 }

 @Override
 public ResolveScope getDefaultFileAttributeResolveScope()
 {
  return defaultFileAttrReslvScope;
 }

 public void setDefaultFileAttributeResolveScope( ResolveScope scp )
 {
  defaultFileAttrReslvScope=scp;
 }

 @Override
 public String getDefaultEmbeddedObjectAttributeSeparator()
 {
  return defaultEmbeddedObjectAttributeSeparator;
 }

 public void setDefaultEmbeddedObjectAttributeSeparator(String defaultEmbeddedObjectAttributeSeparator)
 {
  this.defaultEmbeddedObjectAttributeSeparator = defaultEmbeddedObjectAttributeSeparator;
 }

}
