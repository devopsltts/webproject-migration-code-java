package com.sun.xml.internal.bind.v2.runtime.output;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.bind.marshaller.NamespacePrefixMapper;
import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.bind.v2.runtime.NameList;
import com.sun.xml.internal.bind.v2.runtime.NamespaceContext2;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

public final class NamespaceContextImpl
  implements NamespaceContext2
{
  private final XMLSerializer owner;
  private String[] prefixes = new String[4];
  private String[] nsUris = new String[4];
  private int size;
  private Element current;
  private final Element top;
  private NamespacePrefixMapper prefixMapper = defaultNamespacePrefixMapper;
  public boolean collectionMode;
  private static final NamespacePrefixMapper defaultNamespacePrefixMapper = new NamespacePrefixMapper()
  {
    public String getPreferredPrefix(String paramAnonymousString1, String paramAnonymousString2, boolean paramAnonymousBoolean)
    {
      if (paramAnonymousString1.equals("http://www.w3.org/2001/XMLSchema-instance")) {
        return "xsi";
      }
      if (paramAnonymousString1.equals("http://www.w3.org/2001/XMLSchema")) {
        return "xs";
      }
      if (paramAnonymousString1.equals("http://www.w3.org/2005/05/xmlmime")) {
        return "xmime";
      }
      return paramAnonymousString2;
    }
  };
  
  public NamespaceContextImpl(XMLSerializer paramXMLSerializer)
  {
    this.owner = paramXMLSerializer;
    this.current = (this.top = new Element(this, null, null));
    put("http://www.w3.org/XML/1998/namespace", "xml");
  }
  
  public void setPrefixMapper(NamespacePrefixMapper paramNamespacePrefixMapper)
  {
    if (paramNamespacePrefixMapper == null) {
      paramNamespacePrefixMapper = defaultNamespacePrefixMapper;
    }
    this.prefixMapper = paramNamespacePrefixMapper;
  }
  
  public NamespacePrefixMapper getPrefixMapper()
  {
    return this.prefixMapper;
  }
  
  public void reset()
  {
    this.current = this.top;
    this.size = 1;
    this.collectionMode = false;
  }
  
  public int declareNsUri(String paramString1, String paramString2, boolean paramBoolean)
  {
    paramString2 = this.prefixMapper.getPreferredPrefix(paramString1, paramString2, paramBoolean);
    String str;
    if (paramString1.length() == 0)
    {
      for (i = this.size - 1; i >= 0; i--)
      {
        if (this.nsUris[i].length() == 0) {
          return i;
        }
        if (this.prefixes[i].length() == 0)
        {
          assert ((this.current.defaultPrefixIndex == -1) && (this.current.oldDefaultNamespaceUriIndex == -1));
          str = this.nsUris[i];
          String[] arrayOfString = this.owner.nameList.namespaceURIs;
          if (this.current.baseIndex <= i)
          {
            this.nsUris[i] = "";
            j = put(str, null);
            for (int k = arrayOfString.length - 1; k >= 0; k--) {
              if (arrayOfString[k].equals(str))
              {
                this.owner.knownUri2prefixIndexMap[k] = j;
                break;
              }
            }
            if (this.current.elementLocalName != null) {
              this.current.setTagName(j, this.current.elementLocalName, this.current.getOuterPeer());
            }
            return i;
          }
          for (int j = arrayOfString.length - 1; j >= 0; j--) {
            if (arrayOfString[j].equals(str))
            {
              this.current.defaultPrefixIndex = i;
              this.current.oldDefaultNamespaceUriIndex = j;
              this.owner.knownUri2prefixIndexMap[j] = this.size;
              break;
            }
          }
          if (this.current.elementLocalName != null) {
            this.current.setTagName(this.size, this.current.elementLocalName, this.current.getOuterPeer());
          }
          put(this.nsUris[i], null);
          return put("", "");
        }
      }
      return put("", "");
    }
    for (int i = this.size - 1; i >= 0; i--)
    {
      str = this.prefixes[i];
      if ((this.nsUris[i].equals(paramString1)) && ((!paramBoolean) || (str.length() > 0))) {
        return i;
      }
      if (str.equals(paramString2)) {
        paramString2 = null;
      }
    }
    if ((paramString2 == null) && (paramBoolean)) {
      paramString2 = makeUniquePrefix();
    }
    return put(paramString1, paramString2);
  }
  
  public int force(@NotNull String paramString1, @NotNull String paramString2)
  {
    for (int i = this.size - 1; i >= 0; i--) {
      if (this.prefixes[i].equals(paramString2))
      {
        if (!this.nsUris[i].equals(paramString1)) {
          break;
        }
        return i;
      }
    }
    return put(paramString1, paramString2);
  }
  
  public int put(@NotNull String paramString1, @Nullable String paramString2)
  {
    if (this.size == this.nsUris.length)
    {
      String[] arrayOfString1 = new String[this.nsUris.length * 2];
      String[] arrayOfString2 = new String[this.prefixes.length * 2];
      System.arraycopy(this.nsUris, 0, arrayOfString1, 0, this.nsUris.length);
      System.arraycopy(this.prefixes, 0, arrayOfString2, 0, this.prefixes.length);
      this.nsUris = arrayOfString1;
      this.prefixes = arrayOfString2;
    }
    if (paramString2 == null) {
      if (this.size == 1) {
        paramString2 = "";
      } else {
        paramString2 = makeUniquePrefix();
      }
    }
    this.nsUris[this.size] = paramString1;
    this.prefixes[this.size] = paramString2;
    return this.size++;
  }
  
  private String makeUniquePrefix()
  {
    for (String str = 5 + "ns" + this.size; getNamespaceURI(str) != null; str = str + '_') {}
    return str;
  }
  
  public Element getCurrent()
  {
    return this.current;
  }
  
  public int getPrefixIndex(String paramString)
  {
    for (int i = this.size - 1; i >= 0; i--) {
      if (this.nsUris[i].equals(paramString)) {
        return i;
      }
    }
    throw new IllegalStateException();
  }
  
  public String getPrefix(int paramInt)
  {
    return this.prefixes[paramInt];
  }
  
  public String getNamespaceURI(int paramInt)
  {
    return this.nsUris[paramInt];
  }
  
  public String getNamespaceURI(String paramString)
  {
    for (int i = this.size - 1; i >= 0; i--) {
      if (this.prefixes[i].equals(paramString)) {
        return this.nsUris[i];
      }
    }
    return null;
  }
  
  public String getPrefix(String paramString)
  {
    if (this.collectionMode) {
      return declareNamespace(paramString, null, false);
    }
    for (int i = this.size - 1; i >= 0; i--) {
      if (this.nsUris[i].equals(paramString)) {
        return this.prefixes[i];
      }
    }
    return null;
  }
  
  public Iterator<String> getPrefixes(String paramString)
  {
    String str = getPrefix(paramString);
    if (str == null) {
      return Collections.emptySet().iterator();
    }
    return Collections.singleton(paramString).iterator();
  }
  
  public String declareNamespace(String paramString1, String paramString2, boolean paramBoolean)
  {
    int i = declareNsUri(paramString1, paramString2, paramBoolean);
    return getPrefix(i);
  }
  
  public int count()
  {
    return this.size;
  }
  
  public final class Element
  {
    public final NamespaceContextImpl context;
    private final Element prev;
    private Element next;
    private int oldDefaultNamespaceUriIndex;
    private int defaultPrefixIndex;
    private int baseIndex;
    private final int depth;
    private int elementNamePrefix;
    private String elementLocalName;
    private Name elementName;
    private Object outerPeer;
    private Object innerPeer;
    
    private Element(NamespaceContextImpl paramNamespaceContextImpl, Element paramElement)
    {
      this.context = paramNamespaceContextImpl;
      this.prev = paramElement;
      this.depth = (paramElement == null ? 0 : paramElement.depth + 1);
    }
    
    public boolean isRootElement()
    {
      return this.depth == 1;
    }
    
    public Element push()
    {
      if (this.next == null) {
        this.next = new Element(NamespaceContextImpl.this, this.context, this);
      }
      this.next.onPushed();
      return this.next;
    }
    
    public Element pop()
    {
      if (this.oldDefaultNamespaceUriIndex >= 0) {
        this.context.owner.knownUri2prefixIndexMap[this.oldDefaultNamespaceUriIndex] = this.defaultPrefixIndex;
      }
      this.context.size = this.baseIndex;
      this.context.current = this.prev;
      this.outerPeer = (this.innerPeer = null);
      return this.prev;
    }
    
    private void onPushed()
    {
      this.oldDefaultNamespaceUriIndex = (this.defaultPrefixIndex = -1);
      this.baseIndex = this.context.size;
      this.context.current = this;
    }
    
    public void setTagName(int paramInt, String paramString, Object paramObject)
    {
      assert (paramString != null);
      this.elementNamePrefix = paramInt;
      this.elementLocalName = paramString;
      this.elementName = null;
      this.outerPeer = paramObject;
    }
    
    public void setTagName(Name paramName, Object paramObject)
    {
      assert (paramName != null);
      this.elementName = paramName;
      this.outerPeer = paramObject;
    }
    
    public void startElement(XmlOutput paramXmlOutput, Object paramObject)
      throws IOException, XMLStreamException
    {
      this.innerPeer = paramObject;
      if (this.elementName != null) {
        paramXmlOutput.beginStartTag(this.elementName);
      } else {
        paramXmlOutput.beginStartTag(this.elementNamePrefix, this.elementLocalName);
      }
    }
    
    public void endElement(XmlOutput paramXmlOutput)
      throws IOException, SAXException, XMLStreamException
    {
      if (this.elementName != null)
      {
        paramXmlOutput.endTag(this.elementName);
        this.elementName = null;
      }
      else
      {
        paramXmlOutput.endTag(this.elementNamePrefix, this.elementLocalName);
      }
    }
    
    public final int count()
    {
      return this.context.size - this.baseIndex;
    }
    
    public final String getPrefix(int paramInt)
    {
      return this.context.prefixes[(this.baseIndex + paramInt)];
    }
    
    public final String getNsUri(int paramInt)
    {
      return this.context.nsUris[(this.baseIndex + paramInt)];
    }
    
    public int getBase()
    {
      return this.baseIndex;
    }
    
    public Object getOuterPeer()
    {
      return this.outerPeer;
    }
    
    public Object getInnerPeer()
    {
      return this.innerPeer;
    }
    
    public Element getParent()
    {
      return this.prev;
    }
  }
}
