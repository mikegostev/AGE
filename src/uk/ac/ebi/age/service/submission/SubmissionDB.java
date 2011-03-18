package uk.ac.ebi.age.service.submission;

import java.io.File;
import java.util.List;

import uk.ac.ebi.age.ext.submission.SubmissionMeta;
import uk.ac.ebi.age.ext.submission.SubmissionQuery;

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

 public abstract void storeSubmission(SubmissionMeta sMeta, SubmissionMeta origSbm);

 public abstract void shutdown();

 public abstract List<SubmissionMeta> getSubmissions(SubmissionQuery q);

 public abstract SubmissionMeta getSubmission(String id);

 public abstract boolean hasSubmission(String id);

 public abstract void storeAttachment(String submId, String fileId, long modificationTime, File aux);


}
