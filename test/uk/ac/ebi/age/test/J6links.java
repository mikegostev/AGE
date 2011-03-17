package uk.ac.ebi.age.test;

public class J6links
{
 public interface CLibrary extends Library
 {
  CLibrary INSTANCE = (CLibrary) Native.loadLibrary((Platform.isWindows() ? "msvcrt" : "c"), CLibrary.class);

  int link(String fromFile, String toFile);
 }

 public static void main(String[] args)
 {
  CLibrary.INSTANCE.link(args[0], args[1]);
 }

}
