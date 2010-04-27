package uk.ac.ebi.age.storage.index;


public class AgeIndex
{

 public interface TextFieldExtractor
 {
  String getName();
  TextValueExtractor getExtractor();
 }

}
