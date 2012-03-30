package uk.ac.ebi.age.storage.impl.serswap;

import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import uk.ac.ebi.age.model.ModuleKey;
import uk.ac.ebi.age.storage.impl.serswap.v3.AgeObjectProxy;

public class ModuleRef implements Serializable, StoragePlug
{
 private static final long serialVersionUID = 1L;

 private ModuleKey moduleKey;
 
 transient private SoftReference<ProxyDataModule> module;
 transient private SerializedSwapStorage storage;
 
 private Map<String,AgeObjectProxy> objectProxyMap = new HashMap<String,AgeObjectProxy>();
 
 public synchronized ProxyDataModule getModule()
 {
  if( module == null )
   return null;
  
  ProxyDataModule mod = module.get();
  
  if( mod != null )
   return mod;
  
  mod = storage.loadModule(moduleKey);
  
  mod.setModuleRef( this );
  module = new SoftReference<ProxyDataModule>(mod);
  
  return mod;
 }

 public ProxyDataModule getModuleNoLoad()
 {
  if( module == null )
   return null;
  
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

 public void setModule(ProxyDataModule dm)
 {
  module = new SoftReference<ProxyDataModule>(dm);
 }

 
 public void addObject(String id, AgeObjectProxy pxObj)
 {
  objectProxyMap.put(id, pxObj);
 } 
 
 public AgeObjectProxy getObjectProxy( String id )
 {
  return objectProxyMap.get(id);
 }
 
 public Collection<AgeObjectProxy> getObjectProxies()
 {
  return objectProxyMap.values();
 }

 public SerializedSwapStorage getStorage()
 {
  return storage;
 }

 public void setStorage(SerializedSwapStorage storage)
 {
  this.storage = storage;
 }


}
