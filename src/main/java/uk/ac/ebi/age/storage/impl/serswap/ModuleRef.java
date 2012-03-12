package uk.ac.ebi.age.storage.impl.serswap;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import uk.ac.ebi.age.storage.ModuleKey;

public class ModuleRef
{
 private ModuleKey moduleKey;
 
 private SoftReference<SwapDataModule> module;
 
 private Map<String,AgeObjectProxy> objectProxyMap = new HashMap<String,AgeObjectProxy>();
 
 public SwapDataModule getModule()
 {
  return module.get();
 }

 public ModuleKey getModuleKey()
 {
  return moduleKey;
 }

 public void setModuleKey(ModuleKey moduleKey)
 {
  this.moduleKey = moduleKey;
 }

 public void setModule(SwapDataModule dm)
 {
  module = new SoftReference<SwapDataModule>(dm);
 }

 public void addObject(String id, AgeObjectProxy pxObj)
 {
  objectProxyMap.put(id, pxObj);
 } 
 
 public AgeObjectProxy getObjectProxy( String id )
 {
  return objectProxyMap.get(id);
 }
}
