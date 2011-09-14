package uk.ac.ebi.age.annotation;

import java.io.Serializable;

import uk.ac.ebi.age.entity.ID;
import uk.ac.ebi.age.transaction.Transaction;
import uk.ac.ebi.age.transaction.TransactionalDB;

public interface AnnotationManager extends TransactionalDB
{
 Object getAnnotation(Topic tpc, ID objId);

 boolean addAnnotation(Topic tpc, ID objId, Serializable value);

 boolean addAnnotation(Transaction trn, Topic tpc, ID objId, Serializable value);
}
