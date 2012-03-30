package uk.ac.ebi.age.model.writable;

import uk.ac.ebi.age.model.AgeExternalRelation;
import uk.ac.ebi.age.model.ResolveScope;

public interface AgeExternalRelationWritable extends AgeExternalRelation, AgeRelationWritable
{
 void setSourceObject(AgeObjectWritable ageObject);
 void setTargetObject( AgeObjectWritable obj );
 
 
 AgeExternalRelationWritable getInverseRelation();
 
 void setInverseRelation( AgeExternalRelationWritable inrv );

 void setTargetResolveScope( ResolveScope scp );
}
