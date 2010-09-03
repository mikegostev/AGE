package uk.ac.ebi.age.model.impl.v1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.Cardinality;
import uk.ac.ebi.age.model.QualifiersCondition;
import uk.ac.ebi.age.model.RestrictionType;


public class AttributeAttachmentRuleImpl implements Serializable
{
 private static final long serialVersionUID = 1L;

 private RestrictionType type = RestrictionType.MAY;
 private Cardinality cardType = Cardinality.ANY;
 private AgeAttributeClass attributeClass;
 private int cardinality=1;
 private Map<RestrictionType,Collection<QualifierAttachmentRule>> qualifiers;
 private boolean valueUnique;
 private boolean qualifiersUnique;
 private boolean subclassesIncluded=true;
 private QualifiersCondition qualifiersCondition = QualifiersCondition.ANY ;

 public AttributeAttachmentRuleImpl( RestrictionType typ )
 {
  type=typ;
 }
 
 public AgeAttributeClass getAttributeClass()
 {
  return attributeClass;
 }

 public RestrictionType getType()
 {
  return type;
 }

 public void setType(RestrictionType type)
 {
  this.type = type;
 }

 public Cardinality getCardinalityType()
 {
  return cardType;
 }

 public void setCardinalityType(Cardinality cardType)
 {
  this.cardType = cardType;
 }

 public int getCardinality()
 {
  return cardinality;
 }

 public void setCardinality(int cardinality)
 {
  this.cardinality = cardinality;
 }

 public Map<RestrictionType,Collection<QualifierAttachmentRule>> getQualifiersMap()
 {
  return qualifiers;
 }

 public void addQualifier( QualifierAttachmentRule qr )
 {
  if( qualifiers == null )
  {
   qualifiers=new TreeMap<RestrictionType, Collection<QualifierAttachmentRule>>();
   Collection<QualifierAttachmentRule> coll = new ArrayList<QualifierAttachmentRule>(5);
   coll.add(qr);
   qualifiers.put(qr.getType(), coll);
   return;
  }
  
  Collection<QualifierAttachmentRule> coll = qualifiers.get(qr.getType());
  
  if( coll == null )
  {
   coll = new ArrayList<QualifierAttachmentRule>(5);
   qualifiers.put(qr.getType(), coll);
  }
 
  coll.add(qr);
 
 }
 
 public void setAttributeClass(AgeAttributeClass attributeClass)
 {
  this.attributeClass = attributeClass;
 }

 public String toString()
 {
  StringBuilder sb = new StringBuilder();
  
  sb.append("object ").append(type.getTitle()).append(" have ");
  sb.append("attributes of class <b>").append(attributeClass.getName()).append("</b> (");

  if( subclassesIncluded )
   sb.append("including subclasses)");
  else
   sb.append("excluding subclasses)");
   
  
  if( type != RestrictionType.MUSTNOT )
  {
//   sb.append(" that have ").append(cardType.name());
   
   if( cardType == Cardinality.ANY)
    sb.append(" that may have any number of values ");
   else
    sb.append(" that must have ").append(cardType.getTitle()).append(" ").append(cardinality).append(" value").append(cardinality>1?"s ":" ");
   
   if( ( cardinality > 1 || cardType == Cardinality.MIN ) && (valueUnique || qualifiersUnique) )
   {
    sb.append("(all ");
    
    if( valueUnique )
    {
     sb.append("values");
     
     if(qualifiersUnique)
      sb.append(" and ");
    }

    if(qualifiersUnique)
     sb.append("qualifiers");
    
    sb.append(" must be unique) ");
    
   }
   
   if( qualifiers != null )
   {
    boolean hasQ=false;
  
    sb.append("and ");
    
    for( RestrictionType rt : RestrictionType.values() )
    {
     Collection<QualifierAttachmentRule> qcoll = qualifiers.get(rt);
     
     if( qcoll != null && qcoll.size() > 0 )
     {
      hasQ=true;
      sb.append(rt.getTitle()).append(" have qualifiers of class");
      
      if( qcoll.size() > 1 )
       sb.append("es");
      
      sb.append(" ");
      
      for( QualifierAttachmentRule qr : qcoll )
       sb.append("<b>").append(qr.getAttributeClass().getName()).append("</b>, ");
      
      sb.setLength( sb.length()-2 );
      
      sb.append(" and ");
     }
    }

    if( hasQ )
     sb.setLength( sb.length()-5 );
    else
     sb.setLength( sb.length()-6 );
   }
  }
  else
  {
   if( cardType != Cardinality.ANY )
   {
    sb.append(" if they have ");
    
    switch(cardType)
    {
     case EXACT:
      sb.append(cardinality).append(" values");
      break;
 
     case MIN:
      sb.append(cardinality).append(" or more values");
      break;

     case MAX:
      sb.append(cardinality).append(" or less values");
      break;

     default:
      break;
    }
   }
   
   if( qualifiersCondition != QualifiersCondition.ANY )
   {
    if( cardType != Cardinality.ANY )
     sb.append(" and");
    
    if( qualifiersCondition == QualifiersCondition.NONE )
     sb.append(" if they have no qualifiers");
    else if( qualifiers != null && qualifiers.size() > 0 )
    {
     sb.append(" if they have the following qualifier");
     
     sb.append( qualifiers.size() > 1?"s ":" ");
     
     for( QualifierAttachmentRule qr : qualifiers.get(RestrictionType.MUST) )
      sb.append("<b>").append(qr.getAttributeClass().getName()).append("</b>, ");

     sb.setLength(sb.length()-2);
    }
   }
  }
  
  return sb.toString();
 }

 public void clearQualifiers()
 {
  if( qualifiers != null )
   qualifiers.clear();
 }

 public boolean isValueUnique()
 {
  return valueUnique;
 }
 
 public boolean isQualifiersUnique()
 {
  return qualifiersUnique;
 }

 public void setValueUnique(boolean valueUnique)
 {
  this.valueUnique = valueUnique;
 }

 public void setQualifiersUnique(boolean qualifiersUnique)
 {
  this.qualifiersUnique = qualifiersUnique;
 }

 public void setQualifiersCondition(QualifiersCondition qc)
 {
  qualifiersCondition = qc;
 }

 public QualifiersCondition getQualifiersCondition()
 {
  return qualifiersCondition;
 }

 public boolean isSubclassesIncluded()
 {
  return subclassesIncluded;
 }

 public void setSubclassesIncluded(boolean subclassesIncluded)
 {
  this.subclassesIncluded = subclassesIncluded;
 }
}
