package uk.ac.ebi.age.classif;

import java.util.Collection;
import java.util.List;

import uk.ac.ebi.age.classif.exception.TagException;

import com.pri.util.collection.ListFragment;

public interface ClassifierDB
{

 void deleteClassifier(String csfId) throws TagException;

 void addClassifier(String csfId, String csfDesc) throws TagException;

 void updateClassifier(String csfId, String csfDesc) throws TagException;

 Classifier getClassifier( String id );
 List< ? extends Classifier> getClassifiers(int begin, int end);

 int getClassifiersTotal();

 ListFragment<Classifier> getClassifiers(String string, String string2, int begin, int end);

 void removeTagFromClassifier(String clsId, String tagId) throws TagException;

 void addTagToClassifier(String clsId, String tagId, String description, String parentTagId) throws TagException;

 void updateTag(String clsId, String tagId, String desc, String parentTagId) throws TagException;

 Tag getTag( String clsfId, String tagId ) throws TagException;

 Collection< ? extends Tag> getTagsOfClassifier(String clsId, String parentTagId) throws TagException;
 Collection< ? extends Tag> getTagsOfClassifier(String clsId) throws TagException;

}
