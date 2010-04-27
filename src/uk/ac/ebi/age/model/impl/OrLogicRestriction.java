package uk.ac.ebi.age.model.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import uk.ac.ebi.age.model.AgeAbstractObject;
import uk.ac.ebi.age.model.AgeRestriction;
import uk.ac.ebi.age.model.RestrictionException;

public class OrLogicRestriction implements AgeRestriction
{
 private Collection<AgeRestriction> operands;

 public OrLogicRestriction(Collection<AgeRestriction> operands)
 {
  this.operands=operands;
 }


 public String toString()
 {
  StringBuilder sb = new StringBuilder(1000);
  
  sb.append("OrLogic restriction:\n");
  
  boolean first=true;
  
  for( AgeRestriction rst : operands )
  {
   if( first )
   {
    first=false;
    sb.append("  ");
   }
   else
    sb.append("OR\n  ");
   
   sb.append(rst.toString()).append("\n");
  }
  
  sb.append("End of OrLogic restriction");
  
  return sb.toString();
 }

 public void validate(AgeAbstractObject obj) throws RestrictionException
 {
  List<String> msgs = new ArrayList<String>(10);
  RestrictionException lastExp = null;

  boolean satisf=false;
  for(AgeRestriction rstr : operands)
  {
   try
   {
    rstr.validate(obj);
    satisf=true;
   }
   catch(RestrictionException e)
   {
    msgs.add(e.getMessage());
    lastExp=e;
   }
  }
  
  if( ! satisf )
  {
   StringBuilder sb= new StringBuilder(1000);
   
   sb.append("The OR expression failed:\n");
   
   for( String msg : msgs )
    sb.append(msg).append("\n");
   
   throw new RestrictionException(sb.toString(), lastExp);
  }
 }

}
