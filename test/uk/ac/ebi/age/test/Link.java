package uk.ac.ebi.age.test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Link
{

 /**
  * @param args
  */
 public static void main(String[] args)
 {
  Path path =Paths.get("C:/temp/A.txt");    // Microsoft Windows syntax
//Path path = Paths.get("/home/joe/foo");       // Solaris syntax
  System.out.format("toString: %s%n", path.toString());
  System.out.format("getName(0): %s%n", path.getName(0));
  System.out.format("getNameCount: %d%n", path.getNameCount());
  System.out.format("subpath(0,2): %s%n", path.subpath(0,2));
  System.out.format("getParent: %s%n", path.getParent());
  System.out.format("getRoot: %s%n", path.getRoot());
  System.out.format("exists: %b%n",Files.exists(path));
//  System.out.format("isHidden: %s%n", path.isHidden());
  
  Path lnk = Paths.get("E:\\tmp\\B.txt");
  try
  {
   Path clnk = Files.createLink(lnk, path);
   System.out.format("toString: %s%n", clnk.toString());

  }
  catch(IOException e)
  {
   // TODO Auto-generated catch block
   e.printStackTrace();
  }
  
 }

}
