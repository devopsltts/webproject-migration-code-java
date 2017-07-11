package com.sun.org.apache.xml.internal.security.transforms.params;

import com.sun.org.apache.xml.internal.security.transforms.TransformParam;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XPathContainer
  extends SignatureElementProxy
  implements TransformParam
{
  public XPathContainer(Document paramDocument)
  {
    super(paramDocument);
  }
  
  public void setXPath(String paramString)
  {
    if (this.constructionElement.getChildNodes() != null)
    {
      localObject = this.constructionElement.getChildNodes();
      for (int i = 0; i < ((NodeList)localObject).getLength(); i++) {
        this.constructionElement.removeChild(((NodeList)localObject).item(i));
      }
    }
    Object localObject = this.doc.createTextNode(paramString);
    this.constructionElement.appendChild((Node)localObject);
  }
  
  public String getXPath()
  {
    return getTextFromTextChild();
  }
  
  public String getBaseLocalName()
  {
    return "XPath";
  }
}
