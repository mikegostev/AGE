package uk.ac.ebi.age.storage.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import uk.ac.ebi.age.model.Submission;
import uk.ac.ebi.age.model.writable.SubmissionWritable;
import uk.ac.ebi.age.storage.SubmissionReaderWriter;

public class SerializedSubmissionReaderWriter implements SubmissionReaderWriter
{

 @Override
 public SubmissionWritable read(File f) throws IOException, ClassNotFoundException
 {
  ObjectInputStream ois = new ObjectInputStream( new FileInputStream(f) );
  
  SubmissionWritable submission = (SubmissionWritable)ois.readObject();
  
  ois.close();
  
  return submission;
 }

 @Override
 public void write(Submission s, File f) throws IOException
 {
  FileOutputStream fileOut = new FileOutputStream(f);
  
  ObjectOutputStream oos = new ObjectOutputStream( fileOut );
  
  oos.writeObject(s);
  
  oos.close();
 }

 @Override
 public String getExtension()
 {
  return ".ser";
 }

}
