package com.sun.xml.internal.txw2;

class StartTag
  extends Content
  implements NamespaceResolver
{
  private String uri;
  private final String localName;
  private Attribute firstAtt;
  private Attribute lastAtt;
  private ContainerElement owner;
  private NamespaceDecl firstNs;
  private NamespaceDecl lastNs;
  final Document document;
  
  public StartTag(ContainerElement paramContainerElement, String paramString1, String paramString2)
  {
    this(paramContainerElement.document, paramString1, paramString2);
    this.owner = paramContainerElement;
  }
  
  public StartTag(Document paramDocument, String paramString1, String paramString2)
  {
    assert (paramString1 != null);
    assert (paramString2 != null);
    this.uri = paramString1;
    this.localName = paramString2;
    this.document = paramDocument;
    addNamespaceDecl(paramString1, null, false);
  }
  
  public void addAttribute(String paramString1, String paramString2, Object paramObject)
  {
    checkWritable();
    for (Attribute localAttribute = this.firstAtt; (localAttribute != null) && (!localAttribute.hasName(paramString1, paramString2)); localAttribute = localAttribute.next) {}
    if (localAttribute == null)
    {
      localAttribute = new Attribute(paramString1, paramString2);
      if (this.lastAtt == null)
      {
        assert (this.firstAtt == null);
        this.firstAtt = (this.lastAtt = localAttribute);
      }
      else
      {
        assert (this.firstAtt != null);
        this.lastAtt.next = localAttribute;
        this.lastAtt = localAttribute;
      }
      if (paramString1.length() > 0) {
        addNamespaceDecl(paramString1, null, true);
      }
    }
    this.document.writeValue(paramObject, this, localAttribute.value);
  }
  
  public NamespaceDecl addNamespaceDecl(String paramString1, String paramString2, boolean paramBoolean)
  {
    checkWritable();
    if (paramString1 == null) {
      throw new IllegalArgumentException();
    }
    if (paramString1.length() == 0)
    {
      if (paramBoolean) {
        throw new IllegalArgumentException("The empty namespace cannot have a non-empty prefix");
      }
      if ((paramString2 != null) && (paramString2.length() > 0)) {
        throw new IllegalArgumentException("The empty namespace can be only bound to the empty prefix");
      }
      paramString2 = "";
    }
    for (NamespaceDecl localNamespaceDecl = this.firstNs; localNamespaceDecl != null; localNamespaceDecl = localNamespaceDecl.next)
    {
      if (paramString1.equals(localNamespaceDecl.uri))
      {
        if (paramString2 == null)
        {
          localNamespaceDecl.requirePrefix |= paramBoolean;
          return localNamespaceDecl;
        }
        if (localNamespaceDecl.prefix == null)
        {
          localNamespaceDecl.prefix = paramString2;
          localNamespaceDecl.requirePrefix |= paramBoolean;
          return localNamespaceDecl;
        }
        if (paramString2.equals(localNamespaceDecl.prefix))
        {
          localNamespaceDecl.requirePrefix |= paramBoolean;
          return localNamespaceDecl;
        }
      }
      if ((paramString2 != null) && (localNamespaceDecl.prefix != null) && (localNamespaceDecl.prefix.equals(paramString2))) {
        throw new IllegalArgumentException("Prefix '" + paramString2 + "' is already bound to '" + localNamespaceDecl.uri + '\'');
      }
    }
    localNamespaceDecl = new NamespaceDecl(this.document.assignNewId(), paramString1, paramString2, paramBoolean);
    if (this.lastNs == null)
    {
      assert (this.firstNs == null);
      this.firstNs = (this.lastNs = localNamespaceDecl);
    }
    else
    {
      assert (this.firstNs != null);
      this.lastNs.next = localNamespaceDecl;
      this.lastNs = localNamespaceDecl;
    }
    return localNamespaceDecl;
  }
  
  private void checkWritable()
  {
    if (isWritten()) {
      throw new IllegalStateException("The start tag of " + this.localName + " has already been written. " + "If you need out of order writing, see the TypedXmlWriter.block method");
    }
  }
  
  boolean isWritten()
  {
    return this.uri == null;
  }
  
  boolean isReadyToCommit()
  {
    if ((this.owner != null) && (this.owner.isBlocked())) {
      return false;
    }
    for (Content localContent = getNext(); localContent != null; localContent = localContent.getNext()) {
      if (localContent.concludesPendingStartTag()) {
        return true;
      }
    }
    return false;
  }
  
  public void written()
  {
    this.firstAtt = (this.lastAtt = null);
    this.uri = null;
    if (this.owner != null)
    {
      assert (this.owner.startTag == this);
      this.owner.startTag = null;
    }
  }
  
  boolean concludesPendingStartTag()
  {
    return true;
  }
  
  void accept(ContentVisitor paramContentVisitor)
  {
    paramContentVisitor.onStartTag(this.uri, this.localName, this.firstAtt, this.firstNs);
  }
  
  public String getPrefix(String paramString)
  {
    NamespaceDecl localNamespaceDecl = addNamespaceDecl(paramString, null, false);
    if (localNamespaceDecl.prefix != null) {
      return localNamespaceDecl.prefix;
    }
    return localNamespaceDecl.dummyPrefix;
  }
}
