package uk.ac.ebi.age.parser.impl;

import uk.ac.ebi.age.model.IdScope;
import uk.ac.ebi.age.model.ResolveScope;
import uk.ac.ebi.age.parser.AgeDefaultSyntaxProfileDefinition;
import uk.ac.ebi.age.parser.SyntaxProfileDefinition;

public class SyntaxProfileDefinitionImpl implements SyntaxProfileDefinition
{
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

 private String  horizontalBlockPrefix  = AgeDefaultSyntaxProfileDefinition.horizontalBlockPrefix;
 private String  verticalBlockPrefix    = AgeDefaultSyntaxProfileDefinition.verticalBlockPrefix;

 private IdScope defaultIdScope           = AgeDefaultSyntaxProfileDefinition.defaultIdScope;
 private Boolean horizontalBlockDefault = AgeDefaultSyntaxProfileDefinition.horizontalBlockDefault;
 private Boolean resetPrototype         = AgeDefaultSyntaxProfileDefinition.resetPrototype;
 
 private ResolveScope defaultObjAttrReslvScope = AgeDefaultSyntaxProfileDefinition.defaultObjectAttributeResolveScope;
 private ResolveScope defaultRelReslvScope     = AgeDefaultSyntaxProfileDefinition.defaultRelationResolveScope;

 public String getCustomTokenBrackets()
 {
  return customTokenBrackets;
 }

 public void setCustomTokenBrackets(String customTokenBrackets)
 {
  this.customTokenBrackets = customTokenBrackets;
 }

 public String getFlagsTokenBrackets()
 {
  return flagsTokenBrackets;
 }

 public void setFlagsTokenBrackets(String flagsTokenBrackets)
 {
  this.flagsTokenBrackets = flagsTokenBrackets;
 }

 public String getQualifierTokenBrackets()
 {
  return qualifierTokenBrackets;
 }

 public void setQualifierTokenBrackets(String qualifierTokenBrackets)
 {
  this.qualifierTokenBrackets = qualifierTokenBrackets;
 }

 public String getFlagsSeparatorSign()
 {
  return flagsSeparatorSign;
 }

 public void setFlagsSeparatorSign(String flagsSeparatorSign)
 {
  this.flagsSeparatorSign = flagsSeparatorSign;
 }

 public String getFlagsEqualSign()
 {
  return flagsEqualSign;
 }

 public void setFlagsEqualSign(String flagsEqualSign)
 {
  this.flagsEqualSign = flagsEqualSign;
 }

 public String getPrototypeObjectId()
 {
  return prototypeObjectId;
 }

 public void setPrototypeObjectId(String prototypeObjectId)
 {
  this.prototypeObjectId = prototypeObjectId;
 }

 public String getAnonymousObjectId()
 {
  return anonymousObjectId;
 }

 public void setAnonymousObjectId(String anonymousObjectId)
 {
  this.anonymousObjectId = anonymousObjectId;
 }

 public String getGlobalIdPrefix()
 {
  return globalIdPrefix;
 }

 public void setGlobalIdPrefix(String globalIdPrefix)
 {
  this.globalIdPrefix = globalIdPrefix;
 }

 public String getClusterIdPrefix()
 {
  return clusterIdPrefix;
 }

 public void setClusterIdPrefix(String clusterIdPrefix)
 {
  this.clusterIdPrefix = clusterIdPrefix;
 }

 public String getModuleIdPrefix()
 {
  return moduleIdPrefix;
 }

 public void setModuleIdPrefix(String moduleIdPrefix)
 {
  this.moduleIdPrefix = moduleIdPrefix;
 }

 public String getHorizontalBlockPrefix()
 {
  return horizontalBlockPrefix;
 }

 public void setHorizontalBlockPrefix(String horizontalBlockPrefix)
 {
  this.horizontalBlockPrefix = horizontalBlockPrefix;
 }

 public String getVerticalBlockPrefix()
 {
  return verticalBlockPrefix;
 }

 public void setVerticalBlockPrefix(String verticalBlockPrefix)
 {
  this.verticalBlockPrefix = verticalBlockPrefix;
 }

 public IdScope getDefaultIdScope()
 {
  return defaultIdScope;
 }

 public void setDefaultIdScope(IdScope defaultScope)
 {
  this.defaultIdScope = defaultScope;
 }

 public Boolean isHorizontalBlockDefault()
 {
  return horizontalBlockDefault;
 }

 public void setHorizontalBlockDefault(boolean defaultHorizontal)
 {
  this.horizontalBlockDefault = defaultHorizontal;
 }

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

}
