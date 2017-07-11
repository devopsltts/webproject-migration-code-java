package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.dom.AttrImpl;
import com.sun.org.apache.xerces.internal.dom.CoreDocumentImpl;
import com.sun.org.apache.xerces.internal.dom.ElementImpl;
import com.sun.org.apache.xerces.internal.dom.ElementNSImpl;
import com.sun.org.apache.xerces.internal.dom.PSVIAttrNSImpl;
import com.sun.org.apache.xerces.internal.dom.PSVIDocumentImpl;
import com.sun.org.apache.xerces.internal.dom.PSVIElementNSImpl;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import com.sun.org.apache.xerces.internal.xs.AttributePSVI;
import com.sun.org.apache.xerces.internal.xs.ElementPSVI;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import javax.xml.transform.dom.DOMResult;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

final class DOMResultAugmentor
  implements DOMDocumentHandler
{
  private DOMValidatorHelper fDOMValidatorHelper;
  private Document fDocument;
  private CoreDocumentImpl fDocumentImpl;
  private boolean fStorePSVI;
  private boolean fIgnoreChars;
  private final QName fAttributeQName = new QName();
  
  public DOMResultAugmentor(DOMValidatorHelper paramDOMValidatorHelper)
  {
    this.fDOMValidatorHelper = paramDOMValidatorHelper;
  }
  
  public void setDOMResult(DOMResult paramDOMResult)
  {
    this.fIgnoreChars = false;
    if (paramDOMResult != null)
    {
      Node localNode = paramDOMResult.getNode();
      this.fDocument = (localNode.getNodeType() == 9 ? (Document)localNode : localNode.getOwnerDocument());
      this.fDocumentImpl = ((this.fDocument instanceof CoreDocumentImpl) ? (CoreDocumentImpl)this.fDocument : null);
      this.fStorePSVI = (this.fDocument instanceof PSVIDocumentImpl);
      return;
    }
    this.fDocument = null;
    this.fDocumentImpl = null;
    this.fStorePSVI = false;
  }
  
  public void doctypeDecl(DocumentType paramDocumentType)
    throws XNIException
  {}
  
  public void characters(Text paramText)
    throws XNIException
  {}
  
  public void cdata(CDATASection paramCDATASection)
    throws XNIException
  {}
  
  public void comment(Comment paramComment)
    throws XNIException
  {}
  
  public void processingInstruction(ProcessingInstruction paramProcessingInstruction)
    throws XNIException
  {}
  
  public void setIgnoringCharacters(boolean paramBoolean)
  {
    this.fIgnoreChars = paramBoolean;
  }
  
  public void startDocument(XMLLocator paramXMLLocator, String paramString, NamespaceContext paramNamespaceContext, Augmentations paramAugmentations)
    throws XNIException
  {}
  
  public void xmlDecl(String paramString1, String paramString2, String paramString3, Augmentations paramAugmentations)
    throws XNIException
  {}
  
  public void doctypeDecl(String paramString1, String paramString2, String paramString3, Augmentations paramAugmentations)
    throws XNIException
  {}
  
  public void comment(XMLString paramXMLString, Augmentations paramAugmentations)
    throws XNIException
  {}
  
  public void processingInstruction(String paramString, XMLString paramXMLString, Augmentations paramAugmentations)
    throws XNIException
  {}
  
  public void startElement(QName paramQName, XMLAttributes paramXMLAttributes, Augmentations paramAugmentations)
    throws XNIException
  {
    Element localElement = (Element)this.fDOMValidatorHelper.getCurrentElement();
    NamedNodeMap localNamedNodeMap = localElement.getAttributes();
    int i = localNamedNodeMap.getLength();
    int k;
    Object localObject;
    if (this.fDocumentImpl != null) {
      for (k = 0; k < i; k++)
      {
        AttrImpl localAttrImpl = (AttrImpl)localNamedNodeMap.item(k);
        localObject = (AttributePSVI)paramXMLAttributes.getAugmentations(k).getItem("ATTRIBUTE_PSVI");
        if ((localObject != null) && (processAttributePSVI(localAttrImpl, (AttributePSVI)localObject))) {
          ((ElementImpl)localElement).setIdAttributeNode(localAttrImpl, true);
        }
      }
    }
    int j = paramXMLAttributes.getLength();
    if (j > i) {
      if (this.fDocumentImpl == null) {
        for (k = i; k < j; k++)
        {
          paramXMLAttributes.getName(k, this.fAttributeQName);
          localElement.setAttributeNS(this.fAttributeQName.uri, this.fAttributeQName.rawname, paramXMLAttributes.getValue(k));
        }
      } else {
        for (k = i; k < j; k++)
        {
          paramXMLAttributes.getName(k, this.fAttributeQName);
          localObject = (AttrImpl)this.fDocumentImpl.createAttributeNS(this.fAttributeQName.uri, this.fAttributeQName.rawname, this.fAttributeQName.localpart);
          ((AttrImpl)localObject).setValue(paramXMLAttributes.getValue(k));
          AttributePSVI localAttributePSVI = (AttributePSVI)paramXMLAttributes.getAugmentations(k).getItem("ATTRIBUTE_PSVI");
          if ((localAttributePSVI != null) && (processAttributePSVI((AttrImpl)localObject, localAttributePSVI))) {
            ((ElementImpl)localElement).setIdAttributeNode((Attr)localObject, true);
          }
          ((AttrImpl)localObject).setSpecified(false);
          localElement.setAttributeNode((Attr)localObject);
        }
      }
    }
  }
  
  public void emptyElement(QName paramQName, XMLAttributes paramXMLAttributes, Augmentations paramAugmentations)
    throws XNIException
  {
    startElement(paramQName, paramXMLAttributes, paramAugmentations);
    endElement(paramQName, paramAugmentations);
  }
  
  public void startGeneralEntity(String paramString1, XMLResourceIdentifier paramXMLResourceIdentifier, String paramString2, Augmentations paramAugmentations)
    throws XNIException
  {}
  
  public void textDecl(String paramString1, String paramString2, Augmentations paramAugmentations)
    throws XNIException
  {}
  
  public void endGeneralEntity(String paramString, Augmentations paramAugmentations)
    throws XNIException
  {}
  
  public void characters(XMLString paramXMLString, Augmentations paramAugmentations)
    throws XNIException
  {
    if (!this.fIgnoreChars)
    {
      Element localElement = (Element)this.fDOMValidatorHelper.getCurrentElement();
      localElement.appendChild(this.fDocument.createTextNode(paramXMLString.toString()));
    }
  }
  
  public void ignorableWhitespace(XMLString paramXMLString, Augmentations paramAugmentations)
    throws XNIException
  {
    characters(paramXMLString, paramAugmentations);
  }
  
  public void endElement(QName paramQName, Augmentations paramAugmentations)
    throws XNIException
  {
    Node localNode = this.fDOMValidatorHelper.getCurrentElement();
    if ((paramAugmentations != null) && (this.fDocumentImpl != null))
    {
      ElementPSVI localElementPSVI = (ElementPSVI)paramAugmentations.getItem("ELEMENT_PSVI");
      if (localElementPSVI != null)
      {
        if (this.fStorePSVI) {
          ((PSVIElementNSImpl)localNode).setPSVI(localElementPSVI);
        }
        Object localObject = localElementPSVI.getMemberTypeDefinition();
        if (localObject == null) {
          localObject = localElementPSVI.getTypeDefinition();
        }
        ((ElementNSImpl)localNode).setType((XSTypeDefinition)localObject);
      }
    }
  }
  
  public void startCDATA(Augmentations paramAugmentations)
    throws XNIException
  {}
  
  public void endCDATA(Augmentations paramAugmentations)
    throws XNIException
  {}
  
  public void endDocument(Augmentations paramAugmentations)
    throws XNIException
  {}
  
  public void setDocumentSource(XMLDocumentSource paramXMLDocumentSource) {}
  
  public XMLDocumentSource getDocumentSource()
  {
    return null;
  }
  
  private boolean processAttributePSVI(AttrImpl paramAttrImpl, AttributePSVI paramAttributePSVI)
  {
    if (this.fStorePSVI) {
      ((PSVIAttrNSImpl)paramAttrImpl).setPSVI(paramAttributePSVI);
    }
    Object localObject = paramAttributePSVI.getMemberTypeDefinition();
    if (localObject == null)
    {
      localObject = paramAttributePSVI.getTypeDefinition();
      if (localObject != null)
      {
        paramAttrImpl.setType(localObject);
        return ((XSSimpleType)localObject).isIDType();
      }
    }
    else
    {
      paramAttrImpl.setType(localObject);
      return ((XSSimpleType)localObject).isIDType();
    }
    return false;
  }
}
