package uk.ac.ebi.age.mng;

import uk.ac.ebi.age.log.LogNode;
import uk.ac.ebi.age.log.LogNode.Level;
import uk.ac.ebi.age.model.SubmissionContext;
import uk.ac.ebi.age.model.writable.SubmissionWritable;
import uk.ac.ebi.age.parser.AgeTab2AgeConverter;
import uk.ac.ebi.age.parser.AgeTabSubmission;
import uk.ac.ebi.age.parser.AgeTabSyntaxParser;
import uk.ac.ebi.age.parser.ParserException;
import uk.ac.ebi.age.parser.impl.AgeTab2AgeConverterImpl;
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
 private AgeTab2AgeConverter converter = new AgeTab2AgeConverterImpl();
 private AgeSemanticValidator validator = new AgeSemanticValidatorImpl();
 
 public SubmissionWritable prepareSubmission( String text, SubmissionContext context, LogNode logRoot )
 {
  AgeTabSubmission atSbm=null;
  
  LogNode atLog = logRoot.branch("Parsing AgeTab");
  try
  {
   atSbm =  ageTabParser.parse(text);
   atLog.log(Level.INFO, "Success");
  }
  catch(ParserException e)
  {
   atLog.log(Level.ERROR, "Parsing failed: "+e.getMessage()+". Row: "+e.getLineNumber()+". Col: "+e.getColumnNumber());
   return null;
  }

  LogNode convLog = logRoot.branch("Converting AgeTab to Age model");
  SubmissionWritable ageSbm = converter.convert(atSbm, SemanticManager.getInstance().getContextModel(context), convLog );
  
  if( ageSbm != null )
   convLog.log(Level.INFO, "Success");
  else
  {
   convLog.log(Level.ERROR, "Conversion failed");
   return null;
  }
  
  LogNode semLog = logRoot.branch("Validating semantic");
  
  if( validator.validate(ageSbm, semLog ) )
   convLog.log(Level.INFO, "Success");
  else
  {
   convLog.log(Level.ERROR, "Validation failed");
   return null;
  }

  //Impute reverse relation and revalidate.

  return ageSbm;
 }

 public AgeTabSyntaxParser getAgeTabParser()
 {
  return ageTabParser;
 }

 public AgeTab2AgeConverter getAgeTab2AgeConverter()
 {
  return converter;
 }

 public AgeSemanticValidator getAgeSemanticValidator()
 {
  return validator;
 }
}
