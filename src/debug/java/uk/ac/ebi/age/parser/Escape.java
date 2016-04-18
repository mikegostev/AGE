package uk.ac.ebi.age.parser;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.ac.ebi.age.parser.impl.AgeTabSyntaxParserImpl;
import uk.ac.ebi.mg.spreadsheet.readers.CSVTSVSpreadsheetReader;

import com.pri.util.stream.StreamPump;

public class Escape
{
 private AgeTabSyntaxParser parser;
 
 private AgeTabModule doc;
 
 @Before
 public void prepareDoc() throws Exception
 {
  parser = new AgeTabSyntaxParserImpl( new SyntaxProfile() );
  
  InputStream ist = getClass().getResourceAsStream("Sample1.age.txt");
  
  if( ist == null )
   throw new Exception("Can't find sample file");
  
  ByteArrayOutputStream baos = new ByteArrayOutputStream();
  
  StreamPump.doPump(ist, baos, true);
  
  doc = parser.parse( new CSVTSVSpreadsheetReader( new String( baos.toByteArray(), "Unicode"), '\0') );
 }

 @Test
 public void testNoEscape()
 {
  Assert.assertNotNull(doc);
 }

}
