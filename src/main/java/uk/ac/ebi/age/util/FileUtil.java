package uk.ac.ebi.age.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;

public class FileUtil
{

 public static boolean linkOrCopyFile( File exstFile, File newFile ) throws IOException
 {
  boolean res = false;

  try
  {
   Files.createLink(newFile.toPath(), exstFile.toPath());
   res=true;
  }
  catch(Exception e)
  {
  }

  if( ! res )
   FileUtils.copyFile(exstFile, newFile);
  
  return res;
 }
}
