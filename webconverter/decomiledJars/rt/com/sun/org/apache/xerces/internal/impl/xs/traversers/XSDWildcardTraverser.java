package com.sun.org.apache.xerces.internal.impl.xs.traversers;

import com.sun.org.apache.xerces.internal.impl.xs.SchemaGrammar;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaSymbols;
import com.sun.org.apache.xerces.internal.impl.xs.XSAnnotationImpl;
import com.sun.org.apache.xerces.internal.impl.xs.XSDeclarationPool;
import com.sun.org.apache.xerces.internal.impl.xs.XSParticleDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSWildcardDecl;
import com.sun.org.apache.xerces.internal.impl.xs.util.XInt;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.util.DOMUtil;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import org.w3c.dom.Element;

class XSDWildcardTraverser
  extends XSDAbstractTraverser
{
  XSDWildcardTraverser(XSDHandler paramXSDHandler, XSAttributeChecker paramXSAttributeChecker)
  {
    super(paramXSDHandler, paramXSAttributeChecker);
  }
  
  XSParticleDecl traverseAny(Element paramElement, XSDocumentInfo paramXSDocumentInfo, SchemaGrammar paramSchemaGrammar)
  {
    Object[] arrayOfObject = this.fAttrChecker.checkAttributes(paramElement, false, paramXSDocumentInfo);
    XSWildcardDecl localXSWildcardDecl = traverseWildcardDecl(paramElement, arrayOfObject, paramXSDocumentInfo, paramSchemaGrammar);
    XSParticleDecl localXSParticleDecl = null;
    if (localXSWildcardDecl != null)
    {
      int i = ((XInt)arrayOfObject[XSAttributeChecker.ATTIDX_MINOCCURS]).intValue();
      int j = ((XInt)arrayOfObject[XSAttributeChecker.ATTIDX_MAXOCCURS]).intValue();
      if (j != 0)
      {
        if (this.fSchemaHandler.fDeclPool != null) {
          localXSParticleDecl = this.fSchemaHandler.fDeclPool.getParticleDecl();
        } else {
          localXSParticleDecl = new XSParticleDecl();
        }
        localXSParticleDecl.fType = 2;
        localXSParticleDecl.fValue = localXSWildcardDecl;
        localXSParticleDecl.fMinOccurs = i;
        localXSParticleDecl.fMaxOccurs = j;
        localXSParticleDecl.fAnnotations = localXSWildcardDecl.fAnnotations;
      }
    }
    this.fAttrChecker.returnAttrArray(arrayOfObject, paramXSDocumentInfo);
    return localXSParticleDecl;
  }
  
  XSWildcardDecl traverseAnyAttribute(Element paramElement, XSDocumentInfo paramXSDocumentInfo, SchemaGrammar paramSchemaGrammar)
  {
    Object[] arrayOfObject = this.fAttrChecker.checkAttributes(paramElement, false, paramXSDocumentInfo);
    XSWildcardDecl localXSWildcardDecl = traverseWildcardDecl(paramElement, arrayOfObject, paramXSDocumentInfo, paramSchemaGrammar);
    this.fAttrChecker.returnAttrArray(arrayOfObject, paramXSDocumentInfo);
    return localXSWildcardDecl;
  }
  
  XSWildcardDecl traverseWildcardDecl(Element paramElement, Object[] paramArrayOfObject, XSDocumentInfo paramXSDocumentInfo, SchemaGrammar paramSchemaGrammar)
  {
    XSWildcardDecl localXSWildcardDecl = new XSWildcardDecl();
    XInt localXInt1 = (XInt)paramArrayOfObject[XSAttributeChecker.ATTIDX_NAMESPACE];
    localXSWildcardDecl.fType = localXInt1.shortValue();
    localXSWildcardDecl.fNamespaceList = ((String[])paramArrayOfObject[XSAttributeChecker.ATTIDX_NAMESPACE_LIST]);
    XInt localXInt2 = (XInt)paramArrayOfObject[XSAttributeChecker.ATTIDX_PROCESSCONTENTS];
    localXSWildcardDecl.fProcessContents = localXInt2.shortValue();
    Element localElement = DOMUtil.getFirstChildElement(paramElement);
    XSAnnotationImpl localXSAnnotationImpl = null;
    Object localObject;
    if (localElement != null)
    {
      if (DOMUtil.getLocalName(localElement).equals(SchemaSymbols.ELT_ANNOTATION))
      {
        localXSAnnotationImpl = traverseAnnotationDecl(localElement, paramArrayOfObject, false, paramXSDocumentInfo);
        localElement = DOMUtil.getNextSiblingElement(localElement);
      }
      else
      {
        localObject = DOMUtil.getSyntheticAnnotation(paramElement);
        if (localObject != null) {
          localXSAnnotationImpl = traverseSyntheticAnnotation(paramElement, (String)localObject, paramArrayOfObject, false, paramXSDocumentInfo);
        }
      }
      if (localElement != null) {
        reportSchemaError("s4s-elt-must-match.1", new Object[] { "wildcard", "(annotation?)", DOMUtil.getLocalName(localElement) }, paramElement);
      }
    }
    else
    {
      localObject = DOMUtil.getSyntheticAnnotation(paramElement);
      if (localObject != null) {
        localXSAnnotationImpl = traverseSyntheticAnnotation(paramElement, (String)localObject, paramArrayOfObject, false, paramXSDocumentInfo);
      }
    }
    if (localXSAnnotationImpl != null)
    {
      localObject = new XSObjectListImpl();
      ((XSObjectListImpl)localObject).addXSObject(localXSAnnotationImpl);
    }
    else
    {
      localObject = XSObjectListImpl.EMPTY_LIST;
    }
    localXSWildcardDecl.fAnnotations = ((XSObjectList)localObject);
    return localXSWildcardDecl;
  }
}
