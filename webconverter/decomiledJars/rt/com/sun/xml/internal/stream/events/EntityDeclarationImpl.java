package com.sun.xml.internal.stream.events;

import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.events.EntityDeclaration;

public class EntityDeclarationImpl
  extends DummyEvent
  implements EntityDeclaration
{
  private XMLResourceIdentifier fXMLResourceIdentifier;
  private String fEntityName;
  private String fReplacementText;
  private String fNotationName;
  
  public EntityDeclarationImpl()
  {
    init();
  }
  
  public EntityDeclarationImpl(String paramString1, String paramString2)
  {
    this(paramString1, paramString2, null);
  }
  
  public EntityDeclarationImpl(String paramString1, String paramString2, XMLResourceIdentifier paramXMLResourceIdentifier)
  {
    init();
    this.fEntityName = paramString1;
    this.fReplacementText = paramString2;
    this.fXMLResourceIdentifier = paramXMLResourceIdentifier;
  }
  
  public void setEntityName(String paramString)
  {
    this.fEntityName = paramString;
  }
  
  public String getEntityName()
  {
    return this.fEntityName;
  }
  
  public void setEntityReplacementText(String paramString)
  {
    this.fReplacementText = paramString;
  }
  
  public void setXMLResourceIdentifier(XMLResourceIdentifier paramXMLResourceIdentifier)
  {
    this.fXMLResourceIdentifier = paramXMLResourceIdentifier;
  }
  
  public XMLResourceIdentifier getXMLResourceIdentifier()
  {
    return this.fXMLResourceIdentifier;
  }
  
  public String getSystemId()
  {
    if (this.fXMLResourceIdentifier != null) {
      return this.fXMLResourceIdentifier.getLiteralSystemId();
    }
    return null;
  }
  
  public String getPublicId()
  {
    if (this.fXMLResourceIdentifier != null) {
      return this.fXMLResourceIdentifier.getPublicId();
    }
    return null;
  }
  
  public String getBaseURI()
  {
    if (this.fXMLResourceIdentifier != null) {
      return this.fXMLResourceIdentifier.getBaseSystemId();
    }
    return null;
  }
  
  public String getName()
  {
    return this.fEntityName;
  }
  
  public String getNotationName()
  {
    return this.fNotationName;
  }
  
  public void setNotationName(String paramString)
  {
    this.fNotationName = paramString;
  }
  
  public String getReplacementText()
  {
    return this.fReplacementText;
  }
  
  protected void init()
  {
    setEventType(15);
  }
  
  protected void writeAsEncodedUnicodeEx(Writer paramWriter)
    throws IOException
  {
    paramWriter.write("<!ENTITY ");
    paramWriter.write(this.fEntityName);
    if (this.fReplacementText != null)
    {
      paramWriter.write(" \"");
      charEncode(paramWriter, this.fReplacementText);
    }
    else
    {
      String str = getPublicId();
      if (str != null)
      {
        paramWriter.write(" PUBLIC \"");
        paramWriter.write(str);
      }
      else
      {
        paramWriter.write(" SYSTEM \"");
        paramWriter.write(getSystemId());
      }
    }
    paramWriter.write("\"");
    if (this.fNotationName != null)
    {
      paramWriter.write(" NDATA ");
      paramWriter.write(this.fNotationName);
    }
    paramWriter.write(">");
  }
}
