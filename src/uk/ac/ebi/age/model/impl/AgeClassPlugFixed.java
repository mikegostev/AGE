package uk.ac.ebi.age.model.impl;

import java.io.Serializable;

import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.util.Plug;

public class AgeClassPlugFixed implements Serializable, Plug, uk.ac.ebi.age.model.AgeClassPlug
{
 private AgeClass ageClass;
 
 public AgeClassPlugFixed(AgeClass cls)
 {
  ageClass = cls;
 }

 public void unplug()
 {
 }
 
 public boolean plug()
 {
  return true;
 }
 
 public AgeClass getAgeClass()
 {
  return ageClass;
 }
}
