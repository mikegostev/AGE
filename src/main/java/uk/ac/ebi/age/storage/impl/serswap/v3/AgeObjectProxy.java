package uk.ac.ebi.age.storage.impl.serswap.v3;

import java.lang.ref.SoftReference;
import java.util.Collection;

import uk.ac.ebi.age.ext.entity.Entity;
import uk.ac.ebi.age.ext.entity.EntityDomain;
import uk.ac.ebi.age.model.AgeAttribute;
import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.AttributeClassRef;
import uk.ac.ebi.age.model.AttributedClass;
import uk.ac.ebi.age.model.ContextSemanticModel;
import uk.ac.ebi.age.model.IdScope;
import uk.ac.ebi.age.model.RelationClassRef;
import uk.ac.ebi.age.model.writable.AgeAttributeWritable;
import uk.ac.ebi.age.model.writable.AgeExternalRelationWritable;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;
import uk.ac.ebi.age.model.writable.AgeRelationWritable;
import uk.ac.ebi.age.model.writable.DataModuleWritable;
import uk.ac.ebi.age.storage.ModuleKey;
import uk.ac.ebi.age.storage.impl.serswap.SerializedSwapStorage;

public class AgeObjectProxy implements AgeObjectWritable
{
 private transient SoftReference<AgeObjectWritable> objectRef;

 private StoragePlug storage;
 
 private ModuleKey moduleId;
 private String objectId;
 
 public AgeObjectProxy(String objId, ModuleKey mk, StoragePlug sss)
 {
  storage = sss;
  moduleId=mk;
  objectId = objId;
 }

 
 public AgeObjectProxy(AgeObjectWritable obj, ModuleKey mk, StoragePlug sss)
 {
  storage = sss;
  moduleId=mk;
  objectId = obj.getId();
 }

 private AgeObjectWritable getObject()
 {
  AgeObjectWritable obj = null;
  
  if( objectRef != null )
   obj = objectRef.get();
  
  if( obj != null )
   return obj;
  
  obj = storage.getStorage().getLowLevelObject(moduleId,objectId);
  
  objectRef = new SoftReference<AgeObjectWritable>(obj);
  
  return obj;
 }
 
 public AgeObjectWritable tryGetObject()
 {
  if( objectRef == null )
   return null;

  return objectRef.get();
 }
 
 @Override
 public String getId()
 {
  return objectId;
 }

 @Override
 public IdScope getIdScope()
 {
  return getObject().getIdScope();
 }

 @Override
 public AgeClass getAgeElClass()
 {
  return getObject().getAgeElClass();
 }

 @Override
 public Collection< ? extends AgeRelationClass> getRelationClasses()
 {
  return getObject().getRelationClasses();
 }

 @Override
 public Object getAttributeValue(AgeAttributeClass cls)
 {
  return getObject().getAttributeValue(cls);
 }

 @Override
 public int getOrder()
 {
  return getObject().getOrder();
 }

 @Override
 public AttributedClass getAttributedClass()
 {
  return getObject().getAttributedClass();
 }

 @Override
 public Collection< ? extends AgeAttributeWritable> getAttributes()
 {
  return getObject().getAttributes();
 }

 @Override
 public AgeAttribute getAttribute(AgeAttributeClass cls)
 {
  return getObject().getAttribute( cls );
 }

 @Override
 public Collection< ? extends AgeAttributeWritable> getAttributesByClass(AgeAttributeClass cls, boolean wSubCls)
 {
  return getObject().getAttributesByClass( cls, wSubCls );
 }

 @Override
 public Collection< ? extends AgeAttributeClass> getAttributeClasses()
 {
  return getObject().getAttributeClasses();
 }

 @Override
 public ContextSemanticModel getSemanticModel()
 {
  return getObject().getSemanticModel();
 }

 @Override
 public String getEntityID()
 {
  return getObject().getEntityID();
 }

 @Override
 public EntityDomain getEntityDomain()
 {
  return getObject().getEntityDomain();
 }

 @Override
 public Entity getParentEntity()
 {
  return getObject().getParentEntity();
 }

 @Override
 public AgeAttributeWritable createAgeAttribute(AttributeClassRef attrClass)
 {
  return getObject().createAgeAttribute(attrClass);
 }

 @Override
 public AgeAttributeWritable createExternalObjectAttribute(AttributeClassRef attrClass, String val, boolean glb )
 {
  return getObject().createExternalObjectAttribute(attrClass,val, glb);
 }

 @Override
 public void reset()
 {
  getObject().reset();
 }

 @Override
 public void sortAttributes()
 {
  getObject().sortAttributes();
 }

 @Override
 public DataModuleWritable getDataModule()
 {
  return getObject().getDataModule();
 }

 @Override
 public Collection< ? extends AgeRelationWritable> getRelations()
 {
  return getObject().getRelations();
 }

 @Override
 public Collection< ? extends AgeRelationWritable> getRelationsByClass(AgeRelationClass cls, boolean wSbCl)
 {
  return getObject().getRelationsByClass(cls,wSbCl);
 }

 @Override
 public void addAttribute(AgeAttributeWritable attr)
 {
  getObject().addAttribute(attr);
 }

 @Override
 public void removeAttribute(AgeAttributeWritable attr)
 {
  getObject().removeAttribute(attr);
 }

 @Override
 public void addRelation(AgeRelationWritable r)
 {
  getObject().addRelation(r);
 }

 @Override
 public void removeRelation(AgeRelationWritable rel)
 {
  getObject().removeRelation(rel);
 }

 @Override
 public AgeExternalRelationWritable createExternalRelation(RelationClassRef ref, String val, boolean glb )
 {
  return getObject().createExternalRelation(ref, val, glb);
 }

 @Override
 public AgeRelationWritable createRelation(RelationClassRef ref, AgeObjectWritable targetObj)
 {
  return getObject().createRelation(ref, targetObj);
 }

 @Override
 public void setOrder(int row)
 {
  getObject().setOrder(row);
 }

 @Override
 public void setDataModule(DataModuleWritable s)
 {
  getObject().setDataModule(s);
 }

 @Override
 public void setId(String id)
 {
  getObject().setId(id);
 }

 @Override
 public void setIdScope(IdScope scp)
 {
  getObject().setIdScope(scp);
 }

 @Override
 public void pack()
 {
  getObject().pack();
 }

 public SerializedSwapStorage getStorage()
 {
  return storage.getStorage();
 }
 
 public ModuleKey getModuleKey()
 {
  return moduleId;
 }
 
}
