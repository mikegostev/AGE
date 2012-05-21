package uk.ac.ebi.age.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import org.junit.Before;
import org.junit.Test;

public class JUSample
{

 @Before public void prepare()
 {
  System.out.println("Preparing data");
 }
 
 @Test public void test1()
 {
  System.out.println("Testing");
  
  assertTrue("Correct",true);
 }
 
 @Test public void test2()
 {
  System.out.println("Testing 2");
  
  assumeTrue(true);
  
  assertTrue("Correct",false);
  
//  throw new RuntimeException();
 }

 
}
