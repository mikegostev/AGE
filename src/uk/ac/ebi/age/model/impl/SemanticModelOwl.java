package uk.ac.ebi.age.model.impl;

import java.io.Serializable;
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
import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.AgeRestriction;
import uk.ac.ebi.age.model.DataType;
import uk.ac.ebi.age.model.ModelException;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.writable.AgeAttributeClassWritable;
import uk.ac.ebi.age.model.writable.AgeClassWritable;
import uk.ac.ebi.age.model.writable.AgeRelationClassWritable;
import uk.ac.ebi.age.service.IdGenerator;

public abstract class SemanticModelOwl implements SemanticModel, Serializable
{
 private static final long serialVersionUID = 1L;
 
 private static transient Log log;
 
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
 
 private static class OWLParserHelper
 {
  Map<String,Link<AgeClassWritable,OWLClass>> classSourceMap = new TreeMap<String, Link<AgeClassWritable,OWLClass>>();
  Map<String,Link<AgeAttributeClassWritable,OWLClass>> attributeSourceMap = new TreeMap<String, Link<AgeAttributeClassWritable,OWLClass>>();
  Map<String,Link<AgeRelationClassWritable,OWLObjectProperty>> relationSourceMap = new TreeMap<String, Link<AgeRelationClassWritable,OWLObjectProperty>>();

  OWLOntology ontology;
  OWLClass thing;
 }

 
 
 
// private Map<String,AgeClass> classMap = new TreeMap<String, AgeClass>();
// private Map<String,AgeAttributeClass> attributeMap = new TreeMap<String, AgeAttributeClass>();
// private Map<String,AgeRelationClass> relationMap = new TreeMap<String, AgeRelationClass>();
//
// 
// private AgeClassWritable ageClassRoot ;
// private AgeAttributeClass ageAttrRoot;
// 
// private AgeRelationClass attributeAttachmentRelation;
// 
// private ModelFactory modelFactory;

 
 
// public SemanticModelOwl(ModelFactory modelFactory)
// {
//  this.modelFactory=modelFactory;
// }
 
 

 protected abstract void setClassRoot(AgeClassWritable reproduceClassStructure);
 protected abstract void setAttributeClassRoot(AgeAttributeClassWritable reproduceClassStructure);
 protected abstract void addClass(AgeClassWritable cls);
 protected abstract void addAttributeClass(AgeAttributeClassWritable cls);
 protected abstract void addRelationClass(AgeRelationClassWritable cls);

 
 protected void parseOWL( String sourceURI ) throws ModelException
 {
  OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
  IRI documentIRI = IRI.create(sourceURI);
  OWLDataFactory df = manager.getOWLDataFactory();

  final OWLParserHelper owlHlp = new OWLParserHelper();
  
  try
  {
   owlHlp.ontology = manager.loadOntologyFromOntologyDocument(documentIRI);

//   Set<OWLClass> classRootSet, attrRootSet;
   Set<OWLObjectProperty> relationRootSet;
   
   owlHlp.thing = df.getOWLThing();
   
   OWLClass classRoot=null, attributeRoot=null;
   OWLObjectProperty attrProperty=null;
   String relationRoot=null;
   
   for( OWLAnnotation ant : owlHlp.ontology.getAnnotations() )
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
 
    setClassRoot( reproduceClassStructure(classRoot, defaultClassRootName, owlHlp.classSourceMap, new HierHlp<AgeClassWritable>(){

    public void addSubClass(AgeClassWritable cl, AgeClassWritable sbcls)
    {
     cl.addSubClass(sbcls);
    }

    public void addSuperClass(AgeClassWritable cl, AgeClassWritable spcls)
    {
     cl.addSuperClass(spcls);
    }

    public AgeClassWritable create(String name, OWLClass oCls)
    {
     String pfx=null;
     String id = null;
     
     if( oCls != null )
     {

      for(OWLAnnotation annot : oCls.getAnnotations(owlHlp.ontology))
      {
       if(PREFIX_ANNOTATION.equals(annot.getProperty().getIRI().toString()))
       {
        pfx = ((OWLTypedLiteral) annot.getValue()).getLiteral();
        break;
       }
      }
      
      id = oCls.getIRI().toString();
     }
     else
      id = "AgeClass"+IdGenerator.getInstance().getStringId();
     
     return createAgeClass(name, id, pfx);
    }
   }, owlHlp ) );

   String attrAtRelName = getLabel(attrProperty, owlHlp );
   if( attrAtRelName == null )
    attrAtRelName = attrProperty.getIRI().getFragment();
   if( attrAtRelName == null )
    attrAtRelName=attrProperty.getIRI().toString();
   
//   attributeAttachmentRelation = createAgeRelationClass(attrAtRelName);
   
   setAttributeClassRoot( reproduceClassStructure(attributeRoot, defaultAttributeRootName, owlHlp.attributeSourceMap, new HierHlp<AgeAttributeClassWritable>(){

    public void addSubClass(AgeAttributeClassWritable cl, AgeAttributeClassWritable sbcls)
    {
     cl.addSubClass(sbcls);
    }

    public void addSuperClass(AgeAttributeClassWritable cl, AgeAttributeClassWritable spcls)
    {
     cl.addSuperClass(spcls);
    }

    public AgeAttributeClassWritable create(String name, OWLClass oCls)
    {
     if( oCls != null)
      return createAgeAttributeClass(name, oCls.getIRI().toString(), getDatatype(oCls, owlHlp ) );
     
     return createAgeAttributeClass(name, "AgeAttributeClass"+IdGenerator.getInstance().getStringId(), null );
    }
   }, owlHlp ) );


   AgeRelationClassWritable ageAttrRelationClass = createAgeRelationClass(attrProperty.getIRI().toString(),attrProperty.getIRI().toString());
   
   
   String rootRelName=defaultRelationRootName;
   relationRootSet = new HashSet<OWLObjectProperty>();

   AgeRelationClassWritable ageRelRoot = null;
   
   for( OWLObjectProperty op : owlHlp.ontology.getObjectPropertiesInSignature() )
   {
    if( op.getIRI().equals(attrProperty.getIRI() ) )
     continue;
    
    if( op.getIRI().toString().equals(relationRoot) )
    {
     rootRelName = getLabel(op, owlHlp );
     if( rootRelName == null )
      rootRelName = op.getIRI().getFragment();
     if( rootRelName == null )
      rootRelName = op.getIRI().toString();
     
     relationRootSet.clear();
     for( OWLObjectPropertyExpression pexp : op.getSubProperties(owlHlp.ontology) )
     {
      if( pexp instanceof OWLObjectProperty )
       relationRootSet.add((OWLObjectProperty)pexp);
     }
     
     ageRelRoot=createAgeRelationClass(rootRelName, op.getIRI().toString());
     owlHlp.relationSourceMap.put(relationRoot, new Link<AgeRelationClassWritable,OWLObjectProperty>( ageRelRoot, op ));
     
     break;
    }
    
    if( op.getSuperProperties(owlHlp.ontology).size() == 0 )
     relationRootSet.add(op);
   }


   if( ageRelRoot == null )  
    ageRelRoot=createAgeRelationClass(rootRelName,"AgeRelationClass"+IdGenerator.getInstance().getStringId());
   
   for( OWLObjectProperty opr : relationRootSet )
   {
    AgeRelationClass sbClass = makeRelationsBranch(opr, owlHlp ) ;
    
    if( sbClass != null )
     ageRelRoot.addSubClass( sbClass );
   }
   
   createInverseRelations( owlHlp );
   
   Map<String, Link<AgeRelationClassWritable,OWLObjectProperty>> attrPropMap = 
    Collections.singletonMap(
      attrProperty.getIRI().toString(),
      new Link<AgeRelationClassWritable,OWLObjectProperty>(ageAttrRelationClass, attrProperty));
   
   for( Link<AgeClassWritable,OWLClass> lnk : owlHlp.classSourceMap.values() )
   {
    for( OWLClassExpression clexpr : lnk.owlEl.getEquivalentClasses( owlHlp.ontology ) )
    {
     AgeRestriction rest = convertToRestriction( lnk.ageEl, clexpr, owlHlp.relationSourceMap, owlHlp.classSourceMap, 0 );
     
     if( rest != null )
      lnk.ageEl.addObjectRestriction(rest);
     
     rest = convertToRestriction( lnk.ageEl, clexpr, attrPropMap, owlHlp.attributeSourceMap, 0);

     if( rest != null )
      lnk.ageEl.addAttributeRestriction( rest );
    }

    for( OWLClassExpression clexpr : lnk.owlEl.getSuperClasses( owlHlp.ontology ) )
    {
     AgeRestriction rest = convertToRestriction( lnk.ageEl, clexpr, owlHlp.relationSourceMap, owlHlp.classSourceMap, 0 );
     
     if( rest != null )
      lnk.ageEl.addObjectRestriction(rest);

     rest = convertToRestriction( lnk.ageEl, clexpr, attrPropMap, owlHlp.attributeSourceMap, 0);

     if( rest != null )
      lnk.ageEl.addAttributeRestriction( rest );
    }

   }
   
   
  }
  catch(OWLOntologyCreationException e)
  {
   throw new ModelException(e.getMessage(),e);
  }

  for( Link<AgeClassWritable,OWLClass> l : owlHlp.classSourceMap.values() )
   addClass( l.ageEl );

  for( Link<AgeAttributeClassWritable,OWLClass> l : owlHlp.attributeSourceMap.values() )
   addAttributeClass( l.ageEl );

  for( Link<AgeRelationClassWritable,OWLObjectProperty> l : owlHlp.relationSourceMap.values() )
   addRelationClass( l.ageEl );

  
 }
 
 


 private void createInverseRelations( OWLParserHelper owlHlp )
 {
  for(Link<AgeRelationClassWritable, OWLObjectProperty> l : owlHlp.relationSourceMap.values())
  {
   if(l.ageEl.getInverseClass() != null)
    continue;

   Set<OWLObjectPropertyExpression> invset = l.owlEl.getInverses(owlHlp.ontology);
   
   if( invset == null || invset.size() == 0 )
    continue;

   
   
   OWLObjectProperty invProp = invset.iterator().next().asOWLObjectProperty();

   Link<AgeRelationClassWritable, OWLObjectProperty> invp = owlHlp.relationSourceMap.get(invProp.getIRI().toString());

   if(invp != null)
   {
    l.ageEl.setInverseClass(invp.ageEl);
    invp.ageEl.setInverseClass(l.ageEl);
   }
   else
   {
    AgeRelationClassWritable irc = createAgeRelationClass("!" + l.ageEl.getName(),"!"+l.owlEl.getIRI());
    irc.setImplicit(true);

    l.ageEl.setInverseClass(irc);
    irc.setInverseClass(l.ageEl);
   }
  }
 }



 private <T> T reproduceClassStructure(OWLClass classRoot, String defRootName, Map<String, Link<T,OWLClass>> sourceMap, HierHlp<T> hhlp, OWLParserHelper owlHlp) throws ModelException
 {
  Set<OWLClass> classRootSet = new HashSet<OWLClass>();
  
  if( classRoot != null )
  {
   for( OWLClassExpression supexp :  classRoot.getSubClasses(owlHlp.ontology) )
   {
    if( supexp instanceof OWLClass )
     classRootSet.add((OWLClass)supexp);
   }
  }
  else
  {
   for( OWLClass cls : owlHlp.ontology.getClassesInSignature() )
   {
    Set<OWLClassExpression> supers = cls.getSuperClasses(owlHlp.ontology);
    
    if( cls.equals(owlHlp.thing) )
     continue;
    
    if( supers.size() == 0 )
     classRootSet.add(cls);
    else if( supers.size() == 1 )
    {
     if( supers.iterator().next().equals(owlHlp.thing) )
      classRootSet.add(cls);
    }
     
   }
  }
  
  String rootClassName=null;
  
  if( classRoot != null )
  {
   rootClassName=getLabel(classRoot, owlHlp );
   
   if( rootClassName == null )
    rootClassName=classRoot.getIRI().getFragment();
  }
  
  if( rootClassName == null )
   rootClassName=defRootName;

  
  T ageClassRoot = hhlp.create(rootClassName,null);

//  sourceMap.put(classRoot!=null?classRoot.getIRI().toString():thing.getIRI().toString(), new ClassLink(ageClassRoot,classRoot));
  
  for( OWLClass cls :  classRootSet )
  {
   T sbCls = makeClassesBranchT(cls, sourceMap, hhlp, owlHlp  );
   
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
 
 private <T extends AgeAbstractClass> AgeRestriction convertToRestriction(AgeClassWritable srcClas, OWLClassExpression clexpr, 
   Map<String, Link<AgeRelationClassWritable,OWLObjectProperty>> relMap, Map<String, Link<T,OWLClass>> domainMap, int level)
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
   
   Link<AgeRelationClassWritable,OWLObjectProperty> rlnk = null;

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

 
 private <T> T makeClassesBranchT(OWLClass oobj, Map<String, Link<T,OWLClass>> sourceMap, HierHlp<T> hhlp, OWLParserHelper owlHlp) throws ModelException
 {
  String className = getLabel( oobj, owlHlp );
  
  if( className == null )
   className = oobj.getIRI().getFragment();

  if( className == null )
   className = oobj.getIRI().toString();

  
  T cls = hhlp.create(className, oobj);
  
 
  for( OWLClassExpression opexpr : oobj.getSubClasses( owlHlp.ontology ) )
  {
   if( opexpr instanceof OWLClass )
   {
    Link<T,OWLClass> subLnk = sourceMap.get(((OWLClass)opexpr).getIRI().toString());
    
    T subcl=null;
    
    if( subLnk == null )
     subcl = makeClassesBranchT( (OWLClass) opexpr,sourceMap, hhlp, owlHlp );
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
 
 private String getLabel( OWLEntity ent, OWLParserHelper owlHlp )
 {
  for( OWLAnnotation annt : ent.getAnnotations(owlHlp.ontology) )
  {
   if( annt.getProperty().isLabel() )
    return ((OWLTypedLiteral)annt.getValue()).getLiteral();
  }

  return null;
 }
 
 private AgeRelationClassWritable makeRelationsBranch(OWLObjectProperty opr, OWLParserHelper owlHlp ) throws ModelException
 {
  String relName = getLabel(opr, owlHlp);
  
  if( relName == null )
   relName = opr.getIRI().getFragment();

  if( relName == null )
   relName = opr.getIRI().toString();

  log.debug("Processing object property '"+relName+"'");
  
  boolean suitable=true;
  
  AgeRelationClassWritable ageRelCls = createAgeRelationClass(relName, opr.getIRI().toString() );
  
  boolean hasClassInSet=false;
  
  for( OWLClassExpression clexpr : opr.getDomains(owlHlp.ontology) )
  {
   if( clexpr instanceof OWLClass )
   {
    hasClassInSet = true;
    OWLClass dmainCls = (OWLClass)clexpr;
    
    for(Link<AgeClassWritable, OWLClass> link : owlHlp.classSourceMap.values() )
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

   for(OWLClassExpression clexpr : opr.getRanges(owlHlp.ontology))
   {
    if(clexpr instanceof OWLClass)
    {
     hasClassInSet = true;
     OWLClass rngCls = (OWLClass) clexpr;

     for(Link<AgeClassWritable, OWLClass> link : owlHlp.classSourceMap.values())
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
  
  for(OWLObjectPropertyExpression opexpr : opr.getSubProperties(owlHlp.ontology))
  {
   if(opexpr instanceof OWLObjectProperty)
   {
    AgeRelationClassWritable subCl = makeRelationsBranch((OWLObjectProperty) opexpr, owlHlp );
    
    if( subCl != null )
    {
     ageRelCls.addSubClass(subCl);
     subCl.addSubClass(ageRelCls);
    }
   }
  }
  
  if( !suitable &&( ageRelCls.getSubClasses() == null || ageRelCls.getSubClasses().size() == 0 ) )
   return null;
   
  owlHlp.relationSourceMap.put(opr.getIRI().toString(), new Link<AgeRelationClassWritable,OWLObjectProperty>( ageRelCls, opr) );
  
  return ageRelCls;
 }

// private boolean isTheFirstSubclassOrClassOfTheSecond(OWLClass subClass, OWLClass superCls, OWLParserHelper owlHlp )
// {
//  if( subClass.equals(superCls) )
//   return true;
//
//  if( superCls.equals(owlHlp.thing) )
//   return true;
//  
//  for( OWLClassExpression expr : subClass.getSuperClasses(owlHlp.ontology) )
//  {
//   if( expr instanceof OWLClass )
//    if( isTheFirstSubclassOrClassOfTheSecond((OWLClass)expr, superCls,owlHlp) )
//     return true;
//  }
//  
//  return false;
// }



 private DataType getDatatype(OWLClass oCls, OWLParserHelper owlHlp )
 {
  if( oCls == null )
   return null;
  
  try
  {
   for(OWLAnnotation annot : oCls.getAnnotations(owlHlp.ontology))
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




}
