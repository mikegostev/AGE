package uk.ac.ebi.age.log.impl;

import java.io.Serializable;

import uk.ac.ebi.age.log.LogNode;
import uk.ac.ebi.age.log.LogNode.Level;

public class LogMessageBean implements Serializable
{

 private static final long serialVersionUID = 1L;

 private LogNode.Level level;
 private String message;
 
 public LogMessageBean(Level level, String message)
 {
  super();
  this.level = level;
  this.message = message;
 }
 
 public String getMessage()
 {
  return message;
 }
 
 public void setMessage(String message)
 {
  this.message = message;
 }
 
 public LogNode.Level getLevel()
 {
  return level;
 }
 
 public void setLevel(LogNode.Level level)
 {
  this.level = level;
 }
}
