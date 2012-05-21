package uk.ac.ebi.age.parser;

import org.junit.Before;
import org.junit.Test;

import uk.ac.ebi.age.parser.impl.AgeTabSyntaxParserImpl;

public class Escape
{
 private AgeTabSyntaxParser parser;
 
 @Before
 public void prepareDoc()
 {
  parser = new AgeTabSyntaxParserImpl( new SyntaxProfile() );
 }

 @Test
 public void testNoEscape()
 {
  
 }

}
