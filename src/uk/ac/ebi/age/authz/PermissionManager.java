package uk.ac.ebi.age.authz;

import uk.ac.ebi.age.authz.ACR.Permit;
import uk.ac.ebi.age.ext.authz.SystemAction;
import uk.ac.ebi.age.transaction.ReadLock;

public class PermissionManager
{
 private SessionManager sessMgr;
 private AuthDB authDB;
 
 public Permit checkPermission( SystemAction act )
 {
  Session sess = sessMgr.getSession();
  
  String user = sess!=null?sess.getUser():BuiltInUsers.ANONYMOUS.getName();
  
  ReadLock lck = authDB.getReadLock();
  
  try
  {
   User usr = authDB.getUser(lck, user);

   if(usr == null)
    return Permit.UNDEFINED;
   
   return authDB.checkSystemPermission( act, usr );
  }
  finally
  {
   lck.release();
  }
 }
}
