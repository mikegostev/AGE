package uk.ac.ebi.age.ext.submission;

import java.io.Serializable;


public class SubmissionQuery implements Serializable
{
 private static final long serialVersionUID = 8294154958731342639L;
 
 private String query;
 private String submissionID;
 private String moduleID;
 private String submitter;
 private String modifier;

 private long createedFrom;
 private long modifiedFrom;
 private long createdTo;
 private long modifiedTo;
 
 private int limit;
 private int offset;
 
 private int total=0;
 
 public String getQuery()
 {
  return query;
 }

 public void setQuery(String query)
 {
  this.query = query;
 }

 public String getSubmissionID()
 {
  return submissionID;
 }

 public void setSubmissionID(String submissionID)
 {
  this.submissionID = submissionID;
 }

 public String getModuleID()
 {
  return moduleID;
 }

 public void setModuleID(String moduleID)
 {
  this.moduleID = moduleID;
 }

 public String getSubmitter()
 {
  return submitter;
 }

 public void setSubmitter(String submitter)
 {
  this.submitter = submitter;
 }

 public String getModifier()
 {
  return modifier;
 }

 public void setModifier(String modifier)
 {
  this.modifier = modifier;
 }

 public long getCreatedFrom()
 {
  return createedFrom;
 }

 public void setCreatedFrom(long createedFrom)
 {
  this.createedFrom = createedFrom;
 }

 public long getModifiedFrom()
 {
  return modifiedFrom;
 }

 public void setModifiedFrom(long modifiedFrom)
 {
  this.modifiedFrom = modifiedFrom;
 }

 public long getCreatedTo()
 {
  return createdTo;
 }

 public void setCreatedTo(long createdTo)
 {
  this.createdTo = createdTo;
 }

 public long getModifiedTo()
 {
  return modifiedTo;
 }

 public void setModifiedTo(long modifiedTo)
 {
  this.modifiedTo = modifiedTo;
 }

 public int getLimit()
 {
  return limit;
 }

 public void setLimit(int limit)
 {
  this.limit = limit;
 }

 public int getOffset()
 {
  return offset;
 }

 public void setOffset(int offset)
 {
  this.offset = offset;
 }

 public int getTotal()
 {
  return total;
 }

 public void setTotal(int total)
 {
  this.total = total;
 }

}
