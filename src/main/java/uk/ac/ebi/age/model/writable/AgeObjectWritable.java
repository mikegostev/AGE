package uk.ac.ebi.age.model.writable;

import java.util.List;

import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.IdScope;
import uk.ac.ebi.age.model.RelationClassRef;
import uk.ac.ebi.age.model.ResolveScope;



/**
 @model
*/

public interface AgeObjectWritable extends AgeObject, AttributedWritable
{
 @Override
 DataModuleWritable getDataModule();
 
 @Override
 List<? extends AgeRelationWritable> getRelations();
 
 @Override
 AgeRelationWritable getRelation(AgeRelationClass cls);
 @Override
 List< ? extends AgeRelationWritable> getRelationsByClass(AgeRelationClass cls, boolean wSbCl);
 
 
 @Override
 void addAttribute(AgeAttributeWritable attr);
 @Override
 void removeAttribute(AgeAttributeWritable attr);

 void addRelation(AgeRelationWritable r);
 void removeRelation(AgeRelationWritable rel);


 AgeExternalRelationWritable createExternalRelation( RelationClassRef ref, String val, ResolveScope scope );

 AgeRelationWritable createRelation(RelationClassRef ref, AgeObjectWritable targetObj);

 void setOrder(int row);

 void setDataModule( DataModuleWritable s );
 
 void setId( String id );

 void setIdScope( IdScope scp );
// void setOriginalId(String nId);

 @Override
 void pack();


 
// void resetModel();
}
