package uk.ac.ebi.age.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import uk.ac.ebi.age.service.IdGenException;
import uk.ac.ebi.age.service.IdGenerator;

public class SeqIdGeneratorImpl extends IdGenerator
{
 private final static int ID_BLOCK_LEN=100; 

 private int nextId;
 private int maxId;
 
 private File idFile;
 
 public SeqIdGeneratorImpl( String path )
 {
  idFile = new File(path);
  idFile.getParentFile().mkdirs( );
  
  update();
 }
 
 private void update()
 {
  int availId = -1;

  RandomAccessFile file = null;
  try
  {
   file = new RandomAccessFile(idFile, "rw");
   String line = file.readLine().trim();

   try
   {
    availId = Integer.parseInt(line);
   }
   catch(Exception e)
   {
   }

   if(availId == -1)
   {
    nextId = 1;
    maxId = ID_BLOCK_LEN;
    availId = maxId + 1;
   }
   else
   {
    nextId = availId;
    maxId = availId + ID_BLOCK_LEN;
    availId = maxId + 1;
   }

   file.seek(0);
   file.writeChars(String.valueOf(availId));

  }
  catch(IOException e)
  {
   throw new IdGenException("Can't read/write ID store file. ", e);
  }
  finally
  {
   if(file != null)
   {
    try
    {
     file.close();
    }
    catch(Exception e2)
    {
     throw new IdGenException("Can't close ID store file. ", e2);
    }
   }

  }

 }
 
 @Override
 public String getStringId()
 {
  if( nextId == maxId )
   update();
  
  return String.valueOf(nextId++);
 }

 @Override
 public void shutdown()
 {
  RandomAccessFile file = null;
  try
  {
   file = new RandomAccessFile(idFile, "w");
   file.writeChars(String.valueOf(nextId));
  }
  catch(IOException e)
  {
   throw new IdGenException("Can't read/write ID store file. ", e);
  }
  finally
  {
   if(file != null)
   {
    try
    {
     file.close();
    }
    catch(Exception e2)
    {
     throw new IdGenException("Can't close ID store file. ", e2);
    }
   }

  }
 }

}
