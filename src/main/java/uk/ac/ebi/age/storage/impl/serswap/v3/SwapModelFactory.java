package uk.ac.ebi.age.storage.impl.serswap.v3;

import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.ModelFactory;
import uk.ac.ebi.age.model.writable.AgeExternalRelationWritable;

public interface SwapModelFactory extends ModelFactory
{
 AgeExternalRelationWritable createDefinedInferredExternalInverseRelation(AgeObjectProxy tgObj, AgeObjectProxy src, AgeRelationClass cls);
 AgeExternalRelationWritable createCustomInferredExternalInverseRelation(AgeObjectProxy tgObj, AgeObjectProxy src, String clsName);

}
