package uk.ac.ebi.age.storage;

import java.io.File;
import java.io.IOException;

import uk.ac.ebi.age.model.Submission;
import uk.ac.ebi.age.model.writable.SubmissionWritable;

public interface SubmissionReaderWriter
{
 SubmissionWritable read( File f ) throws IOException, ClassNotFoundException;
 void write( Submission s, File f ) throws IOException;
 String getExtension();
}
