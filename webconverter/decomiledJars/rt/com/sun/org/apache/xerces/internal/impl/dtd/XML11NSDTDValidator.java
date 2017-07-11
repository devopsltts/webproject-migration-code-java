package com.sun.org.apache.xerces.internal.impl.dtd;

import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.XNIException;

public class XML11NSDTDValidator
  extends XML11DTDValidator
{
  private QName fAttributeQName = new QName();
  
  public XML11NSDTDValidator() {}
  
  protected final void startNamespaceScope(QName paramQName, XMLAttributes paramXMLAttributes, Augmentations paramAugmentations)
    throws XNIException
  {
    this.fNamespaceContext.pushContext();
    if (paramQName.prefix == XMLSymbols.PREFIX_XMLNS) {
      this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "ElementXMLNSPrefix", new Object[] { paramQName.rawname }, (short)2);
    }
    int i = paramXMLAttributes.getLength();
    String str3;
    String str4;
    for (int j = 0; j < i; j++)
    {
      String str2 = paramXMLAttributes.getLocalName(j);
      str3 = paramXMLAttributes.getPrefix(j);
      if ((str3 == XMLSymbols.PREFIX_XMLNS) || ((str3 == XMLSymbols.EMPTY_STRING) && (str2 == XMLSymbols.PREFIX_XMLNS)))
      {
        str4 = this.fSymbolTable.addSymbol(paramXMLAttributes.getValue(j));
        if ((str3 == XMLSymbols.PREFIX_XMLNS) && (str2 == XMLSymbols.PREFIX_XMLNS)) {
          this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXMLNS", new Object[] { paramXMLAttributes.getQName(j) }, (short)2);
        }
        if (str4 == NamespaceContext.XMLNS_URI) {
          this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXMLNS", new Object[] { paramXMLAttributes.getQName(j) }, (short)2);
        }
        if (str2 == XMLSymbols.PREFIX_XML)
        {
          if (str4 != NamespaceContext.XML_URI) {
            this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXML", new Object[] { paramXMLAttributes.getQName(j) }, (short)2);
          }
        }
        else if (str4 == NamespaceContext.XML_URI) {
          this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXML", new Object[] { paramXMLAttributes.getQName(j) }, (short)2);
        }
        str3 = str2 != XMLSymbols.PREFIX_XMLNS ? str2 : XMLSymbols.EMPTY_STRING;
        this.fNamespaceContext.declarePrefix(str3, str4.length() != 0 ? str4 : null);
      }
    }
    String str1 = paramQName.prefix != null ? paramQName.prefix : XMLSymbols.EMPTY_STRING;
    paramQName.uri = this.fNamespaceContext.getURI(str1);
    if ((paramQName.prefix == null) && (paramQName.uri != null)) {
      paramQName.prefix = XMLSymbols.EMPTY_STRING;
    }
    if ((paramQName.prefix != null) && (paramQName.uri == null)) {
      this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "ElementPrefixUnbound", new Object[] { paramQName.prefix, paramQName.rawname }, (short)2);
    }
    for (int k = 0; k < i; k++)
    {
      paramXMLAttributes.getName(k, this.fAttributeQName);
      str3 = this.fAttributeQName.prefix != null ? this.fAttributeQName.prefix : XMLSymbols.EMPTY_STRING;
      str4 = this.fAttributeQName.rawname;
      if (str4 == XMLSymbols.PREFIX_XMLNS)
      {
        this.fAttributeQName.uri = this.fNamespaceContext.getURI(XMLSymbols.PREFIX_XMLNS);
        paramXMLAttributes.setName(k, this.fAttributeQName);
      }
      else if (str3 != XMLSymbols.EMPTY_STRING)
      {
        this.fAttributeQName.uri = this.fNamespaceContext.getURI(str3);
        if (this.fAttributeQName.uri == null) {
          this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "AttributePrefixUnbound", new Object[] { paramQName.rawname, str4, str3 }, (short)2);
        }
        paramXMLAttributes.setName(k, this.fAttributeQName);
      }
    }
    k = paramXMLAttributes.getLength();
    for (int m = 0; m < k - 1; m++)
    {
      str4 = paramXMLAttributes.getURI(m);
      if ((str4 != null) && (str4 != NamespaceContext.XMLNS_URI))
      {
        String str5 = paramXMLAttributes.getLocalName(m);
        for (int n = m + 1; n < k; n++)
        {
          String str6 = paramXMLAttributes.getLocalName(n);
          String str7 = paramXMLAttributes.getURI(n);
          if ((str5 == str6) && (str4 == str7)) {
            this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "AttributeNSNotUnique", new Object[] { paramQName.rawname, str5, str4 }, (short)2);
          }
        }
      }
    }
  }
  
  protected void endNamespaceScope(QName paramQName, Augmentations paramAugmentations, boolean paramBoolean)
    throws XNIException
  {
    String str = paramQName.prefix != null ? paramQName.prefix : XMLSymbols.EMPTY_STRING;
    paramQName.uri = this.fNamespaceContext.getURI(str);
    if (paramQName.uri != null) {
      paramQName.prefix = str;
    }
    if ((this.fDocumentHandler != null) && (!paramBoolean)) {
      this.fDocumentHandler.endElement(paramQName, paramAugmentations);
    }
    this.fNamespaceContext.popContext();
  }
}
