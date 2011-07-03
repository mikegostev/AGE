package uk.ac.ebi.age.classif;

import java.util.Collection;
import java.util.List;

import uk.ac.ebi.age.authz.ACR;
import uk.ac.ebi.age.authz.exception.AuthException;
import uk.ac.ebi.age.classif.exception.TagException;
import uk.ac.ebi.age.ext.authz.SystemAction;

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

 boolean removeProfileForGroupACR(String clsfId, String tagId, String subjId, String profileId) throws TagException;
 boolean removeProfileForUserACR(String clsfId, String tagId, String subjId, String profileId) throws TagException;
 boolean removePermissionForUserACR(String clsfId, String tagId, String subjId, SystemAction act, boolean allow) throws TagException;
 boolean removePermissionForGroupACR(String clsfId, String tagId, String subjId, SystemAction act, boolean allow) throws TagException;

 void addProfileForGroupACR(String clsfId, String tagId, String subjId, String profileId) throws TagException, AuthException;
 void addProfileForUserACR(String clsfId, String tagId, String subjId, String profileId) throws TagException, AuthException;
 void addActionForUserACR(String clsfId, String tagId, String subjId, SystemAction act, boolean allow) throws TagException, AuthException;
 void addActionForGroupACR(String clsfId, String tagId, String subjId, SystemAction act, boolean allow) throws TagException, AuthException;

 Collection< ? extends ACR> getACL(String clsfId, String tagId) throws TagException;

}
