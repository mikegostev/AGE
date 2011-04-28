package uk.ac.ebi.age.ext.submission;

import java.io.Serializable;
import java.util.List;

public class SubmissionDiff implements Serializable
{
 private static final long serialVersionUID = 1L;

 private long modTime;
 private long lastModTime;
 private String id;
 
 private String updateDescription;
 
 private List<DataModuleDiff> moduleDiffs;
 private List<AttachmentDiff> attachmentDiffs;
}
