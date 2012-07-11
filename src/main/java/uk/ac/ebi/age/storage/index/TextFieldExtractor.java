/**
 * 
 */
package uk.ac.ebi.age.storage.index;


public class TextFieldExtractor
{
 private String name;
 private TextValueExtractor extractor;
 private boolean store;
 
 public TextFieldExtractor(String fName, TextValueExtractor extr)
 {
  this( fName, extr, false);
 }

 public TextFieldExtractor(String fName, TextValueExtractor extr, boolean stor)
 {
  name=fName;
  extractor = extr;
  store=stor;
 }

 public String getName()
 {
  return name;
 }
 
 public void setName(String name)
 {
  this.name = name;
 }
 
 public TextValueExtractor getExtractor()
 {
  return extractor;
 }
 
 public void setExtractor(TextValueExtractor extractor)
 {
  this.extractor = extractor;
 }

 public boolean isStoreValue()
 {
  return store;
 }
 

}