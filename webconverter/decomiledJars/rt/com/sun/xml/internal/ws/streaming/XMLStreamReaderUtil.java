package com.sun.xml.internal.ws.streaming;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class XMLStreamReaderUtil
{
  private XMLStreamReaderUtil() {}
  
  public static void close(XMLStreamReader paramXMLStreamReader)
  {
    try
    {
      paramXMLStreamReader.close();
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw wrapException(localXMLStreamException);
    }
  }
  
  public static void readRest(XMLStreamReader paramXMLStreamReader)
  {
    try
    {
      while (paramXMLStreamReader.getEventType() != 8) {
        paramXMLStreamReader.next();
      }
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw wrapException(localXMLStreamException);
    }
  }
  
  public static int next(XMLStreamReader paramXMLStreamReader)
  {
    try
    {
      for (int i = paramXMLStreamReader.next(); i != 8; i = paramXMLStreamReader.next()) {
        switch (i)
        {
        case 1: 
        case 2: 
        case 3: 
        case 4: 
        case 12: 
          return i;
        }
      }
      return i;
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw wrapException(localXMLStreamException);
    }
  }
  
  public static int nextElementContent(XMLStreamReader paramXMLStreamReader)
  {
    int i = nextContent(paramXMLStreamReader);
    if (i == 4) {
      throw new XMLStreamReaderException("xmlreader.unexpectedCharacterContent", new Object[] { paramXMLStreamReader.getText() });
    }
    return i;
  }
  
  public static void toNextTag(XMLStreamReader paramXMLStreamReader, QName paramQName)
  {
    if ((paramXMLStreamReader.getEventType() != 1) && (paramXMLStreamReader.getEventType() != 2)) {
      nextElementContent(paramXMLStreamReader);
    }
    if ((paramXMLStreamReader.getEventType() == 2) && (paramQName.equals(paramXMLStreamReader.getName()))) {
      nextElementContent(paramXMLStreamReader);
    }
  }
  
  public static String nextWhiteSpaceContent(XMLStreamReader paramXMLStreamReader)
  {
    next(paramXMLStreamReader);
    return currentWhiteSpaceContent(paramXMLStreamReader);
  }
  
  public static String currentWhiteSpaceContent(XMLStreamReader paramXMLStreamReader)
  {
    StringBuilder localStringBuilder = null;
    for (;;)
    {
      switch (paramXMLStreamReader.getEventType())
      {
      case 1: 
      case 2: 
      case 8: 
        return localStringBuilder == null ? null : localStringBuilder.toString();
      case 4: 
        if (paramXMLStreamReader.isWhiteSpace())
        {
          if (localStringBuilder == null) {
            localStringBuilder = new StringBuilder();
          }
          localStringBuilder.append(paramXMLStreamReader.getText());
        }
        else
        {
          throw new XMLStreamReaderException("xmlreader.unexpectedCharacterContent", new Object[] { paramXMLStreamReader.getText() });
        }
        break;
      }
      next(paramXMLStreamReader);
    }
  }
  
  public static int nextContent(XMLStreamReader paramXMLStreamReader)
  {
    for (;;)
    {
      int i = next(paramXMLStreamReader);
      switch (i)
      {
      case 1: 
      case 2: 
      case 8: 
        return i;
      case 4: 
        if (!paramXMLStreamReader.isWhiteSpace()) {
          return 4;
        }
        break;
      }
    }
  }
  
  public static void skipElement(XMLStreamReader paramXMLStreamReader)
  {
    assert (paramXMLStreamReader.getEventType() == 1);
    skipTags(paramXMLStreamReader, true);
    assert (paramXMLStreamReader.getEventType() == 2);
  }
  
  public static void skipSiblings(XMLStreamReader paramXMLStreamReader, QName paramQName)
  {
    skipTags(paramXMLStreamReader, paramXMLStreamReader.getName().equals(paramQName));
    assert (paramXMLStreamReader.getEventType() == 2);
  }
  
  private static void skipTags(XMLStreamReader paramXMLStreamReader, boolean paramBoolean)
  {
    try
    {
      int j = 0;
      int i;
      while ((i = paramXMLStreamReader.next()) != 8) {
        if (i == 1)
        {
          j++;
        }
        else if (i == 2)
        {
          if ((j == 0) && (paramBoolean)) {
            return;
          }
          j--;
        }
      }
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw wrapException(localXMLStreamException);
    }
  }
  
  public static String getElementText(XMLStreamReader paramXMLStreamReader)
  {
    try
    {
      return paramXMLStreamReader.getElementText();
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw wrapException(localXMLStreamException);
    }
  }
  
  public static QName getElementQName(XMLStreamReader paramXMLStreamReader)
  {
    try
    {
      String str1 = paramXMLStreamReader.getElementText().trim();
      String str2 = str1.substring(0, str1.indexOf(':'));
      String str3 = paramXMLStreamReader.getNamespaceContext().getNamespaceURI(str2);
      if (str3 == null) {
        str3 = "";
      }
      String str4 = str1.substring(str1.indexOf(':') + 1, str1.length());
      return new QName(str3, str4);
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw wrapException(localXMLStreamException);
    }
  }
  
  public static Attributes getAttributes(XMLStreamReader paramXMLStreamReader)
  {
    return (paramXMLStreamReader.getEventType() == 1) || (paramXMLStreamReader.getEventType() == 10) ? new AttributesImpl(paramXMLStreamReader) : null;
  }
  
  public static void verifyReaderState(XMLStreamReader paramXMLStreamReader, int paramInt)
  {
    int i = paramXMLStreamReader.getEventType();
    if (i != paramInt) {
      throw new XMLStreamReaderException("xmlreader.unexpectedState", new Object[] { getStateName(paramInt), getStateName(i) });
    }
  }
  
  public static void verifyTag(XMLStreamReader paramXMLStreamReader, String paramString1, String paramString2)
  {
    if ((!paramString2.equals(paramXMLStreamReader.getLocalName())) || (!paramString1.equals(paramXMLStreamReader.getNamespaceURI()))) {
      throw new XMLStreamReaderException("xmlreader.unexpectedState.tag", new Object[] { "{" + paramString1 + "}" + paramString2, "{" + paramXMLStreamReader.getNamespaceURI() + "}" + paramXMLStreamReader.getLocalName() });
    }
  }
  
  public static void verifyTag(XMLStreamReader paramXMLStreamReader, QName paramQName)
  {
    verifyTag(paramXMLStreamReader, paramQName.getNamespaceURI(), paramQName.getLocalPart());
  }
  
  public static String getStateName(XMLStreamReader paramXMLStreamReader)
  {
    return getStateName(paramXMLStreamReader.getEventType());
  }
  
  public static String getStateName(int paramInt)
  {
    switch (paramInt)
    {
    case 10: 
      return "ATTRIBUTE";
    case 12: 
      return "CDATA";
    case 4: 
      return "CHARACTERS";
    case 5: 
      return "COMMENT";
    case 11: 
      return "DTD";
    case 8: 
      return "END_DOCUMENT";
    case 2: 
      return "END_ELEMENT";
    case 15: 
      return "ENTITY_DECLARATION";
    case 9: 
      return "ENTITY_REFERENCE";
    case 13: 
      return "NAMESPACE";
    case 14: 
      return "NOTATION_DECLARATION";
    case 3: 
      return "PROCESSING_INSTRUCTION";
    case 6: 
      return "SPACE";
    case 7: 
      return "START_DOCUMENT";
    case 1: 
      return "START_ELEMENT";
    }
    return "UNKNOWN";
  }
  
  private static XMLStreamReaderException wrapException(XMLStreamException paramXMLStreamException)
  {
    return new XMLStreamReaderException("xmlreader.ioException", new Object[] { paramXMLStreamException });
  }
  
  public static class AttributesImpl
    implements Attributes
  {
    static final String XMLNS_NAMESPACE_URI = "http://www.w3.org/2000/xmlns/";
    AttributeInfo[] atInfos;
    
    public AttributesImpl(XMLStreamReader paramXMLStreamReader)
    {
      if (paramXMLStreamReader == null)
      {
        this.atInfos = new AttributeInfo[0];
      }
      else
      {
        int i = 0;
        int j = paramXMLStreamReader.getNamespaceCount();
        int k = paramXMLStreamReader.getAttributeCount();
        this.atInfos = new AttributeInfo[j + k];
        for (int m = 0; m < j; m++)
        {
          String str = paramXMLStreamReader.getNamespacePrefix(m);
          if (str == null) {
            str = "";
          }
          this.atInfos[(i++)] = new AttributeInfo(new QName("http://www.w3.org/2000/xmlns/", str, "xmlns"), paramXMLStreamReader.getNamespaceURI(m));
        }
        for (m = 0; m < k; m++) {
          this.atInfos[(i++)] = new AttributeInfo(paramXMLStreamReader.getAttributeName(m), paramXMLStreamReader.getAttributeValue(m));
        }
      }
    }
    
    public int getLength()
    {
      return this.atInfos.length;
    }
    
    public String getLocalName(int paramInt)
    {
      if ((paramInt >= 0) && (paramInt < this.atInfos.length)) {
        return this.atInfos[paramInt].getLocalName();
      }
      return null;
    }
    
    public QName getName(int paramInt)
    {
      if ((paramInt >= 0) && (paramInt < this.atInfos.length)) {
        return this.atInfos[paramInt].getName();
      }
      return null;
    }
    
    public String getPrefix(int paramInt)
    {
      if ((paramInt >= 0) && (paramInt < this.atInfos.length)) {
        return this.atInfos[paramInt].getName().getPrefix();
      }
      return null;
    }
    
    public String getURI(int paramInt)
    {
      if ((paramInt >= 0) && (paramInt < this.atInfos.length)) {
        return this.atInfos[paramInt].getName().getNamespaceURI();
      }
      return null;
    }
    
    public String getValue(int paramInt)
    {
      if ((paramInt >= 0) && (paramInt < this.atInfos.length)) {
        return this.atInfos[paramInt].getValue();
      }
      return null;
    }
    
    public String getValue(QName paramQName)
    {
      int i = getIndex(paramQName);
      if (i != -1) {
        return this.atInfos[i].getValue();
      }
      return null;
    }
    
    public String getValue(String paramString)
    {
      int i = getIndex(paramString);
      if (i != -1) {
        return this.atInfos[i].getValue();
      }
      return null;
    }
    
    public String getValue(String paramString1, String paramString2)
    {
      int i = getIndex(paramString1, paramString2);
      if (i != -1) {
        return this.atInfos[i].getValue();
      }
      return null;
    }
    
    public boolean isNamespaceDeclaration(int paramInt)
    {
      if ((paramInt >= 0) && (paramInt < this.atInfos.length)) {
        return this.atInfos[paramInt].isNamespaceDeclaration();
      }
      return false;
    }
    
    public int getIndex(QName paramQName)
    {
      for (int i = 0; i < this.atInfos.length; i++) {
        if (this.atInfos[i].getName().equals(paramQName)) {
          return i;
        }
      }
      return -1;
    }
    
    public int getIndex(String paramString)
    {
      for (int i = 0; i < this.atInfos.length; i++) {
        if (this.atInfos[i].getName().getLocalPart().equals(paramString)) {
          return i;
        }
      }
      return -1;
    }
    
    public int getIndex(String paramString1, String paramString2)
    {
      for (int i = 0; i < this.atInfos.length; i++)
      {
        QName localQName = this.atInfos[i].getName();
        if ((localQName.getNamespaceURI().equals(paramString1)) && (localQName.getLocalPart().equals(paramString2))) {
          return i;
        }
      }
      return -1;
    }
    
    static class AttributeInfo
    {
      private QName name;
      private String value;
      
      public AttributeInfo(QName paramQName, String paramString)
      {
        this.name = paramQName;
        if (paramString == null) {
          this.value = "";
        } else {
          this.value = paramString;
        }
      }
      
      QName getName()
      {
        return this.name;
      }
      
      String getValue()
      {
        return this.value;
      }
      
      String getLocalName()
      {
        if (isNamespaceDeclaration())
        {
          if (this.name.getLocalPart().equals("")) {
            return "xmlns";
          }
          return "xmlns:" + this.name.getLocalPart();
        }
        return this.name.getLocalPart();
      }
      
      boolean isNamespaceDeclaration()
      {
        return this.name.getNamespaceURI() == "http://www.w3.org/2000/xmlns/";
      }
    }
  }
}
