package uk.ac.ebi.age.model.impl.v1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import uk.ac.ebi.age.model.AgeAnnotation;
import uk.ac.ebi.age.model.AgeAnnotationClass;
import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AgeAttributeClassPlug;
import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.AgeClassPlug;
import uk.ac.ebi.age.model.AgeClassProperty;
import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.AgeRelationClassPlug;
import uk.ac.ebi.age.model.DataType;
import uk.ac.ebi.age.model.ModelException;
import uk.ac.ebi.age.model.ModelFactory;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.impl.SemanticModelOwl;
import uk.ac.ebi.age.model.writable.AgeAnnotationClassWritable;
import uk.ac.ebi.age.model.writable.AgeAnnotationWritable;
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

 private Map<String,AgeClass> classIdMap = new TreeMap<String,AgeClass>();
 private Map<String,AgeAttributeClass> attributeIdMap = new TreeMap<String, AgeAttributeClass>();
 private Map<String,AgeRelationClass> relationIdMap = new TreeMap<String, AgeRelationClass>();

 private AgeRelationClass attributeAttachmentRelation;

 private Collection<AgeAnnotation> annotation = new ArrayList<AgeAnnotation>();
 
 private ModelFactory modelFactory;

 private AgeAttributeClass attrClassRoot;
 private AgeClass classRoot;
 private AgeRelationClass relationRoot;
 private AgeAnnotationClass annotationRoot;
 
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

 public AgeAnnotationWritable createAgeAnnotation(AgeAnnotationClass cls)
 {
  return getModelFactory().createAgeAnnotation(cls, this);
 }


 public AgeAttributeWritable createAgeAttribute( AgeAttributeClass attrClass)
 {
  return modelFactory.createAgeAttribute(attrClass, this);
 }
 
 public AgeAttributeClassWritable createAgeAttributeClass(String name, String id, DataType type)
 {
  return modelFactory.createAgeAttributeClass(name, id, type, this);
 }

 public AgeAnnotationClassWritable createAgeAnnotationClass(String name, String id)
 {
  return modelFactory.createAgeAnnotationClass(name, id, this);
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
  attributeIdMap.put(cls.getId(), cls);
 }

 @Override
 protected void addClass(AgeClassWritable cls)
 {
  classMap.put(cls.getName(), cls);
  classIdMap.put(cls.getId(), cls);
 }

 @Override
 protected void addRelationClass(AgeRelationClassWritable cls)
 {
  relationMap.put(cls.getName(), cls);
  relationIdMap.put(cls.getId(), cls);
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


 @Override
 public AgeClassPlug getAgeClassPlug(AgeClass attrClass)
 {
  return modelFactory.createAgeClassPlug(attrClass, this);
 }

 @Override
 public AgeClass getDefinedAgeClassById(String classId)
 {
  return classIdMap.get(classId);
 }

 @Override
 public AgeRelationClassPlug getAgeRelationClassPlug(AgeRelationClass attrClass)
 {
  return modelFactory.createAgeRelationClassPlug(attrClass, this);
 }

 @Override
 public AgeRelationClass getDefinedAgeRelationClassById(String classId)
 {
  return relationIdMap.get(classId);
 }

 @Override
 public AgeAttributeClassPlug getAgeAttributeClassPlug(AgeAttributeClass attrClass)
 {
  return modelFactory.createAgeAttributeClassPlug(attrClass, this);
 }

 @Override
 public AgeAttributeClass getDefinedAgeAttributeClassById(String classId)
 {
  return attributeIdMap.get(classId);
 }

 @Override
 public Collection<AgeClass> getAgeClasses()
 {
  return classMap.values();
 }

 @Override
 public AgeClass getRootAgeClass()
 {
  return classRoot;
 }

 @Override
 public AgeAttributeClass getRootAgeAttributeClass()
 {
  return attrClassRoot;
 }

 @Override
 protected void setRelationClassRoot(AgeRelationClassWritable rr)
 {
  relationRoot=rr;
 }
 
 @Override
 public AgeRelationClass getRootAgeRelationClass()
 {
  return relationRoot;
 }

 @Override
 public AgeAnnotationClass getRootAgeAnnotationClass()
 {
  return annotationRoot;
 }

 @Override
 public Collection<AgeAnnotation> getAnnotations()
 {
  return annotation;
 }

 @Override
 public void addAnnotation(AgeAnnotation ant)
 {
  annotation.add(ant);
 }


}
