package uk.ac.ebi.age.storage.impl.serswap;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

import uk.ac.ebi.age.model.ModuleKey;
import uk.ac.ebi.age.storage.impl.serswap.v3.AgeObjectProxy;

class StorageIndex implements Serializable
{
 private static final long serialVersionUID = 1L;

 private Map<String, AgeObjectProxy>              globalIndexMap;
 private Map<String, Map<String, AgeObjectProxy>> clusterIndexMap;
 private TreeMap<ModuleKey, ModuleRef>            moduleMap;

 public Map<String, AgeObjectProxy> getGlobalIndexMap()
 {
  return globalIndexMap;
 }

 public void setGlobalIndexMap(Map<String, AgeObjectProxy> globalIndexMap)
 {
  this.globalIndexMap = globalIndexMap;
 }

 public Map<String, Map<String, AgeObjectProxy>> getClusterIndexMap()
 {
  return clusterIndexMap;
 }

 public void setClusterIndexMap(Map<String, Map<String, AgeObjectProxy>> clusterIndexMap)
 {
  this.clusterIndexMap = clusterIndexMap;
 }

 public TreeMap<ModuleKey, ModuleRef> getModuleMap()
 {
  return moduleMap;
 }

 public void setModuleMap(TreeMap<ModuleKey, ModuleRef> moduleMap)
 {
  this.moduleMap = moduleMap;
 }

}
