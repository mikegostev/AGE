package uk.ac.ebi.age.entity;

public class ComposedID implements ID
{
 private EntityDomain domain;
 private String id;
 private ID parentObjectID;
 
 @Override
 public EntityDomain getDomain()
 {
  return domain;
 }

 @Override
 public String getId()
 {
  return id;
 }

 @Override
 public ID getParentObjectID()
 {
  return parentObjectID;
 }

 public void setDomain(EntityDomain domain)
 {
  this.domain = domain;
 }


 public void setComponents( String c1, String c2 )
 {
  id = createComponentId(c1, c2);
 }

 public void setComponents( String ... comps )
 {
  id = createComponentId(comps);
 }
 
 public void setParentObjectID(ID parentObjectID)
 {
  this.parentObjectID = parentObjectID;
 }
 
 public static String createComponentId( String c1, String c2 )
 {
  return String.valueOf(c1.length())+c1+String.valueOf(c2.length())+c2;
 }

 public static String createComponentId( String ... comps )
 {
  StringBuilder sb = new StringBuilder(100);
  
  
  for( String c : comps )
   sb.append( c.length() ).append(c);

  return sb.toString(); 
 }

}
