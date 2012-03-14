package uk.ac.ebi.age.storage.impl.serswap;

import java.util.Collection;

import uk.ac.ebi.age.ext.entity.Entity;
import uk.ac.ebi.age.ext.entity.EntityDomain;
import uk.ac.ebi.age.model.ContextSemanticModel;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.writable.AgeExternalObjectAttributeWritable;
import uk.ac.ebi.age.model.writable.AgeExternalRelationWritable;
import uk.ac.ebi.age.model.writable.AgeFileAttributeWritable;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;
import uk.ac.ebi.age.model.writable.AttributedWritable;
import uk.ac.ebi.age.model.writable.DataModuleWritable;

public class SwapDataModule implements DataModuleWritable
{
 private DataModuleWritable baseModule;
 private transient ModuleRef moduleRef;

 public SwapDataModule( DataModuleWritable bs )
 {
  baseModule = bs;
 }
 
 public String getEntityID()
 {
  return baseModule.getEntityID();
 }

 public EntityDomain getEntityDomain()
 {
  return baseModule.getEntityDomain();
 }

 public Entity getParentEntity()
 {
  return baseModule.getParentEntity();
 }

 public void setId(String id)
 {
  baseModule.setId(id);
 }

 public void setClusterId(String clstId)
 {
  baseModule.setClusterId(clstId);
 }

 public String getId()
 {
  return baseModule.getId();
 }

 public String getClusterId()
 {
  return baseModule.getClusterId();
 }

 public void addObject(AgeObjectWritable obj)
 {
  baseModule.addObject(obj);
 }

 public AgeObjectWritable getObject(String id)
 {
  return baseModule.getObject(id);
 }

 public ContextSemanticModel getContextSemanticModel()
 {
  return baseModule.getContextSemanticModel();
 }

 public Collection<AgeObjectWritable> getObjects()
 {
  return baseModule.getObjects();
 }

 public void setMasterModel(SemanticModel newModel)
 {
  baseModule.setMasterModel(newModel);
 }

 public Collection< ? extends AgeExternalRelationWritable> getExternalRelations()
 {
  return baseModule.getExternalRelations();
 }

 public Collection< ? extends AgeExternalObjectAttributeWritable> getExternalObjectAttributes()
 {
  return baseModule.getExternalObjectAttributes();
 }

 public Collection<AgeFileAttributeWritable> getFileAttributes()
 {
  return baseModule.getFileAttributes();
 }

 public Collection< ? extends AttributedWritable> getAttributed(AttributedSelector sel)
 {
  return baseModule.getAttributed(sel);
 }

 public void registerExternalRelation(AgeExternalRelationWritable rel)
 {
  baseModule.registerExternalRelation(rel);
 }

 public void pack()
 {
  baseModule.pack();
 }

 public ModuleRef getModuleRef()
 {
  return moduleRef;
 }

 public void setModuleRef(ModuleRef moduleRef)
 {
  this.moduleRef = moduleRef;
 }
}
