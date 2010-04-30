package uk.ac.ebi.age.model.impl.v1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import uk.ac.ebi.age.model.AgeAttribute;
import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AgeRelation;
import uk.ac.ebi.age.model.ContextSemanticModel;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.writable.AgeExternalRelationWritable;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;
import uk.ac.ebi.age.model.writable.SubmissionWritable;

class SubmissionImpl  implements SubmissionWritable, Serializable
{
 private static final long serialVersionUID = 1L;
 
// private AgeClass submissionClass = getModelFactoy.createAgeClass("$submission", "SBM", null);
// private AgeRelationClass submissionRelationClass ;
// private AgeRelationClass submissionInvRelationClass;
  
// private Collection<AgeClass> classes = new ArrayList<AgeClass>(10);
 private Collection<AgeObjectWritable> objects = new ArrayList<AgeObjectWritable>(50);
 private ContextSemanticModel model;
 private Collection<AgeExternalRelationWritable> extRels ;

// private Map<AgeRelationClass, Collection<AgeRelationWritable>> rels ;
// private Collection<AgeRelationWritable> relLst;
 
// private Collection<SubmissionBlock> blocks = new ArrayList<SubmissionBlock>(10);
// private Collection<SubmissionBlock> blocks = new ArrayList<SubmissionBlock>(10);

 private String id;
 private String descr;

 public SubmissionImpl(ContextSemanticModel sm)
 {
  model = sm;
//  submissionClass = sm.createAgeClass("$submission", "SBM", sm);
//  
//  submissionRelationClass = ModelFactoryImpl.getInstance().createAgeRelationClass("$insubmission", sm);
//  submissionInvRelationClass = ModelFactoryImpl.getInstance().createAgeRelationClass("!$insubmission", sm);
//  
//  submissionRelationClass.setInverseClass(submissionInvRelationClass);
//  submissionInvRelationClass.setInverseClass(submissionRelationClass);
//  
//  relLst = new ArrayList<AgeRelationAlt>(100);
//  
//  rels = Collections.singletonMap(submissionRelationClass, relLst);
  
 }


 public String getDescription()
 {
  return descr;
 }

 public void setDescription( String dsc )
 {
  descr=dsc;
 }

// public void addClass(AgeClass cls)
// {
//  classes.add(cls);
// }

 public void addObject(AgeObjectWritable obj)
 {
//  obj.createRelation(this, submissionInvRelationClass);
//  relLst.add(getSemanticModel().createAgeRelation(obj, submissionRelationClass));

  objects.add(obj);
  
  for( AgeRelation rel : obj.getRelations())
  {
   if( rel instanceof AgeExternalRelationWritable )
   {
    if( extRels == null )
     extRels = new ArrayList<AgeExternalRelationWritable>(10);
    
    extRels.add((AgeExternalRelationWritable)rel);
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

// public AgeClass getAgeElClass()
// {
//  return submissionClass;
// }

 public AgeAttribute getAttribute(AgeAttributeClass attrCls)
 {
  return null;
 }

 public Collection<AgeAttribute> getAttributes()
 {
  return null;
 }

 public int getOrder()
 {
  return 0;
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
  model.setMasterModel( newModel );
  
  for( AgeObjectWritable o : objects )
   o.resetModel();
 }
 
// public Collection<AgeRelationAlt> getRelations()
// {
//  return relLst;
// }
//
// public Submission getSubmission()
// {
//  return this;
// }
//
// public void addAttribute(AgeAttribute attr)
// {
// }
//
// public void addRelation(AgeRelationAlt createExternalRelation)
// {
// }
//
// public AgeAttributeAlt createAgeAttribute(AgeAttributeClass attrClass)
// {
//  return null;
// }
//
// public AgeRelationAlt createExternalRelation(String val, AgeRelationClass relClass)
// {
//  return null;
// }
//
// public AgeRelationAlt createRelation(AgeObjectAlt targetObj, AgeRelationClass relClass)
// {
//  return null;
// }
//
// public void setOrder(int row)
// {
// }
//
// public void setSubmission(Submission s)
// {
// }
//
// public Map<AgeRelationClass, Collection<AgeRelationAlt>> getRelationsMap()
// {
//  return rels;
// }

}
