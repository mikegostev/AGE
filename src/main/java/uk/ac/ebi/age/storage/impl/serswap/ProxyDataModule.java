package uk.ac.ebi.age.storage.impl.serswap;

import uk.ac.ebi.age.model.writable.DataModuleWritable;


public interface ProxyDataModule extends DataModuleWritable
{

 ModuleRef getModuleRef();

 void setModuleRef(ModuleRef moduleRef);

 SerializedSwapStorage getStorage();

}