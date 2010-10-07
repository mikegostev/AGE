package uk.ac.ebi.age.mng;

import uk.ac.ebi.age.log.LogNode;
import uk.ac.ebi.age.log.impl.BufferLogger;
import uk.ac.ebi.age.model.SubmissionContext;
import uk.ac.ebi.age.model.writable.SubmissionWritable;
import uk.ac.ebi.age.parser.AgeTab2AgeConverter;
import uk.ac.ebi.age.parser.AgeTabSubmission;
import uk.ac.ebi.age.parser.AgeTabSyntaxParser;
import uk.ac.ebi.age.parser.ConvertionException;
import uk.ac.ebi.age.parser.ParserException;
import uk.ac.ebi.age.parser.SemanticException;
import uk.ac.ebi.age.parser.impl.AgeTabSyntaxParserImpl;
import uk.ac.ebi.age.validator.AgeSemanticValidator;
import uk.ac.ebi.age.validator.impl.AgeSemanticValidatorImpl;

public class SubmissionManager
{
 private static SubmissionManager instance = new SubmissionManager();
 
 public static SubmissionManager getInstance()
 {
  return instance;
 }
 
 private AgeTabSyntaxParser ageTabParser = new AgeTabSyntaxParserImpl();
 private AgeTab2AgeConverter converter;
 private AgeSemanticValidator validator = new AgeSemanticValidatorImpl();
 
 public boolean submit( String text, SubmissionContext context )
 {
  try
  {
   BufferLogger logBuf = new BufferLogger();
   LogNode logRoot = logBuf.getRootNode();

   AgeTabSubmission atSbm =  ageTabParser.parse(text);
   
   SubmissionWritable ageSbm = converter.convert(atSbm, SemanticManager.getInstance().getContextModel(context), logRoot.branch("Converting AgeTab to Age model") );
  
   validator.validate(ageSbm, logRoot.branch("Validationg semantic") );
   
   //Impute reverse relation and revalidate.
  }
  catch(ParserException e)
  {
   // TODO Auto-generated catch block
   e.printStackTrace();
  }
  catch(SemanticException e)
  {
   // TODO Auto-generated catch block
   e.printStackTrace();
  }
  catch(ConvertionException e)
  {
   // TODO Auto-generated catch block
   e.printStackTrace();
  }
  
  return true;
  
 }
}
