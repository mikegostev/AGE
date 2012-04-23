package uk.ac.ebi.age.storage;

import java.util.Collection;

import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.model.writable.AgeExternalObjectAttributeWritable;
import uk.ac.ebi.age.model.writable.AgeFileAttributeWritable;
import uk.ac.ebi.age.model.writable.AgeRelationWritable;

import com.pri.util.Pair;
import com.pri.util.collection.CollectionMapCollection;

public class ConnectionInfo
{
 private Collection<Pair<AgeExternalObjectAttributeWritable, AgeObject>> objectAttributesReconnection;
 private Collection<Pair<AgeFileAttributeWritable, Boolean>>             fileAttributesResolution;
 private Collection<AgeRelationWritable>                                 relationsRemoval;
 private Collection<AgeRelationWritable>                                 relationsReconnection;
 private Collection<AgeRelationWritable>                                 relationsAttachment;

 public Collection<Pair<AgeExternalObjectAttributeWritable, AgeObject>> getObjectAttributesReconnection()
 {
  return objectAttributesReconnection;
 }

 public void setObjectAttributesReconnection(Collection<Pair<AgeExternalObjectAttributeWritable, AgeObject>> objectAttributesReconnection)
 {
  this.objectAttributesReconnection = objectAttributesReconnection;
 }

 public Collection<Pair<AgeFileAttributeWritable, Boolean>> getFileAttributesResolution()
 {
  return fileAttributesResolution;
 }

 public void setFileAttributesResolution(Collection<Pair<AgeFileAttributeWritable, Boolean>> fileAttributesResolution)
 {
  this.fileAttributesResolution = fileAttributesResolution;
 }

 public Collection<AgeRelationWritable> getRelationsRemoval()
 {
  return relationsRemoval;
 }

 public void setRelationsRemoval(Collection<AgeRelationWritable> relationsRemoval)
 {
  this.relationsRemoval = relationsRemoval;
 }

 public Collection<AgeRelationWritable> getRelationsReconnection()
 {
  return relationsReconnection;
 }

 public void setRelationsReconnection(Collection<AgeRelationWritable> relationsReconnection)
 {
  this.relationsReconnection = relationsReconnection;
 }

 public void setRelationsAttachment(CollectionMapCollection<AgeRelationWritable> relAtt )
 {
  relationsAttachment = relAtt;
 }

 public Collection<AgeRelationWritable> getRelationsAttachment()
 {
  return relationsAttachment;
 }

 public void setRelationsAttachment(Collection<AgeRelationWritable> relationsAttachment)
 {
  this.relationsAttachment = relationsAttachment;
 }
}
