package uk.ac.ebi.age.model.impl;

import uk.ac.ebi.age.model.AgeSemanticElement;
import uk.ac.ebi.age.model.SemanticModel;

public class AgeSemanticElementImpl implements AgeSemanticElement
{
 private SemanticModel model;
 
 public AgeSemanticElementImpl( SemanticModel m )
 {
  model=m;
 }
 
 @Override
 public SemanticModel getSemanticModel()
 {
  return model;
 }
 
 public void setSemanticModel( SemanticModel m )
 {
  model=m;
 }

}
