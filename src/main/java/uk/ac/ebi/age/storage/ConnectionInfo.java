package uk.ac.ebi.age.storage;

import java.util.Collection;

import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.model.writable.AgeExternalObjectAttributeWritable;
import uk.ac.ebi.age.model.writable.AgeFileAttributeWritable;
import uk.ac.ebi.age.model.writable.AgeRelationWritable;

import com.pri.util.Pair;

public class ConnectionInfo
{
 private Collection<Pair<AgeExternalObjectAttributeWritable, AgeObject>> objectAttributesReconnection;
 private Collection<Pair<AgeFileAttributeWritable, String>> fileAttributesReconnection;
 private Collection<AgeRelationWritable> relationsRemoval;
}
