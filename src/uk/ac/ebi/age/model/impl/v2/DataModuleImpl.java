package uk.ac.ebi.age.model.impl.v2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import uk.ac.ebi.age.model.AgeRelation;
import uk.ac.ebi.age.model.ContextSemanticModel;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.writable.AgeExternalRelationWritable;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;
import uk.ac.ebi.age.model.writable.AgeRelationWritable;
import uk.ac.ebi.age.model.writable.AttributedWritable;
import uk.ac.ebi.age.model.writable.DataModuleWritable;

class DataModuleImpl  implements DataModuleWritable, Serializable
{
 private static final long serialVersionUID = 1L;
 
 private long version;
 private Collection<AgeObjectWritable> objects = new ArrayList<AgeObjectWritable>(50);
 private ContextSemanticModel model;
 private Collection<AgeExternalRelationWritable> extRels ;

 private String id;
 private String descr;

 public DataModuleImpl(ContextSemanticModel sm)
 {
  model = sm;
 }


 public String getDescription()
 {
  return descr;
 }

 public void setDescription( String dsc )
 {
  descr=dsc;
 }

 public void addObject(AgeObjectWritable obj)
 {

  objects.add(obj);

  if( obj.getRelations() != null )
  {
   for(AgeRelation rel : obj.getRelations())
   {
    if(rel instanceof AgeExternalRelationWritable)
    {
     if(extRels == null)
      extRels = new ArrayList<AgeExternalRelationWritable>(10);

     extRels.add((AgeExternalRelationWritable) rel);
    }
   }
  }
  
 }

 
 public void setId(String id)
 {
  this.id=id;
 }

 public String getId()
 {
  return id;
 }

 public Collection<AgeObjectWritable> getObjects()
 {
  return objects;
 }


 @Override
 public ContextSemanticModel getContextSemanticModel()
 {
  return model;
 }


 @Override
 public Collection<AgeExternalRelationWritable> getExternalRelations()
 {
  return extRels;
 }

 
 @Override
 public void setMasterModel( SemanticModel newModel )
 {
  for( AgeObjectWritable obj : objects )
  {
   resetAttributedObject(obj);
   
   if( obj.getRelations() != null )
   {
    for( AgeRelationWritable rel : obj.getRelations() )
     resetAttributedObject(rel);
   }
   
  }
  
  model.setMasterModel( newModel );
 }
 
 private void resetAttributedObject( AttributedWritable atbObj )
 {
  atbObj.reset();
  
  if( atbObj.getAttributes() != null )
  {
   for(AttributedWritable atw : atbObj.getAttributes() )
    resetAttributedObject(atw);
  }
 }

 @Override
 public long getVersion()
 {
  return version;
 }

 @Override
 public void setVersion(long version)
 {
  this.version = version;
 }
 
}
