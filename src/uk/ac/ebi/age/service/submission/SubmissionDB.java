package uk.ac.ebi.age.service.submission;

import java.io.File;

import uk.ac.ebi.age.ext.submission.SubmissionDBException;
import uk.ac.ebi.age.ext.submission.SubmissionMeta;
import uk.ac.ebi.age.ext.submission.SubmissionQuery;
import uk.ac.ebi.age.ext.submission.SubmissionReport;

public abstract class SubmissionDB
{
 private static SubmissionDB instance;
 
 
 public static SubmissionDB getInstance()
 {
  return instance;
 }

 public static void setInstance( SubmissionDB db )
 {
  instance=db;
 }

 public abstract void init();

 public abstract void storeSubmission(SubmissionMeta sMeta, SubmissionMeta origSbm) throws SubmissionDBException;

 public abstract void shutdown();

 public abstract SubmissionReport getSubmissions(SubmissionQuery q) throws SubmissionDBException;

 public abstract SubmissionMeta getSubmission(String id) throws SubmissionDBException;

 public abstract boolean hasSubmission(String id) throws SubmissionDBException;

 public abstract void storeAttachment(String submId, String fileId, long modificationTime, File aux)  throws SubmissionDBException;


}
