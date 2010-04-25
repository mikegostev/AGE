package uk.ac.ebi.age.model.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNaryBooleanClassExpression;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectCardinalityRestriction;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLQuantifiedRestriction;
import org.semanticweb.owlapi.model.OWLTypedLiteral;

import uk.ac.ebi.age.model.AgeAbstractClass;
import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.AgeClassProperty;
import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.AgeRestriction;
import uk.ac.ebi.age.model.DataType;
import uk.ac.ebi.age.model.ModelException;
import uk.ac.ebi.age.model.ModelFactory;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.writable.AgeAttributeWritable;
import uk.ac.ebi.age.model.writable.AgeExternalRelationWritable;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;
import uk.ac.ebi.age.model.writable.AgeRelationWritable;

public class SemanticModelImpl implements SemanticModel
{
 private static Log log;
 
// {
//  assert (log=log!=null?log:LogFactory.getLog(this.getClass())) != null;
// }
 
 {
  log=log!=null?log:LogFactory.getLog(this.getClass());
 }
 
 private static final String CLASS_ROOT_ANNOTATION="http://www.ebi.ac.uk/age/classesRoot";
 private static final String RELATION_ROOT_ANNOTATION="http://www.ebi.ac.uk/age/relationsRoot1";
 private static final String ATTRIBUTE_ROOT_ANNOTATION="http://www.ebi.ac.uk/age/attributesRoot";
 private static final String ATTRIBUTE_ATTACHMENT_PROPERTY="http://www.ebi.ac.uk/age/attributeProperty";
 private static final String DATATYPE_ANNOTATION="http://www.ebi.ac.uk/age/datatype";
 private static final String PREFIX_ANNOTATION="http://www.ebi.ac.uk/age/prefix";
 
 private static final String defaultClassRootName = "classes";
 private static final String defaultRelationRootName = "relations";
 private static final String defaultAttributeRootName = "attributes";
 
 
 private static class Link<AGE, OWL>
 {
  OWL owlEl;
  AGE ageEl;
 
  Link(AGE a, OWL o)
  {
   ageEl = a;
   owlEl = o;
  }
 }

 private interface HierHlp<T>
 {
  T create(String name, OWLClass orig);
  void addSubClass(T cl, T sbcls);
  void addSuperClass(T cl, T spcls);
 }
 
// private static class ClassLink
// {
//  AgeClass ageClass;
//  OWLClass owlClass;
// 
//  ClassLink(AgeClass a, OWLClass o)
//  {
//   ageClass = a;
//   owlClass = o;
//  }
// }
//
// private static class RelationLink
// {
//  AgeRelationClass ageRelation;
//  OWLObjectProperty owlProperty;
// 
//  RelationLink(AgeRelationClass a, OWLObjectProperty o)
//  {
//   ageRelation = a;
//   owlProperty = o;
//  }
// }

 private Map<String,Link<AgeClass,OWLClass>> classSourceMap = new TreeMap<String, Link<AgeClass,OWLClass>>();
 private Map<String,Link<AgeAttributeClass,OWLClass>> attributeSourceMap = new TreeMap<String, Link<AgeAttributeClass,OWLClass>>();
 private Map<String,Link<AgeRelationClass,OWLObjectProperty>> relationSourceMap = new TreeMap<String, Link<AgeRelationClass,OWLObjectProperty>>();
 
 private Map<String,AgeClass> classMap = new TreeMap<String, AgeClass>();
 private Map<String,AgeAttributeClass> attributeMap = new TreeMap<String, AgeAttributeClass>();
 private Map<String,AgeRelationClass> relationMap = new TreeMap<String, AgeRelationClass>();

 
 private AgeClass ageClassRoot ;
 private AgeAttributeClass ageAttrRoot;
 
 private AgeRelationClass attributeAttachmentRelation;
 
 private ModelFactory modelFactory;

 private OWLOntology ontology;
 private OWLClass thing;
 
 
 public SemanticModelImpl(ModelFactory modelFactory)
 {
  this.modelFactory=modelFactory;
 }
 
 public SemanticModelImpl( String sourceURI, ModelFactory modelFactory ) throws ModelException
 {
  OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
  IRI documentIRI = IRI.create(sourceURI);
  OWLDataFactory df = manager.getOWLDataFactory();

  this.modelFactory=modelFactory;
  
  
  try
  {
   ontology = manager.loadOntologyFromOntologyDocument(documentIRI);

//   Set<OWLClass> classRootSet, attrRootSet;
   Set<OWLObjectProperty> relationRootSet;
   
   thing = df.getOWLThing();
   
   OWLClass classRoot=null, attributeRoot=null;
   OWLObjectProperty attrProperty=null;
   String relationRoot=null;
   
   for( OWLAnnotation ant : ontology.getAnnotations() )
   {
    System.out.println( ant.getProperty().getIRI()+"="+((OWLTypedLiteral)ant.getValue()).getLiteral() );
   
    if( CLASS_ROOT_ANNOTATION.equals(ant.getProperty().getIRI().toString()) )
    {
     classRoot = df.getOWLClass(IRI.create(((OWLTypedLiteral)ant.getValue()).getLiteral()));
    }
    else if( ATTRIBUTE_ROOT_ANNOTATION.equals(ant.getProperty().getIRI().toString()) )
    {
     attributeRoot = df.getOWLClass(IRI.create(((OWLTypedLiteral)ant.getValue()).getLiteral()));
    }
    else if( RELATION_ROOT_ANNOTATION.equals(ant.getProperty().getIRI().toString()) )
    {
     relationRoot = ((OWLTypedLiteral)ant.getValue()).getLiteral();
    }
    else if( ATTRIBUTE_ATTACHMENT_PROPERTY.equals(ant.getProperty().getIRI().toString()) )
    {
     attrProperty = df.getOWLObjectProperty(IRI.create(((OWLTypedLiteral)ant.getValue()).getLiteral()));
     
     if( attrProperty == null )
      throw new ModelException("Attribute attachment property ("+((OWLTypedLiteral)ant.getValue()).getLiteral()+") is not found");
    }

   }
 
   ageClassRoot = reproduceClassStructure(classRoot, defaultClassRootName, classSourceMap, new HierHlp<AgeClass>(){

    public void addSubClass(AgeClass cl, AgeClass sbcls)
    {
     cl.addSubClass(sbcls);
    }

    public void addSuperClass(AgeClass cl, AgeClass spcls)
    {
     cl.addSubClass(spcls);
    }

    public AgeClass create(String name, OWLClass oCls)
    {
     String pfx=null;
     
     if( oCls != null )
     {

      for(OWLAnnotation annot : oCls.getAnnotations(ontology))
      {
       if(PREFIX_ANNOTATION.equals(annot.getProperty().getIRI().toString()))
       {
        pfx = ((OWLTypedLiteral) annot.getValue()).getLiteral();
        break;
       }
      }
     }
     return createAgeClass(name, pfx);
    }});

   String attrAtRelName = getLabel(attrProperty);
   if( attrAtRelName == null )
    attrAtRelName = attrProperty.getIRI().getFragment();
   if( attrAtRelName == null )
    attrAtRelName=attrProperty.getIRI().toString();
   
   attributeAttachmentRelation = createAgeRelationClass(attrAtRelName);
   
   ageAttrRoot = reproduceClassStructure(attributeRoot, defaultAttributeRootName, attributeSourceMap, new HierHlp<AgeAttributeClass>(){

    public void addSubClass(AgeAttributeClass cl, AgeAttributeClass sbcls)
    {
     cl.addSubClass(sbcls);
    }

    public void addSuperClass(AgeAttributeClass cl, AgeAttributeClass spcls)
    {
     cl.addSubClass(spcls);
    }

    public AgeAttributeClass create(String name, OWLClass oCls)
    {
     return createAgeAttributeClass(name, getDatatype(oCls) );
    }
   });

   AgeRelationClass ageAttrRelationClass = createAgeRelationClass(attrProperty.getIRI().toString());
   
   
   String rootRelName=defaultRelationRootName;
   relationRootSet = new HashSet<OWLObjectProperty>();

   AgeRelationClass ageRelRoot = null;
   
   for( OWLObjectProperty op : ontology.getObjectPropertiesInSignature() )
   {
    if( op.getIRI().equals(attrProperty.getIRI() ) )
     continue;
    
    if( op.getIRI().toString().equals(relationRoot) )
    {
     rootRelName = getLabel(op);
     if( rootRelName == null )
      rootRelName = op.getIRI().getFragment();
     if( rootRelName == null )
      rootRelName = op.getIRI().toString();
     
     relationRootSet.clear();
     for( OWLObjectPropertyExpression pexp : op.getSubProperties(ontology) )
     {
      if( pexp instanceof OWLObjectProperty )
       relationRootSet.add((OWLObjectProperty)pexp);
     }
     
     ageRelRoot=createAgeRelationClass(rootRelName);
     relationSourceMap.put(relationRoot, new Link<AgeRelationClass,OWLObjectProperty>( ageRelRoot, op ));
     
     break;
    }
    
    if( op.getSuperProperties(ontology).size() == 0 )
     relationRootSet.add(op);
   }


   if( ageRelRoot == null )  
    ageRelRoot=createAgeRelationClass(rootRelName);
   
   for( OWLObjectProperty opr : relationRootSet )
   {
    AgeRelationClass sbClass = makeRelationsBranch(opr) ;
    
    if( sbClass != null )
     ageRelRoot.addSubClass( sbClass );
   }
   
   createInverseRelations();
   
   Map<String, Link<AgeRelationClass,OWLObjectProperty>> attrPropMap = 
    Collections.singletonMap(
      attrProperty.getIRI().toString(),
      new Link<AgeRelationClass,OWLObjectProperty>(ageAttrRelationClass, attrProperty));
   
   for( Link<AgeClass,OWLClass> lnk : classSourceMap.values() )
   {
    for( OWLClassExpression clexpr : lnk.owlEl.getEquivalentClasses( ontology ) )
    {
     AgeRestriction rest = convertToRestriction( lnk.ageEl, clexpr, relationSourceMap, classSourceMap, 0 );
     
     if( rest != null )
      lnk.ageEl.addObjectRestriction(rest);
     
     rest = convertToRestriction( lnk.ageEl, clexpr, attrPropMap, attributeSourceMap, 0);

     if( rest != null )
      lnk.ageEl.addAttributeRestriction( rest );
    }

    for( OWLClassExpression clexpr : lnk.owlEl.getSuperClasses( ontology ) )
    {
     AgeRestriction rest = convertToRestriction( lnk.ageEl, clexpr, relationSourceMap, classSourceMap, 0 );
     
     if( rest != null )
      lnk.ageEl.addObjectRestriction(rest);

     rest = convertToRestriction( lnk.ageEl, clexpr, attrPropMap, attributeSourceMap, 0);

     if( rest != null )
      lnk.ageEl.addAttributeRestriction( rest );
    }

   }
   
   
  }
  catch(OWLOntologyCreationException e)
  {
   throw new ModelException(e.getMessage(),e);
  }

  for( Link<AgeClass,OWLClass> l : classSourceMap.values() )
   classMap.put(l.ageEl.getName(), l.ageEl);

  for( Link<AgeAttributeClass,OWLClass> l : attributeSourceMap.values() )
   attributeMap.put(l.ageEl.getName(), l.ageEl);

  for( Link<AgeRelationClass,OWLObjectProperty> l : relationSourceMap.values() )
   relationMap.put(l.ageEl.getName(), l.ageEl);

  
 }
 
 
 

 private void createInverseRelations()
 {
  for(Link<AgeRelationClass, OWLObjectProperty> l : relationSourceMap.values())
  {
   if(l.ageEl.getInverseClass() != null)
    continue;

   Set<OWLObjectPropertyExpression> invset = l.owlEl.getInverses(ontology);
   
   if( invset == null || invset.size() == 0 )
    continue;

   
   
   OWLObjectProperty invProp = invset.iterator().next().asOWLObjectProperty();

   Link<AgeRelationClass, OWLObjectProperty> invp = relationSourceMap.get(invProp.getIRI().toString());

   if(invp != null)
   {
    l.ageEl.setInverseClass(invp.ageEl);
    invp.ageEl.setInverseClass(l.ageEl);
   }
   else
   {
    AgeRelationClass irc = createAgeRelationClass("!" + l.ageEl.getName());
    irc.setDefined(false);

    l.ageEl.setInverseClass(irc);
    irc.setInverseClass(l.ageEl);
   }
  }
 }



 private <T> T reproduceClassStructure(OWLClass classRoot, String defRootName, Map<String, Link<T,OWLClass>> sourceMap, HierHlp<T> hhlp) throws ModelException
 {
  Set<OWLClass> classRootSet = new HashSet<OWLClass>();
  
  if( classRoot != null )
  {
   for( OWLClassExpression supexp :  classRoot.getSubClasses(ontology) )
   {
    if( supexp instanceof OWLClass )
     classRootSet.add((OWLClass)supexp);
   }
  }
  else
  {
   for( OWLClass cls : ontology.getClassesInSignature() )
   {
    Set<OWLClassExpression> supers = cls.getSuperClasses(ontology);
    
    if( cls.equals(thing) )
     continue;
    
    if( supers.size() == 0 )
     classRootSet.add(cls);
    else if( supers.size() == 1 )
    {
     if( supers.iterator().next().equals(thing) )
      classRootSet.add(cls);
    }
     
   }
  }
  
  String rootClassName=null;
  
  if( classRoot != null )
  {
   rootClassName=getLabel(classRoot);
   
   if( rootClassName == null )
    rootClassName=classRoot.getIRI().getFragment();
  }
  
  if( rootClassName == null )
   rootClassName=defRootName;

  
  T ageClassRoot = hhlp.create(rootClassName,null);

//  sourceMap.put(classRoot!=null?classRoot.getIRI().toString():thing.getIRI().toString(), new ClassLink(ageClassRoot,classRoot));
  
  for( OWLClass cls :  classRootSet )
  {
   T sbCls = makeClassesBranchT(cls, sourceMap, hhlp );
   
   hhlp.addSubClass(ageClassRoot,sbCls);
  }

  return ageClassRoot;
 }

 
 /*
 
 private AgeClass reproduceClassStructure(OWLClass classRoot, String defRootName, Map<String, ClassLink> sourceMap) throws ModelException
 {
  Set<OWLClass> classRootSet = new HashSet<OWLClass>();
  
  if( classRoot != null )
  {
   for( OWLClassExpression supexp :  classRoot.getSubClasses(ontology) )
   {
    if( supexp instanceof OWLClass )
     classRootSet.add((OWLClass)supexp);
   }
  }
  else
  {
   for( OWLClass cls : ontology.getClassesInSignature() )
   {
    Set<OWLClassExpression> supers = cls.getSuperClasses(ontology);
    
    if( cls.equals(thing) )
     continue;
    
    if( supers.size() == 0 )
     classRootSet.add(cls);
    else if( supers.size() == 1 )
    {
     if( supers.iterator().next().equals(thing) )
      classRootSet.add(cls);
    }
     
   }
  }
  
  String rootClassName=null;
  
  if( classRoot != null )
  {
   rootClassName=getLabel(classRoot);
   
   if( rootClassName == null )
    rootClassName=classRoot.getIRI().getFragment();
  }
  
  if( rootClassName == null )
   rootClassName=defRootName;

  
  AgeClass ageClassRoot = createAgeClass(rootClassName);

//  sourceMap.put(classRoot!=null?classRoot.getIRI().toString():thing.getIRI().toString(), new ClassLink(ageClassRoot,classRoot));
  
  for( OWLClass cls :  classRootSet )
  {
   AgeClass sbCls = makeClassesBranch(cls);
   
   sourceMap.put(cls.getIRI().toString(),  new ClassLink(sbCls,cls));
   
   ageClassRoot.addSubClass(sbCls);
  }

  return ageClassRoot;
 }
 
 */ 
 
 private <T extends AgeAbstractClass> AgeRestriction convertToRestriction(AgeClass srcClas, OWLClassExpression clexpr, 
   Map<String, Link<AgeRelationClass,OWLObjectProperty>> relMap, Map<String, Link<T,OWLClass>> domainMap, int level)
 {
  
  log.debug("Converting (class: '"+srcClas+"') ("+clexpr+") to a restriction");
  
  if( clexpr instanceof OWLClass )
  {
   if( level == 0) // We don't represent super classes as restrictions
    return null;
   
   OWLClass filler = (OWLClass)clexpr;
   
   Link<T,OWLClass> clnk = domainMap.get(filler.getIRI().toString());
   
   if( clnk == null )
   {
    log.debug("There is no AgeClass class corresponding to OWL class ("+filler.getIRI()+"). Skiping");
    return null;
   }

   AgeRestriction rstr = getModelFactory().createIsClassRestriction(srcClas,clnk.ageEl);
   log.debug("Creating restriction: "+rstr);
   
   return rstr;
  }
  else if( clexpr instanceof OWLQuantifiedRestriction<?,?> )
  {
   OWLObjectProperty prop = null;
   
   Link<AgeRelationClass,OWLObjectProperty> rlnk = null;

   OWLQuantifiedRestriction<?,?> restr = (OWLQuantifiedRestriction<?,?>)clexpr;
   
   if( !(restr.getProperty() instanceof OWLObjectPropertyExpression) )
   {
    log.debug("Property ("+restr.getProperty()+") is not OWLObjectPropertyExpression. Skiping");
    return null;
   }
   
   OWLObjectPropertyExpression pexp = (OWLObjectPropertyExpression)restr.getProperty();
   
   if( pexp.isAnonymous() )
   {
    log.debug("Property expression ("+pexp+") is anonymous. Skiping");
    return null;
   }
   
   if( !( restr.getFiller() instanceof OWLClassExpression ) )
   {
    log.debug("Expression filler ("+restr.getFiller()+") is not OWLClassExpression. Skiping");
    return null;
   }
   
   prop = pexp.getNamedProperty();
   
   AgeRestriction fillerRestr = convertToRestriction(srcClas, (OWLClassExpression)restr.getFiller(), relMap, domainMap, level+1);
   

   rlnk = relMap.get(prop.getIRI().toString());
   
   if( rlnk == null )
   {
    log.debug("There is no AgeRelation class corresponding to OWL object property ("+prop.getIRI()+"). Skiping");
    return null;
   }
   
   if( fillerRestr == null)
   {
    log.debug("Invalid filler in the restriction. Skiping");
    return null;
   }
   
//   clnk = domainMap.get(filler.getIRI().toString());
// 
//   if( clnk == null )
//   {
//    log.debug("There is no AgeClass class corresponding to OWL class ("+filler.getIRI()+"). Skiping");
//    return null;
//   }
  
   if( clexpr instanceof OWLObjectSomeValuesFrom )
   {
    AgeRestriction rstr = getModelFactory().createSomeValuesFromRestriction(srcClas,fillerRestr,rlnk.ageEl);
    log.debug("Creating restriction: "+rstr);
    
    return rstr;
   }
   else if( clexpr instanceof OWLObjectAllValuesFrom )
   {
    AgeRestriction rstr = getModelFactory().createAllValuesFromRestriction(srcClas,fillerRestr,rlnk.ageEl);
    log.debug("Creating restriction: "+rstr);
    
    return rstr;
   }
   else if( clexpr instanceof OWLObjectExactCardinality )
   {
    int cardinatily = ((OWLObjectCardinalityRestriction)clexpr).getCardinality();
  
    AgeRestriction rstr = getModelFactory().createExactCardinalityRestriction(srcClas,fillerRestr,rlnk.ageEl,cardinatily);
    log.debug("Creating restriction: "+rstr);
    
    return rstr;
   }
   else if( clexpr instanceof OWLObjectMinCardinality )
   {
    int cardinatily = ((OWLObjectCardinalityRestriction)clexpr).getCardinality();

    AgeRestriction rstr = getModelFactory().createMinCardinalityRestriction(srcClas,fillerRestr,rlnk.ageEl,cardinatily);
    log.debug("Creating restriction: "+rstr);
    
    return rstr;
   }
   else if( clexpr instanceof OWLObjectMaxCardinality )
   {
    int cardinatily = ((OWLObjectCardinalityRestriction)clexpr).getCardinality();

    AgeRestriction rstr = getModelFactory().createMaxCardinalityRestriction(srcClas,fillerRestr,rlnk.ageEl,cardinatily);
    log.debug("Creating restriction: "+rstr);
    
    return rstr;
   }
  }
  else if(  clexpr instanceof OWLNaryBooleanClassExpression )
  {
   OWLNaryBooleanClassExpression boolExpr = (OWLNaryBooleanClassExpression)clexpr;
   
   LinkedList<AgeRestriction> operands = new LinkedList<AgeRestriction>();
   
   for( OWLClassExpression subexp : boolExpr.getOperands() )
   {
    AgeRestriction subrest = convertToRestriction(srcClas, subexp, relMap, domainMap, level+1);
    
    if( subrest != null )
     operands.add(subrest);
   }
   
   if( operands.size() == 0 )
   {
    log.debug("Expression has no suitable operands");
    return null;
   }
   
   if( operands.size() == 1 )
    return operands.get(0);
   
   AgeRestriction rstr=null;

   if( clexpr instanceof OWLObjectIntersectionOf )
    rstr = getModelFactory().createAndLogicRestriction(operands);
   else if( clexpr instanceof OWLObjectUnionOf )
    rstr =  getModelFactory().createOrLogicRestriction(operands);

   log.debug("Creating restriction: "+rstr);
   
   return rstr;
  }
  

  return null;
 }

 public ModelFactory getModelFactory()
 {
  return modelFactory;
 }

 
 private <T> T makeClassesBranchT(OWLClass oobj, Map<String, Link<T,OWLClass>> sourceMap, HierHlp<T> hhlp) throws ModelException
 {
  String className = getLabel(oobj);
  
  if( className == null )
   className = oobj.getIRI().getFragment();

  if( className == null )
   className = oobj.getIRI().toString();

  
  T cls = hhlp.create(className, oobj);
  
 
  for( OWLClassExpression opexpr : oobj.getSubClasses( ontology ) )
  {
   if( opexpr instanceof OWLClass )
   {
    Link<T,OWLClass> subLnk = sourceMap.get(((OWLClass)opexpr).getIRI().toString());
    
    T subcl=null;
    
    if( subLnk == null )
     subcl = makeClassesBranchT( (OWLClass) opexpr,sourceMap, hhlp );
    else
     subcl=subLnk.ageEl;
    
    hhlp.addSubClass(cls, subcl);
    hhlp.addSuperClass( subcl, cls );
    
   }
  }
  
  sourceMap.put(oobj.getIRI().toString(), new Link<T,OWLClass>(cls,oobj) );
//  classMap.put(cls.getName(), cls);
  
  return cls;
 }
 
/* 
 private AgeClass makeClassesBranch(OWLClass oobj) throws ModelException
 {
  String className = getLabel(oobj);
  
  if( className == null )
   className = oobj.getIRI().getFragment();

  if( className == null )
   className = oobj.getIRI().toString();

  
  AgeClass cls = createAgeClass(className);
  
 
  for( OWLClassExpression opexpr : oobj.getSubClasses( ontology ) )
  {
   if( opexpr instanceof OWLClass )
   {
    ClassLink subLnk = classSourceMap.get(((OWLClass)opexpr).getIRI().toString());
    
    AgeClass subcl=null;
    
    if( subLnk == null )
     subcl = makeClassesBranch( (OWLClass) opexpr );
    else
     subcl=subLnk.ageClass;
    
    cls.addSubClass( subcl );
    subcl.addSuperClass(cls);
   }
  }
  
  classSourceMap.put(oobj.getIRI().toString(), new ClassLink(cls,oobj) );
  classMap.put(cls.getName(), cls);
  
  return cls;
 }
*/
 
 private String getLabel( OWLEntity ent )
 {
  for( OWLAnnotation annt : ent.getAnnotations(ontology) )
  {
   if( annt.getProperty().isLabel() )
    return ((OWLTypedLiteral)annt.getValue()).getLiteral();
  }

  return null;
 }
 
 private AgeRelationClass makeRelationsBranch(OWLObjectProperty opr) throws ModelException
 {
  String relName = getLabel(opr);
  
  if( relName == null )
   relName = opr.getIRI().getFragment();

  if( relName == null )
   relName = opr.getIRI().toString();

  log.debug("Processing object property '"+relName+"'");
  
  boolean suitable=true;
  
  AgeRelationClass ageRelCls = createAgeRelationClass(relName);
  
  boolean hasClassInSet=false;
  
  for( OWLClassExpression clexpr : opr.getDomains(ontology) )
  {
   if( clexpr instanceof OWLClass )
   {
    hasClassInSet = true;
    OWLClass dmainCls = (OWLClass)clexpr;
    
    for(Link<AgeClass, OWLClass> link : classSourceMap.values() )
    {
     if( link.owlEl.equals(dmainCls) ) 
     {
      log.debug("Adding AgeClass to the domain of AgeRelationClass '"+relName+"'. Domain: "+link.ageEl.getName());
      ageRelCls.addDomainClass(link.ageEl);
      
      break;
     }
    }

   }
  }
  
  if(hasClassInSet && (ageRelCls.getDomain() == null || ageRelCls.getDomain().size() == 0 ) )
  {
   suitable=false;
   log.debug("Domain classes are out of classes tree. AgeRelationClass: '"+relName+"'");

  }
  else
  {
   hasClassInSet = false;

   for(OWLClassExpression clexpr : opr.getRanges(ontology))
   {
    if(clexpr instanceof OWLClass)
    {
     hasClassInSet = true;
     OWLClass rngCls = (OWLClass) clexpr;

     for(Link<AgeClass, OWLClass> link : classSourceMap.values())
     {
      if( link.owlEl.equals(rngCls) )
      {
       log.debug("Adding AgeClass to the range of AgeRelationClass '"+relName+"'. Range: "+link.ageEl.getName());
       ageRelCls.addRangeClass(link.ageEl);
       break;
      }
     }

    }
   }

   if(hasClassInSet && (ageRelCls.getRange() == null || ageRelCls.getRange().size() == 0))
    suitable = false;
  }
  
  for(OWLObjectPropertyExpression opexpr : opr.getSubProperties(ontology))
  {
   if(opexpr instanceof OWLObjectProperty)
   {
    AgeRelationClass subCl = makeRelationsBranch((OWLObjectProperty) opexpr);
    
    if( subCl != null )
    {
     ageRelCls.addSubClass(subCl);
     subCl.addSubClass(ageRelCls);
    }
   }
  }
  
  if( !suitable &&( ageRelCls.getSubClasses() == null || ageRelCls.getSubClasses().size() == 0 ) )
   return null;
   
  relationSourceMap.put(opr.getIRI().toString(), new Link<AgeRelationClass,OWLObjectProperty>( ageRelCls, opr) );
  
  return ageRelCls;
 }

 private boolean isTheFirstSubclassOrClassOfTheSecond(OWLClass subClass, OWLClass superCls)
 {
  if( subClass.equals(superCls) )
   return true;

  if( superCls.equals(thing) )
   return true;
  
  for( OWLClassExpression expr : subClass.getSuperClasses(ontology) )
  {
   if( expr instanceof OWLClass )
    if( isTheFirstSubclassOrClassOfTheSecond((OWLClass)expr, superCls) )
     return true;
  }
  
  return false;
 }


 
 
 public AgeClass getDefinedAgeClass(String name)
 {
  return classMap.get(name);
 }

 
 public AgeClassProperty getDefinedAgeClassProperty(String name)
 {
  AgeClassProperty prop = attributeMap.get(name);
  
  if( prop != null )
   return prop;
  
  return relationMap.get(name);
 }


 public AgeObjectWritable createAgeObject(String id, AgeClass cls)
 {
  return getModelFactory().createAgeObject(id, cls, this);
 }

 public AgeRelationClass createAgeRelationClass(String name)
 {
  return getModelFactory().createAgeRelationClass(name, this);
 }

// public SubmissionWritable createSubmission()
// {
//  return getModelFactory().createSubmission(this);
// }


 public AgeAttributeWritable createAgeAttribute(AgeObject obj, AgeAttributeClass attrClass)
 {
  return modelFactory.createAgeAttribute(obj, attrClass, this);
 }

 public AgeAttributeClass createAgeAttributeClass(String name, DataType type)
 {
  return modelFactory.createAgeAttributeClass(name, type, this);
 }

 public AgeClass createAgeClass(String name, String pfx)
 {
  return modelFactory.createAgeClass(name, pfx, this);
 }

 public AgeExternalRelationWritable createExternalRelation(String id, AgeRelationClass targetClass)
 {
  return modelFactory.createExternalRelation(id, targetClass,  this);
 }

 public AgeRelationWritable createAgeRelation(AgeObjectWritable targetObj, AgeRelationClass relClass)
 {
  return modelFactory.createRelation(targetObj, relClass, this);
 }


 private DataType getDatatype(OWLClass oCls)
 {
  if( oCls == null )
   return null;
  
  try
  {
   for(OWLAnnotation annot : oCls.getAnnotations(ontology))
   {
    if(DATATYPE_ANNOTATION.equals(annot.getProperty().getIRI().toString()))
     return DataType.valueOf(((OWLTypedLiteral) annot.getValue()).getLiteral());

   }
  }
  catch( Exception e )
  {
  }
  
  return null;
 }



 public AgeRelationClass getAttributeAttachmentClass()
 {
  return attributeAttachmentRelation;
 }

 public AgeClass getAgeClass(String name)
 {
  return getDefinedAgeClass(name);
 }

 public AgeRelationClass getAgeRelationClass(String relClassName)
 {
  return getDefinedAgeRelationClass(relClassName);
 }

 public AgeRelationClass getDefinedAgeRelationClass(String name)
 {
  return relationMap.get(name);
 }



 public AgeAttributeClass getAgeAttributeClass(String attClsName)
 {
  return getDefinedAgeAttributeClass(attClsName);
 }



 public AgeAttributeClass getDefinedAgeAttributeClass(String attClsName)
 {
  return attributeMap.get(attClsName);
 }

}
