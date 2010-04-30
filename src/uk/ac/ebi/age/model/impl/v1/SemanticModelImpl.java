package uk.ac.ebi.age.model.impl.v1;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.AgeClassProperty;
import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.DataType;
import uk.ac.ebi.age.model.ModelException;
import uk.ac.ebi.age.model.ModelFactory;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.impl.SemanticModelOwl;
import uk.ac.ebi.age.model.writable.AgeAttributeClassWritable;
import uk.ac.ebi.age.model.writable.AgeAttributeWritable;
import uk.ac.ebi.age.model.writable.AgeClassWritable;
import uk.ac.ebi.age.model.writable.AgeExternalRelationWritable;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;
import uk.ac.ebi.age.model.writable.AgeRelationClassWritable;
import uk.ac.ebi.age.model.writable.AgeRelationWritable;

public class SemanticModelImpl extends SemanticModelOwl implements SemanticModel, Serializable
{
 private static final long serialVersionUID = 1L;
 
 private Map<String,AgeClass> classMap = new TreeMap<String,AgeClass>();
 private Map<String,AgeAttributeClass> attributeMap = new TreeMap<String, AgeAttributeClass>();
 private Map<String,AgeRelationClass> relationMap = new TreeMap<String, AgeRelationClass>();

 private AgeRelationClass attributeAttachmentRelation;
 
 private ModelFactory modelFactory;

 private AgeAttributeClass attrClassRoot;
 private AgeClass classRoot;
 
 public SemanticModelImpl(ModelFactory modelFactory)
 {
  this.modelFactory=modelFactory;
 }
 
 public SemanticModelImpl( String sourceURI, ModelFactory modelFactory ) throws ModelException
 {
  this.modelFactory=modelFactory;
  
  parseOWL(sourceURI);
 }
 
 

 public ModelFactory getModelFactory()
 {
  return modelFactory;
 }

 
 
 public AgeClass getDefinedAgeClass(String name)
 {
  return classMap.get(name);
 }

 
 public AgeClassProperty getDefinedAgeClassProperty(String name)
 {
  AgeClassProperty prop = attributeMap.get(name);
  
  if( prop != null )
   return prop;
  
  return relationMap.get(name);
 }


 public AgeObjectWritable createAgeObject(String id, AgeClass cls)
 {
  return getModelFactory().createAgeObject(id, cls, this);
 }

 public AgeRelationClassWritable createAgeRelationClass(String name, String id)
 {
  return getModelFactory().createAgeRelationClass(name, id, this);
 }


 public AgeAttributeWritable createAgeAttribute(AgeObject obj, AgeAttributeClass attrClass)
 {
  return modelFactory.createAgeAttribute(obj, attrClass, this);
 }

 public AgeAttributeWritable createAgeAttribute(AgeObject obj, AgeAttributeClass attrClass, String prm)
 {
  return modelFactory.createAgeAttribute(obj, attrClass, prm, this);
 }

 
 public AgeAttributeClassWritable createAgeAttributeClass(String name, String id, DataType type)
 {
  return modelFactory.createAgeAttributeClass(name, id, type, this);
 }

 public AgeClassWritable createAgeClass(String name, String id, String pfx)
 {
  return modelFactory.createAgeClass(name, id, pfx, this);
 }

 public AgeExternalRelationWritable createExternalRelation(AgeObjectWritable sourceObj, String id, AgeRelationClass targetClass)
 {
  return modelFactory.createExternalRelation(sourceObj, id, targetClass,  this);
 }

 public AgeRelationWritable createAgeRelation(AgeObjectWritable targetObj, AgeRelationClass relClass)
 {
  return modelFactory.createRelation(targetObj, relClass, this);
 }


 public AgeRelationClass getAttributeAttachmentClass()
 {
  return attributeAttachmentRelation;
 }

 public AgeClass getAgeClass(String name)
 {
  return getDefinedAgeClass(name);
 }

 public AgeRelationClass getAgeRelationClass(String relClassName)
 {
  return getDefinedAgeRelationClass(relClassName);
 }

 public AgeRelationClass getDefinedAgeRelationClass(String name)
 {
  return relationMap.get(name);
 }



 public AgeAttributeClass getAgeAttributeClass(String attClsName)
 {
  return getDefinedAgeAttributeClass(attClsName);
 }



 public AgeAttributeClass getDefinedAgeAttributeClass(String attClsName)
 {
  return attributeMap.get(attClsName);
 }

 @Override
 protected void addAttributeClass(AgeAttributeClassWritable cls)
 {
  attributeMap.put(cls.getName(), cls);
 }

 @Override
 protected void addClass(AgeClassWritable cls)
 {
  classMap.put(cls.getName(), cls);
 }

 @Override
 protected void addRelationClass(AgeRelationClassWritable cls)
 {
  relationMap.put(cls.getName(), cls);
 }

 @Override
 protected void setAttributeClassRoot(AgeAttributeClassWritable root)
 {
  attrClassRoot = root;
 }

 @Override
 protected void setClassRoot(AgeClassWritable root)
 {
  classRoot = root;
 }

}
